package com.forest.common.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.SearchConst;
import com.forest.mfg.beandef.MFG_CustProductCustomerPriceDef;


public class CustomerProfileDef extends BaseDef{
	public static final String TABLE = "customerprofile";

	public static DataObject companyid = null;
	public static DataObject customerid = null;
	
	public static DataObject code = null;
	public static DataObject name = null;
	public static DataObject type = null;	
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
	public static DataObject salesby = null;
	
	public static DataObject note = null;
	
	public static DataObject cookieskey = null;
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static int columns = 2;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public static String KEY = "customerid";
	public static String MODULE_NAME = "customerprofile";	
	public static String DEFAULT_SORT = "a.name";
	public static String DEFAULT_SORT_DIRECTION = "desc";
	public final static String PERMANENT_DELETE = "false";

	public static final Class[] subTableClass = {MFG_CustProductCustomerPriceDef.class};
	public static void setAdditional() {				
		// Search Condition
		customerid.searchOption = SearchConst.EQUAL;
		name.searchOption = SearchConst.LIKE;
		email.searchOption = SearchConst.LIKE;	
		status.prefix = "a";
		
//		initiate(_searchDisplayList, _searchList, _addList);
		
		HashMap listMap = new HashMap();
    	listMap.put("object", companyid);
    	listMap.put("prefix", "a");
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", name);
    	listMap.put("prefix", "a");
    	_autoSuggestSearchList.add(listMap);
    	
    	
    	listMap = new HashMap();
    	listMap.put("object", customerid);
    	listMap.put("astype", "key");
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", name);
    	listMap.put("astype", "value");
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", contactperson);    
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", salesby);    	
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", terms);    	
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", type);    
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", note);    
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
//    	listMap = new HashMap();
//    	listMap.put("object", AdminUserBeanDef.updateby);    
//    	listMap.put("prefix", "u");
//    	_autoSuggestSearchDisplayList.add(listMap);
	}	
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
//			fromBuffer.append(addUserRightQuery());
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
		}catch(Exception e){			
			throw e;
		}
		
		return retBuffer;
	}	
	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 	
//			fromBuffer.append(addUserRightQuery());
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
		}catch(Exception e){			
			throw e;
		}
		
		return retBuffer;						
	}
}
