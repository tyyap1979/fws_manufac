package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class MFG_CartDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (MFG_CartDef.class);	
	public static DataObject companyid = null;
	public static DataObject cartid = null; // Primary Key
	public static DataObject email = null;			
	public static DataObject updatedate = null;		
	
	// Shadow Field
	public static DataObject jobcode = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_cart";
	public static final String MODULE_NAME = "mfg_cart";	
	public static final String DEFAULT_SORT = "updatedate";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "cartid";
	public static final Class[] subTableClass = {MFG_CartDetailDef.class};
		
	public static void setAdditional() {				
		
	}	
}
