package com.forest.common.builder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.AdminUserBeanDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SessionConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.FieldBuilder;
import com.forest.common.util.FileHandler;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;

public class LoginAdminBuilder extends GenericAdminBuilder{
	private Logger logger = Logger.getLogger(this.getClass());
	
	public LoginAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);	
	} 
	
	public JSONObject requestJsonHandler()throws Exception{
		JSONObject json = new JSONObject();
		
		if(GeneralConst.VALIDATE.equals (_reqAction)){
			if(validateAdmin(_req)){
				json.put("redirect", "dashboard.htm");				
			}else{
				json.put("rc", "9");
				json.put("rd", "Invalid Login.");
			}
		}else if("authorization".equals (_reqAction)){
			if(validateAuthorization(_req)){
				json.put("rc", "0");				
			}else{
				json.put("rc", "9");
				json.put("rd", "Invalid Login.");
			}
		}
		
		return json;
	}
	
	public StringBuffer requestHandler()throws Exception{		
		logger.debug("-------------------- requestHandler: "+_reqAction);
		StringBuffer buffer = new StringBuffer();		
		try{						
			if(GeneralConst.VALIDATE.equals (_reqAction)){
				if(validateAdmin(_req)){
					_resp.sendRedirect("dashboard.htm");
				}else{					
					buffer = displayHandler();
				}
			}
		}catch(Exception e){
			throw new OwnException(e);
		}
		
		return buffer;
	}

	public boolean validateAdmin(HttpServletRequest req){
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		HashMap data = null;
		HashMap authMap = new HashMap();
		boolean valid = false;
		ArrayList searchDisplayList = new ArrayList();
		ArrayList searchList = new ArrayList();
		
		HashMap listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.cookieskey);
		searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.name);
		searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.id);
		searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.usergroup);
		searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.userid);
		searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.password);
		listMap.put("type", String.valueOf(ComponentConst.PASSWORD)); 
		searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.companyid);
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN)); 
		searchList.add(listMap);
		
		String userid = req.getParameter(AdminUserBeanDef.userid.name);
		String passwd = req.getParameter(AdminUserBeanDef.password.name);
		
		if(!CommonUtil.isEmpty(userid) && !CommonUtil.isEmpty(passwd)){
			HashMap[] attrReqDataMap = null;
			if(CommonUtil.isEmpty(_shopInfoBean.getCompanyGroup())){
				attrReqDataMap = BuilderUtil.requestValueToDataObject(req, searchList, _shopInfoBean.getShopName());
				attrReqDataMap[0].put(AdminUserBeanDef.companyid.name, _shopInfoBean.getShopName());
			}else{
				attrReqDataMap = BuilderUtil.requestValueToDataObject(req, searchList, _shopInfoBean.getCompanyGroup());
				attrReqDataMap[0].put(AdminUserBeanDef.companyid.name, _shopInfoBean.getCompanyGroup());
			}
			ArrayList arrayListSearch = gs.search(
					_defClass, null, 
					searchList, searchDisplayList, attrReqDataMap, null, "1", GeneralConst.EDIT);
			
			ArrayList resultArray = (ArrayList) arrayListSearch.get(1);
			if(resultArray.size()>0){
				
				data = (HashMap) resultArray.get(0);
				authMap.put (SessionConst.USER_GROUP, data.get(AdminUserBeanDef.usergroup.name));
				authMap.put (SessionConst.USER_NAME, data.get(AdminUserBeanDef.name.name));
				authMap.put (SessionConst.USER_ID, data.get(AdminUserBeanDef.id.name));
				if(CommonUtil.isEmpty(_shopInfoBean.getCompanyGroup())){
					authMap.put (SessionConst.AUTHRIZE_SHOP_NAME, _shopInfoBean.getShopName ());
					logger.debug("LOGIN: Session "+SessionConst.SESSION_ADMIN+_shopInfoBean.getShopName ());
					req.getSession().setAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getShopName (), authMap);
				}else{
					logger.debug("LOGIN: Session "+SessionConst.SESSION_ADMIN+_shopInfoBean.getCompanyGroup ());
					authMap.put (SessionConst.AUTHRIZE_SHOP_NAME, _shopInfoBean.getCompanyGroup ());
					req.getSession().setAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getCompanyGroup (), authMap);
				}
				
							
				valid = true;
			}else{
				logger.debug("--- Login inValid ---");
				req.setAttribute("rd", ResourceUtil.getAdminResourceValue(null,"", "9901", _clientBean.getLocale ()));
				if(CommonUtil.isEmpty(_shopInfoBean.getCompanyGroup())){
					req.getSession().removeAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getShopName());
				}else{
					req.getSession().removeAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getCompanyGroup());	
				}
				
			}
		}
		
		return valid;
	}
	
	public boolean validateAuthorization(HttpServletRequest req){
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		HashMap data = null;		
		boolean valid = false;
		ArrayList searchDisplayList = new ArrayList();
		ArrayList searchList = new ArrayList();
		
		HashMap listMap = new HashMap();
		
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.id);
		searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.userid);
		searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.password);
		listMap.put("type", String.valueOf(ComponentConst.PASSWORD)); 
		searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.usergroup);		
		searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.companyid);
		listMap.put("type", String.valueOf(ComponentConst.HIDDEN)); 
		searchList.add(listMap);
		
		String userid = req.getParameter(AdminUserBeanDef.userid.name);
		String passwd = req.getParameter(AdminUserBeanDef.password.name);
		String usergroup = req.getParameter(AdminUserBeanDef.usergroup.name);
		
		if(!CommonUtil.isEmpty(userid) && !CommonUtil.isEmpty(passwd) && !CommonUtil.isEmpty(usergroup)){
			HashMap[] attrReqDataMap = null;
			if(CommonUtil.isEmpty(_shopInfoBean.getCompanyGroup())){
				attrReqDataMap = BuilderUtil.requestValueToDataObject(req, searchList, _shopInfoBean.getShopName());
				attrReqDataMap[0].put(AdminUserBeanDef.companyid.name, _shopInfoBean.getShopName());
			}else{
				attrReqDataMap = BuilderUtil.requestValueToDataObject(req, searchList, _shopInfoBean.getCompanyGroup());
				attrReqDataMap[0].put(AdminUserBeanDef.companyid.name, _shopInfoBean.getCompanyGroup());
			}			
			
			ArrayList arrayListSearch = gs.search(
					_defClass, null, 
					searchList, searchDisplayList, attrReqDataMap, null, "1", GeneralConst.EDIT);
			
			ArrayList resultArray = (ArrayList) arrayListSearch.get(1);
			if(resultArray.size()>0){				
				data = (HashMap) resultArray.get(0);
				logger.debug("data: "+data);				
				valid = true;
			}
		}
		
		return valid;
	}
	
	public StringBuffer displayHandler()throws Exception{
		if(_clientBean.getRequestFilename().equals("logout.htm")){
			_req.getSession(false).invalidate();
			_resp.sendRedirect("login.htm");				
		}
		logger.debug("-------------------- displayHandler -------------------- ");
		ArrayList addList = new ArrayList();
		HashMap listMap = new HashMap();
    	listMap.put("object", AdminUserBeanDef.userid);
    	listMap.put("size", "20");
    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));
    	addList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", AdminUserBeanDef.password);
    	listMap.put("size", "20");
    	listMap.put("type", String.valueOf(ComponentConst.PASSWORD));   
    	addList.add(listMap);
    	    	
		String content = FileHandler.readAdminFile (_shopInfoBean, _clientBean).toString();
		content = content.replaceAll("admin.login.content", buildLoginForm(
					(String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME), 
					addList).toString());

		return new StringBuffer(content);
	}
	
	private StringBuffer buildLoginForm(String MODULE_NAME, ArrayList addList) throws OwnException{
		FieldBuilder fieldBuilder = new FieldBuilder(_clientBean.getLocale());
		StringBuffer mBuffer = new StringBuffer();
		StringBuffer hiddenBuffer = new StringBuffer();
		String msg = (String) _req.getAttribute("rd");
		HashMap listMap = null;
    	
    	DataObject dataObject = null;    	    	
    	int type = 0;
    	
    	StringBuffer valueBuffer = new StringBuffer();		
	    try{
	    	mBuffer.append ("<form id='").append (GeneralConst.ENTRY_FORM).append ("' method='POST'>");
	    	mBuffer.append ("<table width='300px' class='listTable'>");	    	
		    	mBuffer.append ("<col width='170'>");		    	
		    	mBuffer.append ("<col width='230'>");	
	    	if(!CommonUtil.isEmpty(msg)){
		    	mBuffer.append("<tr><th colspan='2'>").append(msg).append("</th></tr>");
	    	}
	    	for(int i=0; i<addList.size(); i++){
	    		valueBuffer = new StringBuffer();
	    		listMap = (HashMap) addList.get(i);
	    		dataObject 	= (DataObject) listMap.get("object");	    			    		
	    		type 		= Integer.parseInt((String) listMap.get("type"));	    			    	
	    		
	    		if(type==ComponentConst.HIDDEN){
	    			hiddenBuffer.append(AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, null, null, GeneralConst.ENTRY_FORM));
	    		}else{
		    		valueBuffer.append ("<tr>");
			    		valueBuffer.append ("<th>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, dataObject.name, _clientBean.getLocale ())).append ("</th>");
//			    		valueBuffer.append ("<td>").append (":").append ("</td>");			    	
			    		valueBuffer.append ("<td>");
		    				valueBuffer.append (AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, null, null, GeneralConst.ENTRY_FORM));		    				
		    			valueBuffer.append ("</td>");
	    			valueBuffer.append ("</tr>");
	    			
	    			mBuffer.append (valueBuffer);
	    		}   		
	    	}
	    	
	    	mBuffer.append ("<tr>");
	    		mBuffer.append ("<td>");
	    			mBuffer.append (fieldBuilder.buildLink ("link", "forgotpassword.htm", null, ResourceUtil.getAdminResourceValue (null, AdminUserBeanDef.MODULE_NAME, "forgot.password", _clientBean.getLocale ())));
	    		mBuffer.append ("</td>");
				mBuffer.append ("<td>");
					mBuffer.append (fieldBuilder.buildFormObject ("submit", "btnLogin", "login", 
							ResourceUtil.getAdminResourceValue (null, AdminUserBeanDef.MODULE_NAME, "login", _clientBean.getLocale ()), null, "submit"));		    				    		    			    				
	    		mBuffer.append ("</td>");
	    	mBuffer.append ("</tr>");
	    	mBuffer.append ("<tr>");
	    		mBuffer.append ("<td colspan='3'>");
	    			mBuffer.append (AdminFieldBuilder.getActionField(GeneralConst.VALIDATE));	    			
	    		mBuffer.append ("</td>");
	    	mBuffer.append ("</tr>");
	    	mBuffer.append ("</table>");
	    	mBuffer.append(hiddenBuffer);
	    	mBuffer.append ("</form>");
	    }catch(Exception e){
	    	logger.error (e, e);
	    }finally{
	    	
	    }
	    return mBuffer;		
	}
}
