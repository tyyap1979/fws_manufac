package com.forest.share.webbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SessionConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CookiesUtil;
import com.forest.common.util.CryptUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.SendMail;

public class LoginWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (LoginWebBuilder.class);
	private final String SIGNIN = "signin";
	private final String SIGNOUT = "signout";	
	
	private final String CHANGEPASSWORD = "change-password";
	private final String NEWPASSWORD = "new-password";
	private final String CONFIRMPASSWORD = "confirm-password";
	private final String REMEMBERME = "remember-me";
	
	public LoginWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest _req,
			HttpServletResponse _resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, _req, _resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		HashMap map = new HashMap();
		HttpSession session = _req.getSession (false);
		CookiesUtil cookies = new CookiesUtil(_req, _resp);			
		HashMap authMap = null;
		HashMap[] attrReqDataMap = null;
		ArrayList addArray = null;
		String email = _req.getParameter(ClientUserBeanDef.email.name);		
		String password = _req.getParameter (ClientUserBeanDef.password.name);
		if(!CommonUtil.isEmpty (password)){
			password = CryptUtil.getInstance ().encrypt (password);
		}
		
		if(reqAction!=null){
			addArray = BeanDefUtil.getArrayList(ClientUserBeanDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			
			if(reqAction.equalsIgnoreCase ("fbsignin")){
				fbRequestHandler(_req, _resp);
				map.put ("rc", "0");	
									
				if("cart.htm".equals(_clientBean.getRequestFilename())){
					map.put ("redirect", "confirmpay.htm");
				}else{
					map.put ("redirect", "");
				}
			}else if(reqAction.equalsIgnoreCase ("signin")){	
				attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, ClientUserBeanDef._addList, null);				
				attrReqDataMap[0] = getUserInfo(email);
				if(attrReqDataMap[0]!=null && password.equals((String) attrReqDataMap[0].get(ClientUserBeanDef.password.name))){	
					authMap = new HashMap();						
					session = _req.getSession ();											

					authMap.put (SessionConst.USER_ID, (String) attrReqDataMap[0].get (ClientUserBeanDef.cid.name));
					authMap.put (SessionConst.USER_NAME, (String) attrReqDataMap[0].get (ClientUserBeanDef.firstname.name));				
					authMap.put (SessionConst.USER_EMAIL, email);													
					authMap.put (SessionConst.USER_GROUP, (String) attrReqDataMap[0].get (ClientUserBeanDef.type.name));
					session.setAttribute (SessionConst.SESSION_USER, authMap);

					// Update Cookies
					attrReqDataMap[0].put(ClientUserBeanDef.cookieskey.name, CryptUtil.getInstance ().generateSecureCookies ());
					attrReqDataMap[0].remove(ClientUserBeanDef.email.name);
					attrReqDataMap[0].remove(ClientUserBeanDef.firstname.name);
					attrReqDataMap[0].remove(ClientUserBeanDef.password.name);
					attrReqDataMap[0].remove(ClientUserBeanDef.companyid.name);
					attrReqDataMap[0].remove(ClientUserBeanDef.type.name);
					_gs.update(ClientUserBeanDef.class, addArray, attrReqDataMap[0]);
					cookies.setUserInfo (email + GeneralConst.SEPERATOR + (String) attrReqDataMap[0].get(ClientUserBeanDef.cookieskey.name));
					
					map.put ("rc", "0");
					map.put ("rd", "Redirecting...");
					map.put ("name", (String) attrReqDataMap[0].get (ClientUserBeanDef.firstname.name));
					
					if(session.getAttribute (GeneralConst.SESSION_REFERER)!=null){							
						map.put ("redirect", new String((String) session.getAttribute (GeneralConst.SESSION_REFERER)));
						session.removeAttribute(GeneralConst.SESSION_REFERER);
					}else{
						map.put ("redirect", "index.htm");
					}
				}else{
					map.put ("rc", "9999");						
					map.put ("rd", "Invalid Login");
				}								
			}else if(reqAction.equalsIgnoreCase (CHANGEPASSWORD)){
				attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, null);
				authMap = (HashMap) session.getAttribute (SessionConst.SESSION_USER);
				
				int rowUpdate = 0;
				String newPassword = _req.getParameter (NEWPASSWORD);
				if(!CommonUtil.isEmpty (newPassword)){
					newPassword = CryptUtil.getInstance ().encrypt (newPassword);
				}
				
				attrReqDataMap[0].put(ClientUserBeanDef.cid.name, (String) authMap.get(SessionConst.USER_ID));
				rowUpdate = changePassword (attrReqDataMap[0], newPassword);					 
				
				if(rowUpdate==1){
					map.put ("rc", "0");											
				}else{
					map.put ("rc", "9999");			
				}									
			}else if(reqAction.equalsIgnoreCase (SIGNOUT)){
				session.removeAttribute (SessionConst.SESSION_USER);	
				session.invalidate ();
				cookies.setUserInfo ("");				
				_resp.sendRedirect ("/");
			}else if(GeneralConst.FORGOT_PASSWORD.equals (reqAction)){
				StringBuffer result = sendPassword (_req);
				
				map.put ("rc", "0");	
				map.put ("rd", "An email has been sent to your inbox.");
			}
			json = new JSONObject(map);
		}		
		return json;
	}
	
	public JSONObject login(String reqAction){	
		JSONObject json = new JSONObject();
		ArrayList addArray = null;
		Class defClass = ClientUserBeanDef.class;
		HashMap attrReqDataMap = null;
		StringBuffer query = new StringBuffer();
		try{
			query.append("Select ");
			query.append(ClientUserBeanDef.cookieskey);
			json.put("rc", "0000");
		}catch(Exception e){			
			logger.error(e, e);
		}
		return json;
	}
		
	public void fbRequestHandler(HttpServletRequest _req, HttpServletResponse _resp){		
		CookiesUtil cookieUtil = new CookiesUtil(_req, _resp);
		String name = _req.getParameter(ClientUserBeanDef.firstname.name);
		String email = _req.getParameter(ClientUserBeanDef.email.name);			
		ArrayList addArray = null;
		HashMap authMap = new HashMap();	
		boolean isDealer = false;
		try{
			addArray = BeanDefUtil.getArrayList(ClientUserBeanDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		    HashMap attrReqDataMap = new HashMap(); 
			
		    attrReqDataMap.put(ClientUserBeanDef.password.name, "");
			attrReqDataMap.put(ClientUserBeanDef.firstname.name, name);
			attrReqDataMap.put(ClientUserBeanDef.email.name, email);
			attrReqDataMap.put(ClientUserBeanDef.cookieskey.name, CryptUtil.getInstance ().generateSecureCookies ());
			
			
			
		    // Check if it's existing user
		    HashMap data = getUserInfo(email);

		    if(data!=null){
		    	attrReqDataMap.put(ClientUserBeanDef.cid.name, data.get(ClientUserBeanDef.cid.name));
		    	_gs.update(ClientUserBeanDef.class, addArray,	attrReqDataMap);
		    }else{
		    	isDealer = !CommonUtil.isEmpty((String)attrReqDataMap.get(ClientUserBeanDef.companyname.name));
				if(isDealer){
					attrReqDataMap.put(ClientUserBeanDef.status.name, GeneralConst.SUSPENDED);
					attrReqDataMap.put(ClientUserBeanDef.type.name, "D");
				}else{
					attrReqDataMap.put(ClientUserBeanDef.status.name, GeneralConst.ACTIVE);
					attrReqDataMap.put(ClientUserBeanDef.type.name, "P");
				}
		    	attrReqDataMap.put(ClientUserBeanDef.companyid.name, _shopInfoBean.getShopName());
		    	attrReqDataMap.put(ClientUserBeanDef.signtype.name, "FB");
		    	attrReqDataMap.put(ClientUserBeanDef.status.name, GeneralConst.ACTIVE);
		    	logger.debug("attrReqDataMap = "+attrReqDataMap);
		    	_gs.create(ClientUserBeanDef.class, addArray,	attrReqDataMap);
		    }
				
			authMap.put (SessionConst.USER_ID, (String) attrReqDataMap.get (ClientUserBeanDef.cid.name));
			authMap.put (SessionConst.USER_NAME, (String) attrReqDataMap.get (ClientUserBeanDef.firstname.name));			
			authMap.put (SessionConst.USER_EMAIL, (String) attrReqDataMap.get (ClientUserBeanDef.email.name));		
			authMap.put (SessionConst.USER_GROUP, (String) attrReqDataMap.get (ClientUserBeanDef.type.name));					
			
			logger.debug("authMap = "+authMap);
			if(GeneralConst.ACTIVE.equals(attrReqDataMap.get(ClientUserBeanDef.status.name))){
				_req.getSession().setAttribute (SessionConst.SESSION_USER, authMap);
				cookieUtil.setUserInfo (email + GeneralConst.SEPERATOR + (String) attrReqDataMap.get(ClientUserBeanDef.cookieskey.name));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void checkCookiesLogin(){
		logger.debug ("------------------checkCookiesLogin-----------------------");
		HttpSession session = _req.getSession (false);		
		CookiesUtil cookies = new CookiesUtil(_req, _resp);		
		HashMap authMap = null;
		
		String userCookies = cookies.getUserInfo ();
		String[] userInfoArray = null;		
		
		HashMap[] attrReqDataMap = new HashMap[1];
		attrReqDataMap[0] = null;
		String email = null;
		String cookiesKey = null;
		HashMap updateMap = new HashMap();
		ArrayList addArray = null;
		try{
			logger.debug ("userCookies = "+userCookies);
			if(!CommonUtil.isEmpty (userCookies)){
				authMap = new HashMap();
				
				userInfoArray = userCookies.split (GeneralConst.SEPERATOR);
//				attrReqDataMap[0].put(ClientUserBeanDef.email.name, userInfoArray[0]);
				email = userInfoArray[0];
				cookiesKey = userInfoArray[1];
				
				attrReqDataMap[0] = getUserInfo(email);		
				logger.debug("attrReqDataMap[0] = "+attrReqDataMap[0]);
				if(attrReqDataMap[0]!=null && cookiesKey.equals(attrReqDataMap[0].get(ClientUserBeanDef.cookieskey.name))){
					
					addArray = BeanDefUtil.getArrayList(ClientUserBeanDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
					
					updateMap.put(ClientUserBeanDef.cookieskey.name, CryptUtil.getInstance ().generateSecureCookies ());
					updateMap.put(ClientUserBeanDef.cid.name, attrReqDataMap[0].get(ClientUserBeanDef.cid.name));
					_gs.update(ClientUserBeanDef.class, addArray, updateMap);
					
//					cookies.setUserInfo (email + GeneralConst.SEPERATOR + (String) attrReqDataMap[0].get(ClientUserBeanDef.cookieskey.name));
//					cookies.setUser ((String) attrReqDataMap[0].get (ClientUserBeanDef.firstname.name));
					session = _req.getSession (true);
//					cookies.setCookies ("JSESSIONID", session.getId (), 0);
					
					authMap.put (SessionConst.USER_ID, (String) attrReqDataMap[0].get (ClientUserBeanDef.cid.name));
					authMap.put (SessionConst.USER_NAME, (String) attrReqDataMap[0].get (ClientUserBeanDef.firstname.name));
					authMap.put (SessionConst.USER_GROUP, (String) attrReqDataMap[0].get (ClientUserBeanDef.type.name));
					authMap.put (SessionConst.USER_EMAIL, email);								
					session.setAttribute (SessionConst.SESSION_USER, authMap);						
				}
			}
		}catch(Exception e){
			logger.error (e, e);
		}
	}
	
	public int changePassword(HashMap data, String newPassword) throws Exception{
		PreparedStatement pstmt = null;
		int updateValue = 0;
		StringBuffer mQuery = new StringBuffer();		
		try{
			logger.debug("changePassword data = "+data);
			mQuery.append ("Update ").append (ClientUserBeanDef.TABLE).append (" Set ");
			mQuery.append (ClientUserBeanDef.password).append (" = ? ");
			mQuery.append ("Where ");
			mQuery.append (ClientUserBeanDef.cid).append (" = ? And ");
			mQuery.append (ClientUserBeanDef.password).append (" = ? ");

			pstmt = _dbConnection.prepareStatement (mQuery.toString ());
			pstmt.setString (1, newPassword);			
			pstmt.setString (2, (String) data.get(ClientUserBeanDef.cid.name));
			pstmt.setString (3, (String) data.get(ClientUserBeanDef.password.name));
			
			updateValue = pstmt.executeUpdate ();
		}catch(Exception e){
			throw new Exception(e);
		}finally{
			DBUtil.free(null, pstmt, null);
		}		
		
		return updateValue;
	}
	
	private StringBuffer sendPassword(HttpServletRequest req)throws Exception {
		HashMap mData = null;
		String email = req.getParameter(ClientUserBeanDef.email.name);
		String password = null;		
		StringBuffer buffer = new StringBuffer();
		try{						
			mData = getUserInfo(email);
			if(mData!=null){								
				email = (String) mData.get (ClientUserBeanDef.email.name);
				password = (String) mData.get (ClientUserBeanDef.password.name);
				password = CryptUtil.getInstance ().decrypt (password);
				mData.put (ClientUserBeanDef.password.name, password);
				
				SendMail sm = new SendMail(_shopInfoBean, _clientBean, _dbConnection);
				sm.sendEmail(mData, "FORGOT_PASSWORD");
				
//				emailSender.sendClientForgotPassword (email, password, _clientBean.getLocale ());							
			}else{
				//buffer.append (ResourceUtil.getMallResourceValue (MODULE_NAME, "invalid.user.id", _clientBean.getLocale ()));
			}
			
			logger.debug ("buffer = "+buffer);
		}catch(Exception e){
			throw e;
		}					
		
		return buffer;
	}
	
	private HashMap getUserInfo(String email){		
		StringBuffer query = new StringBuffer();
		HashMap data = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		try{
			query.append("Select ");
			query.append(ClientUserBeanDef.firstname).append(",");
			query.append(ClientUserBeanDef.lastname).append(",");
			query.append(ClientUserBeanDef.cid).append(",");
			query.append(ClientUserBeanDef.type).append(",");
			query.append(ClientUserBeanDef.password).append(",");
			query.append(ClientUserBeanDef.companyid).append(",");
			query.append(ClientUserBeanDef.cookieskey).append(",");
			query.append(ClientUserBeanDef.email);
			query.append(" From ");
			query.append(ClientUserBeanDef.TABLE);
			query.append(" Where ").append(ClientUserBeanDef.companyid).append("=?");	
			query.append(" And ").append(ClientUserBeanDef.status).append("=?");
			query.append(" And ").append(ClientUserBeanDef.email).append("=?");
			
			paramMap.put(ClientUserBeanDef.companyid, _shopInfoBean.getShopName());
			paramMap.put(ClientUserBeanDef.status, GeneralConst.ACTIVE);
			paramMap.put(ClientUserBeanDef.email, email);
			
			data = _gs.searchDataMap(query, paramMap);
			logger.debug("getUserInfo data = "+data);
		}catch(Exception e){
			logger.error(e, e);
		}
		
		return data;		
	}
	public void displayHandler(String moduleId)throws Exception{			
		
	}
	
	
}
