package com.forest.common.builder;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.ValidationConst;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.OwnException;
import com.forest.common.util.SelectBuilder;

public class AdminFieldBuilder {
	private static Logger logger = Logger.getLogger(AdminFieldBuilder.class);
	public static StringBuffer getActionField(String value){
		StringBuffer mBuffer = new StringBuffer();
		mBuffer.append ("<input type='hidden'");
		mBuffer.append (" id='").append (GeneralConst.ACTION_NAME).append ("'");
		mBuffer.append (" name='").append (GeneralConst.ACTION_NAME).append ("' value='").append (value).append ("' />");		
		
		return mBuffer;
	}
	
	public static String buildField(Connection conn, ShopInfoBean shopInfoBean, ClientBean clientBean,
			HashMap displayObjectMap, String prefix, String customValue, String formType) throws OwnException{
		
		// formType = ADD, SEARCH, LISTING
		StringBuffer mReturn = new StringBuffer();
//		logger.debug("buildField displayObjectMap = "+displayObjectMap);
		DataObject mObject 	= (DataObject) displayObjectMap.get("object");	    	
		int maxLength = 0;
		int size 			= CommonUtil.parseInt((String) displayObjectMap.get("size"));  		
		int componentConst 	= CommonUtil.parseInt((String) displayObjectMap.get("type"));
		String selectXref	= (String) displayObjectMap.get("xref");
		Class selectClass	= (Class)  displayObjectMap.get("xrefclass");	
		String cssClass		= (String) displayObjectMap.get("validate");
		String className	= (String) displayObjectMap.get("classname");
		String value 		= (String) displayObjectMap.get("default_value");				
		String htmlOption	= (String) displayObjectMap.get("htmloption");
		String as	= (String) displayObjectMap.get("as");	
		String physical		= (String) displayObjectMap.get("shadow");
		
		// Shadow field is for display purpose only. if 
		if("Y".equals(physical) && componentConst==ComponentConst.HIDDEN){
			return "";
		}
		Class[] paramType = null;
		Object[] paramValue = null;				
		Method mthd = null;			
		cssClass = (cssClass==null)?"":cssClass;

		if(!CommonUtil.isEmpty(cssClass))
			cssClass = ValidationConst.getValidate(cssClass);
		
		if(!CommonUtil.isEmpty(className)){
			if(!CommonUtil.isEmpty(cssClass)){
				cssClass += " "+className;
			}else{
				cssClass += className;
			}
		}				
		
		String fieldName = null;
		if(!CommonUtil.isEmpty(prefix)){
			fieldName = prefix + ((as==null)?mObject.name:as);
		}else{
			fieldName = ((as==null)?mObject.name:as);
		}
		if(!CommonUtil.isEmpty(customValue)){
			value = customValue;
		}
		value = (value==null)?"":value;
				
		if(mObject!=null){			
			if(GeneralConst.SEARCH_FORM.equals(formType) && componentConst==ComponentConst.AUTO_SUGGEST){
				componentConst=ComponentConst.TEXTBOX;
				cssClass = null;
			}
			switch(componentConst){				
				case ComponentConst.HIDDEN:					
					mReturn.append ("<input type='hidden'");	
					mReturn.append (" id='").append (fieldName).append ("'");
					mReturn.append (" name='").append (fieldName).append ("'");
					mReturn.append (" value='").append (value).append ("'");			
//					if(cssClass!=null){
//						mReturn.append (" class='").append (cssClass).append ("'");
//					}
					mReturn.append ("/>");
					break;
				case ComponentConst.PASSWORD:					
					mReturn.append ("<input type='password'");	
					mReturn.append (" id='").append (fieldName).append ("'");
					mReturn.append (" name='").append (fieldName).append ("'");
					mReturn.append (" value='").append (value).append ("'");
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					mReturn.append (" size='").append (size).append ("'");
					mReturn.append (" maxlength='").append (mObject.length).append ("'");
					mReturn.append ("/>");					
					break;
				case ComponentConst.AUTO_SUGGEST:							
					mReturn.append ("<input type='hidden'");						
					mReturn.append (" name='").append (fieldName).append ("'");
					mReturn.append ("/>");					
					fieldName += "_value";										
					// Create Extra Hidden Field				
				case ComponentConst.TEXTBOX:									
					mReturn.append ("<input type='text'");	
					mReturn.append (" id='").append (fieldName).append ("'");
					mReturn.append (" name='").append (fieldName).append ("'");
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					
					if(mObject.type == Types.DOUBLE){
						if(size==0){
							size = mObject.length + mObject.precision + 1;
						}						
						maxLength = mObject.length + mObject.precision;
					}else if(mObject.type == Types.DATE){
						size = 10;
						maxLength = size;
					}else if(mObject.type == Types.CLOB){		
						size = 30;
						maxLength = size;
					}else{
						if(size==0){
							size = mObject.length;
						}						
						maxLength = mObject.length;
					}
					
					mReturn.append (" size='").append (size).append ("'");
					if(componentConst!=ComponentConst.AUTO_SUGGEST){
						mReturn.append (" maxlength='").append (maxLength).append ("'");			
					}
					mReturn.append (" value='").append (value).append ("'");
					if(!CommonUtil.isEmpty(htmlOption)){
						mReturn.append (" ").append (htmlOption).append(" ");	
					}
					mReturn.append ("/>");					
					break;
				
				case ComponentConst.CHECKBOX:
					mReturn.append ("<input type='checkbox'");	
					mReturn.append (" id='").append (fieldName).append ("'");
					mReturn.append (" name='").append (fieldName).append ("'");
					mReturn.append (" value='").append (value).append ("'");
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					if(!CommonUtil.isEmpty(htmlOption)){
						mReturn.append (" ").append (htmlOption).append(" ");	
					}
					mReturn.append ("/>");	
					break;
				case ComponentConst.RADIO:
					mReturn.append ("<input type='radio'");	
					mReturn.append (" id='").append (fieldName).append ("'");
					mReturn.append (" name='").append (fieldName).append ("'");
					mReturn.append (" value='").append (value).append ("'");
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					if(!CommonUtil.isEmpty(htmlOption)){
						mReturn.append (" ").append (htmlOption).append(" ");	
					}
					mReturn.append ("/>");	
					break;
				case ComponentConst.TEXTAREA:
					mReturn.append ("<textarea");	
					mReturn.append (" id='").append (fieldName).append ("'");
					mReturn.append (" name='").append (fieldName).append ("'");
//					mReturn.append (" cols='").append (cols).append ("'");
//					mReturn.append (" rows='").append (rows).append ("'");		
					
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					if(!CommonUtil.isEmpty(htmlOption)){
						mReturn.append (" ").append (htmlOption).append(" ");	
					}
					mReturn.append (">").append(value).append("</textarea>");	
					break;
				case ComponentConst.IMAGE:
					mReturn.append ("<img");
					mReturn.append (" id='").append (fieldName).append ("'");
					mReturn.append (" name='").append (fieldName).append ("'");
					mReturn.append (" src='");			    				
    				if(CommonUtil.isEmpty(value)){
    					mReturn.append("' ");
    				}else{
    					mReturn.append(value).append("' ");	
    				}
    			
    				if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
    				
    				if(!CommonUtil.isEmpty(htmlOption)){
						mReturn.append (" ").append (htmlOption).append(" ");	
					}
    				mReturn.append(" />");
    				
    				if(GeneralConst.ENTRY_FORM.equals(formType)){
						mReturn.append ("<input type='hidden'");						
						mReturn.append (" name='").append (fieldName).append ("'");
						mReturn.append (" value='").append (value).append ("'");			
						mReturn.append ("/>");
					}
    				
    				break;
				case ComponentConst.SELECT:
					paramType = new Class[4];
					paramValue = new Object[4];				
								
					try{
						if(!CommonUtil.isEmpty(className)){
							if(className.indexOf("currentmonth")!=-1){
								value = DateUtil.getCurrentMonth();
							}else if(className.indexOf("currentyear")!=-1){
								value = DateUtil.getCurrentYear();
							}
						}
						paramType[0] = Connection.class;
						paramType[1] = ShopInfoBean.class;
						paramType[2] = String.class;
						paramType[3] = String.class;						
						
						
						paramValue[0] = conn;
						paramValue[1] = shopInfoBean;
						paramValue[2] = fieldName;
						paramValue[3] = value;						
						
						if(selectClass!=null){							
							mthd = selectClass.getDeclaredMethod("getSELECT_"+selectXref, paramType);
//							cssClass += " " + selectXref + "-" + selectClass.getName().substring(selectClass.getName().lastIndexOf(".")+1) + "-dynamicselect";
						}else{
							mthd = SelectBuilder.class.getDeclaredMethod("getSELECT_"+selectXref, paramType);								
						}
						
						
						mReturn.append ((StringBuffer) mthd.invoke( SelectBuilder.class, paramValue));	
						if(cssClass!=null){
							mReturn.insert(mReturn.indexOf(">"), " class='"+cssClass+"'");
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
				case ComponentConst.TEXT:
				default:
					if(!CommonUtil.isEmpty(selectXref) && !CommonUtil.isEmpty(value) && selectXref.indexOf("AUTO-")==-1){
						paramType = new Class[2];
						paramValue = new Object[2];
						
						paramType[0] = Connection.class;
						paramType[1] = ShopInfoBean.class;
						
						paramValue[0] = conn;
						paramValue[1] = shopInfoBean;
												
						
						try{
							if(selectClass!=null){
								mthd = selectClass.getDeclaredMethod("getHASH_"+selectXref, paramType);
							}else{
								mthd = SelectBuilder.class.getDeclaredMethod("getHASH_"+selectXref, paramType);	
							}															
							HashMap fieldMap = (HashMap)mthd.invoke (SelectBuilder.class, paramValue);
//							logger.debug("selectXref:"+selectXref+", fieldMap:"+fieldMap);
							if(fieldMap.get(value).getClass()==String.class){
								value = (String) fieldMap.get(value);
							}else{
								value = (String) ((HashMap) fieldMap.get(value)).get(value);
							}									    				
						}catch(Exception e){
							logger.error(e, e);
						}
					}
					if(fieldName.endsWith(BaseDef.UPDATEDATE)){
						value = DateUtil.displayDateTime(value, clientBean);
					}
					mReturn.append ("<span");
//					mReturn.append (" id='").append (fieldName).append ("'"); // cannot have id, will cause salesdate not working properly
					mReturn.append (" name='").append (fieldName).append ("'");
					if(cssClass!=null && cssClass.indexOf("validate")==-1){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					mReturn.append (">").append (value).append ("</span>");
					
					if(GeneralConst.ENTRY_FORM.equals(formType)){
						mReturn.append ("<input type='hidden'");						
						mReturn.append (" name='").append (fieldName).append ("'");
						mReturn.append (" value='").append (value).append ("'");			
						mReturn.append ("/>");
					}
					break;
			}			
		}
//		logger.debug("buildField: "+mReturn);
		return mReturn.toString ();
	}
}
