package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class MFG_RawToProductDetailDef extends BaseDef{
	public static DataObject rtpdtlid = null;	
	public static DataObject rtpid = null;
	public static DataObject rawid = null;
	public static DataObject qty = null;
	public static DataObject widthformula = null; // (Formula)
	public static DataObject heightformula = null; // (Formula)
	public static DataObject comment = null; // Use to control selection (opt1-8), h, w 
	public static DataObject perform_by = null;
	public static DataObject position = null;

	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_rawtoproduct_detail";
	public static final String TABLE_PREFIX = "mfg_rawtoproduct_detail";
	public static final String MODULE_NAME = "mfg_rawtoproduct_detail";	
	public static final String DEFAULT_SORT = "position";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "rtpdtlid";
	public static final String PARENT_KEY = "rtpid";
	
	public final static String CONTROL_BAR = "Y";
	public final static String TABLE_CLASS_NAME = "sortable";	
	
	public static void setAdditional() {		
	
	}	
}
