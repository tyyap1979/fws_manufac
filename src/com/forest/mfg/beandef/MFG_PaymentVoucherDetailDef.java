package com.forest.mfg.beandef;


import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class MFG_PaymentVoucherDetailDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_PaymentVoucherDetailDef.class);
	
	public static DataObject pvid = null;
	public static DataObject pvdid = null;		
	public static DataObject invid = null;	
	public static DataObject payamount = null;		
	public static DataObject status = null;	
	
	public static DataObject invno = null;
	public static DataObject invamt = null;
	public static DataObject invdate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_payment_voucher_detail";
	public static final String MODULE_NAME = "mfg_payment_voucher_detail";	
	public static final String DEFAULT_SORT = "invno";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "pvdid";	
	public static final String PARENT_KEY = "pvid";
	public static final String TABLE_PREFIX = "mfgPaymentVoucherDtl";
	public final static String CONTROL_BAR = "N";	
	public static void setAdditional() {
		invno = MFG_SupplierInvoiceDef.invno;
		invamt = MFG_SupplierInvoiceDef.invamt;
		invdate = MFG_SupplierInvoiceDef.invdate;
	}			
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();			
		try{			
			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 			
			fromBuffer.append (" Left Join ").append(MFG_SupplierInvoiceDef.TABLE).append(" b on b.").append(MFG_SupplierInvoiceDef.invid.name);
			fromBuffer.append (" = a.").append(invid.name);			
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
				
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}

