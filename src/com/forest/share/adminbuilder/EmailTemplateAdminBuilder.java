package com.forest.share.adminbuilder;

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SendMail;
import com.forest.share.beandef.EmailTemplateDef;
import com.forest.share.webbuilder.ShopWebBuilder;

public class EmailTemplateAdminBuilder extends GenericAdminBuilder {
	public EmailTemplateAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
		
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		
		// Read Additional Content From File
		buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/"+_clientBean.getRequestFilename()));		
		return buffer;
	}		
}
