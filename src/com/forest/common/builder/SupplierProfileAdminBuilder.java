package com.forest.common.builder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.beandef.SupplierProfileDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.adminbuilder.MFG_ReportAdminBuilder;
import com.forest.mfg.beandef.MFG_CustProductCustomerPriceDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_CustProductPriceDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.mfg.beandef.MFG_SupplierProductPriceDef;

public class SupplierProfileAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (SupplierProfileAdminBuilder.class);
	public SupplierProfileAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public HashMap getSupplierInfo(String supplierId)throws Exception{
		HashMap dataMap = null;
		StringBuffer buffer = new StringBuffer();
		buffer.append("Select ");
		buffer.append(SupplierProfileDef.code).append(",");
		buffer.append(SupplierProfileDef.contactperson).append(",");
		buffer.append(SupplierProfileDef.name).append(",");
		buffer.append(SupplierProfileDef.address).append(",");
		buffer.append(SupplierProfileDef.city).append(",");
		buffer.append(SupplierProfileDef.state).append(",");
		buffer.append(SupplierProfileDef.postcode).append(",");
		buffer.append(SupplierProfileDef.phoneno).append(",");
		buffer.append(SupplierProfileDef.faxno);
		
		buffer.append(" From ").append(SupplierProfileDef.TABLE);
		buffer.append(" Where ");
		buffer.append(SupplierProfileDef.id).append("=").append(supplierId);
		
		dataMap = _gs.searchDataMap(buffer, null);
		return dataMap;
	}
	
}
