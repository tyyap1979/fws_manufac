package com.forest.common.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;

public class FileHandler
{
	private static Logger logger = Logger.getLogger (FileHandler.class);
	
//	/** initial size of the bundle cache */
//    private static final int INITIAL_CACHE_SIZE = 25;
//
//    /** capacity of cache consumed before it should grow */
//    private static final float CACHE_LOAD_FACTOR = (float)1.0;
//    
//    private static SoftCache cacheList = new SoftCache(INITIAL_CACHE_SIZE, CACHE_LOAD_FACTOR);
	
//    public static void removeCustomProperties(String name){
//    	cacheList.remove (name);
//    }
//    
//    public static void removeAll(){
//    	cacheList.clear ();
//    }
//    
//    public static synchronized void setProcessFile(String cacheName, StringBuffer content){
//    	cacheList.put (cacheName, content);
//    }
    
	public static synchronized StringBuffer readAdminFile(ShopInfoBean shopInfoBean, ClientBean clientBean){
		return readAdminFile(shopInfoBean, clientBean, clientBean.getRequestFilename());
	}
    public static synchronized StringBuffer readAdminFile(ShopInfoBean shopInfoBean, ClientBean clientBean, String filename){
    	String path = shopInfoBean.getTemplatePath () + filename;
    	StringBuffer contentBuffer = readFile(null, path);

    	return contentBuffer;
    }
        
    public static synchronized StringBuffer readWebFile(ShopInfoBean shopInfoBean, ClientBean clientBean, String filename){
    	StringBuffer location = new StringBuffer();		
    	location.append (ResourceUtil.getSettingValue ("website.theme.path"));		
		location.append (shopInfoBean.getTheme()).append("/");		
		if(!CommonUtil.isEmpty(clientBean.getLanguage())){
			location.append (clientBean.getLanguage()).append("/");
		}
		location.append (filename);
		    	
    	logger.debug("readWebFile path = "+location.toString());
    	return readFile(null, location.toString());
    }
    
    public static StringBuffer readCustomPageFile(ShopInfoBean shopInfoBean, ClientBean clientBean, String filename){
    	StringBuffer location = new StringBuffer();		
    	location.append (ResourceUtil.getSettingValue ("company.path"));		
    	location.append (shopInfoBean.getShopDomain()).append("/page/");	
		location.append (filename);
		    	
    	logger.debug("readWebFile path = "+location.toString());
    	return readFile(null, location.toString());
    }
    
    public static void writeCustomPageFile(ShopInfoBean shopInfoBean, ClientBean clientBean, String filename, String content){
    	FileUtil fUtil = new FileUtil();
    	StringBuffer location = new StringBuffer();		
    	location.append (ResourceUtil.getSettingValue ("company.path"));		
		location.append (shopInfoBean.getShopDomain()).append("/page/");		
		location.append (filename);

    	try{
    		fUtil.writeFile(content, location.toString());
    	}catch (Exception e) {
    		logger.error(e, e);
		}
    }
    
    public static void removeCustomPageFile(ShopInfoBean shopInfoBean, ClientBean clientBean, String filename){
    	FileUtil fUtil = new FileUtil();
    	StringBuffer location = new StringBuffer();		
    	location.append (ResourceUtil.getSettingValue ("company.path"));		
		location.append (shopInfoBean.getShopDomain()).append("/page/");		
		location.append (filename);

    	try{
    		fUtil.deleteFolder(location.toString());
    	}catch (Exception e) {
    		logger.error(e, e);
		}
    }
//    public static synchronized StringBuffer readShopFile(ShopInfoBean shopInfoBean, ClientBean clientBean){
//    	String cacheName = shopInfoBean.getShopName()+"-"+shopInfoBean.getShopTemplate()+"-"+clientBean.getRequestFilename();
//    	String path = shopInfoBean.getTemplatePath () + shopInfoBean.getShopTemplate()+ "/" + clientBean.getRequestFilename();		
//    	
//    	return new StringBuffer(readFile(cacheName, path).toString());
//    }
//    
//    public static synchronized StringBuffer readShopFile(ShopInfoBean shopInfoBean, String filename){
//    	String cacheName = shopInfoBean.getShopName()+"-"+shopInfoBean.getShopTemplate()+"-"+filename;
//    	String path = shopInfoBean.getTemplatePath () + filename;		
//    	
//    	return new StringBuffer(readFile(cacheName, path).toString());
//    }
    
//    public static synchronized StringBuffer readShopFile(String shopName, String type, String filename){
//    	String cacheName = shopName+"-"+filename;
//    	String htmlLocation = ResourceUtil.getPath (shopName, type);
//    	String path = htmlLocation + filename;		
//    	
//    	return new StringBuffer(readFile(cacheName, path));
//    }
    
    public static synchronized StringBuffer readFile(String cacheName, String path){
    	logger.debug("readFile path = "+path);
    	StringBuffer mBuffer = null;
		FileInputStream fstream = null;
	    DataInputStream in = null;
	    BufferedReader br = null;
	    String strLine = null;
		try{			
			
//			if(cacheName!=null)
//				mBuffer = (StringBuffer) cacheList.get (cacheName);
			
			if(mBuffer==null){
//				String DOMAIN_NAME = ResourceUtil.getSettingValue ("domain.name");
				mBuffer = new StringBuffer();
				fstream = new FileInputStream(path);
		    	in = new DataInputStream(fstream);
		    	br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		    	
			    //Read File Line By Line
			    while ((strLine = br.readLine()) != null)   {
//			    	strLine = strLine.replaceAll ("%i18n.domain%", DOMAIN_NAME);
			    	mBuffer.append (strLine);
			    	mBuffer.append(System.getProperty("line.separator"));
			    }
			    
//			    if(cacheName!=null)
//			    	cacheList.put (cacheName, mBuffer);
			}
		}catch(Exception e){
			//logger.error (e, e);
		}finally{
			try{
				if(br!=null) br.close (); br = null;
	    		if(in!=null) in.close (); in = null;
		    	if(fstream!=null) fstream.close (); fstream = null;
			}catch (Exception e) {
				logger.error (e, e);
			}
		}

	    return mBuffer;
    }
}
