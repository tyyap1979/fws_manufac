package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;

public class MFG_StatementDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_StatementDef.class);
		
	public static DataObject companyid = null;
	public static DataObject customerid = null;
	public static DataObject stmtid = null;
//	public static DataObject stmtyear = null;
//	public static DataObject stmttotal = null;
//	
	public static DataObject totalamt = null;
	public static DataObject currentamt = null;
	public static DataObject past1dayamt = null;
	public static DataObject past30dayamt = null;
	public static DataObject past60dayamt = null;
	public static DataObject past90dayamt = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	// Dummy	
	public static DataObject name = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_statement";
	public static final String MODULE_NAME = "mfg_statement";	
	public static final String DEFAULT_SORT = "b.name";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "stmtid";	
	
	public static final Class[] subTableClass = {MFG_StatementDetailDef.class};
	public static final String[] OPTIONS = {GeneralConst.REPORT};
	
	public static void setAdditional() {
		name = CustomerProfileDef.name;
    	status.prefix = "a";    	
    	
    	initiate(_searchDisplayList, _searchList, _addList);
    	
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
	}		
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 			
			fromBuffer.append (" Left Join ").append(CustomerProfileDef.TABLE).append(" b on b.").append(CustomerProfileDef.customerid.name);
			fromBuffer.append (" = a.").append(customerid.name);			
			fromBuffer.append(addUserRightQuery());
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer[1]);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
