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
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CookiesUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.beandef.MFG_CartDetailDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;
import com.forest.share.beandef.ShipmentDomesticDef;
import com.forest.share.beandef.ShipmentDomesticDetailDef;
import com.forest.share.webbuilder.BaseWebBuilder;

public class MFG_PaymentWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger(MFG_PaymentWebBuilder.class);

	public MFG_PaymentWebBuilder(ShopInfoBean shopInfoBean, ClientBean clientBean,
			Connection conn, HttpServletRequest req, HttpServletResponse resp,
			Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);

	}

	public Object requestJsonHandler() throws Exception {
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);

		if (!CommonUtil.isEmpty(reqAction)) {
			if (GeneralConst.GET_SHIPMENT.equals(reqAction)) {
				json.put("data", getDomesticShipment());
			} else if (GeneralConst.CREATE.equals(reqAction)) {
				ArrayList addArray = null;
				HashMap attrReqDataMap = null;
				HashMap countryMap = SelectBuilder.getHASH_COUNTRY(_dbConnection, _shopInfoBean);
				StringBuffer deliveryBuffer = new StringBuffer();
				
				Class defClass = MFG_TransactionDef.class;
				
				addArray = BeanDefUtil.getArrayList(defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
				attrReqDataMap = BuilderUtil.requestValueToDataObject(_req,	addArray, _shopInfoBean.getShopName())[0];
				
//				deliveryBuffer.append("Order Comment: \n");
//				deliveryBuffer.append(CommonUtil.nullToEmpty((String) attrReqDataMap.get(MFG_TransactionDef.comment.name)));
//				deliveryBuffer.append("\nDeliver To: \n");
//				deliveryBuffer.append(_req.getParameter("address")).append(",\n");
//				deliveryBuffer.append(_req.getParameter("city")).append(",\n");
//				deliveryBuffer.append(_req.getParameter("postcode")).append(",\n");
//				deliveryBuffer.append(_req.getParameter("state")).append(",\n");
//				deliveryBuffer.append((String)countryMap.get(_req.getParameter("country")));
				
//				attrReqDataMap.put(MFG_TransactionDef.comment.name, deliveryBuffer.toString());
				attrReqDataMap.put(MFG_TransactionDef.status.name, MFG_SelectBuilder.TRANS_ORDER_CONFIRM);
				attrReqDataMap.put(MFG_TransactionDef.terms.name, "0");
				attrReqDataMap.put(MFG_TransactionDef.clientid.name, _clientBean.getLoginUserId());
				attrReqDataMap.put(MFG_TransactionDef.salesdate.name, DateUtil.getCurrentDate());				
				attrReqDataMap.put(MFG_TransactionDef.custtype.name, _clientBean.getUserGroup());
				
				int key = _gs.create(defClass, addArray, attrReqDataMap);				
				saveDetail(String.valueOf(key));
				json.put("rc", "0000");
				json.put("transid", String.valueOf(key));
				// Payment
				if(GeneralConst.PAY_CREDIT_CARD.equals(_req.getParameter("payment_method"))){
					json.put("currency", _shopInfoBean.getPaymentCurrency());
					json.put("business", _shopInfoBean.getPaymentCurrency());
				}								
			}
		}

		return json;
	}

	public void displayHandler(String moduleId) throws Exception {
		_mc.initModuleContent(_moduleEle);
		putMainData();
	}

	private void putMainData() {
		HashMap loopContent = new HashMap();

		try {
			StringBuffer countryBuffer = SelectBuilder.getSELECT_COUNTRY(_dbConnection, _shopInfoBean, ClientUserBeanDef.country.name, "");
			StringBuffer titleBuffer = SelectBuilder.getSELECT_TITLE(_dbConnection, _shopInfoBean, ClientUserBeanDef.title.name, "");
			loopContent.put(ClientUserBeanDef.country.name, countryBuffer.toString());
			loopContent.put(ClientUserBeanDef.title.name, titleBuffer.toString());
			_mc.setElementContent(loopContent);
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	private void saveDetail(String transId) {
		CookiesUtil cu = new CookiesUtil(_req, _resp);
		String cartId = cu.getCartID();
		StringBuffer query = new StringBuffer();
		LinkedHashMap paramMap = new LinkedHashMap();
		query.append(" Insert Into ").append(MFG_TransactionDetailDef.TABLE).append("(");
		query.append(MFG_TransactionDetailDef.transid);
		query.append(",").append(MFG_TransactionDetailDef.prodid);
		query.append(",").append(MFG_TransactionDetailDef.measurement);
		query.append(",").append(MFG_TransactionDetailDef.width);
		query.append(",").append(MFG_TransactionDetailDef.height);
		query.append(",").append(MFG_TransactionDetailDef.depth);
		query.append(",").append(MFG_TransactionDetailDef.qty);
		query.append(",").append(MFG_TransactionDetailDef.unit);
		query.append(",").append(MFG_TransactionDetailDef.cost);
		query.append(",").append(MFG_TransactionDetailDef.price);
		query.append(",").append(MFG_TransactionDetailDef.discount);
		query.append(",").append(MFG_TransactionDetailDef.costsubtotal);
		query.append(",").append(MFG_TransactionDetailDef.sellsubtotal);
		query.append(",").append(MFG_TransactionDetailDef.opt1);
		query.append(",").append(MFG_TransactionDetailDef.opt2);
		query.append(",").append(MFG_TransactionDetailDef.opt3);
		query.append(",").append(MFG_TransactionDetailDef.opt4);
		query.append(",").append(MFG_TransactionDetailDef.customisedata);
		query.append(",").append(MFG_TransactionDetailDef.remark);
		query.append(",").append(MFG_TransactionDetailDef.status);
		query.append(")");
		query.append(" Select ");
		query.append(transId);
		query.append(",").append(MFG_CartDetailDef.prodid);
		query.append(",").append(MFG_CartDetailDef.measurement);
		query.append(",").append(MFG_CartDetailDef.width);
		query.append(",").append(MFG_CartDetailDef.height);
		query.append(",").append(MFG_CartDetailDef.depth);
		query.append(",").append(MFG_CartDetailDef.qty);
		query.append(",").append(MFG_CartDetailDef.unit);
		query.append(",").append(MFG_CartDetailDef.cost);
		query.append(",").append(MFG_CartDetailDef.price);
		query.append(",").append(MFG_CartDetailDef.discount);
		query.append(",").append(MFG_CartDetailDef.costsubtotal);
		query.append(",").append(MFG_CartDetailDef.sellsubtotal);
		query.append(",").append(MFG_CartDetailDef.opt1);
		query.append(",").append(MFG_CartDetailDef.opt2);
		query.append(",").append(MFG_CartDetailDef.opt3);
		query.append(",").append(MFG_CartDetailDef.opt4);
		query.append(",").append(MFG_CartDetailDef.customisedata);
		query.append(",").append(MFG_CartDetailDef.remark);
		query.append(",'").append(GeneralConst.ACTIVE).append("'");
		query.append(" From ").append(MFG_CartDetailDef.TABLE);
		query.append(" Where ").append(MFG_CartDetailDef.cartid).append("=?");
		query.append(" Order By ").append(MFG_CartDetailDef.cartdetailid);
		paramMap.put(MFG_CartDetailDef.cartid, cartId);
		try{
			_gs.executeQuery(query, paramMap);
		}catch (Exception e) {
			logger.error(e, e);
		}
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
}
