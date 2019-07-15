package com.forest.common.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import com.forest.common.bean.ShopInfoBean;
import com.forest.common.constants.GeneralConst;

public class ResourceUtil
{
//	public static final String ADMIN_I18N = "1";
//	public static final String SHOP_I18N = "2";
//	public static final String EXCEPTION_I18N = "3";
//	public static final String OWN_I18N = "4";
//	private static Logger logger = Logger.getLogger (ResourceUtil.class);
	
	/** initial size of the bundle cache */
    private static final int INITIAL_CACHE_SIZE = 25;

    /** capacity of cache consumed before it should grow */
    private static final float CACHE_LOAD_FACTOR = (float)1.0;
    
    private static HashMap cacheList = new HashMap(INITIAL_CACHE_SIZE, CACHE_LOAD_FACTOR);
    
    public static final String PATH_TYPE_REPORT = "report"; 
	
    public static void clearCustomProperties(){
    	cacheList.clear ();
    }
    
    public static void removeCustomProperties(String name){
    	cacheList.remove (name);
    }
    
    public static String getReportDirectory(ShopInfoBean shopInfoBean){	
    	StringBuffer location = new StringBuffer();				
		String companyGrp = shopInfoBean.getCompanyGroup();		
		String shopDomain = shopInfoBean.getShopDomain();
		
		location.append (ResourceUtil.getSettingValue ("company.path"));
		location.append (shopDomain).append("/");
		System.out.println("shopDomain: "+shopDomain);
		System.out.println("GeneralConst.SUBDOMAIN: "+GeneralConst.SUBDOMAIN);
		if(GeneralConst.SUBDOMAIN.equals(shopDomain)){			
			location.append (companyGrp).append ("/");
		}
		
		location.append (ResourceUtil.PATH_TYPE_REPORT).append ("/");
		System.out.println("Adam getReportDirectory location: "+location);
		return location.toString ();
    }
    
    public static String getSubReportDirectory(ShopInfoBean shopInfoBean){	
    	StringBuffer location = new StringBuffer();								
		String shopDomain = shopInfoBean.getShopDomain();
		
		location.append (ResourceUtil.getSettingValue ("company.path"));
		location.append (shopDomain).append("/");		
		location.append (shopInfoBean.getShopContext()).append ("/");
		
		location.append (ResourceUtil.PATH_TYPE_REPORT).append ("/");		
		return location.toString ();
    }
    
    public static String getCompanyMainPath(ShopInfoBean shopInfoBean){					
		StringBuffer location = new StringBuffer();		
		location.append (ResourceUtil.getSettingValue ("company.path"));
		location.append (shopInfoBean.getShopDomain()).append("/");		
		return location.toString ();
	}
        
	public static String getCompanyAdminPath(ShopInfoBean shopInfoBean){					
		StringBuffer location = new StringBuffer();		
		location.append (ResourceUtil.getSettingValue ("company.path"));
		location.append (shopInfoBean.getShopDomain()).append("/");		
		location.append (shopInfoBean.getShopContext()).append ("/");
		return location.toString ();
	}
	
	public static String getHTMLPath(String shopName){			
		StringBuffer location = new StringBuffer();		
		location.append (ResourceUtil.getSettingValue ("html.path"));
		location.append (ResourceUtil.getSettingValue ("admin.page.path"));

		location.append (shopName).append ("/");
//		location.append (templateName).append ("/");
		return location.toString ();	
	}	
	
	public static String getAdminHTMLPath(String business){			
		StringBuffer location = new StringBuffer();		
		location.append (ResourceUtil.getSettingValue ("html.path"));
		location.append (ResourceUtil.getSettingValue ("admin.page.path"));

		location.append("admin/").append (business).append ("/");
//		location.append (templateName).append ("/");
		return location.toString ();	
	}

	
	
//	private static Properties readProperty(String path)throws OwnException{
//		FileInputStream in = null;		
//		Properties props = null;
//		
//		try{
//			in = new FileInputStream(path);			
//			if(in!=null){
//				props = new Properties();
//				props.load(in);
//			}
//		}catch(Exception e){
//			throw new OwnException(e);
//		}finally{
//			try{
//				if(in!=null) in.close();
//			}catch(Exception e){
//				logger.error (e, e);
//			}
//		}
//		return props;		
//	}
//	
//	public static String getAdminResourceValue(_shopInfoBean.getBusiness(),"", String p_sKey, Locale locale){
//		String m_sValue = "";		
//		ResourceBundle m_oRB = null;
//		
//		try{
//			if(locale!=null){
//				m_oRB = ResourceBundle.getBundle("i18n.AdminResources", locale);
//			}else{
//				m_oRB = ResourceBundle.getBundle("i18n.AdminResources");
//			}
//			m_sValue = m_oRB.getString (p_sKey);
//		}catch(Exception e){
//			m_sValue = null;
//		}
//		return m_sValue;
//	}
	
	public static String getSettingValue(String p_sKey){
		String m_sValue = "";		
		ResourceBundle m_oRB = ResourceBundle.getBundle("i18n.Setting");
		
		try{
			m_sValue = m_oRB.getString (p_sKey);
		}catch(Exception e){
			m_sValue = null;
		}
		return m_sValue;
	}
	
	public static String getVersionValue(String p_sKey){
		String m_sValue = "";		
		ResourceBundle m_oRB = ResourceBundle.getBundle("i18n.version");
		
		try{
			m_sValue = m_oRB.getString (p_sKey);
		}catch(Exception e){
			m_sValue = null;
		}
		return m_sValue;
	}
	
//	public static String getAdminResourceValue(_shopInfoBean.getBusiness(),String module, String p_sKey, Locale locale){
//		return getAdminResourceValue(_shopInfoBean.getBusiness(),"", module, p_sKey, locale);
//	}
	
	public static String getAdminResourceValue(String business, String module, String p_sKey, Locale locale){
		String m_sValue = "";				
		ResourceBundle m_oRB = null;	
		String bundleName = null;
		try{
			
			if(CommonUtil.isEmpty(business)){
				bundleName = "i18n.AdminResources";
			}else{
				bundleName = "i18n."+business+".AdminResources";
			}

			if(locale!=null){
				m_oRB = ResourceBundle.getBundle(bundleName, locale);
			}else{
				m_oRB = ResourceBundle.getBundle(bundleName);
			}
			if(CommonUtil.isEmpty(module)){
				m_sValue = m_oRB.getString (p_sKey);
			}else{
				m_sValue = m_oRB.getString (module+".form."+p_sKey);
			}
		}catch(Exception e){					
			m_sValue = "??"+module+".form."+p_sKey+"??";			
		}
		return m_sValue;
	}
	
	public static String getShopResourceValue(String business, String module, String p_sKey, Locale locale){
		String m_sValue = "";				
		ResourceBundle m_oRB = null;
		String bundleName = null;
		try{
			if(CommonUtil.isEmpty(business)){
				bundleName = "i18n.ShopResources";
			}else{
				bundleName = "i18n."+business+".ShopResources";
			}
			
			if(locale!=null){
				m_oRB = ResourceBundle.getBundle(bundleName, locale);
			}else{
				m_oRB = ResourceBundle.getBundle(bundleName);
			}
			if(module!=null){
				m_sValue = m_oRB.getString (module+"."+p_sKey);
			}else{
				m_sValue = m_oRB.getString (p_sKey);
			}
//			logger.debug (module+"."+p_sKey + " = "+m_sValue);
		}catch(Exception e){
			m_sValue = null;
		}
		return m_sValue;
	}
	
	public static String getMallResourceValue(String module, String p_sKey, Locale locale){
		String m_sValue = "";				
		ResourceBundle m_oRB = null;		
		try{
			if(locale!=null){
				m_oRB = ResourceBundle.getBundle("i18n.MallResources", locale);
			}else{
				m_oRB = ResourceBundle.getBundle("i18n.MallResources");
			}
			
			if(module==null){
				m_sValue = m_oRB.getString (p_sKey);
			}else{
				m_sValue = m_oRB.getString (module+"."+p_sKey);
			}
		}catch(Exception e){
			m_sValue = null;
		}
		return m_sValue;
	}
	
	public static String getEmailResourceValue(String p_sKey, Locale locale){
		String m_sValue = "";				
		ResourceBundle m_oRB = null;		
		try{
			if(locale!=null){
				m_oRB = ResourceBundle.getBundle("i18n.EmailResources", locale);
			}else{
				m_oRB = ResourceBundle.getBundle("i18n.EmailResources");
			}

			m_sValue = m_oRB.getString (p_sKey);
		}catch(Exception e){
			m_sValue = null;
		}
		return m_sValue;
	}
}
