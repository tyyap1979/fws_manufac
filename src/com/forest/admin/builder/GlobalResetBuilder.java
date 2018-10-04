package com.forest.admin.builder;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.services.ThemesServices;
import com.forest.common.util.FileHandler;
import com.forest.common.util.OwnException;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;

public class GlobalResetBuilder
{			
	private Logger logger = Logger.getLogger (this.getClass ());
	private Connection _dbConnection = null;
	
	private static GlobalResetBuilder _instance;



	public static synchronized void removeInstance (){
		_instance = null;
	}
	
	public static synchronized GlobalResetBuilder getInstance(Connection conn) {
		if (_instance==null) {			
			_instance = new GlobalResetBuilder();	
			_instance._dbConnection = conn;
			_instance.reloadParameter();
		}else{
			try{
				if(_instance._dbConnection==null || _instance._dbConnection.isClosed ()){
					_instance._dbConnection = conn;
				}				
			}catch(Exception e){
				e.printStackTrace ();
			}
		}
		
		return _instance;
	}
	
	public void reloadParameter(){				
		try{									
//			FileHandler.removeAll();
			MFG_SelectBuilder.clean();
			ThemesServices.removeInstance();
			BeanDefUtil.clean();
		}catch(Exception e){
			logger.error (e, e);
		}
	}
	
	public StringBuffer requestHandler(HttpServletRequest req)throws OwnException{
		StringBuffer mBuffer = new StringBuffer();
		try{			
			if("gc".equals (req.getParameter ("action"))){
				logger.debug ("Perform Garbage Collection");
			    // Get a Runtime object
			    Runtime r = Runtime.getRuntime();
			    logger.debug("B4 GC : " + r.freeMemory () + " bytes");
				r.gc();
				logger.debug("After GC : " + r.freeMemory () + " bytes");
			}else{
				logger.debug ("Perform Clearing parameter and remove cache file");			
				reloadParameter();				
			}
		}catch(Exception e){
			logger.error (e, e);
		}
		
		return mBuffer;
	}	
}
