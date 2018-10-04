package com.forest.common.constants;

public class ValidationConst
{
	public final static String REQUIRED = "required";
	public final static String MATCH_WITH = "confirm";
	public final static String ONLY_NUMBER = "custom[onlyNumber]";
	public final static String EMAIL 	= "custom[email]";
	public final static String PHONE 	= "custom[telephone]";
	public final static String AJAX_SHOP_NAME 	= "ajax[ajaxShopName]";
	public final static String AJAX_MEMBER 	= "ajax[ajaxMember]";
	public final static String AJAX_FORGOT_PASSWORD 	= "ajax[ajaxForgotPassword]";
	
//	public final static String BACKEND_VALIDATE = "backend";
	
	public static String getValidate(String val1){
		String[] arr = val1.split (",");
		StringBuffer valBuffer = new StringBuffer("validate[");
		for(int i=0;i<arr.length;i++){
			valBuffer.append (arr[i]).append (",");
		}
		if(valBuffer.charAt (valBuffer.length ()-1)==','){
			valBuffer.deleteCharAt (valBuffer.length ()-1);
		}
		valBuffer.append ("]");
		return "validate[" + val1 + "]";
	}	
}
