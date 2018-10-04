package com.forest.retail.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class RETAIL_CategoryDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (retail_categoryDef.class);	
	public static DataObject companyid = null;
	public static DataObject catid = null; // Primary Key
	public static DataObject catname = null;		
	public static DataObject catdesc = null;	
	public static DataObject status = null;	
	public static DataObject updatedate = null;			
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "retail_category";
	public static final String MODULE_NAME = "retailCategory";	
	public static final String DEFAULT_SORT = "catname";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "catid";	
//	public static final Class[] subTableClass = {RETAIL_CategoryDetailDef.class};
	
	public static void setAdditional() {				

	}		
}
