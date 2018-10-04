package com.forest.common.util;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

public class IPUtil {
	private Logger logger = Logger.getLogger (this.getClass ());
	
	public static void main(String[] args) {
		try{
			new IPUtil().getCurrency(new DBUtil().getDirectConnection(), "210.19.7.143");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getCountry(Connection conn, String ip){
		String countryCode = "";
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		
		try{
			query.append("Select countrycode From IP_COUNTRY Where ? BETWEEN begin_ip_num AND end_ip_num");
			pstmt = conn.prepareStatement(query.toString());
			
			if(!CommonUtil.isEmpty(ip)){
				pstmt.setString(1, convertIPToNum(ip));
				rs = pstmt.executeQuery();
				if(rs.next()){
					countryCode = rs.getString("countrycode");
				}
			}
			
		}catch (Exception e) {
			logger.error(e, e);
		}
//		logger.info(countryCode+"["+ip+"]");
		return countryCode.toUpperCase();
	}
	
	public String getCurrency(Connection conn, String ip){
		String currencyCode = "";
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		
		try{
			if(!CommonUtil.isEmpty(ip)){
				query.append("Select b.countrycurrency From IP_COUNTRY a Inner Join COUNTRY b On a.countrycode = b.countrycode Where ? BETWEEN a.begin_ip_num AND a.end_ip_num");
				pstmt = conn.prepareStatement(query.toString());
				pstmt.setString(1, convertIPToNum(ip));
				rs = pstmt.executeQuery();
				if(rs.next()){
					currencyCode = rs.getString("countrycurrency");
				}
			}
			
		}catch (Exception e) {
			logger.error(e, e);
		}
//		logger.info(currencyCode+"["+ip+"]");
		return currencyCode.toUpperCase();
	}
	
	private String convertIPToNum(String ip){
		String num = null;
		String[] ipArray = null;
		BigInteger[] ipDec = new BigInteger[4];
		if(!CommonUtil.isEmpty(ip)){
			ipArray = ip.split("\\.");
			
			ipDec[0] = new BigInteger("16777216").multiply(new BigInteger(ipArray[0]));
			ipDec[1] = new BigInteger("65536").multiply(new BigInteger(ipArray[1]));
			ipDec[2] = new BigInteger("256").multiply(new BigInteger(ipArray[2]));
			ipDec[3] = new BigInteger(ipArray[3]);
			
			num = ipDec[0].add(ipDec[1]).add(ipDec[2]).add(ipDec[3]).toString();
		}
//		logger.debug("convertIPToNum: "+ip+" = "+num);
		return num;
	}
}
