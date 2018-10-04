package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;

public class MFG_DebitNoteDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_DebitNoteDef.class);
	
	public static DataObject dnid = null;
	public static DataObject dnno = null;
	public static DataObject companyid = null;
	public static DataObject customerid = null;
	public static DataObject stmtdtlid = null;
	public static DataObject transid = null;
	public static DataObject transno = null;	
	public static DataObject description = null;
	public static DataObject custrefno = null;
	public static DataObject debitdate = null;
	public static DataObject debitamount = null;
	public static DataObject debitamtbeforegst = null;
	public static DataObject gst_amt = null;
	public static DataObject gst_code = null;	
		
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_debitnote";
	public static final String MODULE_NAME = "mfg_debitnote";	
	public static final String DEFAULT_SORT = "a.dnno";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "dnid";	
	public static String AUTOGEN_FIELDS = "dnno,"; // Use when copying. Ignore field when copying
		
	public static final String[] OPTIONS = {GeneralConst.REPORT};
	
	public static void setAdditional() {				
    	status.prefix = "a";    	
    	
    	HashMap listMap = new HashMap();
		listMap.put("object", CustomerProfileDef.name);
		listMap.put("as", customerid.name+"_value");
		listMap.put("prefix", "b");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		_searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", CustomerProfileDef.name);		
		listMap.put("prefix", "b");
		listMap.put("visibility", "show");
		listMap.put("size", "40");
		listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));
		_searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", transno);
		listMap.put("as", transno+"_value");
		listMap.put("prefix", "a");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		_searchDisplayList.add(listMap);
	}	
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 			
			fromBuffer.append (" Left Join ").append(CustomerProfileDef.TABLE).append(" b on b.").append(CustomerProfileDef.customerid.name);
			fromBuffer.append (" = a.").append(customerid.name);			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer[1]);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
