package com.forest.retail.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class RETAIL_CartDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (retail_cartDef.class);	
	public static DataObject companyid = null;
	public static DataObject cartid = null; // Primary Key
	public static DataObject email = null;		
	public static DataObject shippingcharge = null;
	public static DataObject updatedate = null;			
	
	// Shadow
	public static DataObject firstname = null;
	public static DataObject lastname = null;		
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "retail_cart";
	public static final String MODULE_NAME = "retail_cart";	
	public static final String DEFAULT_SORT = "a.updatedate";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "cartid";
	public static final Class[] subTableClass = {RETAIL_CartDetailDef.class};
		
	public static void setAdditional() {
		try{
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
			fromBuffer.append (" Left Join ").append(RETAIL_MemberDef.TABLE).append(" b on b.").append(RETAIL_MemberDef.email.name);
			fromBuffer.append (" = a.").append(email.name);
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());			
		}catch(Exception e){			
			throw e;
		}
		
		return retBuffer;
	}	
}
