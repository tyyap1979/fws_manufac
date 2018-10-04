package com.forest.retail.adminbuilder;

import java.sql.Connection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.BaseSelectBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.retail.beandef.RETAIL_CategoryDef;


public class RETAIL_SelectBuilder extends BaseSelectBuilder{
	private static Logger logger = Logger.getLogger(RETAIL_SelectBuilder.class);
	
	private static final String CODE_CATEGORY = "cat";
	
	static{
		
	}
	
	public static void clean(){
		logger.debug("com.forest.retail.adminbuilder.RETAIL_SelectBuilder clean");
		hashCacheList.clear();
		selectCacheList.clear();
	}
	
	public static void removeCache(String shopName, Class defClass){
		boolean remove = false;
		String code = null;		
		
		if(defClass==RETAIL_CategoryDef.class){
			code = CODE_CATEGORY;
			remove = true;
		}
		if(remove){
			logger.debug("defClass.getName() = "+shopName+"-"+code);
			hashCacheList.remove(shopName+"-"+code);
			selectCacheList.remove(shopName+"-"+code);					
		}
	}	
	
	public static HashMap getHASH_CATEGORY(Connection conn, ShopInfoBean shopInfoBean){			
		if(hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_CATEGORY)==null){
			getSELECT_CATEGORY(conn, shopInfoBean, null, "0");
		}
		return (HashMap) hashCacheList.get(shopInfoBean.getShopName()+"-"+CODE_CATEGORY);
	}
	
	public static StringBuffer getSELECT_CATEGORY(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		String firstOptionValue = "";
		try{			
			query.append("Select ").append(RETAIL_CategoryDef.catid.name).append(",");
			query.append(RETAIL_CategoryDef.catname.name);
			query.append(" From ").append(RETAIL_CategoryDef.TABLE);
			query.append(" Where ").append(RETAIL_CategoryDef.companyid.name).append("='").append(shopInfoBean.getShopName()).append("'");			
			query.append(" And ").append(RETAIL_CategoryDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" Order By ").append(RETAIL_CategoryDef.catname.name);
			logger.debug("getSELECT_CATEGORY query = "+query);
			rtnBuffer = getSelectFromDB(conn, shopInfoBean.getShopName(), CODE_CATEGORY, query, fieldName, defaultvalue, 
					RETAIL_CategoryDef.catid.name, RETAIL_CategoryDef.catname.name, firstOptionValue);
			
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return rtnBuffer;
	}	
}
