package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.GeneralConst;

public class MFG_ProductOptionDetailDef extends BaseDef{
	public static DataObject companyid = null;	
	public static DataObject prodoptdetailid = null;	
	public static DataObject prodoptid = null;
	
	public static DataObject code = null;
	public static DataObject name = null;	
	public static DataObject cost = null;
	public static DataObject dealerprice = null;
	public static DataObject clientprice = null;
	public static DataObject publicprice = null;
	public static DataObject sellunittype = null;
	public static DataObject rawid = null;	// Use for S1, S2 at PERFORM_BY_SPAN
	public static DataObject position = null;
	public static DataObject formula = null;
	public static DataObject filename = null;
	public static DataObject status = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_productopt_detail";
	public static final String TABLE_PREFIX = "mfgProdOptDtl";
	public static final String MODULE_NAME = "mfg_productopt_detail";	
	public static final String DEFAULT_SORT = "prodoptid, position";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "prodoptdetailid";
	public static final String PARENT_KEY = "prodoptid";
	
	public final static String CONTROL_BAR = "Y";
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_UPLOAD_ADD;
	public final static String TABLE_CLASS_NAME = "sortable";	
	
	public static void setAdditional() {		

	}	
}
