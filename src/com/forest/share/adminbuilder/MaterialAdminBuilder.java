package com.forest.share.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.forest.share.beandef.MaterialDef;
import com.forest.share.beandef.MaterialDetailDef;

public class MaterialAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MaterialAdminBuilder.class);	
	
	public MaterialAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);			
	} 
	
	public JSONObject requestJsonHandler()throws Exception{
		JSONObject json = null;
		subClass = BeanDefUtil.getSubClass(_defClass);
		
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){
			addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);
			
			_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, _accessByCompany);							
			StringBuffer validationBuffer = validateBean (addArray, _attrReqDataMap, (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME), _clientBean.getLocale (), _reqAction);
			logger.debug("validationBuffer = "+validationBuffer);
			if(validationBuffer.length ()>0){
				json.put ("rc", RETURN_VALIDATION);						
				json.put ("rd", validationBuffer);									
				return json;
			}
		}
		if(GeneralConst.CREATE.equals (_reqAction)){							
			json = requestCreate();
			UploadAdminBuilder.moveTempPhoto(_attrReqDataDetailMap[0], _shopInfoBean, "material/"+(String)_attrReqDataMap[0].get(MaterialDef.mid.name));			
		}else if(GeneralConst.UPDATE.equals (_reqAction)){
			json = requestUpdate();
			UploadAdminBuilder.moveTempPhoto(_attrReqDataDetailMap[0], _shopInfoBean, "material/"+(String)_attrReqDataMap[0].get(MaterialDef.mid.name));
		}else if(GeneralConst.DELETE.equals (_reqAction)){
			json = requestDelete();
			int count = _deleteArrCode.length;
			for(int i=0; i<count; i++){
				UploadAdminBuilder.removeFolder(_shopInfoBean, "material/"+_deleteArrCode[i]);
			}		
		}else{
			json = super.requestJsonHandler();
		}
		
//		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction) || GeneralConst.DELETE.equals (_reqAction)){
//			BuildWebHTML build = new BuildWebHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp);
//			build.buildHTML(_attrReqDataMap[0]);
//		}
		return json;
	}

	public StringBuffer displayHandler()throws Exception{		
		StringBuffer buffer = null;
		buffer = super.displayHandler();
		// Read Additional Content From File
		buffer.append(FileHandler.readFile(null, ResourceUtil.getAdminHTMLPath ("common") + "html/" + _clientBean.getRequestFilename()));		
		return buffer;
	}		
	protected JSONObject requestCreate()throws Exception{
		JSONObject json = new JSONObject();
		int key = 0;
		
		key = _gs.create(_defClass, addArray, _attrReqDataMap[0]);
		logger.debug("key = "+key);
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
		_attrReqDataDetailMap[0] = BuilderUtil.requestValuesToDataObject(subClass[0], _req, 
				BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
				(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), String.valueOf(key), _shopInfoBean.getShopName());
		
		for(int i=0; i<_attrReqDataDetailMap[0].length; i++){					
			if(_attrReqDataDetailMap[0][i]!=null){
				filename = (String)_attrReqDataDetailMap[0][i].get(MaterialDetailDef.filename.name);
				filename = filename.substring(filename.lastIndexOf("/")+1);
				_attrReqDataDetailMap[0][i].put(MaterialDetailDef.filename.name, filename);				
			}
		}
		if(_attrReqDataDetailMap[0]!=null){							
			_gs.updateDetail(subClass[0], _attrReqDataDetailMap[0], BeanDefUtil.getKeyObject(subClass[0]), BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
		}					

		return json;
	}
	
	protected JSONObject requestUpdate()throws Exception{
		JSONObject json = new JSONObject();
		_gs.update(_defClass, addArray,	_attrReqDataMap[0]);
		json.put ("rc", RETURN_UPDATE);	
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RETURN_UPDATE, _clientBean.getLocale ()));
		json.put ("record", buildAddEditRecord (_defClass,(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
				listingArray, _attrReqDataMap[0]));
		
		
		// Create Detail
		String filename = null;
		_attrReqDataDetailMap = new HashMap[1][];
		_attrReqDataDetailMap[0] = BuilderUtil.requestValuesToDataObject(subClass[0], _req, 
				BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
				(String) BeanDefUtil.getField(subClass[0], BeanDefUtil.PARENT_KEY), null, _shopInfoBean.getShopName());	
		
		for(int i=0; i<_attrReqDataDetailMap[0].length; i++){					
			if(_attrReqDataDetailMap[0][i]!=null){
				filename = (String)_attrReqDataDetailMap[0][i].get(MaterialDetailDef.filename.name);
				filename = filename.substring(filename.lastIndexOf("/")+1);
				_attrReqDataDetailMap[0][i].put(MaterialDetailDef.filename.name, filename);				
			}
		}
		if(_attrReqDataDetailMap[0]!=null){							
			_gs.updateDetail(subClass[0], _attrReqDataDetailMap[0], BeanDefUtil.getKeyObject(subClass[0]), BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
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
				for(int x=0; x<dataList.size (); x++){				
					rowData = (HashMap) dataList.get (x);	
					rowData.put(MaterialDetailDef.filename.name, "/photo/material/"+(String) rowData.get(MaterialDetailDef.mid.name)+"/"+(String) rowData.get(MaterialDetailDef.filename.name));
				}
				json.put ("SUBR"+i, buildEditDetail(dataList, 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.KEY), 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.TABLE_PREFIX), null, isCopy));
			}
		}
		return json;
	}	
}