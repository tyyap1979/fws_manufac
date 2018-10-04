package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;

public class MFG_JobSheetDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_JobSheetDef.class);
	
	public static DataObject companyid = null;
	public static DataObject jobid = null;
	public static DataObject transid = null;
	public static DataObject jobcode = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_jobsheet";
	public static final String MODULE_NAME = "mfg_jobsheet";	
	public static final String DEFAULT_SORT = "a.updatedate";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "true";
	public final static String CONTROL_BAR = "N";	
	public static String KEY = "jobid";
	public static final Class[] subTableClass = {MFG_JobSheetDetailDef.class};
	public static final String[] OPTIONS = {GeneralConst.REPORT};
	
	public static void setAdditional() {		
		status.prefix = "a";
		HashMap listMap = new HashMap();
		listMap.put("object", MFG_TransactionDef.transno);
		listMap.put("prefix", "b");		
		listMap.put("sortable","true");
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_searchDisplayList.add(listMap);	
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDef.custrefno);
		listMap.put("prefix", "b");		
		listMap.put("sortable","true");
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_searchDisplayList.add(listMap);	
		
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDef.transno);
		listMap.put("prefix", "b");		
		listMap.put("sortable","true");
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDef.customerid);
		listMap.put("prefix", "b");		
		listMap.put("sortable","true");
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", CustomerProfileDef.name);
		listMap.put("as", MFG_TransactionDef.customerid.name+"_value");
		listMap.put("prefix", "c");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDef.comment);		
		listMap.put("prefix", "b");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_addList.add(listMap);	
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDef.custrefno);
		listMap.put("prefix", "b");		
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.TEXT));
		_addList.add(listMap);
	}		
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a");
			fromBuffer.append (" Inner Join ").append(MFG_TransactionDef.TABLE).append(" d");
			fromBuffer.append (" On d.").append(MFG_TransactionDef.transid).append("=a.").append(transid);
			fromBuffer.append (" And d.").append(MFG_TransactionDef.status).append("!='").append(GeneralConst.DELETED).append("'");
			fromBuffer.append (" Left Join ").append(MFG_TransactionDef.TABLE).append(" b on b.").append(MFG_TransactionDef.transid.name);
			fromBuffer.append (" = a.").append(transid.name);			
			fromBuffer.append (" Left Join ").append(CustomerProfileDef.TABLE).append(" c on c.").append(CustomerProfileDef.customerid.name);
			fromBuffer.append (" = b.").append(MFG_TransactionDef.customerid.name);			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
