package com.forest.share.webbuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.CurrencyBeanDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CookiesUtil;
import com.forest.common.util.NumberUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SelectBuilder;

public class CurrencyWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (CurrencyWebBuilder.class);
	public CurrencyWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{		
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
				
		if("chgcurrency".equals(reqAction)){
			setCurrency();
		}
		return json;
	}
	
	public void displayHandler(String moduleId)throws Exception{		
		_mc.initModuleContent(_moduleEle);
		
		if("currency.panel".equals(moduleId)){
			buildPanel();
		}
		
//		if("currency.info".equals(moduleId)){
//			buildInfo();
//		}
	}
	
	private void putMainData(){
		
	}
	
//	private void buildInfo(){
//		HashMap loopContent = new HashMap();
//		try{
//			loopContent.put("currency.rate", NumberUtil.getCurrencyRate(_dbConnection, _shopInfoBean, _shopInfoBean.getPaymentCurrency(), _clientBean.getSelectedCurrency()).toString());			
//			_mc.setElementContent(loopContent);
//		}catch(Exception e){
//			logger.error(e, e);
//		}
//	}
	
	private void buildPanel(){
		BigDecimal convertedValue = null;
		HashMap currencyMap = null;
		HashMap loopContent = new HashMap();
		String[] currencyArray = null;
		try{			
			convertedValue = NumberUtil.convertCurrency (_dbConnection, _shopInfoBean, _shopInfoBean.getPaymentCurrency(), _clientBean.getSelectedCurrency (), "1");
			currencyMap = SelectBuilder.getHASH_CURRENCY(_dbConnection, _shopInfoBean);
			currencyArray = (String[]) currencyMap.get(_clientBean.getSelectedCurrency ());
			
			loopContent.put("converted.value", convertedValue.setScale (2).toString());
			loopContent.put("default.currency", _shopInfoBean.getPaymentCurrency ());
			loopContent.put("selected.currency", _clientBean.getSelectedCurrency ());
			loopContent.put("last.update", (String) currencyArray[2]);
			
//			logger.debug("loopContent = "+loopContent);
			loopContent.put("currency.select", SelectBuilder.getSELECT_CURRENCY(_dbConnection, _shopInfoBean, CurrencyBeanDef.currencycode.name, _clientBean.getSelectedCurrency()).toString());			
			if(_shopInfoBean.getPaymentCurrency ().equals(_clientBean.getSelectedCurrency ())){
				loopContent.put("reference.style", "display: none");
			}
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error (e, e);
		}
//		return mBuffer;
	}
	
	private void setCurrency(){
		String changeCurrency = _req.getParameter ("tocurrency");
		CookiesUtil cu = new CookiesUtil (_req, _resp);		
		cu.setCurrency (changeCurrency);
	}
}
