package com.forest.common.builder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.ThemesServices;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.services.AdminSQLServices;
import com.forest.share.adminbuilder.BuildWebHTML;
import com.forest.share.adminbuilder.BuildAdminHTML;

public class ShopAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (ShopAdminBuilder.class);
	
	public ShopAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);	
	} 
	
	public JSONObject requestJsonHandler()throws Exception{
		JSONObject json = super.requestJsonHandler();
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){			
			ThemesServices.getInstance().updateShop(_attrReqDataMap[0]);
			
			if("shop.htm".equals(_clientBean.getRequestFilename())){
				BuildAdminHTML build = new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp);
				build.buildHTML(_attrReqDataMap[0]);			
			}else{
				if(ThemesServices.getInstance().getShopWebSite((String)_attrReqDataMap[0].get(ShopBeanDef.shopdomain.name))!=null){
					BuildWebHTML build = new BuildWebHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp);
					build.buildHTML(_attrReqDataMap[0]);
				}
			}
		}
		
		if("build.html".equals(_reqAction)){
			String[] arrCode =  _req.getParameterValues ((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY));
			buildSelectedCompany(arrCode);
			json.put("rc", "0");
		}
		return json;
	}

	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = null;
		
		if("shop.htm".equals(_clientBean.getRequestFilename())){
			buffer = super.displayHandler();
			// Read Additional Content From File
			buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/" + _clientBean.getRequestFilename()));	
		}else{
			ArrayList addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			int addSize = addArray.size();	 
			HashMap listMap = null;
			DataObject dataObject = null;
	    	for(int i=0; i<addSize; i++){	    			    		
	    		listMap = (HashMap) addArray.get(i);
	    		dataObject 	= (DataObject) listMap.get("object");	
	    		if(dataObject.name.equals(ShopBeanDef.companyid.name)
	    				|| dataObject.name.equals(ShopBeanDef.shopdomain.name)
	    				|| dataObject.name.equals(ShopBeanDef.admincontext.name)
	    				|| dataObject.name.equals(ShopBeanDef.business.name)
	    				|| dataObject.name.equals(ShopBeanDef.companygroup.name)
	    				|| dataObject.name.equals(ShopBeanDef.default_template.name)
	    				|| dataObject.name.equals(ShopBeanDef.status.name)
	    				){
	    			listMap.put("type", String.valueOf(ComponentConst.HIDDEN));
	    		}
	    	}
			buffer = super.displayHandlerEntry(addArray);
			// Read Additional Content From File
			buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/" + _clientBean.getRequestFilename()));	
		}
		
		return buffer;
	}
	
	public void displayHandler(StringBuffer buffer)throws Exception{
		int fromIndex=0;
		if((fromIndex = buffer.indexOf ("system.admin.company.bar", 0)) != -1){		
			if(!CommonUtil.isEmpty(_shopInfoBean.getCompanyGroup())){
				buffer.replace(fromIndex, "system.admin.company.bar".length () + fromIndex, buildChildCompany().toString ());
			}else{
				buffer.replace(fromIndex, "system.admin.company.bar".length () + fromIndex, "");
			}
		}
	}
		
	private StringBuffer buildChildCompany(){
		StringBuffer buffer = new StringBuffer();
		ArrayList companyGroup = null;
		String id = null;		
		String name = null;
		String adminContext = null;
		HashMap rowData = null;		
		try{
			companyGroup = new AdminSQLServices(_shopInfoBean, _clientBean, _dbConnection).getCompanyGroup(_shopInfoBean.getCompanyGroup());
						
			if(companyGroup!=null){				
				buffer.append("<ul class='companybar'>");				
				for(int i=0; i<companyGroup.size(); i++){
					rowData = (HashMap) companyGroup.get(i);
					logger.debug("buildChildCompany rowData = "+rowData);
					id = (String) rowData.get(ShopBeanDef.companyid.name);
					name = (String) rowData.get(ShopBeanDef.shopshortname.name);
					adminContext = (String) rowData.get(ShopBeanDef.admincontext.name);
					if(CommonUtil.isEmpty(adminContext)){
						adminContext = id;
					}

					if(_shopInfoBean.getSelectedShop().equals(id)){
						buffer.append("<li>").append(name).append("</li>");
					}else{
						buffer.append("<li><a id='companygrplink' href='");
						if("forestwebsolution.com".equals((String) rowData.get(ShopBeanDef.shopdomain.name))){
							buffer.append("http://").append(adminContext).append(".").append((String) rowData.get(ShopBeanDef.shopdomain.name));
						}else{
							buffer.append("/").append(adminContext);
						}
						buffer.append("/'>").append(name).append("</a></li>");
					}
				}
				
				buffer.append("</ul>");
			}
		}catch (Exception e) {
			logger.error(e, e);
		}
		return buffer;
	}
	
	private void buildSelectedCompany(String[] arrCode){
		StringBuffer buffer = new StringBuffer();
		ArrayList dataList = null;
		HashMap dataMap = null;
		try{
			BuildAdminHTML build = new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp);
						
			
			buffer.append("Select * From ");
			buffer.append(ShopBeanDef.TABLE);
			buffer.append(" Where ");
			buffer.append(ShopBeanDef.shopid).append(" In (").append(DBUtil.arrayToString(arrCode, ShopBeanDef.shopid)).append(")");
			
			dataList = _gs.searchDataArray(buffer);
			for(int i=0; i<dataList.size(); i++){
				dataMap = (HashMap) dataList.get(i);
				logger.debug("dataMap = "+dataMap);
				build.buildHTML(dataMap);
			}
		}catch(Exception e){
			
		}
	}
	
	
}