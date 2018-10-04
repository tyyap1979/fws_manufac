package com.forest.retail.adminbuilder;

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
import com.forest.retail.beandef.RETAIL_CategoryDef;
import com.forest.retail.beandef.RETAIL_CategoryDetailDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.share.adminbuilder.BuildAdminHTML;
import com.forest.share.adminbuilder.UploadAdminBuilder;

public class RETAIL_ProductGroupAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (RETAIL_ProductGroupAdminBuilder.class);
	public RETAIL_ProductGroupAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public StringBuffer displayHandler()throws Exception{		
		ArrayList addArray = null;
		ArrayList listingArray = null;
		int columns = 1;		
		int tabsCount = 0;
		String[] tabs = null; 
		Object columnObj = null;
		Object tabsObj = null;
		StringBuffer buffer = new StringBuffer();
		StringBuffer subClsBuffer = new StringBuffer();
		
		if(_defClass!=null){
			String MODULE_NAME = (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME);
			Class[] subClass = BeanDefUtil.getSubClass(_defClass);
			
			searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
			listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);
			addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			columnObj = BeanDefUtil.getField(_defClass, "columns");
			tabsObj = BeanDefUtil.getField(_defClass, "tabs");	
			if(columnObj!=null){
				columns = Integer.parseInt(columnObj.toString());
			}
			if(tabsObj!=null){
				tabs = (String[])tabsObj;
				tabsCount = tabs.length;
			}
			
			buffer.append("<p:useTemplate>simplplan-template.htm</p:useTemplate>").append("\n");
			
			buffer.append("<p:component value=\"titlebar\">");
			buffer.append("%i18n.system.").append(MODULE_NAME).append(".title% - ");
			buffer.append(_shopInfoBean.getShopNameDesc());
			buffer.append("</p:component>").append("\n");
			
			buffer.append("<p:component value=\"title_right\">");
			buffer.append("<ul id=\"entryButtonBar\" class=\"sorting\" style=\"display: none;\">");
			buffer.append("<li><a href=\"#\" id=\"btnCancel\">Cancel</a></li>");
			buffer.append("<li><a href=\"#\" id=\"btnSave\">Save</a></li>");
			buffer.append("<li><a href=\"#\" id=\"btnPrint\">Print</a></li>");
			buffer.append("</ul>");
			buffer.append("</p:component>").append("\n");
			
			buffer.append("<p:component value=\"body\">").append("\n");	
	
			buffer.append("<div id=\"").append(MODULE_NAME).append("Module\" class='box_content'>").append("\n");	
				buffer.append("<div id='searchDiv'>");
					buffer.append ("<form id='").append (GeneralConst.LISTING_FORM).append ("' method='POST'>");
						buffer.append(buildListing(listingArray, MODULE_NAME, true)).append("\n");	
					buffer.append ("</form>");			
				buffer.append("</div>");
				
				buffer.append("<div id='searchDiv-Product'  style='display: none;width:900px;' class='dialogView'>");
					buffer.append ("<form action='retail_product.htm' id='").append (GeneralConst.LISTING_FORM).append ("' method='POST'>");
						buffer.append(buildListing(
								BeanDefUtil.getArrayList(RETAIL_ProductDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean), 
								(String) BeanDefUtil.getField(RETAIL_ProductDef.class, BeanDefUtil.MODULE_NAME), false)).append("\n");	
					buffer.append ("</form>");			
				buffer.append("</div>");
			
				// Add Form
				if(subClass!=null){
					for(int i=0; i<subClass.length; i++){
						subClsBuffer.append("<div style='width: auto; overflow:auto;'>");
						subClsBuffer.append("<fieldset>"); 
						subClsBuffer.append("<legend><b>").append(ResourceUtil.getAdminResourceValue(_resources, (String) BeanDefUtil.getField(subClass[i], BeanDefUtil.MODULE_NAME), "title", _clientBean.getLocale())).append("</b></legend>");						
						subClsBuffer.append (buildDetailEntryForm (subClass[i]));						
						subClsBuffer.append("</fieldset>");
						subClsBuffer.append("</div><br>");
					}
				}
				
				buffer.append("<div id='entryDiv' style='display: none;overflow:scroll;'>");
					buffer.append("<div id=\"").append(MODULE_NAME).append("ModuleEntry\"");
					buffer.append(" title='%i18n.system.").append(MODULE_NAME).append(".title% - ");
					buffer.append(_shopInfoBean.getShopNameDesc()).append("'");
					buffer.append(" class=\"entryScreen\">").append("\n");
					buffer.append ("<form id='").append (GeneralConst.ENTRY_FORM).append ("' method='POST' autocomplete='off'>");
						if(tabsCount>0){
							buffer.append("<div class='tabs'><ul>");
							for(int i=0; i< tabsCount; i++){
								buffer.append("<li><a href='#tabs-").append(i+1).append("'>").append(tabs[i]).append("</a></li>");	
							}
							buffer.append("</ul>");
							
							for(int i=0; i< tabsCount; i++){								
								buffer.append("<div id='tabs-").append(i+1).append("'>\n");
									buffer.append(buildEntryForm(MODULE_NAME, addArray, columns, (i+1))).append("\n<br>");
									if(i==0 && subClass!=null){
										buffer.append(subClsBuffer);
									}
								buffer.append("</div>\n\n");
							}
							
							buffer.append("</div>");
						}else{
							buffer.append(buildEntryForm(MODULE_NAME, addArray, columns)).append("\n<br>");
							buffer.append(subClsBuffer);
						}
						
						
					buffer.append ("</form>");	   
					buffer.append("</div>").append("\n");
				buffer.append("</div>");
			buffer.append("</div>").append("\n");
			buffer.append("</p:component>").append("\n");			
			
			// Read Additional Content From File
			buffer.append(FileHandler.readAdminFile (_shopInfoBean, _clientBean));	
		}
		
		return buffer;
	}		
}
