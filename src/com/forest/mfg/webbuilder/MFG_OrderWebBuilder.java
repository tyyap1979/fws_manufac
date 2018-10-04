package com.forest.mfg.webbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.servlet.WebSiteHandler;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.common.util.SendMail;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.beandef.MFG_CartDetailDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.share.webbuilder.BaseWebBuilder;
import com.forest.share.webbuilder.ShopWebBuilder;

public class MFG_OrderWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (MFG_OrderWebBuilder.class);
	public MFG_OrderWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}	
	
	public Object requestJsonHandler() throws Exception {
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		if (!CommonUtil.isEmpty(reqAction)) {
			if ("sendinv".equals(reqAction)) {
				WebSiteHandler wsHandle = new WebSiteHandler();
				StringBuffer parseHtml = wsHandle.getDynamicPage(_shopInfoBean, _clientBean, _dbConnection, _req, _resp);
				sendInvoice(parseHtml);
				json.put("rc", "0000");
			}
		}
		return json;
	}
		
	public void displayHandler(String moduleId)throws Exception{			
		_mc.initModuleContent(_moduleEle, "[custom=loop]");		
		if("order.list".equals(moduleId)){
			putMainData();
		}else if("order.detail".equals(moduleId)){
			putDetailData(_req.getParameter(MFG_TransactionDef.transid.name));
		}
	}
	
	private void sendInvoice(StringBuffer body)throws Exception{
		// Send Email
		SendMail sendMail = new SendMail(_shopInfoBean, _clientBean, _dbConnection);
		HashMap dataMap = null;
				
		String fromName = null;
		String fromEmail = null;
		String toEmail = null;
		String subject = null;
		dataMap = new ShopWebBuilder(_shopInfoBean, _clientBean, _dbConnection, null, null,null).getShopInfo();
		fromName = (String) dataMap.get(ShopBeanDef.contact_person.name);
		fromEmail = (String) dataMap.get(ShopBeanDef.contact_email.name);
//		fromEmail = "tyyap1979@gmail.com";
		LinkedHashMap paramMap = new LinkedHashMap();
		paramMap.put(MFG_TransactionDef.transid, _req.getParameter(MFG_TransactionDef.transid.name));
		paramMap.put(ClientUserBeanDef.cid, _clientBean.getLoginUserId());
		HashMap mainContent = _gs.searchDataMap(getMainQuery(), paramMap);
				
		toEmail = (String) mainContent.get(ClientUserBeanDef.email.name);
		subject = "Invoice For " + (String) mainContent.get(MFG_TransactionDef.transno.name);
		sendMail.sendEmail(toEmail, fromEmail, null, subject, body.toString(), fromEmail, fromName);
	}
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		String clientId = _clientBean.getLoginUserId();
		
		query.append("Select ");
		query.append("a.*");
		query.append(", (Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(")");
		query.append(" + ").append(MFG_TransactionDef.shippingcharge);
		query.append(") As totalsales, ");		
		query.append("c.").append(MFG_CustProductDef.sellunittype);
		
		query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b On");
		query.append(" b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDetailDef.transid);
		
		query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" c On");
		query.append(" c.").append(MFG_CustProductDef.prodid).append("=b.").append(MFG_TransactionDetailDef.prodid);
						
		query.append(" Where");
		query.append(" a.").append(MFG_TransactionDef.clientid).append("=?");
		
		query.append(" Group By");
		query.append(" a.").append(MFG_TransactionDef.transid);
		
		query.append(" Order By");
		query.append(" a.").append(MFG_TransactionDef.transid).append(" desc");		
		
		
		paramMap.put(MFG_TransactionDef.clientid, clientId);
		
		StringBuffer optionsBuffer = new StringBuffer();
		String opt1, opt2, opt3, measurement = null;
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			int count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);					
				optionsBuffer = new StringBuffer();
				opt1 = (String) loopContent.get(MFG_TransactionDetailDef.opt1.name);
				opt2 = (String) loopContent.get(MFG_TransactionDetailDef.opt2.name);
				opt3 = (String) loopContent.get(MFG_TransactionDetailDef.opt3.name);
				measurement = (String) loopContent.get(MFG_TransactionDetailDef.measurement.name);
				if(!CommonUtil.isEmpty(opt1)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt1_groupname")).append(": ").append((String) loopContent.get("opt1_name"));
				}
				if(!CommonUtil.isEmpty(opt2)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt2_groupname")).append(": ").append((String) loopContent.get("opt2_name"));
				}
				if(!CommonUtil.isEmpty(opt3)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt3_groupname")).append(": ").append((String) loopContent.get("opt3_name"));
				}
				if(!CommonUtil.isEmpty(measurement)){
					optionsBuffer.append("<br>").append("Measurement: ").append(measurement);
				}
				loopContent.put(MFG_TransactionDef.status.name, (String)MFG_SelectBuilder.getHASH_TRANS_STATUS(_dbConnection, _shopInfoBean).get(loopContent.get(MFG_TransactionDef.status.name)));
				loopContent.put(MFG_TransactionDef.paymentmethod.name, (String)SelectBuilder.getHASH_PAYMENT_METHOD(_dbConnection, _shopInfoBean).get(loopContent.get(MFG_TransactionDef.paymentmethod.name)));
				loopContent.put(MFG_CustProductDef.sellunittype.name + "_value", (String)MFG_SelectBuilder.getHASH_SELL_UNIT(_dbConnection, _shopInfoBean).get(loopContent.get(MFG_CustProductDef.sellunittype.name)));
				loopContent.put("options", optionsBuffer.toString());
				_mc.setLoopElementContent(loopContent);
			}						
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
	
	private HashMap putDetailData(String transId)throws Exception{
		ArrayList dataArray = null;
		HashMap mainContent = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();		
		
		// Put Detail
		query.append("Select ");
		query.append("a.*");
		query.append(", a.").append(MFG_TransactionDetailDef.price).append("*").append(MFG_TransactionDetailDef.unit).append(" As unitprice");
		query.append(", b.").append(MFG_CustProductDef.name);
		query.append(", b.").append(MFG_CustProductDef.sellunittype);	
		// Options
		query.append(",c.").append(MFG_ProductOptionDetailDef.name).append(" opt1_name");
		query.append(",c.").append(MFG_ProductOptionDetailDef.code).append(" opt1_code");
		query.append(",d.").append(MFG_ProductOptionDetailDef.name).append(" opt2_name");
		query.append(",d.").append(MFG_ProductOptionDetailDef.code).append(" opt2_code");
		query.append(",e.").append(MFG_ProductOptionDetailDef.name).append(" opt3_name");
		query.append(",e.").append(MFG_ProductOptionDetailDef.code).append(" opt3_code");
		query.append(",f.").append(MFG_ProductOptionDetailDef.name).append(" opt4_name");
		query.append(",f.").append(MFG_ProductOptionDetailDef.code).append(" opt4_code");
		
		query.append(",c1.").append(MFG_ProductOptionDef.groupname).append(" opt1_groupname");
		query.append(",d1.").append(MFG_ProductOptionDef.groupname).append(" opt2_groupname");
		query.append(",e1.").append(MFG_ProductOptionDef.groupname).append(" opt3_groupname");	
		query.append(",f1.").append(MFG_ProductOptionDef.groupname).append(" opt4_groupname");	
		
		query.append(" From ").append(MFG_TransactionDetailDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b On");
		query.append(" b.").append(MFG_CustProductDef.prodid).append("=a.").append(MFG_TransactionDetailDef.prodid);
		
		// Options 
		query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" c on c.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_TransactionDetailDef.opt1);			
		query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" c1 on c1.").append(MFG_ProductOptionDef.prodoptid).append(" = c.").append(MFG_ProductOptionDetailDef.prodoptid);
		
		query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" d on d.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_TransactionDetailDef.opt2);			
		query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" d1 on d1.").append(MFG_ProductOptionDef.prodoptid).append(" = d.").append(MFG_ProductOptionDetailDef.prodoptid);
		
		query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" e on e.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_TransactionDetailDef.opt3);			
		query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" e1 on e1.").append(MFG_ProductOptionDef.prodoptid).append(" = e.").append(MFG_ProductOptionDetailDef.prodoptid);
		
		query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" f on f.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_CartDetailDef.opt4);			
		query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" f1 on f1.").append(MFG_ProductOptionDef.prodoptid).append(" = f.").append(MFG_ProductOptionDetailDef.prodoptid);
		
		query.append(" Where");
		query.append(" a.").append(MFG_TransactionDetailDef.transid).append("=?");
		
		query.append(" Order By");
		query.append(" a.").append(MFG_TransactionDetailDef.position);
		
		
		paramMap.put(MFG_TransactionDetailDef.transid, transId);
		
		StringBuffer optionsBuffer = new StringBuffer();
		String opt1, opt2, opt3, opt4, measurement = null;
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			int count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);					
				optionsBuffer = new StringBuffer();
				opt1 = (String) loopContent.get(MFG_TransactionDetailDef.opt1.name);
				opt2 = (String) loopContent.get(MFG_TransactionDetailDef.opt2.name);
				opt3 = (String) loopContent.get(MFG_TransactionDetailDef.opt3.name);
				opt4 = (String) loopContent.get(MFG_TransactionDetailDef.opt4.name);
				
				measurement = (String) loopContent.get(MFG_TransactionDetailDef.measurement.name);
				if(!CommonUtil.isEmpty(opt1)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt1_groupname")).append(": ").append((String) loopContent.get("opt1_name"));
				}
				if(!CommonUtil.isEmpty(opt2)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt2_groupname")).append(": ").append((String) loopContent.get("opt2_name"));
				}
				if(!CommonUtil.isEmpty(opt3)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt3_groupname")).append(": ").append((String) loopContent.get("opt3_name"));
				}
				if(!CommonUtil.isEmpty(opt4)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt4_groupname")).append(": ").append((String) loopContent.get("opt4_name"));
				}
				if(!CommonUtil.isEmpty(measurement)){
					optionsBuffer.append("<br>").append("Measurement: ").append(measurement);
				}
				
				loopContent.put(MFG_CustProductDef.sellunittype.name + "_value", (String)MFG_SelectBuilder.getHASH_SELL_UNIT(_dbConnection, _shopInfoBean).get(loopContent.get(MFG_CustProductDef.sellunittype.name)));
//				loopContent.put(MFG_CustProductDef.sellunittype.name + "_value", loopContent.get(MFG_CustProductDef.sellunittype.name));
//				logger.debug("Adam loopContent = "+loopContent);
				loopContent.put("options", optionsBuffer.toString());
				_mc.setLoopElementContent(loopContent);
			}						
			
			// Put Main
			paramMap = new LinkedHashMap();
			query = new StringBuffer();
			String clientId = _clientBean.getLoginUserId();
			paramMap.put(MFG_TransactionDef.transid, transId);
			paramMap.put(MFG_TransactionDef.clientid, clientId);
			mainContent = _gs.searchDataMap(getMainQuery(), paramMap);
			logger.debug("mainContent = "+mainContent);
			String orderComment = (String)mainContent.get(MFG_TransactionDef.comment.name);
			orderComment = orderComment.replaceAll("\n", "<br>");
			mainContent.put(MFG_TransactionDef.comment.name, orderComment);
			mainContent.put(MFG_TransactionDef.status.name, (String)MFG_SelectBuilder.getHASH_TRANS_STATUS(_dbConnection, _shopInfoBean).get(mainContent.get(MFG_TransactionDef.status.name)));
			mainContent.put(MFG_TransactionDef.paymentmethod.name, (String)SelectBuilder.getHASH_PAYMENT_METHOD(_dbConnection, _shopInfoBean).get(mainContent.get(MFG_TransactionDef.paymentmethod.name)));
			_mc.setElementContent(mainContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
		
		return mainContent;
	}	
	
	private StringBuffer getMainQuery(){
		StringBuffer query = new StringBuffer();
		
		query.append("Select ");
		query.append("a.*");
		query.append(", (Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(")");
		query.append(" + ").append(MFG_TransactionDef.shippingcharge);
		query.append(") As totalsales");		
		query.append(", c.").append(ClientUserBeanDef.clientno);
		query.append(", c.").append(ClientUserBeanDef.email);
		query.append(", c.").append(ClientUserBeanDef.firstname);
		query.append(", c.").append(ClientUserBeanDef.lastname);
		
		query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b On");
		query.append(" b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDetailDef.transid);
		query.append(" Inner Join ").append(ClientUserBeanDef.TABLE).append(" c On");
		query.append(" c.").append(ClientUserBeanDef.cid).append("=a.").append(MFG_TransactionDef.clientid);
		
		query.append(" Where");
		query.append(" a.").append(MFG_TransactionDef.transid).append("=?");
		query.append(" And a.").append(MFG_TransactionDef.clientid).append("=?");
				
		return query;
	}
}
