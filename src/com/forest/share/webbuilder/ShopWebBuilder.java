package com.forest.share.webbuilder;

import java.sql.Connection;
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

public class ShopWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (ShopWebBuilder.class);
	public ShopWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);		
	}
	
	public void displayHandler(String moduleId)throws Exception{		
		_mc.initModuleContent(_moduleEle);
		putMainData();
	}
	
	private void putMainData(){
		try{
			_mc.setElementContent(getShopInfo());
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
	
	public HashMap getShopInfo()throws Exception{
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append("a.").append(ShopBeanDef.shoplongname);
		query.append(",a.").append(ShopBeanDef.address);
		query.append(",b.").append(WebsiteDef.latlng);
		query.append(",a.").append(ShopBeanDef.contact_person);
		query.append(",a.").append(ShopBeanDef.contact_phone);
		query.append(",a.").append(ShopBeanDef.contact_email);
		query.append(",a.").append(ShopBeanDef.phone);
		query.append(",a.").append(ShopBeanDef.fax);
				
		query.append(" From ").append(ShopBeanDef.TABLE).append(" a");;
		query.append(" Left Join ").append(WebsiteDef.TABLE).append(" b On b.");
		query.append(WebsiteDef.companyid).append("=a.").append(ShopBeanDef.companyid);
		
		query.append(" Where");
		query.append(" a.").append(ShopBeanDef.companyid).append("=?");
		 
		paramMap.put(ShopBeanDef.companyid, _shopInfoBean.getShopName());
		try{
			loopContent = _gs.searchDataMap(query, paramMap);			
		}catch(Exception e){
			throw e;
		}			
		return loopContent;
	}
}
