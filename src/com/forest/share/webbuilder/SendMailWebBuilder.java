package com.forest.share.webbuilder;

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.SendMail;

public class SendMailWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (SendMailWebBuilder.class);
	public SendMailWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		
		if(!CommonUtil.isEmpty(reqAction)){
			json = send(reqAction);
		}
		
		return json;
	}
	
	public JSONObject send(String reqAction)throws Exception{	
		JSONObject json = new JSONObject();
		String fromEmail = _req.getParameter("fromemail");
		String fromName = _req.getParameter("fromname");
		String toEmail = null;
		String bodyMessage = _req.getParameter("body");
		String body = "";
		String subject = _req.getParameter("subject");
		SendMail sendMail = new SendMail(_shopInfoBean, _clientBean, _dbConnection);
		HashMap dataMap = null;
		try{
			dataMap = new ShopWebBuilder(_shopInfoBean, _clientBean, _dbConnection, null, null,null).getShopInfo();
			toEmail = (String) dataMap.get(ShopBeanDef.contact_email.name);
			body = "<br>From: "+fromName+" (" + fromEmail + ")" + "<br><br>Comment: " + bodyMessage;
			
			sendMail.sendEmail(toEmail, null, null, subject, body, fromEmail, fromName);
			
			json.put("rc", "0");
			json.put("rd", "Email Sent");
		}catch(Exception e){			
			logger.error(e, e);
			json.put("rd", "Email Error, Please Contact Administrator");
		}
		return json;
	}
		
	
}
