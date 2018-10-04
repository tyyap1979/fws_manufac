package com.forest.common.builder;

import java.util.HashMap;
import com.forest.common.constants.BusinessConst;
import com.forest.mfg.webbuilder.MFG_ModuleConfig;
import com.forest.retail.webbuilder.RETAIL_ModuleConfig;
import com.forest.share.webbuilder.Common_ModuleConfig;

public class WebSiteModuleConfig {	
	public static HashMap getPageToClass(String business, String moduleID) {
		Object pageToClass = null; 
		if(BusinessConst.MANUFACTURING.equals(business)){
			pageToClass = MFG_ModuleConfig.pageToClass.get(moduleID);
		}else if(BusinessConst.RETAIL.equals(business)){
			pageToClass = RETAIL_ModuleConfig.pageToClass.get(moduleID);
		}else{
			pageToClass = Common_ModuleConfig.pageToClass.get(moduleID);
		}

		return (HashMap) pageToClass;
	}
	
	public static HashMap getModuleToClass(String business, String moduleID) {
		if(BusinessConst.MANUFACTURING.equals(business)){
			return (HashMap)MFG_ModuleConfig.moduleToClass.get(moduleID);
		}else if(BusinessConst.RETAIL.equals(business)){		
			return (HashMap)RETAIL_ModuleConfig.moduleToClass.get(moduleID);
		}else{
			return (HashMap)Common_ModuleConfig.moduleToClass.get(moduleID);
		}
	}
	
	
	
}
