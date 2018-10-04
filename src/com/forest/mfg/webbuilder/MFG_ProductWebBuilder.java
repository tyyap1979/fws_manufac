package com.forest.mfg.webbuilder;

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
import com.forest.common.util.DBUtil;
import com.forest.common.util.NumberUtil;
import com.forest.mfg.adminbuilder.MFG_CustProductAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.adminbuilder.MFG_TransactionAdminBuilder;
import com.forest.mfg.beandef.MFG_CustProductCustomerPriceDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_CustProductImageDef;
import com.forest.mfg.beandef.MFG_CustProductSelectionDef;
import com.forest.mfg.beandef.MFG_Grouping;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;
import com.forest.share.beandef.MaterialDetailDef;
import com.forest.share.webbuilder.BaseWebBuilder;

public class MFG_ProductWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (MFG_ProductWebBuilder.class);
	public MFG_ProductWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		if("priceenq".equals(reqAction)){
			json = getProductPrice(_req.getParameter(MFG_TransactionDetailDef.prodid.name), null);
		}
		return json;
	}
	
	public void displayHandler(String moduleId)throws Exception{						
		if("product.list".equals(moduleId)){
			_mc.initModuleContent(_moduleEle);
			putMainData();
		}else if("product.detail".equals(moduleId)){
			_mc.initModuleContent(_moduleEle, "loop[id=option]");
			putDetailData();			
		}
		
	}
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = new HashMap();
		int count = 0;
		LinkedHashMap paramMap = new LinkedHashMap();
		String groupId = _req.getParameter(MFG_Grouping.groupid.name);
		String groupName = null;
		if(CommonUtil.isEmpty(groupId)){
			groupId = _moduleEle.attr(MFG_Grouping.groupid.name);	
			if(!CommonUtil.isInteger(groupId)){
				return;
			}
		}
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append("a.").append(MFG_CustProductDef.prodid);
		query.append(",a.").append(MFG_CustProductDef.name);
		query.append(",a.").append(MFG_CustProductDef.description);
		query.append(" From ").append(MFG_CustProductDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(MFG_CustProductDef.companyid).append("=?");		
		query.append(" And a.").append(MFG_CustProductDef.sellonweb).append("='Y'");
		query.append(" And a.").append(MFG_CustProductDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		
		paramMap.put(MFG_CustProductDef.companyid, _shopInfoBean.getShopName());
		
		
		if(!CommonUtil.isEmpty(groupId)){
			query.append(" And a.").append(MFG_CustProductDef.grouptype).append("=?");
			paramMap.put(MFG_CustProductDef.grouptype, groupId);			
		}
		
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);					
				loopContent.put("images.list", putImagesData((String) loopContent.get(MFG_CustProductDef.prodid.name))[1].toString());				
				_mc.setLoopElementContent(loopContent);
			}						
			if(!CommonUtil.isEmpty(groupId)){
				HashMap groupMap = (HashMap) MFG_SelectBuilder.getHASH_PRODUCT_GROUPING(_dbConnection, _shopInfoBean);			
				groupName = (String)((HashMap) groupMap.get(groupId)).get(MFG_Grouping.groupname.name);
				loopContent = new HashMap();
				loopContent.put(MFG_Grouping.groupname.name, groupName);
				_mc.setElementContent(loopContent);
			}
		}catch(Exception e){
			logger.error(e, e);
		}			
	}
	
	private StringBuffer[] putImagesData(String key){
		ArrayList dataArray = null;
		HashMap loopContent = null;
		int count = 0;
		String filename = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer[] imageBuffer = new StringBuffer[2];
		StringBuffer query = new StringBuffer();		
		query.append("Select ");
		query.append("a.").append(MFG_CustProductImageDef.filename);
		query.append(",a.").append(MFG_CustProductImageDef.description);
		query.append(" From ").append(MFG_CustProductImageDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(MFG_CustProductImageDef.prodid).append("=?");
		query.append(" Order by a.").append(MFG_CustProductImageDef.position);
		
		paramMap.put(MFG_CustProductImageDef.prodid, key);
		try{
			imageBuffer[0] = new StringBuffer();
			imageBuffer[1] = new StringBuffer();
			
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);
				filename = (String) loopContent.get(MFG_CustProductImageDef.filename.name);		
				if(i==0){
					imageBuffer[0].append("/photo/product/").append(key).append("/tn_").append(filename);
				}
				if("product.htm".equals(_clientBean.getRequestFilename())){
					imageBuffer[1].append("<li>");
					imageBuffer[1].append("<a ");
					imageBuffer[1].append(" rel='prettyPhoto[").append(key).append("]'");
					imageBuffer[1].append(" href='").append("/photo/product/"+key+"/"+filename).append("'");
					imageBuffer[1].append(">");
					imageBuffer[1].append("<img src='"+"/photo/product/"+key+"/tn_"+filename+"' alt='").append((String) loopContent.get(MaterialDetailDef.description.name)).append("' />");				     			
	     			imageBuffer[1].append("</a>");     	     			
	     			imageBuffer[1].append("</li>");
				}else{
					if(i>0){
						imageBuffer[1].append("<a class='hide'");
						imageBuffer[1].append(" rel='prettyPhoto[").append(key).append("]'");
						imageBuffer[1].append(" href='").append("/photo/product/"+key+"/"+filename).append("'");
						imageBuffer[1].append(" title='").append((String) loopContent.get(MFG_CustProductImageDef.description.name)).append("'");
						imageBuffer[1].append(">");							     		
		     			imageBuffer[1].append("</a>");   
					}else{
						imageBuffer[1].append("<a ");
						imageBuffer[1].append(" rel='prettyPhoto[").append(key).append("]'");
						imageBuffer[1].append(" href='").append("/photo/product/"+key+"/"+filename).append("'");
						imageBuffer[1].append(">");
						imageBuffer[1].append("<img class='overlay-item-image' src='"+"/photo/product/"+key+"/tn_"+filename+"' alt='View Images' />");				     			
		     			imageBuffer[1].append("</a>");     	     			
					}	
				}
			}						
		}catch(Exception e){
			logger.error(e, e);
		}
//		logger.debug("imageBuffer = "+imageBuffer);
		return imageBuffer;		
	}
	
	private void putDetailData(){
		String prodid = _req.getParameter(MFG_CustProductDef.prodid.name);
		HashMap loopContent = null;		
		StringBuffer[] imageBuffer = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		
		query.append("Select ");		
		query.append("a.").append(MFG_CustProductDef.name);
		query.append(",a.").append(MFG_CustProductDef.prodid);
		query.append(",a.").append(MFG_CustProductDef.code);
		query.append(",a.").append(MFG_CustProductDef.description);
		query.append(",a.").append(MFG_CustProductDef.sellunittype);
		query.append(",a.").append(MFG_CustProductDef.minorder);
		query.append(",a.").append(MFG_CustProductDef.customise);
		query.append(" From ").append(MFG_CustProductDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(MFG_CustProductDef.companyid).append("=?");		
		query.append(" And a.").append(MFG_CustProductDef.prodid).append("=?");		
		
		paramMap.put(MFG_CustProductDef.companyid, _shopInfoBean.getShopName());
		paramMap.put(MFG_CustProductDef.prodid, prodid);
		try{
			loopContent = _gs.searchDataMap(query, paramMap);								
			JSONObject json = putOptionData(prodid, loopContent);
			
			imageBuffer = putImagesData(prodid);			
			loopContent.put(MFG_CustProductDef.sellunittype.name + "_value", (String)MFG_SelectBuilder.getHASH_SELL_UNIT(_dbConnection, _shopInfoBean).get(loopContent.get(MFG_CustProductDef.sellunittype.name)));	
			loopContent.put("main.image", imageBuffer[0].toString());	
			loopContent.put("images.list", imageBuffer[1].toString());
			loopContent.put("selected.currency", _clientBean.getSelectedCurrency());
			loopContent.put("custtype",_clientBean.getUserGroup());
			
			// Customise
			String customiseFlag = (String)loopContent.get(MFG_CustProductDef.customise.name);
			if("Y".equals(customiseFlag)){
				loopContent.put("style.customise", "");
			}else{
				loopContent.put("style.customise", "display: none");
			}
			
			// Set Price			
			if(CommonUtil.isEmpty(_clientBean.getUserGroup())){
				loopContent.put(MFG_CustProductCustomerPriceDef.price.name, "");
				loopContent.put("price.style", "display: none");
				loopContent.put("islogin", "false");
			}else{
				loopContent.put(MFG_CustProductCustomerPriceDef.price.name, json.get(MFG_CustProductCustomerPriceDef.price.name).toString());
				loopContent.put("login.style", "display: none");
				loopContent.put("islogin", "true");
			}
						
//			logger.debug("productMainjson = "+json.get("productMainjson").toString());
			loopContent.put("productPricejsonArray", json.get("productPricejsonArray").toString());
			loopContent.put("productMainjson", json.get("productMainjson").toString());
			loopContent.put("productOptionPriceJson", json.get("productOptionPriceJson").toString());
			
			logger.debug("loopContent = "+loopContent);
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}
	
	private JSONObject putOptionData(String prodid, HashMap mapProduct){
		ArrayList dataArray = null;
		HashMap loopContent = null;
		StringBuffer loopBuffer = new StringBuffer();
		int count = 0;
		String prodoptid = null;
		String sellUnitType = (String)mapProduct.get(MFG_CustProductDef.sellunittype.name);
		String customiseFlag = (String)mapProduct.get(MFG_CustProductDef.customise.name);
		JSONObject json = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		String[] prodoptidArray = null;
		query.append("Select ");
		query.append("a.").append(MFG_CustProductSelectionDef.prodoptid);
		query.append(",b.").append(MFG_ProductOptionDef.groupname);
		query.append(",b.").append(MFG_ProductOptionDef.description).append(" As option_description");
		query.append(" From ").append(MFG_CustProductSelectionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_ProductOptionDef.TABLE).append(" b On b.");
		query.append(MFG_ProductOptionDef.prodoptid).append("=a.").append(MFG_CustProductSelectionDef.prodoptid);
		
		query.append(" Where");
		query.append(" a.").append(MFG_CustProductSelectionDef.prodid).append("=?");				
		
		query.append(" Order By");
		query.append(" a.").append(MFG_CustProductSelectionDef.position);
		
		paramMap.put(MFG_CustProductSelectionDef.prodid, prodid);
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			if("sf".equals(sellUnitType)){				
				loopBuffer.append("Width: <input type='text'");
				loopBuffer.append(MFG_TransactionDetailDef.width);
				loopBuffer.append(" id='").append(MFG_TransactionDetailDef.width).append("_clone'");
				loopBuffer.append(" name='").append(MFG_TransactionDetailDef.width).append("_clone'");
				loopBuffer.append(" class='validate[required]'");
				loopBuffer.append(" size='10' maxlength='15' />");
				loopBuffer.append(" x ");
				loopBuffer.append("Height: <input type='text'");
				loopBuffer.append(" id='").append(MFG_TransactionDetailDef.height).append("_clone'");
				loopBuffer.append(" name='").append(MFG_TransactionDetailDef.height).append("_clone'");
				loopBuffer.append(" class='validate[required]'");
				loopBuffer.append(" size='10' maxlength='15' />");
				loopBuffer.append(" = ");
				loopBuffer.append("<input type='text'");
				loopBuffer.append(" id='").append(MFG_TransactionDetailDef.unit).append("'");
				loopBuffer.append(" name='").append(MFG_TransactionDetailDef.unit).append("'");
				loopBuffer.append(" size='10' maxlength='15' /> SQFT");
				
				loopContent = new LinkedHashMap();
				loopContent.put(MFG_ProductOptionDef.groupname.name, "Measurement");
				loopContent.put("option_description", "");
				loopContent.put("option.selection", loopBuffer.toString());
				_mc.setLoopElementContent(loopContent);
			}else if("mm".equals(sellUnitType) || "in".equals(sellUnitType) || "ft".equals(sellUnitType)){
				loopBuffer.append("Width: <input type='text'");				
				loopBuffer.append(" id='").append(MFG_TransactionDetailDef.width).append("_clone'");
				loopBuffer.append(" name='").append(MFG_TransactionDetailDef.width).append("_clone'");
				loopBuffer.append(" class='validate[required]'");
				loopBuffer.append(" size='10' maxlength='15' />");	
				loopBuffer.append(" <input type='hidden'");				
				loopBuffer.append(" id='").append(MFG_TransactionDetailDef.unit).append("'");
				loopBuffer.append(" name='").append(MFG_TransactionDetailDef.unit).append("'");				
				loopBuffer.append(" />");	
				
				loopContent = new LinkedHashMap();
				loopContent.put(MFG_ProductOptionDef.groupname.name, "Measurement");
				loopContent.put("option_description", "");
				loopContent.put("option.selection", loopBuffer.toString());
				_mc.setLoopElementContent(loopContent);
			}
			
			// Quantity
			loopBuffer = new StringBuffer();			
			loopBuffer.append("<input type='text'");
			loopBuffer.append(" id='").append(MFG_TransactionDetailDef.qty).append("'");
			loopBuffer.append(" name='").append(MFG_TransactionDetailDef.qty).append("'");
//			loopBuffer.append(" value='2'");
			loopBuffer.append(" class='validate[required]'");			
			loopBuffer.append(" size='4' maxlength='8' />");
			
			loopContent = new LinkedHashMap();
			loopContent.put(MFG_ProductOptionDef.groupname.name, "Quantity");
			loopContent.put("option_description", "");
			loopContent.put("option.selection", loopBuffer.toString());

			count = dataArray.size();
			prodoptidArray = new String[count];
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				prodoptid = (String)loopContent.get(MFG_CustProductSelectionDef.prodoptid.name);
				prodoptidArray[i] = prodoptid;
				if("Y".equals(customiseFlag)){
					loopContent.put("style.option", "display: none");
					loopContent.put("option.selection",MFG_SelectBuilder.getSELECT_PRODUCT_OPTION(_dbConnection, _shopInfoBean, "opt"+(i+1), "", prodoptid).toString());
				}else{
					loopContent.put("style.option", "");
					loopContent.put("option_description", "<p>"+(String)loopContent.get("option_description")+"</p>");
					loopContent.put("option.selection",getOption("opt"+(i+1), prodoptid).toString());
				}
				
				
				_mc.setLoopElementContent(loopContent);
			}						
			
						
			
			json = getProductPrice(prodid, prodoptidArray);			
						
		}catch(Exception e){
			logger.error(e, e);
		}	
		return json;
	}
	
	private StringBuffer getOption(String optName, String prodoptid){
		StringBuffer query = new StringBuffer();
		StringBuffer rtnBuffer = new StringBuffer();
		ArrayList dataArray = null;
		HashMap rowMap = null;
		query.append("Select b.").append(MFG_ProductOptionDetailDef.prodoptdetailid.name);
		query.append(", b.").append(MFG_ProductOptionDetailDef.name.name);
		query.append(", b.").append(MFG_ProductOptionDetailDef.dealerprice.name);
		query.append(", b.").append(MFG_ProductOptionDetailDef.clientprice.name);
		query.append(", b.").append(MFG_ProductOptionDetailDef.publicprice.name);
		query.append(", b.").append(MFG_ProductOptionDetailDef.sellunittype.name);
		query.append(", b.").append(MFG_ProductOptionDetailDef.filename.name);
		query.append(" From ").append(MFG_ProductOptionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" b On");
		query.append(" a.").append(MFG_ProductOptionDef.prodoptid).append("=b.").append(MFG_ProductOptionDetailDef.prodoptid);
		query.append(" Where a.").append(MFG_ProductOptionDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
		query.append(" And b.").append(MFG_ProductOptionDetailDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
					
		query.append(" And a.").append(MFG_ProductOptionDetailDef.companyid.name).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.").append(MFG_ProductOptionDetailDef.prodoptid.name).append("=").append(prodoptid);
		query.append(" Order By b.").append(MFG_ProductOptionDetailDef.position);
		try{			
			rtnBuffer.append("<ul class='option-list'>");
			dataArray = _gs.searchDataArray(query);
			for(int i=0; i<dataArray.size(); i++){
				rtnBuffer.append("<li>");
				rowMap = (HashMap)dataArray.get(i);
				rtnBuffer.append("<div class='optCheck'>");
					rtnBuffer.append("<input type='radio' id='").append(optName).append((String)rowMap.get(MFG_ProductOptionDetailDef.prodoptdetailid.name)).append("' name='").append(optName).append("' value='").append((String)rowMap.get(MFG_ProductOptionDetailDef.prodoptdetailid.name)).append("'> ");
				rtnBuffer.append("</div>");
				
				rtnBuffer.append("<div class='optImage'>");
					rtnBuffer.append("<img src='/photo/option/"+prodoptid+"/").append((String)rowMap.get(MFG_ProductOptionDetailDef.filename.name)).append("' />");
				rtnBuffer.append("</div>");		
				
				rtnBuffer.append("<div class='optName'>");
					rtnBuffer.append((String)rowMap.get(MFG_ProductOptionDetailDef.name.name));
					
					
//					if(!CommonUtil.isEmpty(_clientBean.getUserGroup())){
//						rtnBuffer.append(" ");
//						if("P".equals(_clientBean.getUserGroup())){
//							rtnBuffer.append((String)rowMap.get(MFG_ProductOptionDetailDef.publicprice.name));	
//						}else if("D".equals(_clientBean.getUserGroup())){
//							rtnBuffer.append((String)rowMap.get(MFG_ProductOptionDetailDef.dealerprice.name));
//						}else if("C".equals(_clientBean.getUserGroup())){
//							rtnBuffer.append((String)rowMap.get(MFG_ProductOptionDetailDef.clientprice.name));
//						}							
//						
//						rtnBuffer.append("/").append((String)rowMap.get(MFG_ProductOptionDetailDef.sellunittype.name));
//					}
				rtnBuffer.append("</div>");
				rtnBuffer.append("</li>");
			}
			rtnBuffer.append("</ul>");
			rtnBuffer.append("<div class='clear'></div>");
		}catch (Exception e) {
			logger.error(e, e);
		}
		
		return rtnBuffer;
	}
	
	public JSONObject getProductPrice(String prodid, String[] prodoptidArray){	
		JSONObject json = new JSONObject();		
		HashMap productMainMap = new HashMap();	
		HashMap productPriceMap = null;
		HashMap dataMap = null;
		ArrayList dataArray = null;
		String clientType = null;
		String price = null;
		try{
			// JSON Component		
			MFG_TransactionAdminBuilder transAdmin = new MFG_TransactionAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, MFG_TransactionDef.class, ModuleConfig.COMPANY_SELECTED, _shopInfoBean.getBusiness());
			MFG_CustProductAdminBuilder custAdmin = new MFG_CustProductAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, MFG_CustProductDef.class, ModuleConfig.COMPANY_SELECTED, _shopInfoBean.getBusiness());
			
			productMainMap = transAdmin.getProduct(prodid);
			productMainMap.remove("prod0");
			String factory = (String)((HashMap)productMainMap.get("prod"+prodid)).get("factory");
			
			json.put ("productMainjson", new JSONObject().put("data", productMainMap));			
			
			if(CommonUtil.isEmpty(_clientBean.getUserGroup())){
				clientType = "P";
			}else{
				clientType = _clientBean.getUserGroup();
			}
			
			dataMap = custAdmin.getProductPrice(prodid.split(","), factory.split(","),null, clientType);
			dataArray = (ArrayList) dataMap.get("prod"+prodid);
			productPriceMap = (HashMap)dataArray.get(dataArray.size()-1);
			
			price = (String)productPriceMap.get(MFG_CustProductCustomerPriceDef.price.name);
			
			json.put (MFG_CustProductCustomerPriceDef.price.name, price);	
			json.put ("productPricejsonArray", dataMap);			
			json.put ("productOptionPriceJson", transAdmin.buildProductOptionPriceJson(DBUtil.arrayToString(prodoptidArray, MFG_ProductOptionDetailDef.prodoptid)));
		}catch (Exception e) {
			logger.error(e, e);
		}
		return json;
	}
}
