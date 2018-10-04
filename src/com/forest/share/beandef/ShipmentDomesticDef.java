package com.forest.share.beandef;

import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.constants.ComponentConst;


public class ShipmentDomesticDef extends BaseDef{
	public static final String TABLE = "shipment_domestic";
	
	public static DataObject sid = null;
	public static DataObject companyid = null;		
	public static DataObject state = null;	
	public static DataObject numberofday = null;
	
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static ArrayList _autoSuggestSearchDisplayList = new ArrayList();
	public static ArrayList _autoSuggestSearchList = new ArrayList();
	
	public static String KEY = "sid";
	public static String MODULE_NAME = "shipmentdomestic";	
	public static String DEFAULT_SORT = "state";
	public static String DEFAULT_SORT_DIRECTION = "";

	public final static String PERMANENT_DELETE = "true";
	public static final Class[] subTableClass = {ShipmentDomesticDetailDef.class};	
	
	public static void setAdditional() {		
		HashMap listMap = new HashMap();
		
		listMap.put("object", state);
		listMap.put("as", state.name+"_value");		
		listMap.put("shadow", "Y");
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
		_addList.add(listMap);
		_searchDisplayList.add(listMap);
		
		listMap = new HashMap();
    	listMap.put("object", companyid);
    	_autoSuggestSearchList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", state);
    	_autoSuggestSearchList.add(listMap);
    	
    	
    	listMap = new HashMap();
    	listMap.put("object", state);
    	listMap.put("astype", "key");
    	_autoSuggestSearchDisplayList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", state);
    	listMap.put("astype", "value");
    	_autoSuggestSearchDisplayList.add(listMap);    	
	}
	
	public static StringBuffer[] getAutoSuggestSearchScript(ArrayList selectFields)throws Exception{
		StringBuffer[] retBuffer = getSearchScript(selectFields, "From "+TABLE);
		return retBuffer;
	}
}
