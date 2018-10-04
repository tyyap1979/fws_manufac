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
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CookiesUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.retail.beandef.RETAIL_CartDetailDef;
import com.forest.retail.beandef.RETAIL_TransactionDef;
import com.forest.retail.beandef.RETAIL_TransactionDetailDef;
import com.forest.share.webbuilder.BaseWebBuilder;

public class RETAIL_PaymentWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger(RETAIL_PaymentWebBuilder.class);

	public RETAIL_PaymentWebBuilder(ShopInfoBean shopInfoBean, ClientBean clientBean,
			Connection conn, HttpServletRequest req, HttpServletResponse resp,
			Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);

	}

	public Object requestJsonHandler() throws Exception {
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);

		if (!CommonUtil.isEmpty(reqAction)) {
			if (GeneralConst.CREATE.equals(reqAction)) {
				ArrayList addArray = null;
				HashMap attrReqDataMap = null;
				Class defClass = RETAIL_TransactionDef.class;
				
				addArray = BeanDefUtil.getArrayList(defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
				attrReqDataMap = BuilderUtil.requestValueToDataObject(_req,	addArray, _shopInfoBean.getShopName())[0];

				attrReqDataMap.put(RETAIL_TransactionDef.status.name, GeneralConst.TRANS_PENDING_PAYMENT);
				attrReqDataMap.put(RETAIL_TransactionDef.clientid.name, _clientBean.getLoginUserId());
				attrReqDataMap.put(RETAIL_TransactionDef.salesdate.name, DateUtil.getCurrentDate());				
				
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
			loopContent.put(ClientUserBeanDef.country.name, countryBuffer.toString());
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
		query.append(" Insert Into ").append(RETAIL_TransactionDetailDef.TABLE).append("(");
		query.append(RETAIL_TransactionDetailDef.transid);
		query.append(",").append(RETAIL_TransactionDetailDef.prodid);
		query.append(",").append(RETAIL_TransactionDetailDef.optid);
		query.append(",").append(RETAIL_TransactionDetailDef.qty);
		query.append(",").append(RETAIL_TransactionDetailDef.costprice);
		query.append(",").append(RETAIL_TransactionDetailDef.sellprice);
		query.append(",").append(RETAIL_TransactionDetailDef.discount);
		query.append(",").append(RETAIL_TransactionDetailDef.costsubtotal);
		query.append(",").append(RETAIL_TransactionDetailDef.sellsubtotal);
		query.append(",").append(RETAIL_TransactionDetailDef.remark);		
		query.append(")");
		query.append(" Select ");
		query.append(transId);
		query.append(",").append(RETAIL_CartDetailDef.prodid);
		query.append(",").append(RETAIL_CartDetailDef.optid);
		query.append(",").append(RETAIL_CartDetailDef.qty);
		query.append(",").append(RETAIL_CartDetailDef.costprice);
		query.append(",").append(RETAIL_CartDetailDef.sellprice);
		query.append(",").append(RETAIL_CartDetailDef.discount);
		query.append(",").append(RETAIL_CartDetailDef.costsubtotal);
		query.append(",").append(RETAIL_CartDetailDef.sellsubtotal);
		query.append(",").append(RETAIL_CartDetailDef.remark);		
		query.append(" From ").append(RETAIL_CartDetailDef.TABLE);
		query.append(" Where ").append(RETAIL_CartDetailDef.cartid).append("=?");
		
		paramMap.put(RETAIL_CartDetailDef.cartid, cartId);
		try{
			_gs.executeQuery(query, paramMap);
		}catch (Exception e) {
			logger.error(e, e);
		}
	}

	
}
