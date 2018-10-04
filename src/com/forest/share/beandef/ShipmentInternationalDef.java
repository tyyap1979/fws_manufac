package com.forest.share.beandef;

import java.util.ArrayList;
import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class ShipmentInternationalDef extends BaseDef{
	public static final String TABLE = "shipment_international";
	
	public static DataObject sid = null;
	public static DataObject companyid = null;	
	public static DataObject tocountryiso = null;
	public static DataObject numberofday = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static String KEY = "sid";
	public static String MODULE_NAME = "shipmentinternational";	
	public static String DEFAULT_SORT = "tocountryiso";
	public static String DEFAULT_SORT_DIRECTION = "asc";

	public final static String PERMANENT_DELETE = "true";
	public static final Class[] subTableClass = {ShipmentInternationalDetailDef.class};	
	public static void setAdditional() {		
		
	}
}
