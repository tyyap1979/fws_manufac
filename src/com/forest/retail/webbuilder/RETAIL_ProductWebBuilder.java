package com.forest.retail.webbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.ModuleConfig;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.NumberUtil;
import com.forest.retail.adminbuilder.RETAIL_ProductAdminBuilder;
import com.forest.retail.beandef.RETAIL_ProductAttributeDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.retail.beandef.RETAIL_ProductImageDef;
import com.forest.retail.beandef.RETAIL_ProductOptionDef;
import com.forest.retail.services.RetailServices;
import com.forest.share.webbuilder.BaseWebBuilder;

public class RETAIL_ProductWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (RETAIL_ProductWebBuilder.class);
	public RETAIL_ProductWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public HashMap displayHandlerWithHashMap(String moduleId)throws Exception{
		_mc.initModuleContent(_moduleEle);
		HashMap metaMap = null;
		if("product.detail".equals(moduleId)){
			metaMap = putDetailData();
		}
		return metaMap;
	}
	public void displayHandler(String moduleId)throws Exception{				
		_mc.initModuleContent(_moduleEle);
		if("product.list".equals(moduleId)){
			putMainData();
		}
	}
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = new HashMap();
		int count = 0;
		LinkedHashMap paramMap = new LinkedHashMap();
		String catId = _req.getParameter(RETAIL_ProductDef.catid.name);
		String minPrice = null;
		String maxPrice = null;
		
//		String groupName = null;
		if(CommonUtil.isEmpty(catId)){
			catId = _moduleEle.attr(RETAIL_ProductDef.catid.name);	
//			if(!CommonUtil.isInteger(catId)){
//				return;
//			}
		}
		StringBuffer query = new StringBuffer();
		query.append("Select");
		query.append(" a.").append(RETAIL_ProductDef.prodid);
		query.append(",a.").append(RETAIL_ProductDef.prodcode);
		query.append(",a.").append(RETAIL_ProductDef.prodname);
		query.append(",a.").append(RETAIL_ProductDef.description);
		
		query.append(",Min(c.").append(RETAIL_ProductOptionDef.sellprice).append(") As minPrice");
		query.append(",Max(c.").append(RETAIL_ProductOptionDef.sellprice).append(") As maxPrice");
		
		query.append(",a.").append(RETAIL_ProductDef.discount);
		query.append(",b.").append(RETAIL_ProductImageDef.filename);
		
		query.append(" From ").append(RETAIL_ProductDef.TABLE).append(" a");
		query.append(" Inner Join ").append(RETAIL_ProductOptionDef.TABLE).append(" c");
		query.append(" On c.").append(RETAIL_ProductOptionDef.prodid).append("=a.").append(RETAIL_ProductDef.prodid);
		
		query.append(" Left Join ").append(RETAIL_ProductImageDef.TABLE).append(" b");
		query.append(" On b.").append(RETAIL_ProductImageDef.prodid).append("=a.").append(RETAIL_ProductDef.prodid);
		query.append(" And b.").append(RETAIL_ProductImageDef.position).append("=1");
		query.append(" Where");
		query.append(" a.").append(RETAIL_ProductDef.companyid).append("=?");		
		query.append(" And a.").append(RETAIL_ProductDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		
		query.append(" Group By a.").append(RETAIL_ProductDef.prodid);
		
		paramMap.put(RETAIL_ProductDef.companyid, _shopInfoBean.getShopName());
		
		
		if(!CommonUtil.isEmpty(catId)){
			query.append(" And a.").append(RETAIL_ProductDef.catid).append("=?");
			paramMap.put(RETAIL_ProductDef.catid, catId);			
		}
		
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);		
				minPrice = (String)loopContent.get("minPrice");
				maxPrice = (String)loopContent.get("maxPrice");
				if(minPrice.equals(maxPrice)){
					loopContent.put(RETAIL_ProductOptionDef.sellprice.name, NumberUtil.formatCurrency(minPrice));
				}else{
					loopContent.put(RETAIL_ProductOptionDef.sellprice.name, NumberUtil.formatCurrency(minPrice) + " - " + NumberUtil.formatCurrency(maxPrice));
				}
				
				_mc.setLoopElementContent(loopContent);
			}						
		}catch(Exception e){
			logger.error(e, e);
		}			
	}
	
	private HashMap putDetailData(){
		String prodid = _req.getParameter(RETAIL_ProductDef.prodid.name);
		ArrayList dataArray = null;
		HashMap elementContent = null;
		HashMap loopContent = null;		
		HashMap metaMap = new HashMap();
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		int count = 0;
		query.append("Select a.*");		
		query.append(" From ").append(RETAIL_ProductDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(RETAIL_ProductDef.companyid).append("=?");		
		query.append(" And a.").append(RETAIL_ProductDef.prodid).append("=?");		
		
		paramMap.put(RETAIL_ProductDef.companyid, _shopInfoBean.getShopName());
		paramMap.put(RETAIL_ProductDef.prodid, prodid);
		try{
			elementContent = _gs.searchDataMap(query, paramMap);
			metaMap.put("html_title", elementContent.get(RETAIL_ProductDef.prodname.name));			
			
			// Retrieve Options			
			JSONObject json = new RetailServices(_shopInfoBean, _clientBean, _dbConnection).getProductJson(prodid);
			elementContent.put("jsonoption", json.toString());
						
			// Retrieve Image
			paramMap.remove(RETAIL_ProductDef.companyid);
			query = new StringBuffer();
			query.append("Select a.*");		
			query.append(" From ").append(RETAIL_ProductImageDef.TABLE).append(" a");
			query.append(" Where");
			query.append(" a.").append(RETAIL_ProductImageDef.prodid).append("=?");	
			query.append(" Order by a.").append(RETAIL_ProductImageDef.position).append(" asc");	
			
			dataArray = _gs.searchDataArray(query, paramMap);
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);
				if(i==0){					
					elementContent.put("main_"+RETAIL_ProductImageDef.filename, loopContent.get(RETAIL_ProductImageDef.filename.name));
				}else{
					_mc.setLoopElementContent(loopContent, 0);
				}				
			}			

			// Retrieve Attribute 
			query = new StringBuffer();
			query.append("Select *");		
			query.append(" From ").append(RETAIL_ProductAttributeDef.TABLE);
			query.append(" Where");
			query.append(" ").append(RETAIL_ProductAttributeDef.prodid).append("=?");	
			
			dataArray = _gs.searchDataArray(query, paramMap);
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);
				_mc.setLoopElementContent(loopContent, 1);
			}			
			
			// Retrive product price 
			HashMap priceMap = null;
			String minPrice = null;
			String maxPrice = null;
			query = new StringBuffer();
			query.append("Select ");
			query.append(" Min(").append(RETAIL_ProductOptionDef.sellprice).append(") As minPrice, ");
			query.append(" Max(").append(RETAIL_ProductOptionDef.sellprice).append(") As maxPrice ");
			query.append(" From ").append(RETAIL_ProductOptionDef.TABLE);
			query.append(" Where");
			query.append(" ").append(RETAIL_ProductOptionDef.prodid).append("=?");	
			query.append(" And ").append(RETAIL_ProductOptionDef.status).append("='").append(GeneralConst.ACTIVE).append("'");			
			priceMap = _gs.searchDataMap(query, paramMap);
			logger.debug("priceMap: "+priceMap);
			minPrice = (String)priceMap.get("minPrice");
			maxPrice = (String)priceMap.get("maxPrice");
			if(minPrice.equals(maxPrice)){
				elementContent.put(RETAIL_ProductOptionDef.sellprice.name, NumberUtil.formatCurrency(minPrice));
			}else{
				elementContent.put(RETAIL_ProductOptionDef.sellprice.name, NumberUtil.formatCurrency(minPrice) + " - " + NumberUtil.formatCurrency(maxPrice));
			}
			_mc.setElementContent(elementContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
		
		return metaMap;
	}		
}
