package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.SearchConst;

public class MFG_RawDef extends BaseDef{
	public static DataObject companyid = null;
	public static DataObject rawid = null;	
	public static DataObject groupid = null;	
	public static DataObject name = null;	
	
	public static DataObject cost = null;
	public static DataObject qty = null;
	public static DataObject reorder = null;
	public static DataObject unittype = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_raw";
	public static final String MODULE_NAME = "mfg_raw";	
	public static final String DEFAULT_SORT = "name";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "rawid";	
	
	public static void setAdditional() {		
		name.searchOption = SearchConst.LIKE;
    	
	}	
}
