package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.ComponentConst;

public class MFG_SupplierProductOptionDetailDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_SupplierProductOptionDetailDef.class);
		
//	public static DataObject companyid = null;	
	public static DataObject prodoptdetailid = null; // Supplier product option id
	public static DataObject prodoptid = null; // Supplier product opt id
	
	public static DataObject name = null; // = supplier price
	public static DataObject cost = null; // = supplier price
	public static DataObject dealerprice = null;
	public static DataObject dealer1price = null;
	public static DataObject dealer2price = null;
	public static DataObject clientprice = null;
	public static DataObject client1price = null;
	public static DataObject client2price = null;
	public static DataObject publicprice = null;
	public static DataObject public1price = null;
	public static DataObject public2price = null;
	public static DataObject status = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_supplierproductopt_detail";
	public static final String TABLE_PREFIX = "mfgProdOptDtl"; // Must same with MFG_ProductOptionDetailDef
	public static final String MODULE_NAME = "mfg_supplierproductopt_detail";	
	public static final String DEFAULT_SORT = "a.prodoptid, b.position asc";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "prodoptdetailid";
	public static final String PARENT_KEY = "prodoptid";
	
	public final static String CONTROL_BAR = "N";
	
	public static void setAdditional() {	
		status.prefix = "a";	
		
		HashMap listMap = new HashMap();		
		listMap.put("object", MFG_ProductOptionDetailDef.name);		
		listMap.put("prefix", "b");		
		listMap.put("shadow", "Y");	
		listMap.put("type", String.valueOf(ComponentConst.TEXT));				
		_addList.add(listMap);
		
		listMap = new HashMap();		
		listMap.put("object", MFG_ProductOptionDetailDef.sellunittype);		
		listMap.put("prefix", "b");		
		listMap.put("shadow", "Y");	
		listMap.put("type", String.valueOf(ComponentConst.TEXT));				
		_addList.add(listMap);
	}	
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" b on b.").append(MFG_ProductOptionDetailDef.prodoptdetailid.name);
			fromBuffer.append (" = a.").append(prodoptdetailid.name);			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString(), "a.prodoptdetailid");
			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}	
}
