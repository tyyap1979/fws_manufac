package com.forest.share.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.WebsiteDef;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.builder.ModuleConfig;
import com.forest.common.builder.ShopAdminBuilder;
import com.forest.common.builder.AdminBuilder;
import com.forest.common.constants.BusinessConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.ThemesServices;
import com.forest.common.servlet.WebSiteHandler;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.FileUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.TemplateProcess;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_Grouping;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_RawDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDef;
import com.forest.mfg.services.AdminSQLServices;
import com.forest.share.beandef.FriendlyLinkDef;
import com.forest.share.beandef.GalleryDef;
import com.forest.share.beandef.MaterialDef;

public class BuildWebHTML {
	private Logger logger = Logger.getLogger (this.getClass ());
	private ShopInfoBean _shopInfoBean = null;
	private ClientBean _clientBean 	= null;
	private Connection _dbConnection = null;
	private HttpServletRequest _req = null;
	private HttpServletResponse _resp = null;
	private StringBuffer _parseHtml = null;

	public BuildWebHTML(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn,HttpServletRequest req, HttpServletResponse resp)throws Exception{
		_shopInfoBean = shopInfoBean;
		_clientBean = clientBean;
		_dbConnection = conn;
		_req = req;
		_resp = resp;
	}
	
	private String[] getCreateSuperFilesArray(){
		String[] files = {"index.htm","gallery.htm", "material.htm","contact.htm"};
		return files;
	}
	
	private String[] getMFGCreateFilesArray(){
		String[] files = {"index.htm","gallery.htm", "material.htm","contact.htm"};
		return files;
	}
	
	private String[] getIDCreateFilesArray(){
		String[] files = {"index.htm","gallery.htm", "material.htm","contact.htm"};
		return files;
	}
	
	public void buildHTML(Class defClass)throws Exception{		
		String[] files = null;
		files = getIDCreateFilesArray();
		try{							
//			HashMap reqMap = ThemesServices.getInstance().getShopInfo(_shopInfoBean);			
//			reqMap.put(ShopBeanDef.language.name, _shopInfoBean.getDefaultLanguage());				
//			buildHTML(reqMap, files);			
		}catch(Exception e){
			logger.error(e, e);
		}
	}
	
	public void buildHTML(HashMap reqMap)throws Exception{	
		String[] files = null;		
		try{
//			files = getIDCreateFilesArray();
//			buildHTML(reqMap, files);
		}catch(Exception e){
			logger.error(e, e);
		}
	}
	
	public void buildHTML(HashMap reqMap, String[] files)throws Exception{		
		String htmlDirectory = null;
		String shopName = (String) reqMap.get(WebsiteDef.companyid.name);
		String shopDomain = null; 	
		String companyGroup = null;
		HashMap dataMap = null;
		AdminSQLServices sqlService = new AdminSQLServices(_shopInfoBean,_clientBean, _dbConnection);
		ArrayList dataList = sqlService.getShopsWebSite(shopName);
		
		if(dataList!=null && dataList.size()>0){
			dataMap = (HashMap)dataList.get(0);
			shopDomain = (String)dataMap.get(WebsiteDef.domain.name);
			companyGroup = (String)dataMap.get(ShopBeanDef.companygroup.name);
		}
		
		ClientBean clientBean = new ClientBean();
		clientBean.setLocale (new Locale("en"));
		
		ShopInfoBean shopInfoBean = new ShopInfoBean();
		shopInfoBean.setShopDomain(shopDomain);
		shopInfoBean.setShopName(shopName);
		shopInfoBean.setCompanyGroup(companyGroup);
		shopInfoBean.setModule("website");
				
		int fileCount = files.length;
		ThemesServices.getInstance ().assignShopWebSiteSetting (shopInfoBean, clientBean);
		
		for(int x=0; x<fileCount; x++){
			clientBean.setRequestFilename(files[x]);
			
			htmlDirectory = ResourceUtil.getCompanyAdminPath (shopInfoBean);
			buildHTML(shopName, htmlDirectory, files[x], shopInfoBean, clientBean);
		}
		
	}
	
	public void buildHTML(String shopName, String htmlDirectory, String page, ShopInfoBean shopInfoBean, ClientBean clientBean)throws Exception{		
		FileUtil fu = new FileUtil();
		
		String htmlLocation =  htmlDirectory + page + "l"; // a.htm -> a.html
		logger.debug("htmlLocation = "+htmlLocation);
		
		WebSiteHandler wsHandler = new WebSiteHandler();
		clientBean.setRequestFilename(page);
		_parseHtml = wsHandler.getDynamicPage(shopInfoBean, clientBean, _dbConnection, _req, _resp);
		
		fu.createFolder(htmlDirectory);
		fu.writeFile(_parseHtml.toString(), htmlLocation);
	}
	
}
