package com.forest.cron;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.forest.common.util.DBUtil;


public class IpCountryPatch {
	public Connection _dbConnection = null;
	public static void main(String[] args) {
		IpCountryPatch a = new IpCountryPatch();
		try{
			a.process();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void process() throws Exception{		
		FileInputStream fstream = null;
	    DataInputStream in = null;
	    BufferedReader br = null;
	    String strLine = null;
	    String[] ipArray = null;
	    PreparedStatement pstmt = null;
	    StringBuffer query = new StringBuffer();
		try{
			_dbConnection = new DBUtil().getDirectConnection();
			query.append("Insert Into ip_country");
			query.append(" (begin_ip_num, end_ip_num, countrycode)");
			query.append(" Value(?, ?, ?)");
			
			_dbConnection.createStatement().execute("Delete From ip_country");
			pstmt = _dbConnection.prepareStatement(query.toString());
			fstream = new FileInputStream("D:/TDDownload/IPCountry/GeoIPCountryWhois.csv");
	    	in = new DataInputStream(fstream);
	    	br = new BufferedReader(new InputStreamReader(in));
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		    	ipArray = strLine.split(",");
		    	pstmt.setString(1, ipArray[2]);
		    	pstmt.setString(2, ipArray[3]);
		    	pstmt.setString(3, ipArray[4]);
		    	pstmt.execute();
		    }
		    System.out.println("Done Import IP Country.");		   
		}catch(Exception e){
			System.out.println("begin_ip_num: "+ipArray[2]);
			System.out.println("end_ip_num: "+ipArray[3]);
			System.out.println("countrycode: "+ipArray[4]);
			e.printStackTrace();
//			throw new OwnException(e);
		}finally{
			DBUtil.free(_dbConnection, pstmt, null);
		}
	}
	
}
