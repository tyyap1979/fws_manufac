package com.forest.mfg.beandef;


import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class MFG_SupplierProductOptionDef extends BaseDef{
	private static Logger logger = Logger.getLogger (MFG_SupplierProductOptionDef.class);	
	public static DataObject prodoptid = null;
//	public static DataObject companyid = null;		
	public static DataObject suppcompanyid = null;	
		
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	// Shadow Field
	public static DataObject groupname = null;	
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "mfg_supplierproductopt";
	public static final String MODULE_NAME = "mfg_supplierproductopt";	
	public static final String DEFAULT_SORT = "b.groupname";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "prodoptid";
	public static final Class[] subTableClass = {MFG_SupplierProductOptionDetailDef.class};
	
//	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
//	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public static void setAdditional() {
		prodoptid.prefix = "a";
		status.prefix = "a";		
		groupname = MFG_ProductOptionDef.groupname;
		
	}	
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" b on b.").append(MFG_ProductOptionDef.prodoptid.name);
			fromBuffer.append (" = a.").append(prodoptid.name);
			fromBuffer.append (" And b.").append(MFG_ProductOptionDef.companyid.name).append (" = a.").append(suppcompanyid.name);
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
			logger.debug("retBuffer = "+retBuffer[0]);			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return retBuffer;
	}
}
