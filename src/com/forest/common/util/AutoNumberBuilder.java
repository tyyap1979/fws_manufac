package com.forest.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.services.BaseService;


public class AutoNumberBuilder{
	private static Logger logger = Logger.getLogger(AutoNumberBuilder.class);
	
	public static void main (String[] args){
		PreparedStatement pstmt = null;
		BaseService baseServ = new BaseService();
		Connection conn = null;
		try{
			logger.info("Monthly Reset Running Number Start");
			conn = baseServ.getDirectConnection ();
			pstmt = conn.prepareStatement("Update gennumber Set curnum=1 Where format like '%{MM}%'");
			pstmt.executeUpdate();			
			logger.info("Update gennumber Set curnum=1 Where format like '%{MM}%'");
		}catch(Exception e){
			logger.debug(e, e);
		}finally{
			DBUtil.free(conn, pstmt, null);
			logger.info("Monthly Reset Running Number End");
		}
    }
	
	public String AUTO_NUM(Connection conn, String shopName, String code, HashMap dataMap)throws Exception{
		String num = null;
		String format = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String totalNumber = "";
		String fieldName = null;
		String[] formatArr = null;
		StringBuffer actualNum = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		boolean chgNumber = false;		
		try{			
			pstmt = conn.prepareStatement("Select format, curnum From gennumber Where companyid='"+shopName+"' And code='"+code+"'");
			rs = pstmt.executeQuery();
			if(rs.next()){
				num = rs.getString("curnum");
				format = rs.getString("format");
//				if(format.indexOf("MM")!=-1 && cal.get(Calendar.DAY_OF_MONTH)==1 && Integer.parseInt(num)!=1){
//					num = "1";
//					chgNumber = true;
//				}
				
				logger.debug("---------- num = "+num+", format = "+format);
				logger.debug("dataMap: "+dataMap);
				if(!CommonUtil.isEmpty(format)){
					formatArr = format.split(",");
					for(int i=0; i<formatArr.length; i++){
						if(formatArr[i].startsWith("{N")){
							totalNumber = formatArr[i].substring(2, formatArr[i].length()-1);
							num = formatNumber(num, Integer.parseInt(totalNumber));
							actualNum.append(num);
						}else if(formatArr[i].startsWith("{R")){
							totalNumber = formatArr[i].substring(2, formatArr[i].length()-1);
							num = CommonUtil.randomString(Integer.parseInt(totalNumber));
							actualNum.append(num);
						}else if(formatArr[i].equals("{YY}")){
							actualNum.append(String.valueOf(cal.get(Calendar.YEAR)).substring(2));						
						}else if(formatArr[i].equals("{YYYY}")){
							actualNum.append(cal.get(Calendar.YEAR));
						}else if(formatArr[i].equals("{MM}")){
							actualNum.append(CommonUtil.fixlength(String.valueOf(cal.get(Calendar.MONTH) + 1), "0", 2, "L"));
						}else if(formatArr[i].startsWith("[")){
							fieldName = formatArr[i].substring(1, formatArr[i].length()-1);
							actualNum.append((String) dataMap.get(fieldName.toLowerCase()));
						}else{
							actualNum.append(formatArr[i]);
						}
						
					}
				}				
			}
			if(chgNumber){
				pstmt = conn.prepareStatement("Update gennumber Set curnum=1 Where companyid='"+shopName+"' And code='"+code+"'");
			}else{
				pstmt = conn.prepareStatement("Update gennumber Set curnum=curnum+1 Where companyid='"+shopName+"' And code='"+code+"'");	
			}
			
			pstmt.executeUpdate();
//			conn.commit();
//			logger.debug("Running Number shopName = "+shopName+", format="+format+", code = "+code+", actualNum = "+actualNum+", num = "+num);
		}catch(Exception e){
//			conn.rollback();
			logger.error(e,e);
			throw e;
		}finally{
			DBUtil.free(null, pstmt, rs);
		}
		return actualNum.toString();
	}
	
	private static String formatNumber(String num, int totalNumber){
		StringBuffer numBuffer = new StringBuffer();
		int dif = totalNumber - num.length();
		for(int i=0;i<dif; i++){
			numBuffer.append("0");
		}
		numBuffer.append(num);
		return numBuffer.toString();
	}
}
