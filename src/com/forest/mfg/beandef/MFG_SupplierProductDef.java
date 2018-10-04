package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.ComponentConst;

public class MFG_SupplierProductDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_SupplierProductDef.class);	
//	public static DataObject companyid = null;
	public static DataObject prodid = null;	
	public static DataObject suppcompanyid = null;			
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	// Shadow Field
	public static DataObject code = null;
	public static DataObject name = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_supplierproduct";
	public static final String MODULE_NAME = "mfg_supplierproduct";	
	public static final String DEFAULT_SORT = "b.name";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "prodid";
	public static final Class[] subTableClass = {MFG_SupplierProductPriceDef.class, MFG_SupplierProductSelectionDef.class};
	
	public static ArrayList _jsonSearchDisplayList = new ArrayList();
	
	public static void setAdditional() {		
		status.prefix = "a";
		prodid.prefix = "a";
		code = MFG_CustProductDef.code;
		name = MFG_CustProductDef.name;
		
		
		
		HashMap listMap = new HashMap();
//		listMap.put("object", MFG_CustProductDef.name);
//		listMap.put("as", prodid.name+"_value");
//		listMap.put("prefix", "b");
//		listMap.put("shadow", "Y");		
//		listMap.put("type", String.valueOf(ComponentConst.TEXT));
//		_addList.add(listMap);
//		_searchDisplayList.add(listMap);
		
		/* Json  */
		listMap = new HashMap();
    	listMap.put("object", prodid);
    	listMap.put("prefix", "b");
    	_jsonSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", MFG_CustProductDef.sellunittype);
    	listMap.put("prefix", "b");
    	_jsonSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", MFG_CustProductDef.minorder);
    	listMap.put("prefix", "b");
    	_jsonSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", MFG_CustProductDef.customise);
    	listMap.put("prefix", "b");
    	_jsonSearchDisplayList.add(listMap);
	}	
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(MFG_CustProductDef.TABLE).append(" b on b.").append(MFG_CustProductDef.prodid.name);
			fromBuffer.append (" = a.").append(prodid.name);			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}	
}
