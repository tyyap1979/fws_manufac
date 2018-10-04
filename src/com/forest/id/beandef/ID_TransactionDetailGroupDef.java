package com.forest.id.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class ID_TransactionDetailGroupDef extends BaseDef{
	public static DataObject groupid = null;
	public static DataObject transid = null;	
	public static DataObject grpcode = null;
	public static DataObject grpdesc = null;		
		
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "id_transaction_detail_group";
	public static final String TABLE_PREFIX = "transtdgrp";
	public static final String MODULE_NAME = "id_transaction_detail_group";	
	public static final String DEFAULT_SORT = "grpcode";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "groupid";
	public static final String PARENT_KEY = "transid";
	
	public final static String CONTROL_BAR = "Y";	
	
	public static void setAdditional() {		

	}		
}
