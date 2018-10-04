package com.forest.common.util;

import java.util.Random;


public class CommonUtil {
	public static boolean isEmpty(String pStr){
		return (pStr==null || pStr.trim ().length () == 0);
	}
	
	public static String nullToEmpty(String pStr){
		if(pStr==null) pStr = "";
		return pStr;
	}
	
	public static int parseInt(String pStr){
		if(isEmpty(pStr)){
			return 0;
		}else{
			return Integer.parseInt(pStr);
		}
		
	}
	public static boolean isInteger( String input )  {  
	   try  
	   {  
	      Integer.parseInt( input );  
	      return true;  
	   }  
	   catch( Exception e)  
	   {  
	      return false;  
	   }  
	} 
	
	public static double parseDouble(String pStr){
		if(isEmpty(pStr)){
			return 0;
		}else{
			return Double.parseDouble(pStr);
		}
		
	}
	
	public static StringBuffer fixlength(String value, String emptyValue, int totalLength, String align){
		StringBuffer mNewValue = new StringBuffer();
		value = (value==null)?"":value;
		
		
		if(value.length ()>totalLength){
			mNewValue = new StringBuffer(value.substring (0, totalLength));
		}else{
			mNewValue = new StringBuffer(value);
		}
		if(align.equals ("R")){
			for(int i=value.length (); i<totalLength; i++){
				mNewValue.append (emptyValue);
			}
		}else{
			for(int i=value.length (); i<totalLength; i++){
				mNewValue.insert (0, emptyValue);
			}
		}
		
				
		return mNewValue;
	}
	
	public static String replaceSpaceWithPlus(String str){
		String result = null;
		if(!isEmpty(str))
			result = str.replaceAll(" ", "+");
		return result;
	}
	
	public static void replaceBuffer(StringBuffer content, String replaceWhat, String replaceWith){
		int start = content.indexOf(replaceWhat);
		int end = start + replaceWhat.length();
		content.replace(start, end, replaceWith);
	}
	
	public static String randomString(int length){
		Random r = new Random();

	    String alphabet = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    StringBuffer randomBuffer = new StringBuffer();
	    for (int i = 0; i < length; i++) {
	    	randomBuffer.append(alphabet.charAt(r.nextInt(alphabet.length())));		        
	    } 
	    return randomBuffer.toString();
	}
}
