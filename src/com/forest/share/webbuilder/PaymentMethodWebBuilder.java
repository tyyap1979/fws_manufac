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
import com.forest.common.constants.GeneralConst;
import com.forest.share.beandef.PaymentMethodDef;

public class PaymentMethodWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (PaymentMethodWebBuilder.class);
	public PaymentMethodWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler(String moduleId)throws Exception{		
		_mc.initModuleContent(_moduleEle, "[custom=loop]");	
		putMainData();
	}
	
	private void putMainData(){		
		HashMap loopContent = new HashMap();		
		ArrayList dataArray = null;
		StringBuffer query = new StringBuffer();
		LinkedHashMap paramMap = new LinkedHashMap();
		try{
			query.append("Select ");
			query.append(PaymentMethodDef.code);
			query.append(",").append(PaymentMethodDef.instruction);
			query.append(",").append(PaymentMethodDef.parameters);
			query.append(" From ").append(PaymentMethodDef.TABLE);
			query.append(" Where ").append(PaymentMethodDef.companyid).append("=?");
			query.append(" And ").append(PaymentMethodDef.status).append("=?");
			query.append(" Order By ").append(PaymentMethodDef.code);
			
			paramMap.put(PaymentMethodDef.companyid, _shopInfoBean.getShopName());
			paramMap.put(PaymentMethodDef.status, GeneralConst.ACTIVE);
			
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
