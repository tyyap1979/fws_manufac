package com.forest.retail.beandef;

import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;

public class RETAIL_ProductImageDef extends BaseDef{
	public static final String TABLE = "retail_product_image";

	public static DataObject imageid = null;		//PRI	
	public static DataObject prodid = null;
	public static DataObject filename = null;
	public static DataObject position = null;
	public static DataObject description = null;	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "imageid";
	public static String MODULE_NAME = "retail_product_image";	
	public static String DEFAULT_SORT = "position";
	public static String DEFAULT_SORT_DIRECTION = "asc";
	public static final String PARENT_KEY = "prodid";
	
	public static final String TABLE_PREFIX = "imageDetail";
	
	public final static String PERMANENT_DELETE = "true";
	public final static String CONTROL_BAR = "Y";
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_UPLOAD;
	public final static String TABLE_CLASS_NAME = "sortable";	
	
	public static void setAdditional() {						
		imageid.searchOption = SearchConst.EQUAL;
		prodid.searchOption = SearchConst.EQUAL;
	}
}
