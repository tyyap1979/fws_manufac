package com.forest.mfg.beandef;

import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.SearchConst;

public class MFG_SupplierProductSelectionDef extends BaseDef{

	public static final String TABLE = "mfg_supplierproductselection";

//	public static DataObject companyid = null;
	public static DataObject selectid = null;		//PRI	
	public static DataObject prodid = null;
	public static DataObject prodoptid = null;
	public static DataObject position = null;	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "selectid";
	public static String MODULE_NAME = "mfg_supplierproductselection";	
	public static String DEFAULT_SORT = "position";
	public static String DEFAULT_SORT_DIRECTION = "asc";
	public static final String PARENT_KEY = "prodid";
	
	public static final String TABLE_PREFIX = "selectionDetail";
	
	public final static String PERMANENT_DELETE = "true";
	public final static String CONTROL_BAR = "N";
	public final static String TABLE_CLASS_NAME = "sortable";	
	
	public static void setAdditional() {						
		selectid.searchOption = SearchConst.EQUAL;
		prodid.searchOption = SearchConst.EQUAL;
	}
	
}
