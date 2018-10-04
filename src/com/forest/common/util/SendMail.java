package com.forest.common.util;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.WebsiteDef;
import com.forest.common.services.GenericServices;
import com.forest.common.services.ThemesServices;
import com.forest.share.beandef.EmailTemplateDef;
import com.forest.share.webbuilder.ShopWebBuilder;

  
public class SendMail{  
//	private static final String SMTP_HOST_NAME = "ip-182-50-141-93.ip.secureserver.net";  
	private Logger logger = Logger.getLogger (SendMail.class);
//    private static SendMail _instance  = null;    
	private ClientBean _clientBean = null;
	private ShopInfoBean _shopInfoBean = null;
	private Connection _dbConnection = null;
	
    public static void main(String args[]) throws Exception {  
//    	SendMail sendMail = new SendMail();
//    	sendMail.sendKitchenDoorDesignEmail("tyyap1979@gmail.com", null, null, "Adam Test "+new Date(), "Test Email", "sales@kitchendoordesign.com", "Adam Yap");
    }
    
    public SendMail(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn){
    	_shopInfoBean = shopInfoBean;
    	_clientBean = clientBean;
    	_dbConnection = conn;
    }
    
    public void sendEmail(HashMap attrReqDataMap, String templateCode)throws Exception{
		// Send Email		
		HashMap dataMap = null;
		HashMap templateMap = null;		
		String fromName = null;
		String fromEmail = null;
		String toEmail = null;
		String subject = null;
		String body = null;
		 
		templateMap = getEmailTemplate(templateCode);
		if(templateMap!=null){
			body = findReplaceVariable(attrReqDataMap, (String) templateMap.get(EmailTemplateDef.content.name));
			subject = findReplaceVariable(attrReqDataMap, (String) templateMap.get(EmailTemplateDef.subject.name));		
			
			dataMap = new ShopWebBuilder(_shopInfoBean, _clientBean, _dbConnection, null, null,null).getShopInfo();
			fromName = (String) dataMap.get(ShopBeanDef.contact_person.name);
			fromEmail = (String) dataMap.get(ShopBeanDef.contact_email.name);
							
			toEmail = (String) attrReqDataMap.get(ClientUserBeanDef.email.name);		
			sendEmail(toEmail, fromEmail, null, subject, body, fromEmail, fromName);
		}
	}
	
	private String findReplaceVariable(HashMap dataMap, String content){
		String key = null;
	    String value = null;			    
	    int startIndex = 0;
	    int endIndex = 0;
	    String START_TAG = "{";
	    String END_TAG = "}";
	    StringBuffer contentBuffer = new StringBuffer(content);
	    logger.debug("dataMap: "+dataMap);
	    logger.debug("content: "+content);
	    while((startIndex = contentBuffer.indexOf (START_TAG, 0)) != -1){	
	    	endIndex = contentBuffer.indexOf (END_TAG, startIndex+1);	    		    	
	    	key = contentBuffer.substring (startIndex + START_TAG.length (), endIndex);
	    	key = key.trim ();
	    	
	    	if(key!=null){
	    		value = (String) dataMap.get(key);
	    		value = (value==null)?"":value;
	    		contentBuffer.replace(startIndex, (startIndex + START_TAG.length () + key.length () + END_TAG.length ()), value);	
	    	}
	    }
	    
	    return contentBuffer.toString();
	}
    
    private HashMap getEmailTemplate(String code)throws Exception{    	
    	StringBuffer query = new StringBuffer();
    	LinkedHashMap paramMap = new LinkedHashMap();
    	GenericServices gs = null;
    	
    	query.append("Select ");
    	query.append(EmailTemplateDef.subject).append(",");
    	query.append(EmailTemplateDef.content);
    	query.append(" From ");
    	query.append(EmailTemplateDef.TABLE);
    	query.append(" Where ");
    	query.append(EmailTemplateDef.companyid).append("=? And ");
    	query.append(EmailTemplateDef.code).append("=?");
    	
    	paramMap.put(EmailTemplateDef.companyid, _shopInfoBean.getShopName());
    	paramMap.put(EmailTemplateDef.code, code);
    	
    	gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
    	
    	HashMap mainContent = gs.searchDataMap(query, paramMap);
    	return mainContent;
    }
    
    public void sendEmail(String recipients, String cc, String bcc, String subject, String body, String from, String fromName) throws Exception {
    	HashMap webInfoMap = ThemesServices.getInstance().getShopWebSite(_shopInfoBean.getShopDomain());
    	logger.debug("webInfoMap = "+webInfoMap);
    	sendEmail(webInfoMap, recipients, cc, bcc, subject, body, from, fromName);
    }

    private void sendEmail(HashMap webInfoMap, String recipients, String cc, String bcc, String subject, String body, String from, String fromName) throws Exception {
    	logger.debug ("Inside initGmail");
    	String SMTP_HOST_NAME = "smtp.gmail.com";
     	String SMTP_PORT = "465";    
     	final String USER_NAME = (String)webInfoMap.get(WebsiteDef.email.name);
     	final String PASSWORD = (String)webInfoMap.get(WebsiteDef.email_passwd.name);
     	String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
     	
     	List toArray = null;
		List ccArray = null;
		List bccArray = null;
		
     	Session session = null;
    	Properties props = new Properties();
//    	props.put("mail.debug", "true");        

    	props.put("mail.smtp.host", SMTP_HOST_NAME);         
	    props.put("mail.smtp.port", SMTP_PORT);  
	    props.put("mail.smtp.auth", "true");    
	    props.put("mail.smtps.quitwait", "false");

	    props.put("mail.smtp.socketFactory.port", SMTP_PORT);  
	    props.put("mail.smtp.socketFactory.class", SSL_FACTORY);  
	    props.put("mail.smtp.socketFactory.fallback", "false");  

	    try{
	    	logger.debug("props = "+props);
	    	
	    	// Get session	    
	    	session = Session.getInstance(props, new javax.mail.Authenticator() {    
	          protected PasswordAuthentication getPasswordAuthentication() {  
	              return new PasswordAuthentication(USER_NAME, PASSWORD);  
	          }  
		    });
		    logger.debug("Got Session...");
//	    	transport = session.getTransport("smtp");
//	    	transport.connect ();
	    	
	    	
	    	
	    	if(recipients!=null) {
				toArray = Arrays.asList (recipients.split (","));
			}
			if(cc!=null) {
				ccArray = Arrays.asList (cc.split (","));
			}
			
			if(bcc!=null) {
				bccArray = Arrays.asList (bcc.split (","));
			}
			
    		logger.debug ("from = "+from);
    		
	        MimeMessage msg = new MimeMessage(session);         
	        InternetAddress addressFrom = new InternetAddress(from, fromName);  
	        msg.setFrom(addressFrom);  
	        
	        Address replyToList[] = { new InternetAddress(from, fromName) };
	        msg.setReplyTo (replyToList);
	  
	        for (int i = 0; i < toArray.size (); i++) {  
	        	logger.debug ("recipients[i] = "+toArray.get (i));
	            msg.addRecipients(Message.RecipientType.TO, (String) toArray.get (i));
	        }  

	        if(cc!=null && ccArray.size ()>0){
		        for (int i = 0; i < ccArray.size (); i++) {  
		        	logger.debug ("cc[i] = "+ccArray.get (i));
		        	msg.addRecipients (Message.RecipientType.CC, (String) ccArray.get (i));
		        }  
	        }
	        
	        if(bcc!=null && bccArray.size ()>0){
		        for (int i = 0; i < bccArray.size (); i++) {  
		        	logger.debug ("bcc[i] = "+bccArray.get (i));
		        	msg.addRecipients (Message.RecipientType.BCC, (String) bccArray.get (i));
		        }  
	        }
	  
	        // Setting the Subject and Content Type  
	        logger.debug("subject = "+subject);
	        logger.debug("body = "+body);
	        msg.setSubject(subject);          
	        msg.setContent(body,"text/html; charset=utf-8");
	 
//	        transport.sendMessage(msg, msg.getAllRecipients ());
	        Transport.send(msg);
	    	logger.debug ("------------------ Email Sent -----------------");
	    }catch(Exception e){
	    	logger.error (e, e);
	    	throw e;
	    }
	    	    

    }
}
