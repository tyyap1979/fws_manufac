package com.forest.mfg.webbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.FileHandler;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_Grouping;
import com.forest.share.webbuilder.BaseWebBuilder;

public class MFG_ProductGroupWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (MFG_ProductGroupWebBuilder.class);
	public MFG_ProductGroupWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler()throws Exception{		
		_mc.initModuleContent(_moduleEle);
		getData();
	}
	
	private void getData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;
		int count = 0;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append("a.").append(MFG_Grouping.groupid);
		query.append(",a.").append(MFG_Grouping.groupname);
		query.append(" From ").append(MFG_Grouping.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b On b.");
		query.append(MFG_CustProductDef.grouptype).append("=a.").append(MFG_Grouping.groupid);
		query.append(" Where");
		query.append(" b.").append(MFG_CustProductDef.companyid).append("=?");
		query.append(" And a.").append(MFG_Grouping.status).append("='").append(GeneralConst.ACTIVE).append("'");
		query.append(" And b.").append(MFG_CustProductDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		query.append(" Group By ").append("a.").append(MFG_Grouping.groupid).append(",a.").append(MFG_Grouping.groupname); 
		paramMap.put(MFG_CustProductDef.companyid, _shopInfoBean.getShopName());
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);
				_mc.setLoopElementContent(loopContent);
			}						
		}catch(Exception e){
			logger.error(e, e);
		}
				
	}
}
