package com.forest.retail.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.retail.beandef.RETAIL_ProductAttributeDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.retail.beandef.RETAIL_ProductImageDef;
import com.forest.retail.beandef.RETAIL_ProductOptionDef;
import com.forest.retail.services.RetailServices;
import com.forest.share.adminbuilder.BuildWebHTML;
import com.forest.share.adminbuilder.UploadAdminBuilder;
import com.forest.share.beandef.PageContentDef;

public class RETAIL_ProductAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (RETAIL_ProductAdminBuilder.class);
	public RETAIL_ProductAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		subClass = BeanDefUtil.getSubClass(_defClass);
		
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){
			addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);
			
			_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, _accessByCompany);							
			StringBuffer validationBuffer = validateBean (addArray, _attrReqDataMap, (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME), _clientBean.getLocale (), _reqAction);
//			logger.debug("validationBuffer = "+validationBuffer);
			if(validationBuffer.length ()>0){
				json.put ("rc", RETURN_VALIDATION);						
				json.put ("rd", validationBuffer);									
				return json;
			}
		}
		if(GeneralConst.CREATE.equals (_reqAction)){							
			json = requestCreate();
			for(int i=0; i<subClass.length; i++){		
				if(subClass[i]==RETAIL_ProductImageDef.class){
					UploadAdminBuilder.moveTempPhoto(_attrReqDataDetailMap[i], _shopInfoBean, "product/"+(String)_attrReqDataMap[0].get(RETAIL_ProductDef.prodid.name));
				}
			}
		}else if(GeneralConst.UPDATE.equals (_reqAction)){
			json = requestUpdate();
			for(int i=0; i<subClass.length; i++){		
				if(subClass[i]==RETAIL_ProductImageDef.class){
					UploadAdminBuilder.moveTempPhoto(_attrReqDataDetailMap[i], _shopInfoBean, "product/"+(String)_attrReqDataMap[0].get(RETAIL_ProductDef.prodid.name));
				}
			}
		}else if(GeneralConst.DELETE.equals (_reqAction)){
			json = requestDelete();
			int count = _deleteArrCode.length;
			for(int i=0; i<count; i++){
				UploadAdminBuilder.removeFolder(_shopInfoBean, "product/"+_deleteArrCode[i]);
				FileHandler.removeCustomPageFile(_shopInfoBean, _clientBean, "PD_"+_deleteArrCode[i]);
			}					
		}else if("productoption".equals(_reqAction)){			
			json = new RetailServices(_shopInfoBean, _clientBean, _dbConnection).getProductJson(_req.getParameter(RETAIL_ProductDef.prodid.name));
		}else{
			json = super.requestJsonHandler();
		}
		
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){			
			String filename = "PD_"+(String)_attrReqDataMap[0].get(RETAIL_ProductDef.prodid.name);
			String fileContent = (String)_req.getParameter(RETAIL_ProductDef.description.name);
			FileHandler.writeCustomPageFile(_shopInfoBean, _clientBean, filename, fileContent);	
		}
		
		
		
		return json;
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		
		// Read Additional Content From File
		buffer.append(FileHandler.readAdminFile (_shopInfoBean, _clientBean));	
		return buffer;
	}
	
	protected JSONObject requestCreate()throws Exception{
		JSONObject json = new JSONObject();
		int key = 0;
		
		_attrReqDataMap[0].put(RETAIL_ProductDef.description.name, "");
		
		key = _gs.create(_defClass, addArray, _attrReqDataMap[0]);
		
		if(key>0){
			_attrReqDataMap[0].put((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), String.valueOf(key));	
		}else{
			key = Integer.parseInt((String)_attrReqDataMap[0].get((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY)));
		}
						
		json.put ("rc", RETURN_CREATE);	
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RETURN_CREATE, _clientBean.getLocale ()));
		json.put ("record", buildAddRecordReturn (_defClass,
				(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
				listingArray, _attrReqDataMap[0]));
		
		String filename = null;		
		_attrReqDataDetailMap = new HashMap[1][];
							
		// Create Detail
		if(subClass!=null){			
			_attrReqDataDetailMap = new HashMap[subClass.length][];
			for(int i=0; i<subClass.length; i++){						
				// Create Detail
				_attrReqDataDetailMap[i] = BuilderUtil.requestValuesToDataObject(subClass[i], _req, 
						BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
						(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), String.valueOf(key), _shopInfoBean.getShopName());
				
				if(subClass[i]==RETAIL_ProductImageDef.class){
					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){					
						if(_attrReqDataDetailMap[i][x]!=null){
							filename = (String)_attrReqDataDetailMap[i][x].get(RETAIL_ProductImageDef.filename.name);
							filename = filename.substring(filename.lastIndexOf("/")+1);
							_attrReqDataDetailMap[i][x].put(RETAIL_ProductImageDef.filename.name, filename);				
						}
					}
				}
				
				if(_attrReqDataDetailMap[i]!=null && _attrReqDataDetailMap[i].length>0){							
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
				}
			}
		}	

		return json;
	}
	
	protected JSONObject requestUpdate()throws Exception{
		JSONObject json = new JSONObject();
		_attrReqDataMap[0].put(RETAIL_ProductDef.description.name, "");
		_gs.update(_defClass, addArray,	_attrReqDataMap[0]);
		json.put ("rc", RETURN_UPDATE);	
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RETURN_UPDATE, _clientBean.getLocale ()));
		json.put ("record", buildAddEditRecord (_defClass,(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
				listingArray, _attrReqDataMap[0]));
		
		
		// Create Detail
		String filename = null;
		if(subClass!=null){								
			_attrReqDataDetailMap = new HashMap[subClass.length][];
			for(int i=0; i<subClass.length; i++){						
				// Create Detail
				_attrReqDataDetailMap[i] = BuilderUtil.requestValuesToDataObject(subClass[i], _req, 
						BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.PARENT_KEY), null, _shopInfoBean.getShopName());	
				
				
				if(subClass[i]==RETAIL_ProductImageDef.class){
					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){					
						if(_attrReqDataDetailMap[i][x]!=null){
							filename = (String)_attrReqDataDetailMap[i][x].get(RETAIL_ProductImageDef.filename.name);
							filename = filename.substring(filename.lastIndexOf("/")+1);
							_attrReqDataDetailMap[i][x].put(RETAIL_ProductImageDef.filename.name, filename);				
						}
					}
				}
				
				if(_attrReqDataDetailMap[i]!=null)						
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));							
			}
		}	

		return json;
	}
	
	protected JSONObject requestEdit(StringBuffer searchCriteria, StringBuffer detailSearchCriteria, boolean isCopy)throws Exception{		
		subClass = BeanDefUtil.getSubClass(_defClass);
		HashMap rowData = null;
		ArrayList dataList = null;
		JSONObject json = new JSONObject();
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
		_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, searchArray, _accessByCompany);
		
		arrayListSearch = _gs.search(_defClass, searchCriteria, 
				searchArray, 
				addArray, _attrReqDataMap, null, "1", _reqAction);				

		HashMap dataMap = null;
		dataList = (ArrayList) arrayListSearch.get(1);
		int count = dataList.size();
		String code = null;
		String fileContent = null;
		for(int i=0; i<count; i++){
			dataMap = (HashMap) dataList.get(i);				
			code = "PD_"+(String) dataMap.get(RETAIL_ProductDef.prodid.name);
			fileContent = FileHandler.readCustomPageFile (_shopInfoBean, _clientBean, code).toString();			
			dataMap.put(RETAIL_ProductDef.description.name, fileContent);
		}
		
		json.put ("md", (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME));
		json.put ("rc", RETURN_EDIT);
		json.put ("isCopy", isCopy);
		json.put ("R", buildEdit((ArrayList) arrayListSearch.get(1), _defClass, null, isCopy));
		if(subClass!=null){				
			json.put("subclasslength", subClass.length);			
			for(int i=0; i<subClass.length; i++){				
				addArray = BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
				searchArray = BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);				
				arrayListSearch = _gs.search(subClass[i], detailSearchCriteria, 
						searchArray, addArray, _attrReqDataMap, null, "*", _reqAction);
				
				dataList = (ArrayList) arrayListSearch.get(1);
				if(subClass[i]==RETAIL_ProductImageDef.class){					
					for(int x=0; x<dataList.size (); x++){				
						rowData = (HashMap) dataList.get (x);	
						rowData.put(RETAIL_ProductImageDef.filename.name, "/photo/product/"+(String) rowData.get(RETAIL_ProductDef.prodid.name)+"/"+(String) rowData.get(RETAIL_ProductImageDef.filename.name));
					}
				}
				json.put ("SUBR"+i, buildEditDetail(dataList, 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.KEY), 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.TABLE_PREFIX), null, isCopy));
			}
		}
		return json;
	}	
	
	
	
}
