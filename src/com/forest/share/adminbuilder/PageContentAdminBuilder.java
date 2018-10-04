package com.forest.share.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.DBUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.FileUtil;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;
import com.forest.share.beandef.PageContentDef;

public class PageContentAdminBuilder extends GenericAdminBuilder {
	public PageContentAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{
		JSONObject json = null;
		String fileContent = null;
		String filename = null;		
	
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){			
			json = super.requestJsonHandler();
			
			filename = (String)_req.getParameter(PageContentDef.code.name);
			fileContent = (String)_req.getParameter(PageContentDef.content.name);
			FileHandler.writeCustomPageFile(_shopInfoBean, _clientBean, filename, fileContent);
			
			BuildWebHTML build = new BuildWebHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp);
			build.buildHTML(_attrReqDataMap[0]);		
		}else{
			json = super.requestJsonHandler();
		}

		return json;
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		
		// Read Additional Content From File
		buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/"+_clientBean.getRequestFilename()));		
		return buffer;
	}
	
	public JSONObject requestCreate()throws Exception{
		JSONObject json = null;
		_attrReqDataMap[0].put(PageContentDef.content.name, "");
		json = super.requestCreate();
		
		return json;
	}
	
	public JSONObject requestUpdate()throws Exception{
		JSONObject json = null;
		_attrReqDataMap[0].put(PageContentDef.content.name, "");
		json = super.requestUpdate();
		
		return json;
	}
	
	public JSONArray buildEdit(ArrayList dataList, Class defClass, String prefix, boolean isCopy)throws OwnException {		
		HashMap dataMap = null;
		int count = dataList.size();
		String code = null;
		String fileContent = null;
		for(int i=0; i<count; i++){
			dataMap = (HashMap) dataList.get(i);				
			code = (String) dataMap.get(PageContentDef.code.name);
			fileContent = FileHandler.readCustomPageFile (_shopInfoBean, _clientBean, code).toString();
			dataMap.put(PageContentDef.content.name, fileContent);
		}
		return super.buildEdit(dataList, defClass, prefix, isCopy);
	}
	
	public JSONObject requestDelete()throws Exception{		
		JSONObject json = null;		
		StringBuffer query = new StringBuffer();
		ArrayList dataList = null;
		HashMap dataMap = null;
		int count = 0;
		
		_deleteArrCode = _req.getParameterValues ((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY));				
		query.append("Select ");
		query.append(PageContentDef.code);
		query.append(" From ").append(PageContentDef.TABLE);
		query.append(" Where ").append(PageContentDef.cid).append(" In (").append(DBUtil.arrayToString(_deleteArrCode, PageContentDef.cid)).append(")");
		dataList = _gs.searchDataArray(query);
		count = dataList.size();
		for(int i=0; i<count; i++){
			dataMap = (HashMap) dataList.get(i);			
			FileHandler.removeCustomPageFile(_shopInfoBean, _clientBean, (String)dataMap.get(PageContentDef.code.name));
		}		
		
		json = super.requestDelete();
		return json;
	}
}
