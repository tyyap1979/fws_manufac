package com.forest.common.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;


public class ClientUserBeanDef extends BaseDef{
	public static final String TABLE = "client_user";

	public static DataObject cid = null;
	public static DataObject companyid = null;
	public static DataObject clientno = null;
	public static DataObject email = null;	
	public static DataObject firstname = null;
	public static DataObject lastname = null;
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
	
	// For Dealer register
	public static DataObject companyname = null;	
	public static DataObject tob = null; //Type of Business
	public static DataObject nob = null; //Nature of Business
	
	
	public static DataObject newsletter = null;
	public static DataObject website = null;
	public static DataObject frommedia = null;
	public static DataObject ipcountry = null;
	
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
	public static String DEFAULT_SORT = "updatedate";
	public static String DEFAULT_SORT_DIRECTION = "asc";
	public final static String PERMANENT_DELETE = "true";
	
	public static void setAdditional() {				
		// Search Condition
		cid.searchOption = SearchConst.EQUAL;
		firstname.searchOption = SearchConst.LIKE;
		lastname.searchOption = SearchConst.LIKE;
		email.searchOption = SearchConst.LIKE;		
		
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
    	listMap.put("object", cid);
    	listMap.put("astype", "key");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", firstname);
    	listMap.put("custom", "Concat("+firstname+",' ',"+lastname+", ' (',"+email+", ')') As "+firstname);
//    	listMap.put("as", firstname.name);
    	listMap.put("astype", "value");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
//    	listMap = new HashMap();
//    	listMap.put("object", email);    
//    	listMap.put("astype", "value");
//    	_autoSuggestSearchDisplayList.add(listMap);    	
	}	
	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		return getSearchScript(selectFields, "From "+TABLE);
	}
}
