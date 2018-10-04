package com.forest.common.util;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class MathUtil {
	private static Logger logger = Logger.getLogger(MathUtil.class);
	public static double convertMeasurement(String fromUnit, String toUnit, double value){									
		// 2in = ?mm
		double calValue = 0;
		if(toUnit.equals("in")){				
			if(fromUnit.equals("mm")){					
				calValue = value / 25.4;
			}
		}else if(toUnit.equals("ft")){
			if(fromUnit.equals("mm")){
				calValue = (value / 304.8);					
			}else if(fromUnit.equals("in")){
				calValue = (value / 12);
			}
		}else if(toUnit.equals("mm")){	
			if(fromUnit.equals("in")){
				calValue = (value * 25.4);					
			}
		}				
		calValue =  new BigDecimal(calValue).setScale(2, BigDecimal.ROUND_UP).doubleValue();
		logger.debug("fromUnit="+fromUnit+", "+"toUnit="+toUnit + ", value="+value+", calValue=" + calValue);
		return calValue;
	}

}
