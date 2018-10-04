package com.forest.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateUtil {
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd";
	private static Logger logger = Logger.getLogger (DateUtil.class);
	public static void main(String[] args) {		
		System.out.println("MONTH = "+DateUtil.formatDate("2013-03-01", DATE_FORMAT_NOW, "dd/MM/yyyy"));
		
	}
	public static String getLastDateOfMonth() {
	    Calendar cal = Calendar.getInstance();
	    int month = cal.get(Calendar.MONTH);
	    cal.set(Calendar.MONTH, month-1);
	    int day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    cal.set(Calendar.DAY_OF_MONTH, day);
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);	    
	    return sdf.format(cal.getTime());
	}
	
	public static String getCurrentDate() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);	    
	    return sdf.format(cal.getTime());
	}

	public static String displayDateTime(String timestampDiff, com.forest.common.bean.ClientBean clientBean){
		String display = "";
	    int diff = 0;
	    
	    if(!CommonUtil.isEmpty (timestampDiff)){
	    	diff = Integer.parseInt (timestampDiff);
	    	
	    	if(diff<60){
	    		display = diff + " " + ResourceUtil.getAdminResourceValue (null, null, "i18n.system.second", clientBean.getLocale ());
	    	}else if(diff<3600){
	    		display = (diff/60) + " " +  ResourceUtil.getAdminResourceValue (null, null, "i18n.system.minute", clientBean.getLocale ());
	    	}else if(diff<86400){
	    		display = (diff/3600) + " " +  ResourceUtil.getAdminResourceValue (null, null, "i18n.system.hour", clientBean.getLocale ());
	    	}else if(diff>86400 && diff<5184000){
	    		display = (diff/86400) + " " +  ResourceUtil.getAdminResourceValue (null, null, "i18n.system.day", clientBean.getLocale ());
	    	}else if(diff>5184000 && diff<62208000){
	    		display = (diff/5184000) + " " +  ResourceUtil.getAdminResourceValue (null, null, "i18n.system.month", clientBean.getLocale ());
	    	}else if(diff>62208000){
	    		display = (diff/62208000) + " " +  ResourceUtil.getAdminResourceValue (null, null, "i18n.system.year", clientBean.getLocale ());
	    	}
	    }
	    return display;
	}
	
	public static String getCurrentYear(){
		return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	}
	
	public static String getCurrentMonth(){
		return String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
	}
	
	public static String getPreviousMonth(){
		return String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
	}
	
	public static String getMonthInWord(int month){		
		if(month<=0) month += 12;		
		String[] monthWord = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		return monthWord[month];
	}
	
	public static long daysBetween(Calendar startDate, Calendar endDate) {
		Calendar date = (Calendar) startDate.clone();
		long daysBetween = 0;
		while (date.before(endDate)) {
			date.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}
	
	public static String formatDate(String date, String fromFormat, String toFormat){
		String newDate = "";
		Date formatDate = null;
		SimpleDateFormat sdf = null;
		if(CommonUtil.isEmpty(date)){
			return null;
		}else{
			try{			
				sdf = new SimpleDateFormat(toFormat);	 
				formatDate = new SimpleDateFormat(fromFormat).parse(date);
				newDate = sdf.format(formatDate);
			}catch (Exception e) {
				newDate = null;
			}
			return newDate;
		}		
	}
	
	public static Date getDate(String date){
		Date formatDate = null;
		try{
			formatDate = new SimpleDateFormat(DATE_FORMAT_NOW).parse(date);
		}catch(Exception e){
			return null;
		}
		return formatDate;
	}
	
}
