package com.forest.common.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;


public class VoucherDef extends BaseDef{
	public static DataObject companyid = null;
	public static DataObject vid = null;
	public static DataObject cid = null;
	public static DataObject voucher_code = null;
	public static DataObject voucher_type = null; //OPEN:Can use on all condition(3Months), FB-SHARE:Non Discount Items, RM30 Above(1Month)
	public static DataObject voucher_amt = null;
	public static DataObject description = null;
	public static DataObject issue_date = null;
	public static DataObject exp_date = null;
	
	public static DataObject status = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "voucher";
	public static final String TABLE_PREFIX = "voucher";
	public static final String MODULE_NAME = "voucher";	
	public static final String DEFAULT_SORT = "voucher_code";
	public static final String DEFAULT_SORT_DIRECTION = "";
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "vid";
	public static final String PARENT_KEY = "transid";
	
	public final static String CONTROL_BAR = "Y";	
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_ADD_CUSTOM;
	public static String AUTOGEN_FIELDS = "voucher_code,"; 
	
	public static void setAdditional() {		
		status.prefix = "a";
		companyid.prefix = "a";
		
		HashMap listMap = new HashMap();
		listMap = new HashMap();
		listMap.put("object", cid);
		listMap.put("custom", "Concat("+ClientUserBeanDef.firstname+",' ',"+ClientUserBeanDef.lastname+", ' (',"+ClientUserBeanDef.email+", ')') As "+cid.name+"_value");
		listMap.put("as", cid.name+"_value");
		listMap.put("prefix", "b");
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
			fromBuffer.append (" Left Join ").append(ClientUserBeanDef.TABLE).append(" b on b.").append(ClientUserBeanDef.cid.name);
			fromBuffer.append (" = a.").append(cid.name);
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
				
		}catch(Exception e){			
			throw e;
		}
		
		return retBuffer;
	}	
}
