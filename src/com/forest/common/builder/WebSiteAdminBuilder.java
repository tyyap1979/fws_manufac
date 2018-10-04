package com.forest.common.builder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.WebsiteDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.ThemesServices;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.services.AdminSQLServices;
import com.forest.share.adminbuilder.BuildWebHTML;
import com.forest.share.adminbuilder.BuildAdminHTML;

public class WebSiteAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (WebSiteAdminBuilder.class);
	
	public WebSiteAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);	
	} 
	
	public JSONObject requestJsonHandler()throws Exception{
		JSONObject json = super.requestJsonHandler();
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){						
			BuildWebHTML build = new BuildWebHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp);
			build.buildHTML(_attrReqDataMap[0]);			
			
			ThemesServices.getInstance().reloadTheme(_dbConnection);
		}
		logger.debug("json = "+json);
		return json;
	}

	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = null;
		
		if("website.htm".equals(_clientBean.getRequestFilename())){
			buffer = super.displayHandler();
		}else{
			ArrayList addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			int addSize = addArray.size();	 
			HashMap listMap = null;
			DataObject dataObject = null;
	    	for(int i=0; i<addSize; i++){	    			    		
	    		listMap = (HashMap) addArray.get(i);
	    		dataObject 	= (DataObject) listMap.get("object");	
	    		if(dataObject.name.equals(WebsiteDef.companyid.name)){
	    			listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
	    		}
	    		if(dataObject.name.equals(WebsiteDef.domain.name)){
	    			listMap.put("type", String.valueOf(ComponentConst.TEXT));
	    		}
	    	}
			buffer = super.displayHandlerEntry(addArray);			
			// Read Additional Content From File
			buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/" + _clientBean.getRequestFilename()));	
		}			
		return buffer;
	}	
}