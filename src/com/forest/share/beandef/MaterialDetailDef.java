package com.forest.share.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.GeneralConst;

public class MaterialDetailDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (materialDetailDef.class);	
	
	public static DataObject mdtlid = null;
	public static DataObject mid = null;	
	public static DataObject filename = null;
	public static DataObject position = null;
	public static DataObject description = null;
	public static DataObject tag = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "material_detail"; // No Table
	public static final String TABLE_PREFIX = "materialDetail";
	public static final String MODULE_NAME = "materialDetail";	
	public static final String DEFAULT_SORT = "position";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "mdtlid";
	public static final String PARENT_KEY = "mid";
	
	public final static String CONTROL_BAR = "Y";	
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_ADD_CUSTOM;
	public final static String TABLE_CLASS_NAME = "sortable";	
	
	public static void setAdditional() {				
		
	}			
}
