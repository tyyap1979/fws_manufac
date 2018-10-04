package com.forest.retail.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.VoucherDef;
import com.forest.common.constants.GeneralConst;

public class RETAIL_TransactionVoucherDef extends BaseDef{	
	public static DataObject transid = null;
	public static DataObject vid = null;
	
	public static DataObject voucher_code = null;
	public static DataObject voucher_type = null;
	public static DataObject voucher_amt = null;
	public static DataObject description = null;
	public static DataObject issue_date = null;
	public static DataObject exp_date = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "retail_transaction_voucher";
	public static final String TABLE_PREFIX = "retailtv";
	public static final String MODULE_NAME = "retail_transaction_voucher";	
	public static final String DEFAULT_SORT = "vid";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "vid";
	public static final String PARENT_KEY = "transid";
	
	public final static String CONTROL_BAR = "Y";	
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_ADD_CUSTOM;
	
	public static void setAdditional() {		
		try{
			voucher_code = (DataObject) VoucherDef.voucher_code.clone();	
			voucher_type = (DataObject) VoucherDef.voucher_type.clone();	
			voucher_amt = (DataObject) VoucherDef.voucher_amt.clone();	
			description = (DataObject) VoucherDef.description.clone();	
			issue_date = (DataObject) VoucherDef.issue_date.clone();	
			exp_date = (DataObject) VoucherDef.exp_date.clone();	
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
	}		
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Inner Join ").append(VoucherDef.TABLE).append(" b on b.").append(VoucherDef.vid.name);
			fromBuffer.append (" = a.").append(vid.name);	
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());			
		}catch(Exception e){			
			throw e;
		}
		
		return retBuffer;
	}	
}
