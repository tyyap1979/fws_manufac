package com.forest.common.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.ComponentConst;


public class CountryBeanDef extends BaseDef{
	public static final String TABLE = "country";

	public static DataObject countrycode = null;		//PRI
	public static DataObject countrydesc = null;
	public static DataObject countrycurrency = null;
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "countrycode";
	public static String MODULE_NAME = "country";	
	public static String DEFAULT_SORT = "countrydesc";
	public static String DEFAULT_SORT_DIRECTION = "asc";
//	public static String LAYOUT_PREFIX = "system.admin.country";

	public final static String PERMANENT_DELETE = "false";
	
	public static void setAdditional() {		
			
		HashMap listMap = new HashMap();
		// Search Listing 
	    	listMap.put("object", countrycode);
	    	listMap.put("width", "50");
	    	listMap.put("sortable", "false");
	    	listMap.put("label", "false");
	    	listMap.put("type", String.valueOf(ComponentConst.CHECKBOX));	    	
	    	_searchDisplayList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", countrycode);
	    	listMap.put("width", "150");
	    	listMap.put("sortable", "true");
	    	listMap.put("label", "true");
	    	_searchDisplayList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", countrycurrency);
	    	listMap.put("width", "100");
	    	listMap.put("sortable", "true");
	    	listMap.put("label", "true");
	    	_searchDisplayList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", countrydesc);
	    	listMap.put("width", "");
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
	    	listMap.put("object", countrycode);
	    	listMap.put("visibility", "show");
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));
	    	listMap.put("displayControl", "true");
	    	_searchList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", countrycurrency);
	    	listMap.put("visibility", "hide");
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.SELECT));
	    	listMap.put("xref", "SELECT_CURRENCY");   
	    	_searchList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", countrydesc);
	    	listMap.put("visibility", "hide");
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));
	    	_searchList.add(listMap);
	    	
	    // Add Form
	    	listMap = new HashMap();
	    	listMap.put("object", countrycode);
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));    	
	    	_addList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", countrydesc);
	    	listMap.put("size", "30");
	    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));    	
	    	_addList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", countrycurrency);
	    	listMap.put("size", "10");
	    	listMap.put("type", String.valueOf(ComponentConst.SELECT));
	    	listMap.put("xref", "SELECT_CURRENCY");     	
	    	_addList.add(listMap);
	    	
	    	listMap = new HashMap();
	    	listMap.put("object", status);
	    	listMap.put("size", "50");
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
