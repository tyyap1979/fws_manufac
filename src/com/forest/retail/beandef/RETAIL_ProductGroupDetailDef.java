package com.forest.retail.beandef;


import java.util.ArrayList;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.GeneralConst;

public class RETAIL_ProductGroupDetailDef extends BaseDef{
	public static DataObject pdtlid = null;	
	public static DataObject pid = null;
	public static DataObject prodid = null;
	public static DataObject position = null;	
	
	public static DataObject prodcode = null;
	public static DataObject prodname = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "retail_productgroup_detail";
	public static final String TABLE_PREFIX = "retailproductgroupdetail";
	public static final String MODULE_NAME = "retailproductgroupdetail";	
	public static final String DEFAULT_SORT = "position";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "pdtlid";
	public static final String PARENT_KEY = "pid";
	
	public final static String CONTROL_BAR = "Y";		
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_ADD_CUSTOM;
	
	public static void setAdditional() {	
		try{
			prodcode = (DataObject) RETAIL_ProductDef.prodcode.clone();	
			prodname = (DataObject) RETAIL_ProductDef.prodname.clone();	
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
	}		
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(RETAIL_ProductDef.TABLE).append(" b on b.").append(RETAIL_ProductDef.prodid.name);
			fromBuffer.append (" = a.").append(prodid.name);	
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());			
		}catch(Exception e){			
			throw e;
		}
		
		return retBuffer;
	}	
}
