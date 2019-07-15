package com.forest.common.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.ComponentConst;
import com.forest.common.util.CommonUtil;


public class BaseDef {
	public static final String STATUS = "status";
	public static final String UPDATEDATE = "updatedate";
	public static final String UPDATEBY = "updateby";
	public static final String COMPANYID = "companyid";
	
	private static Logger logger = Logger.getLogger (BaseDef.class);		
	
//	public static void initiate(ArrayList searchDisplayList, ArrayList searchList, ArrayList addList){
//		HashMap listMap = new HashMap();
//		listMap.put("object", AdminUserBeanDef.name);
//		listMap.put("as", UPDATEBY+"_value");
//		listMap.put("prefix", "u");
//		listMap.put("shadow", "Y");
//		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
//		searchDisplayList.add(listMap);
//		
//		listMap = new HashMap();
//		listMap.put("object", AdminUserBeanDef.usergroup);		
//		listMap.put("prefix", "u");
//		listMap.put("shadow", "Y");
//		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
//		searchDisplayList.add(listMap);
//	}
	
//	public static StringBuffer addUserRightQuery(){
//		StringBuffer query = new StringBuffer();
//		query.append (" Left Join ").append(AdminUserBeanDef.TABLE).append(" u on u.").append(AdminUserBeanDef.id.name);
//		query.append (" = a.").append(UPDATEBY).append(" ");	
//		return query;
//	}
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields, String fromTableScript)throws Exception{
		return getSearchScript(selectFields, fromTableScript, null);
	}
	public static StringBuffer[] getSearchScript(ArrayList selectFields, String fromTableScript, String key)throws Exception{
		StringBuffer mainQuery = new StringBuffer();
		StringBuffer keyOnlyQuery = new StringBuffer();
		StringBuffer fieldBuffer = new StringBuffer();
		DataObject field = null;
		String keyFieldName = "";
		HashMap map = null;
//		String dbfield = null;
		
		StringBuffer[] retBuffer = new StringBuffer[2];
		String prefix = null;
		String fieldName = null;
		String as = null;
		String custom = null;
		try{			
			for(int i=0; i<selectFields.size (); i++){
				map = (HashMap) selectFields.get(i);
//				logger.debug("map = "+map);
				field = (DataObject) map.get ("object");
				prefix = (String) map.get ("prefix");
				as = (String) map.get("as");
				custom = (String) map.get("custom");
				
//				logger.debug(field.name + ": " + field.key);
//				if("false".equals(dbfield)) continue;
				if(!CommonUtil.isEmpty(custom)){
					fieldBuffer.append (custom).append (",");
					continue;
				}
				if(field.key){
					if(!CommonUtil.isEmpty(prefix)){
						keyFieldName += prefix +"." +field.name + ",";				
					}else{
						keyFieldName += field.name + ",";		
					}
				}else{
					if(CommonUtil.isEmpty(prefix)){
						fieldName = field.name;
						if(map.get ("object2")!=null){
							fieldName = "Concat("+fieldName+",' ',"+((DataObject) map.get ("object2")).name+")";
						}
					}else{
						fieldName = prefix + "." + field.name;
						if(map.get ("object2")!=null){
							fieldName = "Concat("+fieldName+",' ',"+prefix + "." +((DataObject) map.get ("object2")).name+")";
						}
					}
					
					if(fieldName.endsWith(BaseDef.UPDATEDATE)){
						fieldName = "TIMESTAMPDIFF(SECOND, " + fieldName + ", now()) As " + UPDATEDATE;				
					}
					
					fieldBuffer.append (fieldName);
					if(!CommonUtil.isEmpty(as)){
						fieldBuffer.append (" As ").append(as);
					}					
					fieldBuffer.append (",");
				}
				
			}
			if(fieldBuffer.length()>0){
				fieldBuffer  = fieldBuffer.delete(fieldBuffer.length()-1, fieldBuffer.length());
			}
			if(!CommonUtil.isEmpty(keyFieldName)){
				keyFieldName  = keyFieldName.substring(0, keyFieldName.length()-1);
			}else{
				keyFieldName = key;
				//throw new Exception("selectFields must contain key"); 
			}
			mainQuery.append ("Select ").append(keyFieldName);		
			if(fieldBuffer.length()>0){
				mainQuery.append(",").append(fieldBuffer);
			}
			mainQuery.append (" ").append (fromTableScript);		
			
			keyOnlyQuery.append ("Select ").append(keyFieldName);				
			keyOnlyQuery.append (" ").append (fromTableScript);		
			
			retBuffer[0] = keyOnlyQuery;
			retBuffer[1] = mainQuery;
		}catch(Exception e){
			logger.debug("map = "+map);
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
	
	
}
