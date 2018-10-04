package com.forest.share.webbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.forest.common.util.IPUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.common.util.SendMail;

public class RegisterWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (RegisterWebBuilder.class);
	public RegisterWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		
		if(!CommonUtil.isEmpty(reqAction)){
			json = create(reqAction);
		}
		
		return json;
	}
	
	public JSONObject create(String reqAction){	
		JSONObject json = new JSONObject();
		ArrayList addArray = null;
		Class defClass = ClientUserBeanDef.class;
		HashMap attrReqDataMap = null;
		String ipCountry = null;
		try{
			addArray = BeanDefUtil.getArrayList(defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, _shopInfoBean.getShopName())[0];
			
			ipCountry = new IPUtil().getCountry(_dbConnection, _req.getRemoteAddr());
			if(!"MY".equals(ipCountry) && "D".equals((String) attrReqDataMap.get(ClientUserBeanDef.type.name))){
				attrReqDataMap.put(ClientUserBeanDef.type.name, "C");
			}
			
			attrReqDataMap.put(ClientUserBeanDef.status.name, GeneralConst.SUSPENDED);
			attrReqDataMap.put(ClientUserBeanDef.ipcountry.name, ipCountry);
			_gs.create(defClass, addArray, attrReqDataMap);
			
			SendMail sm = new SendMail(_shopInfoBean, _clientBean, _dbConnection);
			sm.sendEmail(attrReqDataMap, "MEMBER_REGISTER");
			
			json.put("rc", "0000");
		}catch(Exception e){			
			logger.error(e, e);
		}
		return json;
	}
		
	
	public void displayHandler(String moduleId)throws Exception{			
		_mc.initModuleContent(_moduleEle);		
		putMainData();
	}
	
	private void putMainData(){		
		HashMap loopContent = new HashMap();		
		
		try{
			StringBuffer countryBuffer = SelectBuilder.getSELECT_COUNTRY(_dbConnection, _shopInfoBean, ClientUserBeanDef.country.name, "");
			StringBuffer titleBuffer = SelectBuilder.getSELECT_TITLE(_dbConnection, _shopInfoBean, ClientUserBeanDef.title.name, "");
			loopContent.put(ClientUserBeanDef.country.name, countryBuffer.toString());
			loopContent.put(ClientUserBeanDef.title.name, titleBuffer.toString());
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}		
}
