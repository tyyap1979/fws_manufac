package com.forest.share.webbuilder;

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
import com.forest.common.util.SelectBuilder;


public class UserProfileWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (UserProfileWebBuilder.class);
	public UserProfileWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		
		if(GeneralConst.UPDATE.equals(reqAction)){
			json = update();
		}else if("change".equals(reqAction)){
			json = changePassword();
		}
		
		return json;
	}
	
	public JSONObject update(){	
		JSONObject json = new JSONObject();
		ArrayList addArray = null;
		Class defClass = ClientUserBeanDef.class;
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
		String newPassword = _req.getParameter(ClientUserBeanDef.password.name);
		try{						
			if(!CommonUtil.isEmpty (oldPassword) && !CommonUtil.isEmpty (newPassword)){
				oldPassword = CryptUtil.getInstance ().encrypt (oldPassword);
				newPassword = CryptUtil.getInstance ().encrypt (newPassword);
			}
			query.append ("Update ").append (ClientUserBeanDef.TABLE).append (" Set ");
			query.append (ClientUserBeanDef.password).append (" = ? ");
			query.append ("Where ");
			query.append (ClientUserBeanDef.cid).append (" = ? And ");
			query.append (ClientUserBeanDef.password).append (" = ? ");
			
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
	
	public void displayHandler(String moduleId)throws Exception{			
		_mc.initModuleContent(_moduleEle);		
		putMainData();
	}
	
	private void putMainData(){		
		HashMap loopContent = new HashMap();		
		StringBuffer query = new StringBuffer();
		LinkedHashMap paramMap = new LinkedHashMap();
		try{
			query.append("Select *");
			query.append(" From ").append(ClientUserBeanDef.TABLE);
			query.append(" Where ").append(ClientUserBeanDef.cid).append("=?");
			
			paramMap.put(ClientUserBeanDef.cid, _clientBean.getLoginUserId());
			loopContent = _gs.searchDataMap(query, paramMap);
			
			StringBuffer countryBuffer = SelectBuilder.getSELECT_COUNTRY(_dbConnection, _shopInfoBean, ClientUserBeanDef.country.name, (String)loopContent.get(ClientUserBeanDef.country.name));
			StringBuffer titleBuffer = SelectBuilder.getSELECT_TITLE(_dbConnection, _shopInfoBean, ClientUserBeanDef.title.name, (String)loopContent.get(ClientUserBeanDef.title.name));
			loopContent.put(ClientUserBeanDef.country.name, countryBuffer.toString());
			loopContent.put(ClientUserBeanDef.title.name, titleBuffer.toString());
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
}
