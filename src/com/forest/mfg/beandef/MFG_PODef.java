package com.forest.mfg.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.AdminUserBeanDef;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.SupplierProfileDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;

public class MFG_PODef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_PODef.class);	
	public static final String TABLE = "mfg_po";

	public static DataObject companyid = null;
	public static DataObject poid = null;
	
	public static DataObject supplierid = null;
	public static DataObject pono = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public static String KEY = "poid";
	public static String MODULE_NAME = "mfg_po";	
	public static String DEFAULT_SORT = "a.poid";
	public static String DEFAULT_SORT_DIRECTION = "desc";
	public final static String PERMANENT_DELETE = "false";
	public static final Class[] subTableClass = {MFG_PODetailDef.class};
	public static final String[] OPTIONS = {GeneralConst.REPORT};

	public static void setAdditional() {			
		logger.debug("MFG_PODef setAdditional");
		status.prefix = "a";
		// Search Condition
		poid.searchOption = SearchConst.EQUAL;
		pono.searchOption = SearchConst.LIKE;
		supplierid.searchOption = SearchConst.EQUAL;			
		
//		initiate(_searchDisplayList, _searchList, _addList);
		
		HashMap listMap = new HashMap();
		listMap.put("object", SupplierProfileDef.name);
		listMap.put("as", supplierid.name+"_value");
		listMap.put("prefix", "b");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		_searchDisplayList.add(listMap);

    	listMap = new HashMap();
    	listMap.put("object", companyid);
    	listMap.put("prefix", "a");
    	_autoSuggestSearchList.add(listMap);    	
    	
    	listMap = new HashMap();
    	listMap.put("object", MFG_PODetailDef.name);
    	listMap.put("prefix", "b");
    	_autoSuggestSearchList.add(listMap);    	
    	
    	listMap = new HashMap();
    	listMap.put("object", MFG_PODetailDef.podtlid);
    	listMap.put("prefix", "b");
    	listMap.put("astype", "key");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", MFG_PODetailDef.name);
    	listMap.put("prefix", "b");
    	listMap.put("astype", "value");
    	_autoSuggestSearchDisplayList.add(listMap);
	}	
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(SupplierProfileDef.TABLE).append(" b on b.").append(SupplierProfileDef.id.name);
			fromBuffer.append (" = a.").append(supplierid.name);	
//			fromBuffer.append(addUserRightQuery());
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}	
	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(MFG_PODef.TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(MFG_PODetailDef.TABLE).append(" b on b.").append(MFG_PODetailDef.poid.name);
			fromBuffer.append (" = a.").append(MFG_PODef.poid.name);			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());				
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
