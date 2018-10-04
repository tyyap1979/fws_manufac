package com.forest.retail.webbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.forest.common.util.CryptUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.IPUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.retail.beandef.RETAIL_MemberDef;
import com.forest.share.webbuilder.BaseWebBuilder;

public class RETAIL_RegisterWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (RETAIL_RegisterWebBuilder.class);
	public RETAIL_RegisterWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		
		if(GeneralConst.CREATE.equals(reqAction)){
			json = create(reqAction);
		}else if(GeneralConst.UPDATE.equals(reqAction)){
			json = update();
		}else if("change".equals(reqAction)){
			json = changePassword();
		}
		
		return json;
	}
	
	public void displayHandler(String moduleId)throws Exception{			
		_mc.initModuleContent(_moduleEle);		
		if("register.detail".equals(moduleId)){
			putMainData();
		}else if("member.detail".equals(moduleId)){
			putViewMainData();
		}
	}
	
	public JSONObject create(String reqAction){	
		JSONObject json = new JSONObject();
		ArrayList addArray = null;
		Class defClass = RETAIL_MemberDef.class;
		HashMap attrReqDataMap = null;
		String ipCountry = null;
		try{
			addArray = BeanDefUtil.getArrayList(defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, _shopInfoBean.getShopName())[0];
			
			ipCountry = new IPUtil().getCountry(_dbConnection, _req.getRemoteAddr());

			attrReqDataMap.put(RETAIL_MemberDef.status.name, GeneralConst.ACTIVE);
			attrReqDataMap.put(RETAIL_MemberDef.ipcountry.name, ipCountry);
			_gs.create(defClass, addArray, attrReqDataMap);
			
//			SendMail sm = new SendMail(_shopInfoBean, _clientBean, _dbConnection);
//			sm.sendEmail(attrReqDataMap, "MEMBER_REGISTER");
			
			json.put("rc", "0000");
		}catch(Exception e){			
			logger.error(e, e);
		}
		return json;
	}	
	
	public JSONObject update(){	
		JSONObject json = new JSONObject();
		ArrayList addArray = null;
		Class defClass = RETAIL_MemberDef.class;
		HashMap attrReqDataMap = null;
	
		try{
			addArray = BeanDefUtil.getArrayList(defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, _shopInfoBean.getShopName())[0];			
			_gs.update(defClass, addArray, attrReqDataMap);
			
			json.put("rc", "0000");
		}catch(Exception e){			
			logger.error(e, e);
		}
		return json;
	}
	
	public JSONObject changePassword() throws Exception{
		JSONObject json = new JSONObject();
		PreparedStatement pstmt = null;
		int updateValue = 0;
		StringBuffer query = new StringBuffer();		
		String oldPassword = _req.getParameter("oldpassword");
		String newPassword = _req.getParameter(RETAIL_MemberDef.password.name);
		try{						
			if(!CommonUtil.isEmpty (oldPassword) && !CommonUtil.isEmpty (newPassword)){
				oldPassword = CryptUtil.getInstance ().encrypt (oldPassword);
				newPassword = CryptUtil.getInstance ().encrypt (newPassword);
			}
			query.append ("Update ").append (RETAIL_MemberDef.TABLE).append (" Set ");
			query.append (RETAIL_MemberDef.password).append (" = ? ");
			query.append ("Where ");
			query.append (RETAIL_MemberDef.mid).append (" = ? And ");
			query.append (RETAIL_MemberDef.password).append (" = ? ");
			
			pstmt = _dbConnection.prepareStatement (query.toString ());
			pstmt.setString (1, newPassword);			
			pstmt.setString (2, _clientBean.getLoginUserId());
			pstmt.setString (3, oldPassword);
			
			updateValue = pstmt.executeUpdate ();
			if(updateValue>0){
				json.put("rc", "0000");
			}else{
				json.put("rc", "9999");
				json.put("rd", "Wrong Password");
			}
		}catch(Exception e){
			throw e;
		}finally{
			DBUtil.free(null, pstmt, null);
		}		
		
		return json;
	}	
	
	
	private void putMainData(){		
		HashMap loopContent = new HashMap();		
		String ipCountry = new IPUtil().getCountry(_dbConnection, _req.getRemoteAddr());
		try{
			if(CommonUtil.isEmpty(ipCountry)) ipCountry = "MY";
			StringBuffer countryBuffer = SelectBuilder.getSELECT_COUNTRY(_dbConnection, _shopInfoBean, RETAIL_MemberDef.country.name, ipCountry);			
			loopContent.put(RETAIL_MemberDef.country.name, countryBuffer.toString());			
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}		
	
	private void putViewMainData(){		
		HashMap loopContent = new HashMap();		
		StringBuffer query = new StringBuffer();
		LinkedHashMap paramMap = new LinkedHashMap();
		try{
			query.append("Select *");
			query.append(" From ").append(RETAIL_MemberDef.TABLE);
			query.append(" Where ").append(RETAIL_MemberDef.mid).append("=?");
			
			paramMap.put(RETAIL_MemberDef.mid, _clientBean.getLoginUserId());
			loopContent = _gs.searchDataMap(query, paramMap);
			
			StringBuffer countryBuffer = SelectBuilder.getSELECT_COUNTRY(_dbConnection, _shopInfoBean, ClientUserBeanDef.country.name, (String)loopContent.get(ClientUserBeanDef.country.name));			
			loopContent.put(ClientUserBeanDef.country.name, countryBuffer.toString());			
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
}
