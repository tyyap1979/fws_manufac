package com.forest.share.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;
import com.forest.share.beandef.ShipmentDomesticDef;

public class ShipmentDomesticAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (ShipmentDomesticAdminBuilder.class);
	public ShipmentDomesticAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		subClass = BeanDefUtil.getSubClass(_defClass);

		try{
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
				
				_attrReqDataMap[0].put(ShipmentDomesticDef.state.name, _attrReqDataMap[0].get(ShipmentDomesticDef.state.name+"_value"));
			}
			// Functional 
			if(GeneralConst.CREATE.equals (_reqAction)){		
				json = requestCreate();
			}else if(GeneralConst.UPDATE.equals (_reqAction)){				
				json = requestUpdate();
			}else{
				json = super.requestJsonHandler();
			}
			
		}catch(OwnException e){			
			throw e;
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}		
		return json;
	}		
}
