package com.forest.mfg.beandef;

import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.SearchConst;

public class MFG_PODetailDef extends BaseDef{
	public static final String TABLE = "mfg_podtl";
	
	public static DataObject podtlid = null;
	public static DataObject poid = null;
	
	public static DataObject name = null;
	public static DataObject price = null;
	public static DataObject qty = null;
	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public static String KEY = "podtlid";
	public static String MODULE_NAME = "mfg_podtl";	
	public static final String TABLE_PREFIX = "mfgPodtl";
	public static String DEFAULT_SORT = "podtlid";
	public static String DEFAULT_SORT_DIRECTION = "";
	public final static String PERMANENT_DELETE = "true";
	public static final String PARENT_KEY = "poid";
	public final static String CONTROL_BAR = "Y";

	public static void setAdditional() {				
		// Search Condition
		podtlid.searchOption = SearchConst.EQUAL;
		name.searchOption = SearchConst.LIKE;
		
		
	}			
	
	
}
