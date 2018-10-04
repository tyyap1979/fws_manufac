package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class MFG_Grouping extends BaseDef{
	public static DataObject companyid = null;
	public static DataObject groupid = null;
	public static DataObject groupname = null;
	public static DataObject grouptype = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_grouping";
	public static final String MODULE_NAME = "mfg_grouping";	
	public static final String DEFAULT_SORT = "updatedate";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "groupid";
		
	public static void setAdditional() {				
    	
	}	
}
