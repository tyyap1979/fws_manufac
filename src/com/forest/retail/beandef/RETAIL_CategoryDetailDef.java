package com.forest.retail.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.GeneralConst;

public class RETAIL_CategoryDetailDef extends BaseDef{
	public static DataObject catdetailid = null;	
	public static DataObject catid = null;
	public static DataObject filename = null;
	public static DataObject position = null;
	public static DataObject description = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "retail_category_detail";
	public static final String TABLE_PREFIX = "retailcatdetail";
	public static final String MODULE_NAME = "retailCategoryDetail";	
	public static final String DEFAULT_SORT = "position";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "catdetailid";
	public static final String PARENT_KEY = "catid";
	
	public final static String CONTROL_BAR = "Y";	
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_UPLOAD;
	
	public static void setAdditional() {		
	}		
	
}
