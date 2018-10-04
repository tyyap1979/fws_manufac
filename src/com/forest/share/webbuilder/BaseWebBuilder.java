package com.forest.share.webbuilder;

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.services.GenericServices;
import com.forest.common.util.ModuleContent;

public class BaseWebBuilder {
	protected ClientBean _clientBean = null;
	protected ShopInfoBean _shopInfoBean = null;
	protected Connection _dbConnection = null;
	protected HttpServletRequest _req = null;
	protected HttpServletResponse _resp = null;
	protected Element _moduleEle = null;
	protected GenericServices _gs = null;
	protected ModuleContent _mc = new ModuleContent();
	
	public BaseWebBuilder(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn, HttpServletRequest req, 
			HttpServletResponse resp, Element moduleEle){
		_clientBean = clientBean;
		_shopInfoBean = shopInfoBean;
		_dbConnection = conn;		
		_req = req;
		_resp = resp;
		_moduleEle = moduleEle;
		_gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
	}
	
	public void displayHandler(String moduleID)throws Exception{		
	}
	
	public HashMap displayHandlerWithHashMap(String moduleID)throws Exception{
		return null;
	}
	
	public Object requestJsonHandler()throws Exception{		
		return null;
	}
}
