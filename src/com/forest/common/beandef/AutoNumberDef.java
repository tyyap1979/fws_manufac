package com.forest.common.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.SearchConst;

public class AutoNumberDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (AutoNumberDef.class);
	
	public static DataObject companyid = null;
	public static DataObject id = null;	
	public static DataObject code = null;		
	public static DataObject format = null;
	public static DataObject curnum = null; //Next number
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "gennumber";
	public static final String MODULE_NAME = "gennumber";	
	public static final String DEFAULT_SORT = "code";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "id";	
	
	
	public static void setAdditional() {		
		code.searchOption = SearchConst.LIKE;
		format.searchOption = SearchConst.LIKE;		
	}	
}
