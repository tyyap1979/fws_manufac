package com.forest.common.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.CurrencyBeanDef;

public class NumberUtil {
	private static Logger logger = Logger.getLogger (NumberUtil.class);
	public static void main(String[] args) throws Exception{		
		System.out.println("109.00 = "+NumberUtil.convert("1092.45"));
	}
	
	public static String formatCurrency(String pStr){
		DecimalFormat df = new DecimalFormat("#,##0.00");
		
		if(CommonUtil.isEmpty(pStr)){
			return "0.00";
		}else{
			return df.format(Double.parseDouble(pStr));
		}
		
	}
	
	final private  static String[] units = {"Zero","One","Two","Three","Four",
		"Five","Six","Seven","Eight","Nine","Ten",
		"Eleven","Twelve","Thirteen","Fourteen","Fifteen",
		"Sixteen","Seventeen","Eighteen","Nineteen"};
	final private static String[] tens = {"","","Twenty","Thirty","Forty","Fifty",
		"Sixty","Seventy","Eighty","Ninety"};

	public static String convert(String value){
		logger.debug("convert = "+value);
		int v = 0;
		int d = 0;
		String word = "";
		try{
			if(value.indexOf(".")!=1){
				// Got Decimal Point
				v = Integer.parseInt(value.substring(0, value.indexOf(".")));
				d = Integer.parseInt(value.substring(value.indexOf(".")+1));
				
				word = convert(v);
				if(d>0){
					word += " AND CENTS " + convert(d);
				}
			}else{
				v = Integer.parseInt(value);
				word = convert(v);
			}
			
			word += " ONLY";
			logger.debug(value + " = "+word.toUpperCase());
		}catch(Exception e){
			logger.error(e, e);
		}
		return word.toUpperCase();
	}
	
	private static String convert(int i) {
		if( i < 20)  return units[i];
		if( i < 100) return tens[i/10] + ((i % 10 > 0)? " " + convert(i % 10):"");
		if( i < 1000) return units[i/100] + " Hundred" + ((i % 100 > 0)?" " + convert(i % 100):"");
		if( i < 1000000) return convert(i / 1000) + " Thousand" + ((i % 1000 > 0)? " " + convert(i % 1000):"") ;
		return convert(i / 1000000) + " Million" + ((i % 1000000 > 0)? " " + convert(i % 1000000):"") ;
	}
	
	public static double parseDouble(String num){
		if(CommonUtil.isEmpty(num)){
			return 0;
		}else{
			return Double.parseDouble(num);
		}
	}
	
	public static double parseInt(String num){
		if(CommonUtil.isEmpty(num)){
			return 0;
		}else{
			return Integer.parseInt(num);
		}
	}
	
	public static BigDecimal convertCurrency(Connection dbConnection, ShopInfoBean shopInfoBean, String fromISOCurrency, String toISOCurrency, String fromAmount)throws OwnException{
		BigDecimal amount = null;
		BigDecimal fromRate = null;
		BigDecimal toRate = null;
		
		BigDecimal convertedAmount = null;
		
		HashMap currencyMap = SelectBuilder.getHASH_CURRENCY(dbConnection, shopInfoBean);		
		String[] fromCurrency = (String[]) currencyMap.get(fromISOCurrency);
		String[] toCurrency = (String[]) currencyMap.get(toISOCurrency);
		
		try{
			fromAmount = (CommonUtil.isEmpty (fromAmount))?"0":fromAmount; 
			amount 		= new BigDecimal(fromAmount);
			if(fromISOCurrency.equals (toISOCurrency)){
				return amount;
			}

			fromRate 	= new BigDecimal((String) fromCurrency[1]);
			toRate 		= new BigDecimal((String) toCurrency[1]);
			
//			logger.debug (amount + "*" + fromRate + "/" + toRate);
			convertedAmount = amount.multiply (fromRate).divide (toRate, BigDecimal.ROUND_HALF_UP);		
		}catch(Exception e){			
			logger.error(e, e);
		}
		
		return convertedAmount.setScale (2, BigDecimal.ROUND_HALF_UP);
	}
	
	public static String getCurrencySymbol(Connection dbConnection, ShopInfoBean shopInfoBean, String currencyCode){		
		String symbol = null;
		HashMap currencyMap = SelectBuilder.getHASH_CURRENCY(dbConnection, shopInfoBean);
		String[] currencyArray = (String[]) currencyMap.get(currencyCode);
		symbol = currencyArray[3];
		return (CommonUtil.isEmpty(symbol)?"$":symbol);
	}
	
	public static BigDecimal getCurrencyRate(Connection dbConnection, ShopInfoBean shopInfoBean, String fromISOCurrency, String toISOCurrency){		
		BigDecimal fromRate = null;
		BigDecimal toRate = null;		
		BigDecimal currencyRate = null;
		
		HashMap currencyMap = SelectBuilder.getHASH_CURRENCY(dbConnection, shopInfoBean);		
		String[] fromCurrency = (String[]) currencyMap.get(fromISOCurrency);
		String[] toCurrency = (String[]) currencyMap.get(toISOCurrency);
		
		try{						
			fromRate 	= new BigDecimal((String) fromCurrency[1]);
			toRate 		= new BigDecimal((String) toCurrency[1]);
//			logger.debug("fromISOCurrency["+(String) fromCurrency[1]+"]: "+fromISOCurrency+ ", toISOCurrency["+(String) toCurrency[1]+"]: "+toISOCurrency);
//			logger.debug (amount + "*" + fromRate + "/" + toRate);
			currencyRate = fromRate.divide (toRate, BigDecimal.ROUND_HALF_UP);		
		}catch(Exception e){
			logger.debug("currencyMap: "+currencyMap);
			logger.debug("fromISOCurrency: "+fromISOCurrency+ ", toISOCurrency: "+toISOCurrency);
			logger.error(e, e);
		}
		
		return currencyRate.setScale (5, BigDecimal.ROUND_HALF_UP);
	}
}
