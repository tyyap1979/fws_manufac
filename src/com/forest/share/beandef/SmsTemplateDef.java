package com.forest.share.beandef;


import java.util.ArrayList;
import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class SmsTemplateDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (EmailTemplateDef.class);
	
	public static DataObject sid = null;	
	public static DataObject companyid = null;
	public static DataObject code = null;
	public static DataObject content = null;	
	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "sms_template";
	public static final String MODULE_NAME = "sms_template";	
	public static final String DEFAULT_SORT = "code";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "sid";	
	
	//public static final String[] OPTIONS = {};
	
	public static void setAdditional() {				
    	
	}		
}
