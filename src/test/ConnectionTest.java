package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;


public class ConnectionTest {
	public static void main(String[] args) {
		try {
			new ConnectionTest().test();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void test()throws Exception {
		Connection conConnection = null;
	    PreparedStatement ps = null;
	    ResultSet rsData = null;
	    
	    try {
	    	conConnection = getDirectConnection();
	    	ps = conConnection.prepareStatement("Select * from currency where currencycode = 'ADF1'");
	    	rsData = ps.executeQuery();
	    	while(rsData.next()) {
	    		System.out.println(rsData.getString("currencydesc"));
	    	}
	    }catch(Exception e) {
	    	System.out.println("Throw Exception FieldMap:"+e.getMessage());
	        e.printStackTrace();
	    }finally {
	    	rsData.close();
	    }
	}
	
	public Connection getDirectConnection() throws Exception{
	    Connection connection = null;	   
	    
	    String url = "jdbc:mysql://www.jhomedesign.com:3306/forestdb1";
	    String driver = "com.mysql.jdbc.Driver";	    
	    String userID = "forestdbuser";
	    String password = "whatislove";

	    Properties properties = new Properties();
	    properties.put ( "user", userID );
	    properties.put ( "password", password );
	    properties.put ( "charset", "utf8" );	    
	    
	    
	    try {	    	
	    	Class.forName (driver).newInstance();
	    	connection = DriverManager.getConnection ( url, properties );
	    	if(connection == null) {
	    		throw new Exception("Connection Failed.");	      
	    	}
	    }
	    catch ( Exception e ) {
	    	e.printStackTrace();
	    	throw e;
	    }
	    
	    return connection;
	}
}
