package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.SupplierProfileDef;
import com.forest.common.constants.ComponentConst;

public class MFG_SupplierInvoiceDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_SupplierInvoiceDef.class);
		
	public static DataObject companyid = null;
	public static DataObject invid = null;
	public static DataObject supp_id = null;

	public static DataObject invno = null;
	public static DataObject invdate = null;
	public static DataObject invamt = null;
	public static DataObject paidamt = null;
	
	public static DataObject note = null;
	public static DataObject gst_amt = null;
	public static DataObject sst_amt = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	// Dummy	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_supplier_invoice";
	public static final String MODULE_NAME = "mfg_supplier_invoice";	
	public static final String DEFAULT_SORT = "a.updatedate";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "invid";	
	
	public static void setAdditional() {		
    	status.prefix = "a";    	
    	    	
    	HashMap listMap = new HashMap();
		listMap.put("object", SupplierProfileDef.name);
		listMap.put("as", supp_id.name+"_value");
		listMap.put("prefix", "b");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		_searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", SupplierProfileDef.name);		
		listMap.put("prefix", "b");
		listMap.put("visibility", "show");
		listMap.put("size", "40");
		listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));
		_searchList.add(listMap);
	}		
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 			
			fromBuffer.append (" Left Join ").append(SupplierProfileDef.TABLE).append(" b on b.").append(SupplierProfileDef.id.name);
			fromBuffer.append (" = a.").append(supp_id.name);			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer[1]);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
