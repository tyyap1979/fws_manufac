package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.AdminUserBeanDef;
import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.builder.BaseSelectBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_Grouping;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_RawDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDetailDef;
import com.forest.share.beandef.SmsTemplateDef;


public class MFG_SelectBuilder extends BaseSelectBuilder{
	private static Logger logger = Logger.getLogger(MFG_SelectBuilder.class);
	
	// Transaction
	public final static String TRANS_CANCEL = "0"; 
	public final static String TRANS_QUOTATION = "1"; 
	public final static String TRANS_ORDER_CONFIRM = "2";	
	public final static String TRANS_INVOICE = "3";
	public final static String TRANS_DELIVER = "4";
	public final static String TRANS_RECEIVED = "5";

	public final static String PERFORM_BY_ROW = "R";
	public final static String PERFORM_BY_COL = "C";
	public final static String PERFORM_BY_SPAN = "S";
	
	public final static String PAY_STATUS_OPEN = "0";
//	public final static String PAY_STATUS_PARTIAL = "1";
	public final static String PAY_STATUS_PAID = "2";
	
	public final static String GROUP_TYPE_MATERIAL = "1";
	public final static String GROUP_TYPE_PRODUCT = "2";
	
	private static Map _HASH_STATEMENT_STATUS = new LinkedHashMap();
	public static Map _HASH_SUBSCRIBE_STATUS = new LinkedHashMap();
	private static Map _HASH_TRANS_STATUS = new LinkedHashMap();
	private static Map _HASH_SELL_UNIT = new LinkedHashMap();
	private static Map _HASH_MEASUREMENT = new LinkedHashMap();
	private static Map _HASH_TERM = new LinkedHashMap();
	private static Map _HASH_PERFORM_BY = new LinkedHashMap();
	private static Map _HASH_PAY_STATUS = new LinkedHashMap();
	
	private static Map _HASH_GROUP_TYPE = new LinkedHashMap();
	
	private static StringBuffer _STATEMENT_STATUS_SELECT_BUFFER = null;	
	private static StringBuffer _TRANS_STATUS_SELECT_BUFFER = null;	
	private static StringBuffer _SELL_UNIT_SELECT_BUFFER = null;
	private static StringBuffer _MEASUREMENT_SELECT_BUFFER = null;	
	private static StringBuffer _TERM_SELECT_BUFFER = null;
	private static StringBuffer _PERFORM_BY_SELECT_BUFFER = null;
	private static StringBuffer _SUBSCRIBE_STATUS_SELECT_BUFFER = null;
	private static StringBuffer _PAY_STATUS_SELECT_BUFFER = null;
	private static StringBuffer _GROUP_TYPE_SELECT_BUFFER = null;
	
	private static final String CODE_PRODUCT_SELECTION = "mat";
	private static final String CODE_CUST_PRODUCT = "cp";
	private static final String CODE_CUST_PRODUCT_OPTION = "cpo";
	private static final String CODE_RAW_MATERIAL = "crm";
	private static final String CODE_GROUPING = "gr";
	private static final String CODE_PRODUCT_GROUPING = "pgr";
	private static final String CODE_SUPPLIER_PRODUCT_SELECTION = "spo";
	private static final String CODE_AGENT_PROFILE = "ap";
	
	public static final String ACTIVE 		= "A";
	public static final String SUSPENDED 	= "S";
	public static final String BAD_DEBT 	= "B";
	
	static{
		_HASH_STATEMENT_STATUS.put(ACTIVE, "Active");
		_HASH_STATEMENT_STATUS.put(SUSPENDED, "Suspended");
		_HASH_STATEMENT_STATUS.put(BAD_DEBT, "Bad Debt");		
		
		_HASH_PERFORM_BY.put("", "");
		_HASH_PERFORM_BY.put(PERFORM_BY_ROW, "By Row");
		_HASH_PERFORM_BY.put(PERFORM_BY_COL, "By Col");
		_HASH_PERFORM_BY.put(PERFORM_BY_SPAN, "By Span");
		
		_HASH_TRANS_STATUS.put("", "");
		_HASH_TRANS_STATUS.put(TRANS_QUOTATION, "Quotation");
		_HASH_TRANS_STATUS.put(TRANS_ORDER_CONFIRM, "Order Confirmation");
		_HASH_TRANS_STATUS.put(TRANS_INVOICE, "Invoice");
		_HASH_TRANS_STATUS.put(TRANS_DELIVER, "Delivery Order");
		_HASH_TRANS_STATUS.put(TRANS_CANCEL, "Cancel Order");		
		
		_HASH_SELL_UNIT.put("u", "Unit");
		_HASH_SELL_UNIT.put("mm", "Milimeter");
		_HASH_SELL_UNIT.put("cm", "Centimeter");
	    _HASH_SELL_UNIT.put("m", "Meter");
		_HASH_SELL_UNIT.put("in", "Inch");
		_HASH_SELL_UNIT.put("ft", "Feet");
		_HASH_SELL_UNIT.put("sf", "SqFt");
		
		_HASH_MEASUREMENT.put("mm", "MM");
		_HASH_MEASUREMENT.put("in", "Inch");		
		
		_HASH_TERM.put("", "");
		_HASH_TERM.put("0", "Cash");
		_HASH_TERM.put("30", "30 Days");
		_HASH_TERM.put("45", "45 Days");
		_HASH_TERM.put("60", "60 Days");
		_HASH_TERM.put("90", "90 Days");
				
		_HASH_SUBSCRIBE_STATUS.put("0", "New");
		_HASH_SUBSCRIBE_STATUS.put("1", "Subscribe");
		
		_HASH_PAY_STATUS.put(PAY_STATUS_OPEN, "Open");
//		_HASH_PAY_STATUS.put(PAY_STATUS_PARTIAL, "Partial Paid");
		_HASH_PAY_STATUS.put(PAY_STATUS_PAID, "Paid");
		
		_HASH_GROUP_TYPE.put("", "");
		_HASH_GROUP_TYPE.put(GROUP_TYPE_MATERIAL, "Material");
		_HASH_GROUP_TYPE.put(GROUP_TYPE_PRODUCT, "Product");
	}
	
	public static void clean(){
		logger.debug("com.forest.mfg.adminbuilder.SelectBuilder clean");
		hashCacheList.clear();
		selectCacheList.clear();
	}
	
	public static void removeCache(String shopName, Class defClass){
		boolean remove = false;
		String code = null;		
		if(defClass==MFG_CustProductDef.class){
			remove = true;
			code = CODE_CUST_PRODUCT;			
		}else if(defClass==MFG_SupplierProductDef.class){
			remove = true;
			code = CODE_CUST_PRODUCT;			
		}else if (defClass==MFG_ProductOptionDef.class || defClass==MFG_SupplierProductOptionDef.class){
			remove = true;
			code = CODE_PRODUCT_SELECTION;
			// Additional Remove
			ArrayList list = new ArrayList();
			String key = null;
			Iterator it = selectCacheList.keySet().iterator();
			while(it.hasNext()){
				key = (String)it.next();
				if(key.indexOf(shopName+"-"+CODE_CUST_PRODUCT_OPTION)!=-1){
					list.add(key);
				}
			}
			if(list.size()>0){
				for(int i=0; i<list.size(); i++){
					logger.debug("Remove key = "+(String)list.get(i));
					selectCacheList.remove((String)list.get(i));
				}
			}
		}else if (defClass==MFG_RawDef.class){
			remove = true;
			code = CODE_RAW_MATERIAL;
		}else if (defClass==AgentProfileDef.class){
			remove = true;
			code = CODE_AGENT_PROFILE;
		}else if (defClass==SmsTemplateDef.class){
			remove = true;
			code = "SMS_TEMPLATE";
		}else if (defClass==MFG_Grouping.class){
			remove = false;			
			hashCacheList.remove(shopName+"-"+CODE_GROUPING);
			selectCacheList.remove(shopName+"-"+CODE_GROUPING);	
			hashCacheList.remove(shopName+"-"+CODE_PRODUCT_GROUPING);
			selectCacheList.remove(shopName+"-"+CODE_PRODUCT_GROUPING);	
			
			hashCacheList.remove(shopName+"-"+CODE_CUST_PRODUCT);
			selectCacheList.remove(shopName+"-"+CODE_CUST_PRODUCT);			
		}
		if(remove){
			logger.debug("defClass.getName() = "+shopName+"-"+code);
			hashCacheList.remove(shopName+"-"+code);
			selectCacheList.remove(shopName+"-"+code);					
		}
	}
	
	public static Map getHASH_STATEMENT_STATUS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_STATEMENT_STATUS;
	}
	
	public static StringBuffer getSELECT_STATEMENT_STATUS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_STATEMENT_STATUS, _STATEMENT_STATUS_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_GROUP_TYPE(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_GROUP_TYPE;
	}
	
	public static StringBuffer getSELECT_GROUP_TYPE(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_GROUP_TYPE, _GROUP_TYPE_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_PAY_STATUS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_PAY_STATUS;
	}
	
	public static StringBuffer getSELECT_PAY_STATUS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_PAY_STATUS, _PAY_STATUS_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_SUBSCRIBE_STATUS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_SUBSCRIBE_STATUS;
	}
	
	public static StringBuffer getSELECT_SUBSCRIBE_STATUS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_SUBSCRIBE_STATUS, _SUBSCRIBE_STATUS_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_PERFORM_BY(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_PERFORM_BY;
	}
	
	public static StringBuffer getSELECT_PERFORM_BY(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_PERFORM_BY, _PERFORM_BY_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_TERM(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_TERM;
	}
	
	public static StringBuffer getSELECT_TERM(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_TERM, _TERM_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_MEASUREMENT(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_MEASUREMENT;
	}
	
	public static StringBuffer getSELECT_MEASUREMENT(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_MEASUREMENT, _MEASUREMENT_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_SELL_UNIT(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_SELL_UNIT;
	}
	
	public static StringBuffer getSELECT_SELL_UNIT(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_SELL_UNIT, _SELL_UNIT_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static Map getHASH_TRANS_STATUS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_TRANS_STATUS;
	}
	
	public static StringBuffer getSELECT_TRANS_STATUS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_TRANS_STATUS, _TRANS_STATUS_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static HashMap getHASH_PRODUCT_SELECTION(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_PRODUCT_SELECTION)==null){
			getSELECT_PRODUCT_SELECTION(conn, shopInfoBean, null, null);
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_PRODUCT_SELECTION);
	}
	
	public static StringBuffer getSELECT_PRODUCT_SELECTION(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		String firstOptionValue = "Option";
		try{
			query.append("Select ").append(MFG_ProductOptionDef.prodoptid.name).append(",").append(MFG_ProductOptionDef.groupname.name);
			query.append(" From ").append(MFG_ProductOptionDef.TABLE);
			query.append(" Where ").append(MFG_ProductOptionDef.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");
			query.append(" And ").append(MFG_ProductOptionDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			if("lsm".equals(shopInfoBean.getShopName())){
				query.append(" Union ");
				query.append("Select a.").append(MFG_SupplierProductOptionDef.prodoptid.name).append(",b.").append(MFG_ProductOptionDef.groupname.name);
				query.append(" From ").append(MFG_SupplierProductOptionDef.TABLE).append(" a");
				query.append(" Inner Join ").append(MFG_ProductOptionDef.TABLE).append(" b On");
				query.append(" b.").append(MFG_ProductOptionDef.prodoptid).append("=a.").append(MFG_SupplierProductOptionDef.prodoptid);
				query.append(" Where a.").append(MFG_SupplierProductOptionDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			}
			query.append(" Order By ").append(MFG_ProductOptionDef.groupname.name);
			
			logger.debug("getSELECT_PRODUCT_SELECTION query = "+query);
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_PRODUCT_SELECTION, query, fieldName, defaultvalue, 
					MFG_ProductOptionDef.prodoptid.name, MFG_ProductOptionDef.groupname.name,firstOptionValue);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}
	
	public static HashMap getHASH_SUPPLIER_PRODUCT_SELECTION(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_PRODUCT_SELECTION)==null){
			getSELECT_SUPPLIER_PRODUCT_SELECTION(conn, shopInfoBean, null, null);
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_SUPPLIER_PRODUCT_SELECTION);
	}
	
	public static StringBuffer getSELECT_SUPPLIER_PRODUCT_SELECTION(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		String firstOptionValue = "Option";
		try{
			query.append("Select a.").append(MFG_SupplierProductOptionDef.prodoptid).append(",b.").append(MFG_SupplierProductOptionDef.groupname);
			query.append(" From ").append(MFG_SupplierProductOptionDef.TABLE).append(" a");
			query.append(" Inner Join ").append(MFG_ProductOptionDef.TABLE).append(" b On ");
			query.append(" b.").append(MFG_ProductOptionDef.prodoptid).append("=a.").append(MFG_SupplierProductOptionDef.prodoptid);
			query.append(" Where a.").append(MFG_SupplierProductOptionDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" Order By b.").append(MFG_ProductOptionDef.groupname.name);
			logger.debug("getSELECT_SUPPLIER_PRODUCT_SELECTION query = "+query);
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_SUPPLIER_PRODUCT_SELECTION, query, fieldName, defaultvalue, 
					MFG_SupplierProductOptionDef.prodoptid.name, MFG_ProductOptionDef.groupname.name,firstOptionValue);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}
	public static HashMap getHASH_RAW(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_RAW_MATERIAL)==null){
			getSELECT_RAW(conn, shopInfoBean, null, null);
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_RAW_MATERIAL);
	}
	
	public static StringBuffer getSELECT_RAW(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		String firstOptionValue = "Raw Material";
		try{
			query.append("Select ").append(MFG_RawDef.rawid.name).append(",");
			query.append(MFG_RawDef.name.name);
			query.append(" From ").append(MFG_RawDef.TABLE);
			query.append(" Where ").append(MFG_RawDef.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");
			query.append(" And ").append(MFG_RawDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" Order By ").append(MFG_RawDef.name.name);
			
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_RAW_MATERIAL, query, fieldName, defaultvalue, 
					MFG_RawDef.rawid.name, MFG_RawDef.name.name, firstOptionValue);
			
			
			// For Customize Door
			StringBuffer additionalBuffer = new StringBuffer();
			for(int i=1; i<=3; i++){
				additionalBuffer.append("<option value='opt").append(i).append("'>Option ").append(i).append("</option>");
			}
			rtnBuffer.insert(rtnBuffer.indexOf("</select>"),additionalBuffer);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}
	
	public static HashMap getHASH_PRODUCT_OPTION(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_CUST_PRODUCT_OPTION)==null){
			getSELECT_PRODUCT_OPTION(conn, shopInfoBean, null, null, null);
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_CUST_PRODUCT_OPTION+"-null");
	}
	
	public static StringBuffer getSELECT_PRODUCT_OPTION(Connection conn, ShopInfoBean shopInfoBean, 
			String fieldName, String defaultvalue, String prodoptid){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();		
		String firstOptionValue = null;
		try{
			if(prodoptid!=null){
//				HashMap a = (HashMap) getHASH_PRODUCT_SELECTION(conn, shopInfoBean);	
//				logger.debug("------prodoptid = "+prodoptid+"----------- a = "+a);
				firstOptionValue = (String) ((HashMap) getHASH_PRODUCT_SELECTION(conn, shopInfoBean).get(prodoptid)).get(prodoptid);
			}
			query.append("Select b.").append(MFG_ProductOptionDetailDef.prodoptdetailid.name).append(",");
			query.append("a.").append(MFG_ProductOptionDef.groupname.name).append(",");
			query.append("b.").append(MFG_ProductOptionDetailDef.code.name).append(",");
			query.append("b.").append(MFG_ProductOptionDetailDef.name.name).append(",");
			query.append("b.").append(MFG_ProductOptionDetailDef.position);
			query.append(" From ").append(MFG_ProductOptionDef.TABLE).append(" a");
			query.append(" Inner Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" b On");
			query.append(" a.").append(MFG_ProductOptionDef.prodoptid).append("=b.").append(MFG_ProductOptionDetailDef.prodoptid);
			query.append(" Where a.").append(MFG_ProductOptionDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" And b.").append(MFG_ProductOptionDetailDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
						
			if(shopInfoBean.getShopName()!=null){
				query.append(" And a.").append(MFG_ProductOptionDetailDef.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");
			}
			
			if(prodoptid!=null){
				query.append(" And a.").append(MFG_ProductOptionDetailDef.prodoptid.name).append("=").append(prodoptid);
			}
			
			query.append(" Union ");
			query.append(" Select b.").append(MFG_SupplierProductOptionDetailDef.prodoptdetailid).append(",");
			query.append(" c.").append(MFG_SupplierProductOptionDef.groupname).append(",");
			query.append(" d.").append(MFG_ProductOptionDetailDef.code).append(",");
			query.append(" d.").append(MFG_ProductOptionDetailDef.name).append(",");
			query.append(" d.").append(MFG_ProductOptionDetailDef.position);
			query.append(" From ").append(MFG_SupplierProductOptionDef.TABLE).append(" a");			
			query.append(" Inner Join ").append(MFG_SupplierProductOptionDetailDef.TABLE).append(" b On");
			query.append(" a.").append(MFG_SupplierProductOptionDef.prodoptid).append("=b.").append(MFG_SupplierProductOptionDetailDef.prodoptid);
			query.append(" Inner Join ").append(MFG_ProductOptionDef.TABLE).append(" c On ");
			query.append(" c.").append(MFG_ProductOptionDef.companyid).append("=a.").append(MFG_SupplierProductOptionDef.suppcompanyid);
			query.append(" And c.").append(MFG_ProductOptionDef.prodoptid).append("=a.").append(MFG_SupplierProductOptionDef.prodoptid);
			query.append(" Inner Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" d On");
			query.append(" d.").append(MFG_ProductOptionDetailDef.prodoptid).append("=b.").append(MFG_SupplierProductOptionDetailDef.prodoptid);
			query.append(" And d.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append("=b.").append(MFG_SupplierProductOptionDetailDef.prodoptdetailid);
			
			query.append(" Where a.").append(MFG_ProductOptionDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" And b.").append(MFG_ProductOptionDetailDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
						
//			if(shopInfoBean.getShopName()!=null){
//				query.append(" And a.").append(MFG_SupplierProductOptionDetailDef.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");
//			}
			
			if(prodoptid!=null){
				query.append(" And a.").append(MFG_SupplierProductOptionDetailDef.prodoptid.name).append("=").append(prodoptid);
			}
			
			
			query.append(" Order By ").append(MFG_ProductOptionDetailDef.position.name);
			
			logger.debug("prodoptid = "+prodoptid+", getSELECT_PRODUCT_OPTION query = "+query);
			
			
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_CUST_PRODUCT_OPTION+"-"+prodoptid, query, fieldName, defaultvalue, 
					MFG_ProductOptionDetailDef.prodoptdetailid.name, MFG_ProductOptionDetailDef.name.name, firstOptionValue);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}	
	
	public static HashMap getHASH_CUST_PRODUCT(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_CUST_PRODUCT)==null){
			getSELECT_CUST_PRODUCT(conn, shopInfoBean, null, null);
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_CUST_PRODUCT);
	}
	
	public static StringBuffer getSELECT_CUST_PRODUCT(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		String firstOptionValue = "Product";
		try{
			query.append(" Select 0 As prodid, '[Custom Product]' As name, 'Others' as groupname From Dual");
			
			query.append(" Union ");
			
			query.append(" Select a.").append(MFG_CustProductDef.prodid.name).append(",a.").append(MFG_CustProductDef.name.name);
			query.append(" ,COALESCE(b.").append(MFG_Grouping.groupname).append(",'Others') As ").append(MFG_Grouping.groupname);
			query.append(" From ").append(MFG_CustProductDef.TABLE).append(" a");
			query.append(" Left Join ").append(MFG_Grouping.TABLE).append(" b On b.").append(MFG_Grouping.groupid).append("=a.").append(MFG_CustProductDef.grouptype);
			query.append(" Where a.").append(MFG_CustProductDef.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");
			query.append(" And a.").append(MFG_CustProductDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			
			if("lsm".equals(shopInfoBean.getShopName())){
				query.append(" Union ");
				
				query.append(" Select a.").append(MFG_SupplierProductDef.prodid.name).append(",b.").append(MFG_CustProductDef.name.name);
				query.append(" ,COALESCE(c.").append(MFG_Grouping.groupname).append(",'Others') As ").append(MFG_Grouping.groupname);
				query.append(" From ").append(MFG_SupplierProductDef.TABLE).append(" a");
				query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b On ");
				query.append(" b.").append(MFG_CustProductDef.prodid).append("=a.").append(MFG_SupplierProductDef.prodid);
				query.append(" Left Join ").append(MFG_Grouping.TABLE).append(" c On c.").append(MFG_Grouping.groupid).append("=b.").append(MFG_CustProductDef.grouptype);
				
				query.append(" Where a.").append(MFG_SupplierProductDef.suppcompanyid.name).append("=b.").append(MFG_CustProductDef.companyid);
				query.append(" And a.").append(MFG_CustProductDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");				
			}
			query.append(" Order By ").append(MFG_Grouping.groupname).append(",").append(MFG_CustProductDef.name.name);
			logger.debug("getSELECT_CUST_PRODUCT["+shopInfoBean.getShopName()+"] query = "+query);
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_CUST_PRODUCT, query, fieldName, defaultvalue, 
					MFG_CustProductDef.prodid.name, MFG_CustProductDef.name.name, firstOptionValue, MFG_Grouping.groupname.name);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
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
			query.append(" And ").append(MFG_Grouping.grouptype.name).append("='").append(GROUP_TYPE_MATERIAL).append("'");
			query.append(" Order By ").append(MFG_Grouping.groupname.name);
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_GROUPING, query, fieldName, defaultvalue, 
					MFG_Grouping.groupid.name, MFG_Grouping.groupname.name,firstOptionValue);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}
	
	public static HashMap getHASH_PRODUCT_GROUPING(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_PRODUCT_GROUPING)==null){
			getSELECT_PRODUCT_GROUPING(conn, shopInfoBean, null, null);
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_PRODUCT_GROUPING);
	}
	
	public static StringBuffer getSELECT_PRODUCT_GROUPING(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		String firstOptionValue = "Option";
		try{
			query.append("Select ").append(MFG_Grouping.groupid.name).append(",").append(MFG_Grouping.groupname.name);
			query.append(" From ").append(MFG_Grouping.TABLE);
			query.append(" Where ").append(MFG_Grouping.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");
			query.append(" And ").append(MFG_Grouping.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" And ").append(MFG_Grouping.grouptype.name).append("='").append(GROUP_TYPE_PRODUCT).append("'");
			
			query.append(" Order By ").append(MFG_Grouping.groupname.name);
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_PRODUCT_GROUPING, query, fieldName, defaultvalue, 
					MFG_Grouping.groupid.name, MFG_Grouping.groupname.name,firstOptionValue);
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}
	
	public static HashMap getHASH_AGENT_PROFILE(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_AGENT_PROFILE)==null){
			getSELECT_AGENT_PROFILE(conn, shopInfoBean, null, "0");
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_AGENT_PROFILE);
	}
	
	public static StringBuffer getSELECT_AGENT_PROFILE(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		String firstOptionValue = "Representative";
		try{			
			query.append("Select ").append(AgentProfileDef.id.name).append(",");
			query.append(AgentProfileDef.name.name);
			query.append(" From ").append(AgentProfileDef.TABLE);
			query.append(" Where ").append(AgentProfileDef.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");			
			query.append(" And ").append(AgentProfileDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" Order By ").append(AgentProfileDef.name.name);
			logger.debug("getSELECT_AGENT_PROFILE query = "+query);
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_AGENT_PROFILE, query, fieldName, defaultvalue, 
					AgentProfileDef.id.name, AgentProfileDef.name.name, firstOptionValue);
			
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}	
}
