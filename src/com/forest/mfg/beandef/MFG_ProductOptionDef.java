package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.SearchConst;

public class MFG_ProductOptionDef extends BaseDef{
	public static DataObject prodoptid = null;
	public static DataObject companyid = null;	
	
	public static DataObject groupname = null;	
	public static DataObject description = null;
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_productopt";
	public static final String MODULE_NAME = "mfg_productopt";	
	public static final String DEFAULT_SORT = "groupname";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "prodoptid";
	public static final Class[] subTableClass = {MFG_ProductOptionDetailDef.class};
	
	public static void setAdditional() {
		groupname.searchOption = SearchConst.LIKE;		
	}		
}
