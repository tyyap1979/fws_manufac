package com.forest.mfg.beandef;


import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;

public class MFG_StatementDetailDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_StatementDetailDef.class);
	
	public static DataObject customerid = null;
	public static DataObject stmtdtlid = null;		
	public static DataObject stmtid = null;
	public static DataObject transid = null;
	public static DataObject transno = null;
	public static DataObject custrefno = null;
	public static DataObject salesdate = null;
	public static DataObject terms = null;
	public static DataObject duedate = null;
	public static DataObject amount = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	// Join 
	public static DataObject payamount = null; // MFG_ReceivePaymentDef
	public static DataObject creditamount = null; // MFG_CreditNoteDef
	public static DataObject debitamount = null; // MFG_DebitNoteDef
	
	// AutoSuggest
	public static DataObject companyid = null;	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_statement_detail";
	public static final String MODULE_NAME = "mfg_statement_detail";	
	public static final String DEFAULT_SORT = "salesdate";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "stmtdtlid";	
	public static final String PARENT_KEY = "customerid";
	public static final String TABLE_PREFIX = "mfgStmtDtl";
		
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public final static String CONTROL_BAR = "N";
	
	public static void setAdditional() {			
		transno.searchOption = SearchConst.LIKE;
		companyid = new DataObject(BaseDef.COMPANYID, Types.VARCHAR, true, 50, 0);		
		companyid.prefix = "b";
		
		customerid = MFG_StatementDef.customerid;
		payamount = MFG_ReceivePaymentDetailDef.payamount;
		creditamount = MFG_CreditNoteDef.creditamount;
		debitamount = MFG_DebitNoteDef.debitamount;
		status.prefix = "a";
		
		HashMap listMap = new HashMap();
		listMap.put("object", MFG_StatementDef.customerid);		
		listMap.put("prefix", "b");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));		
		_searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_StatementDef.companyid);		
		listMap.put("prefix", "b");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));		
		_searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", status);		
		listMap.put("prefix", "a");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));		
		_searchList.add(listMap);
		
		listMap = new HashMap();
    	listMap.put("object", companyid);
    	listMap.put("prefix", "b");
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", transno);
    	listMap.put("prefix", "a");
    	_autoSuggestSearchList.add(listMap);    	
    	
    	listMap = new HashMap();
    	listMap.put("object", transid);
    	listMap.put("prefix", "a");
    	listMap.put("astype", "key");
    	_autoSuggestSearchDisplayList.add(listMap);
    	   	
    	listMap = new HashMap();
    	listMap.put("object", transno);
    	listMap.put("prefix", "a");
    	listMap.put("astype", "value");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", custrefno);
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap); 
    	
    	listMap = new HashMap();
    	listMap.put("object", stmtdtlid);
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap); 
    	
    	listMap = new HashMap();
    	listMap.put("object", customerid);
    	listMap.put("prefix", "b");
    	_autoSuggestSearchDisplayList.add(listMap);    	
    	
    	listMap = new HashMap();
    	listMap.put("object", amount);
    	listMap.put("prefix", "a");
    	_autoSuggestSearchDisplayList.add(listMap);  
    	
    	listMap = new HashMap();
    	listMap.put("object", CustomerProfileDef.name);
		listMap.put("astype", MFG_StatementDef.customerid.name+"_value");
		listMap.put("prefix", "c");		
		_autoSuggestSearchDisplayList.add(listMap); 
	}
	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer fromBuffer = new StringBuffer();
		fromBuffer.append (" From ").append(TABLE).append(" a"); 
		fromBuffer.append (" Left Join ").append(MFG_StatementDef.TABLE).append(" b on b.").append(MFG_StatementDef.stmtid.name);
		fromBuffer.append (" = a.").append(stmtid.name);	
		fromBuffer.append (" Left Join ").append(CustomerProfileDef.TABLE).append(" c on c.").append(CustomerProfileDef.customerid.name);
		fromBuffer.append (" = b.").append(MFG_StatementDef.customerid.name);		
		return getSearchScript(selectFields, fromBuffer.toString());
	}
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 			
			fromBuffer.append (" Left Join ").append(MFG_StatementDef.TABLE).append(" b on b.").append(MFG_StatementDef.stmtid.name);
			fromBuffer.append (" = a.").append(stmtid.name);		
			fromBuffer.append (" Left Join ").append(MFG_ReceivePaymentDetailDef.TABLE).append(" c on c.").append(MFG_ReceivePaymentDetailDef.stmtdtlid.name);
			fromBuffer.append (" = a.").append(stmtdtlid.name);
			fromBuffer.append (" Left Join ").append(MFG_CreditNoteDef.TABLE).append(" d on d.").append(MFG_CreditNoteDef.stmtdtlid.name);
			fromBuffer.append (" = a.").append(stmtdtlid.name);
			fromBuffer.append (" And d.").append(MFG_CreditNoteDef.status.name).append("!='").append(GeneralConst.DELETED).append("'");
			
			fromBuffer.append (" Left Join ").append(MFG_DebitNoteDef.TABLE).append(" e on e.").append(MFG_DebitNoteDef.stmtdtlid.name);
			fromBuffer.append (" = a.").append(stmtdtlid.name);
			fromBuffer.append (" And e.").append(MFG_DebitNoteDef.status.name).append("!='").append(GeneralConst.DELETED).append("'");
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer[1]);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
