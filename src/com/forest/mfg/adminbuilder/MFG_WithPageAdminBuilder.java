package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.util.FileHandler;

public class MFG_WithPageAdminBuilder extends GenericAdminBuilder {
	public MFG_WithPageAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		
		// Read Additional Content From File
		buffer.append(FileHandler.readAdminFile (_shopInfoBean, _clientBean));	
		return buffer;
	}
	
}
