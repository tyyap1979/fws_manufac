package com.forest.common.builder;

import java.util.HashMap;
import java.util.Map;

import com.forest.admin.builder.MenuBuilder;
import com.forest.common.beandef.AdminUserBeanDef;
import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.beandef.AutoNumberDef;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.CountryBeanDef;
import com.forest.common.beandef.CurrencyBeanDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.beandef.ModuleAdminDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.SupplierProfileDef;
import com.forest.common.beandef.VoucherDef;
import com.forest.common.beandef.WebsiteDef;
import com.forest.common.constants.BusinessConst;
import com.forest.id.adminbuilder.ID_TransactionWebBuilder;
import com.forest.id.beandef.ID_TransactionDef;
import com.forest.mfg.adminbuilder.CustomerProfileAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_CreditNoteAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_CustProductAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_DebitNoteAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_JobSheetAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_POAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_PaymentVoucherAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_ProductOptionAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_ReceivePaymentAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_ReportAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_StatementAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_SupplierInvoiceAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_WithPageAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_TransactionAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_XMLAdminBuilder;
import com.forest.mfg.adminbuilder.SmsAdminBuilder;
import com.forest.mfg.beandef.MFG_CartDef;
import com.forest.mfg.beandef.MFG_CreditNoteDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_DebitNoteDef;
import com.forest.mfg.beandef.MFG_Grouping;
import com.forest.mfg.beandef.MFG_JobSheetDef;
import com.forest.mfg.beandef.MFG_PODef;
import com.forest.mfg.beandef.MFG_PaymentVoucherDef;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_RawDef;
import com.forest.mfg.beandef.MFG_RawToProductDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_StatementDetailDef;
import com.forest.mfg.beandef.MFG_SupplierInvoiceDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.SMSDef;
import com.forest.retail.adminbuilder.RETAIL_CartAdminBuilder;
import com.forest.retail.adminbuilder.RETAIL_CategoryAdminBuilder;
import com.forest.retail.adminbuilder.RETAIL_ProductAdminBuilder;
import com.forest.retail.adminbuilder.RETAIL_ProductGroupAdminBuilder;
import com.forest.retail.adminbuilder.RETAIL_TransactionAdminBuilder;
import com.forest.retail.beandef.RETAIL_CartDef;
import com.forest.retail.beandef.RETAIL_CategoryDef;
import com.forest.retail.beandef.RETAIL_MemberDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.retail.beandef.RETAIL_ProductGroupDef;
import com.forest.retail.beandef.RETAIL_TransactionDef;
import com.forest.share.adminbuilder.ClientUserAdminBuilder;
import com.forest.share.adminbuilder.CommonAdminBuilder;
import com.forest.share.adminbuilder.EmailTemplateAdminBuilder;
import com.forest.share.adminbuilder.EmptyAdminBuilder;
import com.forest.share.adminbuilder.FriendlyLinkAdminBuilder;
import com.forest.share.adminbuilder.GalleryAdminBuilder;
import com.forest.share.adminbuilder.MaterialAdminBuilder;
import com.forest.share.adminbuilder.PageContentAdminBuilder;
import com.forest.share.adminbuilder.ShipmentDomesticAdminBuilder;
import com.forest.share.adminbuilder.UploadAdminBuilder;
import com.forest.share.beandef.EmailTemplateDef;
import com.forest.share.beandef.FriendlyLinkDef;
import com.forest.share.beandef.GalleryDef;
import com.forest.share.beandef.MaterialDef;
import com.forest.share.beandef.PageContentDef;
import com.forest.share.beandef.PaymentMethodDef;
import com.forest.share.beandef.ShipmentDomesticDef;
import com.forest.share.beandef.ShipmentInternationalDef;
import com.forest.share.beandef.SmsTemplateDef;

public class ModuleConfig {	
	public static HashMap filenameToClass = new HashMap();
//	public static HashMap filenameAccessRight = new HashMap();	
//	public static HashMap filenameWebBuilder = new HashMap();
	public static HashMap pagePrefixWebBuilder = new HashMap();
	public static HashMap commonFile = new HashMap();
	
	public static final String HANDLE_CLASS = "class";
	public static final String BY_COMPANY = "company";
	public static final String BY_USER_RIGHT = "userright";
	public static final String HANDLE_BUILDER = "builder";
	public static final String RESOURCES = "resource";
	
	public static final String COMPANY_MASTER 	= "0";
	public static final String COMPANY_SELECTED	= "1";
	public static final String COMPANY_NONE 	= "2";
	
	public static final String USER_RIGHT_NO 	= "N";
	public static final String USER_RIGHT_YES 	= "Y";
	static{		
		commonFile.put("error.htm", "");
		commonFile.put("forgotpassword.htm", "");
		commonFile.put("global.htm", "");
		commonFile.put("login.htm", "");
		commonFile.put("upload.htm", "");		
		
		Map data = null;
//		data = new HashMap();
//		data.put(HANDLE_CLASS, 		null);
//		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
//		data.put(BY_COMPANY, COMPANY_NONE);
//		filenameToClass.put("default.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		EmailTemplateDef.class);
		data.put(HANDLE_BUILDER, 	EmailTemplateAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);				
		filenameToClass.put("emailtemplate.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		SmsTemplateDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);				
		filenameToClass.put("smstemplate.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		GalleryDef.class);
		data.put(HANDLE_BUILDER, 	GalleryAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);				
		filenameToClass.put("gallery.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MaterialDef.class);
		data.put(HANDLE_BUILDER, 	MaterialAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);				
		filenameToClass.put("material.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		PageContentDef.class);
		data.put(HANDLE_BUILDER, 	PageContentAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);				
		filenameToClass.put("pagecontent.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		FriendlyLinkDef.class);
		data.put(HANDLE_BUILDER, 	FriendlyLinkAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);				
		filenameToClass.put("friendlylink.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		BaseDef.class);
		data.put(HANDLE_BUILDER, 	UploadAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);				
		filenameToClass.put("upload.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		AutoNumberDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);		
		filenameToClass.put("autonumber.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		CountryBeanDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY, COMPANY_NONE);
		filenameToClass.put("country.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		CurrencyBeanDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY, COMPANY_NONE);
		filenameToClass.put("currency.htm", data);
				
		data = new HashMap();
		data.put(HANDLE_CLASS, 		ShopBeanDef.class);
		data.put(HANDLE_BUILDER, 	ShopAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_NONE);
		filenameToClass.put("shop.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		ShopBeanDef.class);
		data.put(HANDLE_BUILDER, 	ShopAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		filenameToClass.put("shopprofile.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		WebsiteDef.class);
		data.put(HANDLE_BUILDER, 	WebSiteAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_NONE);
		filenameToClass.put("website.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		WebsiteDef.class);
		data.put(HANDLE_BUILDER, 	WebSiteAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		filenameToClass.put("websiteprofile.htm", data);
//		data = new HashMap();
//		data.put(HANDLE_CLASS, 		ShopSubDef.class);
//		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
//		data.put(BY_COMPANY,		COMPANY_MASTER);
//		filenameToClass.put("shopsub.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		AdminUserBeanDef.class);
		data.put(HANDLE_BUILDER, 	SelectWebBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		filenameToClass.put("select.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		AdminUserBeanDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_MASTER);
		filenameToClass.put("adminuser.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		AgentProfileDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		filenameToClass.put("agentprofile.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		ClientUserBeanDef.class);
		data.put(HANDLE_BUILDER, 	ClientUserAdminBuilder.class);
		data.put(BY_COMPANY, COMPANY_SELECTED);		
		filenameToClass.put("clientuser.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		CustomerProfileDef.class);
		data.put(HANDLE_BUILDER, 	CustomerProfileAdminBuilder.class);
		data.put(BY_USER_RIGHT,		USER_RIGHT_YES);
		data.put(BY_COMPANY, COMPANY_SELECTED);		
		filenameToClass.put("customer.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		SupplierProfileDef.class);
		data.put(HANDLE_BUILDER, 	SupplierProfileAdminBuilder.class);
		data.put(BY_COMPANY, COMPANY_SELECTED);		
		filenameToClass.put("supplier.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		AdminUserBeanDef.class);
		data.put(HANDLE_BUILDER, 	LoginAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		filenameToClass.put("login.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		AdminUserBeanDef.class);
		data.put(HANDLE_BUILDER, 	LoginAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		filenameToClass.put("logout.htm", data);

		data = new HashMap();
		data.put(HANDLE_CLASS, 		AdminUserBeanDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		filenameToClass.put("forgotpassword.htm", data);		
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		ModuleAdminDef.class);
		data.put(HANDLE_BUILDER, 	ModuleAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_NONE);
		filenameToClass.put("module-admin.htm", data);
		
		// Manufacturing
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_ProductOptionDef.class);
		data.put(HANDLE_BUILDER, 	MFG_ProductOptionAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_prodoption.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_CustProductDef.class);
		data.put(HANDLE_BUILDER, 	MFG_CustProductAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_custproduct.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_SupplierProductDef.class);
		data.put(HANDLE_BUILDER, 	MFG_WithPageAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_supplierproduct.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_SupplierProductOptionDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_supplierprodoption.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_TransactionDef.class);
		data.put(HANDLE_BUILDER, 	MFG_TransactionAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(BY_USER_RIGHT,		USER_RIGHT_YES);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_transaction.htm", data);
		
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_CartDef.class);
		data.put(HANDLE_BUILDER, 	MFG_TransactionAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_cart.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_RawDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_raw.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_RawToProductDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_rawtoproduct.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_JobSheetDef.class);
		data.put(HANDLE_BUILDER, 	MFG_JobSheetAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_jobsheet.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		null);
		data.put(HANDLE_BUILDER, 	MFG_XMLAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		filenameToClass.put("chart.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		CustomerProfileDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);		
		filenameToClass.put("autosuggest.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_Grouping.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);		
		filenameToClass.put("mfg_grouping.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_PODef.class);
		data.put(HANDLE_BUILDER, 	MFG_POAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(BY_USER_RIGHT,		USER_RIGHT_YES);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);		
		filenameToClass.put("mfg_po.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_StatementDef.class);
		data.put(HANDLE_BUILDER, 	MFG_StatementAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);		
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);		
		filenameToClass.put("mfg_statement.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_StatementDetailDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);		
		filenameToClass.put("mfg_statementdetail.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_ReceivePaymentDef.class);
		data.put(HANDLE_BUILDER, 	MFG_ReceivePaymentAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);		
		filenameToClass.put("mfg_receivepayment.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_SupplierInvoiceDef.class);
		data.put(HANDLE_BUILDER, 	MFG_SupplierInvoiceAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_supplierinvoice.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_PaymentVoucherDef.class);
		data.put(HANDLE_BUILDER, 	MFG_PaymentVoucherAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);
		filenameToClass.put("mfg_paymentvoucher.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_CreditNoteDef.class);
		data.put(HANDLE_BUILDER, 	MFG_CreditNoteAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);		
		filenameToClass.put("mfg_creditnote.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		MFG_DebitNoteDef.class);
		data.put(HANDLE_BUILDER, 	MFG_DebitNoteAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);		
		filenameToClass.put("mfg_debitnote.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		BaseDef.class);
		data.put(HANDLE_BUILDER, 	MFG_ReportAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.MANUFACTURING);		
		filenameToClass.put("mfg_report.htm", data);
		filenameToClass.put("mfg_report_custom.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		ShipmentDomesticDef.class);
		data.put(HANDLE_BUILDER, 	ShipmentDomesticAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);			
		filenameToClass.put("shipmentdomestic.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		ShipmentInternationalDef.class);
		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);			
		filenameToClass.put("shipmentinternational.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		PaymentMethodDef.class);
		data.put(HANDLE_BUILDER, 	CommonAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);			
		filenameToClass.put("paymentmethod.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		VoucherDef.class);
		data.put(HANDLE_BUILDER, 	CommonAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);			
		filenameToClass.put("voucher.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		SMSDef.class);
		data.put(HANDLE_BUILDER, 	SmsAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);			
		filenameToClass.put("sms.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		BaseDef.class);
		data.put(HANDLE_BUILDER, 	EmptyAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);			
		filenameToClass.put("print.htm", data);
		
		// For ID Only
		data = new HashMap();
		data.put(HANDLE_CLASS, 		ID_TransactionDef.class);
		data.put(HANDLE_BUILDER, 	ID_TransactionWebBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.INTERIOR_DESIGN);		
		filenameToClass.put("dsgn_transaction.htm", data);
		
		// For Retail Only		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		RETAIL_ProductDef.class);
		data.put(HANDLE_BUILDER, 	RETAIL_ProductAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.RETAIL);		
		filenameToClass.put("retail_product.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		RETAIL_CartDef.class);
		data.put(HANDLE_BUILDER, 	RETAIL_CartAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.RETAIL);		
		filenameToClass.put("retail_cart.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		RETAIL_CategoryDef.class);
		data.put(HANDLE_BUILDER, 	RETAIL_CategoryAdminBuilder.class);
		data.put(BY_COMPANY,		COMPANY_SELECTED);
		data.put(RESOURCES, 		BusinessConst.RETAIL);		
		filenameToClass.put("retail_category.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		RETAIL_MemberDef.class);
		data.put(HANDLE_BUILDER, 	CommonAdminBuilder.class);
		data.put(BY_COMPANY, COMPANY_SELECTED);		
		data.put(RESOURCES, 		BusinessConst.RETAIL);		
		filenameToClass.put("member.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		RETAIL_TransactionDef.class);
		data.put(HANDLE_BUILDER, 	RETAIL_TransactionAdminBuilder.class);
		data.put(BY_COMPANY, COMPANY_SELECTED);		
		data.put(RESOURCES, 		BusinessConst.RETAIL);		
		filenameToClass.put("retail_order.htm", data);
		
		data = new HashMap();
		data.put(HANDLE_CLASS, 		RETAIL_ProductGroupDef.class);
		data.put(HANDLE_BUILDER, 	RETAIL_ProductGroupAdminBuilder.class);
		data.put(BY_COMPANY, COMPANY_SELECTED);		
		data.put(RESOURCES, 		BusinessConst.RETAIL);		
		filenameToClass.put("retail_productgroup.htm", data);
		
//		data = new HashMap();
//		data.put(HANDLE_CLASS, 		.class);
//		data.put(HANDLE_BUILDER, 	GenericAdminBuilder.class);
//		data.put(BY_COMPANY,		COMPANY_SELECTED);
//		filenameToClass.put(".htm", data);

		
		pagePrefixWebBuilder.put("system.menu", MenuBuilder.class);
//		pagePrefixWebBuilder.put("system.dash.board", DashBoardBuilder.class);		
	}
}
