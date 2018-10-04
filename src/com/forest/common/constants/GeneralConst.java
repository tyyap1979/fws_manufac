package com.forest.common.constants;

public class GeneralConst
{
	public static String DOMAIN 	= ""; // Set At InitServlet
	public static String SUBDOMAIN 	= ""; // Set At InitServlet
	public static final String ADMIN_NAME 	= "admin";
	public static final String CREATE 		= "action.create";
	public static final String DELETE 		= "action.delete";
	public static final String UPDATE 		= "action.update";
	public static final String RETRIEVE 	= "action.retrieve";
	public static final String SEARCH 		= "action.search";
	public static final String REPORT 		= "action.report";
	public static final String REPORT_INV 	= "action.report_inv";
	public static final String REPORT_DO 	= "action.report_do";
	public static final String JOBSHEET 	= "action.js";	
	public static final String AUTO_SUGGEST	= "action.auto.suggest";
	public static final String DYNAMIC_SELECT= "action.dynamic.select";
	public static final String EDIT 		= "action.retrieve.edit";
	public static final String COPY 		= "action.retrieve.copy";
	public static final String VALIDATE 	= "action.validate";
	public static final String SIGNOFF		= "action.sign.off";
	public static final String FORGOT_PASSWORD		= "action.send.passwd";
	public static final String CLEAR_CART 			= "action.clear.cart";
	
	public final static String ACTION_NAME 	= "action1";
	public final static String SESSION_REFERER = "sr";
	
	public final static String CHECKALL 	= "action.check.all";
	public final static String CHECKNONE 	= "action.check.none";		
	public final static String ADD_RECORD 	= "action.addrecord";
	public final static String ADD_DETAIL 	= "action.add.detail";
	public final static String REMOVE_DETAIL= "action.remove.detail";
	public final static String CLONE_ADD 	= "action.clone.add";
	public final static String CLONE_ADD_NEW 	= "action.clone.add.new";
	public final static String CLONE_ADD_CUSTOM 	= "action.clone.add.custom";
	public final static String CLONE_UPLOAD 	= "action.clone.upload";
	public final static String CLONE_UPLOAD_ADD 	= "action.clone.upload.add";
	
	public final static String CLONE_REMOVE = "action.clone.delete";	
	public final static String PAGE_NUM		= "p";	
	public final static String TOTAL_RECORD_PER_PAGE 	= "totalRecordPerPage";	
	
	// Control
	public final static String CONTROL_RETRIEVE_PRODUCT = "action.rtrv.product"; // Use at Customer Profile
	
	public final static String ENTRY_FORM 	= "entryForm";
	public final static String LISTING_FORM = "listingForm";
	public final static String SEARCH_FORM 	= "searchForm";
	
	public final static String NEW_RECORD_CLASS = "editedrecord";	
	
	// Transaction
	public final static String TRANS_PENDING_PAYMENT = "PP";
	public final static String TRANS_PAID = "P";
	public final static String TRANS_PROCESSING = "PR";
	public final static String TRANS_DELIVERING = "D";
	public final static String TRANS_RECEIVED = "R";
	public final static String TRANS_RETURN = "RT";
	
	// Payment Method 
	public static final String PAY_CREDIT_CARD = "CC";
	public static final String PAY_ONLINE_TRANSFER = "OT";
	public static final String PAY_COD = "COD";
		
	// Delivery Method 
	public static final String DEL_POST = "SA";
	public static final String DEL_SELF_COLLECT = "SP";

	// Status 
	public static final String ACTIVE 		= "A";
	public static final String SUSPENDED 	= "S";
	public static final String DELETED 		= "D";
	
	public final static String SEPERATOR = ",";
	public final static String JOINER = "-";
	
	public final static String GET_SHIPMENT = "action.retrieve.shipment";
	public final static String CONFIRM_PAY = "action.confirm.pay";
	
	public final static String DEFAULT_THEME = "cupertino";
	
	
	public static final String HANDLE_CLASS = "class";
	public static final String BY_COMPANY = "company";
	public static final String COMPANY_MASTER 	= "0";
	public static final String COMPANY_SELECTED	= "1";
	public static final String COMPANY_NONE 	= "2";
}
