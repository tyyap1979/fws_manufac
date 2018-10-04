package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.builder.ModuleConfig;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ReportUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.cron.AccountingPatch;
import com.forest.mfg.beandef.MFG_ReceivePaymentDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDetailDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_StatementDetailDef;
import com.forest.mfg.beandef.MFG_TransactionDef;


public class MFG_ReceivePaymentAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_ReceivePaymentAdminBuilder.class);
	public MFG_ReceivePaymentAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		
		String allTransNo = "";
		AccountingPatch acc = new AccountingPatch();
		HashMap dataMap = null;
		MFG_StatementAdminBuilder stmtBuilder = new MFG_StatementAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, 
				MFG_StatementDef.class, ModuleConfig.COMPANY_SELECTED, _resources);
		
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){
			json = super.requestJsonHandler();
			String[] rpayid = {(String)_attrReqDataMap[0].get(MFG_ReceivePaymentDef.rpayid.name)}; 
			dataMap = getInfo(rpayid);			
		}else if(GeneralConst.DELETE.equals (_reqAction)){		
			String[] rpayid = _req.getParameterValues(MFG_ReceivePaymentDef.rpayid.name); 
			dataMap = getInfo(rpayid);
			json = super.requestJsonHandler();
		}else{
			json = super.requestJsonHandler();
		}
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction) || GeneralConst.DELETE.equals (_reqAction)){
			stmtBuilder.updatePaymentAmountToStatement((String[])dataMap.get(MFG_StatementDetailDef.transid.name));
			stmtBuilder.updateStatementDetail((String[])dataMap.get(MFG_StatementDetailDef.transid.name));
			acc.accountAgingPatch(_dbConnection, _shopInfoBean.getShopName(), (String[])dataMap.get(MFG_ReceivePaymentDef.customerid.name));
		}
		if(!CommonUtil.isEmpty(allTransNo)){			
			// Update Account Detail			
			
		}
		
		return json;
	}
	
	public HashMap getInfo(String[] rpayid)throws Exception{
		HashMap map = new HashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append("c.").append(MFG_StatementDetailDef.transid).append(",");
		query.append("a.").append(MFG_ReceivePaymentDef.customerid);
		query.append(" From ").append(MFG_ReceivePaymentDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_ReceivePaymentDetailDef.TABLE).append(" b On b.");
		query.append(MFG_ReceivePaymentDetailDef.rpayid).append("=a.").append(MFG_ReceivePaymentDef.rpayid);
		
		query.append(" Inner Join ").append(MFG_StatementDetailDef.TABLE).append(" c On c.");
		query.append(MFG_StatementDetailDef.stmtdtlid).append("=b.").append(MFG_ReceivePaymentDetailDef.stmtdtlid);
		
		query.append(" Where a.").append(MFG_ReceivePaymentDef.rpayid).append(" In (").append(DBUtil.arrayToString(rpayid, MFG_ReceivePaymentDef.rpayid)).append(")");
		
		String customerId = ",";
		HashMap rowMap = null;		
		ArrayList dataList = _gs.searchDataArray(query);
		int size = dataList.size();
		String[] transIDArray = new String[size];
		for(int i=0; i<size; i++){
			rowMap = (HashMap)dataList.get(i);
			logger.debug("rowMap = "+rowMap);
			transIDArray[i] = (String)rowMap.get(MFG_StatementDetailDef.transid.name);
			if(customerId.indexOf(","+(String)rowMap.get(MFG_ReceivePaymentDef.customerid.name)+",")==-1){
				customerId += (String)rowMap.get(MFG_ReceivePaymentDef.customerid.name)+",";
			}
		}
		logger.debug("customerId = "+customerId);
		
		if(!CommonUtil.isEmpty(customerId)){
			customerId = customerId.substring(1, customerId.length()-1);
			map.put(MFG_ReceivePaymentDef.customerid.name, customerId.split(","));
		}
		
		map.put(MFG_StatementDetailDef.transid.name, transIDArray);
		
		return map;
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		// Read Additional Content From File
		buffer.append(FileHandler.readAdminFile (_shopInfoBean, _clientBean));	
		return buffer;
	}
	
	public JasperPrint buildReport()throws Exception{
		JasperPrint jprint = null;
		Map parameters = new HashMap();
		ArrayList arrayListSearch = null;
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		Map countryMap = SelectBuilder.getHASH_COUNTRY(_dbConnection, _shopInfoBean);
		Map termMap = MFG_SelectBuilder.getHASH_TERM(_dbConnection, _shopInfoBean);
		
		String rpayid = _req.getParameter(MFG_ReceivePaymentDef.rpayid.name);
		
		query.append("Select");
		query.append(" a.*,");
		query.append(" b.*,");
		query.append(" c.").append(AgentProfileDef.name).append(" As ").append(CustomerProfileDef.salesby);
		
		query.append(" From ").append(MFG_ReceivePaymentDef.TABLE).append(" a");
		query.append(" Left Join ").append(CustomerProfileDef.TABLE).append(" b");
		query.append(" on b.").append(CustomerProfileDef.customerid).append(" = a.").append(MFG_ReceivePaymentDef.customerid);
		
		query.append(" Left Join ").append(AgentProfileDef.TABLE).append(" c");
		query.append(" on c.").append(AgentProfileDef.id).append(" = b.").append(CustomerProfileDef.salesby);
		
		query.append(" Where a.").append(MFG_ReceivePaymentDef.rpayid).append(" = ").append(rpayid);
		parameters = gs.searchDataMap(query, null);
		
		if(CommonUtil.isEmpty((String) parameters.get(CustomerProfileDef.country.name))){
			parameters.put(CustomerProfileDef.country.name,"");
		}else{
			parameters.put(CustomerProfileDef.country.name, countryMap.get(parameters.get(CustomerProfileDef.country.name)));
		}
		parameters.put(MFG_TransactionDef.terms.name, termMap.get(parameters.get(MFG_TransactionDef.terms.name)));
		
		
		// Detail
		subClass = BeanDefUtil.getSubClass(_defClass);
		addArray = BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		searchArray = BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
		_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, _accessByCompany);	
		arrayListSearch = (ArrayList)_gs.search(subClass[0], null, searchArray, addArray, _attrReqDataMap, null, "*", _reqAction).get(1);
//		query = new StringBuffer();
//		query.append("Select");
//		query.append(" a.*");
//		query.append(" From ").append(MFG_ReceivePaymentDetailDef.TABLE).append(" a");
//	
//		query.append(" Where a.").append(MFG_ReceivePaymentDetailDef.rpayid).append(" = ").append(rpayid);
//		arrayListSearch = gs.searchDataArray(query);
		logger.debug("arrayListSearch = "+arrayListSearch);
		ReportUtil rptUtil = new ReportUtil();
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String subReportDir = ResourceUtil.getSubReportDirectory(_shopInfoBean);
		
		String logoFile = subReportDir + "logo.jpg";
		String reportFilename = reportDir + "OfficialReceipt.jasper"; 
		parameters.put("REPORT_DIR", reportDir);
		parameters.put("SUBREPORT_DIR", subReportDir);		
		parameters.put("LOGO", logoFile);
		
		logger.debug("parameters = "+parameters);		
		
		// Split to 2 Column if rows > 6
		int maxColumnRow = 6;
		int rowCount = arrayListSearch.size();
		HashMap rowMap = new HashMap();
		HashMap rowMap2 = new HashMap();
		
		if(rowCount>maxColumnRow){
			for(int i=0; i<rowCount && rowCount>maxColumnRow; i++){
				rowMap = (HashMap) arrayListSearch.get(i);
				rowMap2 = (HashMap) arrayListSearch.get(maxColumnRow);
				rowMap.put("transno_1", rowMap2.get("transno"));
				rowMap.put("duedate_1", rowMap2.get("duedate"));
				rowMap.put("amount_1", rowMap2.get("amount"));
				rowMap.put("payamount_1", rowMap2.get("payamount"));
				rowMap.put("salesdate_1", rowMap2.get("salesdate"));
				
				arrayListSearch.remove(maxColumnRow);
				rowCount--;
				
//				logger.debug("Row "+i+" : "+(HashMap) arrayListSearch.get(i));
			}
		}
		
		jprint = rptUtil.getReportPrint(reportFilename, arrayListSearch, parameters);
		
		return jprint;
	}
}
