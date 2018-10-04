package com.forest.retail.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.retail.beandef.RETAIL_CategoryDef;
import com.forest.retail.beandef.RETAIL_CategoryDetailDef;
import com.forest.share.adminbuilder.BuildAdminHTML;
import com.forest.share.adminbuilder.UploadAdminBuilder;

public class RETAIL_CategoryAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (RETAIL_CategoryAdminBuilder.class);
	public RETAIL_CategoryAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		subClass = BeanDefUtil.getSubClass(_defClass);
		json = super.requestJsonHandler();
		
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction) || GeneralConst.DELETE.equals (_reqAction)){	
			RETAIL_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
//			new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);
		}
		return json;
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		
		// Read Additional Content From File
		buffer.append(FileHandler.readAdminFile (_shopInfoBean, _clientBean));	
		return buffer;
	}		
}
