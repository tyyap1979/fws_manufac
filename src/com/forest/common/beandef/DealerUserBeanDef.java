package com.forest.common.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.SearchConst;


public class DealerUserBeanDef extends BaseDef{
	public static final String TABLE = "client_user";

	public static DataObject cid = null;
	public static DataObject companyid = null;
	public static DataObject email = null;	
	public static DataObject name = null;
	public static DataObject password = null;
	
	public static DataObject title = null;
	public static DataObject dob = null;
	public static DataObject address = null;
	public static DataObject city = null;
	public static DataObject state = null;
	public static DataObject postcode = null;
	public static DataObject country = null;
	public static DataObject phoneno = null;
	
	public static DataObject businessphone = null;
	public static DataObject homephone = null;
	public static DataObject fax = null;
	
	public static DataObject signtype = null;
	public static DataObject cookieskey = null;
	public static DataObject type = null;	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public static int columns = 2;
	
	public static String KEY = "cid";
	public static String MODULE_NAME = "clientuser";	
	public static String DEFAULT_SORT = "name";
	public static String DEFAULT_SORT_DIRECTION = "asc";
	public final static String PERMANENT_DELETE = "false";

	public static void setAdditional() {				
		// Search Condition
		cid.searchOption = SearchConst.EQUAL;
		name.searchOption = SearchConst.LIKE;
		email.searchOption = SearchConst.LIKE;		
		
		HashMap listMap = new HashMap();
    	listMap.put("object", companyid);
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", name);
    	_autoSuggestSearchList.add(listMap);
    	
    	
    	listMap = new HashMap();
    	listMap.put("object", cid);
    	listMap.put("astype", "key");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", name);
    	listMap.put("astype", "value");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", email);    	
    	_autoSuggestSearchDisplayList.add(listMap);    	
	}	
	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		return getSearchScript(selectFields, "From "+TABLE);
	}
}
