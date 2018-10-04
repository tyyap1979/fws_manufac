package com.forest.retail.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class RETAIL_ProductGroupDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (retail_categoryDef.class);	
	public static DataObject companyid = null;
	public static DataObject pid = null; // Primary Key
	public static DataObject code = null;			
	public static DataObject status = null;	
	public static DataObject updatedate = null;			
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "retail_productgroup";
	public static final String MODULE_NAME = "retailproductgroup";	
	public static final String DEFAULT_SORT = "code";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "pid";	
	public static final Class[] subTableClass = {RETAIL_ProductGroupDetailDef.class};
	
	public static void setAdditional() {				

	}		
}
