package com.forest.mfg.adminbuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.builder.BaseSelectBuilder;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.CommonPattern;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.NumberUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.SMSDef;
import com.forest.share.beandef.SmsTemplateDef;

public class SmsAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (SmsAdminBuilder.class);
	public SmsAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		
		
		String templateCode = _req.getParameter("templatecode");
		String type = _req.getParameter("type");
		
		if("send".equals (_reqAction)){			
			json = new JSONObject(send());
		}else if("generate".equals (_reqAction)){	
			if(type.equals("agentprofile")){
				json = new JSONObject(smsAgentProfile(templateCode));
			}else if(type.equals("customerprofile")){
				json = new JSONObject(smsCustomerProfile(templateCode));
			}else if(type.equals("stmtrpt")){
				json = new JSONObject(smsStatement(templateCode));
			}
		}else{
			json = super.requestJsonHandler();
		}
		
		return json;
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		
		// Read Additional Content From File
		StringBuffer pageBuffer = FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/"+_clientBean.getRequestFilename());
		int idxStart = pageBuffer.indexOf("%data.templatecode%");
		pageBuffer.replace(idxStart, idxStart + "%data.templatecode%".length(), getSelectBox().toString());
		buffer.append(pageBuffer);		
		return buffer;
	}
	
	public HashMap send()throws Exception{		
		PreparedStatement pstmt = _dbConnection.prepareStatement("Update "+SMSDef.TABLE+" Set "+SMSDef.resp+"=?, "+SMSDef.status+"=? Where "+SMSDef.smsid+"=?");
		HashMap jsonMap = new HashMap();
		HashMap rowData = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		String[] smsId = _req.getParameterValues ((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY));
		int count = 0;
		String resp = null;
		String status = null;
		String gateway = null;
		ArrayList dataArray = null;
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append("a.").append(ShopBeanDef.shoplongname);
		query.append(",a.").append(ShopBeanDef.sms_gateway);
		
		query.append(" From ").append(ShopBeanDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(ShopBeanDef.companyid).append("=?");
		 
		paramMap.put(ShopBeanDef.companyid, _shopInfoBean.getShopName());
		try{
			rowData = _gs.searchDataMap(query, paramMap);	
			gateway = (String) rowData.get(ShopBeanDef.sms_gateway.name);
			logger.debug("gateway = "+gateway);
			// Retrieve sms data
			paramMap.clear();
			query = new StringBuffer();
			query.append("Select ");
			query.append("a.").append(SMSDef.smsid);			
			query.append(",a.").append(SMSDef.tonum);
			query.append(",a.").append(SMSDef.content);
			query.append(" From ").append(SMSDef.TABLE).append(" a");
			query.append(" Where");
			query.append(" a.").append(SMSDef.companyid).append("=?");
			query.append(" And a.").append(SMSDef.smsid).append(" In (").append(DBUtil.arrayToString(smsId, SMSDef.smsid)).append(")");
			
			paramMap.put(SMSDef.companyid, _shopInfoBean.getShopName());
			
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				rowData = (HashMap) dataArray.get(i);
				logger.debug("rowData = "+rowData);
				resp = sendSMS(gateway, (String) rowData.get(SMSDef.tonum.name), (String) rowData.get(SMSDef.content.name));				
				pstmt.setString(1, resp);
				if(resp.indexOf("ERR:")!=-1){
					status = "F";					
				}else{
					status = "S";									
				}
				pstmt.setString(2, status);
				pstmt.setString(3, (String)rowData.get(SMSDef.smsid.name));				
				pstmt.executeUpdate();
				
				jsonMap.put("resp", resp);
				jsonMap.put("status", status);
			}
		}catch(Exception e){
			throw e;
		}finally{
			DBUtil.free(null, pstmt, null);
		}
		return jsonMap;
	}
	
	private String sendSMS(String gateway, String to, String content){
		String url = new String(gateway);
		StringBuffer resp = new StringBuffer();
		try{			
			url = url.replaceAll("%to%", to).replaceAll("%content%", URLEncoder.encode(content, "utf8"));
			logger.debug("url = "+url);
			URL oracle = new URL(url);
	        URLConnection yc = oracle.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	        	resp.append(inputLine);
	        in.close();
			logger.debug("resp = "+resp);
		}catch(Exception e){
			logger.error(e, e);
			resp = new StringBuffer("ERR: "+e.toString().substring(0, 49));
		}
		return resp.toString();
	}
	
	private StringBuffer getSelectBox(){
		StringBuffer rtnBuffer = null;
		StringBuffer query = new StringBuffer();
		query.append("Select ").append(SmsTemplateDef.sid.name).append(",");
		query.append(SmsTemplateDef.code.name);
		query.append(" From ").append(SmsTemplateDef.TABLE);
		query.append(" Where ").append(SmsTemplateDef.companyid.name).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" Order By ").append(SmsTemplateDef.code.name);
		
		rtnBuffer = BaseSelectBuilder.getSelectFromDB(_dbConnection, _shopInfoBean.getShopName(), "SMS_TEMPLATE", query, "templatecode", "", 
				SmsTemplateDef.sid.name, SmsTemplateDef.code.name, "Select Template");
		
		return rtnBuffer;
	}
	
	private String getTemplateContent(String code){
		String content = null;
		HashMap dataMap = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ").append(SmsTemplateDef.content);
		query.append(" From ").append(SmsTemplateDef.TABLE);
		query.append(" Where ").append(SmsTemplateDef.sid).append("=?");
		query.append(" And ").append(SmsTemplateDef.companyid).append("=?");
		
		paramMap.put(SmsTemplateDef.sid, code);
		paramMap.put(SmsTemplateDef.companyid, _shopInfoBean.getShopName());
		try{
			dataMap = _gs.searchDataMap(query, paramMap);
			content = (String) dataMap.get(SmsTemplateDef.content.name);
		}catch(Exception e){
			logger.error(e, e);
		}
		
		return content;
	}
	
	private HashMap smsAgentProfile(String code)throws Exception{
		ArrayList arrayListSearch = null;	
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		HashMap dataMap = null;
		HashMap insertMap = null;
		HashMap jsonMap = new HashMap();
		String smsContent = null;
		String templateContent = getTemplateContent(code);
		logger.debug("templateContent = "+templateContent);
		logger.info(" code = "+code+ ",  query = "+query);
		
		// Get Data
		query.append("Select ").append(AgentProfileDef.name);
		query.append(",").append(AgentProfileDef.mobileno);
		query.append(" From ").append(AgentProfileDef.TABLE);
		query.append(" Where ").append(AgentProfileDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		query.append(" And ").append(AgentProfileDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
		arrayListSearch = gs.searchDataArray(query);
		
		// Add
		_defClass = SMSDef.class;
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		
		int len = arrayListSearch.size();
		for(int i=0; i<len; i++){
			dataMap = (HashMap) arrayListSearch.get(i);
			logger.debug("dataMap = "+dataMap);
			smsContent = templateContent.replaceAll("%name%", (String)dataMap.get(AgentProfileDef.name.name));
			
			insertMap = new HashMap();
			insertMap.put(SMSDef.companyid.name, _shopInfoBean.getShopName());
			insertMap.put(SMSDef.tonum.name, CommonPattern.getPhoneNumber((String) dataMap.get(AgentProfileDef.mobileno.name)));
			insertMap.put(SMSDef.content.name, smsContent);
			insertMap.put(SMSDef.status.name, "N");
			gs.create(_defClass, addArray, insertMap);
		}
		jsonMap.put("rc", "0000");
		
		return jsonMap;
	}
	
	private HashMap smsCustomerProfile(String code)throws Exception{
		ArrayList arrayListSearch = null;	
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		HashMap dataMap = null;
		HashMap insertMap = null;
		HashMap jsonMap = new HashMap();
		String smsContent = null;
		String templateContent = getTemplateContent(code);
		logger.debug("templateContent = "+templateContent);
		logger.info(" code = "+code+ ",  query = "+query);
		
		// Get Data
		query.append("Select ").append(CustomerProfileDef.name);
		query.append(",").append(CustomerProfileDef.contactperson);
		query.append(",").append(CustomerProfileDef.mobileno);
		query.append(" From ").append(CustomerProfileDef.TABLE);
		query.append(" Where ").append(CustomerProfileDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		query.append(" And ").append(CustomerProfileDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
		arrayListSearch = gs.searchDataArray(query);
		
		// Add
		_defClass = SMSDef.class;
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		
		int len = arrayListSearch.size();
		for(int i=0; i<len; i++){
			dataMap = (HashMap) arrayListSearch.get(i);
			logger.debug("dataMap = "+dataMap);
			smsContent = templateContent.replaceAll("%name%", (String)dataMap.get(CustomerProfileDef.name.name));
			smsContent = smsContent.replaceAll("%contactperson%", (String)dataMap.get(CustomerProfileDef.contactperson.name));
			
			insertMap = new HashMap();
			insertMap.put(SMSDef.companyid.name, _shopInfoBean.getShopName());
			insertMap.put(SMSDef.tonum.name, CommonPattern.getPhoneNumber((String) dataMap.get(CustomerProfileDef.mobileno.name)));
			insertMap.put(SMSDef.content.name, smsContent);
			insertMap.put(SMSDef.remark.name, (String)dataMap.get(CustomerProfileDef.name.name));
			insertMap.put(SMSDef.status.name, "N");
			gs.create(_defClass, addArray, insertMap);
		}
		jsonMap.put("rc", "0000");
		
		return jsonMap;
	}
	public StringBuffer stmtQuery(){
		Calendar cal = Calendar.getInstance();
		StringBuffer query = new StringBuffer();
		int currentMonth = Integer.parseInt(DateUtil.getCurrentMonth());
		String currentYear = DateUtil.getCurrentYear();
		
		query.append("Select i.name");
		query.append(" ,i.").append(CustomerProfileDef.mobileno);
		query.append(" ,i.").append(CustomerProfileDef.terms);
		query.append(" ,i.").append(CustomerProfileDef.contactperson);
		query.append(", COALESCE(Sum(b.amount-COALESCE(b.payamount,0)-COALESCE(b.creditamount,0)+COALESCE(b.debitamount,0)),0) as totalamt");
		query.append(", COALESCE(Sum(d.amount-COALESCE(d.payamount,0)-COALESCE(d.creditamount,0)+COALESCE(d.debitamount,0)),0) as currentamt");
		query.append(", COALESCE(Sum(j.amount-COALESCE(j.payamount,0)-COALESCE(j.creditamount,0)+COALESCE(j.debitamount,0)),0) as dueamt");
		
		query.append(" From mfg_statement a");
		query.append(" Inner Join customerprofile i on i.customerid=a.customerid");
							
		query.append(" Left Join (");
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount");
		query.append(" From mfg_statement_detail a");
		query.append(" Where a.salesdate <= '").append(DateUtil.getLastDateOfMonth()).append("'");
		query.append(" ) b On b.stmtid = a.stmtid");
		
		// Minus 1 Month
		cal.add(Calendar.MONTH, -1);
		query.append(" Left Join (");
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
		query.append(" From mfg_statement_detail a");
		query.append(" Where MONTH(a.salesdate) = ").append((cal.get(Calendar.MONTH)+1));
		query.append(" And YEAR(a.salesdate) = ").append(cal.get(Calendar.YEAR));
		query.append(" ) d On d.stmtdtlid = b.stmtdtlid");
		
		// Minus another 1 month
		cal.add(Calendar.MONTH, -1);
	    int day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    cal.set(Calendar.DAY_OF_MONTH, day);
	    SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_FORMAT_NOW);
	    
		query.append(" Left Join ("); 
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
		query.append(" From mfg_statement_detail a");
		query.append(" Where a.salesdate <= '").append(sdf.format(cal.getTime())).append("'");
		query.append(" ) j On j.stmtdtlid = b.stmtdtlid");	
		
		
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
//		query.append(" And i.").append(CustomerProfileDef.terms).append(">0");
		
		query.append(" Group By i.").append(CustomerProfileDef.customerid);
		query.append(" Having Sum(b.amount-COALESCE(b.payamount,0)-COALESCE(b.creditamount,0)+COALESCE(b.debitamount,0)) > 0");
		query.append(" Order By i.").append(CustomerProfileDef.name);
		return query;
	}
	
	private HashMap smsStatement(String code)throws Exception{
		ArrayList arrayListSearch = null;			
		HashMap jsonMap = new HashMap();
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		HashMap dataMap = null;
		HashMap insertMap = null;

		String templateContent = null;
		String smsContent = null;		
		query = stmtQuery();
		
		logger.info("query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		templateContent = getTemplateContent(code);
		logger.debug("templateContent = "+templateContent);
		
		// Add
		_defClass = SMSDef.class;
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		try{
			Map termsMap = MFG_SelectBuilder.getHASH_TERM(_dbConnection, _shopInfoBean);
			int len = arrayListSearch.size();
			for(int i=0; i<len; i++){
				dataMap = (HashMap) arrayListSearch.get(i);				
				smsContent = templateContent.replaceAll("%contactperson%", (String)dataMap.get(CustomerProfileDef.contactperson.name));
				
				smsContent = smsContent.replaceAll("%lastmonth%", DateUtil.getMonthInWord(Integer.parseInt(DateUtil.getPreviousMonth())));
				smsContent = smsContent.replaceAll("%name%", (String)dataMap.get("name"));
				smsContent = smsContent.replaceAll("%terms%", (String)termsMap.get(CommonUtil.nullToEmpty((String)dataMap.get("terms"))));
				smsContent = smsContent.replaceAll("%totalamt%", NumberUtil.formatCurrency((String)dataMap.get("totalamt")));
				smsContent = smsContent.replaceAll("%currentamt%", NumberUtil.formatCurrency((String)dataMap.get("currentamt")));
				smsContent = smsContent.replaceAll("%dueamt%", NumberUtil.formatCurrency((String)dataMap.get("dueamt")));
				smsContent = smsContent.replaceAll("%duedate%", "15 "+DateUtil.getMonthInWord(Integer.parseInt(DateUtil.getCurrentMonth()))+ " " + DateUtil.getCurrentYear());
				insertMap = new HashMap();
				insertMap.put(SMSDef.companyid.name, _shopInfoBean.getShopName());
				insertMap.put(SMSDef.tonum.name, CommonPattern.getPhoneNumber((String) dataMap.get(CustomerProfileDef.mobileno.name)));
				insertMap.put(SMSDef.content.name, smsContent);
				insertMap.put(SMSDef.remark.name, (String)dataMap.get("name"));
				insertMap.put(SMSDef.status.name, "N");
				gs.create(_defClass, addArray, insertMap);
				
			}
		}catch (Exception e) {
			logger.error(e, e);
		}
		jsonMap.put("rc", "0000");
		
		return jsonMap;
	}
}


