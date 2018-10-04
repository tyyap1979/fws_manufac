package com.forest.retail.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.SearchConst;

public class RETAIL_ProductDef extends BaseDef{
	public static DataObject companyid = null;
	
	public static DataObject prodid = null;		//PRI
	public static DataObject prodcode = null;
	public static DataObject internalcode = null;			
	public static DataObject prodname = null;
	public static DataObject description = null;
	public static DataObject discount = null;	
	
	public static DataObject catid = null;	
	public static DataObject opt1_name = null;
	public static DataObject opt2_name = null;
	public static DataObject opt3_name = null;
	public static DataObject opt4_name = null;
		
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static int columns = 2;
	public static String[] tabs = {"General","Description"};
	
	public static final String TABLE = "retail_product";
	public static final String MODULE_NAME = "retail_product";	
	public static final String DEFAULT_SORT = "prodid";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "prodid";
	public static String AUTOGEN_FIELDS = "prodcode,"; // Use when copying. Ignore field when copying
	
	public static final Class[] subTableClass = {RETAIL_ProductOptionDef.class, RETAIL_ProductAttributeDef.class, RETAIL_ProductImageDef.class};	
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	
	public static ArrayList _jsonSearchDisplayList = new ArrayList();	
	
	public static void setAdditional() {	
		prodname.searchOption = SearchConst.LIKE;
		description.searchOption = SearchConst.LIKE;		
		
		HashMap listMap = new HashMap();
    	listMap.put("object", companyid);
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", prodname);
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", prodcode);
    	_autoSuggestSearchList.add(listMap);
    	
    	
    	listMap = new HashMap();
    	listMap.put("object", prodid);
    	listMap.put("astype", "key");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", prodname);
    	listMap.put("custom", "Concat("+prodname+",' (',"+prodcode+", ')') As "+prodname);
    	listMap.put("astype", "value");
    	_autoSuggestSearchDisplayList.add(listMap);
	}	
	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		return getSearchScript(selectFields, "From "+TABLE);
	}
	
	
}
