package com.forest.common.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.ComponentConst;


public class CurrencyBeanDef extends BaseDef{
	public static final String TABLE = "currency";

	public static DataObject currencycode = null;		//PRI
	public static DataObject currencydesc = null;
	public static DataObject rate = null;
	public static DataObject symbol = null;
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "currencycode";
	public static String MODULE_NAME = "currency";	
	public static String DEFAULT_SORT = "currencydesc";
	public static String DEFAULT_SORT_DIRECTION = "asc";
//	public static String LAYOUT_PREFIX = "system.admin.currency";

	public final static String PERMANENT_DELETE = "false";
	
	public static void setAdditional() {		
			
		HashMap listMap = new HashMap();
		// Search Listing 
	    	listMap.put("object", currencycode);
	    	listMap.put("width", "50");
	    	listMap.put("sortable", "false");
	    	listMap.put("label", "false");
	    	listMap.put("type", String.valueOf(ComponentConst.CHECKBOX));	    	
	    	_searchDisplayList.add(listMap);	  
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", currencydesc);
	    	listMap.put("width", "");
	    	listMap.put("sortable", "true");
	    	listMap.put("label", "true");
	    	_searchDisplayList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", rate);
	    	listMap.put("width", "150");
	    	listMap.put("sortable", "true");	 
	    	listMap.put("label", "true");
	    	_searchDisplayList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", updatedate);
	    	listMap.put("width", "100");
	    	listMap.put("sortable", "true");
	    	listMap.put("label", "true");
	    	_searchDisplayList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", status);
	    	listMap.put("width", "100");
	    	listMap.put("sortable", "true");	    	
	    	listMap.put("label", "true");
	    	listMap.put("xref", "HASH_STATUS");
	    	_searchDisplayList.add(listMap);
	    	
	    // Search Form
	    	listMap = new HashMap();
	    	listMap.put("object", currencycode);
	    	listMap.put("visibility", "show");
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));
	    	listMap.put("displayControl", "true");
	    	_searchList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", currencydesc);
	    	listMap.put("visibility", "hide");
	    	listMap.put("size", "30");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));
	    	_searchList.add(listMap);	    	
	    	
	    // Add Form
	    	listMap = new HashMap();
	    	listMap.put("object", currencycode);
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));    	
	    	_addList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", currencydesc);
	    	listMap.put("size", "30");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));    	
	    	_addList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", rate);
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));    	
	    	_addList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", status);
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.SELECT));    	
	    	listMap.put("xref", "SELECT_STATUS");     	
	    	_addList.add(listMap);	
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", updatedate);
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.HIDDEN));    	
	    	_addList.add(listMap);
	}
}
