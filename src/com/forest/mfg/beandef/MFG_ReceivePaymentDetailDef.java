package com.forest.mfg.beandef;


import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class MFG_ReceivePaymentDetailDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_ReceivePaymentDetailDef.class);
	
	public static DataObject rpayid = null;
	public static DataObject rpaydtlid = null;		
	public static DataObject stmtdtlid = null;	
	public static DataObject payamount = null;
		
	// Shadow
	public static DataObject transno = null;
	public static DataObject salesdate = null;
	public static DataObject duedate = null;
	public static DataObject amount = null;
	public static DataObject creditamount = null; // MFG_CreditNoteDef
	public static DataObject debitamount = null; // MFG_DebitNoteDef
//	public static DataObject paidamount = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_receivepaymentdetail";
	public static final String MODULE_NAME = "mfg_receivepaymentdetail";	
	public static final String DEFAULT_SORT = "transno";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "rpaydtlid";	
	public static final String PARENT_KEY = "rpayid";
	public static final String TABLE_PREFIX = "mfgReceivePaymentDtl";
	public final static String CONTROL_BAR = "N";	
	public static void setAdditional() {
		transno = MFG_StatementDetailDef.transno;
		salesdate = MFG_StatementDetailDef.salesdate;
		duedate = MFG_StatementDetailDef.duedate;
		amount = MFG_StatementDetailDef.amount;
//		paidamount = MFG_StatementDetailDef.amount;
		creditamount = MFG_CreditNoteDef.creditamount;
		debitamount = MFG_DebitNoteDef.debitamount;
	}			
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();			
		try{			
			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 			
			fromBuffer.append (" Left Join ").append(MFG_StatementDetailDef.TABLE).append(" b on b.").append(MFG_StatementDetailDef.stmtdtlid.name);
			fromBuffer.append (" = a.").append(stmtdtlid.name);			
			
			fromBuffer.append (" Left Join ").append(MFG_CreditNoteDef.TABLE).append(" c on c.").append(MFG_CreditNoteDef.stmtdtlid.name);
			fromBuffer.append (" = a.").append(stmtdtlid.name);	
			
			fromBuffer.append (" Left Join ").append(MFG_DebitNoteDef.TABLE).append(" d on d.").append(MFG_DebitNoteDef.stmtdtlid.name);
			fromBuffer.append (" = a.").append(stmtdtlid.name);			
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
				
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}

