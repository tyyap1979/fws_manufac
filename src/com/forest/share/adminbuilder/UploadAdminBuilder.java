package com.forest.share.adminbuilder;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.FileUtil;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;


public class UploadAdminBuilder extends GenericAdminBuilder{
	private static Logger logger = Logger.getLogger(UploadAdminBuilder.class);
	private final int VERTICAL = 0;
	private final int HORIZONTAL = 1;
	private final String thumbnailPrefix = "tn_";
	private final int imageHeight = 800;
	private final int thumbnailHeight = 360;	
	private boolean _createThumbnail = true;
	public UploadAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);	
		
	} 
	
	public StringBuffer requestHandler()throws OwnException{
		JSONObject json = new JSONObject();
		StringBuffer rtnString = new StringBuffer();
		try{	
			// nicEdit
			String check = _req.getParameter("check");
			
			if(!CommonUtil.isEmpty(check)){
				
			}else{
				if(CommonUtil.isEmpty(_req.getParameter("id"))){
					uploadHandle(_req, _shopInfoBean.getShopName());
				}else{
					json = uploadNicEdit(_req, _shopInfoBean.getShopName());
					rtnString.append("<script>top.nicUploadButton.statusCb("+json.toString()+");</script>");
				}
			}
		}catch(Exception e){
			logger.error(e, e); 
		}
		return rtnString;
	}
	
	public static void removeFolder(ShopInfoBean shopInfoBean, String folderName){
		FileUtil fUtil = new FileUtil();
		String uploadPath = ResourceUtil.getCompanyAdminPath (shopInfoBean) ;	
		String toDirectory = uploadPath + "photo/" + folderName + "/";
		fUtil.deleteFolder(toDirectory);
	}
	
	public static void moveTempPhoto(HashMap[] detailMap, ShopInfoBean shopInfoBean, String folderName){
		FileUtil fUtil = new FileUtil();
		String uploadPath = ResourceUtil.getCompanyMainPath (shopInfoBean) ;	
		String webPath = ResourceUtil.getCompanyMainPath (shopInfoBean);
		String filename = null;		
		String status = null;
		String toDirectory = "";
		
		if(detailMap!=null){			
			toDirectory = webPath + "photo/" + folderName + "/";
			for(int i=0; i<detailMap.length; i++){				
				if(detailMap[i]!=null){				
					status = (String) detailMap[i].get("row-status");
					if("U".equals(status) && folderName.indexOf("option/")==-1){
						continue;
					}
					filename = (String) detailMap[i].get("filename");					
					
					if("N".equals(status) || "U".equals(status)){												
						fUtil.moveFile(uploadPath + "temp/" +filename, toDirectory);
						fUtil.moveFile(uploadPath + "temp/tn_" +filename, toDirectory);
						logger.debug("Moving "+uploadPath + "temp/" +filename + " to "+toDirectory);						
					}else if("D".equals(status)){
						logger.debug("Delete "+toDirectory+filename);
						fUtil.deleteFolder(toDirectory+filename);
						fUtil.deleteFolder(toDirectory+"tn_"+filename);
					}
				}
			}
		}
	}
	
	private JSONObject uploadNicEdit(HttpServletRequest req, String shopName) throws OwnException{
		logger.debug ("--------- uploadHandle ---------");		
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();		
		File m_oFile = null; 
		ServletFileUpload upload = null;
		
		String uploadPath = null;
		JSONObject json = new JSONObject();
		int width = 0;
		try{
			_createThumbnail = false;
			uploadPath = ResourceUtil.getCompanyAdminPath (_shopInfoBean) + "photo/nicEdit";		
			logger.debug ("uploadPath = "+uploadPath);
			
			new FileUtil().createFolder(uploadPath);		
			m_oFile = new File(uploadPath);
			
			// Set factory constraints
			factory.setSizeThreshold(1000);
			factory.setRepository(m_oFile);
			
			// Create a new file upload handler
			upload = new ServletFileUpload(factory);			
			
			List /* FileItem */ items = upload.parseRequest(req);
			FileItem item = null;
			Iterator iter = items.iterator();			
			String filename = null;
			while (iter.hasNext()) {
			    item = (FileItem) iter.next();
			    if (!item.isFormField()) {
			    	width = new ImageIcon(item.get ()).getIconWidth();
			    	filename = item.getName();
					filename = _req.getParameter("id") + filename.substring(filename.lastIndexOf("."));
			        processUploadedFile(item, uploadPath, filename);				        
			    }
			}			
		
			json.put("done", 1);
			json.put("width", width);
			json.put("url", "/photo/nicEdit/"+filename);
			
		}catch(Exception e){
			logger.error (e, e);
		}	
		
		return json;
	}
	
	private void uploadHandle(HttpServletRequest req, String shopName) throws OwnException{
		logger.debug ("--------- uploadHandle ---------");		
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();		
		File m_oFile = null; 
		ServletFileUpload upload = null;
		
		String uploadPath = null;
		try{
			_createThumbnail = true;
			uploadPath = ResourceUtil.getCompanyMainPath (_shopInfoBean) + "temp";		
			logger.debug ("uploadPath = "+uploadPath);
			
			new FileUtil().createFolder(uploadPath);		
			m_oFile = new File(uploadPath);
			
			// Set factory constraints
			factory.setSizeThreshold(1000);
			factory.setRepository(m_oFile);
			
			// Create a new file upload handler
			upload = new ServletFileUpload(factory);			
			
			List /* FileItem */ items = upload.parseRequest(req);
			FileItem item = null;
			Iterator iter = items.iterator();			
			while (iter.hasNext()) {
			    item = (FileItem) iter.next();
			    if (!item.isFormField()) {			    				    	
			        processUploadedFile(item, uploadPath, item.getName());				        
			    }else{
			    	if(item.getFieldName().equals("thumb") && item.getString().equals("false")){
			    		_createThumbnail = false;
			    	}
			    	logger.debug(item.getFieldName() + " = "+item.getString());
			    }
			}			
		}catch(Exception e){
			logger.error (e, e);
		}
				
	}
	private void processUploadedFile(FileItem p_oItem, String saveLocation, String fileName) throws Exception{
	    try{
	    	ImageIcon m_oImage = new ImageIcon(p_oItem.get ());	  	    			    
		    
		    logger.debug ("fileName: "+fileName); 
//		    File uploadedFile = new File(saveLocation+"/"+fileName);
//		    
//		    p_oItem.write(uploadedFile);
		    

		    if(m_oImage.getIconHeight () > m_oImage.getIconWidth ()){
		    	if(m_oImage.getIconHeight()<imageHeight){
		    		p_oItem.write(new File(saveLocation+"/"+fileName));
		    	}else{
		    		saveResize(m_oImage, imageHeight, VERTICAL, saveLocation, fileName);
		    	}
		    	if(_createThumbnail){
		    		saveThumbnail(m_oImage, thumbnailHeight, VERTICAL, saveLocation, fileName);
		    	}
		    }else{
		    	if(m_oImage.getIconHeight()<imageHeight){
		    		p_oItem.write(new File(saveLocation+"/"+fileName));
		    	}else{
		    		saveResize(m_oImage, imageHeight, HORIZONTAL, saveLocation, fileName);
		    	}
		    	if(_createThumbnail){
		    		saveThumbnail(m_oImage, thumbnailHeight, HORIZONTAL, saveLocation, fileName);
		    	}
		    }
		   
	    }catch(Exception oe){
	    	throw oe;
	    }
	    
	}
	
	public void saveResize(ImageIcon p_oImage, int height, int dir, String saveLocation, String p_sFilename){
		ImageIcon thumb = null;		
		
		File m_oFile = new File(saveLocation + "/" + p_sFilename);
		thumb = new ImageIcon(p_oImage.getImage().getScaledInstance(-1, height, Image.SCALE_SMOOTH));

        
        if (thumb != null){
            BufferedImage bi = new BufferedImage(
                thumb.getIconWidth(), 
                thumb.getIconHeight(), 
                BufferedImage.TYPE_INT_RGB
            );
            Graphics g = bi.getGraphics();
            g.drawImage(thumb.getImage(), 0, 0, null);
            try{
                ImageIO.write(bi, "JPEG", m_oFile);
            }catch (IOException ioe){
                logger.debug("Error occured saving thumbnail");
            }
        }else{
            logger.debug("Thumbnail has not yet been created");
        }
    }
	
	public void saveThumbnail(ImageIcon p_oImage, int height, int dir, String saveLocation, String p_sFilename){
		ImageIcon thumb = null;		
		
		File m_oFile = new File(saveLocation+"/"+thumbnailPrefix + p_sFilename);
		
        if (dir == HORIZONTAL){
            thumb = new ImageIcon(p_oImage.getImage().getScaledInstance(height, -1, Image.SCALE_SMOOTH));
        }else{
            thumb = new ImageIcon(p_oImage.getImage().getScaledInstance(-1, height, Image.SCALE_SMOOTH));
        }
        
        if (thumb != null){
            BufferedImage bi = new BufferedImage(
                thumb.getIconWidth(), 
                thumb.getIconHeight(), 
                BufferedImage.TYPE_INT_RGB
            );
            Graphics g = bi.getGraphics();
            g.drawImage(thumb.getImage(), 0, 0, null);
            try{
                ImageIO.write(bi, "JPEG", m_oFile);
            }catch (IOException ioe){
                logger.debug("Error occured saving thumbnail");
            }
        }else{
            logger.debug("Thumbnail has not yet been created");
        }
    }
}
