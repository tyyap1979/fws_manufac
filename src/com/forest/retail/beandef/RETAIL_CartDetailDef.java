package com.forest.retail.beandef;


import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;

public class RETAIL_CartDetailDef extends BaseDef{
	public static DataObject cartdetailid = null;	
	public static DataObject cartid = null;
	public static DataObject prodid = null;
	
	public static DataObject optid = null;
	public static DataObject qty = null;	
	public static DataObject costprice = null;
	public static DataObject sellprice = null;
	public static DataObject discount = null;
	public static DataObject costsubtotal = null;
	public static DataObject sellsubtotal = null;
	public static DataObject remark = null;	
	
	public static DataObject prodcode = null;
	public static DataObject prodname = null;
	public static DataObject opt1 = null;
	public static DataObject opt2 = null;
	public static DataObject opt3 = null;
	public static DataObject opt4 = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "retail_cart_detail";
	public static final String TABLE_PREFIX = "retailcd"; // Same with MFG_TransactionDetailDef
	public static final String MODULE_NAME = "retail_cart_detail";	
	public static final String DEFAULT_SORT = "prodid";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "cartdetailid";
	public static final String PARENT_KEY = "cartid";
	
	public final static String CONTROL_BAR = "Y";	
	public static String CUSTOM_CONTROL = GeneralConst.CLONE_ADD_CUSTOM;
	
	public static void setAdditional() {		
		try{
			prodcode = (DataObject) RETAIL_ProductDef.prodcode.clone();	
			prodname = (DataObject) RETAIL_ProductDef.prodname.clone();	
			opt1 = (DataObject) RETAIL_ProductOptionDef.opt1.clone();
			opt2 = (DataObject) RETAIL_ProductOptionDef.opt2.clone();
			opt3 = (DataObject) RETAIL_ProductOptionDef.opt3.clone();
			opt4 = (DataObject) RETAIL_ProductOptionDef.opt4.clone();
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
			fromBuffer.append (" Left Join ").append(RETAIL_ProductOptionDef.TABLE).append(" c on c.").append(RETAIL_ProductOptionDef.optid.name);
			fromBuffer.append (" = a.").append(optid.name);
			
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());			
		}catch(Exception e){			
			throw e;
		}
		
		return retBuffer;
	}	
}
