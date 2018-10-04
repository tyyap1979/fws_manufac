package com.forest.retail.adminbuilder;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;

public class RETAIL_TransactionAdminBuilder extends GenericAdminBuilder {
	public RETAIL_TransactionAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		
		// Read Additional Content From File
		buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath (_resources) + "html/"+_clientBean.getRequestFilename()));		
		return buffer;
	}
	
}
