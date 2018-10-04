package com.forest.common.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class WebsiteDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (AutoNumberDef.class);
	
	public static DataObject webid = null;	
	public static DataObject companyid = null;	
	public static DataObject domain = null;
	public static DataObject theme = null;	
		
	public static DataObject html_title = null;
	public static DataObject html_keyword = null;
	public static DataObject html_description = null;
	
	public static DataObject facebook_url = null;
	public static DataObject facebook_id = null;
	
	public static DataObject twitter_url = null;
	public static DataObject youtube_url = null;
	
	public static DataObject email_name = null;
	public static DataObject email = null;
	public static DataObject email_passwd = null;
	
	public static DataObject countryiso = null;
	public static DataObject paycurrencyiso = null;
	public static DataObject latlng = null;
	
	public static DataObject status = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "website";
	public static final String MODULE_NAME = "website";	
	public static final String DEFAULT_SORT = "domain";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "webid";	
	
	
	public static void setAdditional() {		

	}	
}
