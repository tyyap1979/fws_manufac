package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.beandef.MFG_CustProductCustomerPriceDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_CustProductImageDef;
import com.forest.mfg.beandef.MFG_CustProductPriceDef;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_RawDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.mfg.beandef.MFG_SupplierProductPriceDef;
import com.forest.mfg.beandef.MFG_SupplierProductSelectionDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;
import com.forest.share.adminbuilder.BuildWebHTML;
import com.forest.share.adminbuilder.BuildAdminHTML;
import com.forest.share.adminbuilder.UploadAdminBuilder;
import com.forest.share.beandef.GalleryDef;
import com.forest.share.beandef.GalleryDetailDef;

public class MFG_CustProductAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_CustProductAdminBuilder.class);
	public MFG_CustProductAdminBuilder(ShopInfoBean shopInfoBean,
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
			logger.debug("validationBuffer = "+validationBuffer);
			if(validationBuffer.length ()>0){
				json.put ("rc", RETURN_VALIDATION);						
				json.put ("rd", validationBuffer);									
				return json;
			}
		}
		if(GeneralConst.CREATE.equals (_reqAction)){							
			json = requestCreate();
			for(int i=0; i<subClass.length; i++){		
				if(subClass[i]==MFG_CustProductImageDef.class){
					UploadAdminBuilder.moveTempPhoto(_attrReqDataDetailMap[i], _shopInfoBean, "product/"+(String)_attrReqDataMap[0].get(MFG_CustProductDef.prodid.name));
				}
			}
		}else if(GeneralConst.UPDATE.equals (_reqAction)){
			json = requestUpdate();
			for(int i=0; i<subClass.length; i++){		
				if(subClass[i]==MFG_CustProductImageDef.class){
					UploadAdminBuilder.moveTempPhoto(_attrReqDataDetailMap[i], _shopInfoBean, "product/"+(String)_attrReqDataMap[0].get(MFG_CustProductDef.prodid.name));
				}
			}
		}else if(GeneralConst.DELETE.equals (_reqAction)){
			json = requestDelete();
			int count = _deleteArrCode.length;
			for(int i=0; i<count; i++){
				UploadAdminBuilder.removeFolder(_shopInfoBean, "product/"+_deleteArrCode[i]);
			}		
		}else if("priceenq".equals(_reqAction)){
			json = getProductPrice();
		}else{
			json = super.requestJsonHandler();
		}
		
		
		
//		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction) || GeneralConst.DELETE.equals (_reqAction)){
//			BuildWebHTML build = new BuildWebHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp);
//			build.buildHTML(_attrReqDataMap[0]);
//		}
		
		
		// For Supplier Product
//		_defClass = MFG_SupplierProductDef.class;
//		subClass = BeanDefUtil.getSubClass(_defClass);
//		
//		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){
//			addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
//			listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);
//		}
//		if(GeneralConst.CREATE.equals (_reqAction)){
//			if(_shopInfoBean.getShopName().equals("eurotrend")){				
//				requestCreateCustom();
//			}
//		}else if(GeneralConst.UPDATE.equals (_reqAction)){
//			if(_shopInfoBean.getShopName().equals("eurotrend")){
//				requestUpdateCustom();					
//			}
//		}else if(GeneralConst.DELETE.equals (_reqAction)){				
//			// Delete Supplier Product
//			if(_shopInfoBean.getShopName().equals("eurotrend")){
//				_gs.delete (MFG_SupplierProductDef.class, BeanDefUtil.getKeyObject(MFG_SupplierProductDef.class), _deleteArrCode);
//				subClass = BeanDefUtil.getSubClass(MFG_SupplierProductDef.class);
//				if(subClass!=null){
//					for(int i=0; i<subClass.length; i++){	
//						_gs.delete (subClass[i], BeanDefUtil.getParentKeyObject(subClass[i]), _deleteArrCode);
//					}
//				}
//				
//				updateTransactionDetailOnDeleteProduct(_deleteArrCode);
//			}
//		}		
		
		
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
				
				if(subClass[i]==MFG_CustProductImageDef.class){
					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){					
						if(_attrReqDataDetailMap[i][x]!=null){
							filename = (String)_attrReqDataDetailMap[i][x].get(MFG_CustProductImageDef.filename.name);
							filename = filename.substring(filename.lastIndexOf("/")+1);
							_attrReqDataDetailMap[i][x].put(MFG_CustProductImageDef.filename.name, filename);				
						}
					}
				}
				if(_attrReqDataDetailMap[i]!=null){							
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
				}
			}
		}	
		MFG_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
		new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);
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
				
				
				if(subClass[i]==MFG_CustProductImageDef.class){
					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){					
						if(_attrReqDataDetailMap[i][x]!=null){
							filename = (String)_attrReqDataDetailMap[i][x].get(MFG_CustProductImageDef.filename.name);
							filename = filename.substring(filename.lastIndexOf("/")+1);
							_attrReqDataDetailMap[i][x].put(MFG_CustProductImageDef.filename.name, filename);				
						}
					}
				}
				
				if(_attrReqDataDetailMap[i]!=null)						
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));							
			}
		}	
		MFG_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
		new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);		
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
				if(subClass[i]==MFG_CustProductImageDef.class){					
					for(int x=0; x<dataList.size (); x++){				
						rowData = (HashMap) dataList.get (x);	
						rowData.put(GalleryDetailDef.filename.name, "/photo/product/"+(String) rowData.get(MFG_CustProductDef.prodid.name)+"/"+(String) rowData.get(MFG_CustProductImageDef.filename.name));
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
		
//		_attrReqDataMap[0].put(MFG_SupplierProductDef.companyid.name, "megatrend");
		_attrReqDataMap[0].put(MFG_SupplierProductDef.suppcompanyid.name, _shopInfoBean.getShopName());
		
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
				if(subClass[i]==MFG_SupplierProductPriceDef.class){
					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){
						rowMap = _attrReqDataDetailMap[i][x];								
						if(rowMap!=null){						
							rowMap.put(MFG_SupplierProductPriceDef.cost.name, rowMap.get(MFG_SupplierProductPriceDef.dealerprice.name));
							rowMap.put(MFG_SupplierProductPriceDef.dealerprice.name, "0");
							rowMap.put(MFG_SupplierProductPriceDef.clientprice.name, "0");
							rowMap.put(MFG_SupplierProductPriceDef.publicprice.name, "0");
//							rowMap.put(MFG_SupplierProductPriceDef.dealer1price.name, "0");
//							rowMap.put(MFG_SupplierProductPriceDef.client1price.name, "0");
//							rowMap.put(MFG_SupplierProductPriceDef.public1price.name, "0");
//							rowMap.put(MFG_SupplierProductPriceDef.dealer2price.name, "0");
//							rowMap.put(MFG_SupplierProductPriceDef.client2price.name, "0");
//							rowMap.put(MFG_SupplierProductPriceDef.public2price.name, "0");
						}
					}
				}else if(subClass[i]==MFG_SupplierProductSelectionDef.class){
//					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){
//						rowMap = _attrReqDataDetailMap[i][x];								
//						if(rowMap!=null){						
//							rowMap.put(MFG_SupplierProductSelectionDef.companyid.name, "megatrend");
//						}
//					}
				}
//				for(int x=0; x<_attrReqDataDetailMap[i].length; x++){
//					rowMap = _attrReqDataDetailMap[i][x];								
//					if(rowMap!=null){						
//						rowMap.put("row-status", "N");										
//					}
//				}
				if(_attrReqDataDetailMap[i]!=null){							
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
				}
			}
		}
		if(_defClass==MFG_CustProductDef.class || _defClass==MFG_ProductOptionDef.class 
				|| _defClass==MFG_RawDef.class){
			MFG_SelectBuilder.removeCache();
		}
		return json;
	}
	
	protected JSONObject requestUpdateCustom()throws Exception{
		JSONObject json = new JSONObject();
		HashMap rowMap = null;
		ArrayList detailAddArray = null;
		
//		_attrReqDataMap[0].put(MFG_SupplierProductDef.companyid.name, "megatrend");
		_attrReqDataMap[0].put(MFG_SupplierProductDef.suppcompanyid.name, _shopInfoBean.getShopName());
		
		_gs.update(_defClass, addArray,	_attrReqDataMap[0]);
		json.put ("rc", RETURN_UPDATE);	
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RETURN_UPDATE, _clientBean.getLocale ()));
		json.put ("record", buildAddEditRecord (_defClass,(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
				listingArray, _attrReqDataMap[0]));
		
		
		if(subClass!=null){										
			for(int i=0; i<subClass.length; i++){						
				// Update Detail					
				detailAddArray = BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
				if(subClass[i]==MFG_SupplierProductPriceDef.class){
					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){
						rowMap = _attrReqDataDetailMap[i][x];								
						if(rowMap!=null){						
							rowMap.put(MFG_SupplierProductPriceDef.cost.name, rowMap.get(MFG_SupplierProductPriceDef.dealerprice.name));
							rowMap.remove(MFG_SupplierProductPriceDef.dealerprice.name);
							rowMap.remove(MFG_SupplierProductPriceDef.clientprice.name);	
							rowMap.remove(MFG_SupplierProductPriceDef.publicprice.name);	
//							rowMap.remove(MFG_SupplierProductPriceDef.dealer1price.name);
//							rowMap.remove(MFG_SupplierProductPriceDef.client1price.name);	
//							rowMap.remove(MFG_SupplierProductPriceDef.public1price.name);	
//							rowMap.remove(MFG_SupplierProductPriceDef.dealer2price.name);
//							rowMap.remove(MFG_SupplierProductPriceDef.client2price.name);	
//							rowMap.remove(MFG_SupplierProductPriceDef.public2price.name);	
						}
					}
				}else if(subClass[i]==MFG_SupplierProductSelectionDef.class){
//					for(int x=0; x<_attrReqDataDetailMap[i].length; x++){
//						rowMap = _attrReqDataDetailMap[i][x];								
//						if(rowMap!=null){						
//							rowMap.put(MFG_SupplierProductSelectionDef.companyid.name, "megatrend");
//						}
//					}
				}
				
				if(_attrReqDataDetailMap[i]!=null)						
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), detailAddArray, true);
			}
		}			
		if(_defClass==MFG_CustProductDef.class || _defClass==MFG_ProductOptionDef.class 
				|| _defClass==MFG_RawDef.class){
			MFG_SelectBuilder.removeCache();
		}
		return json;
	}
	
	private void updateTransactionDetailOnDeleteProduct(String[] prodid)throws Exception{
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		try {
			query.append("Update mfg_transaction_detail a, mfg_custproduct b set a.prodname = b.name");
			query.append(" Where b.prodid = a.prodid");
			query.append(" And b.status = '").append(GeneralConst.DELETED).append("'");
			query.append(" And b.prodid In (").append(DBUtil.arrayToString(prodid, MFG_CustProductDef.prodid)).append(")");
			
			pstmt = _dbConnection.prepareStatement(query.toString());			
			pstmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		}finally{
			DBUtil.free(null, pstmt, null);
		}
	}
	
	public JSONObject getProductPrice()throws Exception{	
		JSONObject json = new JSONObject();
		String prodId[] 			= _req.getParameter(MFG_TransactionDetailDef.prodid.name).split(",");
		String isFactoryProduct[] 	= _req.getParameter("factory").split(",");
		String customerId 			= _req.getParameter(MFG_TransactionDef.customerid.name);
		String customerType 		= _req.getParameter(MFG_TransactionDef.custtype.name);
				
		json.put("rc", "0000");
		json.put("data", getProductPrice(prodId, isFactoryProduct, customerId, customerType));
		
		return json;
	}
	public HashMap getProductPrice(String prodId[], String isFactoryProduct[], String customerId, String customerType)throws Exception{		
		StringBuffer queryNormal = new StringBuffer();		
		ArrayList dataArray = null;
		HashMap prodHash = new HashMap();
		

		try {
			int len = prodId.length;
			for(int i=0; i<len; i++){
				queryNormal = new StringBuffer();
				queryNormal.append("Select ");
				queryNormal.append("a.").append(MFG_CustProductPriceDef.prodid).append(",");
				queryNormal.append("COALESCE(");
				
				if(!CommonUtil.isEmpty(customerId)){
					queryNormal.append("b.").append(MFG_CustProductCustomerPriceDef.price).append(",");
				}else{
					queryNormal.append("null,");
				}
				
				if("D".equals(customerType) || CommonUtil.isEmpty(customerType)){
					queryNormal.append("a.").append(MFG_CustProductPriceDef.dealerprice);
				}else if("C".equals(customerType)){
					queryNormal.append("a.").append(MFG_CustProductPriceDef.clientprice);
				}else if("P".equals(customerType)){
					queryNormal.append("a.").append(MFG_CustProductPriceDef.publicprice);
				}
				queryNormal.append(") ");
				queryNormal.append(" As ").append(MFG_CustProductCustomerPriceDef.price).append(", ");
				
				// Original Price
				if("D".equals(customerType) || CommonUtil.isEmpty(customerType)){
					queryNormal.append("a.").append(MFG_CustProductPriceDef.dealerprice);
				}else if("C".equals(customerType)){
					queryNormal.append("a.").append(MFG_CustProductPriceDef.clientprice);
				}else if("P".equals(customerType)){
					queryNormal.append("a.").append(MFG_CustProductPriceDef.publicprice);
				}
				queryNormal.append(" As original,");
				
				queryNormal.append("a.").append(MFG_CustProductPriceDef.orderfrom).append(",");
				queryNormal.append("a.").append(MFG_CustProductPriceDef.orderto);
				
				if("Y".equals(isFactoryProduct[i])){
					queryNormal.append(" From ").append(MFG_SupplierProductPriceDef.TABLE).append(" a");
				}else{
					queryNormal.append(" From ").append(MFG_CustProductPriceDef.TABLE).append(" a");
				}
				if(!CommonUtil.isEmpty(customerId)){
					queryNormal.append(" Left Join ").append(MFG_CustProductCustomerPriceDef.TABLE).append(" b On b.");
					queryNormal.append(MFG_CustProductCustomerPriceDef.prodid).append("=a.").append(MFG_CustProductPriceDef.prodid);
					queryNormal.append(" And b.").append(MFG_CustProductCustomerPriceDef.priceid).append("=a.").append(MFG_CustProductPriceDef.priceid);
					queryNormal.append(" And b.").append(MFG_CustProductCustomerPriceDef.customerid).append("=").append(customerId);				
					queryNormal.append(" And b.").append(MFG_CustProductCustomerPriceDef.price).append(">0");
				}
				queryNormal.append(" Where a.").append(MFG_CustProductPriceDef.prodid).append("=").append(prodId[i]);

				dataArray = _gs.searchDataArray(queryNormal);
				prodHash.put("prod"+prodId[i], dataArray);
			}

			
			return prodHash;
		} catch (Exception e) {
			throw e;
		}finally{
			
		}
	}
}
