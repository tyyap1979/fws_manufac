package com.forest.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonPattern {

	public static String getPhoneNumber(String phone){
		Pattern pat = Pattern.compile("\\d+");
		Matcher match = null;
		if(CommonUtil.isEmpty(phone)) return "";
		match = pat.matcher(phone);
		StringBuffer rtnBuffer = new StringBuffer();
		
		while(match.find()){
			rtnBuffer.append(match.group());
    	}
		return rtnBuffer.toString();
	}
}
