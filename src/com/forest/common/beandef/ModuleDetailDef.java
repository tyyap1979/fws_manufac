package com.forest.common.beandef;

import java.util.ArrayList;

import com.forest.common.bean.DataObject;


public class ModuleDetailDef extends BaseDef{
	public static final String TABLE = "moduledetail";
	public static final String TABLE_PREFIX = "moduledetail";
	
	public static DataObject moduledetailid = null;		//PRI
	public static DataObject moduleid = null;	
	public static DataObject columnname = null;
	public static DataObject tablename = null;
	public static DataObject colspan = null;	
	public static DataObject prefix = null;
	public static DataObject type = null;
	public static DataObject xref = null;
	public static DataObject xrefclass = null;
	public static DataObject classname = null;
	
	public static DataObject position = null;
	public static DataObject htmloption = null;
	
	public static DataObject systemfield = null;
	public static DataObject shadow = null;
	public static DataObject columngroup = null;
	
	public static DataObject width = null;
	public static DataObject visibility = null;
	public static DataObject size = null;
		
	public static DataObject islisting = null;
	public static DataObject issearch = null;
	public static DataObject isadd = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "moduledetailid";
	public static String MODULE_NAME = "moduledetail";	
	public static String DEFAULT_SORT = "position";
	public static String DEFAULT_SORT_DIRECTION = "asc";

	public final static String PERMANENT_DELETE = "true";
	public static final String PARENT_KEY = "moduleid";
	
	public final static String CONTROL_BAR = "Y";
	public final static String TABLE_CLASS_NAME = "sortable";	
	
	public static void setAdditional() {		

	}
}
