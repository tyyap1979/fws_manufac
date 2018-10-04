package com.forest.mfg.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class SMSDef extends BaseDef{
	public static DataObject companyid = null;
	public static DataObject smsid = null;
	public static DataObject fromnum = null;
	public static DataObject tonum = null;
	public static DataObject content = null;
	public static DataObject remark = null;
	public static DataObject resp = null;
	public static DataObject status = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "sms";
	public static final String MODULE_NAME = "sms";	
	public static final String DEFAULT_SORT = "smsid";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "smsid";
		
	public static void setAdditional() {				
		
	}	
}
