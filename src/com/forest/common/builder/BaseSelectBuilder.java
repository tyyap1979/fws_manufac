package com.forest.common.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.forest.common.util.CommonUtil;

public class BaseSelectBuilder {
	private static Logger logger = Logger.getLogger(BaseSelectBuilder.class);
	
	/** initial size of the bundle cache */
    private static final int INITIAL_CACHE_SIZE = 25;
    /** capacity of cache consumed before it should grow */
    private static final float CACHE_LOAD_FACTOR = (float)1.0;    
    
    protected static HashMap selectCacheList = new HashMap(INITIAL_CACHE_SIZE, CACHE_LOAD_FACTOR);
    protected static HashMap hashCacheList = new HashMap(INITIAL_CACHE_SIZE, CACHE_LOAD_FACTOR);    
    
	public static StringBuffer buildHashSelect(Map dataMap, StringBuffer buffer, String fieldName, String defaultvalue){
		StringBuffer selectBuffer = new StringBuffer();
		int selectedIndex = 0;
		Iterator it = dataMap.keySet().iterator();
		String key = null;
		String value = null;
		if(buffer==null){
			selectBuffer.append("<select name='' id=''>");//
			
			while(it.hasNext()){
				key = (String) it.next();
				value = (String) dataMap.get(key);
				selectBuffer.append("<option value='").append(key).append("'>").append(value).append("</option>");
			}
									
			selectBuffer.append("</select>");
			buffer = selectBuffer;
		}
		
		selectBuffer = new StringBuffer(buffer.toString());
		if(!CommonUtil.isEmpty(defaultvalue)){			
			selectedIndex = selectBuffer.indexOf("'"+defaultvalue+"'");
			if(selectedIndex!=-1){
				selectBuffer.insert(selectedIndex+("'"+defaultvalue+"'").length(), " selected");
			}
		}
		selectedIndex = selectBuffer.indexOf("name=''");
		selectBuffer.insert(selectedIndex+("name='").length(), fieldName);

		selectedIndex = selectBuffer.indexOf("id=''");
		selectBuffer.insert(selectedIndex+("id='").length(), fieldName);
		
		return selectBuffer;
	}
	
	public static void listCache(String shopName, String code){
		logger.debug("listCache " + hashCacheList.get(shopName+"-"+code));
	}
	
	public static void removeCache(){
		hashCacheList.clear();
		selectCacheList.clear();
	}
	
	public static void removeCache(String shopName, String code){
		hashCacheList.remove(shopName+"-"+code);
		selectCacheList.remove(shopName+"-"+code);
	}
	
	public static StringBuffer getSelectFromDB(Connection conn, String shopName, String code,
			StringBuffer query, String fieldName, String defaultvalue, String keyName, String valueName, 
			String firstOptionValue){
		return getSelectFromDB(conn, shopName, code, query, fieldName, defaultvalue, keyName, valueName, firstOptionValue, null);
		
	}
	public static StringBuffer getSelectFromDB(Connection conn, String shopName, String code,
			StringBuffer query, String fieldName, String defaultvalue, String keyName, String valueName, 
			String firstOptionValue, String groupField){		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		StringBuffer selectBuffer = new StringBuffer();	
		StringBuffer rtnBuffer = new StringBuffer();
		int selectedIndex = 0;
		HashMap dataHash = null;
		HashMap dataDetailHash = null;
		int metaCount = 0;
		
		String groupName = null;
		try{
			logger.debug("getSelectFromDB code = "+shopName+"-"+code);
			selectBuffer = (StringBuffer) selectCacheList.get(shopName+"-"+code);
			dataHash = (HashMap) hashCacheList.get(shopName+"-"+code);
			if(selectBuffer==null){			
				logger.debug("----- getSelectFromDB Create New -----");
				selectBuffer = new StringBuffer();
				dataHash = new LinkedHashMap();
//				logger.debug(code+": query = "+query);			
				pstmt = conn.prepareStatement(query.toString());
				rs = pstmt.executeQuery();
				rsmd = rs.getMetaData();
				metaCount = rsmd.getColumnCount();
				selectBuffer.append("<select id='' name=''>");
				selectBuffer.append("<option value=''>");
				if(!CommonUtil.isEmpty(firstOptionValue)){
					selectBuffer.append("--- ").append(firstOptionValue).append(" ---");
				}
				selectBuffer.append("</option>");
				
				while(rs.next()){
					// Fields Grouping
					if(!CommonUtil.isEmpty(groupField)){
						if(CommonUtil.isEmpty(groupName)){
							// Starting of Group
							groupName = rs.getString(groupField);
							selectBuffer.append("<OPTGROUP LABEL='").append(groupName).append("'>");
						}else {							
							if(!rs.getString(groupField).equals(groupName)){
								selectBuffer.append("</OPTGROUP>");
								groupName = rs.getString(groupField);
								selectBuffer.append("<OPTGROUP LABEL='").append(groupName).append("'>");
							}
						}
						
					}
					
					selectBuffer.append("<option value='").append(rs.getString(keyName)).append("'>");										
						selectBuffer.append(rs.getString(valueName));
					selectBuffer.append("</option>");				
					
//					dataHash.put(rs.getString(keyName), rs.getString(valueName));
					
					dataDetailHash = new HashMap();
					for(int i=0; i<metaCount; i++){
						dataDetailHash.put(rsmd.getColumnLabel(i+1), rs.getString (i+1));		
					}
					dataDetailHash.put(rs.getString(keyName), rs.getString(valueName));
					dataHash.put(rs.getString(keyName), dataDetailHash);
				}
				selectBuffer.append("</select>");
				selectCacheList.put(shopName+"-"+code, selectBuffer);
				hashCacheList.put(shopName+"-"+code, dataHash);
			}
			
			if(!CommonUtil.isEmpty(groupField)){
				selectBuffer.append("</OPTGROUP>");
			}
			
			rtnBuffer = new StringBuffer(selectBuffer.toString());
			selectedIndex = rtnBuffer.indexOf("'"+defaultvalue+"'");
			rtnBuffer.insert(selectedIndex+("'"+defaultvalue+"'").length(), " selected");
			
			selectedIndex = rtnBuffer.indexOf("name=''");
			rtnBuffer.insert(selectedIndex+("name='").length(), fieldName);
			
			selectedIndex = rtnBuffer.indexOf("id=''");
			rtnBuffer.insert(selectedIndex+("id='").length(), fieldName);
			logger.debug("getSelectFromDB rtnBuffer = "+rtnBuffer);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}
}
