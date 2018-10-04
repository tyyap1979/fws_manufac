package com.forest.mfg.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;

public class MFG_CustProductCustomerPriceDef extends BaseDef{
	private static Logger logger = Logger.getLogger(MFG_CustProductCustomerPriceDef.class);
	public static final String TABLE = "mfg_custproductcustomerprice";

	public static DataObject custpriceid = null;
	public static DataObject companyid = null;
	public static DataObject customerid = null;
	public static DataObject prodid = null;
	public static DataObject priceid = null;
	public static DataObject price = null;
			
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();
	
	public static String KEY = "custpriceid";
	public static String MODULE_NAME = "mfg_custproductcustomerprice";	
	public static String DEFAULT_SORT = "name,orderfrom";
	public static String DEFAULT_SORT_DIRECTION = "asc";
	public static final String PARENT_KEY = "customerid";
	
	public static final String TABLE_PREFIX = "custPriceDetail";
	
	public final static String PERMANENT_DELETE = "true";
	public final static String CONTROL_BAR = "Y";
	public static String CUSTOM_CONTROL = GeneralConst.CONTROL_RETRIEVE_PRODUCT;
	
	public static void setAdditional() {						
		custpriceid.searchOption = SearchConst.EQUAL;				
		
		HashMap listMap = new HashMap();
		listMap.put("object", MFG_CustProductDef.name);
		listMap.put("as", prodid.name+"_value");
		listMap.put("prefix", "b");
		listMap.put("position", "0");
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_CustProductPriceDef.clientprice);
		listMap.put("custom", "COALESCE(d.clientprice,c.clientprice) As clientprice");
		listMap.put("shadow", "Y");				
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_CustProductPriceDef.dealerprice);
		listMap.put("custom", "COALESCE(d.dealerprice,c.dealerprice) As dealerprice");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_CustProductPriceDef.publicprice);
		listMap.put("custom", "COALESCE(d.publicprice,c.publicprice) As publicprice");
		listMap.put("shadow", "Y");			
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_CustProductPriceDef.orderfrom);
		listMap.put("shadow", "Y");
		listMap.put("prefix", "c");						
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_CustProductPriceDef.orderto);
		listMap.put("shadow", "Y");
		listMap.put("prefix", "c");						
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", null);
		listMap.put("shadow", "Y");
		listMap.put("custom", "if(a."+custpriceid+" is null,'N','U') As 'row-status'");						
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
//		listMap = new HashMap();
//		listMap.put("object", MFG_CustProductDef.status);	
//		listMap.put("prefix", "b");
//		listMap.put("defaultvalue", "A");
//		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
//		_searchList.add(listMap);
	}
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{
			fromBuffer.append (" From ").append(TABLE).append(" a");
			fromBuffer.append (" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b on b.").append(MFG_CustProductDef.prodid.name);
			fromBuffer.append (" = a.").append(prodid.name);		
			
			fromBuffer.append (" Inner Join ").append(MFG_CustProductPriceDef.TABLE).append(" c on c.").append(MFG_CustProductPriceDef.prodid.name);
			fromBuffer.append (" = a.").append(prodid.name);
			fromBuffer.append (" And c.").append(MFG_CustProductPriceDef.priceid.name).append(" = a.").append(priceid);
			
			fromBuffer.append (" Left Join ").append(MFG_SupplierProductDef.TABLE).append(" e on e.").append(MFG_SupplierProductDef.prodid.name).append(" = a.").append(prodid);
			
			fromBuffer.append (" Left Join ").append(MFG_SupplierProductPriceDef.TABLE).append(" d on d.").append(MFG_SupplierProductPriceDef.prodid.name);
			fromBuffer.append (" = e.").append(prodid.name);			
			fromBuffer.append (" And d.").append(MFG_SupplierProductPriceDef.priceid.name).append(" = a.").append(priceid);
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer[1]);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
