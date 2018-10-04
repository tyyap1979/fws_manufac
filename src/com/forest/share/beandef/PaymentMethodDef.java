package com.forest.share.beandef;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class PaymentMethodDef extends BaseDef{
	public static final String TABLE = "payment_method";
	
	public static DataObject pid = null;
	public static DataObject companyid = null;
	public static DataObject code = null;
	public static DataObject instruction = null;
	public static DataObject parameters = null;
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static String KEY = "pid";
	public static String MODULE_NAME = "paymentmethod";	
	public static String DEFAULT_SORT = "code";
	public static String DEFAULT_SORT_DIRECTION = "asc";

	public final static String PERMANENT_DELETE = "false";
	
	public static void setAdditional() {		
		
	}
}
