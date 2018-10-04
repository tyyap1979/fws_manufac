package com.forest.share.webbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.WebsiteDef;

public class WebInfoWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (WebInfoWebBuilder.class);
	public WebInfoWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler(String moduleId)throws Exception{		
		_mc.initModuleContent(_moduleEle);
		putMainData();
	}
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append("a.").append(WebsiteDef.html_title);
		query.append(",a.").append(WebsiteDef.html_keyword);
		query.append(",a.").append(WebsiteDef.html_description);
		query.append(",a.").append(WebsiteDef.facebook_id);		
		query.append(", coalesce(a.").append(WebsiteDef.facebook_url).append(", '#') As ").append(WebsiteDef.facebook_url);
		query.append(", coalesce(a.").append(WebsiteDef.twitter_url).append(", '#') As ").append(WebsiteDef.twitter_url);
		query.append(", coalesce(a.").append(WebsiteDef.youtube_url).append(", '#') As ").append(WebsiteDef.youtube_url);
		query.append(" From ").append(WebsiteDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(WebsiteDef.companyid).append("=?");
		 
		paramMap.put(ShopBeanDef.companyid, _shopInfoBean.getShopName());
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			loopContent = (HashMap) dataArray.get(0);
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
}
