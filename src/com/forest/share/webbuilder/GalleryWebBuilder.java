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
import com.forest.common.util.DateUtil;
import com.forest.share.beandef.GalleryDef;
import com.forest.share.beandef.GalleryDetailDef;

public class GalleryWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (GalleryWebBuilder.class);
	public GalleryWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public void displayHandler(String moduleId)throws Exception{				
		_mc.initModuleContent(_moduleEle);
		if("gallery.list".equals(moduleId)){
			putMainData();
		}else if("gallery.detail".equals(moduleId)){			
			putSingleData();
		}else if("gallery.list.single".equals(moduleId)){			
			putMainSingleData();
		}else{			
			putBannerData();
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
		String gid=_req.getParameter(GalleryDef.gid.name);
		
		query.append("Select ");		
		query.append("b.").append(GalleryDetailDef.gid);
		query.append(",b.").append(GalleryDetailDef.filename);
		query.append(",b.").append(GalleryDetailDef.description);
		query.append(" From ").append(GalleryDef.TABLE).append(" a");		
		query.append(" Inner Join ").append(GalleryDetailDef.TABLE).append(" b On");
		query.append(" b.").append(GalleryDetailDef.gid).append("=a.").append(GalleryDef.gid);
		
		query.append(" Where");
		query.append(" a.").append(GalleryDef.companyid).append("=?");
		query.append(" And a.").append(GalleryDef.gid).append("=?");		
		
		query.append(" Order By b.").append(GalleryDetailDef.position);
		paramMap.put(GalleryDef.companyid, _shopInfoBean.getShopName());
		paramMap.put(GalleryDef.gid, gid);
		
		
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				filename = (String) loopContent.get(GalleryDetailDef.filename.name);	
				filename = "/photo/gallery/"+(String) loopContent.get(GalleryDetailDef.gid.name)+"/"+filename;
				loopContent.put(GalleryDetailDef.filename.name, filename);
				_mc.setLoopElementContent(loopContent);
			}									
			
			// Set Main
			queryMain.append("Select ");			
			queryMain.append(GalleryDef.title).append(",");
			queryMain.append(GalleryDef.description).append(",");
			queryMain.append("DATE(").append(GalleryDef.updatedate).append(") As ").append(GalleryDef.updatedate);
			queryMain.append(" From ").append(GalleryDef.TABLE);
			queryMain.append(" Where ");
			queryMain.append(GalleryDef.companyid).append("=?");
			queryMain.append(" And ").append(GalleryDef.gid).append("=?");
			
			loopContent = _gs.searchDataMap(queryMain, paramMap);
			loopContent.put(GalleryDef.updatedate.name, DateUtil.formatDate((String) loopContent.get(GalleryDef.updatedate.name), "yyyy-MM-dd", "dd MMM yyyy"));
			String desc = (String) loopContent.get(GalleryDef.description.name);
			if(!CommonUtil.isEmpty(desc)){
				desc = desc.replaceAll("\n", "<br>");
				loopContent.put(GalleryDef.description.name, desc);
			}
			_mc.setElementContent(loopContent);
		}catch(Exception e){
			logger.error(e, e);
		}			
	}
	
	private void putBannerData(){
		String filename = null;
		ArrayList dataArray = null;
		HashMap loopContent = null;
		int count = 0;
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ");		
		query.append("b.").append(GalleryDetailDef.gid);
		query.append(",b.").append(GalleryDetailDef.filename);
		query.append(",b.").append(GalleryDetailDef.description);
		query.append(" From ").append(GalleryDef.TABLE).append(" a");		
		query.append(" Inner Join ").append(GalleryDetailDef.TABLE).append(" b On");
		query.append(" b.").append(GalleryDetailDef.gid).append("=a.").append(GalleryDef.gid);
		
		query.append(" Where");
		query.append(" a.").append(GalleryDef.companyid).append("=?");
		query.append(" And a.").append(GalleryDef.title).append("=?");		
		
		query.append(" Order By a.").append(GalleryDef.updatedate).append(" desc");
		paramMap.put(GalleryDef.companyid, _shopInfoBean.getShopName());
		paramMap.put(GalleryDef.title, "MAIN BANNER");
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				filename = (String) loopContent.get(GalleryDetailDef.filename.name);	
				filename = "/photo/gallery/"+(String) loopContent.get(GalleryDetailDef.gid.name)+"/"+filename;
				loopContent.put(GalleryDetailDef.filename.name, filename);
				_mc.setLoopElementContent(loopContent);
			}									
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
		query.append("a.").append(GalleryDef.gid);
		query.append(",a.").append(GalleryDef.title);
		query.append(",a.").append(GalleryDef.description);
		query.append(" From ").append(GalleryDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(GalleryDef.companyid).append("=?");
		query.append(" And a.").append(GalleryDef.title).append("!='MAIN BANNER'");
		query.append(" Order By a.").append(GalleryDef.updatedate).append(" desc");
		
		paramMap.put(GalleryDef.companyid, _shopInfoBean.getShopName());
		
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				loopContent.put(GalleryDef.title.name+"_plus", CommonUtil.replaceSpaceWithPlus((String) loopContent.get(GalleryDef.title.name)));
//				logger.debug("loopContent = "+loopContent.get(GalleryDef.title.name+"_plus"));
				putDetailData(loopContent);
				_mc.setLoopElementContent(loopContent);
				//putDetailData((String) loopContent.get(GalleryDef.gid.name));
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
		query.append("a.").append(GalleryDef.gid);
		query.append(",a.").append(GalleryDef.title);
		query.append(",a.").append(GalleryDef.description);
		query.append(", Concat('/photo/gallery/',").append("a.").append(GalleryDef.gid).append(",'/tn_',").append("b.").append(GalleryDetailDef.filename).append(") As ").append(GalleryDetailDef.filename);
		query.append(" From ").append(GalleryDef.TABLE).append(" a");
		query.append(" Inner Join ").append(GalleryDetailDef.TABLE).append(" b On ");
		query.append(" b.").append(GalleryDetailDef.gid).append(" = a.").append(GalleryDef.gid);
		query.append(" Where");
		query.append(" a.").append(GalleryDef.companyid).append("=?");
		query.append(" And a.").append(GalleryDef.title).append("!='MAIN BANNER'");
		query.append(" And (");
		query.append(" b.").append(GalleryDetailDef.position).append("=1 Or b.").append(GalleryDetailDef.gdtlid).append(" In (");
		query.append(" Select Min(").append(GalleryDetailDef.gdtlid).append(") From ").append(GalleryDetailDef.TABLE);
		query.append(" Where ").append(GalleryDetailDef.gid).append(" = a.").append(GalleryDef.gid);
		query.append(" )");
		query.append(" )");
		query.append(" Order By a.").append(GalleryDef.gid);
		
		paramMap.put(GalleryDef.companyid, _shopInfoBean.getShopName());
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);	
				putDetailData(loopContent);
				_mc.setLoopElementContent(loopContent);
				//putDetailData((String) loopContent.get(GalleryDef.gid.name));
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
		String key = (String) contentMap.get(GalleryDef.gid.name);
		query.append("Select ");
		query.append("a.").append(GalleryDetailDef.filename);
		query.append(",a.").append(GalleryDetailDef.description);
		query.append(" From ").append(GalleryDetailDef.TABLE).append(" a");
		query.append(" Where");
		query.append(" a.").append(GalleryDetailDef.gid).append("=?");
		query.append(" Order By a.").append(GalleryDetailDef.position);
		query.append(" Limit 5");
		paramMap.put(GalleryDetailDef.gid, key);
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			
			count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);
				filename = (String) loopContent.get(GalleryDetailDef.filename.name);				
			
				imageBuffer.append("<li>");
//				imageBuffer.append("<a href='").append("gallery-single/").append(key).append("/").append((String)contentMap.get(GalleryDef.title.name+"_plus")).append("/'>");
				imageBuffer.append("<img");
				imageBuffer.append(" src='").append("/photo/gallery/"+key+"/tn_"+filename).append("'");
				imageBuffer.append(" title='").append(URLEncoder.encode((String) loopContent.get(GalleryDetailDef.description.name), "UTF8")).append("'");
				imageBuffer.append(">");
//				imageBuffer.append("</a>");
     			imageBuffer.append("</li>");   
     			
     			if(i==0){
     				contentMap.put("main.image", "/photo/gallery/"+key+"/"+filename);
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
