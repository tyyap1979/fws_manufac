package com.forest.common.servlet;
 
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.forest.admin.builder.GlobalResetBuilder;
import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.services.ThemesServices;
import com.forest.common.util.DBUtil;
	
public class HTMLHandler extends BaseServlet
{	
	private Logger logger = Logger.getLogger (this.getClass ());
	static final long serialVersionUID = 1L;
	
	private ShopInfoBean _shopInfoBean = null;
	private ClientBean _clientBean = null;
	HttpServletRequest _req = null;
	HttpServletResponse _resp = null;
	
	private void formatURL(){
//		http://www.megatrend.com/admin/login.htm
		String serverName = _req.getServerName ();
	    String[] serverNameArray = null;
	    String[] servletArray = null;
	    String pathInfo = _req.getServletPath ();			    
	    String moduleName = null;
	    String pageName = null;	    
	    String domainName = null;
	    String subDomain = null;
		if(pathInfo.indexOf ("/")!=-1){
			servletArray = pathInfo.split ("/");	
		}		
//		logger.debug("getRequestURL = "+_req.getRequestURL());		
//		logger.debug("_req.getScheme() = "+_req.getScheme());
		logger.debug("serverName = "+serverName);
		logger.debug("servletArray.length: "+servletArray.length);
		serverNameArray = serverName.split ("\\.");	
		
		if(serverNameArray.length==3){ //www.abc.com					
			if(serverNameArray[1].substring(serverNameArray[1].length()-1).equals("1")){
				serverNameArray[1] = serverNameArray[1].substring(0, serverNameArray[1].length()-1);
			}
			subDomain = serverNameArray[0];
			domainName = serverNameArray[1] + "." + serverNameArray[2];
			
			if("www".equals(subDomain)) {
				subDomain = servletArray[1];
			}
		}else{ // abc.com
			domainName = serverNameArray[0] + "." + serverNameArray[1];
		}
		logger.debug("domainName: "+domainName);
		if(pathInfo.substring (pathInfo.length ()-1).equals ("/")){
			pageName = "default.htm";
		}else{
			if(servletArray.length==3){
				_clientBean.setLanguage(servletArray[servletArray.length - 2]);
			}
			pageName = servletArray[servletArray.length - 1];
		}
		
		// Check For company admin context		
		String[] adminContext = ThemesServices.getInstance ().getAdminContext(domainName, subDomain);
				
		logger.debug("adminContext[1] = "+adminContext[1]);		
		
		if(adminContext[1]!=null && adminContext[1].equals (subDomain)){
			moduleName = "admin";			
		}else{
			moduleName = "website";
		}
		
		_shopInfoBean.setModule(moduleName);
		_shopInfoBean.setShopDomain(domainName);
		_shopInfoBean.setShopContext(subDomain);
		_clientBean.setRequestFilename (pageName);
		
		
		logger.info(adminContext[0]+(moduleName==null?"":"/"+moduleName)+"/"+pageName+"["+_shopInfoBean.getShopContext()+"]");
	}
	
	protected void doGet (HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{		
		super.doGet (req, resp);
		printRequest (req);
		
	    
		try{						
			_shopInfoBean = new ShopInfoBean();
			_clientBean = new ClientBean();
			_req = req;
			_resp = resp;
			
			formatURL();
				
			if(("global.htm".equals(_clientBean.getRequestFilename ()))){			    	
				GlobalResetBuilder.getInstance (getDbConnection ()).requestHandler (req);
				return;
			}

			if("admin".equals (_shopInfoBean.getModule())){
				ThemesServices.getInstance ().assignShopSetting (_shopInfoBean, _clientBean);
				new AdminHandler().doGet (req, resp, _shopInfoBean, _clientBean);			
			}else if("website".equals (_shopInfoBean.getModule())){
				ThemesServices.getInstance ().assignShopWebSiteSetting (_shopInfoBean, _clientBean);
				new WebSiteHandler().doGet(req,resp,_shopInfoBean, _clientBean);
			}
		}catch(Exception e){
			logger.error (e, e);								
		}finally{
	    	
		}
	}
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{		
		doGet(request, response);
	}			
	
	
}
