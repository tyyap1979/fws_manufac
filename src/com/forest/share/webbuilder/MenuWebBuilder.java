package com.forest.share.webbuilder;

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.constants.SessionConst;
import com.forest.common.util.CommonUtil;

public class MenuWebBuilder extends BaseWebBuilder {
//	private Logger logger = Logger.getLogger (MenuWebBuilder.class);
	public MenuWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler(String moduleId)throws Exception{		
		_mc.initModuleContent(_moduleEle);
		putMainData();
	}
	
	private void putMainData(){
		StringBuffer menuBuffer = new StringBuffer();
		HashMap dataMap = new HashMap();
		if(CommonUtil.isEmpty(_clientBean.getLoginUserId())){
			menuBuffer.append("<a href='sign-in.htm'>Sign-In</a>");
		}else{
			HashMap authMap = (HashMap) _req.getSession().getAttribute (SessionConst.SESSION_USER);
			menuBuffer.append("<a href='order.htm'>").append((String) authMap.get(SessionConst.USER_NAME)).append("</a>, ");
			menuBuffer.append("<a href='sign-in.htm?return=json&action1=signout'>Sign-Out</a>");
		}
		dataMap.put("menu", menuBuffer.toString());
		_mc.setElementContent(dataMap);
	}	
}
