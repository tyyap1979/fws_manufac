package com.forest.common.beandef;

import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.SearchConst;

public class ModuleAdminDef extends BaseDef{
	public static final String TABLE = "module";

	public static DataObject moduleid = null;		//PRI
	public static DataObject tablename = null;	
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "moduleid";
	public static String MODULE_NAME = "moduleadmin";	
	public static String DEFAULT_SORT = "updatedate";
	public static String DEFAULT_SORT_DIRECTION = "desc";

	public final static String PERMANENT_DELETE = "false";
	public static final Class[] subTableClass = {ModuleDetailDef.class};
	public static void setAdditional() {		
		tablename.searchOption = SearchConst.LIKE;
	}
}
