package com.forest.retail.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.WebsiteDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DateUtil;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.retail.beandef.RETAIL_ProductOptionDef;

public class RetailServices extends GenericServices{

	
	private Logger logger = Logger.getLogger (RetailServices.class);
	
	public RetailServices(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn){
		super(conn, shopInfoBean, clientBean);
	}

	public JSONObject getProductJson(String prodid)throws Exception{	
		JSONObject json = new JSONObject();		
		ArrayList subList = null;
		HashMap mainMap = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		
		paramMap.put(RETAIL_ProductDef.companyid, _shopInfoBean.getShopName());
		paramMap.put(RETAIL_ProductDef.prodid, prodid);
		
		query.append("Select ").append(RETAIL_ProductDef.opt1_name);
		query.append(",").append(RETAIL_ProductDef.opt2_name);
		query.append(",").append(RETAIL_ProductDef.opt3_name);
		query.append(",").append(RETAIL_ProductDef.opt4_name);
		query.append(" From ").append(RETAIL_ProductDef.TABLE);
		query.append(" Where ").append(RETAIL_ProductDef.companyid).append("=?");
		query.append(" And ").append(RETAIL_ProductDef.prodid).append("=?");
		mainMap = searchDataMap(query, paramMap);
		
		paramMap.remove(RETAIL_ProductDef.companyid);
		query = new StringBuffer();
		query.append("Select ").append(RETAIL_ProductOptionDef.optid);
		query.append(",").append(RETAIL_ProductOptionDef.opt1);
		query.append(",").append(RETAIL_ProductOptionDef.opt2);
		query.append(",").append(RETAIL_ProductOptionDef.opt3);
		query.append(",").append(RETAIL_ProductOptionDef.opt4);
		query.append(",").append(RETAIL_ProductOptionDef.qty);
		query.append(",").append(RETAIL_ProductOptionDef.sellprice);
		query.append(" From ").append(RETAIL_ProductOptionDef.TABLE);
		query.append(" Where ").append(RETAIL_ProductOptionDef.prodid).append("=?");
		subList = searchDataArray(query, paramMap);
				
		json.put("main", mainMap);
		json.put("sub", subList);
		
		return json;
	}
}
