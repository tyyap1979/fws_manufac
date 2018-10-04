package com.forest.retail.webbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SessionConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CookiesUtil;
import com.forest.common.util.CryptUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.SendMail;
import com.forest.retail.beandef.RETAIL_CartDef;
import com.forest.retail.beandef.RETAIL_MemberDef;
import com.forest.share.webbuilder.BaseWebBuilder;

public class RETAIL_LoginWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (RETAIL_LoginWebBuilder.class);
	private final String FBSIGNIN = "fbsignin";
	private final String SIGNIN = "signin";
	private final String SIGNOUT = "signout";	
	
	private final String CHANGEPASSWORD = "change-password";
	private final String NEWPASSWORD = "new-password";
	private final String CONFIRMPASSWORD = "confirm-password";
	private final String REMEMBERME = "remember-me";
	
	public RETAIL_LoginWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest _req,
			HttpServletResponse _resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, _req, _resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		String email = _req.getParameter(RETAIL_MemberDef.email.name);		
		String password = _req.getParameter (RETAIL_MemberDef.password.name);
		HttpSession session = _req.getSession (false);
		CookiesUtil cookies = new CookiesUtil(_req, _resp);		
		
		HashMap map = new HashMap();
		HashMap authMap = null;
		HashMap[] attrReqDataMap = null;
		ArrayList addArray = null;		
		
		if(!CommonUtil.isEmpty (password)){
			password = CryptUtil.getInstance ().encrypt (password);
		}
		
		if(reqAction!=null){
			addArray = BeanDefUtil.getArrayList(RETAIL_MemberDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			
			if(reqAction.equalsIgnoreCase (FBSIGNIN)){
				fbRequestHandler(_req, _resp);
				map.put ("rc", "0");	
									
				if("cart.htm".equals(_clientBean.getRequestFilename())){
					map.put ("redirect", "payment.htm");
				}else{
					map.put ("redirect", "");
				}
			}else if(reqAction.equalsIgnoreCase (SIGNIN)){	
				attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, RETAIL_MemberDef._addList, null);				
				attrReqDataMap[0] = getUserInfo(email);
				if(attrReqDataMap[0]!=null && password.equals((String) attrReqDataMap[0].get(RETAIL_MemberDef.password.name))){	
					authMap = new HashMap();						
					session = _req.getSession ();											

					authMap.put (SessionConst.USER_ID, (String) attrReqDataMap[0].get (RETAIL_MemberDef.mid.name));
					authMap.put (SessionConst.USER_NAME, (String) attrReqDataMap[0].get (RETAIL_MemberDef.firstname.name));				
					authMap.put (SessionConst.USER_EMAIL, email);																		
					session.setAttribute (SessionConst.SESSION_USER, authMap);

					// Update Cookies
					attrReqDataMap[0].put(RETAIL_MemberDef.cookieskey.name, CryptUtil.getInstance ().generateSecureCookies ());
					attrReqDataMap[0].remove(RETAIL_MemberDef.email.name);
					attrReqDataMap[0].remove(RETAIL_MemberDef.firstname.name);
					attrReqDataMap[0].remove(RETAIL_MemberDef.password.name);
					attrReqDataMap[0].remove(RETAIL_MemberDef.companyid.name);					
					_gs.update(RETAIL_MemberDef.class, addArray, attrReqDataMap[0]);
					cookies.setUserInfo (email + GeneralConst.SEPERATOR + (String) attrReqDataMap[0].get(RETAIL_MemberDef.cookieskey.name));
					
					map.put ("rc", "0000");
					map.put ("rd", "Redirecting...");
					map.put ("name", (String) attrReqDataMap[0].get (RETAIL_MemberDef.firstname.name));
					
					if(session.getAttribute (GeneralConst.SESSION_REFERER)!=null){							
						map.put ("redirect", new String((String) session.getAttribute (GeneralConst.SESSION_REFERER)));
						session.removeAttribute(GeneralConst.SESSION_REFERER);
					}else{
						map.put ("redirect", "index.htm");
					}
					updateEmailToCart();
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
				
				attrReqDataMap[0].put(RETAIL_MemberDef.mid.name, (String) authMap.get(SessionConst.USER_ID));
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
				sendPassword (_req);
				
				map.put ("rc", "0");	
				map.put ("rd", "An email has been sent to your inbox.");
			}
			json = new JSONObject(map);
		}		
		return json;
	}
	
//	public JSONObject login(String reqAction){	
//		JSONObject json = new JSONObject();
//		StringBuffer query = new StringBuffer();
//		try{
//			query.append("Select ");
//			query.append(RETAIL_MemberDef.cookieskey);
//			json.put("rc", "0000");
//		}catch(Exception e){			
//			logger.error(e, e);
//		}
//		return json;
//	}
		
	public void fbRequestHandler(HttpServletRequest _req, HttpServletResponse _resp){		
		CookiesUtil cookieUtil = new CookiesUtil(_req, _resp);
		String name = _req.getParameter(RETAIL_MemberDef.firstname.name);
		String email = _req.getParameter(RETAIL_MemberDef.email.name);			
		ArrayList addArray = null;
		HashMap authMap = new HashMap();	
		
		try{
			addArray = BeanDefUtil.getArrayList(RETAIL_MemberDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		    HashMap attrReqDataMap = new HashMap(); 
			
		    attrReqDataMap.put(RETAIL_MemberDef.password.name, "");
			attrReqDataMap.put(RETAIL_MemberDef.firstname.name, name);
			attrReqDataMap.put(RETAIL_MemberDef.email.name, email);
			attrReqDataMap.put(RETAIL_MemberDef.cookieskey.name, CryptUtil.getInstance ().generateSecureCookies ());
			
			
			
		    // Check if it's existing user
		    HashMap data = getUserInfo(email);

		    if(data!=null){
		    	attrReqDataMap.put(RETAIL_MemberDef.mid.name, data.get(RETAIL_MemberDef.mid.name));
		    	_gs.update(RETAIL_MemberDef.class, addArray,	attrReqDataMap);
		    }else{		    	
		    	attrReqDataMap.put(RETAIL_MemberDef.companyid.name, _shopInfoBean.getShopName());
		    	attrReqDataMap.put(RETAIL_MemberDef.signtype.name, "FB");
		    	attrReqDataMap.put(RETAIL_MemberDef.status.name, GeneralConst.ACTIVE);
		    	logger.debug("attrReqDataMap = "+attrReqDataMap);
		    	_gs.create(RETAIL_MemberDef.class, addArray,	attrReqDataMap);
		    }
				
			authMap.put (SessionConst.USER_ID, (String) attrReqDataMap.get (RETAIL_MemberDef.mid.name));
			authMap.put (SessionConst.USER_NAME, (String) attrReqDataMap.get (RETAIL_MemberDef.firstname.name));			
			authMap.put (SessionConst.USER_EMAIL, (String) attrReqDataMap.get (RETAIL_MemberDef.email.name));								
			
			logger.debug("authMap = "+authMap);
			if(GeneralConst.ACTIVE.equals(attrReqDataMap.get(RETAIL_MemberDef.status.name))){
				_req.getSession().setAttribute (SessionConst.SESSION_USER, authMap);
				cookieUtil.setUserInfo (email + GeneralConst.SEPERATOR + (String) attrReqDataMap.get(RETAIL_MemberDef.cookieskey.name));
			}
			
			updateEmailToCart();
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
//				attrReqDataMap[0].put(RETAIL_MemberDef.email.name, userInfoArray[0]);
				email = userInfoArray[0];
				cookiesKey = userInfoArray[1];
				
				attrReqDataMap[0] = getUserInfo(email);		
				logger.debug("attrReqDataMap[0] = "+attrReqDataMap[0]);
				if(attrReqDataMap[0]!=null && cookiesKey.equals(attrReqDataMap[0].get(RETAIL_MemberDef.cookieskey.name))){
					
					addArray = BeanDefUtil.getArrayList(RETAIL_MemberDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
					
					updateMap.put(RETAIL_MemberDef.cookieskey.name, CryptUtil.getInstance ().generateSecureCookies ());
					updateMap.put(RETAIL_MemberDef.mid.name, attrReqDataMap[0].get(RETAIL_MemberDef.mid.name));
					_gs.update(RETAIL_MemberDef.class, addArray, updateMap);
					
//					cookies.setUserInfo (email + GeneralConst.SEPERATOR + (String) attrReqDataMap[0].get(RETAIL_MemberDef.cookieskey.name));
//					cookies.setUser ((String) attrReqDataMap[0].get (RETAIL_MemberDef.firstname.name));
					session = _req.getSession (true);
//					cookies.setCookies ("JSESSIONID", session.getId (), 0);
					
					authMap.put (SessionConst.USER_ID, (String) attrReqDataMap[0].get (RETAIL_MemberDef.mid.name));
					authMap.put (SessionConst.USER_NAME, (String) attrReqDataMap[0].get (RETAIL_MemberDef.firstname.name));					
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
			mQuery.append ("Update ").append (RETAIL_MemberDef.TABLE).append (" Set ");
			mQuery.append (RETAIL_MemberDef.password).append (" = ? ");
			mQuery.append ("Where ");
			mQuery.append (RETAIL_MemberDef.mid).append (" = ? And ");
			mQuery.append (RETAIL_MemberDef.password).append (" = ? ");

			pstmt = _dbConnection.prepareStatement (mQuery.toString ());
			pstmt.setString (1, newPassword);			
			pstmt.setString (2, (String) data.get(RETAIL_MemberDef.mid.name));
			pstmt.setString (3, (String) data.get(RETAIL_MemberDef.password.name));
			
			updateValue = pstmt.executeUpdate ();
		}catch(Exception e){
			throw new Exception(e);
		}finally{
			DBUtil.free(null, pstmt, null);
		}		
		
		return updateValue;
	}
	
	private void updateEmailToCart() throws Exception{
		PreparedStatement pstmt = null;		
		ResultSet rs = null;
		StringBuffer mQuery = new StringBuffer();	
		CookiesUtil cu = new CookiesUtil(_req, _resp);
		String cartId = cu.getCartID();
		HashMap authMap = null;
		HttpSession session = _req.getSession (false);
		int updatedRow = 0;
		try{																						
			authMap = (HashMap) session.getAttribute (SessionConst.SESSION_USER);			
			if(!CommonUtil.isEmpty(cartId)){
				mQuery.append ("Update ").append (RETAIL_CartDef.TABLE).append (" Set ");
				mQuery.append (RETAIL_CartDef.email).append (" = ? ");
				mQuery.append ("Where ");
				mQuery.append (RETAIL_CartDef.cartid).append (" = ? And ");
				mQuery.append (RETAIL_CartDef.companyid).append (" = ? ");

				pstmt = _dbConnection.prepareStatement (mQuery.toString ());
				pstmt.setString (1, (String) authMap.get(SessionConst.USER_EMAIL));			
				pstmt.setString (2, cartId);
				pstmt.setString (3, _shopInfoBean.getShopName());
				
				updatedRow = pstmt.executeUpdate ();
			}
			// Retrieve User cart id if user cookies have no cartid or cart id is expired.
			if(updatedRow==0){
				// Set cart to user
				mQuery = new StringBuffer();
				mQuery.append ("Select ");
				mQuery.append (RETAIL_CartDef.cartid).append (" From ");
				mQuery.append (RETAIL_CartDef.TABLE);
				mQuery.append (" Where ");
				mQuery.append (RETAIL_CartDef.email).append (" = ? And ");
				mQuery.append (RETAIL_CartDef.companyid).append (" = ? ");

				pstmt = _dbConnection.prepareStatement (mQuery.toString ());
				pstmt.setString (1, (String) authMap.get(SessionConst.USER_EMAIL));			
				pstmt.setString (2, _shopInfoBean.getShopName());
				
				rs = pstmt.executeQuery();
				if(rs.next()){
					cartId = rs.getString(RETAIL_CartDef.cartid.name);
					cu.setCartID(cartId);
				}
			}
		}catch(Exception e){
			throw new Exception(e);
		}finally{
			DBUtil.free(null, pstmt, null);
		}		
	}
	
	private StringBuffer sendPassword(HttpServletRequest req)throws Exception {
		HashMap mData = null;
		String email = req.getParameter(RETAIL_MemberDef.email.name);
		String password = null;		
		StringBuffer buffer = new StringBuffer();
		try{						
			mData = getUserInfo(email);
			if(mData!=null){								
				email = (String) mData.get (RETAIL_MemberDef.email.name);
				password = (String) mData.get (RETAIL_MemberDef.password.name);
				password = CryptUtil.getInstance ().decrypt (password);
				mData.put (RETAIL_MemberDef.password.name, password);
				
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
			query.append(RETAIL_MemberDef.firstname).append(",");
			query.append(RETAIL_MemberDef.lastname).append(",");
			query.append(RETAIL_MemberDef.mid).append(",");			
			query.append(RETAIL_MemberDef.password).append(",");
			query.append(RETAIL_MemberDef.companyid).append(",");
			query.append(RETAIL_MemberDef.cookieskey).append(",");
			query.append(RETAIL_MemberDef.email);
			query.append(" From ");
			query.append(RETAIL_MemberDef.TABLE);
			query.append(" Where ").append(RETAIL_MemberDef.companyid).append("=?");	
			query.append(" And ").append(RETAIL_MemberDef.status).append("=?");
			query.append(" And ").append(RETAIL_MemberDef.email).append("=?");
			
			paramMap.put(RETAIL_MemberDef.companyid, _shopInfoBean.getShopName());
			paramMap.put(RETAIL_MemberDef.status, GeneralConst.ACTIVE);
			paramMap.put(RETAIL_MemberDef.email, email);
			
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
