package com.forest.mfg.webbuilder;

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
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.beandef.MFG_Grouping;
import com.forest.share.webbuilder.BaseWebBuilder;

public class MFG_CategoryWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (MFG_CategoryWebBuilder.class);
	public MFG_CategoryWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler(String moduleId)throws Exception{				
		_mc.initModuleContent(_moduleEle, "loop[id=category]");
		putMainData();		
		_mc.moduleContentFinish();
	}
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append(MFG_Grouping.groupid);
		query.append(",").append(MFG_Grouping.groupname);		
		
		query.append(" From ").append(MFG_Grouping.TABLE);
		query.append(" Where ");
		query.append(MFG_Grouping.companyid).append("=?");
		query.append(" And ").append(MFG_Grouping.grouptype).append("=?");
		query.append(" And ").append(MFG_Grouping.status).append("='").append(GeneralConst.ACTIVE).append("'");
		 
		paramMap.put(MFG_Grouping.companyid, _shopInfoBean.getShopName());
		paramMap.put(MFG_Grouping.grouptype, MFG_SelectBuilder.GROUP_TYPE_PRODUCT);
		try{
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
