package com.forest.common.util;


import org.apache.log4j.Logger;

public class FormatUtil {
	private static Logger logger = Logger.getLogger (FormatUtil.class);
	public static void main(String[] args) {		
		FormatUtil.formatInvoiveDescription("1000mm x 2000mm", "{w} W x {h} H");
	}
	public static String formatInvoiveDescription(String measurement, String format){
		String returnValue = "";
		String[] measureArray = null;
		logger.debug("measurement: "+measurement+", format:"+format);
		try{
		measureArray = measurement.split("x");
		returnValue = format.replaceAll("\\{w\\}", measureArray[0].trim()).replaceAll("\\{h\\}", measureArray[1].trim());
		}catch(Exception e){
			logger.error(e, e);
		}
		logger.debug("returnValue = "+returnValue);
		return returnValue;
	}
}
