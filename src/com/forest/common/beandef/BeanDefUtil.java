package com.forest.common.beandef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.constants.ComponentConst;
import com.forest.common.util.CommonUtil;

public class BeanDefUtil {
//	/** initial size of the bundle cache */
//    private static final int INITIAL_CACHE_SIZE = 25;
//    /** capacity of cache consumed before it should grow */
//    private static final float CACHE_LOAD_FACTOR = (float)1.0;  
//    public static SoftCache beanCacheList = new SoftCache(INITIAL_CACHE_SIZE, CACHE_LOAD_FACTOR);
    public static HashMap beanCacheList = new HashMap();
    
	private static Logger logger = Logger.getLogger(BeanDefUtil.class);
	public static final String MODULE_NAME = "MODULE_NAME";
	public static final String CUSTOM_CONTROL = "CUSTOM_CONTROL";
	public static final String TABLE = "TABLE";
	public static final String DEFAULT_SORT = "DEFAULT_SORT";
	public static final String DEFAULT_SORT_DIRECTION = "DEFAULT_SORT_DIRECTION";
	public static final String KEY = "KEY";
	public static final String AUTOGEN_FIELDS = "AUTOGEN_FIELDS";
	public static final String PARENT_KEY = "PARENT_KEY";	
	public static final String TABLE_PREFIX = "TABLE_PREFIX";
	public static final String PERMANENT_DELETE = "PERMANENT_DELETE";
	public static final String TABLE_CLASS_NAME = "TABLE_CLASS_NAME";
	
	public static final String AS_SEARCH_DISPLAY_LIST = "_autoSuggestSearchDisplayList";
	public static final String AS_SEARCH_LIST = "_autoSuggestSearchList";
	
	public static final String DISPLAY_TYPE_SEARCH = "0";
	public static final String DISPLAY_TYPE_LISTING = "1";
	public static final String DISPLAY_TYPE_ADD = "2";
	
	public static void clean(){
		logger.debug("BeanDefUtil clean");
		beanCacheList.clear();		
	}
	
	public static Object getField(Class defClass, String name)throws Exception{
		Object rtnObj = null;
		try{
			rtnObj = defClass.getDeclaredField(name).get(defClass);
		}catch (Exception e) {
			logger.error(defClass + "." + name);
			//throw e;
		}
		return rtnObj;
	}
	
	public static DataObject getDataObject(Class defClass, String name)throws Exception{
		return (DataObject) defClass.getDeclaredField(name).get(defClass);
	}
	
	public static DataObject getKeyObject(Class defClass)throws Exception{
		String key = (String) defClass.getDeclaredField(KEY).get(defClass);
		if(key.indexOf(".")!=-1){
			key = key.substring(key.indexOf(".")+1, key.length());
		}
		return (DataObject) defClass.getDeclaredField(key).get(defClass);
	}
	
	public static DataObject getParentKeyObject(Class defClass)throws Exception{
		String key = (String) defClass.getDeclaredField(PARENT_KEY).get(defClass);
		return (DataObject) defClass.getDeclaredField(key).get(defClass);
	}
	
	public static ArrayList getArrayList(Class defClass, Connection conn, String displayType, ShopInfoBean shopInfoBean, ClientBean clientBean)throws Exception{		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList dataList = null;
		ArrayList hardcodeList = null;
		StringBuffer query = new StringBuffer();
//		StringBuffer userGroupQuery = new StringBuffer();
		HashMap dataRow = null;
		String columnName = null;
		String visibility = null;
		String xref = null;
		int i = 0;
		DataObject dataObj = null;		
		String xrefClass = null;
		String systemField = null;
		try{			
			dataList = (ArrayList) beanCacheList.get(defClass.getName()+"_"+displayType);
			if(dataList!=null){
				return dataList;
			}else{
				logger.debug("Create New For "+defClass.getName()+"["+displayType+"]");
				dataList = new ArrayList();
			}
			
			// Get Predefined data
			if(displayType.equals(DISPLAY_TYPE_ADD)){
				hardcodeList = getArrayList(defClass, "_addList");				
			}else if(displayType.equals(DISPLAY_TYPE_LISTING)){
				hardcodeList = getArrayList(defClass, "_searchDisplayList");				
			}else if(displayType.equals(DISPLAY_TYPE_SEARCH)){
				hardcodeList = getArrayList(defClass, "_searchList");				
			}
			if(hardcodeList!=null && hardcodeList.size()>0){
				for(i=0; i<hardcodeList.size(); i++){
					dataList.add(hardcodeList.get(i));
				}
			}
			
			query.append("Select ");
			query.append(ModuleDetailDef.columnname).append(",");
			query.append(ModuleDetailDef.colspan).append(",");			
			query.append(ModuleDetailDef.type).append(",");
			query.append(ModuleDetailDef.prefix).append(",");
			query.append(ModuleDetailDef.xref).append(",");
			query.append(ModuleDetailDef.xrefclass).append(",");
			query.append(ModuleDetailDef.classname).append(",");
			query.append(ModuleDetailDef.htmloption).append(",");
			query.append(ModuleDetailDef.systemfield).append(",");
			query.append(ModuleDetailDef.shadow).append(",");
			query.append(ModuleDetailDef.columngroup).append(",");
			
			// Client Config
			query.append(ModuleDetailDef.width).append(",");
			query.append(ModuleDetailDef.visibility).append(",");
			query.append(ModuleDetailDef.size);

			
			query.append(" From ").append(ModuleDetailDef.TABLE);
			query.append(" Where ").append(ModuleAdminDef.tablename).append("='").append(defClass.getDeclaredField(TABLE).get(defClass)).append("'");		
			
			if(displayType.equals(DISPLAY_TYPE_SEARCH)){
				query.append(" And (").append(ModuleDetailDef.systemfield).append(" = 'Y'");
				query.append(" Or (").append(ModuleDetailDef.issearch).append(" = 'Y'))");
				query.append(" Order By ").append(ModuleDetailDef.visibility).append(" desc");
				query.append(", ").append(ModuleDetailDef.position).append(" ASC");
			}else if(displayType.equals(DISPLAY_TYPE_LISTING)){
				query.append(" And (").append(ModuleDetailDef.systemfield).append(" = 'Y'");
				query.append(" Or (").append(ModuleDetailDef.islisting).append(" = 'Y'))");
				query.append(" Order By ").append(ModuleDetailDef.position).append(" ASC");
			}else if(displayType.equals(DISPLAY_TYPE_ADD)){
				query.append(" And (").append(ModuleDetailDef.systemfield).append(" = 'Y'");
				query.append(" Or (").append(ModuleDetailDef.isadd).append(" = 'Y'))");
				query.append(" Order By ").append(ModuleDetailDef.position).append(" ");
			}
						
			logger.debug("BeanDefUtil.getArrayList query = "+query);
			pstmt = conn.prepareStatement(query.toString());
			rs = pstmt.executeQuery();
			while(rs.next()){
				columnName = rs.getString(ModuleDetailDef.columnname.name);
				visibility = rs.getString(ModuleDetailDef.visibility.name);
				xref = rs.getString("xref");
				xrefClass = rs.getString("xrefclass");
				systemField = rs.getString("systemfield");
				dataObj = (DataObject) defClass.getDeclaredField(columnName).get(defClass);
				dataObj.prefix = rs.getString(ModuleDetailDef.prefix.name);
//				logger.debug(dataObj.name + ": " + dataObj.key);
				dataRow = new HashMap();
				dataRow.put("object", dataObj);
				dataRow.put("width",rs.getString(ModuleDetailDef.width.name));
				dataRow.put("colspan",rs.getString(ModuleDetailDef.colspan.name));
								
				if(displayType.equals(DISPLAY_TYPE_LISTING)){
					if(dataObj.key){
						dataRow.put("type", String.valueOf(ComponentConst.CHECKBOX));
					}else{
						if(String.valueOf(ComponentConst.IMAGE).equals(rs.getString(ModuleDetailDef.type.name))){
							dataRow.put("type", String.valueOf(ComponentConst.IMAGE));
						}else{
							if("Y".equalsIgnoreCase(systemField)){
								dataRow.put("type", rs.getString(ModuleDetailDef.type.name));
							}else{
								dataRow.put("type", String.valueOf(ComponentConst.TEXT));
							}
						}
					}
				}else if(displayType.equals(DISPLAY_TYPE_ADD)){
					dataRow.put("type", rs.getString(ModuleDetailDef.type.name));
//					if("S".equalsIgnoreCase(systemField)){
//						dataRow.put("shadow", "Y");
//					}
				}else if(displayType.equals(DISPLAY_TYPE_SEARCH)){					
					if(String.valueOf(ComponentConst.TEXT).equals(rs.getString(ModuleDetailDef.type.name))){
						dataRow.put("type", String.valueOf(ComponentConst.TEXTBOX));
					}else{
						dataRow.put("type",rs.getString("type"));
					}
				}
				
				
				dataRow.put("xref", xref);
				dataRow.put("size",rs.getString(ModuleDetailDef.size.name));
				dataRow.put("prefix",rs.getString(ModuleDetailDef.prefix.name));
				dataRow.put("htmloption",rs.getString(ModuleDetailDef.htmloption.name));
				dataRow.put("columngroup",rs.getString(ModuleDetailDef.columngroup.name));
				dataRow.put("visibility",visibility);
				dataRow.put("systemfield",systemField);
				dataRow.put("shadow",rs.getString(ModuleDetailDef.shadow.name));
				
				if(!CommonUtil.isEmpty(xrefClass)){					
					dataRow.put("xrefclass", Class.forName(xrefClass));	
				}
				
				dataRow.put("classname",rs.getString(ModuleDetailDef.classname.name));

//				logger.debug(displayType + ", dataRow = "+dataRow);
				dataList.add(dataRow);
//				if(displayType.equals(DISPLAY_TYPE_SEARCH)){					
//					if("hide".equals(visibility) && gotControl==false){
//						dataRow = new HashMap();
//						dataRow = (HashMap)dataList.get(i-1);
//						dataRow.put("displayControl", "true");	
//						gotControl = true;
//					}
//					// To prevent readOnly 
//					dataRow.put("htmloption","");
//				}
//				logger.debug("dataRow = "+dataRow);
				i++;
			}
			
			beanCacheList.put(defClass.getName()+"_"+displayType, dataList);			
		}catch(Exception e){
			logger.debug("defClass="+defClass+", columnName="+columnName);
			logger.error(e, e);
		}
		
		return dataList;
	}	
	
	public static ArrayList getArrayList(Class defClass, String name)throws Exception{
		return (ArrayList) defClass.getDeclaredField(name).get(defClass);
	}
	
	public static Class[] getSubClass(Class defClass)throws Exception{
		Class[] clsArray = null;
		try{
			clsArray = (Class[]) defClass.getDeclaredField("subTableClass").get(defClass);
		}catch (Exception e) {
			 
		}
		return clsArray;
	}
	
	public static HashMap addField(DataObject obj, String prefix, int type, String xref, String xrefclass, String shadow, String as){
		HashMap listMap = new HashMap();
		listMap.put("object", obj);		
		listMap.put("prefix", prefix);
		listMap.put("shadow", shadow);
		listMap.put("xref", xref);
		listMap.put("xrefclass", xrefclass);
		listMap.put("type", String.valueOf(type));
		if(as!=null) listMap.put("as", as);
		return listMap;
	}
}
