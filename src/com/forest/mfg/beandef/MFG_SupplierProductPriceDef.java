package com.forest.mfg.beandef;

import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.SearchConst;

public class MFG_SupplierProductPriceDef extends BaseDef{
	public static final String TABLE = "mfg_supplierproductprice";

	public static DataObject priceid = null;		//PRI	
	public static DataObject prodid = null;
	
	public static DataObject orderfrom = null;
	public static DataObject orderto = null;
	public static DataObject cost = null;
	public static DataObject dealerprice = null;
	public static DataObject dealer1price = null;
	public static DataObject dealer2price = null;
	public static DataObject clientprice = null;
	public static DataObject client1price = null;
	public static DataObject client2price = null;
	public static DataObject publicprice = null;
	public static DataObject public1price = null;
	public static DataObject public2price = null;
		
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "priceid";
	public static String MODULE_NAME = "mfg_supplierproductprice";	
	public static String DEFAULT_SORT = "orderfrom";
	public static String DEFAULT_SORT_DIRECTION = "asc";
	public static final String PARENT_KEY = "prodid";
	
	public static final String TABLE_PREFIX = "priceDetail";
	
	public final static String PERMANENT_DELETE = "true";
	public final static String CONTROL_BAR = "N";
	
	public static void setAdditional() {						
		priceid.searchOption = SearchConst.EQUAL;
		prodid.searchOption = SearchConst.EQUAL;
	}
	
}
