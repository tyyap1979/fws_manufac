package com.forest.share.webbuilder;

import java.net.URLEncoder;
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
import com.forest.common.util.CommonUtil;
import com.forest.share.beandef.GalleryDef;
import com.forest.share.beandef.GalleryDetailDef;
import com.forest.share.beandef.MaterialDef;
import com.forest.share.beandef.MaterialDetailDef;

public class MaterialWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (MaterialWebBuilder.class);
	public MaterialWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler(String moduleId)throws Exception{		
		_mc.initModuleContent(_moduleEle);
		if("material.list".equals(moduleId)){
			putMainData();
		}else if("material.list.single".equals(moduleId)){			
			putMainSingleData();
		}else if("material.detail".equals(moduleId)){			
			putSingleData();
		}else if("material.tag".equals(moduleId)){			
			putTagDetail();
		}else if("material.tag.title".equals(moduleId)){			
			putTagDetailTitle();
		}
	}
	
	private void putTagDetailTitle(){
		String tag = _req.getParameter("tag");
		HashMap mainContent = new HashMap();
		mainContent.put("title", tag);
		_mc.setElementContent(mainContent);
	}
	private void putTagDetail(){
		String filename = null;
		String tnFilename = null;
		ArrayList dataArray = null;
		HashMap loopContent = null;
		int count = 0;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		
		String tag = _req.getParameter("tag");
		
		query.append("Select ");
		query.append("a.").append(MaterialDef.mid);
		query.append(",b.").append(MaterialDetailDef.filename);
		query.append(",b.").append(MaterialDetailDef.description);
		query.append(" From ").append(MaterialDef.TABLE).append(" a");		
		query.append(" Inner Join ").append(MaterialDetailDef.TABLE).append(" b On");
		query.append(" b.").append(MaterialDetailDef.mid).append("=a.").append(MaterialDef.mid);
		
		query.append(" Where");
		query.append(" a.").append(MaterialDef.companyid).append("=?");
		query.append(" And b.").append(MaterialDetailDef.tag).append(" like '%").append(tag).append("%'");		
		
		query.append(" Order By a.").append(MaterialDef.updatedate).append(" desc");
		paramMap.put(MaterialDef.companyid, _shopInfoBean.getShopName());		
				
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				filename = (String) loopContent.get(MaterialDetailDef.filename.name);
				tnFilename = "/photo/material/"+(String) loopContent.get(MaterialDetailDef.mid.name)+"/tn_"+filename;
				filename = "/photo/material/"+(String) loopContent.get(MaterialDetailDef.mid.name)+"/"+filename;
				
				loopContent.put(MaterialDetailDef.filename.name, filename);
				loopContent.put("tn_"+MaterialDetailDef.filename.name, tnFilename);				
				_mc.setLoopElementContent(loopContent);
			}									
			
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
	
	private void putSingleData(){
		String filename = null;
		ArrayList dataArray = null;
		HashMap loopContent = null;
		int count = 0;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		StringBuffer queryMain = new StringBuffer();
		String mid=_req.getParameter(MaterialDef.mid.name);
		
		query.append("Select ");		
		query.append("b.").append(MaterialDetailDef.mid);
		query.append(",b.").append(MaterialDetailDef.filename);
		query.append(",b.").append(MaterialDetailDef.description);
		query.append(" From ").append(MaterialDef.TABLE).append(" a");		
		query.append(" Inner Join ").append(MaterialDetailDef.TABLE).append(" b On");
		query.append(" b.").append(MaterialDetailDef.mid).append("=a.").append(MaterialDef.mid);
		
		query.append(" Where");
		query.append(" a.").append(MaterialDef.companyid).append("=?");
		query.append(" And a.").append(MaterialDef.mid).append("=?");		
		
		query.append(" Order By b.").append(MaterialDetailDef.position);
		paramMap.put(MaterialDef.companyid, _shopInfoBean.getShopName());
		paramMap.put(MaterialDef.mid, mid);
		
		
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				filename = (String) loopContent.get(MaterialDetailDef.filename.name);	
				filename = "/photo/material/"+(String) loopContent.get(MaterialDetailDef.mid.name)+"/"+filename;
				loopContent.put(MaterialDetailDef.filename.name, filename);
				_mc.setLoopElementContent(loopContent);
			}									
			
			// Set Main
			queryMain.append("Select ");			
			queryMain.append(MaterialDef.title).append(",");
			queryMain.append(MaterialDef.description).append(",");
			queryMain.append("DATE(").append(MaterialDef.updatedate).append(") As ").append(MaterialDef.updatedate);
			queryMain.append(" From ").append(MaterialDef.TABLE);
			queryMain.append(" Where ");
			queryMain.append(MaterialDef.companyid).append("=?");
			queryMain.append(" And ").append(MaterialDef.mid).append("=?");
			
			loopContent = _gs.searchDataMap(queryMain, paramMap);
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;
		int count = 0;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append("a.").append(MaterialDef.mid);
		query.append(",a.").append(MaterialDef.title);
		query.append(",a.").append(MaterialDef.description);
		query.append(" From ").append(MaterialDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(MaterialDef.companyid).append("=?");		
		query.append(" Order By a.").append(MaterialDef.updatedate).append(" desc");	
		
		paramMap.put(MaterialDef.companyid, _shopInfoBean.getShopName());
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				loopContent.put(MaterialDef.title.name+"_plus", CommonUtil.replaceSpaceWithPlus((String) loopContent.get(MaterialDef.title.name)));
				putDetailData(loopContent);
				_mc.setLoopElementContent(loopContent);
				//putDetailData((String) loopContent.get(MaterialDef.mid.name));
			}						
		}catch(Exception e){
			logger.error(e, e);
		}			
	}
	
	private void putMainSingleData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;
		int count = 0;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		
		query.append("Select ");
		query.append("a.").append(MaterialDef.mid);
		query.append(",a.").append(MaterialDef.title);
		query.append(",a.").append(MaterialDef.description);
		query.append(", Concat('/photo/material/',").append("a.").append(MaterialDef.mid).append(",'/tn_',").append("b.").append(MaterialDetailDef.filename).append(") As ").append(MaterialDetailDef.filename);
		query.append(" From ").append(MaterialDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MaterialDetailDef.TABLE).append(" b On ");
		query.append(" b.").append(MaterialDetailDef.mid).append(" = a.").append(MaterialDef.mid);
		query.append(" Where");
		query.append(" a.").append(MaterialDef.companyid).append("=?");
		query.append(" And a.").append(MaterialDef.title).append("!='MAIN BANNER'");
		query.append(" And (");
		query.append(" b.").append(MaterialDetailDef.position).append("=1 Or b.").append(MaterialDetailDef.mdtlid).append(" In (");
		query.append(" Select Min(").append(MaterialDetailDef.mdtlid).append(") From ").append(MaterialDetailDef.TABLE);
		query.append(" Where ").append(MaterialDetailDef.mid).append(" = a.").append(MaterialDef.mid);
		query.append(" )");
		query.append(" )");
		query.append(" Order By a.").append(MaterialDef.mid);
		
		paramMap.put(MaterialDef.companyid, _shopInfoBean.getShopName());
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				putDetailData(loopContent);
				_mc.setLoopElementContent(loopContent);
			}						
		}catch(Exception e){
			logger.error(e, e);
		}			
	}
	
	private StringBuffer putDetailData(HashMap contentMap){
		ArrayList dataArray = null;
		HashMap loopContent = null;
		int count = 0;
		String filename = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer imageBuffer = new StringBuffer();
		StringBuffer query = new StringBuffer();		
		String key = (String) contentMap.get(MaterialDef.mid.name);
		query.append("Select ");
		query.append("a.").append(MaterialDetailDef.filename);
		query.append(",a.").append(MaterialDetailDef.description);
		query.append(" From ").append(MaterialDetailDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(MaterialDetailDef.mid).append("=?");
		query.append(" Order By a.").append(MaterialDetailDef.position);
		query.append(" Limit 5");
		
		paramMap.put(MaterialDetailDef.mid, key);
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
//			for(int i=0; i<count; i++){
//				loopContent = (HashMap) dataArray.get(i);
//				filename = (String) loopContent.get(MaterialDetailDef.filename.name);				
//				if(i>0){
//					imageBuffer.append("<a class='hide'");
//					imageBuffer.append(" rel='prettyPhoto[").append(key).append("]'");
//					imageBuffer.append(" href='").append("/photo/material/"+key+"/"+filename).append("'");
//					imageBuffer.append(" title='").append((String) loopContent.get(MaterialDetailDef.description.name)).append("'");
//					imageBuffer.append(">");							     		
//	     			imageBuffer.append("</a>");   
//				}else{
//					imageBuffer.append("<a ");
//					imageBuffer.append(" rel='prettyPhoto[").append(key).append("]'");
//					imageBuffer.append(" href='").append("/photo/material/"+key+"/"+filename).append("'");
//					imageBuffer.append(">");
//					imageBuffer.append("<img class='overlay-item-image' src='"+"/photo/material/"+key+"/tn_"+filename+"' alt='View Images' />");				     			
//	     			imageBuffer.append("</a>");     	
//	     			contentMap.put("main.image", "/photo/material/"+key+"/"+filename);
//				}				
//			}	
			
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);
				filename = (String) loopContent.get(MaterialDetailDef.filename.name);				
			
				imageBuffer.append("<li>");
				imageBuffer.append("<img");
				imageBuffer.append(" src='").append("/photo/material/"+key+"/tn_"+filename).append("'");
				imageBuffer.append(" title='").append(URLEncoder.encode((String) loopContent.get(MaterialDetailDef.description.name), "UTF8")).append("'");
				imageBuffer.append(">");
     			imageBuffer.append("</li>");   
     			
     			if(i==0){
     				contentMap.put("main.image", "/photo/material/"+key+"/"+filename);
     			}
			}		
			
			contentMap.put("images.list", imageBuffer.toString());
			contentMap.put("image.key", key);
			
		}catch(Exception e){
			logger.error(e, e);
		}
//		logger.debug("imageBuffer = "+imageBuffer);
		return imageBuffer;		
	}
}
