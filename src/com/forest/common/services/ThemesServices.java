package com.forest.common.services;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.WebsiteDef;
import com.forest.common.builder.ModuleConfig;
import com.forest.common.constants.ExceptionConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.ThemesServices;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CryptUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.services.AdminSQLServices;

public class ThemesServices
{
	private Logger logger = Logger.getLogger (this.getClass ());
	private HashMap _shopTemplate = null;
	private HashMap _shopWebSite = null;
	private static ThemesServices _instance = null;	
	private Connection _dbConnection = null;
	
	private ThemesServices(){
		try{
			_dbConnection = new DBUtil().getDbConnection();
		}catch(Exception e){
			logger.error (e, e);
		}
	}
	
	public static synchronized void removeInstance (){
		_instance = null;
	}
	
	public static synchronized ThemesServices getInstance ()
	{
		try{
			if (_instance == null){					
				_instance = new ThemesServices ();		
				_instance.reloadTheme ();				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return _instance;
	}	
	
	public void reloadTheme(){
		reloadTheme(_dbConnection);
		DBUtil.free(_dbConnection, null, null);
	}
		
	public void reloadTheme(Connection conn){
		logger.debug ("---reloadTheme-- -");				
		HashMap map = null;
		
		ArrayList mArrayListSearch = null;								
		AdminSQLServices ass = new AdminSQLServices(null, null, conn);
		
		mArrayListSearch = ass.getShopsSetting(null);
		String key = null;
		String email_passwd = null;
		String adminContext = null;
		String companyGroup = null;
		try{
			_shopTemplate = new HashMap();			
			for(int i=0; i<mArrayListSearch.size (); i++){
				map = (HashMap) mArrayListSearch.get (i);				
				adminContext = (String) map.get(ShopBeanDef.admincontext.name);
				companyGroup = (String) map.get(ShopBeanDef.companygroup.name);
				if(CommonUtil.isEmpty(adminContext)){
					if(CommonUtil.isEmpty(companyGroup)){
						adminContext = "admin";
					}else{
						adminContext = (String) map.get(ShopBeanDef.companyid.name);
					}
				}
				map.put(ShopBeanDef.admincontext.name, adminContext);
				key = (String) map.get(ShopBeanDef.shopdomain.name) + adminContext;
//				logger.debug("key = "+key);
				_shopTemplate.put (key, map);						
			}
//			logger.debug("_shopTemplate = "+_shopTemplate);	
			
			// Load WebSite Setting 
			_shopWebSite = new HashMap();	
			mArrayListSearch = ass.getShopsWebSite(null);
			for(int i=0; i<mArrayListSearch.size (); i++){
				map = (HashMap) mArrayListSearch.get (i);
				key = (String) map.get(WebsiteDef.domain.name);
				email_passwd = (String) map.get(WebsiteDef.email_passwd.name);				
				if(!CommonUtil.isEmpty(email_passwd)){					
					map.put(WebsiteDef.email_passwd.name, CryptUtil.getInstance ().decrypt(email_passwd));
				}
				_shopWebSite.put(key, map);
			}
		}catch (Exception e) {
			logger.error (e, e);
		}finally{
			//DBUtil.free(conn, null, null);
		}
	}
	
	public HashMap getShopInfo(ShopInfoBean shopInfoBean){		
		return (HashMap) _shopTemplate.get (shopInfoBean.getShopDomain()+shopInfoBean.getShopContext());				
	}
	
	public void updateTheme(String shopName, String templateName){
		HashMap map = null;
		
		logger.debug ("shopName = "+shopName);
		map = (HashMap) _shopTemplate.get (shopName);				
		map.put (ShopBeanDef.default_template.name, templateName);

		logger.debug ("_shopTemplate = "+_shopTemplate);
	}
	
	public void removeShop(String shopName){		
		_shopTemplate.remove (shopName);				
	}
	
	public void updateShop(HashMap reqMap){		
		String shopDomain = (String) reqMap.get(ShopBeanDef.shopdomain.name);
		String adminContext = (String) reqMap.get(ShopBeanDef.admincontext.name);		
		
		_shopTemplate.put (shopDomain+adminContext, reqMap);
//		logger.debug ("updateShop = "+map);
	}
	
	public void setTemplate(ShopInfoBean mBean, String shopName){		
		String themeName = null; 		
		HashMap map = null;
//		logger.debug ("_shopTemplate = "+_shopTemplate);
		map = (HashMap) _shopTemplate.get (shopName);
//		logger.debug (shopName + " setTemplate map = "+map);
		themeName = (String) map.get (ShopBeanDef.default_template.name);
		
		if("admin".equals (shopName) && themeName==null){
			themeName = "standard01";
		}
		themeName = (themeName==null)?"standard01":themeName;
		mBean.setShopTemplate (themeName);
	}
	
	public ArrayList getCompanyGroup(String shopName){	
		HashMap map = (HashMap) _shopTemplate.get (shopName);
		ArrayList companyGroup = null;
		if(map.get (ShopBeanDef.companygroup.name)!=null){
			companyGroup = (ArrayList) map.get (ShopBeanDef.companygroup.name);
		}
		return companyGroup;
	}
	
	public String[] getAdminContext(String domainName, String contextRoot){	
		logger.debug("domainName+contextRoot = "+domainName+contextRoot);
		
		HashMap map = (HashMap) _shopTemplate.get (domainName+contextRoot);
		String[] adminContext = new String[2];
		logger.debug(map);		
		if(map!=null){
			adminContext[0] = (String) map.get(ShopBeanDef.companyid.name);
			adminContext[1] = (String) map.get(ShopBeanDef.admincontext.name);
			logger.debug("getAdminContext "+adminContext[0]+"="+adminContext[1]);
		}else{
			logger.debug("_shopTemplate = "+_shopTemplate);
		}
		
		return adminContext;
	}
	public void assignShopSetting(ShopInfoBean shopInfoBean, ClientBean clientBean)throws OwnException{		
		HashMap map = null;
		String themeName = null;
		String htmlLocation = null;
		map = (HashMap) _shopTemplate.get (shopInfoBean.getShopDomain()+shopInfoBean.getShopContext());
		logger.debug(shopInfoBean.getShopDomain() + " = "+map);
		
		try{
			if(map!=null){
				shopInfoBean.setShopName((String) map.get (ShopBeanDef.companyid.name));
				shopInfoBean.setShopNameDesc((String) map.get (ShopBeanDef.shoplongname.name));
				
				themeName = (String) map.get (ShopBeanDef.theme.name);
				themeName = CommonUtil.isEmpty(themeName)?GeneralConst.DEFAULT_THEME:themeName;
				shopInfoBean.setMasterShopName(shopInfoBean.getShopName ());
				shopInfoBean.setTheme (themeName);
//				shopInfoBean.setDefaultCurrency ((String) map.get (ShopBeanDef.currencyiso.name));
				
				if(map.get (ShopBeanDef.companygroup.name)!=null){
					shopInfoBean.setCompanyGroup ((String) map.get (ShopBeanDef.companygroup.name));
				}
				shopInfoBean.setPaymentCurrency ((String) map.get (WebsiteDef.paycurrencyiso.name));
				shopInfoBean.setDefaultLanguage ((String) map.get (ShopBeanDef.language.name));
				shopInfoBean.setShopCountryISO ((String) map.get (WebsiteDef.countryiso.name));
				shopInfoBean.setBusiness ((String) map.get (ShopBeanDef.business.name));
				shopInfoBean.setSelectedShop (shopInfoBean.getShopName ());				
				
				if("admin".equals(shopInfoBean.getModule())){
					if(isCommonPage(clientBean.getRequestFilename())){
						htmlLocation = ResourceUtil.getAdminHTMLPath ("common") + "html/";
					}else{
						htmlLocation = ResourceUtil.getAdminHTMLPath (shopInfoBean.getBusiness()) + "html/";
					}
				}else{
					htmlLocation = ResourceUtil.getHTMLPath (shopInfoBean.getShopName()) ;
				}
				logger.debug("htmlLocation = "+htmlLocation);
				shopInfoBean.setTemplatePath (htmlLocation);
			}else{
				logger.debug (shopInfoBean.getShopName () + " = _shopTemplate = "+_shopTemplate);
				throw new OwnException(ExceptionConst.NO_SHOP);
			}
		}catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	public void assignShopWebSiteSetting(ShopInfoBean shopInfoBean, ClientBean clientBean)throws OwnException{		
		HashMap map = null;		
		String htmlLocation = null;
		map = (HashMap) _shopWebSite.get (shopInfoBean.getShopDomain());
//		logger.debug("_shopWebSite = "+_shopWebSite);
//		logger.debug("shopInfoBean.getShopDomain() = "+shopInfoBean.getShopDomain());
//		logger.debug("assignShopWebSiteSetting = "+map);
		
		try{
			if(map!=null){
				shopInfoBean.setShopName((String) map.get (WebsiteDef.companyid.name));
				shopInfoBean.setShopNameDesc((String) map.get (ShopBeanDef.shoplongname.name));				
//				shopInfoBean.setDefaultCurrency ((String) map.get (ShopBeanDef.currencyiso.name));
				shopInfoBean.setPaymentCurrency ((String) map.get (WebsiteDef.paycurrencyiso.name));
				shopInfoBean.setDefaultLanguage ((String) map.get (ShopBeanDef.language.name));
				shopInfoBean.setShopCountryISO ((String) map.get (WebsiteDef.countryiso.name));
				shopInfoBean.setBusiness ((String) map.get (ShopBeanDef.business.name));
				shopInfoBean.setSelectedShop (shopInfoBean.getShopName ());
				shopInfoBean.setTheme ((String) map.get (WebsiteDef.theme.name));	
				
				StringBuffer location = new StringBuffer();		
				location.append (ResourceUtil.getSettingValue ("website.theme.path"));		
				location.append (shopInfoBean.getTheme()).append("/");
				shopInfoBean.setTemplatePath(location.toString());
				
			}else{				
				logger.debug("shopInfoBean.getShopDomain(): "+shopInfoBean.getShopDomain());
				throw new OwnException(ExceptionConst.NO_SHOP);
			}
		}catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	public boolean isCommonPage(String requestFilename){
		boolean isCommon = false;
		if(ModuleConfig.commonFile.get(requestFilename)!=null){
			isCommon = true;
		}

		
		return isCommon;
	}
//	
//	public String getApplicationResourcePath(){
//		return "C:/Adam/Developement/ApplicationResources";
//	}

	public HashMap getShopWebSite(String domain) {
		return (HashMap)_shopWebSite.get(domain);
	}
}
