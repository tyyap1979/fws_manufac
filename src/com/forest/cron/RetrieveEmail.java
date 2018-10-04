package com.forest.cron;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.log4j.Logger;

import com.forest.common.services.BaseService;
import com.forest.common.util.OwnException;

public class RetrieveEmail
{
	private Connection conn = null;
	private Logger logger = Logger.getLogger (RetrieveEmail.class);
	private int timeout = 5000;
	public static void main(String[] args) throws Exception{
		BaseService baseServ = new BaseService();
		
		RetrieveEmail t = new RetrieveEmail();	
		t.conn = baseServ.getDirectConnection ();
	   	t.readMain (args[0]);
//		System.out.println("find = "+t.findEmail("Email: a.browncow@live.com</p><strong>Latest Posts</strong>"));
		System.out.println ("-----------RetrieveEmail End------------");		
	}

	private void readMain(String url)throws Exception{		
		StringBuffer fileContent = null;		
		int end = 0;
		String contentUrl = null;
		String blogShopUrl = null;
		String title = null;
		StringBuffer contentPage = null;
		String email = null;
		
		String findString = null;
		int x = 0;
		
		PreparedStatement pstmt = conn.prepareStatement("Insert Into blogshop (title, email, url) values(?, ?, ?)");
		try{			
			fileContent = readFile (url);
			if(fileContent.length ()>0){							
				for(int i=0;i<fileContent.length();i++){
					i = fileContent.indexOf("<a href=\"", i)+"<a href=\"".length();
					end = fileContent.indexOf("\"",i);			
					contentUrl = fileContent.substring(i, end);
//					System.out.println("contentUrl = "+contentUrl);
					
					i = end+1;
					
					i = fileContent.indexOf("title=\"", i)+"title=\"".length();
					end = fileContent.indexOf("\"",i);			
					title = fileContent.substring(i, end);
//					System.out.println("title = "+title);
					
					contentPage =  readFile(contentUrl);
					email = findEmail(contentPage.toString());
//					System.out.println("email = "+email);
					
					findString = "Visit Shop: <a href=\"";
					x = contentPage.indexOf(findString, x)+findString.length();
					end = contentPage.indexOf("\"",x);			
					
					blogShopUrl = contentPage.substring(x, end);
					
					
					title = trimLength(title,200);
					email = trimLength(email,200);
					blogShopUrl = trimLength(blogShopUrl,200);
					
					System.out.println("-----------------------------------------");
					System.out.println("title: "+title);
					System.out.println("email: "+email);
					System.out.println("blogShopUrl: "+blogShopUrl);
					
					pstmt.setString(1, title);
					pstmt.setString(2, email);
					pstmt.setString(3, blogShopUrl);
					pstmt.execute();					
				}				
			}
		}catch(Exception e){
			e.printStackTrace ();
		}finally{
			free(conn, pstmt, null);
		}
	}
	
	public String trimLength(String value, int length){
		if(value==null){
			return "";
		}else{
			if(value.length()>length){
				return value.substring(0, length);
			}else{
				return value;
			}
		}
	}
	
	 public String findEmail(String emailAddress){  
		String  expression="[\\w\\-]([\\.\\w])+[\\w]+.@.+\\.[a-z]+\\b";  
		CharSequence inputStr = emailAddress;  
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);  
		Matcher matcher = pattern.matcher(inputStr);
		
		String email = null;
		while (matcher.find()){		
			email = matcher.group().trim();			
		}
		
		return email;  
		  
	}  
	
	private StringBuffer readFile(String path) throws OwnException{			
		StringBuffer mBuffer = new StringBuffer();
		    
	    String strLine = null;
		try{		
			path = path.replaceAll (" ", "%20");
			System.out.println ("Reading "+path);
			URL urlObject = new URL(path);
			URLConnection urlConn = urlObject.openConnection();
//			urlConn.setReadTimeout (timeout);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream (), "iso-8859-1"));			
			
//		    //Read File Line By Line
			while ((strLine = in.readLine()) != null){
				strLine = strLine.trim ();	
				mBuffer.append(strLine).append("\n");
		    }		    
		}catch(Exception e){
			e.printStackTrace ();		
			mBuffer = new StringBuffer();
		}

	    return mBuffer;
	}
	
	public void free(Connection p_oConn, PreparedStatement p_oPstmt, ResultSet p_oRs){
		try{
			if(p_oRs!=null){
				p_oRs.close ();				
			}
			
			if(p_oPstmt!=null){
				p_oPstmt.close ();
			}
			
			if(p_oConn!=null && !p_oConn.isClosed ()){
				p_oConn.close ();
			}
			
			p_oRs = null;
			p_oPstmt = null;
			p_oConn=null;
		}catch(Exception e){
			e.printStackTrace ();
		}
	}
	
}
