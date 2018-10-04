package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.AdminUserBeanDef;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;

public class MFG_TransactionDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_TransactionDef.class);	
	public static DataObject companyid = null;
	public static DataObject transid = null; // Primary Key
	public static DataObject transno = null;	// Number, Customize auto increase
	public static DataObject jobno = null;
	public static DataObject attn = null; 
	
	public static DataObject customerid = null;
	public static DataObject custtype = null;
	public static DataObject custrefno = null;
	public static DataObject comment = null;
	public static DataObject terms = null;		
	public static DataObject salesby = null;
	public static DataObject salesdate = null;
	public static DataObject deliverydate = null;
	
	public static DataObject discount = null;
	
	public static DataObject gst_code = null;
	public static DataObject gst_amt = null;	
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
	public static DataObject jobcode = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_transaction";
	public static final String MODULE_NAME = "mfg_transaction";	
	public static final String DEFAULT_SORT = "a.transno";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "transid";
	public static String AUTOGEN_FIELDS = "transno,jobcode,"; // Use when copying. Ignore field when copying
	public static final Class[] subTableClass = {MFG_TransactionDetailDef.class, MFG_TransactionDetailGroupDef.class};
	
	public static final String[] OPTIONS = {GeneralConst.JOBSHEET, GeneralConst.REPORT, GeneralConst.REPORT_INV, GeneralConst.REPORT_DO};	
	
	public static void setAdditional() {		
		status.prefix = "a";
		companyid.prefix = "a";
		updatedate.prefix = "a";

		transno.searchOption = SearchConst.LIKE;
		custrefno.searchOption = SearchConst.LIKE;
		jobcode.searchOption = SearchConst.LIKE;
		customerid.searchOption = SearchConst.EQUAL;
		
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
		
//		listMap = new HashMap();
//		listMap.put("object", MFG_TransactionDetailDef.prodname);		
//		listMap.put("prefix", "a1");
//		_searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", CustomerProfileDef.note);		
		listMap.put("prefix", "b");		
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", ClientUserBeanDef.firstname);
		listMap.put("as", clientid.name+"_value");
		listMap.put("prefix", "d");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		_searchDisplayList.add(listMap);

		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.name);
		listMap.put("as", salesby.name+"_value");
		listMap.put("prefix", "c");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		_searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", customerid);
		listMap.put("as", customerid.name+"_ori");
		listMap.put("prefix", "a");		
		listMap.put("systemfield", "S");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
	}	

	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a");
//			fromBuffer.append (" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" a1 on a1.").append(MFG_TransactionDetailDef.transid.name);
//			fromBuffer.append (" = a.").append(transid.name);
			fromBuffer.append (" Inner Join ").append(CustomerProfileDef.TABLE).append(" b on b.").append(CustomerProfileDef.customerid.name);
			fromBuffer.append (" = a.").append(customerid.name);	
			fromBuffer.append (" Left Join ").append(AdminUserBeanDef.TABLE).append(" c on c.").append(AdminUserBeanDef.id.name);
			fromBuffer.append (" = a.").append(salesby.name);
			fromBuffer.append (" Left Join ").append(ClientUserBeanDef.TABLE).append(" d on d.").append(ClientUserBeanDef.cid.name);
			fromBuffer.append (" = a.").append(clientid.name);
			
			fromBuffer.append(addUserRightQuery());
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			
			retBuffer[1].insert(7, "DISTINCT ");
			logger.debug("retBuffer = "+retBuffer[1]);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}	
}
