package com.forest.cron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.forest.common.services.BaseService;
import com.forest.common.util.OwnException;

public class ProcessMail
{
	private static Logger logger = Logger.getLogger (ProcessMail.class);
//	private Session _session = null;
//    private Transport _transport = null;
	
    private static final String SMTP_HOST_NAME = "127.0.0.1";  
//    private static final String SMTP_PORT = "25";  
//    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";   
    private static final String USER_NAME = "support";
//    private static final String PASSWORD = "smtpuser";
    
    private static final String path = "lock.txt";
    
	public static void main(String[] args) throws Exception{
		ProcessMail p = new ProcessMail();
		p.process ();				
	}	
	
	
	public void process(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BaseService baseServ = new BaseService();
		StringBuffer query = new StringBuffer();
		StringBuffer updateQuery = new StringBuffer();
		StringBuffer ids = new StringBuffer();

		String from = null;
		String fromName = null;
		String to = null;
		String bcc = null;
		String cc = null;
		String subject = null;
		String body = null;
		
		List toArray = null;
		List ccArray = null;
		List bccArray = null;
			
		boolean isInit = false;
		try{
			query.append ("Select id, emailfrom, fromname, emailto, emailcc, emailbcc, subject, content");
			query.append (" From normal_email");
			query.append (" Where status is null");
			
			conn = baseServ.getDirectConnection ();
			pstmt = conn.prepareStatement (query.toString ());
			rs = pstmt.executeQuery ();
			while(rs.next ()){
				if(!isInit) {					
					isInit = true;
				}
				from = rs.getString ("emailfrom");
				fromName = rs.getString ("fromname");
				to = rs.getString ("emailto");
				cc = rs.getString ("emailcc");
				bcc = rs.getString ("emailbcc");
				subject = rs.getString ("subject");
				body = rs.getString ("content");			
				
				if(to!=null) {
					toArray = Arrays.asList (to.split (","));
				}
				if(cc!=null) {
					ccArray = Arrays.asList (cc.split (","));
				}
				
				if(bcc!=null) {
					bccArray = Arrays.asList (bcc.split (","));
				}
				
				try{					
					sendEmail (toArray, ccArray, bccArray, subject, body, from, fromName);
					ids.append (rs.getInt ("id")).append (",");
				}catch(Exception e){
					e.printStackTrace ();
				}				
			}			
			
			if(ids.length ()>0){
				ids.delete (ids.length ()-1, ids.length ());
				
				updateQuery.append ("Update normal_email set status = 'Y' Where id in (");
				updateQuery.append (ids).append (")");
				pstmt = conn.prepareStatement (updateQuery.toString ());			
				pstmt.executeUpdate ();
			}

			logger.debug ("done");
		}catch(Exception e){
			logger.error (e, e);
		}finally{
			baseServ.free (conn, pstmt, rs);
		}
	}
	
	private void sendEmail(List recipients, List cc, List bcc, String subject, String body, String from, String fromName) throws Exception {
    	try{
    		logger.debug ("from = "+from);
    		logger.debug ("recipients.size () = "+recipients.size ());
    		
    		Properties props = System.getProperties();
    		
			// Setup mail server
			props.put("mail.smtp.host", SMTP_HOST_NAME);
			
    		// Get session
			Session session = Session.getDefaultInstance(props, null);
			
	        MimeMessage msg = new MimeMessage(session);         
	        InternetAddress addressFrom = new InternetAddress(USER_NAME, fromName);  
	        msg.setFrom(addressFrom);  
	        
	        Address replyToList[] = { new InternetAddress(from, fromName) };
	        msg.setReplyTo (replyToList);
	  
	        for (int i = 0; i < recipients.size (); i++) {  
	        	logger.debug ("recipients[i] = "+recipients.get (i));
	            msg.addRecipients(Message.RecipientType.TO, (String) recipients.get (i));
	        }  

	        if(cc!=null && cc.size ()>0){
		        for (int i = 0; i < cc.size (); i++) {  
		        	logger.debug ("cc[i] = "+cc.get (i));
		        	msg.addRecipients (Message.RecipientType.CC, (String) cc.get (i));
		        }  
	        }
	        
	        if(bcc!=null && bcc.size ()>0){
		        for (int i = 0; i < bcc.size (); i++) {  
		        	logger.debug ("bcc[i] = "+bcc.get (i));
		        	msg.addRecipients (Message.RecipientType.BCC, (String) bcc.get (i));
		        }  
	        }
	  
	        // Setting the Subject and Content Type  
	        msg.setSubject(reverseConvert(subject), "UTF-8");          
	        msg.setContent(reverseConvert(body),"text/html; charset=utf-8");
	        
	        Transport.send(msg, msg.getAllRecipients ());
	        
	        logger.debug ("--------------- Sent-------------------------");
    	}catch(Exception e){    		
    		logger.error (e, e);
    		throw e;
    	}
    }
	
    private String reverseConvert(String str){    	
    	String temp;	
		int i=0;
		int endposition = 0;
		
		while((i = str.indexOf("&#", i))!=-1){			
			endposition = str.indexOf(";", i+2);
			temp = str.substring(i+2, endposition);			
			str = str.substring(0, i) + (char)Integer.parseInt (temp) + str.substring(endposition+1, str.length ());			
		}
				
		return str;    	
    }
}
