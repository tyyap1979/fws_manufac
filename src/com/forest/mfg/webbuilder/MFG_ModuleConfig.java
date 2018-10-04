package com.forest.mfg.webbuilder;

import java.util.HashMap;
import java.util.Map;

import com.forest.common.constants.GeneralConst;
import com.forest.share.webbuilder.Common_ModuleConfig;
import com.forest.share.webbuilder.CurrencyWebBuilder;
import com.forest.share.webbuilder.FriendlyLinkWebBuilder;
import com.forest.share.webbuilder.GalleryWebBuilder;
import com.forest.share.webbuilder.LoginWebBuilder;
import com.forest.share.webbuilder.MaterialWebBuilder;
import com.forest.share.webbuilder.MenuWebBuilder;
import com.forest.share.webbuilder.PageContentWebBuilder;
import com.forest.share.webbuilder.PaymentMethodWebBuilder;
import com.forest.share.webbuilder.RegisterWebBuilder;
import com.forest.share.webbuilder.SendMailWebBuilder;
import com.forest.share.webbuilder.ShopWebBuilder;
import com.forest.share.webbuilder.UserProfileWebBuilder;
import com.forest.share.webbuilder.WebInfoWebBuilder;

public class MFG_ModuleConfig {
	public static HashMap moduleToClass = new HashMap();
	public static HashMap pageToClass = new HashMap();
	public static HashMap pagePrefixWebBuilder = new HashMap();
	
	static {		
		Map data = null;
		// Common 
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		WebInfoWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("web.info", data);		
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		ShopWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("shop.info", data);			
				
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		GalleryWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("gallery.list", data);
		moduleToClass.put("gallery.list.single", data);
		moduleToClass.put("gallery.detail", data);		
		moduleToClass.put("banner.list", data);		
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		MaterialWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("material.list", data);
		moduleToClass.put("material.list.single", data);
		moduleToClass.put("material.detail", data);
		moduleToClass.put("material.tag", data);
		moduleToClass.put("material.tag.title", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		PageContentWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);	
		moduleToClass.put("page.list", data);	
		moduleToClass.put("page.content", data);
		moduleToClass.put("html.page.content", data);	
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		FriendlyLinkWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("friendly.link", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		RegisterWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("register.detail", data);
		pageToClass.put("register.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		UserProfileWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("userprofile.detail", data);
		pageToClass.put("userprofile.htm", data);
		pageToClass.put("changepassword.htm", data);

		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		LoginWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		pageToClass.put("sign-in.htm", data);
		pageToClass.put("forgotpassword.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		PaymentMethodWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		moduleToClass.put("payment.method", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		MenuWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		moduleToClass.put("user.menu", data);		
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		SendMailWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		pageToClass.put("sendmail.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		CurrencyWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		moduleToClass.put("currency.panel", data);		
		pageToClass.put("currency.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		MFG_OrderWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("order.list", data);
		moduleToClass.put("order.detail", data);
		pageToClass.put("order.htm", data);
		pageToClass.put("invoice.htm", data);
		
//		data = new HashMap();
//		data.put(GeneralConst.HANDLE_CLASS, 		MFG_ProductGroupWebBuilder.class);		
//		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
//		moduleToClass.put("category.list", data);	
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		MFG_PaymentWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		moduleToClass.put("payment.detail", data);
		moduleToClass.put("payment.options", data);
		pageToClass.put("payment.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		MFG_ProductWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("product.list", data);
		moduleToClass.put("product.detail", data);
		pageToClass.put("product.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		MFG_CategoryWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("category.list", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		MFG_CartWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("cart.list", data);
		pageToClass.put("cart.htm", data);
	}
	
}
