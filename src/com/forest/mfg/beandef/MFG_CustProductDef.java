package com.forest.mfg.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.SearchConst;

public class MFG_CustProductDef extends BaseDef{
	public static DataObject prodid = null;
	public static DataObject companyid = null;
	
	public static DataObject code = null;	
	public static DataObject name = null;
	public static DataObject grouptype = null;
	public static DataObject description = null;
	public static DataObject sellonweb = null;
		
	public static DataObject sellunittype = null;
	public static DataObject minorder = null;
	public static DataObject customformula = null;
	public static DataObject customise = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static DataObject roundup_prefix = null; // LS = roundUpSqft_LS, null = roundUpSqft 
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static int columns = 2;
	
	public static final String TABLE = "mfg_custproduct";
	public static final String MODULE_NAME = "mfg_custproduct";	
	public static final String DEFAULT_SORT = "b.groupname, a.name";
	public static final String DEFAULT_SORT_DIRECTION = "";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "prodid";
	public static final Class[] subTableClass = {MFG_CustProductPriceDef.class, MFG_CustProductSelectionDef.class, MFG_CustProductImageDef.class};	
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	
	public static ArrayList _jsonSearchDisplayList = new ArrayList();	
	
	public static void setAdditional() {	
		status.prefix = "a";
		companyid.prefix = "a";
		updatedate.prefix = "a";
		code.searchOption = SearchConst.LIKE;
		name.searchOption = SearchConst.LIKE;		
    	
		HashMap listMap = new HashMap();
		listMap.put("object", MFG_Grouping.groupname);		
		listMap.put("prefix", "b");
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_searchList.add(listMap);		
	}	
	
	public static StringBuffer[] getSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = new StringBuffer[2];
		StringBuffer fromBuffer = new StringBuffer();		
		try{			
			fromBuffer.append (" From ").append(TABLE).append(" a"); 
			fromBuffer.append (" Left Join ").append(MFG_Grouping.TABLE).append(" b on b.").append(MFG_Grouping.groupid.name);
			fromBuffer.append (" = a.").append(MFG_CustProductDef.grouptype);
			retBuffer = getSearchScript(selectFields, fromBuffer.toString());
		}catch(Exception e){			
			throw e;
		}
		
		return retBuffer;
	}	
}
