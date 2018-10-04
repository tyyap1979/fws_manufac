package com.forest.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;

public class DBUtil {
	private static Logger logger = Logger.getLogger(DBUtil.class);
	public static void free(Connection p_oConn, PreparedStatement p_oPstmt, ResultSet p_oRs){
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
			e.printStackTrace();
		}
	}
	
	public static String arrayToString(String[] pArray, DataObject dataObj)throws Exception {
		StringBuffer mBuffer = new StringBuffer();
		
		if(pArray!=null){				
			for(int i=0; i<pArray.length; i++){				
				if(dataObj.length<pArray[i].length()){
					throw new Exception("Data length is more then allowed. "+dataObj.name+" Table:"+dataObj.length+" Actual:"+pArray[i].length());
				}
				if(!CommonUtil.isEmpty(pArray[i])){
					if(Types.VARCHAR==dataObj.type){
						mBuffer.append ("'").append (pArray[i]).append ("'").append(",");
					}else{
						mBuffer.append (pArray[i]).append(",");
					}
				}
				
			}
			if(mBuffer.length()>0){
				mBuffer.replace(mBuffer.length()-1, mBuffer.length(), "");
			}
		}
		
		return mBuffer.toString ();
	}
	
	private DataSource getDataSource() throws OwnException {
		DataSource dataSource = null;
		try {			
			String m_sJNDI = ResourceUtil.getSettingValue ("database.jndi");//						
			Context envContext = (Context)new InitialContext().lookup("java:/comp/env");
			dataSource = (DataSource)envContext.lookup(m_sJNDI);		
			
		} catch (Exception e) {
			throw new OwnException("9000");
		}
		return dataSource;
	}
	
	public Connection getDbConnection() throws OwnException{
		Connection conn = null;

		try{			
			logger.debug("--------------------getDbConnection-------------");
			conn = getDataSource ().getConnection ();
			conn.setAutoCommit (false);			
		}catch(Exception e){
			throw new OwnException(e);
		}
		return conn;
	}	
	
	public Connection getDirectConnection() throws Exception{
	    Connection connection = null;	   
	    logger.debug("--------------------getDirectConnection-------------");
	    String url = ResourceUtil.getSettingValue ("db.url");
	    String driver = ResourceUtil.getSettingValue ("db.driver");	    
	    String userID = ResourceUtil.getSettingValue ("db.userid");
	    String password = ResourceUtil.getSettingValue ("db.password");

	    Properties properties = new Properties();
	    properties.put ( "user", userID );
	    properties.put ( "password", password );
	    properties.put ( "charset", "utf8" );	    
	    
	    logger.debug ( "Connection url: '" + url + "'" );
	    try {	    	
	    	Class.forName (driver).newInstance();
	    	connection = DriverManager.getConnection ( url, properties );
	    	if(connection == null) {
	    		throw new Exception("Connection Failed.");	      
	    	}
	    }
	    catch ( Exception e ) {
	      throw e;
	    }
	    
	    return connection;
	}
	
}
