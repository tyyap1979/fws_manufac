package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.SupplierProfileDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;

public class MFG_PaymentVoucherDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_PaymentVoucherDef.class);
	
	public static DataObject pvid = null;
	public static DataObject pvno = null;
	public static DataObject companyid = null;
	public static DataObject supp_id = null;
	public static DataObject paydate = null;
	public static DataObject payby = null;	
	
	public static DataObject payrefno = null; // Check No, Bank Payment ref no
	public static DataObject payamount = null;
	public static DataObject paynote = null; // Write anything you want
		
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_payment_voucher";
	public static final String MODULE_NAME = "mfg_payment_voucher";	
	public static final String DEFAULT_SORT = "pvid";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "pvid";	
		
	public static final Class[] subTableClass = {MFG_PaymentVoucherDetailDef.class};
	public static final String[] OPTIONS = {GeneralConst.REPORT};
	
	public static void setAdditional() {				    	
    	payrefno.searchOption = SearchConst.LIKE;
    	
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
			logger.debug("retBuffer = "+retBuffer);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
