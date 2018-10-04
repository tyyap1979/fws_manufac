package com.forest.retail.webbuilder;

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
import com.forest.share.webbuilder.BaseWebBuilder;

public class RETAIL_MenuWebBuilder extends BaseWebBuilder {
//	private Logger logger = Logger.getLogger (MenuWebBuilder.class);
	public RETAIL_MenuWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler(String moduleId)throws Exception{		
		_mc.initModuleContent(_moduleEle);
		putMainData();
	}
	
	private void putMainData(){		
		HashMap dataMap = new HashMap();
		dataMap.put("shoplongname", _shopInfoBean.getShopNameDesc());
		
		if(CommonUtil.isEmpty(_clientBean.getLoginUserId())){
			dataMap.put("before.signin.style", "");
			dataMap.put("after.signin.style", "display: none;");		
		}else{
			HashMap authMap = (HashMap) _req.getSession().getAttribute (SessionConst.SESSION_USER);
			dataMap.put("before.signin.style", "display: none;");
			dataMap.put("after.signin.style", "");
			dataMap.put("name", (String) authMap.get(SessionConst.USER_NAME));			
		}		
		_mc.setElementContent(dataMap);
	}	
}
