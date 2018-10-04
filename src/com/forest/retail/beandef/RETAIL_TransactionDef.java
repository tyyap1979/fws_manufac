package com.forest.retail.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.VoucherDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.SearchConst;

public class RETAIL_TransactionDef extends BaseDef{
	private static Logger logger = Logger.getLogger (RETAIL_TransactionDef.class);	
	public static DataObject companyid = null;
	public static DataObject transid = null; // Primary Key
	public static DataObject transno = null;	// Number, Customize auto increase
	public static DataObject comment = null;
	public static DataObject salesdate = null;
	public static DataObject deliverydate = null;
	
	public static DataObject address = null;
	public static DataObject city = null;
	public static DataObject state = null;
	public static DataObject postcode = null;
	public static DataObject country = null;
	public static DataObject phoneno = null;
	
	public static DataObject totalsales = null;
	public static DataObject totalweight = null;
	public static DataObject discount = null;
	
		
	// Web
	public static DataObject clientid = null;
	public static DataObject paymentmethod = null;
	public static DataObject shippingmethod = null;
	public static DataObject shippingcharge = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static int columns = 2;
	
	// Shadow Field
	public static DataObject email = null;
	public static DataObject firstname = null;
	public static DataObject lastname = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "retail_transaction";
	public static final String MODULE_NAME = "retail_transaction";	
	public static final String DEFAULT_SORT = "a.transid";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "transid";
	public static String AUTOGEN_FIELDS = "transno,"; // Use when copying. Ignore field when copying
	public static final Class[] subTableClass = {RETAIL_TransactionDetailDef.class, RETAIL_TransactionVoucherDef.class};
	
//	public static final String[] OPTIONS = {GeneralConst.JOBSHEET, GeneralConst.REPORT, GeneralConst.REPORT_INV, GeneralConst.REPORT_DO};	
	
	public static void setAdditional() {		
		try{
			email = (DataObject) RETAIL_MemberDef.email.clone();
			firstname = (DataObject) RETAIL_MemberDef.firstname.clone();	
			lastname = (DataObject) RETAIL_MemberDef.lastname.clone();	
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}

	}	

	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(RETAIL_MemberDef.TABLE).append(" b on b.").append(RETAIL_MemberDef.mid.name);
			fromBuffer.append (" = a.").append(clientid.name);
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer[1]);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}	
}
