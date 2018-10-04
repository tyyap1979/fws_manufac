package com.forest.retail.webbuilder;

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
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.servlet.WebSiteHandler;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.common.util.SendMail;
import com.forest.retail.beandef.RETAIL_CartDetailDef;
import com.forest.retail.beandef.RETAIL_MemberDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.retail.beandef.RETAIL_ProductImageDef;
import com.forest.retail.beandef.RETAIL_ProductOptionDef;
import com.forest.retail.beandef.RETAIL_TransactionDef;
import com.forest.retail.beandef.RETAIL_TransactionDetailDef;
import com.forest.share.webbuilder.BaseWebBuilder;
import com.forest.share.webbuilder.ShopWebBuilder;

public class RETAIL_OrderWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (RETAIL_OrderWebBuilder.class);
	public RETAIL_OrderWebBuilder(ShopInfoBean shopInfoBean,
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
				logger.debug("parseHtml: "+parseHtml);
//				sendInvoice(parseHtml);
				json.put("rc", "0000");
			}
		}
		return json;
	}
		
	public void displayHandler(String moduleId)throws Exception{			
		_mc.initModuleContent(_moduleEle);		
		if("order.list".equals(moduleId)){
			_mc.initModuleContent(_moduleEle, "[custom=loop]");	
			putMainData();
		}else if("order.detail".equals(moduleId)){
			_mc.initModuleContent(_moduleEle, "[custom=loop]");	
			putDetailData(_req.getParameter(RETAIL_TransactionDef.transid.name));
		}
	}
	
	private void sendInvoice(StringBuffer body)throws Exception{
		// Send Email
		SendMail sendMail = new SendMail(_shopInfoBean, _clientBean, _dbConnection);
				
//		fromEmail = "tyyap1979@gmail.com";
		LinkedHashMap paramMap = new LinkedHashMap();
		paramMap.put(RETAIL_TransactionDef.transid, _req.getParameter(RETAIL_TransactionDef.transid.name));
		paramMap.put(RETAIL_MemberDef.mid, _clientBean.getLoginUserId());
		HashMap mainContent = _gs.searchDataMap(getMainQuery(), paramMap);
		
		mainContent.put("content", body.toString());
//		toEmail = (String) mainContent.get(RETAIL_MemberDef.email.name);
//		subject = "Invoice For " + (String) mainContent.get(RETAIL_TransactionDef.transno.name);
		
		sendMail.sendEmail(mainContent, "INVOICE_EMAIL");
		
//		sendMail.sendEmail(toEmail, fromEmail, null, subject, body.toString(), fromEmail, fromName);
	}
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		String clientId = _clientBean.getLoginUserId();
		
		query.append("Select ");
		query.append("a.*");
		query.append(" From ").append(RETAIL_TransactionDef.TABLE).append(" a");
//		query.append(" Inner Join ").append(RETAIL_TransactionDetailDef.TABLE).append(" b On");
//		query.append(" b.").append(RETAIL_TransactionDetailDef.transid).append("=a.").append(RETAIL_TransactionDetailDef.transid);
//
//		query.append(" Inner Join ").append(RETAIL_ProductDef.TABLE).append(" c On");
//		query.append(" c.").append(RETAIL_ProductDef.prodid).append("=b.").append(RETAIL_TransactionDetailDef.prodid);
//		
//		// Options 
//		query.append(" Inner Join ").append(RETAIL_ProductOptionDef.TABLE).append(" d On");
//		query.append(" d.").append(RETAIL_ProductOptionDef.prodid).append("=b.").append(RETAIL_TransactionDetailDef.prodid);
		
		query.append(" Where");
		query.append(" a.").append(RETAIL_TransactionDef.companyid).append("=?");
		query.append(" And a.").append(RETAIL_TransactionDef.clientid).append("=?");
		
		query.append(" Order By");
		query.append(" a.").append(RETAIL_TransactionDef.transid).append(" desc");				
		
		paramMap.put(RETAIL_TransactionDef.companyid, _shopInfoBean.getShopName());
		paramMap.put(RETAIL_TransactionDef.clientid, clientId);
		
		StringBuffer optionsBuffer = new StringBuffer();
		String opt1, opt2, opt3, opt4 = null;
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			int count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);					
//				optionsBuffer = new StringBuffer();
//				
//				opt1 = (String) loopContent.get(RETAIL_ProductDef.opt1_name.name);
//				opt2 = (String) loopContent.get(RETAIL_ProductDef.opt2_name.name);
//				opt3 = (String) loopContent.get(RETAIL_ProductDef.opt3_name.name);
//				opt4 = (String) loopContent.get(RETAIL_ProductDef.opt4_name.name);
//
//				if(!CommonUtil.isEmpty(opt1)){
//					optionsBuffer.append("<br>").append(opt1).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt1.name));
//				}
//				if(!CommonUtil.isEmpty(opt2)){
//					optionsBuffer.append("<br>").append(opt2).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt2.name));
//				}
//				if(!CommonUtil.isEmpty(opt3)){
//					optionsBuffer.append("<br>").append(opt3).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt3.name));
//				}
//				if(!CommonUtil.isEmpty(opt4)){
//					optionsBuffer.append("<br>").append(opt4).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt4.name));
//				}
				
				loopContent.put(RETAIL_TransactionDef.status.name, (String)SelectBuilder.getHASH_TRANS_STATUS(_dbConnection, _shopInfoBean).get(loopContent.get(RETAIL_TransactionDef.status.name)));
				loopContent.put(RETAIL_TransactionDef.paymentmethod.name, (String)SelectBuilder.getHASH_PAYMENT_METHOD(_dbConnection, _shopInfoBean).get(loopContent.get(RETAIL_TransactionDef.paymentmethod.name)));
				
//				loopContent.put("options", optionsBuffer.toString());
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
		query.append(",b.").append(RETAIL_ProductDef.prodname);
		query.append(",b.").append(RETAIL_ProductDef.opt1_name);
		query.append(",b.").append(RETAIL_ProductDef.opt2_name);
		query.append(",b.").append(RETAIL_ProductDef.opt3_name);
		query.append(",b.").append(RETAIL_ProductDef.opt4_name);
		// Options
		query.append(",c.").append(RETAIL_ProductOptionDef.opt1);
		query.append(",c.").append(RETAIL_ProductOptionDef.opt2);
		query.append(",c.").append(RETAIL_ProductOptionDef.opt3);
		query.append(",c.").append(RETAIL_ProductOptionDef.opt4);
		
		query.append(",d.").append(RETAIL_ProductImageDef.filename);
		
		query.append(" From ").append(RETAIL_TransactionDetailDef.TABLE).append(" a");
		query.append(" Inner Join ").append(RETAIL_ProductDef.TABLE).append(" b On");
		query.append(" b.").append(RETAIL_ProductDef.prodid).append("=a.").append(RETAIL_TransactionDetailDef.prodid);
		
		// Options 
		query.append(" Inner Join ").append(RETAIL_ProductOptionDef.TABLE).append(" c On");
		query.append(" c.").append(RETAIL_ProductOptionDef.prodid).append("=a.").append(RETAIL_TransactionDetailDef.prodid);		
		query.append(" And c.").append(RETAIL_ProductOptionDef.optid).append("=a.").append(RETAIL_TransactionDetailDef.optid);	
		
		query.append(" Left Join ").append(RETAIL_ProductImageDef.TABLE).append(" d on");
		query.append(" d.").append(RETAIL_ProductImageDef.prodid).append(" = a.").append(RETAIL_TransactionDetailDef.prodid);
		query.append(" And d.").append(RETAIL_ProductImageDef.position).append(" = 1");
		
		query.append(" Where");
		query.append(" a.").append(RETAIL_TransactionDetailDef.transid).append("=?");
		
		query.append(" Order By");
		query.append(" b.").append(RETAIL_ProductDef.prodname);		
		
		
		paramMap.put(RETAIL_TransactionDetailDef.transid, transId);
		
		StringBuffer optionsBuffer = new StringBuffer();
		String opt1, opt2, opt3, opt4 = null;
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			int count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);			
				logger.debug("loopContent: "+loopContent);
				optionsBuffer = new StringBuffer();
				
				opt1 = (String) loopContent.get(RETAIL_ProductDef.opt1_name.name);
				opt2 = (String) loopContent.get(RETAIL_ProductDef.opt2_name.name);
				opt3 = (String) loopContent.get(RETAIL_ProductDef.opt3_name.name);
				opt4 = (String) loopContent.get(RETAIL_ProductDef.opt4_name.name);

				if(!CommonUtil.isEmpty(opt1)){
					optionsBuffer.append("<br>").append(opt1).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt1.name));
				}
				if(!CommonUtil.isEmpty(opt2)){
					optionsBuffer.append("<br>").append(opt2).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt2.name));
				}
				if(!CommonUtil.isEmpty(opt3)){
					optionsBuffer.append("<br>").append(opt3).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt3.name));
				}
				if(!CommonUtil.isEmpty(opt4)){
					optionsBuffer.append("<br>").append(opt4).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt4.name));
				}

				loopContent.put("options", optionsBuffer.toString());
				_mc.setLoopElementContent(loopContent);
			}						
			
			// Put Main
			paramMap = new LinkedHashMap();
			query = new StringBuffer();
			String clientId = _clientBean.getLoginUserId();
			paramMap.put(RETAIL_TransactionDef.transid, transId);
			paramMap.put(RETAIL_TransactionDef.clientid, clientId);
			mainContent = _gs.searchDataMap(getMainQuery(), paramMap);
			logger.debug("mainContent = "+mainContent);
			
			String orderComment = (String)mainContent.get(RETAIL_TransactionDef.comment.name);
			if(!CommonUtil.isEmpty(orderComment)){
				orderComment = orderComment.replaceAll("\n", "<br>");
				mainContent.put(RETAIL_TransactionDef.comment.name, orderComment);
			}
			
			mainContent.put(RETAIL_TransactionDef.status.name, (String)SelectBuilder.getHASH_TRANS_STATUS(_dbConnection, _shopInfoBean).get(mainContent.get(RETAIL_TransactionDef.status.name)));
			mainContent.put(RETAIL_TransactionDef.paymentmethod.name, (String)SelectBuilder.getHASH_PAYMENT_METHOD(_dbConnection, _shopInfoBean).get(mainContent.get(RETAIL_TransactionDef.paymentmethod.name)));
			mainContent.put(RETAIL_TransactionDef.country.name, (String)SelectBuilder.getHASH_COUNTRY(_dbConnection, _shopInfoBean).get(mainContent.get(RETAIL_TransactionDef.country.name)));
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
		query.append(", (Sum(b.").append(RETAIL_TransactionDetailDef.sellsubtotal).append(")");
		query.append(" + ").append(RETAIL_TransactionDef.shippingcharge);
		query.append(") As totalsales");		
		query.append(", c.").append(RETAIL_MemberDef.email);
		query.append(", c.").append(RETAIL_MemberDef.firstname);
		query.append(", c.").append(RETAIL_MemberDef.lastname);
		
		query.append(" From ").append(RETAIL_TransactionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(RETAIL_TransactionDetailDef.TABLE).append(" b On");
		query.append(" b.").append(RETAIL_TransactionDetailDef.transid).append("=a.").append(RETAIL_TransactionDetailDef.transid);
		query.append(" Inner Join ").append(RETAIL_MemberDef.TABLE).append(" c On");
		query.append(" c.").append(RETAIL_MemberDef.mid).append("=a.").append(RETAIL_TransactionDef.clientid);
		
		query.append(" Where");
		query.append(" a.").append(RETAIL_TransactionDef.transid).append("=?");
		query.append(" And a.").append(RETAIL_TransactionDef.clientid).append("=?");
				
		return query;
	}
}
