package com.forest.id.adminbuilder;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.BaseSelectBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.mfg.beandef.MFG_Grouping;


public class ID_SelectBuilder extends BaseSelectBuilder{
	private static Logger logger = Logger.getLogger(ID_SelectBuilder.class);
	
	// Transaction
	public final static String TRANS_QUOTATION = "1"; 
	public final static String TRANS_ORDER_CONFIRM = "2";	
	public final static String TRANS_INVOICE = "3";
	public final static String TRANS_DELIVER = "4";
	public final static String TRANS_RECEIVED = "5";
	
	private static Map _HASH_TRANS_STATUS = new LinkedHashMap();
	private static Map _HASH_UOM = new LinkedHashMap();
	private static Map _HASH_TERM = new LinkedHashMap();
	
	
	private static StringBuffer _TRANS_STATUS_SELECT_BUFFER = null;	
	private static StringBuffer _UOM_SELECT_BUFFER = null;
	private static StringBuffer _TERM_SELECT_BUFFER = null;
	
	private static final String CODE_GROUPING = "gr";
	
	static{
		_HASH_TRANS_STATUS.put("", "");
		_HASH_TRANS_STATUS.put(TRANS_QUOTATION, "Quotation");
		_HASH_TRANS_STATUS.put(TRANS_ORDER_CONFIRM, "Order Confirmation");
		_HASH_TRANS_STATUS.put(TRANS_INVOICE, "Invoice");
		_HASH_TRANS_STATUS.put(TRANS_DELIVER, "Delivery Order");
//		_HASH_TRANS_STATUS.put(TRANS_RECEIVED, "Received");		
		
		_HASH_UOM.put("FR", "Unit");
		_HASH_UOM.put("SF", "SqFt");
		_HASH_UOM.put("SET", "Set");
		_HASH_UOM.put("NOS", "Feet");
		_HASH_UOM.put("PCS", "Piece");
		
		_HASH_TERM.put("", "");
		_HASH_TERM.put("0", "Cash");
		_HASH_TERM.put("30", "30 Days");
		_HASH_TERM.put("45", "45 Days");
		_HASH_TERM.put("60", "60 Days");
		_HASH_TERM.put("90", "90 Days");				
	}
	
	public static void clean(){
		logger.debug("com.forest.mfg.adminbuilder.SelectBuilder clean");
		hashCacheList.clear();
		selectCacheList.clear();
	}
	
	public static Map getHASH_TERM(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_TERM;
	}
	
	public static StringBuffer getSELECT_TERM(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_TERM, _TERM_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_UOM(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_UOM;
	}
	
	public static StringBuffer getSELECT_UOM(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_UOM, _UOM_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_TRANS_STATUS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_TRANS_STATUS;
	}
	
	public static StringBuffer getSELECT_TRANS_STATUS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_TRANS_STATUS, _TRANS_STATUS_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static HashMap getHASH_GROUPING(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_GROUPING)==null){
			getSELECT_GROUPING(conn, shopInfoBean, null, null);
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_GROUPING);
	}
	
	public static StringBuffer getSELECT_GROUPING(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		String firstOptionValue = "Option";
		try{
			query.append("Select ").append(MFG_Grouping.groupid.name).append(",").append(MFG_Grouping.groupname.name);
			query.append(" From ").append(MFG_Grouping.TABLE);
			query.append(" Where ").append(MFG_Grouping.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");
			query.append(" And ").append(MFG_Grouping.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" Order By ").append(MFG_Grouping.groupname.name);
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_GROUPING, query, fieldName, defaultvalue, 
					MFG_Grouping.groupid.name, MFG_Grouping.groupname.name,firstOptionValue);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}
	
	public static StringBuffer buildSelect(HashMap map, String name){
		StringBuffer buffer = new StringBuffer();
		String key = null;
		Iterator keyIt = map.keySet().iterator();
		buffer.append("<Select name='").append(name).append("'>");
		while(keyIt.hasNext()){
			key = (String) keyIt.next();
			buffer.append("<option value='").append(key).append("'>");
			buffer.append((String) map.get(key));
			buffer.append("</option>");
		}
		buffer.append("</select>");
				
		return buffer;
	}
	
	
}
