package com.forest.common.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SessionConst;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.NumberUtil;
import com.forest.common.util.ResourceUtil;

public class BaseServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{	
	private Logger logger = Logger.getLogger (BaseServlet.class);
	static final long serialVersionUID = 1L;
	public static final String START_TAG = "%";
	public static final String END_TAG = "%";
	
	protected Connection _dbConnection 	= null;
	protected String _requestFilename 	= null;
	protected String _returnType 		= null;	
	protected String _action 			= null;
	protected ShopInfoBean _shopInfoBean = null;
	protected ClientBean _clientBean 	= null;
	protected HttpServletRequest _req	= null;
	protected HttpServletResponse _resp	= null; 
	protected StringBuffer _parseHtml 	= new StringBuffer();
	
	protected Connection getDbConnection ()
	{
		try{
			if(_dbConnection==null || _dbConnection.isClosed ()){
				_dbConnection = new DBUtil().getDbConnection();				
			}
		}catch(Exception e){
			logger.error (e, e);
		}
		return _dbConnection;
	}
	
	protected void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{		
		//doPost (req, resp);
		req.setCharacterEncoding ("UTF-8");
		resp.setCharacterEncoding ("UTF-8");						
	}

	protected void doPost (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{		
		super.doPost (request, response);		
	}
	
	protected void printRequest(HttpServletRequest request){
		try
		{
		
//			Enumeration headerNames = request.getHeaderNames();
//		    while(headerNames.hasMoreElements()) {
//		        String headerName = (String)headerNames.nextElement();
//		        logger.debug(headerName + ":" + request.getHeader(headerName));
//		    }		    
			Enumeration m_oName = request.getParameterNames ();
			Object nextE = null;
			int len=0;
			String[] value = null;
//			if(m_oName.hasMoreElements ()){
				logger.debug ("-------------- start printRequest ----------------");
//			}
			while (m_oName.hasMoreElements ()){
				nextE = m_oName.nextElement ();
				value = request.getParameterValues(nextE.toString ());
				len = value.length;
				for(int i=0; i<len; i++){
					logger.debug (nextE.toString ()+"["+i+"] = "+ value[i]);
				}
				if(len>1){
					
				}else{
					
				}
				
			}
			logger.debug ("----------------------------------------");
		}
		catch (Exception e)
		{
			logger.error (e, e);
		}	
	}

	protected boolean checkLogin(){		
		boolean isValid = false;		
		String authCompany = null;
		try{			
			// Authentication			
			HttpSession session = _req.getSession (false);
			
			if("sign-out".equals (_action)){						
				if(session!=null){
					if(_shopInfoBean.getCompanyGroup()!=null){
						session.removeAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getCompanyGroup());
					}else{
						session.removeAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getShopName());
					}
				}
			}
			
			_requestFilename = _clientBean.getRequestFilename ();
			
			if(session==null || session.getAttribute (SessionConst.SESSION_ADMIN)==null){			
				//new AdminUserBuilder(shopInfoBean, clientBean, getDbConnection ()).checkCookiesLogin (req, resp);
			}
			
			if(session!=null){					
				HashMap authMap = null;
				if(!CommonUtil.isEmpty(_shopInfoBean.getCompanyGroup())){
					authMap = (HashMap) session.getAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getCompanyGroup());
				}else{
					authMap = (HashMap) session.getAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getShopName());
				}
				logger.debug("_shopInfoBean.getShopName() = "+_shopInfoBean.getShopName());
				logger.debug("_shopInfoBean.getCompanyGroup() = "+_shopInfoBean.getCompanyGroup());
				logger.debug("authMap = "+authMap);
				if(authMap!=null){					
					authCompany = (String) authMap.get (SessionConst.AUTHRIZE_SHOP_NAME);					
					if(authMap!=null ){
						if(_shopInfoBean.getCompanyGroup()!=null && _shopInfoBean.getCompanyGroup().equals (authCompany)){
							isValid = true;
						}else if(_shopInfoBean.getShopName().equals (authCompany)){
							isValid = true;				
						}
						if(isValid){
							_clientBean.setUserGroup ((String) authMap.get (SessionConst.USER_GROUP));
							_clientBean.setLoginUserId ((String) authMap.get (SessionConst.USER_ID));
							_clientBean.setLoginUserName ((String) authMap.get (SessionConst.USER_NAME));
						}
					}
				}
			}				
			if(isValid){
				if(isValid && _requestFilename.equals ("login.htm") && !"authorization".equals (_req.getParameter("action1"))){
					_resp.sendRedirect ("dashboard.html");
				}
			}else{
				if(_requestFilename.equals ("login.htm") 
						|| _requestFilename.equals ("contact.htm")					
						|| _requestFilename.equals ("upload.htm")
						|| _requestFilename.equals ("forgotpassword.htm")){
					isValid = true; // Skip Validation
				}else{
					if("json".equals (_returnType)){
						JSONObject json = new JSONObject();
						json.put ("redirect", "login.htm");		
						_parseHtml = new StringBuffer();
						_parseHtml.append (json.toString ());
						logger.debug ("parseHtml = "+_parseHtml);						
					}else{
						_resp.sendRedirect ("login.htm");
					}	
				}
			}

		}catch(Exception e){
			logger.error(e, e);
		}
		
		return isValid;
	}
	
	protected void commit(){
		try {
			if(_shopInfoBean.getShopName ().equals ("demo") 
		    		&& (!GeneralConst.EDIT.equals (_action) || !GeneralConst.SEARCH.equals (_action))
		    		&& !"admin".equals (_clientBean.getLoginUserName ())){		    	
		    	if(_dbConnection!=null && !_dbConnection.isClosed ()){		    		
		    		_dbConnection.rollback ();
		    	}
		    }else{		    	
		    	if(_dbConnection!=null && !_dbConnection.isClosed ()) _dbConnection.commit ();
		    }
		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	protected void printResult(){
		try{
			if(_dbConnection!=null && !_dbConnection.isClosed ()) _dbConnection.close ();
			_dbConnection = null;
			
			if("xml".equals (_returnType) || _requestFilename.endsWith(".xml")){
				_resp.setContentType("text/xml");
			}else if("json".equals (_returnType)){
				_resp.setContentType("text/html; charset=UTF-8");

			}else{							
				_resp.setContentType("text/html; charset=UTF-8");
			}
			PrintWriter out = _resp.getWriter();
			//ServletOutputStream out = _resp.getOutputStream();
			
			int flushMax = 0;
			int length = 0;
			
			if(_parseHtml!=null){
				length = _parseHtml.length ();
				for(int i=0; i<length; i++){
					flushMax = (i+flushMax < length)?length-i:300;	
					out.print (_parseHtml.substring (i, i+flushMax));
					out.flush ();
					i += flushMax - 1;
				}				
			}
		}catch(Exception e){
			logger.error(e, e);
		}
	}
	
	protected void printException(Exception ex){
		try{
			logger.error (ex, ex);
			if(_dbConnection!=null && !_dbConnection.isClosed ()) {
				_dbConnection.rollback ();
				logger.debug("------------- RollBack ---------------");
			}
			
			JSONObject mJson = new JSONObject();
			mJson.put ("rc", "9999");	
	//		mJson.put ("rd", ResourceUtil.getExceptionResourceValue (mJson.getString ("rc"), _clientBean.getLocale ()));
			_parseHtml = new StringBuffer();
			_parseHtml.append (mJson.toString ());
		}catch(Exception e){
			logger.error(e, e);
		}
	}
	
	protected void insertCommonProperties(){
		// js i18n
	    if(_parseHtml.indexOf ("</body>")!=-1){
			StringBuffer mBuffer = new StringBuffer();
			mBuffer.append ("<div style=\"display: none\">");
				mBuffer.append ("<span id=\"js-processing-request\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.processing.request", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-ajax-fail\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.ajax.fail", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-save\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.save", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-upload\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.upload", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-add\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.add", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-remove\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.remove", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-refresh-search\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.refresh.search", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-close\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.close", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-show\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.show", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-hide\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.hide", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-latest-feature-once\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.latest.feature.once", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-yes\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.yes", _clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-no\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.no", _clientBean.getLocale ())).append ("</span>");
				
				mBuffer.append ("<span id=\"js-required\">").append (ResourceUtil.getShopResourceValue (null,"js", "required", _clientBean.getLocale ())).append ("</span>");
			mBuffer.append ("</div>");	
		
			_parseHtml.insert (_parseHtml.indexOf ("</body>"), mBuffer);
		}
	}
	
	protected void replaceI18n(String resources){
		String key = null;
	    String value = null;			    
	    int fromIndex = 0;
	    while((fromIndex = _parseHtml.indexOf (START_TAG+"i18n", 0)) != -1){	
	    	key = _parseHtml.substring (fromIndex + START_TAG.length (), _parseHtml.indexOf (END_TAG, fromIndex+1));
	    	key = key.trim ();
	    	
	    	if(key!=null){	    		
	    		if(key.indexOf("i18n.domain")!=-1){
	    			value = GeneralConst.DOMAIN;
	    		}else if(key.indexOf("i18n.version")!=-1){
	    			value = ResourceUtil.getVersionValue(key);
	    		}else if(key.indexOf("i18n.theme")!=-1){
	    			value = _shopInfoBean.getTheme();
	    		}else{	    		
		    		value = ResourceUtil.getAdminResourceValue (resources, null, key, _clientBean.getLocale ());	    		
	    		}
	    		
	    		value = (value==null)?"??"+key+"??":value;
		    	_parseHtml.replace(fromIndex, (fromIndex + START_TAG.length () + key.length () + END_TAG.length ()), value);	
	    	}
	    }
	}
	
	protected void replaceSystemVariable(){
		String key = null;
	    String value = null;			    
	    int fromIndex = 0;
	    while((fromIndex = _parseHtml.indexOf (START_TAG+"system.", 0)) != -1){	
	    	key = _parseHtml.substring (fromIndex + START_TAG.length (), _parseHtml.indexOf (END_TAG, fromIndex+1));
	    	key = key.trim ();
	    	
	    	if(key!=null){	  
//	    		logger.debug("key = "+key);
	    		try{
		    		if(key.equals("system.selected.currency")) value = _clientBean.getSelectedCurrency();	    		
		    		if(key.equals("system.payment.currency")) value = _shopInfoBean.getPaymentCurrency();
		    		if(key.equals("system.shop.country")) value = _shopInfoBean.getShopCountryISO();
		    		if(key.equals("system.domain")) value = _req.getServerName ();
		    		
		    		if(key.equals("system.selected.currency.rate")){
		    			value = NumberUtil.getCurrencyRate(_dbConnection, _shopInfoBean, _shopInfoBean.getPaymentCurrency(), _clientBean.getSelectedCurrency()).toString();
		    		}
		    		if(key.equals("system.selected.currency.symbol")){
		    			value = NumberUtil.getCurrencySymbol(_dbConnection, _shopInfoBean, _clientBean.getSelectedCurrency());
		    		}
	    		}catch(Exception e){
	    			logger.error(e);
	    		}
	    		value = (value==null)?"??"+key+"??":value;
		    	_parseHtml.replace(fromIndex, (fromIndex + START_TAG.length () + key.length () + END_TAG.length ()), value);	
	    	}
	    }
	}
}
