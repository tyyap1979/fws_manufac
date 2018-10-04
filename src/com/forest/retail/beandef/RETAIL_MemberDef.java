package com.forest.retail.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;


public class RETAIL_MemberDef extends BaseDef{
	public static final String TABLE = "retail_member";

	public static DataObject mid = null;
	public static DataObject companyid = null;	
	public static DataObject email = null;	
	public static DataObject firstname = null;
	public static DataObject lastname = null;
	public static DataObject password = null;
		
	public static DataObject dob = null;
	public static DataObject address = null;
	public static DataObject city = null;
	public static DataObject state = null;
	public static DataObject postcode = null;
	public static DataObject country = null;
	public static DataObject phoneno = null;
	
	public static DataObject newsletter = null;
	public static DataObject ipcountry = null;
	
	public static DataObject signtype = null;
	public static DataObject cookieskey = null;
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public static int columns = 2;
	
	public static String KEY = "mid";
	public static String MODULE_NAME = "retailmember";	
	public static String DEFAULT_SORT = "updatedate";
	public static String DEFAULT_SORT_DIRECTION = "asc";
	public final static String PERMANENT_DELETE = "false";
	
	public static void setAdditional() {				
		HashMap listMap = new HashMap();
    	listMap.put("object", companyid);
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", firstname);
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", email);
    	_autoSuggestSearchList.add(listMap);
    	
    	
    	listMap = new HashMap();
    	listMap.put("object", mid);
    	listMap.put("astype", "key");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", firstname);
    	listMap.put("custom", "Concat("+firstname+",' ',"+lastname+", ' (',"+email+", ')') As "+firstname);

    	listMap.put("astype", "value");
    	_autoSuggestSearchDisplayList.add(listMap);
	}
	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		return getSearchScript(selectFields, "From "+TABLE);
	}
}
