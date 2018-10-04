package com.forest.admin.builder;


import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;


import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.AdminBuilder;
import com.forest.common.util.OwnException;

public class UploaderBuilder extends AdminBuilder
{		
//	private Logger logger = Logger.getLogger (this.getClass ());
//	private final String MODULE_NAME = "uploader";
	
	public UploaderBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, String resources) {
		super (mShopInfoBean, mClientBean, conn, resources);		
	} 

//	public static UploaderBuilder getInstance(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn) {
//		
////		if (_instance==null) {
////			synchronized (AdminUserBuilder.class) {
////				if (_instance==null) {
////					_instance = new AdminUserBuilder();					
////				}
////			}
////		} 
////		return _instance;
//		
//		return new UploaderBuilder(mShopInfoBean, mClientBean, conn);
//	}		
	
	public void displayHandler(HttpServletRequest req, StringBuffer mBuffer)throws OwnException{	
		int fromIndex = 0;
		if((fromIndex = mBuffer.indexOf ("system.admin.uploader", 0)) != -1){		
			mBuffer.replace(fromIndex, "system.admin.uploader".length () + fromIndex, buildContent().toString ());
		}		
	}
	
	public static StringBuffer buildContent() throws OwnException{
		StringBuffer mBuffer = new StringBuffer();
		
		mBuffer.append ("<div id='content'>");				
			mBuffer.append ("<div id='divSWFUploadUI'>");
				mBuffer.append ("<div class='fieldset flash' id='fsUploadProgress'>");
					mBuffer.append ("<span class='legend'>Upload Queue</span>");
				mBuffer.append ("</div>");
			
				mBuffer.append ("<p id='divStatus'>0 Files Uploaded</p>");
				mBuffer.append ("<p>");
					mBuffer.append ("<span id='mySpanButtonPlaceholder'>");
						mBuffer.append ("<span id='spanButtonPlaceholder'></span>");
					mBuffer.append ("</span>");			
					mBuffer.append ("<input id='btnCancel' type='button' value='Cancel All Uploads' disabled='disabled' style='margin-left: 63px; height: 22px; font-size: 8pt;' />");
				mBuffer.append ("</p>");
				mBuffer.append ("<br style='clear: both;' />");
			mBuffer.append ("</div>");
		
			mBuffer.append ("<noscript style='background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px;'>");
				mBuffer.append ("We're sorry.  SWFUpload could not load.  You must have JavaScript enabled to enjoy SWFUpload.");
			mBuffer.append ("</noscript>");
			mBuffer.append ("<div id='divLoadingContent' class='content' style='background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;'>");
				mBuffer.append ("SWFUpload is loading. Please wait a moment...");
			mBuffer.append ("</div>");
			mBuffer.append ("<div id='divLongLoading' class='content' style='background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;'>");
				mBuffer.append ("SWFUpload is taking a long time to load or the load has failed.  Please make sure that the Flash Plugin is enabled and that a working version of the Adobe Flash Player is installed.");
			mBuffer.append ("</div>");
			mBuffer.append ("<div id='divAlternateContent' class='content' style='background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;'>");
				mBuffer.append ("We're sorry.  SWFUpload could not load.  You may need to install or upgrade Flash Player.");
				mBuffer.append ("Visit the <a href='http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash'>Adobe website</a> to get the Flash Player.");
			mBuffer.append ("</div>");
		mBuffer.append ("</div>");
		
		return mBuffer;
	}
	
}
