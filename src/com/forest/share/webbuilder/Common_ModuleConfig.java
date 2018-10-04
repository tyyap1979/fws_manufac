package com.forest.share.webbuilder;

import java.util.HashMap;
import java.util.Map;

import com.forest.common.constants.GeneralConst;

public class Common_ModuleConfig {
	public static HashMap moduleToClass = new HashMap();
	public static HashMap pageToClass = new HashMap();
	public static HashMap pagePrefixWebBuilder = new HashMap();
	
	static{
		System.out.println("Load Common_ModuleConfig");
		Map data = null;
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		WebInfoWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("web.info", data);								
				
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
		data.put(GeneralConst.HANDLE_CLASS, 		ShopWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("shop.info", data);	
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		PageContentWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);	
		moduleToClass.put("page.list", data);	
		moduleToClass.put("page.content", data);
		moduleToClass.put("html.page.content", data);	
	}
}
