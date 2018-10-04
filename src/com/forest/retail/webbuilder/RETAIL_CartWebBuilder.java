package com.forest.retail.webbuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CookiesUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.IPUtil;
import com.forest.common.util.NumberUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.retail.beandef.RETAIL_CartDef;
import com.forest.retail.beandef.RETAIL_CartDetailDef;
import com.forest.retail.beandef.RETAIL_MemberDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.retail.beandef.RETAIL_ProductImageDef;
import com.forest.retail.beandef.RETAIL_ProductOptionDef;
import com.forest.share.beandef.ShipmentDomesticDef;
import com.forest.share.beandef.ShipmentDomesticDetailDef;
import com.forest.share.beandef.ShipmentInternationalDef;
import com.forest.share.beandef.ShipmentInternationalDetailDef;
import com.forest.share.webbuilder.BaseWebBuilder;

public class RETAIL_CartWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (RETAIL_CartWebBuilder.class);
	public RETAIL_CartWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		String rtnValue  = null;
		if(GeneralConst.AUTO_SUGGEST.equals(reqAction)){
			rtnValue = requestJsonArrayHandler().toString();
		}else if ("getlocal".equals(reqAction)) {
			rtnValue = getDomesticShipment().toString();			
		}else if ("getinternational".equals(reqAction)) {
			rtnValue = getInternationalShipment().toString();			
		}else if(!CommonUtil.isEmpty(reqAction)){
			rtnValue = addToCart(reqAction).toString();
		}
		
		return rtnValue;
	}
	
	public void displayHandler(String moduleId)throws Exception{						
		if("cart.list".equals(moduleId)){
			_mc.initModuleContent(_moduleEle, "[custom=loop]");	
			putMainData();
		}
	}
	
	public JSONObject addToCart(String reqAction){	
		JSONObject json = new JSONObject();
		StringBuffer query = null;
		HashMap[] detailMap = null;
		LinkedHashMap paramMap = null;	
		try{
			String cartId = getCartID();
			detailMap = BuilderUtil.requestValuesToDataObject(RETAIL_CartDetailDef.class, _req, 
					BeanDefUtil.getArrayList(RETAIL_CartDetailDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
					(String) BeanDefUtil.getField(RETAIL_CartDef.class, BeanDefUtil.KEY), cartId, _shopInfoBean.getShopName(),"");
			logger.debug("detailMap: "+detailMap);

			if("update".equals(reqAction)){
				if(detailMap[0]!=null){
													
					query = new StringBuffer();
					query.append("Update ").append(RETAIL_CartDetailDef.TABLE);
					query.append(" Set ").append(RETAIL_CartDetailDef.qty).append("=?");
					query.append(" ,").append(RETAIL_CartDetailDef.sellsubtotal).append("=").append(RETAIL_CartDetailDef.qty).append("*").append(RETAIL_CartDetailDef.sellprice);
					query.append(" Where ").append(RETAIL_CartDetailDef.cartdetailid).append("=?");
					
					paramMap = new LinkedHashMap();
					paramMap.put(RETAIL_CartDetailDef.qty, (String) detailMap[0].get(RETAIL_CartDetailDef.qty.name));
					paramMap.put(RETAIL_CartDetailDef.cartdetailid, (String) detailMap[0].get(RETAIL_CartDetailDef.cartdetailid.name));
					_gs.executeQuery(query, paramMap);
				}
			}else if("add".equals(reqAction) || "delete".equals(reqAction)){
				if(detailMap!=null){							
					_gs.updateDetail(RETAIL_CartDetailDef.class, detailMap, BeanDefUtil.getKeyObject(RETAIL_CartDetailDef.class), BeanDefUtil.getArrayList(RETAIL_CartDetailDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
				}
			}
			// Set Price info
			if("add".equals(reqAction) || "update".equals(reqAction)){
				updatePriceInfo(cartId);
			}
			
			json.put("rc", "0000");	
			if("update".equals(reqAction) || "delete".equals(reqAction)){
				// Get Subtotal 
				query = new StringBuffer();
				query.append("Select ");
				query.append(RETAIL_CartDetailDef.sellsubtotal);
				query.append(" From ");
				query.append(RETAIL_CartDetailDef.TABLE);
				query.append(" Where ").append(RETAIL_CartDetailDef.cartdetailid).append("=?");
				
				paramMap = new LinkedHashMap();
				paramMap.put(RETAIL_CartDetailDef.cartdetailid, (String) detailMap[0].get(RETAIL_CartDetailDef.cartdetailid.name));
				
				json.put("data",_gs.searchDataMap(query, paramMap));
			}
		}catch (Exception e) {
			logger.error(e, e);
		}
		return json;
	}
		
	private String getCartID()throws Exception{
		CookiesUtil cu = new CookiesUtil(_req, _resp);
		String cartId = cu.getCartID();
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int key = 0;
		if(CommonUtil.isEmpty(cartId)){
			query.append("Insert Into ").append(RETAIL_CartDef.TABLE).append("(");
			query.append(RETAIL_CartDef.companyid);
			query.append(",").append(RETAIL_CartDef.email);
			query.append(",").append(RETAIL_CartDef.updatedate);
			query.append(") Values(?, ?, now())");
			pstmt = _dbConnection.prepareStatement(query.toString(),PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, _shopInfoBean.getShopName());
			pstmt.setString(2, _clientBean.getLoginUserEmail());
			pstmt.execute ();
			
			rs = pstmt.getGeneratedKeys ();
			if (rs.next()) {
				key = rs.getInt(1);		
				cartId = String.valueOf(key);
				cu.setCartID(cartId);
		    }
		}else{
			query.append("Update ").append(RETAIL_CartDef.TABLE);
			query.append(" Set ").append(RETAIL_CartDef.updatedate).append(" = now()");
			query.append(" Where ").append(RETAIL_CartDef.cartid).append("=").append(cartId);
			_gs.executeQuery(query, null);
		}
		return cartId;
	}
	
	
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		BigDecimal subtotal = new BigDecimal("0");
		int totalWeight = 0;
		String cartId = new CookiesUtil(_req, _resp).getCartID();
		
		if(CommonUtil.isEmpty(cartId)){
			loopContent = new HashMap();
			loopContent.put("total.items", "0");
			loopContent.put("subtotal", "0.00");
			_mc.setElementContent(loopContent);
			
			return;
		}
		
		query.append("Select ");
		query.append(RETAIL_CartDetailDef.cartdetailid);
		query.append(",a.").append(RETAIL_CartDetailDef.prodid);
		query.append(",a.").append(RETAIL_CartDetailDef.qty);
		query.append(",a.").append(RETAIL_CartDetailDef.sellprice);
		query.append(",a.").append(RETAIL_CartDetailDef.discount);
		query.append(",a.").append(RETAIL_CartDetailDef.sellsubtotal);
		
		query.append(",b.").append(RETAIL_ProductDef.prodcode);
		query.append(",b.").append(RETAIL_ProductDef.prodname);
		query.append(",b.").append(RETAIL_ProductDef.opt1_name);
		query.append(",b.").append(RETAIL_ProductDef.opt2_name);
		query.append(",b.").append(RETAIL_ProductDef.opt3_name);
		query.append(",b.").append(RETAIL_ProductDef.opt4_name);
		
		query.append(",c.").append(RETAIL_ProductOptionDef.opt1);
		query.append(",c.").append(RETAIL_ProductOptionDef.opt2);
		query.append(",c.").append(RETAIL_ProductOptionDef.opt3);
		query.append(",c.").append(RETAIL_ProductOptionDef.opt4);
		query.append(",c.").append(RETAIL_ProductOptionDef.weight);
		
		query.append(",d.").append(RETAIL_ProductImageDef.filename);
				
		query.append(" From ").append(RETAIL_CartDetailDef.TABLE).append(" a");

		query.append(" Inner Join ").append(RETAIL_ProductDef.TABLE).append(" b On");
		query.append(" b.").append(RETAIL_ProductDef.prodid).append("=a.").append(RETAIL_CartDetailDef.prodid);

		query.append(" Inner Join ").append(RETAIL_ProductOptionDef.TABLE).append(" c on");
		query.append(" c.").append(RETAIL_ProductOptionDef.optid).append(" = a.").append(RETAIL_CartDetailDef.optid);	
		
		query.append(" Left Join ").append(RETAIL_ProductImageDef.TABLE).append(" d on");
		query.append(" d.").append(RETAIL_ProductImageDef.prodid).append(" = a.").append(RETAIL_CartDetailDef.prodid);
		query.append(" And d.").append(RETAIL_ProductImageDef.position).append(" = 1");
				
		query.append(" Where");
		query.append(" a.").append(RETAIL_CartDetailDef.cartid).append("=?");		
		
		query.append(" Order By");
		query.append(" b.").append(RETAIL_ProductDef.prodname);		
		
		
		paramMap.put(RETAIL_CartDetailDef.cartid, cartId);
		
		StringBuffer optionsBuffer = new StringBuffer();
		String opt1, opt2, opt3, opt4 = null;
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			int count = dataArray.size();
			
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);					
				optionsBuffer = new StringBuffer();
				
				opt1 = (String) loopContent.get(RETAIL_ProductDef.opt1_name.name);
				opt2 = (String) loopContent.get(RETAIL_ProductDef.opt2_name.name);
				opt3 = (String) loopContent.get(RETAIL_ProductDef.opt3_name.name);
				opt4 = (String) loopContent.get(RETAIL_ProductDef.opt4_name.name);

				if(!CommonUtil.isEmpty(opt1)){
					optionsBuffer.append("<br>").append(opt1).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt1.name));
				}
				if(!CommonUtil.isEmpty(opt2)){
					optionsBuffer.append("<br>").append(opt2).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt2.name));
				}
				if(!CommonUtil.isEmpty(opt3)){
					optionsBuffer.append("<br>").append(opt3).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt3.name));
				}
				if(!CommonUtil.isEmpty(opt4)){
					optionsBuffer.append("<br>").append(opt4).append(": ").append((String) loopContent.get(RETAIL_ProductOptionDef.opt4.name));
				}
				loopContent.put("running.no", String.valueOf(i+1));				
				loopContent.put("options", optionsBuffer.toString());
				_mc.setLoopElementContent(loopContent);
				
				subtotal = subtotal.add(new BigDecimal((String) loopContent.get(RETAIL_CartDetailDef.sellsubtotal.name)));
				totalWeight += NumberUtil.parseInt((String) loopContent.get(RETAIL_ProductOptionDef.weight.name));
			}						
			String ipCountry = new IPUtil().getCountry(_dbConnection, _req.getRemoteAddr());
			StringBuffer countryBuffer = SelectBuilder.getSELECT_COUNTRY(_dbConnection, _shopInfoBean, RETAIL_MemberDef.country.name, ipCountry);
			
			// Get Main
			query = new StringBuffer("Select ");
			query.append(RETAIL_CartDef.shippingcharge);
			query.append(" From ").append(RETAIL_CartDef.TABLE);
			query.append(" Where ").append(RETAIL_CartDef.cartid).append("=?");
			
			loopContent = _gs.searchDataMap(query, paramMap);
			if(loopContent==null){
				loopContent = new HashMap();
			}

			loopContent.put("total.items", String.valueOf(count));
			loopContent.put("subtotal", subtotal.toString());
			loopContent.put("total.weight", String.valueOf(totalWeight));								
			loopContent.put(RETAIL_MemberDef.country.name, countryBuffer.toString());
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
	
	private void updatePriceInfo(String cartId){
		ArrayList dataList = null;
		HashMap dataMap = null;
		int count = 0;
		StringBuffer query = new StringBuffer();
		StringBuffer queryUpdate = new StringBuffer();
		
		PreparedStatement pstmt = null;		
		
		queryUpdate.append("Update ");
		queryUpdate.append(RETAIL_CartDetailDef.TABLE);
		queryUpdate.append(" Set ");
		queryUpdate.append(RETAIL_CartDetailDef.costprice).append("=?, ");
		queryUpdate.append(RETAIL_CartDetailDef.sellprice).append("=?, ");
		queryUpdate.append(RETAIL_CartDetailDef.costsubtotal).append("=?, ");
		queryUpdate.append(RETAIL_CartDetailDef.sellsubtotal).append("=?");
		queryUpdate.append(" Where ");
		queryUpdate.append(RETAIL_CartDetailDef.cartdetailid).append("=?");				
		
		// Select all the cart item where sellsubtotal is null
		query.append("Select ");
		query.append("a.").append(RETAIL_CartDef.companyid);
		query.append(",b.").append(RETAIL_CartDetailDef.cartdetailid);
		query.append(",b.").append(RETAIL_CartDetailDef.qty).append(" * c.").append(RETAIL_ProductOptionDef.costprice).append(" As ").append(RETAIL_CartDetailDef.costsubtotal);
		query.append(",b.").append(RETAIL_CartDetailDef.qty).append(" * c.").append(RETAIL_ProductOptionDef.sellprice).append(" As ").append(RETAIL_CartDetailDef.sellsubtotal);
		query.append(",c.").append(RETAIL_ProductOptionDef.costprice);
		query.append(",c.").append(RETAIL_ProductOptionDef.sellprice);
		query.append(" From ");
		query.append(RETAIL_CartDef.TABLE).append(" a");
		
		query.append(" Inner Join ").append(RETAIL_CartDetailDef.TABLE).append(" b On");
		query.append(" b.").append(RETAIL_CartDetailDef.cartid).append(" = a.").append(RETAIL_CartDef.cartid);
		
		query.append(" Inner Join ").append(RETAIL_ProductOptionDef.TABLE).append(" c On");
		query.append(" c.").append(RETAIL_ProductOptionDef.optid).append(" = b.").append(RETAIL_CartDetailDef.optid);
		
		query.append(" Where a.").append(RETAIL_CartDef.cartid).append("=").append(cartId);
		query.append(" And b.").append(RETAIL_CartDetailDef.sellsubtotal).append(" Is Null");
		
		try{
			pstmt = _dbConnection.prepareStatement(queryUpdate.toString());
			
			dataList = _gs.searchDataArray(query);
			count = dataList.size();
			for(int i=0; i<count; i++){
				dataMap = (HashMap) dataList.get(i);
				pstmt.setString(1, (String) dataMap.get(RETAIL_ProductOptionDef.costprice.name));
				pstmt.setString(2, (String) dataMap.get(RETAIL_ProductOptionDef.sellprice.name));
				pstmt.setString(3, (String) dataMap.get(RETAIL_CartDetailDef.costsubtotal.name));
				pstmt.setString(4, (String) dataMap.get(RETAIL_CartDetailDef.sellsubtotal.name));
				pstmt.setString(5, (String) dataMap.get(RETAIL_CartDetailDef.cartdetailid.name));
				pstmt.executeUpdate();
			}
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			DBUtil.free(null, pstmt, null);
		}
	}
	
	public JSONArray requestJsonArrayHandler()throws Exception{	
		JSONArray jsonArray = new JSONArray();
		StringBuffer mainQuery = new StringBuffer();
		mainQuery.append("Select ");
		mainQuery.append(ShipmentDomesticDef.state);
		mainQuery.append(" From ").append(ShipmentDomesticDef.TABLE);
		mainQuery.append(" Where ").append(ShipmentDomesticDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
		mainQuery.append(" And ").append(ShipmentDomesticDef.status).append("!='").append(GeneralConst.DELETED).append("'");				
		
		
		ArrayList arrayListAutoSuggest = (ArrayList) _gs.searchDataArray(mainQuery);

		HashMap dataRow = null;

		for(int i=0; i<arrayListAutoSuggest.size(); i++){			
			dataRow = (HashMap)arrayListAutoSuggest.get(i);							
			jsonArray.put((String) dataRow.get(ShipmentDomesticDef.state.name));
		}				
		
		
		return jsonArray;
	}
	
	public JSONObject getDomesticShipment() {
		StringBuffer query = new StringBuffer();
		HashMap dataMap = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		String state = _req.getParameter(ShipmentDomesticDef.state.name);
		String weight = _req.getParameter("weight");
		try {
			query.append("Select ");
			query.append("a.").append(ShipmentDomesticDef.numberofday);
			query.append(",b.").append(ShipmentDomesticDetailDef.rate);
			query.append(" From ").append(ShipmentDomesticDef.TABLE).append(" a");
			query.append(" Inner Join ").append(ShipmentDomesticDetailDef.TABLE).append(" b On");
			query.append(" b.").append(ShipmentDomesticDetailDef.sid).append("=a.").append(ShipmentDomesticDef.sid);
			query.append(" Where a.").append(ShipmentDomesticDef.companyid).append("=?");
			query.append(" And a.").append(ShipmentDomesticDef.state).append("=?");
			query.append(" And b.").append(ShipmentDomesticDetailDef.weightbelow).append(" >= ?");
			
			paramMap.put(ShipmentDomesticDef.companyid, _shopInfoBean.getShopName());
			paramMap.put(ShipmentDomesticDef.state, state);
			paramMap.put(ShipmentDomesticDetailDef.weightbelow, weight);
			dataMap = _gs.searchDataMap(query, paramMap);
			
			if(dataMap!=null){
				dataMap.put("rc", "0000");
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return new JSONObject(dataMap);
	}
	
	public JSONObject getInternationalShipment() {
		StringBuffer query = new StringBuffer();
		HashMap dataMap = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		String country = _req.getParameter("country");
		String weight = _req.getParameter("weight");
		try {
			query.append("Select ");
			query.append("a.").append(ShipmentInternationalDef.numberofday);
			query.append(",b.").append(ShipmentInternationalDetailDef.rate);
			query.append(" From ").append(ShipmentInternationalDef.TABLE).append(" a");
			query.append(" Inner Join ").append(ShipmentInternationalDetailDef.TABLE).append(" b On");
			query.append(" b.").append(ShipmentInternationalDetailDef.sid).append("=a.").append(ShipmentInternationalDef.sid);
			query.append(" Where a.").append(ShipmentInternationalDef.companyid).append("=?");
			query.append(" And a.").append(ShipmentInternationalDef.tocountryiso).append("=?");
			query.append(" And b.").append(ShipmentInternationalDetailDef.weightbelow).append(" >= ?");
			
			paramMap.put(ShipmentInternationalDef.companyid, _shopInfoBean.getShopName());
			paramMap.put(ShipmentInternationalDef.tocountryiso, country);
			paramMap.put(ShipmentInternationalDetailDef.weightbelow, weight);
			dataMap = _gs.searchDataMap(query, paramMap);
			
			if(dataMap!=null){
				dataMap.put("rc", "0000");
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return new JSONObject(dataMap);
	}
}
