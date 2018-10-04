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
import com.forest.common.util.CommonUtil;
import com.forest.common.util.FileHandler;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.share.beandef.PageContentDef;

public class PageContentWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (PageContentWebBuilder.class);
	public PageContentWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler(String moduleId)throws Exception{		
		_mc.initModuleContent(_moduleEle);
		if("page.list".equals(moduleId)){
			putListData();
		}else if("html.page.content".equals(moduleId)){
			putHTMLData();
		}else{		
			putMainData();
		}
	}
	
	private void putListData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		int count = 0;
		String code = null;
		query.append("Select ");
		query.append("a.").append(PageContentDef.cid);
		query.append(",a.").append(PageContentDef.title);
		query.append(",a.").append(PageContentDef.code);
//		query.append(",a.").append(PageContentDef.content);
		query.append(" From ").append(PageContentDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(PageContentDef.companyid).append("=?");
		query.append(" And a.").append(PageContentDef.code).append(" Like '").append(_moduleEle.attr("key")).append("%'");
		query.append(" Order By a.").append(PageContentDef.code);
		
		paramMap.put(PageContentDef.companyid, _shopInfoBean.getShopName());
		
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);			
				code = (String) loopContent.get(PageContentDef.code.name);
				loopContent.put(PageContentDef.content.name, FileHandler.readCustomPageFile (_shopInfoBean, _clientBean, code).toString());
				_mc.setLoopElementContent(loopContent);
			}	
		}catch(Exception e){
			logger.error(e, e);
		}		
	}
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		String cid = _req.getParameter(PageContentDef.cid.name);
		String code = null;
		query.append("Select ");
		query.append("a.").append(PageContentDef.title);
//		query.append(",a.").append(PageContentDef.content);				
		query.append(" From ").append(PageContentDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(PageContentDef.companyid).append("=?");
		if(CommonUtil.isEmpty(cid)){
			query.append(" And a.").append(PageContentDef.code).append("=?");
		}else{
			query.append(" And a.").append(PageContentDef.cid).append("=?");
		}
		paramMap.put(PageContentDef.companyid, _shopInfoBean.getShopName());
		if(CommonUtil.isEmpty(cid)){
			code = _moduleEle.attr("key");
			code = (code.equals("page.code"))?_clientBean.getRequestFilename():code;
			paramMap.put(PageContentDef.code, code);
		}else{
			paramMap.put(PageContentDef.cid, cid);
		}
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			if(dataArray.size()>0){
				loopContent = (HashMap) dataArray.get(0);
				loopContent.put(PageContentDef.content.name, FileHandler.readCustomPageFile (_shopInfoBean, _clientBean, code).toString());
				_mc.setElementContent(loopContent);
			}else{
				_mc.setElementContent(null);
			}
		}catch(Exception e){
			logger.error(e, e);
		}		
	}	
	
	private void putHTMLData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		String cid = _req.getParameter(PageContentDef.cid.name);
		String code = null;
		query.append("Select ");
		query.append("a.").append(PageContentDef.title).append(" As h").append(PageContentDef.title);
//		query.append(",a.").append(PageContentDef.content).append(" As h").append(PageContentDef.content);				
		query.append(" From ").append(PageContentDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(PageContentDef.companyid).append("=?");
		if(CommonUtil.isEmpty(cid)){
			query.append(" And a.").append(PageContentDef.code).append("=?");
		}else{
			query.append(" And a.").append(PageContentDef.cid).append("=?");
		}
		paramMap.put(PageContentDef.companyid, _shopInfoBean.getShopName());
		if(CommonUtil.isEmpty(cid)){
			code = _moduleEle.attr("key");
			code = (code.equals("page.code"))?_clientBean.getRequestFilename():code;
			paramMap.put(PageContentDef.code, code);
		}else{
			paramMap.put(PageContentDef.cid, cid);
		}
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			if(dataArray.size()>0){
				loopContent = (HashMap) dataArray.get(0);	
				loopContent.put("h"+PageContentDef.content.name, FileHandler.readCustomPageFile (_shopInfoBean, _clientBean, code).toString());
				_mc.setElementContent(loopContent);
			}else{
				_mc.setElementContent(null);
			}
		}catch(Exception e){
			logger.error(e, e);
		}		
	}	
}
