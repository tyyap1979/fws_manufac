package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class MFG_RawToProductDef extends BaseDef{
	public static DataObject companyid = null;
	public static DataObject rtpid = null;	
	public static DataObject prodid = null;	
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_rawtoproduct";
	public static final String MODULE_NAME = "mfg_rawtoproduct";	
	public static final String DEFAULT_SORT = "updatedate";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "rtpid";	
	
	public static final Class[] subTableClass = {MFG_RawToProductDetailDef.class};
	
	public static void setAdditional() {		

    	
	}	
}
