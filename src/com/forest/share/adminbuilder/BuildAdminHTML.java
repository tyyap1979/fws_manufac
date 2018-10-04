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
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.builder.ModuleConfig;
import com.forest.common.builder.ShopAdminBuilder;
import com.forest.common.builder.AdminBuilder;
import com.forest.common.constants.BusinessConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.ThemesServices;
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
import com.forest.retail.beandef.RETAIL_CategoryDef;
import com.forest.share.beandef.SmsTemplateDef;

public class BuildAdminHTML {
	private Logger logger = Logger.getLogger (this.getClass ());
	private ShopInfoBean _shopInfoBean = null;
	private ClientBean _clientBean 	= null;
	private Connection _dbConnection = null;
	private HttpServletRequest _req = null;
	private HttpServletResponse _resp = null;
	private StringBuffer _parseHtml = null;

	public BuildAdminHTML(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn,HttpServletRequest req, HttpServletResponse resp)throws Exception{
		_shopInfoBean = shopInfoBean;
		_clientBean = clientBean;
		_dbConnection = conn;
		_req = req;
		_resp = resp;
	}
	
	private String[] getCreateSuperFilesArray(){
		String[] files = {"dashboard.htm", "shop.htm","adminuser.htm","module-admin.htm","module.htm"
				};
		return files;
	}
	
	private String[] getMFGCreateFilesArray(){
		String[] files = {"dashboard.htm", "supplier.htm","customer.htm","agentprofile.htm","autonumber.htm",
				"mfg_transaction.htm","mfg_po.htm","mfg_receivepayment.htm","mfg_statement.htm",
				"mfg_debitnote.htm","mfg_creditnote.htm","mfg_report.htm","mfg_rawtoproduct.htm",
				"mfg_custproduct.htm","mfg_prodoption.htm","mfg_supplierproduct.htm","mfg_supplierprodoption.htm",
				"mfg_raw.htm","mfg_grouping.htm","mfg_paymentvoucher.htm","mfg_supplierinvoice.htm","adminuser.htm",
				"pagecontent.htm","gallery.htm","material.htm","clientuser.htm","shipmentdomestic.htm","paymentmethod.htm",
				"shopprofile.htm","websiteprofile.htm","smstemplate.htm","sms.htm","emailtemplate.htm"
				};
		return files;
	}
	
	private String[] getIDCreateFilesArray(){
		String[] files = {"dashboard.htm","adminuser.htm","websiteprofile.htm",
				"shopprofile.htm","pagecontent.htm","gallery.htm","material.htm","smstemplate.htm","sms.htm","emailtemplate.htm"};
		return files;
	}
	
	private String[] getRetailCreateFilesArray(){
		String[] files = {"dashboard.htm","adminuser.htm","websiteprofile.htm",
				"clientuser.htm","shipmentdomestic.htm","paymentmethod.htm",
				"shopprofile.htm","pagecontent.htm","gallery.htm","emailtemplate.htm"};
		return files;
	}

	public void buildHTML(Class defClass)throws Exception{
		HashMap reqMap = ThemesServices.getInstance().getShopInfo(_shopInfoBean);
		if(defClass.toString().indexOf("MFG_")!=-1){
			buildMfgHTML(defClass, reqMap);
		}else if(defClass.toString().indexOf("RETAIL_")!=-1){
			buildRetailHTML(defClass, reqMap);
		}else{
			buildCommonHTML(defClass, reqMap);
		}
	}
	
	public void buildHTML(Class defClass, String companyid)throws Exception{	
		HashMap reqMap = new AdminSQLServices(_shopInfoBean,_clientBean, _dbConnection).getCompanyMap(companyid);
		if(defClass.toString().indexOf("MFG_")!=-1){
			buildMfgHTML(defClass, reqMap);
		}else if(defClass.toString().indexOf("RETAIL_")!=-1){
			buildRetailHTML(defClass, reqMap);
		}else{
			buildCommonHTML(defClass, reqMap);
		}
	}
	
	private void buildCommonHTML(Class defClass, HashMap reqMap)throws Exception{
		String pageName = null;		
		String[] files = null;
		if (defClass==AgentProfileDef.class){
			pageName = "mfg_transaction.htm,customer.htm";	
		}else if (defClass==SmsTemplateDef.class){
			pageName = "sms.htm";	
		}
		try{			
			if(!CommonUtil.isEmpty(pageName)){
				files = pageName.split(",");
				reqMap.put(ShopBeanDef.language.name, _shopInfoBean.getDefaultLanguage());
				logger.debug("pageName = "+pageName);
				buildHTML(reqMap, files);
			}
		}catch(Exception e){
			logger.error(e, e);
		}
	}
	
	private void buildRetailHTML(Class defClass, HashMap reqMap)throws Exception{
		String pageName = null;		
		String[] files = null;
		if(defClass==RETAIL_CategoryDef.class){
			pageName = "retail_product.htm";		
		}
		try{			
			if(!CommonUtil.isEmpty(pageName)){
				files = pageName.split(",");
				reqMap.put(ShopBeanDef.language.name, _shopInfoBean.getDefaultLanguage());
				logger.debug("pageName = "+pageName);
				buildHTML(reqMap, files);
			}
		}catch(Exception e){
			logger.error(e, e);
		}
	}
	
	private void buildMfgHTML(Class defClass, HashMap reqMap)throws Exception{
		String pageName = null;		
		String[] files = null;
		if(defClass==MFG_CustProductDef.class){
			pageName = "mfg_rawtoproduct.htm,mfg_transaction.htm";		
		}else if (defClass==MFG_ProductOptionDef.class){
			pageName = "mfg_custproduct.htm,mfg_transaction.htm";		
		}else if(defClass==MFG_SupplierProductDef.class){
			pageName = "mfg_transaction.htm";	
		}else if (defClass==MFG_SupplierProductOptionDef.class){
			pageName = "mfg_supplierproduct.htm,mfg_transaction.htm";		
		}else if (defClass==MFG_RawDef.class){
			pageName = "mfg_prodoption.htm,mfg_rawtoproduct.htm";	
		}else if (defClass==MFG_Grouping.class){
			pageName = "mfg_raw.htm,mfg_custproduct.htm,mfg_transaction.htm";	
		}
		try{			
			if(!CommonUtil.isEmpty(pageName)){
				files = pageName.split(",");
				reqMap.put(ShopBeanDef.language.name, _shopInfoBean.getDefaultLanguage());
				logger.debug("pageName = "+pageName);
				buildHTML(reqMap, files);
			}
		}catch(Exception e){
			logger.error(e, e);
		}
	}
	
	public void buildHTML(HashMap reqMap)throws Exception{	
		String[] files = null;
		String shopName = (String) reqMap.get(ShopBeanDef.companyid.name);
		String business = (String) reqMap.get(ShopBeanDef.business.name);
		try{
			if(BusinessConst.MANUFACTURING.equals(business)){
				files = getMFGCreateFilesArray();
			}else if(BusinessConst.INTERIOR_DESIGN.equals(business)){
				files = getIDCreateFilesArray();
			}else if(BusinessConst.RETAIL.equals(business)){
				files = getRetailCreateFilesArray();
			}
			buildHTML(reqMap, files);
		}catch(Exception e){
			logger.error(e, e);
		}
	}
	
	private void buildHTML(HashMap reqMap, String[] files)throws Exception{		
		String htmlDirectory = null;
//		ArrayList companyGroup = null;
		logger.debug("buildHTML reqMap = "+reqMap);
		String shopName = (String) reqMap.get(ShopBeanDef.companyid.name);
		String shopDomain = (String) reqMap.get(ShopBeanDef.shopdomain.name); 	
		String adminContext = (String) reqMap.get(ShopBeanDef.admincontext.name); 
		String companyGrp  = (String) reqMap.get(ShopBeanDef.companygroup.name);
		String language  = (String) reqMap.get(ShopBeanDef.language.name);
		String business  = (String) reqMap.get(ShopBeanDef.business.name);
		
		ClientBean clientBean = new ClientBean();
		clientBean.setLocale (new Locale(language));
		
		ShopInfoBean shopInfoBean = new ShopInfoBean();
		shopInfoBean.setShopDomain(shopDomain);
		shopInfoBean.setShopName(shopName);
		shopInfoBean.setBusiness(business);
		shopInfoBean.setModule("admin");
		shopInfoBean.setCompanyGroup(companyGrp);
				
		int fileCount = files.length;
		
		if(GeneralConst.SUBDOMAIN.equals(shopDomain)){
			companyGrp = "";
		}else{
			if(CommonUtil.isEmpty(companyGrp)){
				companyGrp = shopName;
			}
		}		
		
		shopInfoBean.setMasterShopName(shopName);
		if(CommonUtil.isEmpty(adminContext)){
			adminContext = shopName;
		}
		shopInfoBean.setShopDomain((String) reqMap.get(ShopBeanDef.shopdomain.name));
		shopInfoBean.setShopContext((String) reqMap.get(ShopBeanDef.admincontext.name));
		shopInfoBean.setShopName((String) reqMap.get(ShopBeanDef.companyid.name));
		shopInfoBean.setSelectedShop((String) reqMap.get(ShopBeanDef.companyid.name));
		shopInfoBean.setTheme((String) reqMap.get(ShopBeanDef.theme.name));
		shopInfoBean.setShopNameDesc((String) reqMap.get(ShopBeanDef.shoplongname.name));	
		
		for(int x=0; x<fileCount; x++){
			clientBean.setRequestFilename(files[x]);
			
			htmlDirectory = ResourceUtil.getCompanyAdminPath (shopInfoBean);
			logger.debug("htmlDirectory: "+htmlDirectory);
			buildHTML(shopName, htmlDirectory, files[x], shopInfoBean, clientBean);
		}
		
	}
	
	public void buildHTML(String shopName, String htmlDirectory, String page, ShopInfoBean shopInfoBean, ClientBean clientBean)throws Exception{		
		AdminBuilder wb = new AdminBuilder(shopInfoBean, clientBean, _dbConnection, null); 
		HashMap hashPageMap = (HashMap) ModuleConfig.filenameToClass.get(page);		
		GenericAdminBuilder genAdminBuilder = null;		
		TemplateProcess tp = new TemplateProcess();
		FileUtil fu = new FileUtil();
		String templatePath = null;
		String htmlLocation =  htmlDirectory + page + "l"; // a.htm -> a.html
		
		logger.debug("hashPageMap = "+hashPageMap);
		logger.debug("htmlLocation = "+htmlLocation);
		if("admin".equals(shopInfoBean.getModule())){
			if(ThemesServices.getInstance().isCommonPage(clientBean.getRequestFilename())){
				templatePath = ResourceUtil.getAdminHTMLPath ("common") + "html/";
			}else{
				templatePath = ResourceUtil.getAdminHTMLPath (shopInfoBean.getBusiness()) + "html/";
			}
		}else{
			templatePath = ResourceUtil.getHTMLPath (shopInfoBean.getShopName()) ;
		}				
		shopInfoBean.setTemplatePath (templatePath);
		
		if(hashPageMap!=null){
    		genAdminBuilder = (GenericAdminBuilder)wb.getWebBuilder((Class) hashPageMap.get(ModuleConfig.HANDLE_BUILDER), hashPageMap, _req, _resp);	    			    		
		}
		if(hashPageMap!=null && hashPageMap.get(ModuleConfig.HANDLE_BUILDER) != null){		    			
			_parseHtml = genAdminBuilder.displayHandler();			    			
		}else{		    		    
			_parseHtml = FileHandler.readAdminFile (shopInfoBean, clientBean);
		}
		
		_parseHtml = tp.initTemplateProcess (_parseHtml, shopInfoBean.getTemplatePath ());
		
		Iterator it = ModuleConfig.pagePrefixWebBuilder.keySet().iterator();
		String key = null;
		while(it.hasNext()){
			key = (String) it.next();	    				
			
			if(_parseHtml.indexOf (key)!=-1){    					
				logger.debug("key = "+key);
				genAdminBuilder = (GenericAdminBuilder)wb.getWebBuilder((Class) ModuleConfig.pagePrefixWebBuilder.get(key), null, _req, _resp);	
				genAdminBuilder.displayHandler(_parseHtml);
			}
		}

	    if(_parseHtml.indexOf ("system.admin.company.bar")!=-1){
	    	genAdminBuilder = (GenericAdminBuilder)wb.getWebBuilder(ShopAdminBuilder.class, null, _req, _resp);
	    	genAdminBuilder.displayHandler (_parseHtml);
	    }
	    	    
//		replacePath(_parseHtml);
		insertCommonProperties(clientBean);
		if(hashPageMap!=null){
			replaceI18n((String) hashPageMap.get(ModuleConfig.RESOURCES),shopInfoBean, clientBean);
		}else{
			replaceI18n(null,shopInfoBean, clientBean);
		}
		
		
//		logger.debug("_parseHtml = "+_parseHtml);
		fu.createFolder(htmlDirectory);
		fu.writeFile(_parseHtml.toString(), htmlLocation);
	}
	
	private void replaceI18n(String businessType, ShopInfoBean shopInfoBean, ClientBean clientBean){
		String key = null;
	    String value = null;			    
	    int fromIndex = 0;
	    String START_TAG = "%";
		String END_TAG = "%";
		
	    while((fromIndex = _parseHtml.indexOf (START_TAG+"i18n", 0)) != -1){	
	    	key = _parseHtml.substring (fromIndex + START_TAG.length (), _parseHtml.indexOf (END_TAG, fromIndex+1));
	    	key = key.trim ();
	    	
	    	if(key!=null){
	    		
	    		if(key.indexOf("i18n.domain")!=-1){
	    			value = ResourceUtil.getSettingValue ("domain.name");
	    		}else if(key.indexOf("i18n.version")!=-1){
	    			value = ResourceUtil.getVersionValue(key);
	    		}else if(key.indexOf("i18n.theme")!=-1){
	    			value = shopInfoBean.getTheme();
	    		}else{	    		
//		    		if(key.indexOf ("i18n.system")!=-1){
		    			value = ResourceUtil.getAdminResourceValue (businessType, null, key, clientBean.getLocale ());
//		    		}else{
//		    			value = ResourceUtil.getShopCustomResourceValue (_shopInfoBean.getShopName (), key, _clientBean.getLocale ());
//		    		}			    
	    		}
	    		
	    		value = (value==null)?"??"+key+"??":value;	    			    	
	    		_parseHtml.replace(fromIndex, (fromIndex + START_TAG.length () + key.length () + END_TAG.length ()), value);
	    	}
	    }
	}
	
	private void insertCommonProperties(ClientBean clientBean){
		// js i18n
	    if(_parseHtml.indexOf ("</body>")!=-1){
			StringBuffer mBuffer = new StringBuffer();
			mBuffer.append ("<div style=\"display: none\">");
				mBuffer.append ("<span id=\"js-processing-request\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.processing.request", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-ajax-fail\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.ajax.fail", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-save\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.save", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-upload\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.upload", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-add\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.add", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-remove\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.remove", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-refresh-search\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.refresh.search", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-close\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.close", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-show\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.show", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-hide\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.hide", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-latest-feature-once\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.latest.feature.once", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-yes\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.yes", clientBean.getLocale ())).append ("</span>");
//				mBuffer.append ("<span id=\"js-button-no\">").append (ResourceUtil.getAdminResourceValue(null,"", "js.button.no", clientBean.getLocale ())).append ("</span>");
				
				mBuffer.append ("<span id=\"js-required\">").append (ResourceUtil.getShopResourceValue (null,"js", "required", clientBean.getLocale ())).append ("</span>");
			mBuffer.append ("</div>");	
		
			_parseHtml.insert (_parseHtml.indexOf ("</body>"), mBuffer);
		}
	}
}
