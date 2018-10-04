package com.forest.common.util;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.ModuleDetailDef;
import com.forest.common.constants.ComponentConst;

public class BuilderUtil {
	private static Logger logger = Logger.getLogger(BuilderUtil.class);
	
	
	public static StringBuffer requestSearchValueToDataObject(HttpServletRequest req, ArrayList searchList, String shopName){
		DataObject obj = null;

		HashMap map = new HashMap();
		String value = null;
		HashMap[] arrDataMap = new HashMap[1];	
		String prefix = null;
		String as = null;
		String name = null;
		StringBuffer searchCriteria = new StringBuffer("");
		int length = 0;
		try{
			String search = req.getParameter("sSearch");
			search = (search==null)?req.getParameter("term"):search;
			arrDataMap[0] = new HashMap();
		
			for(int i=0; i<searchList.size(); i++){
				map = (HashMap) searchList.get(i);	
				logger.debug("requestSearchValueToDataObject map = "+map);
				obj = (DataObject) map.get("object");
				prefix = (String) map.get(ModuleDetailDef.prefix.name);
				as = (String) map.get("as");
				
				if(!CommonUtil.isEmpty(as)){
					name = as;
				}else{
					name = obj.name;
				}
				value = req.getParameter(name);
				value = (value==null)?"":value;
				
//				logger.debug("requestValueToDataObject: "+name + "= "+value);				
				if(name.equals("companyid")){
					continue;			
				}
				if(!obj.key){		
					prefix = (CommonUtil.isEmpty(prefix))?"":prefix+".";
					searchCriteria.append(prefix).append(name).append(" Like '%").append(search).append("%' Or ");					
				}
			}			
			length = searchCriteria.length();
			if(length>0){
				searchCriteria.insert(0, "(");
				searchCriteria.replace(length-3, length, "");
				searchCriteria.append(")");	
			}
//			logger.debug("searchCriteria = "+searchCriteria);
//			logger.debug("");
		}catch(Exception e){
			logger.error(e, e);
		}
		return searchCriteria;
	}
	
	public static HashMap[] requestValueToDataObject(HttpServletRequest req, ArrayList searchList, String shopName){
		DataObject obj = null;

		HashMap map = new HashMap();
		String value = null;
		HashMap[] arrDataMap = new HashMap[1];	
		String type = null;
		String as = null;
		String name = null;
		try{
			arrDataMap[0] = new HashMap();
		
			for(int i=0; i<searchList.size(); i++){
				map = (HashMap) searchList.get(i);				
				obj = (DataObject) map.get("object");
				type = (String) map.get("type");
				as = (String) map.get("as");
				
				if(!CommonUtil.isEmpty(as)){
					name = as;
				}else{
					name = obj.name;
				}
				value = req.getParameter(name);
				if(value==null && !name.equals("companyid")) continue;
//				value = (value==null)?"":value;
				
				logger.debug("requestValueToDataObject: "+name + "= "+value);
				if(name.equals("companyid") && shopName!=null && CommonUtil.isEmpty(value)){
					value = shopName;
				}			
				if(!CommonUtil.isEmpty(type)){
					if(Integer.parseInt(type)==ComponentConst.PASSWORD){
						if(!CommonUtil.isEmpty(value)){
							value = CryptUtil.getInstance ().encrypt (value);
						}
					}else if(Integer.parseInt(type)==ComponentConst.AUTO_SUGGEST){
						arrDataMap[0].put(name, req.getParameter(name));	
					}
				}
				
				arrDataMap[0].put(name, value);			
			}			
			logger.debug("requestValueToDataObject arrDataMap[0] = "+arrDataMap[0]);
		}catch(Exception e){
			logger.error(e, e);
		}
		return arrDataMap;
	}
	
	public static HashMap[] requestAutoSuggestValueToDataObject(HttpServletRequest req, ArrayList searchList, String shopName){
		DataObject obj = null;

		HashMap map = new HashMap();
		String value = null;
		HashMap[] arrDataMap = new HashMap[1];	
		try{
			arrDataMap[0] = new HashMap();
		
			for(int i=0; i<searchList.size(); i++){
				map = (HashMap) searchList.get(i);
				logger.debug("map = "+map);
				obj = (DataObject) map.get("object");				
				
				value = req.getParameter("term");
				value = (value==null)?"":value;
				
				logger.debug("requestValueToDataObject: "+obj.name + "= "+value);
				if(obj.name.equals("companyid") && shopName!=null){
					value = shopName;
				}							
				
				arrDataMap[0].put(obj.name, value);			
			}
		}catch(Exception e){
			logger.error(e, e);
		}
		return arrDataMap;
	}
	
	public static HashMap[] requestValuesToDataObject(Class defClass, HttpServletRequest req, 
			ArrayList searchList, String parentKeyName, String parentKeyValue, String shopName) throws Exception{
		return requestValuesToDataObject(defClass, req, searchList, parentKeyName, parentKeyValue, shopName, (String) BeanDefUtil.getField(defClass, BeanDefUtil.TABLE_PREFIX));	
	}
	public static HashMap[] requestValuesToDataObject(Class defClass, HttpServletRequest req, 
			ArrayList searchList, String parentKeyName, String parentKeyValue, String shopName, String TABLE_PREFIX) throws OwnException{
		DataObject obj = null;
		HashMap map = new HashMap();
		String[] value = null;
		if(CommonUtil.isEmpty(parentKeyValue)){
			parentKeyValue = req.getParameter(parentKeyName);
		}
		
		logger.debug("parentkey= "+parentKeyName+":"+parentKeyValue);
				
		String[] detailListStatus = null;		
		
		HashMap[] arrDataMap = null;
		try{							    			
	    	detailListStatus = req.getParameterValues(TABLE_PREFIX+"row-status");
    		for(int x=0; x<searchList.size(); x++){	    			
				map = (HashMap) searchList.get(x);
				obj = (DataObject) map.get("object");
				
				if(obj==null) continue;
				logger.debug("TABLE_PREFIX+obj.name = "+TABLE_PREFIX+obj.name);
				value = req.getParameterValues(TABLE_PREFIX+obj.name);						
				if(value==null){
					continue;
				}else{
					
					
					if(arrDataMap==null){							
						arrDataMap = new HashMap[value.length];	
						for(int i=0; i<value.length; i++){						
							arrDataMap[i] = new HashMap();
						}
					}									
					String printValue = obj.name;
					for(int i=0; i<value.length; i++){	
						if(obj.name.equals("companyid") && shopName!=null){
							value[i] = shopName;
						}	
						
						printValue += value[i]+",";
						// record consider void when not nullable field empty value 
						if(obj.nullable==false && CommonUtil.isEmpty(value[i])){
							if(!"N".equals(detailListStatus[i]) && obj.key){ // If it is key and it's new record (auto increment)								
								arrDataMap[i] = null;
							}
						}
						if(arrDataMap[i]!=null){	
//							logger.debug(obj.name + " value["+i+"]:"+ value[i]);
							arrDataMap[i].put(obj.name, value[i]);							
							if(detailListStatus!=null){															
								arrDataMap[i].put("row-status", detailListStatus[i]);
							}
						}					
					}			
//					logger.debug("printValue = "+printValue);
				}
			}
//    		Add Parent Keys	    				
    		if(arrDataMap!=null){
	    		for(int i=0; i<arrDataMap.length; i++){    		
	    			if(arrDataMap[i]!=null){
		    			arrDataMap[i].put(parentKeyName, parentKeyValue);	
		    			logger.debug("requestValuesToDataObject arrDataMap["+i+"] = "+arrDataMap[i]);
	    			}else{
	    				logger.debug(i + " is null");
	    			}
	    		}
    		}
		}catch(Exception e){			
			logger.error(e, e);
			throw new OwnException(e);
		}
		return arrDataMap;
	}
	
	public static HashMap[] hashValueToDataObject(HashMap hashData, ArrayList searchList, String shopName){
		DataObject obj = null;

		HashMap map = new HashMap();
		String value = null;
		HashMap[] arrDataMap = new HashMap[1];		
		try{
			arrDataMap[0] = new HashMap();
		
			for(int i=0; i<searchList.size(); i++){
				map = (HashMap) searchList.get(i);
				obj = (DataObject) map.get("object");
				
				value = (String) hashData.get(obj.name);
				value = (value==null)?"":value;
				
				if(obj.name.equals("companyid") && shopName!=null){
					value = shopName;
				}				
				arrDataMap[0].put(obj.name, value);			
			}
		}catch(Exception e){
			logger.error(e, e);
		}
		return arrDataMap;			
	}
}
