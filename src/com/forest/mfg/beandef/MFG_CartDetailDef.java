package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.GeneralConst;

public class MFG_CartDetailDef extends BaseDef{
	public static DataObject cartdetailid = null;	
	public static DataObject cartid = null;
	public static DataObject prodid = null;
	
	public static DataObject measurement = null; // 12in x 20 1/6in    or 1000mm x 500mm
	public static DataObject width = null;
	public static DataObject height = null;
	public static DataObject depth = null;
	
	public static DataObject qty = null;
	public static DataObject unit = null;
	public static DataObject cost = null;
	public static DataObject price = null;
	public static DataObject discount = null;
	public static DataObject costsubtotal = null;
	public static DataObject sellsubtotal = null;
	
	public static DataObject opt1 = null;
	public static DataObject opt2 = null;
	public static DataObject opt3 = null;
	public static DataObject opt4 = null;
	public static DataObject customisedata = null;
	
	public static DataObject remark = null;
	public static DataObject status = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_cart_detail";
	public static final String TABLE_PREFIX = "mfgtd"; // Same with MFG_TransactionDetailDef
	public static final String MODULE_NAME = "mfg_cart_detail";	
	public static final String DEFAULT_SORT = "prodid";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "cartdetailid";
	public static final String PARENT_KEY = "cartid";
	
	public final static String CONTROL_BAR = "Y";	
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_ADD_CUSTOM;
	
	public static void setAdditional() {		

	}		
}
