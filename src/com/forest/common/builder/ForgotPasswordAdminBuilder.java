package com.forest.common.builder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

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
import com.forest.common.util.SendMail;

public class ForgotPasswordAdminBuilder extends GenericAdminBuilder{
	private Logger logger = Logger.getLogger(this.getClass());
	
	public ForgotPasswordAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);	
	} 
	
	public StringBuffer requestHandler()throws Exception{		
		logger.debug("-------------------- requestHandler: "+_reqAction);
		StringBuffer buffer = new StringBuffer();		
		try{			
			if(GeneralConst.VALIDATE.equals (_reqAction)){
				if(validateEmail(_req)){
					_resp.sendRedirect("login.htm");
				}else{					
					buffer = displayHandler();
				}
			}else if(GeneralConst.SIGNOFF.equals (_reqAction)){
				_req.getSession(false).invalidate();
				_resp.sendRedirect("login.htm");				
			}else if(GeneralConst.FORGOT_PASSWORD.equals(_reqAction)){
				
			}
		}catch(Exception e){
			throw new OwnException(e);
		}
		
		return buffer;
	}

	public boolean validateEmail(HttpServletRequest req)throws Exception{
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		HashMap data = null;
		HashMap authMap = new HashMap();
		boolean valid = false;
		ArrayList searchDisplayList = new ArrayList();
		ArrayList searchList = new ArrayList();
		
		HashMap listMap = new HashMap();
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.email);
		searchDisplayList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.password);
		searchDisplayList.add(listMap);
		
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.userid);		
		searchList.add(listMap);
		
		listMap = new HashMap();
		listMap.put("object", AdminUserBeanDef.email);		
		searchList.add(listMap);
		
		String userid = req.getParameter(AdminUserBeanDef.userid.name);
		String email = req.getParameter(AdminUserBeanDef.email.name);
		
		if(!CommonUtil.isEmpty(userid) && !CommonUtil.isEmpty(email)){
			HashMap[] attrReqDataMap = BuilderUtil.requestValueToDataObject(req, searchList, _shopInfoBean.getShopName());
			ArrayList arrayListSearch = gs.search(
					_defClass, null, 
					searchList, searchDisplayList, attrReqDataMap, null, "1", GeneralConst.EDIT);
			
			ArrayList resultArray = (ArrayList) arrayListSearch.get(1);
			if(resultArray.size()>0){
				logger.debug("--- Forgot Password Valid ---");
				SendMail sm = new SendMail(_shopInfoBean, _clientBean, _dbConnection);
				sm.sendEmail(data, "FORGOT_PASSWORD");
//				EmailSender emailSender = new EmailSender(_shopInfoBean, _clientBean, _dbConnection);
//				emailSender.sendAdminForgotPassword (userid, email,  (String) data.get(AdminUserBeanDef.password), _clientBean.getLocale ());				
				valid = true;
				req.setAttribute("rd", ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "8002", _clientBean.getLocale ()));
			}else{
				req.setAttribute("rd", ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "9002", _clientBean.getLocale ()));
				logger.debug("--- Login inValid ---");				
			}
		}
		
		return valid;
	}
	
	public StringBuffer displayHandler()throws Exception{
		logger.debug("-------------------- displayHandler -------------------- ");
		ArrayList addList = new ArrayList();
		HashMap listMap = new HashMap();
    	listMap.put("object", AdminUserBeanDef.userid);
    	listMap.put("size", "20");
    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));
    	addList.add(listMap);
    	
    	listMap = new HashMap();
    	listMap.put("object", AdminUserBeanDef.email);
    	listMap.put("size", "20");
    	listMap.put("type", String.valueOf(ComponentConst.TEXTBOX));   
    	addList.add(listMap);
    	    	
		String content = FileHandler.readAdminFile (_shopInfoBean, _clientBean).toString();
		content = content.replaceAll("admin.forgot.password.content", buildForm(
					(String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME), 
					addList).toString());

		return new StringBuffer(content);
	}
	
	private StringBuffer buildForm(String MODULE_NAME, ArrayList addList) throws OwnException{
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
	    	mBuffer.append ("<table width='300px'>");	    	
		    	mBuffer.append ("<col width='150'>");
		    	mBuffer.append ("<col width='10'>");
		    	mBuffer.append ("<col width='200'>");	
	    	if(!CommonUtil.isEmpty(msg)){
		    	mBuffer.append("<tr><th colspan='3'>").append(msg).append("</th></tr>");
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
			    		valueBuffer.append ("<td class='paramName' nowrap>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, dataObject.name, _clientBean.getLocale ())).append ("</td>");
			    		valueBuffer.append ("<td>").append (":").append ("</td>");			    	
			    		valueBuffer.append ("<td>");
		    				valueBuffer.append (AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, null, null, GeneralConst.ENTRY_FORM));		    				
		    			valueBuffer.append ("</td>");
	    			valueBuffer.append ("</tr>");
	    			
	    			mBuffer.append (valueBuffer);
	    		}   		
	    	}
	    	
	    	mBuffer.append ("<tr>");
	    		mBuffer.append ("<td colspan='2'>");
	    			mBuffer.append (fieldBuilder.buildLink ("link", "forgotpassword.htm", null, ResourceUtil.getAdminResourceValue (_shopInfoBean.getBusiness(), AdminUserBeanDef.MODULE_NAME, "forgot.password", _clientBean.getLocale ())));
	    		mBuffer.append ("</td>");
				mBuffer.append ("<td>");
					mBuffer.append (fieldBuilder.buildFormObject ("submit", "btnLogin", "login", 
							ResourceUtil.getAdminResourceValue (_shopInfoBean.getBusiness(), AdminUserBeanDef.MODULE_NAME, "reset.password", _clientBean.getLocale ()), null, "submit"));		    				    		    			    				
	    		mBuffer.append ("</td>");
	    	mBuffer.append ("</tr>");
	    	mBuffer.append ("<tr>");
	    		mBuffer.append ("<td colspan='3'>");
	    			mBuffer.append (AdminFieldBuilder.getActionField(GeneralConst.FORGOT_PASSWORD));	    			
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
