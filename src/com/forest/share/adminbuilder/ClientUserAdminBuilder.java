package com.forest.share.adminbuilder;

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
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SendMail;
import com.forest.share.beandef.EmailTemplateDef;
import com.forest.share.beandef.GalleryDef;
import com.forest.share.beandef.GalleryDetailDef;
import com.forest.share.webbuilder.ShopWebBuilder;

public class ClientUserAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (ClientUserAdminBuilder.class);	
	
	public ClientUserAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);			
	} 
	
	public JSONObject requestJsonHandler()throws Exception{
		JSONObject json = null;
		
		json = super.requestJsonHandler();
		
		if(GeneralConst.UPDATE.equals (_reqAction)){
			
			if("Y".equals(_req.getParameter("activate"))){
				// Send Email
				SendMail sm = new SendMail(_shopInfoBean, _clientBean, _dbConnection);
				sm.sendEmail(_attrReqDataMap[0], "MEMBER_ACTIVATION");
			}
		}
		return json;
	}

	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		// Read Additional Content From File
		buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/"+_clientBean.getRequestFilename()));		
		return buffer;
	}
}