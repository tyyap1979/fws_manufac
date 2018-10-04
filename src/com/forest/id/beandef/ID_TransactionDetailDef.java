package com.forest.id.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class ID_TransactionDetailDef extends BaseDef{
	
	public static DataObject transdetailid = null;	
	public static DataObject transid = null;
	public static DataObject grpcode = null;
	public static DataObject prodname = null;
	
	public static DataObject qty = null;	
	public static DataObject unittype = null;
	public static DataObject cost = null;
	public static DataObject price = null;
	public static DataObject discount = null;
	public static DataObject costsubtotal = null;
	public static DataObject sellsubtotal = null;		
	
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "id_transaction_detail";
	public static final String TABLE_PREFIX = "transtd";
	public static final String MODULE_NAME = "id_transaction_detail";	
	public static final String DEFAULT_SORT = "grpcode, prodname";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "transdetailid";
	public static final String PARENT_KEY = "transid";
	
	public final static String CONTROL_BAR = "Y";	
	
	public static void setAdditional() {		

	}		
}
