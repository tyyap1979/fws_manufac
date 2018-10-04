package com.forest.mfg.beandef;


import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.ComponentConst;

public class MFG_JobSheetDetailDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_JobSheetDetailDef.class);	
	
	public static DataObject jobdetailid = null;
	public static DataObject jobid = null;
	public static DataObject transdetailid = null;
	public static DataObject prodid = null;
	public static DataObject org_rawid = null;
	public static DataObject rawid = null;
	public static DataObject rtpdtlid = null;
	public static DataObject qty = null;
	public static DataObject width = null;
	public static DataObject height = null;
	public static DataObject remark = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_jobsheet_detail"; // No Table
	public static final String TABLE_PREFIX = "mfgjsd";
	public static final String MODULE_NAME = "mfg_jobsheet_detail";	
	public static final String DEFAULT_SORT = "b.transdetailid, a.rawid";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "jobdetailid";
	public static final String PARENT_KEY = "jobid";
	
	public final static String CONTROL_BAR = "N";	
	
	public static void setAdditional() {				
		HashMap listMap = null;
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.width);
		listMap.put("as", "prod_"+MFG_TransactionDetailDef.width);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.height);
		listMap.put("as", "prod_"+MFG_TransactionDetailDef.height);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.qty);
		listMap.put("as", "prod_"+MFG_TransactionDetailDef.qty);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.opt1);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.opt2);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.opt3);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.opt4);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.opt5);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.opt6);
		listMap.put("prefix", "b");				
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.opt7);
		listMap.put("prefix", "b");		
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", MFG_TransactionDetailDef.opt8);
		listMap.put("prefix", "b");				
		listMap.put("shadow","Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
	}		
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(MFG_TransactionDetailDef.TABLE).append(" b on b.").append(MFG_TransactionDetailDef.transdetailid.name);
			fromBuffer.append (" = a.").append(transdetailid.name);			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
