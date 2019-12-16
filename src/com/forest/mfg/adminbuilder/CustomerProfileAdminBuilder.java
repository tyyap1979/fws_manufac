package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
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
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.beandef.MFG_CustProductCustomerPriceDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_CustProductPriceDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.mfg.beandef.MFG_SupplierProductPriceDef;

public class CustomerProfileAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (CustomerProfileAdminBuilder.class);
	public CustomerProfileAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		json = super.requestJsonHandler();
		
		if(GeneralConst.CONTROL_RETRIEVE_PRODUCT.equals (_reqAction)){
			String customerId = _req.getParameter(CustomerProfileDef.customerid.name);
			String type = _req.getParameter(CustomerProfileDef.type.name);
			String typeName = null;
			StringBuffer query = new StringBuffer();
			
			if("D".equals(type)){
				typeName = MFG_CustProductPriceDef.dealerprice.name;
			}else if("C".equals(type)){
				typeName = MFG_CustProductPriceDef.clientprice.name;
			}else{
				typeName = MFG_CustProductPriceDef.publicprice.name;
			}
			if(!"lsm".equals(_shopInfoBean.getShopName())) {
				query.append(" Select ");
				query.append("c.").append(MFG_CustProductCustomerPriceDef.custpriceid);
				query.append(",'").append(_shopInfoBean.getShopName()).append("' As ").append(MFG_CustProductDef.companyid);
				query.append(",'").append(customerId).append("' As ").append(CustomerProfileDef.customerid);
				query.append(",a.").append(MFG_CustProductDef.prodid);
				query.append(",a.").append(MFG_CustProductDef.name).append(" As ").append(MFG_CustProductDef.prodid).append("_value");
				query.append(",b.").append(MFG_CustProductPriceDef.priceid);
				
				query.append(",COALESCE(c.").append(MFG_CustProductCustomerPriceDef.price);
				
				query.append(",b.").append(typeName).append(") As ").append(MFG_CustProductCustomerPriceDef.price);
				
				query.append(",b.").append(MFG_CustProductPriceDef.orderfrom);
				query.append(",b.").append(MFG_CustProductPriceDef.orderto);
				query.append(",if(c.").append(MFG_CustProductCustomerPriceDef.custpriceid).append(" is null,'N','U') As 'row-status'");
				
				query.append(" From ").append(MFG_CustProductDef.TABLE).append(" a");					
				query.append(" Inner Join ").append(MFG_CustProductPriceDef.TABLE).append(" b on b.").append(MFG_CustProductDef.prodid);
				query.append(" = a.").append(MFG_CustProductDef.prodid);
				query.append(" Left Join ").append(MFG_CustProductCustomerPriceDef.TABLE).append(" c on c.").append(MFG_CustProductCustomerPriceDef.prodid);
				query.append(" = a.").append(MFG_CustProductDef.prodid);
				query.append(" And ");
				query.append(" c.").append(MFG_CustProductCustomerPriceDef.priceid).append(" = b.").append(MFG_CustProductPriceDef.priceid);
				query.append(" And ");
				query.append(" c.").append(MFG_CustProductCustomerPriceDef.customerid).append("=").append(customerId);
				
				query.append(" Where a.").append(MFG_CustProductDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
				query.append(" And a.").append(MFG_CustProductDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
				
				query.append(" Union ");
			}
			query.append(" Select ");
			query.append("c.").append(MFG_CustProductCustomerPriceDef.custpriceid);
			query.append(",'").append(_shopInfoBean.getShopName()).append("' As ").append(MFG_CustProductDef.companyid);
			query.append(",'").append(customerId).append("' As ").append(CustomerProfileDef.customerid);
			query.append(",a.").append(MFG_CustProductDef.prodid);
			query.append(",sup.").append(MFG_CustProductDef.name).append(" As ").append(MFG_CustProductDef.prodid).append("_value");
			query.append(",b.").append(MFG_CustProductPriceDef.priceid);
			
			query.append(",COALESCE(c.").append(MFG_CustProductCustomerPriceDef.price);
			query.append(",b.").append(typeName).append(") As ").append(MFG_CustProductCustomerPriceDef.price);
			
			query.append(",b.").append(MFG_CustProductPriceDef.orderfrom);
			query.append(",b.").append(MFG_CustProductPriceDef.orderto);
			query.append(",if(c.").append(MFG_CustProductCustomerPriceDef.custpriceid).append(" is null,'N','U') As 'row-status'");
			
			query.append(" From ").append(MFG_SupplierProductDef.TABLE).append(" a");		
			query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" sup on sup.").append(MFG_CustProductDef.prodid);
			query.append(" = a.").append(MFG_SupplierProductDef.prodid);
			query.append(" Inner Join ").append(MFG_SupplierProductPriceDef.TABLE).append(" b on b.").append(MFG_CustProductDef.prodid);
			query.append(" = a.").append(MFG_CustProductDef.prodid);
			query.append(" Left Join ").append(MFG_CustProductCustomerPriceDef.TABLE).append(" c on c.").append(MFG_CustProductCustomerPriceDef.prodid);
			query.append(" = a.").append(MFG_CustProductDef.prodid);
			query.append(" And ");
			query.append(" c.").append(MFG_CustProductCustomerPriceDef.priceid).append(" = b.").append(MFG_CustProductPriceDef.priceid);
			query.append(" And ");
			query.append(" c.").append(MFG_CustProductCustomerPriceDef.customerid).append("=").append(customerId);
			
			query.append(" Where ");
//			query.append(" a.").append(MFG_CustProductDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
			query.append(" a.").append(MFG_CustProductDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
			
			query.append(" Order By ").append(MFG_CustProductDef.name).append(",").append(MFG_CustProductPriceDef.orderfrom);
			arrayListSearch = _gs.searchDataArray(query);
			
			json.put ("md", (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME));
			json.put ("SUBR0", buildEditDetail((ArrayList) arrayListSearch, 
					(String) BeanDefUtil.getField(MFG_CustProductCustomerPriceDef.class, BeanDefUtil.KEY), 
					(String) BeanDefUtil.getField(MFG_CustProductCustomerPriceDef.class, BeanDefUtil.TABLE_PREFIX), null, false));
		}else if("chkoutstanding".equals(_reqAction)){
			json = getAccountData();
		}
		return json;
	}
	
	public JSONObject getAccountData()throws Exception{			
		JSONObject json = new JSONObject();
		String customerId = _req.getParameter(MFG_StatementDef.customerid.name);
		MFG_ReportAdminBuilder rptBuilder = new MFG_ReportAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, null, null, null);
		HashMap dataMap = _gs.searchDataMap(rptBuilder.rptStmtQuery(customerId, GeneralConst.ACTIVE), null);
		logger.debug("dataMap = "+dataMap);
		json.put("rc", "0000");
		json.put("data", dataMap);
		return json;
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		// Read Additional Content From File
		buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/"+_clientBean.getRequestFilename()));		
		return buffer;
	}	
	
	public HashMap getCustomerInfo(String custId)throws Exception{
		HashMap dataMap = null;
		StringBuffer buffer = new StringBuffer();
		buffer.append("Select ");
		buffer.append(CustomerProfileDef.code).append(",");
		buffer.append(CustomerProfileDef.name).append(",");
		buffer.append(CustomerProfileDef.address).append(",");
		buffer.append(CustomerProfileDef.city).append(",");
		buffer.append(CustomerProfileDef.state).append(",");
		buffer.append(CustomerProfileDef.postcode).append(",");
		buffer.append(CustomerProfileDef.phoneno).append(",");
		buffer.append(CustomerProfileDef.faxno);
		
		buffer.append(" From ").append(CustomerProfileDef.TABLE);
		buffer.append(" Where ");
		buffer.append(CustomerProfileDef.customerid).append("=").append(custId);
		
		dataMap = _gs.searchDataMap(buffer, null);
		return dataMap;
	}
	
}
