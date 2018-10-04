package com.forest.mfg.adminbuilder;

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
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDetailDef;
import com.forest.share.adminbuilder.BuildAdminHTML;
import com.forest.share.adminbuilder.UploadAdminBuilder;

public class MFG_ProductOptionAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_ProductOptionAdminBuilder.class);
	public MFG_ProductOptionAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
//		MFG_SupplierProductOptionDef
		
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
			MFG_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
			new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);
			for(int i=0; i<subClass.length; i++){		
				if(subClass[i]==MFG_ProductOptionDetailDef.class){
					UploadAdminBuilder.moveTempPhoto(_attrReqDataDetailMap[i], _shopInfoBean, "option/"+(String)_attrReqDataMap[0].get(MFG_ProductOptionDetailDef.prodoptid.name));
				}
			}
		}else if(GeneralConst.UPDATE.equals (_reqAction)){
			json = requestUpdate();
			MFG_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
			new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);
			
			for(int i=0; i<subClass.length; i++){		
				if(subClass[i]==MFG_ProductOptionDetailDef.class){
					UploadAdminBuilder.moveTempPhoto(_attrReqDataDetailMap[i], _shopInfoBean, "option/"+(String)_attrReqDataMap[0].get(MFG_ProductOptionDetailDef.prodoptid.name));
				}
			}
		}else if(GeneralConst.DELETE.equals (_reqAction)){
			json = requestDelete();
			MFG_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
			new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);
			
			int count = _deleteArrCode.length;
			for(int i=0; i<count; i++){
				UploadAdminBuilder.removeFolder(_shopInfoBean, "option/"+_deleteArrCode[i]);
			}		
		}else{
			json = super.requestJsonHandler();
		}				
		
		// Supplier Product
		_defClass = MFG_SupplierProductOptionDef.class;
		subClass = BeanDefUtil.getSubClass(_defClass);
		
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){
			addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);
		}
		
		if(GeneralConst.CREATE.equals (_reqAction)){
			if(_shopInfoBean.getShopName().equals("eurotrend")){
				requestCreateCustom();
			}
		}else if(GeneralConst.UPDATE.equals (_reqAction)){
			if(_shopInfoBean.getShopName().equals("eurotrend")){
				requestUpdateCustom();
			}
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
		if(subClass!=null){			
			_attrReqDataDetailMap = new HashMap[subClass.length][];
			for(int i=0; i<subClass.length; i++){						
				// Create Detail
				_attrReqDataDetailMap[i] = BuilderUtil.requestValuesToDataObject(subClass[i], _req, 
						BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
						(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), String.valueOf(key), _shopInfoBean.getShopName());
				
				if(subClass[i]==MFG_ProductOptionDetailDef.class){
					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){					
						if(_attrReqDataDetailMap[i][x]!=null){
							filename = (String)_attrReqDataDetailMap[i][x].get(MFG_ProductOptionDetailDef.filename.name);
							filename = filename.substring(filename.lastIndexOf("/")+1);
							_attrReqDataDetailMap[i][x].put(MFG_ProductOptionDetailDef.filename.name, filename);				
						}
					}
				}
				if(_attrReqDataDetailMap[i]!=null){							
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
				}
			}
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
		if(subClass!=null){								
			_attrReqDataDetailMap = new HashMap[subClass.length][];
			for(int i=0; i<subClass.length; i++){						
				// Create Detail
				_attrReqDataDetailMap[i] = BuilderUtil.requestValuesToDataObject(subClass[i], _req, 
						BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.PARENT_KEY), null, _shopInfoBean.getShopName());	
				
				
				if(subClass[i]==MFG_ProductOptionDetailDef.class){
					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){					
						if(_attrReqDataDetailMap[i][x]!=null){
							filename = (String)_attrReqDataDetailMap[i][x].get(MFG_ProductOptionDetailDef.filename.name);
							filename = filename.substring(filename.lastIndexOf("/")+1);
							_attrReqDataDetailMap[i][x].put(MFG_ProductOptionDetailDef.filename.name, filename);				
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
				if(subClass[i]==MFG_ProductOptionDetailDef.class){					
					for(int x=0; x<dataList.size (); x++){				
						rowData = (HashMap) dataList.get (x);	
						rowData.put(MFG_ProductOptionDetailDef.filename.name, "/photo/option/"+(String) rowData.get(MFG_ProductOptionDetailDef.prodoptid.name)+"/"+(String) rowData.get(MFG_ProductOptionDetailDef.filename.name));
					}
				}
				json.put ("SUBR"+i, buildEditDetail(dataList, 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.KEY), 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.TABLE_PREFIX), null, isCopy));
			}
		}
		return json;
	}	
	
	protected JSONObject requestCreateCustom()throws Exception{
		JSONObject json = new JSONObject();		
		int key = 0;
		
//		_attrReqDataMap[0].put(MFG_SupplierProductOptionDef.companyid.name, "megatrend");
		_attrReqDataMap[0].put(MFG_SupplierProductOptionDef.suppcompanyid.name, _shopInfoBean.getShopName());
		
		key = _gs.create(_defClass, addArray, _attrReqDataMap[0]);
		logger.debug("key = "+key);
		if(key>0){
			_attrReqDataMap[0].put((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), String.valueOf(key));	
		}else{
			key = Integer.parseInt((String)_attrReqDataMap[0].get((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY)));
		}
						
		json.put ("rc", RETURN_CREATE);	
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RETURN_CREATE, _clientBean.getLocale ()));
		json.put ("record", buildAddEditRecord (_defClass,
				(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
				listingArray, _attrReqDataMap[0]));
		
		HashMap rowMap = null;
		if(subClass!=null){			
			for(int i=0; i<subClass.length; i++){						
				// Create Detail					
				for(int x=0; x<_attrReqDataDetailMap[i].length; x++){
					rowMap = _attrReqDataDetailMap[i][x];								
					if(rowMap!=null){
//						rowMap.put(MFG_SupplierProductOptionDetailDef.companyid.name, "megatrend");						
						rowMap.put(MFG_SupplierProductOptionDetailDef.cost.name, rowMap.get(MFG_SupplierProductOptionDetailDef.dealerprice.name));
						rowMap.put(MFG_SupplierProductOptionDetailDef.dealerprice.name, "0");
						rowMap.put(MFG_SupplierProductOptionDetailDef.clientprice.name, "0");
						rowMap.put(MFG_SupplierProductOptionDetailDef.publicprice.name, "0");
						rowMap.put("row-status", "N");
					}
				}
				if(_attrReqDataDetailMap[i]!=null){							
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
				}
			}
		}
		
		MFG_SelectBuilder.removeCache("megatrend",_defClass);
		new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass, "megatrend");
		return json;
	}
	
	protected JSONObject requestUpdateCustom()throws Exception{
		JSONObject json = new JSONObject();
		HashMap rowMap = null;
		
//		_attrReqDataMap[0].put(MFG_SupplierProductOptionDef.companyid.name, "megatrend");
		_attrReqDataMap[0].put(MFG_SupplierProductOptionDef.suppcompanyid.name, _shopInfoBean.getShopName());
		
		_gs.update(_defClass, addArray,	_attrReqDataMap[0]);
		json.put ("rc", RETURN_UPDATE);	
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RETURN_UPDATE, _clientBean.getLocale ()));
		json.put ("record", buildAddEditRecord (_defClass,(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
				listingArray, _attrReqDataMap[0]));
		
		
		if(subClass!=null){										
			for(int i=0; i<subClass.length; i++){						
				// Update Detail					
				for(int x=0; x<_attrReqDataDetailMap[i].length; x++){
					rowMap = _attrReqDataDetailMap[i][x];			
					if(rowMap!=null){
//						rowMap.put(MFG_SupplierProductOptionDetailDef.companyid.name, "megatrend");						
						rowMap.put(MFG_SupplierProductOptionDetailDef.cost.name, rowMap.get(MFG_SupplierProductOptionDetailDef.dealerprice.name));
						rowMap.remove(MFG_SupplierProductOptionDetailDef.dealerprice.name);
						rowMap.remove(MFG_SupplierProductOptionDetailDef.clientprice.name);
						rowMap.remove(MFG_SupplierProductOptionDetailDef.publicprice.name);
					}
				}
				if(_attrReqDataDetailMap[i]!=null)						
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
			}
		}			
		MFG_SelectBuilder.removeCache("megatrend",_defClass);
		new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass, "megatrend");
		return json;
	}
}
