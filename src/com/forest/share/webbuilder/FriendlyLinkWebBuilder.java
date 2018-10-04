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
import com.forest.share.beandef.FriendlyLinkDef;

public class FriendlyLinkWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (FriendlyLinkWebBuilder.class);
	public FriendlyLinkWebBuilder(ShopInfoBean shopInfoBean,
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
		query.append("a.").append(FriendlyLinkDef.url);
		query.append(",a.").append(FriendlyLinkDef.title);
		query.append(",a.").append(FriendlyLinkDef.description);		
		
		query.append(" From ").append(FriendlyLinkDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(FriendlyLinkDef.companyid).append("=?");
		 
		paramMap.put(FriendlyLinkDef.companyid, _shopInfoBean.getShopName());
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			int count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);					
				_mc.setLoopElementContent(loopContent);
			}									
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
}
