package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.GeneralConst;

public class MFG_TransactionDetailDef extends BaseDef{
	public static DataObject transdetailid = null;	
	public static DataObject transid = null;
	public static DataObject custtransdetailid = null;
	public static DataObject grpcode = null;
	public static DataObject prodid = null;
	public static DataObject prodname = null;
	
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
	public static DataObject opt5 = null;
	public static DataObject opt6 = null;
	public static DataObject opt7 = null;
	public static DataObject opt8 = null;
	public static DataObject customisedata = null;
	public static DataObject remark = null;
	
	public static DataObject position = null;
	public static DataObject status = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_transaction_detail";
	public static final String TABLE_PREFIX = "mfgtd";
	public static final String MODULE_NAME = "mfg_transaction_detail";	
	public static final String DEFAULT_SORT = "position, prodid";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "transdetailid";
	public static final String PARENT_KEY = "transid";
	
	public final static String CONTROL_BAR = "Y";	
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_ADD_CUSTOM;
	public final static String TABLE_CLASS_NAME = "sortable";	
	
	public static void setAdditional() {		

	}		
}
