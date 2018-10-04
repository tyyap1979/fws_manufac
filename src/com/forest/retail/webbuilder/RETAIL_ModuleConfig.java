package com.forest.retail.webbuilder;

import java.util.HashMap;
import java.util.Map;

import com.forest.common.constants.GeneralConst;
import com.forest.mfg.webbuilder.MFG_OrderWebBuilder;
import com.forest.mfg.webbuilder.MFG_PaymentWebBuilder;
import com.forest.share.webbuilder.Common_ModuleConfig;
import com.forest.share.webbuilder.LoginWebBuilder;
import com.forest.share.webbuilder.PageContentWebBuilder;
import com.forest.share.webbuilder.PaymentMethodWebBuilder;
import com.forest.share.webbuilder.RegisterWebBuilder;
import com.forest.share.webbuilder.ShopWebBuilder;
import com.forest.share.webbuilder.UserProfileWebBuilder;
import com.forest.share.webbuilder.WebInfoWebBuilder;

public class RETAIL_ModuleConfig{
	public static HashMap moduleToClass = new HashMap();
	public static HashMap pageToClass = new HashMap();
	public static HashMap pagePrefixWebBuilder = new HashMap();
	
	static{
		System.out.println("Load RETAIL_ModuleConfig");
		Map data = null;
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 	RETAIL_ProductWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("product.list", data);
		moduleToClass.put("product.detail", data);
		pageToClass.put("product.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		RETAIL_CategoryWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("category.list", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		RETAIL_CartWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("cart.list", data);
		pageToClass.put("cart.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		RETAIL_RegisterWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("register.detail", data);
		moduleToClass.put("member.detail", data);
		pageToClass.put("register.htm", data);
		pageToClass.put("userprofile.htm", data);
		pageToClass.put("changepassword.htm", data);

		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		RETAIL_LoginWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		pageToClass.put("sign-in.htm", data);
		pageToClass.put("forgotpassword.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		PaymentMethodWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		moduleToClass.put("payment.method", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		RETAIL_PaymentWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);				
		moduleToClass.put("payment.detail", data);
		moduleToClass.put("payment.options", data);
		pageToClass.put("payment.htm", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		RETAIL_MenuWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("user.menu", data);
		
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		RETAIL_OrderWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("order.list", data);
		moduleToClass.put("order.detail", data);
		pageToClass.put("order.htm", data);
		pageToClass.put("orderdetail.htm", data);
		pageToClass.put("invoice.htm", data);
		
		// Common Module
		data = new HashMap();
		data.put(GeneralConst.HANDLE_CLASS, 		WebInfoWebBuilder.class);		
		data.put(GeneralConst.BY_COMPANY,		GeneralConst.COMPANY_SELECTED);		
		moduleToClass.put("web.info", data);
		
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
