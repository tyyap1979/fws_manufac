package com.forest.mfg.adminbuilder;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.mfg.services.ChartDataServices;

public class MFG_XMLAdminBuilder extends GenericAdminBuilder{
	private Logger logger = Logger.getLogger(MFG_XMLAdminBuilder.class);	

	public MFG_XMLAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);			
	} 	
	
	public JSONObject requestJsonHandler()throws Exception{
		JSONObject json = new JSONObject();	
		StringBuffer buffer = new StringBuffer();
		String mod = _req.getParameter("mod");
		ChartDataServices cds = new ChartDataServices(_shopInfoBean, _clientBean, _dbConnection);
		if("SALESMONTH".equals(mod)){
			buffer = cds.xmlMonthlySales();
		}else if("SALESDAY".equals(mod)){
			buffer = cds.xmlCurrentMonthSales();
		}
		json.put("mod", mod);
		json.put("content", buffer);
		logger.debug("buffer = "+buffer);
		return json;
	}
	
}
