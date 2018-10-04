package com.forest.common.builder;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.util.SelectBuilder;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;

public class SelectWebBuilder extends GenericAdminBuilder{
	private Logger logger = Logger.getLogger(this.getClass());
	
	public SelectWebBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);	
	} 
	
	public JSONObject requestJsonHandler()throws Exception{		
		logger.debug("-------------------- requestHandler: "+_reqAction);
		StringBuffer buffer = new StringBuffer();				
		String selectModule = _req.getParameter("mod");
		JSONObject json = new JSONObject();
		try{		
			json.put("options", getSelect(selectModule));			
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}
		
		return json;
	}

	private String getSelect(String param)throws Exception{		
		String rtnOption = null;
		String[] mod = null;
		StringBuffer selectBuffer = null;
		Class[] paramType = null;
		Object[] paramValue = null;				
		Method mthd = null;
		
		mod = param.split("-");
		paramType = new Class[4];
		paramValue = new Object[4];	
		
		paramType[0] = Connection.class;
		paramType[1] = ShopInfoBean.class;
		paramType[2] = String.class;
		paramType[3] = String.class;

		paramValue[0] = _dbConnection;
		paramValue[1] = _shopInfoBean;
		paramValue[2] = "";
		paramValue[3] = "";

		try{
			if("MFG_SelectBuilder".equals(mod[1])){
				mthd = MFG_SelectBuilder.class.getDeclaredMethod("getSELECT_"+mod[0], paramType);
				selectBuffer = (StringBuffer) mthd.invoke( MFG_SelectBuilder.class, paramValue);
			}		
			int start = selectBuffer.indexOf(">")+1;
			int end = selectBuffer.indexOf("</select>");			
			rtnOption = selectBuffer.substring(start, end);
		}catch(Exception e){
			logger.error(e, e);
			logger.debug("mod[0]="+mod[0]+", mod[1]"+mod[1]);			
		}
		logger.debug("getSelect rtnOption = "+rtnOption);
		return rtnOption;
	}
	
}
