package com.forest.admin.builder;

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.BusinessConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SessionConst;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.services.AdminSQLServices;

public class MenuBuilder extends GenericAdminBuilder
{			
	private Logger logger = Logger.getLogger (this.getClass ());
	private final String MODULE_NAME = "menu";

	public MenuBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{		
		super(mShopInfoBean, mClientBean, conn, req, resp, defClass, accessByShop, resources);	
	} 	

	public void displayHandler(StringBuffer buffer)throws OwnException{
		int fromIndex = 0;		
		try{						
			// Display						
			if((fromIndex = buffer.indexOf ("system.menu.list", 0)) != -1){				
				if("forestwebsolution".equals (_shopInfoBean.getShopName ())){
					buffer.replace(fromIndex, "system.menu.list".length () + fromIndex, buildSuperListing().toString ());
				}else{
					if(_shopInfoBean.getBusiness().equals(BusinessConst.MANUFACTURING)){
						buffer.replace(fromIndex, "system.menu.list".length () + fromIndex, buildManufacturingListing().toString ());
					}else if(_shopInfoBean.getBusiness().equals(BusinessConst.INTERIOR_DESIGN)){
						buffer.replace(fromIndex, "system.menu.list".length () + fromIndex, buildIDMenu().toString ());
					}else if(_shopInfoBean.getBusiness().equals(BusinessConst.RETAIL)){
						buffer.replace(fromIndex, "system.menu.list".length () + fromIndex, buildRetailMenu().toString ());
					}
				}
			}
			
			if((fromIndex = buffer.indexOf ("system.menu.top", 0)) != -1){									
				buffer.replace(fromIndex, "system.menu.top".length () + fromIndex, buildTopMenu ().toString ());
			}
			
		}catch(Exception e){			
			throw new OwnException(e);
		}finally{
			
		}		
	}
	
	private StringBuffer buildSuperListing() {
		HttpSession session = _req.getSession (false);
		StringBuffer buffer = new StringBuffer();
	    try{	    		    
//	    	if(session==null){
//	    		return null;
//	    	}
//	    	HashMap authMap = null;	    	
//			if(_shopInfoBean.getCompanyGroup()!=null){
//				authMap = (HashMap) session.getAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getCompanyGroup());
//			}else{
//				authMap = (HashMap) session.getAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getShopName());
//			}
//	    	if(authMap!=null){
	    		buffer.append ("<ul id='navigation' class='dropdown'>");    
		    		
		    		buffer.append ("<li><a href='shop.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.shop", _clientBean.getLocale ())).append ("</a></li>");		    				    			    	
		    		buffer.append ("<li><a href='website.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.website", _clientBean.getLocale ())).append ("</a></li>");
		    		
		    		buffer.append ("<li class='topnav'>");
			    		buffer.append ("<a href='#'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "user", _clientBean.getLocale ())).append ("</a>");
				    	buffer.append ("<ul class='subnav'>");
					    	buffer.append ("<li><a href='clientuser.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.client.user", _clientBean.getLocale ())).append ("</a></li>");
				    		buffer.append ("<li><a href='adminuser.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.admin.user", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("</ul>");
			    	buffer.append ("</li>");
			    	
			    	buffer.append ("<li class='topnav'>");
			    		buffer.append ("<a href='#'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "admin", _clientBean.getLocale ())).append ("</a>");
				    	buffer.append ("<ul class='subnav'>");
				    		buffer.append ("<li><a href='module-admin.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.moduleadmin", _clientBean.getLocale ())).append ("</a></li>");
//				    		buffer.append ("<li><a href='module.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.module", _clientBean.getLocale ())).append ("</a></li>");				    		
				    	buffer.append ("</ul>");
			    	buffer.append ("</li>");
			    	
	
		    		buffer.append ("</ul>");
//	    	}
	    }catch(Exception e){
	    	logger.error (e, e);
	    }finally{
	    	
	    }
	    return buffer;
	}
	
	private StringBuffer buildManufacturingListing() {
		HttpSession session = _req.getSession (false);
		StringBuffer buffer = new StringBuffer();
		AdminSQLServices ass = new AdminSQLServices(null, null, _dbConnection);
	    try{
	    	
	    	if(session==null){
	    		return null;
	    	}
		    	buffer.append ("<ul id='navigation' class='dropdown'>");            
		    	
		    	buffer.append ("<li class='topnav'>");	    	
		    		buffer.append ("<a href='mfg_transaction.htm' class='frames'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "transaction", _clientBean.getLocale ())).append ("</a>");
			    	buffer.append ("<ul class='subnav'>");				    	
				    	buffer.append ("<li><a href='mfg_transaction.htm' class='icon edit'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_transaction", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='mfg_po.htm' class='icon edit'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_po", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("</ul>");
		    	buffer.append ("</li>");

		    	buffer.append ("<li class='topnav'>");	    	
		    		buffer.append ("<a href='#' class='dashboard'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_account", _clientBean.getLocale ())).append ("</a>");
			    	buffer.append ("<ul class='subnav'>");				    	
				    	buffer.append ("<li><a href='mfg_receivepayment.htm' class='icon book2'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_receivepayment", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='mfg_statement.htm' class='icon book2'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_statement", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='mfg_debitnote.htm' class='icon book2'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_debitnote", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='mfg_creditnote.htm' class='icon book2'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_creditnote", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='mfg_supplierinvoice.htm' class='icon book2'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_supplierinvoice", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='mfg_paymentvoucher.htm' class='icon book2'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_paymentvoucher", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='mfg_report.htm' class='icon cabin'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_report", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("</ul>");
		    	buffer.append ("</li>");
		    	
		    	
		    	buffer.append ("<li class='topnav'>");	    	
		    		buffer.append ("<a href='mfg_custproduct.htm' class='pictures'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_custproduct", _clientBean.getLocale ())).append ("</a>");
			    	buffer.append ("<ul class='subnav'>");
			    	
			    	buffer.append ("<li><a href='mfg_custproduct.htm' class='icon list'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_custproduct", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='mfg_prodoption.htm' class='icon list'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_productopt", _clientBean.getLocale ())).append ("</a></li>");
			    	if("megatrend".equals(_shopInfoBean.getShopName())){
				    	buffer.append ("<li><a href='mfg_supplierproduct.htm' class='icon list'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_supplierproduct", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='mfg_supplierprodoption.htm' class='icon list'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_supplierproductopt", _clientBean.getLocale ())).append ("</a></li>");
			    	}
			    	buffer.append ("<li><a href='mfg_raw.htm' class='icon settings'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_raw", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='mfg_grouping.htm' class='icon settings'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_grouping", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("</ul>");
		    	buffer.append ("</li>");
		    				    	
		    	buffer.append ("<li class='topnav'>");
		    	buffer.append ("<a href='#' class='settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "admin", _clientBean.getLocale ())).append ("</a>");
		    	buffer.append ("<ul class='subnav'>");

		    	buffer.append ("<li><a href='supplier.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.supplierprofile", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='customer.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.customerprofile", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='agentprofile.htm' class='icon users'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.agentprofile", _clientBean.getLocale ())).append ("</a>").append ("</li>");
	    		buffer.append ("<li><a href='adminuser.htm' class='icon users'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.admin.user", _clientBean.getLocale ())).append ("</a>").append ("</li>");
		    	buffer.append ("<li><a href='autonumber.htm' class='icon settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.gennumber", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='mfg_rawtoproduct.htm' class='icon settings'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "mfg_rawtoproduct", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='shopprofile.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.shop.profile", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='smstemplate.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.smstemplate", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='sms.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.sms", _clientBean.getLocale ())).append ("</a></li>");		    	
		    	buffer.append ("</ul>");
		    	buffer.append ("</li>");
		    	
		    	if(ass.getShopsWebSite(_shopInfoBean.getShopName()).size()==1){
			    	buffer.append ("<li class='topnav'>");
			    	buffer.append ("<a href='#' class='settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "web", _clientBean.getLocale ())).append ("</a>");
			    	buffer.append ("<ul class='subnav'>");
				    	buffer.append ("<li><a href='websiteprofile.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.website.profile", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='pagecontent.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.pagecontent", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='gallery.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.gallery", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='material.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.material", _clientBean.getLocale ())).append ("</a></li>");		    	
				    	buffer.append ("<li><a href='clientuser.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.members", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='shipmentdomestic.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.shipmentdomestic", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='paymentmethod.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.paymentmethod", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='emailtemplate.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.emailtemplate", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("</ul>");
				    	buffer.append ("</li>");			    	
			    	buffer.append ("</ul>");
			    	buffer.append ("</li>");	
		    	}
		    	buffer.append ("</ul>");	    		
//	    	}
	    }catch(Exception e){
	    	logger.error (e, e);
	    }finally{
	    	
	    }
	    return buffer;		
	}
	
	private StringBuffer buildIDMenu() {
		HttpSession session = _req.getSession (false);
		StringBuffer buffer = new StringBuffer();
	    try{
	    	
	    	if(session==null){
	    		return null;
	    	}
		    	buffer.append ("<ul id='navigation' class='dropdown'>");            
		    	
		    	buffer.append ("<li class='topnav'>");	    	
		    		buffer.append ("<a href='id_transaction.html' class='frames'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "id_transaction", _clientBean.getLocale ())).append ("</a>");
			    	buffer.append ("<ul class='subnav'>");				    	
				    	buffer.append ("<li><a href='id_transaction.html' class='icon edit'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "id_transaction", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("<li><a href='id_po.html' class='icon edit'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "id_po", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("</ul>");
		    	buffer.append ("</li>");
		    				    	
		    	buffer.append ("<li class='topnav'>");
		    	buffer.append ("<a href='#' class='settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "admin", _clientBean.getLocale ())).append ("</a>");
		    	buffer.append ("<ul class='subnav'>");
		    	buffer.append ("<li><a href='friendlylink.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.friendlylink", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='websiteprofile.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.website.profile", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='shopprofile.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.shop.profile", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='agentprofile.html' class='icon users'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.agentprofile", _clientBean.getLocale ())).append ("</a>").append ("</li>");
		    	buffer.append ("<li><a href='pagecontent.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.pagecontent", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='gallery.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.gallery", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='material.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.material", _clientBean.getLocale ())).append ("</a></li>");
	    		buffer.append ("<li><a href='adminuser.html' class='icon users'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.admin.user", _clientBean.getLocale ())).append ("</a>").append ("</li>");
		    	buffer.append ("<li><a href='autonumber.html' class='icon settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.gennumber", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='emailtemplate.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.emailtemplate", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='smstemplate.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.smstemplate", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("<li><a href='sms.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.sms", _clientBean.getLocale ())).append ("</a></li>");
		    	buffer.append ("</ul>");
		    	buffer.append ("</li>");				             
		    	buffer.append ("</ul>");	    		
//	    	}
	    }catch(Exception e){
	    	logger.error (e, e);
	    }finally{
	    	
	    }
	    return buffer;		
	}
	
	private StringBuffer buildRetailMenu() {
		HttpSession session = _req.getSession (false);
		StringBuffer buffer = new StringBuffer();
	    try{
	    	
	    	if(session==null){
	    		return null;
	    	}
		    	buffer.append ("<ul id='navigation' class='dropdown'>");            
		    	
			    	buffer.append ("<li class='topnav'>");	    	
			    		buffer.append ("<a href='retail_order.htm' class='frames'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "retail_transaction", _clientBean.getLocale ())).append ("</a>");
				    	buffer.append ("<ul class='subnav'>");				    	
					    	buffer.append ("<li><a href='retail_order.htm' class='icon edit'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "retail_transaction", _clientBean.getLocale ())).append ("</a></li>");
					    	buffer.append ("<li><a href='retail_cart.htm' class='icon edit'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "retail_cart", _clientBean.getLocale ())).append ("</a></li>");
				    	buffer.append ("</ul>");
			    	buffer.append ("</li>");
			    				    	
			    	buffer.append ("<li class='topnav'>");	    	
		    		buffer.append ("<a href='retail_product.htm' class='pictures'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "retail_product", _clientBean.getLocale ())).append ("</a>");
			    	buffer.append ("<ul class='subnav'>");
			    	buffer.append ("<li><a href='retail_product.htm' class='icon list'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "retail_product", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='retail_category.htm' class='icon list'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "retailCategory", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='retail_productgroup.htm' class='icon list'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "retailproductgroup", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("</ul>");
		    	buffer.append ("</li>");
	    	
		    	buffer.append ("<li class='topnav'>");
			    	buffer.append ("<a href='#' class='settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "admin", _clientBean.getLocale ())).append ("</a>");
			    	buffer.append ("<ul class='subnav'>");
			    	buffer.append ("<li><a href='shopprofile.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.shop.profile", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='websiteprofile.html' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.website.profile", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='adminuser.html' class='icon users'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.admin.user", _clientBean.getLocale ())).append ("</a>").append ("</li>");
			    	buffer.append ("<li><a href='autonumber.html' class='icon settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.gennumber", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='voucher.htm' class='icon settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.voucher", _clientBean.getLocale ())).append ("</a></li>");
			    	
			    	buffer.append ("<li><a href='shipmentdomestic.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.shipmentdomestic", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='shipmentinternational.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.shipmentinternational", _clientBean.getLocale ())).append ("</a></li>");
			    	if("fws".equals(_shopInfoBean.getShopName())){
				    	buffer.append ("<li><a href='shop.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.shop", _clientBean.getLocale ())).append ("</a></li>");		    				    			    	
			    		buffer.append ("<li><a href='website.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.website", _clientBean.getLocale ())).append ("</a></li>");
			    		buffer.append ("<li><a href='module-admin.htm'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.moduleadmin", _clientBean.getLocale ())).append ("</a></li>");
			    	}
			    	buffer.append ("</ul>");
		    	buffer.append ("</li>");	
		    	
		    	
		    	buffer.append ("<li class='topnav'>");
		    	buffer.append ("<a href='#' class='settings'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "web", _clientBean.getLocale ())).append ("</a>");
		    	buffer.append ("<ul class='subnav'>");
			    	
			    	buffer.append ("<li><a href='pagecontent.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.pagecontent", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='gallery.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.gallery", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='member.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(), MODULE_NAME, "retailmember", _clientBean.getLocale ())).append ("</a></li>");
			    	
			    	buffer.append ("<li><a href='paymentmethod.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.paymentmethod", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("<li><a href='emailtemplate.htm' class='icon house2'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "item.emailtemplate", _clientBean.getLocale ())).append ("</a></li>");
			    	buffer.append ("</ul>");
			    	buffer.append ("</li>");			    	
		    	buffer.append ("</ul>");
		    	buffer.append ("</li>");	
		    	
		    	buffer.append ("</ul>");	    		
//	    	}
	    }catch(Exception e){
	    	logger.error (e, e);
	    }finally{
	    	
	    }
	    return buffer;		
	}
	
	
	private StringBuffer buildTopMenu(){
		HttpSession session = _req.getSession (false);
		StringBuffer buffer = new StringBuffer();
		buffer.append ("<a href='default.html'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "home", _clientBean.getLocale ())).append ("</a>");										
//		buffer.append ("<a href='contact.html'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "contact.us", _clientBean.getLocale ())).append ("</a> &nbsp; | &nbsp;");
//		buffer.append ("<a href='guide.html'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "guide", _clientBean.getLocale ())).append ("</a>");
		
    	HashMap authMap = null;	    	
		if(_shopInfoBean.getCompanyGroup()!=null){
			authMap = (HashMap) session.getAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getCompanyGroup());
		}else{
			authMap = (HashMap) session.getAttribute (SessionConst.SESSION_ADMIN+_shopInfoBean.getShopName());
		}
    	if(authMap!=null){
    		buffer.append (" &nbsp; | &nbsp;");
			buffer.append ("<a href='login.htm?").append (ACTION_NAME).append ("=").append(GeneralConst.SIGNOFF).append("'>").append (ResourceUtil.getAdminResourceValue(null, MODULE_NAME, "sign.out", _clientBean.getLocale ())).append ("</a> &nbsp;");    		
    	}		
		return buffer;
	}
}