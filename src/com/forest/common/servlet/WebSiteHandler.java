package com.forest.common.servlet;
 
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.AdminBuilder;
import com.forest.common.builder.WebSiteModuleConfig;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SessionConst;
import com.forest.common.servlet.BaseServlet;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CookiesUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.IPUtil;
import com.forest.common.util.ModuleContent;
import com.forest.common.util.TemplateProcess;
import com.forest.share.webbuilder.BaseWebBuilder;
import com.forest.share.webbuilder.LoginWebBuilder;
 
public class WebSiteHandler extends BaseServlet
{	
	private Logger logger = Logger.getLogger (this.getClass ());
	
	static final long serialVersionUID = 1L;			
	
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp, ShopInfoBean shopInfoBean, ClientBean clientBean)
			throws ServletException, IOException, Exception
	{			
		try{						
			_shopInfoBean = shopInfoBean;
			_clientBean = clientBean;
			_req = req;
			_resp = resp;
			_requestFilename = clientBean.getRequestFilename();
			_dbConnection =  getDbConnection();
			String returnType = _req.getParameter("return");
			
			setClientInfo();
			if(CommonUtil.isEmpty(_clientBean.getLoginUserId())){
				boolean needLogin = false;
				if("order.htm".equals(_requestFilename)){
					needLogin = true;
				}
				if("changepassword.htm".equals(_requestFilename)){
					needLogin = true;
				}
				if("userprofile.htm".equals(_requestFilename)){
					needLogin = true;
				}
				if("payment.htm".equals(_requestFilename)){
					needLogin = true;
					_req.getSession().setAttribute (GeneralConst.SESSION_REFERER,"payment.htm");
				}
				if(needLogin){
					_resp.sendRedirect("sign-in.htm");
				}
			}
			if("json".equals(returnType)){
				jsonRequest(shopInfoBean, clientBean, _dbConnection);				
			}else{
				getDynamicPage(shopInfoBean, clientBean, _dbConnection, req, resp);
			}
		}catch(Exception e){		
			e.printStackTrace();
		}finally{			
			printResult();
		}
	}			
	
	private void setClientInfo(){
		HashMap authMap = null;
		HttpSession session = _req.getSession (false);
		CookiesUtil cu = new CookiesUtil (_req, _resp);
		String currencyCode = cu.getCurrency();
		try{
			if(session==null || session.getAttribute (SessionConst.SESSION_USER)==null){
				new LoginWebBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, null).checkCookiesLogin();
			}
//			logger.info("_req.getRemoteAddr() = "+_req.getRemoteAddr());
//			logger.info("_req.getRemoteHost() = "+_req.getRemoteHost());
			if(session!=null){
				authMap = (HashMap) session.getAttribute (SessionConst.SESSION_USER);
				if(authMap!=null){
					_clientBean.setLoginUserId((String) authMap.get (SessionConst.USER_ID));
					_clientBean.setLoginUserName((String) authMap.get (SessionConst.USER_NAME));
					_clientBean.setLoginUserEmail((String) authMap.get (SessionConst.USER_EMAIL));
					_clientBean.setUserGroup((String) authMap.get (SessionConst.USER_GROUP));				
				}				
			}
			
			if(CommonUtil.isEmpty(currencyCode)){
				currencyCode = new IPUtil().getCurrency(_dbConnection, _req.getRemoteAddr());
				cu.setCurrency(currencyCode);					
			}
			_clientBean.setSelectedCurrency(CommonUtil.isEmpty(currencyCode)?_shopInfoBean.getPaymentCurrency():currencyCode);
		}catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	public StringBuffer jsonRequest(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn){
		Map hashPageMap = null; 		
        AdminBuilder wb = new AdminBuilder(shopInfoBean, clientBean, conn, null); 
        BaseWebBuilder webBuilder = null;
        Object rtnObj = null;
        try{        	                		        	
	    	hashPageMap = (HashMap) WebSiteModuleConfig.getPageToClass(shopInfoBean.getBusiness(), _clientBean.getRequestFilename());	        	
	    	if(hashPageMap!=null){
	    		webBuilder = (BaseWebBuilder)wb.getWebSiteBuilder(hashPageMap, _req, _resp, null);
	    		rtnObj = webBuilder.requestJsonHandler();
//	    		if(rtnObj instanceof JSONObject){
//	    			_parseHtml = new StringBuffer(((JSONObject)rtnObj).toString());	
//	    		}else if(rtnObj instanceof JSONArray){
//	    			_parseHtml = new StringBuffer(((JSONArray)rtnObj).toString());
//	    		}else{
	    			_parseHtml = new StringBuffer(String.valueOf(rtnObj));
//	    		}
	    			 	    		
	    		logger.debug("json = "+_parseHtml);
			}	        		       
	    	
	    	commit();
        }catch(Exception e){
        	logger.error("hashPageMap: "+hashPageMap);
        	logger.error(e, e);
        	printException(e);
        }
        
        return _parseHtml;
	}
	
	public StringBuffer getDynamicPage(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn, HttpServletRequest req, HttpServletResponse resp){	
		Map hashPageMap = null; 
		Element ele = null;
		Element metaEle = null;
		Document doc = null;   
        Elements media = null;
        int count = 0;
       
        String moduleId = null;
        String originalPage = null;
        String metaModuleId = null;
        HashMap metaMap = null;
        TemplateProcess tp = new TemplateProcess();
        AdminBuilder wb = new AdminBuilder(shopInfoBean, clientBean, conn, null); 
        BaseWebBuilder webBuilder = null;
//        StringBuffer location = new StringBuffer();	
        try{        	        
//        	logger.debug("shopInfoBean.getBusiness() = "+shopInfoBean.getBusiness());
//        	location.append (ResourceUtil.getSettingValue ("company.path"));			
//    		location.append (shopInfoBean.getShopName()).append("/themes/");		
//    		location.append (shopInfoBean.getTheme()).append("/");    		
    		
        	_parseHtml = new StringBuffer(FileHandler.readWebFile(shopInfoBean, clientBean, clientBean.getRequestFilename()).toString());	
        	if(_parseHtml==null || _parseHtml.length()==0){
        		originalPage = clientBean.getRequestFilename();        		
        		_parseHtml = new StringBuffer(FileHandler.readWebFile (shopInfoBean, clientBean, "custompage.htm").toString());
//        		CommonUtil.replaceBuffer(_parseHtml, "page.code", originalPage);
        	}
        	doc = tp.initTemplateProcessDoc (_clientBean, _parseHtml, shopInfoBean.getTemplatePath ());        	
        	// Get Meta Info
        	media = doc.select("meta-content"); 
        	if(media.size()>0){
	        	metaEle = media.get(0);
	        	metaModuleId = metaEle.attr("id");
	        	metaModuleId = metaModuleId.substring(0, metaModuleId.length()-".meta".length());
	        	logger.debug("metaModuleId: "+metaModuleId);
        	}
        	media = doc.select("p|module-content");        	
        	count = media.size();        	
	        for (int i=0; i<count;i++) {
	        	ele = media.get(i);
	        	moduleId = ele.attr("id");
	        	if(CommonUtil.isEmpty(moduleId)) continue;	        		        	
	        	
	        	hashPageMap = (HashMap) WebSiteModuleConfig.getModuleToClass(shopInfoBean.getBusiness(), moduleId);
	        	logger.debug("Part A moduleId = "+moduleId+": "+hashPageMap);
	        	if(hashPageMap!=null){
	        		webBuilder = (BaseWebBuilder)wb.getWebSiteBuilder(hashPageMap, req, resp, ele);
//	        		logger.debug(moduleId.substring(0, metaModuleId.length()) + " === "+ metaModuleId);
	        		if(metaModuleId!=null && moduleId.substring(0, metaModuleId.length()).equals(metaModuleId)){
	        			metaMap = webBuilder.displayHandlerWithHashMap(moduleId);
	        		}else{
	        			webBuilder.displayHandler(moduleId);
	        		}
				}
	        }	        
	        
	        // Set Meta data
	        if(metaMap!=null){
	        	ModuleContent mc = new ModuleContent();
	        	
	        	mc.initModuleContent(metaEle);
	        	logger.debug("metaMap: "+metaMap);
	        	mc.setElementContent(metaMap);
	        }
	        
	        media = doc.select("p|module-content");        	
        	count = media.size();        	
	        for (int i=0; i<count;i++) {
	        	ele = media.get(i);
	        	moduleId = ele.attr("id");
	        	if(!CommonUtil.isEmpty(ele.attr("pid"))) continue;
	        	
	        	hashPageMap = (HashMap) WebSiteModuleConfig.getModuleToClass(shopInfoBean.getBusiness(), moduleId);
	        	logger.debug("Part B moduleId = "+moduleId+": "+hashPageMap);
	        	if(hashPageMap!=null){
	        		webBuilder = (BaseWebBuilder)wb.getWebSiteBuilder(hashPageMap, req, resp, ele);
	        		webBuilder.displayHandler(moduleId);	        		
				}
	        }
	        
	        
	        _parseHtml = new StringBuffer(doc.html());
	        replaceI18n(null);
	        replaceSystemVariable();
			
        }catch(Exception e){
        	logger.error("originalPage: "+originalPage);
        	logger.error(e, e);
        }
        
        return _parseHtml;
	}
}
