package com.forest.common.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.SearchConst;


public class SupplierProfileDef extends BaseDef{
	public static final String TABLE = "supplierprofile";

	public static DataObject companyid = null;
	public static DataObject id = null;
	
	public static DataObject code = null;
	public static DataObject name = null;	
	public static DataObject contactperson = null;
	
	public static DataObject terms = null;
	public static DataObject creditlimit = null;
	
	public static DataObject address = null;
	public static DataObject city = null;
	public static DataObject state = null;
	public static DataObject postcode = null;
	public static DataObject country = null;
	
	public static DataObject email = null;	
	public static DataObject mobileno = null;
	public static DataObject phoneno = null;
	public static DataObject faxno = null;
	public static DataObject gstid = null;
	public static DataObject sstid = null;
	
	public static DataObject cookieskey = null;
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public static String KEY = "id";
	public static String MODULE_NAME = "supplierprofile";	
	public static String DEFAULT_SORT = "updatedate";
	public static String DEFAULT_SORT_DIRECTION = "desc";
	public final static String PERMANENT_DELETE = "false";

	public static void setAdditional() {				
		// Search Condition
		id.searchOption = SearchConst.EQUAL;
		name.searchOption = SearchConst.LIKE;
		email.searchOption = SearchConst.LIKE;		
		
		HashMap listMap = new HashMap();
    	listMap.put("object", companyid);
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", name);
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", id);
    	listMap.put("astype", "key");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", name);
    	listMap.put("astype", "value");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", contactperson);    	
    	_autoSuggestSearchDisplayList.add(listMap);
    	
	}	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		return getSearchScript(selectFields, "From "+TABLE);
	}
}
