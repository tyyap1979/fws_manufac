package com.forest.share.beandef;

import java.util.ArrayList;
import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;


public class ShipmentDomesticDetailDef extends BaseDef{
	public static DataObject sid = null;
	public static DataObject sdtlid = null;			
	public static DataObject weightbelow = null;
	public static DataObject rate = null;	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	

	public static final String TABLE = "shipment_domestic_detail";
	public static final String TABLE_PREFIX = "shipmentdomesticdetail";
	public static String MODULE_NAME = "shipmentdomesticdetail";	
	public static String DEFAULT_SORT = "weightbelow";
	public static String DEFAULT_SORT_DIRECTION = "";
	public final static String PERMANENT_DELETE = "true";
	public static String KEY = "sdtlid";
	public static final String PARENT_KEY = "sid";
	

	

	public static void setAdditional() {		
	
	}
}
