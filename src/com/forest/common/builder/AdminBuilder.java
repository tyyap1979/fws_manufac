package com.forest.common.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CryptUtil;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.mfg.beandef.MFG_ReceivePaymentDef;
import com.forest.mfg.beandef.MFG_StatementDef;

public class AdminBuilder {
	private Logger logger = Logger.getLogger (AdminBuilder.class);
	
	
	protected ClientBean _clientBean = null;
	protected ShopInfoBean _shopInfoBean = null;
	protected Connection _dbConnection = null;
	
	protected static final String NON_NEGATIVE_INTEGER_FIELD = "(\\d){1,9}";
	protected static final String INTEGER_FIELD = "(-)?" + NON_NEGATIVE_INTEGER_FIELD;
	protected static final String NON_NEGATIVE_FLOATING_POINT_FIELD = "(\\d){1,10}\\.(\\d){1,10}";
	protected static final String FLOATING_POINT_FIELD =  "(-)?" + NON_NEGATIVE_FLOATING_POINT_FIELD;
	protected static final String DATE_FIELD = "\\d{4}-\\d{1,2}-\\d{1,2}";
	protected static final String TIMESTAMP_FIELD = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1}";
	
	public final static String ACTION_NAME 		= "action1";
	
	
	
	public final String SORT_NAME 				= "sortName"; // And Request
	public final String TOTAL_RECORD_PER_PAGE 	= "iDisplayLength";
	public final String RECORD_START_AT 		= "iDisplayStart";	
	public final String SORT_DIRECTION 			= "sortDirection";	
	
	// Response Code
	public final String RETURN_CREATE 		= "0";
	public final String RETURN_UPDATE 		= "1";
	public final String RETURN_DELETE 		= "2";
	public final String RETURN_EDIT			= "3";
	public final String RETURN_SEARCH		= "4";
	public final String RETURN_VALIDATION 	= "9";
	
	
	public final String RECORD_DELETED	= "record.deleted";	
	public final String RESULT_HTML		= "resultHTML";
	public final String RESULT_HTML_APPEND = "resultHTMLAppend";

	public static final String NO_IMAGE = "/components/images/NoPhoto.gif";		
	
	protected String _resources = "";
	
	public AdminBuilder(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn, String resources){
		_clientBean = clientBean;
		_shopInfoBean = shopInfoBean;
		_dbConnection = conn;
		_resources = resources;
	}
	
	public Object getWebBuilder(Class clsBuilder, Map hashPageMap, HttpServletRequest req, HttpServletResponse resp) throws Exception{			    	
		Class[] paramType = new Class[8];
		Object[] paramValue = new Object[8];		
		Constructor cstr = null;		
		
		paramType[0] = ShopInfoBean.class;
		paramType[1] = ClientBean.class;
		paramType[2] = Connection.class;
		paramType[3] = HttpServletRequest.class;
		paramType[4] = HttpServletResponse.class;
		paramType[5] = Class.class;
		paramType[6] = String.class;
		paramType[7] = String.class;
		
		paramValue[0] = _shopInfoBean;
		paramValue[1] = _clientBean;
		paramValue[2] = _dbConnection;
		paramValue[3] = req;
		paramValue[4] = resp;
		if(hashPageMap!=null){
			paramValue[5] = (Class) hashPageMap.get(ModuleConfig.HANDLE_CLASS);
			paramValue[6] = (String) hashPageMap.get(ModuleConfig.BY_COMPANY);
			paramValue[7] = (String) hashPageMap.get(ModuleConfig.RESOURCES);
		}else{
			paramValue[5] = null;
			paramValue[6] = null;
			paramValue[7] = null;
		}
		logger.debug("hashPageMap: "+hashPageMap);
		cstr = clsBuilder.getConstructor(paramType);
		Object webBuilder = cstr.newInstance(paramValue);		
		return webBuilder;
	}
	
	public Object getWebSiteBuilder(Map hashPageMap, HttpServletRequest req, HttpServletResponse resp, Element moduleEle) throws Exception{			    	
		Class[] paramType = new Class[6];
		Object[] paramValue = new Object[6];		
		Constructor cstr = null;		
		
		Class clsBuilder = (Class) hashPageMap.get(GeneralConst.HANDLE_CLASS);
		paramType[0] = ShopInfoBean.class;
		paramType[1] = ClientBean.class;
		paramType[2] = Connection.class;
		paramType[3] = HttpServletRequest.class;
		paramType[4] = HttpServletResponse.class;		
		paramType[5] = Element.class;
				
		paramValue[0] = _shopInfoBean;
		paramValue[1] = _clientBean;
		paramValue[2] = _dbConnection;
		paramValue[3] = req;
		paramValue[4] = resp;
		paramValue[5] = moduleEle;
		
		cstr = clsBuilder.getConstructor(paramType);
		Object webBuilder = cstr.newInstance(paramValue);		
		return webBuilder;
	}
	
	public StringBuffer validateBean(ArrayList currentDataList, HashMap[] dataMap, String module, Locale locale, String action) throws Exception{
		StringBuffer errorMsg = new StringBuffer();		
		StringBuffer resultBuffer = new StringBuffer();
		
		DataObject dataObj = new DataObject();			
		String value = null;
		HashMap map = null;
		
		try{
			for(int i=0; i<currentDataList.size(); i++){				
				errorMsg = new StringBuffer();
				
				map = (HashMap) currentDataList.get(i);
				dataObj = (DataObject) map.get("object");				
				value = (String) dataMap[0].get(dataObj.name);
				logger.debug("validateBean map = "+map);
				
				
				if(map.get("shadow")!=null && map.get("shadow").toString().equals("Y")) continue;
				if(map.get("xref")!=null && map.get("xref").toString().indexOf("AUTO-")!=-1) continue;
				if(GeneralConst.CREATE.equals(action) && dataObj.key) continue;
				if(dataObj.name.equals(BaseDef.UPDATEDATE)) continue;
				if("S".equals((String) map.get("systemfield"))) continue;
				
				if(!dataObj.nullable && CommonUtil.isEmpty(value)){
					errorMsg.append (ResourceUtil.getAdminResourceValue(null,"", "field.cannot.null", locale));
				}else{
					if(!CommonUtil.isEmpty(value)){
						if(errorMsg.length ()==0 && dataObj.type==Types.DATE){
							if(!Pattern.matches(DATE_FIELD, value)){
								errorMsg.append (ResourceUtil.getAdminResourceValue(null,"", "field.invalid.date", locale));					
							}
						}
						if(errorMsg.length ()==0 && dataObj.type==Types.TIMESTAMP){
							if(!Pattern.matches(TIMESTAMP_FIELD, value)){
								errorMsg.append (ResourceUtil.getAdminResourceValue(null,"", "field.invalid.date", locale));					
							}
						}
						if(errorMsg.length ()==0 && dataObj.type==Types.INTEGER){
							if(!Pattern.matches(INTEGER_FIELD, value)){
								errorMsg.append (ResourceUtil.getAdminResourceValue(null,"", "field.invalid.integer", locale));					
							}
						}
						if(errorMsg.length ()==0 && dataObj.type==Types.DOUBLE){
							if(!Pattern.matches(FLOATING_POINT_FIELD, value) && !Pattern.matches(INTEGER_FIELD, value)){
								errorMsg.append (ResourceUtil.getAdminResourceValue(null,"", "field.invalid.decimal", locale));					
							}
						}
						
						if(errorMsg.length ()==0 && (dataObj.type==Types.VARCHAR || dataObj.type==Types.DECIMAL)){
							if(value.length () > dataObj.length + dataObj.precision){								
								errorMsg.append (ResourceUtil.getAdminResourceValue(null,"", "field.value.too.long", locale));					
							}
						}
		 
					}
				}
				
				if(errorMsg.length ()>0){
					resultBuffer.append ("<span>");
						resultBuffer.append (ResourceUtil.getAdminResourceValue(_resources, module, dataObj.name, locale));
					resultBuffer.append (" </span>");					
					resultBuffer.append (errorMsg);
					resultBuffer.append ("<br>");
				}
			}			
		}catch(Exception e){
			throw e;
		}
		
		return resultBuffer;
	}	
	// With Standard Control Bar
	public StringBuffer buildListing(ArrayList searchDisplayList, String moduleName, boolean editable)
	throws Exception{
		return buildListing(searchDisplayList, moduleName, editable, getControlOption(editable, moduleName));
	}
	

	public StringBuffer buildListing(ArrayList searchDisplayList, String moduleName, boolean editable, StringBuffer controlBar)
	throws Exception{
		StringBuffer buffer = new StringBuffer();
		StringBuffer bufferColumnStyle = new StringBuffer();
		StringBuffer bufferColumnField = new StringBuffer();
		HashMap map = null;				
		DataObject dataObj = null;		
		String shadowField = null;		
		int colCount = 1;
		int type = 0;
		String className = null;
		String as = null;
		HashMap asMap = new HashMap();
		try{						
//			buffer.append ("<form id='").append (GeneralConst.LISTING_FORM).append ("' method='POST'>");	
			
			
			bufferColumnStyle.append ("<col width='10'>"); // Checkbox
			bufferColumnField.append("<th class='checkers'><input type='checkbox' class='checkall' /></th>"); // Checkbox (Key)
			
			// Shadow Field is for value of an id, exp. customer id field will have 1 value field contain customer name
			for(int i=0; i<searchDisplayList.size(); i++){
				map = (HashMap) searchDisplayList.get(i);
				logger.debug("buildListing map = "+map);
				dataObj = (DataObject) map.get("object");
				shadowField = (String) map.get("shadow");	
				as = (String) map.get("as");
				className = (String) map.get("classname");
				if(!CommonUtil.isEmpty(as)){
					asMap.put(as, dataObj);
				}
				type = CommonUtil.parseInt((String) map.get("type"));
				
				if(!dataObj.key && ComponentConst.HIDDEN!=type){
					colCount++;
					bufferColumnStyle.append ("<col width='").append((String) map.get("width")).append("'>");
					
					if("true".equals((String) map.get("sortable"))){	
						if(!CommonUtil.isEmpty(className) && className.indexOf("-autosuggest")!=-1){
							dataObj = (DataObject) asMap.get(dataObj.name+"_value");						
						}
						bufferColumnField.append (buildSortableColumn (moduleName, dataObj.name, null));
					}else{
						bufferColumnField.append (buildNormalColumn (moduleName, dataObj.name));
					}
				}				
			}
			if(editable){
				colCount++;
				bufferColumnStyle.append ("<col width='70'>"); // Edit
				bufferColumnField.append ("<th class='align_left center tools'>Tools</th>"); // Edit
			}
			bufferColumnField.append ("</tr>");
			
			buffer.append ("<table id='").append(moduleName).append("' class='sorting listingTable' border='0'>");
			buffer.append(bufferColumnStyle);
			
			buffer.append ("<thead>");
			buffer.append ("<tr>");
				buffer.append ("<td colspan='").append(colCount).append("' id='controlBar'>");
					buffer.append (controlBar);     			
				buffer.append ("</td>");
			buffer.append ("</tr>");	 
			buffer.append ("<tr>");				
				buffer.append(bufferColumnField);
			buffer.append ("</tr>");	
			buffer.append ("</thead>");
			buffer.append ("<tbody id='paginationData'>");
	    	
			buffer.append ("</tbody>");
			buffer.append ("</table>");
			buffer.append (AdminFieldBuilder.getActionField(""));
//			buffer.append ("</form>");	
		}catch(Exception e){
			throw e;
		}
		return buffer;
	}
	
	public StringBuffer buildSearchForm(String MODULE_NAME, ArrayList searchList){
		return buildSearchForm(MODULE_NAME, searchList, "");
	}
	
	public StringBuffer buildSearchForm(String MODULE_NAME, ArrayList searchList, String defaultAction) {
		StringBuffer mBuffer = new StringBuffer();					
		StringBuffer colBuffer = new StringBuffer();		
		HashMap listMap = null;
    	
    	DataObject dataObject = null;
    	String visibility = null;    	
    	int type = 0;
    	
    	StringBuffer visibleBuffer = new StringBuffer();
    	StringBuffer hideBuffer = new StringBuffer();
    	StringBuffer hiddenFieldBuffer = new StringBuffer();
    	StringBuffer valueBuffer = new StringBuffer();
    	
	    try{
	    	colBuffer.append ("<col width='130'>");
//	    	colBuffer.append ("<col width='10'>");
	    	colBuffer.append ("<col>");
	    	for(int i=0; i<searchList.size(); i++){
	    		listMap = (HashMap) searchList.get(i);
	    		logger.debug("buildSearchForm listMap = "+listMap);
	    		dataObject 	= (DataObject) listMap.get("object");
	    		visibility 	= (String) listMap.get("visibility");	    			    			    		
	    		type 		= Integer.parseInt((String) listMap.get("type"));
	    		
	    		valueBuffer = new StringBuffer();
	    		if(type==ComponentConst.HIDDEN){
	    			hiddenFieldBuffer.append (AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, null, null, GeneralConst.SEARCH_FORM));
	    		}else{		    		
	    			if(ComponentConst.IMAGE==type) continue;
	    			if(dataObject.name.equals("companyid")) continue;
		    		valueBuffer.append ("<tr>");
			    		valueBuffer.append ("<th>").append (ResourceUtil.getAdminResourceValue(_resources, MODULE_NAME, dataObject.name, _clientBean.getLocale ())).append ("</th>");
//			    		valueBuffer.append ("<td>").append (":").append ("</td>");			    	
			    		valueBuffer.append ("<td>");
		    				valueBuffer.append (AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, null, null, GeneralConst.SEARCH_FORM));		    				
			    			if("true".equals((String) listMap.get("displayControl"))){
			    				valueBuffer.append (getSearchOptionLink());
			    			}
		    			valueBuffer.append ("</td>");
	    			valueBuffer.append ("</tr>");
	    		
	    		if("show".equals(visibility)){
	    			visibleBuffer.append (valueBuffer);		    			
	    		}else{
	    			hideBuffer.append (valueBuffer);	    			
	    		}
	    		}
	    	}
	    	mBuffer.append("<div id='").append(MODULE_NAME).append("' class='searchDiv'>");
//		    	mBuffer.append ("<form id='").append (GeneralConst.SEARCH_FORM).append ("' method='POST'>");	
			    	mBuffer.append (AdminFieldBuilder.getActionField(""));	    
			    	
		    		mBuffer.append ("<table class='searchFormTable'>");    
		    			mBuffer.append (colBuffer);
		    			mBuffer.append (visibleBuffer);
		    			
		    			mBuffer.append ("<tbody id='searchMoreOption' style='display: none;'>").append (hideBuffer).append ("</tbody>");
				    mBuffer.append ("</table>");
					
//					mBuffer.append ("<div id='searchMoreOption' style='display: none;'>");
//						mBuffer.append ("<table id='moreOptionTable' border='1'>");
//							mBuffer.append (colBuffer);
//							mBuffer.append ("<tbody>").append (hideBuffer).append ("</tbody>");
//				    	mBuffer.append ("</table>");
//					mBuffer.append ("</div>");
//					mBuffer.append ("<div id='topPageDiv'>");	    			
//						mBuffer.append (getPaginationControl());
//					mBuffer.append ("</div>");    		    		
					mBuffer.append(hiddenFieldBuffer);
//		    	mBuffer.append ("</form>");	  
	    	mBuffer.append ("</div>");
	    }catch(Exception e){
	    	logger.error (e, e);
	    }finally{
	    	
	    }
	    return mBuffer;		
	}
	protected StringBuffer buildEntryForm(String MODULE_NAME, ArrayList addList, int columns) {
		return buildEntryForm(MODULE_NAME, addList, columns, 0);
	}
	protected StringBuffer buildEntryForm(String MODULE_NAME, ArrayList addList, int columns, int tabs) {
		StringBuffer mBuffer = new StringBuffer();
		StringBuffer columnBuffer = new StringBuffer();
		StringBuffer hiddenBuffer = new StringBuffer();
		HashMap listMap = null;
    	
    	DataObject dataObject = null;    	    	
    	int type = 0;    	
    	StringBuffer valueBuffer = new StringBuffer();
    	String tabGroup = null;     	
    	String xref = null;
    	String htmloption = null;
    	String colspan = null;
    	int addSize = 0;
    	int i=0;
    	int showCount = 0;
	    try{
//	    	mBuffer.append ("<form id='").append (GeneralConst.ENTRY_FORM).append ("' method='POST' class='validateForm'>");
//	    	columnBuffer[0] = new StringBuffer();
//	    	columnBuffer[1] = new StringBuffer();
//	    	columnBuffer[2] = new StringBuffer();
	    	addSize = addList.size();	    	
	    	for(i=0; i<addSize; i++){	    		
//	    		logger.debug("("+showCount+"%"+columns+") = "+(showCount%columns));
	    		listMap = (HashMap) addList.get(i);
	    		dataObject 	= (DataObject) listMap.get("object");	    		
	    		tabGroup = (String) listMap.get("columngroup");
	    		colspan = (String) listMap.get("colspan");
	    		xref = (String) listMap.get("xref");
	    		type 		= Integer.parseInt((String) listMap.get("type"));	

	    		if(!CommonUtil.isEmpty(tabGroup) && !("tab-"+tabs).equals(tabGroup)) continue;
	    		
//	    		if(xref!=null && xref.indexOf("AUTO-")!=-1){
//	    			htmloption = (String) listMap.get("htmloption") + " readonly='readonly'";
//	    			listMap.put("htmloption", htmloption);
//	    		}
//	    		logger.debug("buildEntryForm: "+listMap);	 
//	    		if(listMap.get("shadow")!=null && listMap.get("shadow").equals("Y")) continue;
	    		if(type==ComponentConst.HIDDEN){
	    			hiddenBuffer.append(AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, null, null, GeneralConst.ENTRY_FORM));
	    		}else{
	    			if(type==ComponentConst.IMAGE) continue;
	    			
	    			valueBuffer = new StringBuffer();
	    			if((columns>1 && (showCount%columns)==0) || columns==1){
	    				valueBuffer.append ("<tr>");
	    			}
		    		
		    		valueBuffer.append ("<th>");
		    		valueBuffer.append (ResourceUtil.getAdminResourceValue(_resources, MODULE_NAME, dataObject.name, _clientBean.getLocale ()));

		    		valueBuffer.append ("</th>");	   
		    		if(!CommonUtil.isEmpty(colspan)){
		    			valueBuffer.append ("<td colspan='").append(colspan).append("'>");
		    			showCount+=3;
		    		}else{
		    			valueBuffer.append ("<td>");
		    		}
		    		
	    				valueBuffer.append (AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, null, null, GeneralConst.ENTRY_FORM));		    				
	    			valueBuffer.append ("</td>");
	    			
	    			if((columns>1 && (showCount%columns)==1) || columns==1){
	    				valueBuffer.append ("</tr>");
	    			}
	 
	    			columnBuffer.append (valueBuffer);
	    			showCount++;
	    		}   		    		
	    	}
	    	// Insert Leftover TD
	    	
	    	if(columns>1 && (showCount%columns)>0){
	    		int leftover = (showCount%columns);
	    		for(i=0; i<leftover; i++){
	    			columnBuffer.append("<th nowrap> </th><td> </td>");
	    		}
	    		columnBuffer.append("</tr>");
	    	}
	    			
	    	
	    	
	    	mBuffer.append ("<table id='entryFormTable' width='100%' class='validateForm listTable'>");
	    	if(columns==2){
	    		mBuffer.append("<col width='15%'><col width='35%'><col width='15%'><col width='35%'>");
	    	}else{
	    		mBuffer.append("<col width='15%'><col />");
	    	}
	    	mBuffer.append (columnBuffer);
//	    	for(int i=0; i<columnBuffer.length; i++){
//	    		if(columnBuffer[i].length()>0){
//		    		mBuffer.append ("<td>");
//		    			mBuffer.append ("<table>");
//			    			mBuffer.append ("<col width='100'><col>");
//			    			mBuffer.append (columnBuffer[i]);
//		    			mBuffer.append ("</table>");
//		    		mBuffer.append ("</td>");
//	    		}
//	    	}
//	    	mBuffer.append ("</tr>");	    
	    	
	    	mBuffer.append ("</table>");
	    	mBuffer.append (AdminFieldBuilder.getActionField(""));
	    	mBuffer.append(hiddenBuffer);
	    }catch(Exception e){
	    	logger.error (e, e);
	    }finally{
	    	
	    }
	    return mBuffer;		
	}
	public StringBuffer buildDetailEntryForm(Class defClass){
		return buildDetailEntryForm(defClass, "detail-listingTable", null); // With event bind
	}
	public StringBuffer buildDetailEntryForm(Class defClass, String className, StringBuffer customControlBar){
		StringBuffer mBuffer = new StringBuffer();		
		StringBuffer colHeaderBuffer = new StringBuffer();
		StringBuffer cloneBuffer = new StringBuffer();
		StringBuffer cloneHiddenBuffer = new StringBuffer();
		String controlBar = null;
		String customControl = null;
		HashMap listMap = null;
    	
    	DataObject dataObject = null;    	
    	int type = 0;
    	String MODULE_NAME = null; 
    	ArrayList addList = null;
    	String TABLE_PREFIX = null;
    	String TABLE_CLASS_NAME = null;
    	String as = null;
    	
    	StringBuffer firstColumnBuffer = new StringBuffer();
    	StringBuffer controlBarBuffer = new StringBuffer();
	    try{	    	
	    	MODULE_NAME 	= (String) BeanDefUtil.getField(defClass,BeanDefUtil.MODULE_NAME);
	    	customControl 	= (String) BeanDefUtil.getField(defClass, BeanDefUtil.CUSTOM_CONTROL);	    	
	    	TABLE_PREFIX 	= (String) BeanDefUtil.getField(defClass,BeanDefUtil.TABLE_PREFIX);
	    	TABLE_CLASS_NAME 	= (String) BeanDefUtil.getField(defClass,BeanDefUtil.TABLE_CLASS_NAME);
	    	
//	    	defClassField = defClass.getDeclaredField("_addList");
	    	addList = BeanDefUtil.getArrayList(defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
	    	
	    	controlBar = (String) BeanDefUtil.getField(defClass, "CONTROL_BAR");	    	
	    	controlBar = (controlBar==null)?"Y":controlBar;
	    	
    		colHeaderBuffer.append ("<th class='checkers'><input type='checkbox' class='checkall' /></th>");	    	
    		
    		int totalTD = 0;
	    	for(int i=0; i<addList.size(); i++){
	    		listMap = (HashMap) addList.get(i);
	    		dataObject 	= (DataObject) listMap.get("object");	  
	    		if(dataObject==null) continue;
	    		as = (String)listMap.get("as");
	    		type = Integer.parseInt((String) listMap.get("type"));		    				    		
		    		
	    		if(type!=ComponentConst.HIDDEN){
	    			totalTD++;
	    			
	    			colHeaderBuffer.append ("<th>");		    		
	    			colHeaderBuffer.append (ResourceUtil.getAdminResourceValue(_resources, MODULE_NAME, (as==null)?dataObject.name:as, _clientBean.getLocale ()));		    		
	    			colHeaderBuffer.append ("</th>");
	    			
	    			cloneBuffer.append ("<td>").append (AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, TABLE_PREFIX, null, GeneralConst.ENTRY_FORM)).append ("</td>");
	    			cloneBuffer.append("\n");	
	    		}else{
	    			cloneHiddenBuffer.append(AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, listMap, TABLE_PREFIX, null, GeneralConst.ENTRY_FORM));
	    		}
	    			    		   	
	    	}	    	
    		firstColumnBuffer.append("<td>");
	    	firstColumnBuffer.append("<input type='checkbox' name='chkDetail'><input type='hidden' name='"+TABLE_PREFIX+"row-status' value='X' />");
	    	firstColumnBuffer.append(cloneHiddenBuffer);
	    	firstColumnBuffer.append("</td>");
	    	
	    	cloneBuffer.insert(0,firstColumnBuffer);	    	
    	
	    	controlBarBuffer.append("<tr><td colspan='").append(totalTD+1).append("' id='controlBar'>");
	    	if(controlBar.equals("Y")){	    		
	    		if(customControlBar!=null){
	    			controlBarBuffer.append (customControlBar);
	    		}else{
	    			if(GeneralConst.CLONE_ADD_NEW.equals(customControl)){
	    				controlBarBuffer.append (getControlOptionCloneNew());
	    			}else if(GeneralConst.CLONE_ADD_CUSTOM.equals(customControl)){
	    				controlBarBuffer.append (getControlOptionCloneWithPopUpEntry());
	    			}else if(GeneralConst.CONTROL_RETRIEVE_PRODUCT.equals(customControl)){
	    				controlBarBuffer.append (getControlOptionRetrieveProduct());
	    			}else if(GeneralConst.CLONE_UPLOAD.equals(customControl)){
	    				controlBarBuffer.append (getControlOptionCloneUpload());
	    			}else if(GeneralConst.CLONE_UPLOAD_ADD.equals(customControl)){
	    				controlBarBuffer.append (getControlOptionCloneUploadAdd());
	    			}else{
	    				controlBarBuffer.append (getControlOptionClone());
	    			}
	    		}
	    	}
	    	controlBarBuffer.append("</td></tr>"); 	    	

	    	mBuffer.append ("<table id='").append(TABLE_PREFIX).append("EntryTable' class='listTable ");
	    	if(!CommonUtil.isEmpty(TABLE_CLASS_NAME)){
	    		mBuffer.append(TABLE_CLASS_NAME).append(" ");
	    	}
	    	mBuffer.append(className).append("'>");
	    	mBuffer.append ("<thead>");
	    		mBuffer.append(controlBarBuffer);
	    		mBuffer.append("<tr>").append(colHeaderBuffer).append("</tr>");
	    		mBuffer.append ("\n<tr style='display: none' id='").append(TABLE_PREFIX).append("Clone'>").append(cloneBuffer).append("</tr>");
//	    		mBuffer.append ("<tr id='").append(TABLE_PREFIX).append("Clone'>").append(cloneBuffer).append("</tr>");
	    	mBuffer.append ("</thead>");
	    		    	
	    	
	    	
	    	mBuffer.append ("<tbody id='").append(TABLE_PREFIX).append("EntryTableBody' class='validateForm'>");	    		    		    	
//	    	mBuffer.append ("<tr>").append(cloneBuffer).append("</tr>");	    	
	    	mBuffer.append ("</tbody>");
	    	mBuffer.append ("</table>");	    		    	    	
	    }catch(Exception e){
	    	logger.error (e, e);
	    }
	    return mBuffer;		
	}
	
	public JSONArray buildEdit(ArrayList dataList, Class defClass, String prefix, boolean isCopy)throws OwnException {
		System.out.println("Adam 2");
		HashMap mData = null;
		Iterator mItr = null;
		String fieldName = null;
		JSONObject mJSON = new JSONObject();
		JSONArray m_oJSON = new JSONArray();		
		JSONArray arrJSON = new JSONArray();
		String autoGenField = "";
		String key = "";
		String value = "";
		try{
			key = (String) BeanDefUtil.getField(defClass, BeanDefUtil.KEY);
			if(isCopy){
				autoGenField = (String) BeanDefUtil.getField(defClass, BeanDefUtil.AUTOGEN_FIELDS);
				logger.debug("autoGenField = "+autoGenField);
			}
			for(int i=0; i<dataList.size (); i++){				
				mData = (HashMap) dataList.get (i);		
//				logger.debug("buildEdit mData = "+mData);
				mItr = mData.keySet ().iterator ();
				m_oJSON = new JSONArray();
				
				while(mItr.hasNext ()){
					fieldName = (String) mItr.next ();		
					value = (String) mData.get (fieldName);
					
					if((isCopy && fieldName.equals(key)) || (autoGenField!=null && autoGenField.indexOf(fieldName+",")!=-1)){
						continue;
					}
					if("password".equals(fieldName)){
						value = CryptUtil.getInstance ().decrypt(value);
					}
					
					mJSON = new JSONObject();
					if(!CommonUtil.isEmpty(prefix)){
						mJSON.put ("n", prefix + fieldName); // Name						
					}else{
						mJSON.put ("n", fieldName); // Name
					}
					
					mJSON.put ("v", value); // Value
						if(fieldName.equalsIgnoreCase (key)){
							mJSON.put ("c", "L"); // Control
						}
					m_oJSON.put (mJSON);					
				}						
				arrJSON.put(m_oJSON);
			}		
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}				
		return arrJSON;
	}	
	
	public JSONObject buildEditDetail(ArrayList dataList, String key, String prefix, HashMap extraDataEachRow, boolean isCopy)throws OwnException {		
		HashMap mData = null;
		Iterator mItr = null;
		Iterator extraDataIt = null;
		String fieldName = null;
		String fieldValue = null;
		JSONObject jsonMain = new JSONObject();
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();		
		JSONArray arrJSON = new JSONArray();
		
		String extraKey = null;
		try{
			String imagePath = ResourceUtil.getSettingValue ("photos.path"); 
			imagePath += _shopInfoBean.getShopName () + "/";
			
			jsonMain.put("md", prefix);
			logger.debug("isCopy = "+isCopy);
			for(int i=0; i<dataList.size (); i++){				
				mData = (HashMap) dataList.get (i);		
//				logger.debug("buildEditDetail mData  = "+mData);
				mItr = mData.keySet ().iterator ();
				jsonArray = new JSONArray();
				
				json = new JSONObject();
				while(mItr.hasNext ()){
					fieldName = (String) mItr.next ();				
					if(isCopy && fieldName.equals(key)){
						continue;
					}
					fieldValue = (String) mData.get (fieldName);
					
					if(!CommonUtil.isEmpty(prefix)){
						fieldName = prefix + fieldName;
					}
					if(!CommonUtil.isEmpty(fieldValue)){
						if(fieldValue.toLowerCase().endsWith(".jpg") || fieldValue.toLowerCase().endsWith(".jpeg") || fieldValue.toLowerCase().endsWith(".gif")){
							json.put(fieldName, fieldValue);
							//json.put(fieldName+"tn", imagePath+mData.get(ProductBeanDef.productid.name)+"/tn_"+fieldValue);
						}else{
							json.put(fieldName, fieldValue);
						}
					}

					if(fieldName.equalsIgnoreCase (key)){
						//json.put ("c", "L"); // Control
					}
//					jsonArray.put (json);					
				}				
				if(json.isNull(prefix + "row-status") && json.isNull("row-status")){
					if(!CommonUtil.isEmpty(prefix)){
						json.put(prefix + "row-status", isCopy?"N":"U");											
					}else{
						json.put("row-status", isCopy?"N":"U");
					}
				}
				if(extraDataEachRow!=null){
					extraDataIt = extraDataEachRow.keySet().iterator();
					while(extraDataIt.hasNext()){
						extraKey = (String) extraDataIt.next();
						json.put(extraKey, extraDataEachRow.get(extraKey));
					}
				}
				jsonArray.put (json);				
				arrJSON.put(jsonArray);
			}		
			
			jsonMain.put("data", arrJSON);
//			logger.debug("jsonMain = "+jsonMain);
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}				
		return jsonMain;
	}
	
	public JSONArray buildAddEditRecord(Class defClass, String key, ArrayList searchDisplayList, HashMap dataMap) throws Exception {
		StringBuffer mBuffer = new StringBuffer();
		StringBuffer keyBuffer = new StringBuffer();
//		StringBuffer trBuffer = new StringBuffer();
		
		DataObject dataObj = null;
		HashMap displayMap = null;
		String xref = null;
		HashMap fieldMap = null;	
		Class[] paramType = new Class[2];
		Object[] paramValue = new Object[2];				
		Method mthd = null;	
		int type = 0;
		String className = null;
//		StringBuffer tdBuffer = new StringBuffer();
		Class selectClass = null;
		String[] OPTIONS = (String[]) BeanDefUtil.getField(defClass, "OPTIONS");
		String value = null;
				
		JSONArray rowArray = new JSONArray();		
		
		try{
			String imagePath = ResourceUtil.getSettingValue ("photos.path"); 
			imagePath += _shopInfoBean.getShopName () + "/";
			logger.debug("buildAddEditRecord dataMap = "+dataMap);
			if(dataMap!=null){		 								    					
	    		for(int a=0; a<searchDisplayList.size(); a++){
//	    			tdBuffer = new StringBuffer();
	    			displayMap = (HashMap) searchDisplayList.get(a);
//	    			logger.debug("buildAddEditRecord displayMap = "+displayMap);
	    			dataObj = (DataObject) displayMap.get("object");
	    			xref = (String) displayMap.get("xref");
	    			type = CommonUtil.parseInt((String) displayMap.get("type"));
	    			className 	= (String) displayMap.get("classname");
	    			selectClass	= (Class)  displayMap.get("xrefclass");
	    			
	    			if(type==ComponentConst.HIDDEN) continue;
	    			if(dataObj.name.equals("updatedate")){
		    			dataMap.put(dataObj.name, getCurrentDate());
		    		}
//	    			if(dataObj.key){
//	    				keyBuffer.append ("<td>");
//	    			}else{
//	    				tdBuffer.append ("<td>");
//	    			}
	    			if(!CommonUtil.isEmpty(xref) && !xref.startsWith("AUTO-")){		    				
	    				paramType[0] = Connection.class;
	    				paramType[1] = ShopInfoBean.class;
	    				paramValue[0] = _dbConnection;
	    				paramValue[1] = _shopInfoBean;
	    				if(selectClass==null){
	    					selectClass = SelectBuilder.class;
	    				}
	    				logger.debug("selectClass = "+selectClass + ", xref = "+xref);
	    				
	    				mthd = selectClass.getMethod ("getHASH_"+xref, paramType);						
	    				fieldMap = (HashMap)mthd.invoke (selectClass, paramValue);	
	    				value = (String)dataMap.get (dataObj.name);
	    				logger.debug("fieldMap = "+fieldMap);
	    				logger.debug("xref = "+xref);
	    				logger.debug("value = "+value);
	    				if(fieldMap.get(value).getClass()==String.class){
							value = (String) fieldMap.get(value);
						}else{
							value = (String) ((HashMap) fieldMap.get(value)).get(value);
						}		    				
//	    				tdBuffer.append (value);	
	    				rowArray.put(value);
	    			}else{
		    			if(dataObj.key){
		    				keyBuffer.append ("<input type='checkbox'");	
		    				keyBuffer.append (" id='").append (key).append ("'");
		    				keyBuffer.append (" name='").append (key).append ("'");
		    				keyBuffer.append (" value='").append (dataMap.get(key)).append ("'");
		    				keyBuffer.append ("/>");	
		    				rowArray.put(keyBuffer);
		    			}else if(ComponentConst.IMAGE==type){
//		    				tdBuffer.append ("<img src='").append(dataMap.get(dataObj.name)).append("' ");
//		    				tdBuffer.append(" class='").append(className).append("'");
//		    				tdBuffer.append(" />");
		    			}else{	    				
		    				if(!CommonUtil.isEmpty(className) && className.endsWith("-autosuggest")){	
		    					value = (String)dataMap.get (dataObj.name+"_value");		    					
//		    					tdBuffer.append ((String)dataMap.get (dataObj.name+"_value"));
		    				}else{
		    					value = (String)dataMap.get (dataObj.name);
//		    					tdBuffer.append ((String)dataMap.get (dataObj.name));
		    				}
		    				rowArray.put(value);
		    			}
		    			
		    			
	    			}
//	    			if(dataObj.key){	    				
//	    				keyBuffer.append ("</td>");
//	    			}else{
//	    				tdBuffer.append ("</td>");
//	    			}    			
//	    			mBuffer.append(tdBuffer);
	    		}
		    		
//	    		mBuffer.append ("<td nowrap>");
	    		
	    		mBuffer.append("<a href='#' class='paginate_button' id='").append (GeneralConst.EDIT).append ("'");
		    	mBuffer.append (" rel='").append (key).append ("=").append (dataMap.get(key)).append ("' >");
		    	mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.EDIT, _clientBean.getLocale ())).append ("</a>");
		    	
	    		if(OPTIONS!=null){
    				for(int x=0;x<OPTIONS.length;x++){
    					mBuffer.append("<a href='");
    					mBuffer.append(_clientBean.getRequestFilename());
    					mBuffer.append("?");
    					mBuffer.append(GeneralConst.ACTION_NAME).append("=").append(OPTIONS[x]).append("&");
    					mBuffer.append(key).append("=").append((String)dataMap.get (key));
    					if(GeneralConst.REPORT.equals(OPTIONS[x]) || GeneralConst.REPORT_INV.equals(OPTIONS[x]) ||GeneralConst.REPORT_DO.equals(OPTIONS[x]) ||GeneralConst.JOBSHEET.equals(OPTIONS[x])){
    						mBuffer.append("&").append("return").append("=").append("rpt");		    						
    					}
    					mBuffer.append("' id='").append (OPTIONS[x]).append ("'");
    					mBuffer.append(" class='paginate_button'");
    					mBuffer.append(" target='_blank'");
		    			mBuffer.append(" rel='").append (key).append ("=").append ((String)dataMap.get (key)).append ("' >");
		    			mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", OPTIONS[x], _clientBean.getLocale ())).append ("</a>");		    			
    				}
    			}
	    		
		    	rowArray.put(mBuffer);
//		    	trBuffer.append ("<tr class='").append (GeneralConst.NEW_RECORD_CLASS).append ("'>");
//		    	trBuffer.append(keyBuffer);
//		    	trBuffer.append(mBuffer);
//		    	trBuffer.append ("</tr>");				    	
	    	}
		}catch(Exception e){
			throw e;
		}
		
		return rowArray;
	}
	
	// Temporary solution to gather for DataTable fnAddData
	public StringBuffer buildAddRecordReturn(Class defClass, String key, ArrayList searchDisplayList, HashMap dataMap) throws Exception {
		StringBuffer trBuffer = new StringBuffer();
		StringBuffer keyBuffer = new StringBuffer();
		StringBuffer tableBuffer = new StringBuffer();
		DataObject dataObj = null;
		HashMap displayMap = null;
		String xref = null;
		HashMap fieldMap = null;	
		Class[] paramType = new Class[2];
		Object[] paramValue = new Object[2];				
		Method mthd = null;	
		int type = 0;
		String className = null;
		StringBuffer tdBuffer = new StringBuffer();
		Class selectClass = null;
		String[] OPTIONS = (String[]) BeanDefUtil.getField(defClass, "OPTIONS");
		String value = null;
		try{
			String imagePath = ResourceUtil.getSettingValue ("photos.path"); 
			imagePath += _shopInfoBean.getShopName () + "/";
			logger.debug("buildAddEditRecord dataMap = "+dataMap);
			if(dataMap!=null){		 								    					
	    		for(int a=0; a<searchDisplayList.size(); a++){
	    			tdBuffer = new StringBuffer();
	    			displayMap = (HashMap) searchDisplayList.get(a);
//	    			logger.debug("buildAddEditRecord displayMap = "+displayMap);
	    			dataObj = (DataObject) displayMap.get("object");
	    			xref = (String) displayMap.get("xref");
	    			type = CommonUtil.parseInt((String) displayMap.get("type"));
	    			className 	= (String) displayMap.get("classname");
	    			selectClass	= (Class)  displayMap.get("xrefclass");
	    			
	    			if(type==ComponentConst.HIDDEN) continue;
	    			if(dataObj.name.equals("updatedate")){
		    			dataMap.put(dataObj.name, getCurrentDate());
		    		}
	    			if(dataObj.key){
	    				keyBuffer.append ("<td>");
	    			}else{
	    				tdBuffer.append ("<td>");
	    			}
	    			if(!CommonUtil.isEmpty(xref) && !xref.startsWith("AUTO-")){		    				
	    				paramType[0] = Connection.class;
	    				paramType[1] = ShopInfoBean.class;
	    				paramValue[0] = _dbConnection;
	    				paramValue[1] = _shopInfoBean;
	    				if(selectClass==null){
	    					selectClass = SelectBuilder.class;
	    				}
	    				logger.debug("selectClass = "+selectClass + ", xref = "+xref);
	    				
	    				mthd = selectClass.getMethod ("getHASH_"+xref, paramType);						
	    				fieldMap = (HashMap)mthd.invoke (selectClass, paramValue);	
	    				value = (String)dataMap.get (dataObj.name);
	    				logger.debug("xref = "+xref);
	    				logger.debug("value = "+value);
	    				logger.debug("fieldMap = "+fieldMap);
	    				if(fieldMap.get(value).getClass()==String.class){
							value = (String) fieldMap.get(value);
						}else{
							value = (String) ((HashMap) fieldMap.get(value)).get(value);
						}		    				
	    				tdBuffer.append (value);	
	    			}else{
		    			if(dataObj.key){
		    				keyBuffer.append ("<input type='checkbox'");	
		    				keyBuffer.append (" id='").append (key).append ("'");
		    				keyBuffer.append (" name='").append (key).append ("'");
		    				keyBuffer.append (" value='").append (dataMap.get(key)).append ("'");
		    				keyBuffer.append ("/>");	
		    			}else if(ComponentConst.IMAGE==type){
		    				tdBuffer.append ("<img src='").append(dataMap.get(dataObj.name)).append("' ");
		    				tdBuffer.append(" class='").append(className).append("'");
		    				tdBuffer.append(" />");
		    			}else{	    				
		    				if(!CommonUtil.isEmpty(className) && className.endsWith("-autosuggest")){		    					
		    					tdBuffer.append ((String)dataMap.get (dataObj.name+"_value"));
		    				}else{
		    					tdBuffer.append ((String)dataMap.get (dataObj.name));
		    				}
		    			}
	    			}
	    			if(dataObj.key){
	    				keyBuffer.append ("</td>");
	    			}else{
	    				tdBuffer.append ("</td>");
	    			}    			
	    			trBuffer.append(tdBuffer);
	    		}
		    	
	    		trBuffer.append("<td>");
	    		trBuffer.append("<a href='#' class='paginate_button' id='").append (GeneralConst.EDIT).append ("'");
		    	trBuffer.append (" rel='").append (key).append ("=").append (dataMap.get(key)).append ("' >");
		    	trBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.EDIT, _clientBean.getLocale ())).append ("</a>");
		    	
	    		if(OPTIONS!=null){
    				for(int x=0;x<OPTIONS.length;x++){
    					trBuffer.append("<a href='");
    					trBuffer.append(_clientBean.getRequestFilename());
    					trBuffer.append("?");
    					trBuffer.append(GeneralConst.ACTION_NAME).append("=").append(OPTIONS[x]).append("&");
    					trBuffer.append(key).append("=").append((String)dataMap.get (key));
    					if(GeneralConst.REPORT.equals(OPTIONS[x]) || GeneralConst.REPORT_INV.equals(OPTIONS[x]) ||GeneralConst.REPORT_DO.equals(OPTIONS[x]) ||GeneralConst.JOBSHEET.equals(OPTIONS[x])){
    						trBuffer.append("&").append("return").append("=").append("rpt");		    						
    					}
    					trBuffer.append("' id='").append (OPTIONS[x]).append ("'");
    					trBuffer.append(" class='paginate_button'");
    					trBuffer.append(" target='_blank'");
		    			trBuffer.append(" rel='").append (key).append ("=").append ((String)dataMap.get (key)).append ("' >");
		    			trBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", OPTIONS[x], _clientBean.getLocale ())).append ("</a>");		    			
    				}
    			}
	    		trBuffer.append("</td>");
	    		
	    		tableBuffer.append ("<tr class='").append (GeneralConst.NEW_RECORD_CLASS).append ("'>");
	    		tableBuffer.append(keyBuffer);
	    		tableBuffer.append(trBuffer);
	    		tableBuffer.append ("</tr>");			    				    	
	    	}
		}catch(Exception e){
			throw e;
		}
		
		return tableBuffer;
	}
	
	public JSONObject buildSearchResult1(Class defClass, String key, ArrayList searchDisplayList, ArrayList recordArrayList, 
			boolean editable, boolean withRowStatusField, String prefix) throws Exception {
		logger.debug("buildSearchResult key = "+key);
		HashMap mMap = null;
		String[] arrRow = {"class='even'", ""};		
		StringBuffer mBuffer = new StringBuffer();
		StringBuffer bufferHidden = new StringBuffer();
		String value = null;
		int type = 0;
		String className = null;
		String shadowField = null;
		HashMap displayMap = null;
				
		prefix = (prefix==null)?"":prefix;
		DataObject dataObj = null;
		
		StringBuffer lastRow = new StringBuffer(); 
		StringBuffer firstColumnBuffer = new StringBuffer();
		StringBuffer columnBuffer = new StringBuffer();		
		String[] OPTIONS = (String[]) BeanDefUtil.getField(defClass, "OPTIONS");
		
		JSONObject json = new JSONObject();
		JSONArray rowArray = new JSONArray();
		JSONArray dataArray = new JSONArray();
		int dataSize = 0;
		int searchDisplayCount = 0;
		ArrayList keyList = null;
		ArrayList dataArrayList = null;
		try{
//			if(!CommonUtil.isEmpty(className)){
//				arrRow[0] = "class='"+className+"'";
//				arrRow[1] = "class='"+className+"'";
//			}
			if(recordArrayList!=null && recordArrayList.size () > 0){		
				keyList = (ArrayList) recordArrayList.get(0);
				dataArrayList = (ArrayList) recordArrayList.get(1);
				dataSize = dataArrayList.size ();
				searchDisplayCount = searchDisplayList.size();
				
		    	for(int i=0; i<dataSize; i++){
		    		lastRow = new StringBuffer();
		    		bufferHidden = new StringBuffer();
		    		firstColumnBuffer = new StringBuffer();
		    		columnBuffer = new StringBuffer();
		    		
		    		mMap = (HashMap) dataArrayList.get (i);
		    		logger.debug("mMap = "+mMap);
		    		mBuffer.append ("<tr ").append (arrRow[(i % 2)]).append (">");//
		    		
		    		rowArray = new JSONArray();
		    		
		    		for(int a=0; a<searchDisplayCount; a++){
		    			displayMap = (HashMap) searchDisplayList.get(a);
//		    			logger.debug("displayMap = "+displayMap);
		    			dataObj = (DataObject) displayMap.get("object");
		    			value = (String) mMap.get(dataObj.name);		    			
		    			type 	= CommonUtil.parseInt((String) displayMap.get("type"));
		    			className = (String) displayMap.get("classname");
		    			shadowField = (String) displayMap.get("shadow");
		    			
		    			if((!CommonUtil.isEmpty(className) && className.endsWith("autosuggest")) || !CommonUtil.isEmpty((String) mMap.get(dataObj.name+"_value")) ){
		    				if(!dataObj.key){
		    					value= (String) mMap.get(dataObj.name+"_value");
		    				}else{
		    					value= (String) mMap.get(dataObj.name);
		    				}
		    			}
//		    			logger.debug("Name: "+dataObj.name +", Value: " + value);
		    			if(dataObj.key){
		    				rowArray.put(AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, displayMap, prefix, value, GeneralConst.LISTING_FORM));	
		  
//		    				firstColumnBuffer.append ("<th class='checkers'>");	
//		    					firstColumnBuffer.append (WebFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, displayMap, prefix, value, GeneralConst.LISTING_FORM));
//		    				firstColumnBuffer.append ("</th>");
		    				
		    			}else{
		    				if(!"Y1".equalsIgnoreCase(shadowField)){
		    					
				    			if(type!=ComponentConst.HIDDEN){
				    				rowArray.put(AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, displayMap, prefix, value, GeneralConst.LISTING_FORM));
//				    				rowArray.put(value);
//				    				columnBuffer.append ("<td>");	
//				    					columnBuffer.append (WebFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, displayMap, prefix, value, GeneralConst.LISTING_FORM));
//				    				columnBuffer.append ("</td>");
				    			}else{		    	
				    				
//				    				bufferHidden.append (WebFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, displayMap, prefix, value));
				    			}
		    				}
		    			}
		    		}
		    		mBuffer.append(firstColumnBuffer).append(columnBuffer);
		    				    					    		
		    		if(withRowStatusField){
		    			rowArray.put("<input type='hidden' name='"+prefix+"row-status' value='U' />");
//		    			bufferHidden.append ("<input type='hidden' name='").append(prefix).append("row-status' value='U' />");	
		    		}
		    		if(editable){		  
		    			lastRow.append("<a href='#' class='paginate_button'  id='").append (GeneralConst.EDIT).append ("'");
		    			lastRow.append (" rel='").append (key).append ("=").append ((String)mMap.get (key)).append ("' >");
		    			lastRow.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.EDIT, _clientBean.getLocale ()));
		    			lastRow.append ("</a>");
		    
		    			if(OPTIONS!=null){
		    				for(int x=0;x<OPTIONS.length;x++){
//		    					lastRow.append(", ");
		    					lastRow.append("<a href='").append(_clientBean.getRequestFilename()).append("?");
		    					lastRow.append(GeneralConst.ACTION_NAME).append("=").append(OPTIONS[x]).append("&");
		    					lastRow.append(key).append("=").append((String)mMap.get (key));

		    					if(GeneralConst.REPORT.equals(OPTIONS[x]) || GeneralConst.REPORT_INV.equals(OPTIONS[x]) ||GeneralConst.REPORT_DO.equals(OPTIONS[x]) ||GeneralConst.JOBSHEET.equals(OPTIONS[x])){
		    						lastRow.append("&").append("return").append("=").append("rpt");		    						
		    					}
		    					lastRow.append("' id='").append (OPTIONS[x]).append ("'");
//		    					if(GeneralConst.REPORT.equals(OPTIONS[x])){
//		    						lastRow.append(" class='printer tip' title='Print'");
//		    					}else if(GeneralConst.JOBSHEET.equals(OPTIONS[x])){
//		    						lastRow.append(" class='list tip' title='Print Job Sheet'");
//		    					}
//		    					
		    					lastRow.append(" class='paginate_button'");
		    					lastRow.append(" target='_blank'");
				    			lastRow.append(" rel='").append (key).append ("=").append ((String)mMap.get (key)).append ("' >");
				    			lastRow.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", OPTIONS[x], _clientBean.getLocale ())).append ("</a>");
				    			
		    				}
		    			}
		    			
		    		}
		    		lastRow.append(bufferHidden);	
		    		
			    	if(editable){
			    		rowArray.put(lastRow);
			    		mBuffer.append ("<td class='align_left tools center'>").append(lastRow).append ("</td>");
		    		}else{
		    			mBuffer.insert(mBuffer.length()-"</td>".length(), lastRow);
		    		}
		    		
		    		
		    		mBuffer.append ("</tr>");		
		    		dataArray.put(rowArray);
		    	}		
		    	
		    	
	    	}
			
//			json.put("sEcho", 1);
	    	json.put("iTotalRecords", keyList.size());
	    	json.put("iTotalDisplayRecords", keyList.size());
	    	json.put("aaData", dataArray);
		}catch(Exception e){
			throw e;			
		}
		return json;
	}
	
	public String buildSearchResult(Class defClass, String key, ArrayList searchDisplayList, ArrayList dataArrayList, 
			boolean editable, boolean withRowStatusField, String prefix) throws Exception {
		logger.debug("buildSearchResult key = "+key);
		HashMap mMap = null;
		String[] arrRow = {"class='rowEven'", "class='rowOdd'"};		
		StringBuffer mBuffer = new StringBuffer();
		StringBuffer bufferHidden = new StringBuffer();
		String value = null;
		int type = 0;
		String className = null;
		String shadowField = null;
		HashMap displayMap = null;
				
		prefix = (prefix==null)?"":prefix;
		DataObject dataObj = null;
		
		StringBuffer lastRow = new StringBuffer(); 
		StringBuffer firstColumnBuffer = new StringBuffer();
		StringBuffer columnBuffer = new StringBuffer();		
		String[] OPTIONS = (String[]) BeanDefUtil.getField(defClass, "OPTIONS");
		try{
//			if(!CommonUtil.isEmpty(className)){
//				arrRow[0] = "class='"+className+"'";
//				arrRow[1] = "class='"+className+"'";
//			}
			if(dataArrayList!=null && dataArrayList.size () > 0){		 				
		    	for(int i=0; i<dataArrayList.size (); i++){
		    		lastRow = new StringBuffer();
		    		bufferHidden = new StringBuffer();
		    		firstColumnBuffer = new StringBuffer();
		    		columnBuffer = new StringBuffer();
		    		
		    		mMap = (HashMap) dataArrayList.get (i);
//		    		logger.debug("mMap = "+mMap);
		    		mBuffer.append ("<tr ").append (arrRow[(i % 2)]).append (">");//
		    		for(int a=0; a<searchDisplayList.size(); a++){
		    			displayMap = (HashMap) searchDisplayList.get(a);
//		    			logger.debug("displayMap = "+displayMap);
		    			dataObj = (DataObject) displayMap.get("object");
		    			value = (String) mMap.get(dataObj.name);		    			
		    			type 	= CommonUtil.parseInt((String) displayMap.get("type"));
		    			className = (String) displayMap.get("classname");
		    			shadowField = (String) displayMap.get("shadow");
		    			
		    			if(!CommonUtil.isEmpty(className) && className.endsWith("autosuggest")){
		    				if(!dataObj.key){
		    					value= (String) mMap.get(dataObj.name+"_value");
		    				}else{
		    					value= (String) mMap.get(dataObj.name);
		    				}
		    			}
//		    			logger.debug("Name: "+dataObj.name +", Value: " + value);
		    			if(dataObj.key){
		    				firstColumnBuffer.append ("<th class='checkers'>");	
		    					firstColumnBuffer.append (AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, displayMap, prefix, value, GeneralConst.LISTING_FORM));
		    				firstColumnBuffer.append ("</th>");
		    				
		    			}else{
		    				if(!"Y".equalsIgnoreCase(shadowField)){
				    			if(type!=ComponentConst.HIDDEN){
				    				columnBuffer.append ("<td>");	
				    					columnBuffer.append (AdminFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, displayMap, prefix, value, GeneralConst.LISTING_FORM));
				    				columnBuffer.append ("</td>");
				    			}else{		    				
//				    				bufferHidden.append (WebFieldBuilder.buildField (_dbConnection, _shopInfoBean, _clientBean, displayMap, prefix, value));
				    			}
		    				}
		    			}
		    		}
		    		mBuffer.append(firstColumnBuffer).append(columnBuffer);
		    				    					    		
		    		if(withRowStatusField){
		    			bufferHidden.append ("<input type='hidden' name='").append(prefix).append("row-status' value='U' />");	
		    		}
		    		if(editable){		    			    			
		    			if(OPTIONS!=null){
		    				for(int x=0;x<OPTIONS.length;x++){
		    					lastRow.append("<a href='?");
		    					lastRow.append(GeneralConst.ACTION_NAME).append("=").append(OPTIONS[x]).append("&");
		    					lastRow.append(key).append("=").append((String)mMap.get (key));
		    					
		    					if(GeneralConst.REPORT.equals(OPTIONS[x])){
		    						lastRow.append(" class='icon printer' title='Print'").append((String)mMap.get (key));
		    					}else if(GeneralConst.JOBSHEET.equals(OPTIONS[x])){
		    						lastRow.append(" class='icon list' title='Print Job Sheet'").append((String)mMap.get (key));
		    					}
		    					
		    					if(GeneralConst.REPORT.equals(OPTIONS[x]) || GeneralConst.JOBSHEET.equals(OPTIONS[x])){
		    						lastRow.append("&").append("return").append("=").append("rpt");
		    					}
		    					lastRow.append("' id='").append (OPTIONS[x]).append ("'");
		    					lastRow.append(" target='_blank'");
				    			lastRow.append(" rel='").append (key).append ("=").append ((String)mMap.get (key)).append ("' >");
				    			lastRow.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", OPTIONS[x], _clientBean.getLocale ())).append ("</a>");
				    			lastRow.append(", ");
		    				}
		    			}
		    			lastRow.append("<a href='#' class='edit tip' title='edit' id='").append (GeneralConst.EDIT).append ("'");
		    			lastRow.append (" rel='").append (key).append ("=").append ((String)mMap.get (key)).append ("' >");
		    			lastRow.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.EDIT, _clientBean.getLocale ()));
		    			lastRow.append ("</a>");
		    		}
		    		lastRow.append(bufferHidden);	
		    		
			    	if(editable){
			    		mBuffer.append ("<td class='align_left tools center'>").append(lastRow).append ("</td>");
		    		}else{
		    			mBuffer.insert(mBuffer.length()-"</td>".length(), lastRow);
		    		}
		    		
		    		
		    		mBuffer.append ("</tr>");			    		
		    	}				    			    	
	    	}
		}catch(Exception e){
			throw e;			
		}
		return mBuffer.toString();
	}
	
	protected StringBuffer getSearchOptionLink(){
		StringBuffer mBuffer = new StringBuffer();
		
		mBuffer.append ("&nbsp;&nbsp;<input type='button' id='action.search' value='").append (ResourceUtil.getAdminResourceValue(null,"", "action.normal.search", _clientBean.getLocale ())).append ("' />");
		mBuffer.append ("&nbsp;");
		mBuffer.append ("<a href='#'");
		mBuffer.append (" id='").append ("action.show.more.search").append ("' rel='");	    					
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", "action.hide.more.search", _clientBean.getLocale ())).append ("'>");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", "action.show.more.search", _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		return mBuffer;
	}
	
	public StringBuffer getClientPagination(ArrayList mList, int recordPerPage, 
			StringBuffer mQuery, String pageNum, String idName)throws OwnException{
		logger.debug("getClientPagination idName = "+idName);
		StringBuffer mBuffer = new StringBuffer();		
//		HashMap mMap = null;		 
		String key = null;
		int pageNumber = 0;
		int linkPerPage = 10;
		int currentDivNumber = 0;
		int divNumber = 0;

		String className = "";
		String prevClassName = "";
		String nextClassName = "";
		
		StringBuffer mQueryString = null;
		StringBuffer idBuffer = new StringBuffer();
		
		String prevQuery = null;
		String nextQuery = null;
		
		StringBuffer contentBuffer = new StringBuffer();
		StringBuffer prevBuffer = new StringBuffer();
		StringBuffer nextBuffer = new StringBuffer();
		try{	
			if(mList.size()>0){
				idName = (idName==null)?"viewPageNumber":idName;
				pageNum = (pageNum==null)?"1":pageNum;
				currentDivNumber = Math.round ((Integer.parseInt (pageNum))/linkPerPage);
				
				if(Integer.parseInt (pageNum)%linkPerPage!=0) currentDivNumber++;
				
				mQuery = (mQuery==null)?new StringBuffer("?"):mQuery;
				
				if(mQuery.length ()>1){
					mQuery.append ("&");
				}		
				if(mQuery.indexOf(GeneralConst.ACTION_NAME)==-1){
					mQuery.append (GeneralConst.ACTION_NAME).append ("=").append (GeneralConst.RETRIEVE).append ("&");
				}
				
				if(currentDivNumber==1){
					prevClassName = "style='display: none'";
					contentBuffer.append ("<div id='pagee' style='position:relative;'>");
				}else{
					prevClassName = "";
					contentBuffer.append ("<div id='pagee' style='position:relative; display:none'>");
				}
	
				for(int i=0; i<mList.size (); i++){						
					mQueryString = new StringBuffer(mQuery.toString ());						
					pageNumber = (i / recordPerPage) + 1;
					divNumber = Math.round ((pageNumber)/linkPerPage);
					if(pageNumber%linkPerPage!=0) divNumber++;
					
	//				logger.debug (currentDivNumber + ":" + divNumber+"("+pageNumber+"), "+Math.round ((pageNumber)/linkPerPage));
					if((pageNumber % linkPerPage) == 1 && pageNumber!=1){
						contentBuffer.append ("</div>");
						
						if(currentDivNumber==divNumber){
							contentBuffer.append ("<div id='pagee' style='position:relative;'>");
						}else{
							contentBuffer.append ("<div id='pagee' style='position:relative; display:none'>");
						}
					}
																		
					key = (String) mList.get(i);
					
					// Retrieve ALL ID
					int x = 0;
					idBuffer = new StringBuffer();
					while(x<recordPerPage){
						idBuffer.append (key);												
						x++;
						if(i+x==mList.size ()) break;
						
						if(x<recordPerPage) idBuffer.append (",");
						key = (String) mList.get(i+x);	
					}
					i+=(recordPerPage-1);
					mQueryString.append ("id=").append (idBuffer).append ("&").append (GeneralConst.PAGE_NUM).append ("=").append (pageNumber);
					
	//				logger.debug (pageNumber + " % " + linkPerPage + " = "+(pageNumber % linkPerPage));
					
					if(pageNumber!=1){					
						if(prevQuery==null && (pageNumber % linkPerPage)==0){						
							prevQuery = mQueryString.toString ();
						}
					}
	//				logger.debug (currentDivNumber + ":" + divNumber);
					if((currentDivNumber+1)==divNumber){
	//					logger.debug (pageNumber + " % " + linkPerPage + " = "+(pageNumber % linkPerPage));
						if(nextQuery==null && (pageNumber % linkPerPage)==1){
							nextQuery = mQueryString.toString ();
						}
					}
					className = (pageNumber==Integer.parseInt (pageNum))?"selectedPage":"deSelectedPage";
					
					contentBuffer.append ("<a href='").append (mQueryString).append ("'");
						contentBuffer.append (" id='").append(idName).append("-").append (pageNumber).append ("'");
						contentBuffer.append (" class='").append (className).append ("'");
						if(idName.indexOf("viewPageNumber")!=-1){
							contentBuffer.append (" rel='address:/").append (mQueryString).append ("'");
						}else{
							contentBuffer.append (" rel='").append (mQueryString).append ("'");
						}
						contentBuffer.append (">");
						contentBuffer.append (pageNumber);
					contentBuffer.append ("</a>");
					
					if(i+1<mList.size () && (pageNumber % linkPerPage) != 0){
						contentBuffer.append ("&nbsp;|&nbsp;");
					}
				}						
				contentBuffer.append ("</div>");		
				
				if(currentDivNumber<divNumber){
					nextClassName = "";
				}else{
					nextClassName = "style='display: none'";
				}
				
				prevBuffer.append ("<span ").append (prevClassName).append ("><a href='").append (CommonUtil.nullToEmpty(prevQuery)).append ("' id='pagePrev'>");
					prevBuffer.append (ResourceUtil.getAdminResourceValue(null,"", "navigator.prev", _clientBean.getLocale ()));
				prevBuffer.append ("</a></span>&nbsp;&nbsp;");	
				nextBuffer.append ("&nbsp;&nbsp;<span ").append (nextClassName).append ("><a href='").append (CommonUtil.nullToEmpty(nextQuery)).append ("' id='pageNext'>");
					nextBuffer.append (ResourceUtil.getAdminResourceValue(null,"", "navigator.next", _clientBean.getLocale ()));
				nextBuffer.append ("</a></span>");			
				
				mBuffer.append ("<div class='paginationTable'>");
				mBuffer.append ("<table>");
					mBuffer.append ("<tr>");
						mBuffer.append ("<td class='navigatorPrev'>").append (prevBuffer).append ("</td>");
						mBuffer.append ("<td class='navigatorContent'>").append (contentBuffer).append ("</td>");
						mBuffer.append ("<td class='navigatorNext'>").append (nextBuffer).append ("</td>");
					mBuffer.append ("</tr>");
				mBuffer.append ("</table>");
				mBuffer.append ("</div>");	
			}
		}catch(Exception e){
			throw new OwnException(e);
		}
		return mBuffer;
	}

	protected StringBuffer buildSortableColumn(String module, String name, String mDirection){
		StringBuffer mBuffer = new StringBuffer();
		mDirection = (mDirection==null)?"":mDirection;
		
//		if(mDirection.equalsIgnoreCase ("ASC")){
//			mBuffer.append ("<th class='ui-state-active'>");
//		}else if(mDirection.equalsIgnoreCase ("DESC")){
//			mBuffer.append ("<th class='ui-state-active'>");
//		}else{
//			mBuffer.append ("<th class='ui-state-default'>");
//		}
		mBuffer.append ("<th>");
//    	mBuffer.append ("<a href='#'");
//    	mBuffer.append (" id='sortableField'");
//    	mBuffer.append (" rel='").append (name).append ("'>");
    	mBuffer.append (ResourceUtil.getAdminResourceValue(_resources, module, name, _clientBean.getLocale ())).append ("</th>");	
		
		return mBuffer;
	}
	
	protected StringBuffer buildNormalColumn(String module, String name){
		StringBuffer mBuffer = new StringBuffer();
//		if(className!=null){
//			mBuffer.append ("<th class='").append (className).append ("'>");
//		}else{
			mBuffer.append ("<th>");
//		}
    	mBuffer.append (ResourceUtil.getAdminResourceValue(_resources, module, name, _clientBean.getLocale ())).append ("</th>");	
		
		return mBuffer;
	}
	
	private StringBuffer getControlOptionBasic(){
		StringBuffer mBuffer = new StringBuffer();
//		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", "action.select", _clientBean.getLocale ())).append (": &nbsp;");
//		mBuffer.append ("<a href='#' id='").append (GeneralConst.CHECKALL).append ("' >");
//		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.CHECKALL, _clientBean.getLocale ()));
//		mBuffer.append ("</a>");
//		
//		mBuffer.append (", &nbsp;");
//			    			
//		mBuffer.append ("<a href='#' id='").append (GeneralConst.CHECKNONE).append ("' >");
//		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.CHECKNONE, _clientBean.getLocale ()));
//		mBuffer.append ("</a>");
		
		return mBuffer;
	}
	
	public StringBuffer getControlOption(boolean editable, String ModuleName){
		StringBuffer mBuffer = getControlOptionBasic();
		
		if(editable){			
			mBuffer.append ("<a href='#' class='paginate_button tip' title='Remove Record' id='").append (GeneralConst.DELETE).append ("'>");
			mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
			mBuffer.append ("</a>");

			mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.ADD_RECORD).append ("'>");
			mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
			mBuffer.append ("</a>");
			
			if(!ModuleName.equals(MFG_ReceivePaymentDef.MODULE_NAME) &&
					!ModuleName.equals(MFG_StatementDef.MODULE_NAME)					
					){
				mBuffer.append ("<a href='#' class='paginate_button tip' title='Copy Record' id='").append (GeneralConst.COPY).append ("'>");
				mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.COPY, _clientBean.getLocale ()));
				mBuffer.append ("</a>");			
			}
			
			if(ModuleName.equals(ShopBeanDef.MODULE_NAME)){
				mBuffer.append ("<a href='#' class='paginate_button tip' title='Build HTML' id='build.html'>");
				mBuffer.append ("Build HTML");
				mBuffer.append ("</a>");
			}
		}
		
		return mBuffer;
	}
	
	public StringBuffer getControlOptionNoDeleteNoCopy(){
		StringBuffer mBuffer = getControlOptionBasic();
				
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.ADD_RECORD).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
//		mBuffer.append ("<a href='#' class='paginate_button tip' title='Copy Record' id='").append (GeneralConst.COPY).append ("'>");
//		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.COPY, _clientBean.getLocale ()));
//		mBuffer.append ("</a>");
		return mBuffer;
	}
	
	public StringBuffer getControlOptionNoDelete(){
		StringBuffer mBuffer = getControlOptionBasic();
				
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.ADD_RECORD).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Copy Record' id='").append (GeneralConst.COPY).append ("'>");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.COPY, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		return mBuffer;
	}
	
	public StringBuffer getControlOptionRetrieveProduct(){
		StringBuffer mBuffer = new StringBuffer();
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete' id='").append (GeneralConst.CLONE_REMOVE).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Refresh All Products' id='").append (GeneralConst.CONTROL_RETRIEVE_PRODUCT).append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.CONTROL_RETRIEVE_PRODUCT, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		return mBuffer;
	}
	
	public StringBuffer getControlOptionCloneWithPopUpEntry(){
		StringBuffer mBuffer = getControlOptionBasic();
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete' id='").append (GeneralConst.CLONE_REMOVE).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.CLONE_ADD_CUSTOM).append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		return mBuffer;
	}
	
	public StringBuffer getControlOptionCloneNew(){
		StringBuffer mBuffer = getControlOptionBasic();
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete Record' id='").append (GeneralConst.CLONE_REMOVE).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.CLONE_ADD_NEW).append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		return mBuffer;
	}
	
	
	public StringBuffer getControlOptionClone(){
		StringBuffer mBuffer = getControlOptionBasic();
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete Record' id='").append (GeneralConst.CLONE_REMOVE).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.CLONE_ADD).append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		return mBuffer;
	}
	
	public StringBuffer getControlOptionCloneUpload(){
		StringBuffer mBuffer = getControlOptionBasic();
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete Record' id='").append (GeneralConst.CLONE_REMOVE).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.CLONE_UPLOAD).append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.CLONE_UPLOAD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		return mBuffer;
	}
	
	public StringBuffer getControlOptionCloneUploadAdd(){
		StringBuffer mBuffer = getControlOptionBasic();
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete Record' id='").append (GeneralConst.CLONE_REMOVE).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.CLONE_ADD).append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");	
		
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.CLONE_UPLOAD).append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.CLONE_UPLOAD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		return mBuffer;
	}
		
	public StringBuffer getControlOptionGroup(){
		StringBuffer mBuffer = getControlOptionBasic();
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete Record' id='").append (GeneralConst.CLONE_REMOVE).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.CLONE_ADD).append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
		mBuffer.append ("</a>");	
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add Group' id='").append (GeneralConst.CLONE_ADD+"group").append ("'");
		mBuffer.append (" >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD+"group", _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		
		return mBuffer;
	}
	
//	public StringBuffer getControlOptionAttach(String MODULE_NAME){
//		StringBuffer mBuffer = getControlOptionBasic();
//		
////		mBuffer.append (", &nbsp;");
//		
//		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete Record' id='").append (GeneralConst.REMOVE_DETAIL).append ("' >");
//		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
//		mBuffer.append ("</a>");
//		
////		mBuffer.append (", &nbsp;");
//		
//		mBuffer.append ("<a href='#' class='paginate_button tip' title='Add New Record' id='").append (GeneralConst.ADD_DETAIL).append ("'");
//		mBuffer.append (" rel='").append(MODULE_NAME).append("'>");
//		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.ADD_RECORD, _clientBean.getLocale ()));
//		mBuffer.append ("</a>");
//		
//		
//		return mBuffer;
//	}
	
	public StringBuffer getControlOptionWithUpload(){
		StringBuffer mBuffer = getControlOptionBasic();
		
//		mBuffer.append (", &nbsp;");
		
		mBuffer.append ("<span>");
		mBuffer.append ("<a href='#' class='paginate_button tip' title='Delete Record' id='").append (GeneralConst.CLONE_REMOVE).append ("' >");
		mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", GeneralConst.DELETE, _clientBean.getLocale ()));
		mBuffer.append ("</a>");
		
		return mBuffer;
	}
	
	protected StringBuffer getPaginationControl(){
		StringBuffer mBuffer = new StringBuffer();
		mBuffer.append ("<table id='paginationTable' class='ui-state-active'>");
		mBuffer.append ("<col>");
		mBuffer.append ("<col width='150'>");
		mBuffer.append ("<tbody>");
		mBuffer.append ("<tr>");
		mBuffer.append ("<td align='right' id='paginationDiv'></td>");	
		mBuffer.append ("<td align='right' nowrap>");	
			mBuffer.append (ResourceUtil.getAdminResourceValue(null,"", "show", _clientBean.getLocale ()));
			mBuffer.append (": <select id='").append (GeneralConst.TOTAL_RECORD_PER_PAGE).append ("'");
			mBuffer.append (" name='").append (GeneralConst.TOTAL_RECORD_PER_PAGE).append ("'>");
			mBuffer.append ("<option value='25' selected='selected'>25</option>");
			mBuffer.append ("<option value='50'>50</option>");
			mBuffer.append ("<option value='100'>100</option>");			
			mBuffer.append ("</select>");
			mBuffer.append ("&nbsp;&nbsp;");
		mBuffer.append ("</td>");
		
		mBuffer.append ("</tr>");
		mBuffer.append ("</tbody>");
		mBuffer.append ("</table>");		

		return mBuffer;
	}
		
	private String getCurrentDate(){
		Calendar cal = GregorianCalendar.getInstance ();
	    SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
	    
	    return dateFormat.format (cal.getTime ());
	}
}
