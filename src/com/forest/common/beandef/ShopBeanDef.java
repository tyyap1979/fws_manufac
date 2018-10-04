package com.forest.common.beandef;

import java.util.ArrayList;

import com.forest.common.bean.DataObject;


public class ShopBeanDef extends BaseDef{
	public static final String TABLE = "shop";
//	private static Logger logger = Logger.getLogger(ShopBeanDef.class);
	public static DataObject shopid = null;
	public static DataObject companyid = null;
	public static DataObject shopshortname = null;
	public static DataObject shoplongname = null;
	public static DataObject shopdomain = null;
	public static DataObject theme = null;	
	public static DataObject admincontext = null;
	public static DataObject seq = null;
	public static DataObject business = null;
	public static DataObject companygroup = null;
	
	
	public static DataObject language = null;
	public static DataObject address = null;
	
	public static DataObject phone = null;
	public static DataObject fax = null;
//	
//	public static DataObject country = null;
	public static DataObject contact_person = null;
	public static DataObject contact_email = null;
	public static DataObject contact_phone = null;
//	public static DataObject contact_person_2 = null;
//	public static DataObject contact_email_2 = null;
//	public static DataObject contact_phone_2 = null;
	public static DataObject default_template = null;
	
	public static DataObject sms_gateway = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static int columns = 2;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "shopid";
	public static String MODULE_NAME = "shop";	
	public static String DEFAULT_SORT = "companyid";
	public static String DEFAULT_SORT_DIRECTION = "asc";
//	public static String LAYOUT_PREFIX = "system.admin.shop";
	
	public final static String PERMANENT_DELETE = "false";
	public static final String[] OPTIONS = {};
	public static void setAdditional() {		
			
	}
}
