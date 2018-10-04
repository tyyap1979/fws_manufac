package com.forest.common.util;
 
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.ValidationConst;
import com.forest.common.servlet.BaseServlet;

public class FieldBuilder
{	
//	private Locale _locale = null;
	private Logger logger = Logger.getLogger (this.getClass ());
	
	public FieldBuilder(Locale locale){
//		_locale = locale;		
	}
	
	public String buildField(DataObject mObject, int componentConst) throws OwnException{
		return buildField(mObject, componentConst, 0);
	}
	
	public StringBuffer buildSelect(String fieldName, ArrayList mData, String keyName, String valueName, 
			String firstLineValue, String selectedValue){
		return buildSelect (fieldName, mData, keyName, valueName, firstLineValue, selectedValue, true, null);
	}
	
	public StringBuffer buildSelect(String fieldName, ArrayList mData, String keyName, String valueName, 
			String firstLineValue, String selectedValue, boolean readResource, String validationClass){
		return buildSelect (fieldName, mData, keyName, valueName, firstLineValue, selectedValue, readResource, validationClass, true);
	}
	
	public StringBuffer buildSelect(String fieldName, ArrayList mData, String keyName, String valueName, 
			String firstLineValue, String selectedValue, boolean readResource, String validationClass, boolean autoComplete){
		
		StringBuffer mBuffer = new StringBuffer();
		HashMap mRow = null;
		String[] fields = null;
		int x=0;
		String value = null;
		String keyValue = null;
		
		try{
			if(valueName.indexOf (",")!=-1){
				fields = valueName.split (",");
			}else{
				fields = new String[1];
				fields[0] = valueName;
			}
			
			mBuffer.append ("<select");
			if(autoComplete==false){
				mBuffer.append (" autocomplete='off'");	
			}
			mBuffer.append (" name='").append (fieldName).append ("'");
			if(validationClass!=null){
				mBuffer.append (" class='").append (validationClass).append ("'");
			}
			mBuffer.append (" id='").append (fieldName).append ("'>");
			
			if(firstLineValue!=null){
				mBuffer.append ("<option value=''>").append (firstLineValue).append ("</option>");
			}
			if(mData!=null){			
				for(int i=0; i<mData.size ();i++){
					mRow = (HashMap) mData.get (i);			
					try{
						keyValue = (String) mRow.get (keyName);
						if(keyValue.indexOf (BaseServlet.START_TAG)!=-1){
							keyValue = URLEncoder.encode (keyValue, "UTF-8");
						}
						
						if(selectedValue!=null && selectedValue.equals (mRow.get (keyName))){
							mBuffer.append ("<option value='").append (keyValue).append ("' selected='selected'>");
						}else{
							mBuffer.append ("<option value='").append (keyValue).append ("'>");	
						}
					}catch(UnsupportedEncodingException e){
						logger.error (e);
					}
					for(x=0; x<fields.length; x++){
//						if(readResource){
//							value = ResourceUtil.getShopCustomResourceValue (GeneralConst.ADMIN_NAME, (String) mRow.get (fields[x]), _locale);
//						}else{
							value = (String) mRow.get (fields[x]);	
//						}
						
						value = (value==null)?fields[x]:value;
						
						mBuffer.append (value);
						if((x+1)!=fields.length){
							mBuffer.append (" ");
						}
					}
					mBuffer.append ("</option>");
				}
			}
			mBuffer.append ("</select>");
		}catch(Exception e){
			logger.error (e, e);
		}
		return mBuffer;
	}
	
//	public StringBuffer buildSelect(DataObject mBean, ArrayList mData, String keyName, String valueName){
//		
//	}
	
//	public StringBuffer buildSelectStatus(DataObject mBean){
//		StringBuffer mBuffer = new StringBuffer();
//		mBuffer.append ("<select");
//		mBuffer.append (" name='").append (mBean.name).append ("'");
//		mBuffer.append (" id='").append (mBean.name).append ("'>");
//		mBuffer.append ("<option value='A'>Active</option>");
//		mBuffer.append ("<option value='I'>Inactive</option>");
//		mBuffer.append ("<option value='D'>Deleted</option>");
//		mBuffer.append ("</select>");
//		return mBuffer;
//	}
	public String buildTextAreaField(DataObject mObject, int row, int col) throws OwnException{
		return buildTextAreaField (mObject.name, mObject.stringValue, row, col);
	}
	
	public String buildTextAreaField(String name, String text, int row, int col) throws OwnException{
		StringBuffer mReturn = new StringBuffer();
		if(name!=null){
			String value = (text==null)?"":text;						
			mReturn.append ("<textarea rows='").append (row).append ("' cols='").append (col).append ("' ");						
			mReturn.append (" id='").append (name).append ("'");
			mReturn.append (" name='").append (name).append ("'");			
			mReturn.append (">");
			mReturn.append (value);	
			mReturn.append ("</textarea>");			
		}
		
		return mReturn.toString ();
	}
	
	public String buildTextAreaField(String name, String text) throws OwnException{
		StringBuffer mReturn = new StringBuffer();
		if(name!=null){
			String value = (text==null)?"":text;						
			mReturn.append ("<textarea");						
			mReturn.append (" id='").append (name).append ("'");
			mReturn.append (" name='").append (name).append ("'");			
			mReturn.append (">");
			mReturn.append (value);	
			mReturn.append ("</textarea>");			
		}
		
		return mReturn.toString ();
	}
	
	public String buildLink(String id, String href, String rel, String value) throws OwnException{
		StringBuffer mReturn = new StringBuffer();
		mReturn.append ("<a href='").append (href).append ("'");
		
		if(rel!=null) mReturn.append (" rel='").append (rel).append ("'");
		if(id!=null) mReturn.append (" id='").append (id).append ("'");
				
		mReturn.append (">").append (value).append ("</a>");
		
		return mReturn.toString ();
	}
	
	public String buildFormObject(String type, String id, String name, String value){
		return buildFormObject (type, id, name, value, null, null);
	}
	
	public String buildFormObject(String type, String id, String name, String value, String size){
		return buildFormObject (type, id, name, value, size, null);
	}
	
	public String buildFormObject(String type, String id, String name, String value, String size, String validationClass){
		StringBuffer mBuffer = new StringBuffer();
		mBuffer.append ("<input type='").append (type).append ("'");
		mBuffer.append (" name='").append (name).append ("'");
		mBuffer.append (" id='").append (id).append ("'");
		if(validationClass!=null){
			mBuffer.append (" class='").append (validationClass).append ("'");
		}
		if(size!=null)
			mBuffer.append (" size='").append (size).append ("'");
		mBuffer.append (" value='").append (value).append ("' />");	
		
		return mBuffer.toString ();
	}
	
	public String buildRadio(String name, String value, boolean selected){
		StringBuffer mBuffer = new StringBuffer();
		mBuffer.append ("<input type='radio'");
		mBuffer.append (" name='").append (name).append ("'");
		mBuffer.append (" id='").append (name).append ("'");
		if(selected)
			mBuffer.append (" checked='checked'");
		mBuffer.append (" value='").append (value).append ("' />");	
		
		return mBuffer.toString ();
	}
	
	public String buildField(DataObject mObject, int componentConst, int size) throws OwnException{
		return buildField(mObject, componentConst, size, null);
	}
	public String buildField(DataObject mObject, int componentConst, int size, String cssClass) throws OwnException{
		return buildField(mObject, componentConst, size, cssClass, "");
	}
	public String buildField(DataObject mObject, int componentConst, int size, String cssClass, String value) throws OwnException{
		StringBuffer mReturn = new StringBuffer();
		
		if(mObject!=null){			
			switch(componentConst){
				case ComponentConst.TEXT:
					mReturn.append (value);					
					break;
				case ComponentConst.HIDDEN:					
					mReturn.append ("<input type='hidden'");	
					mReturn.append (" id='").append (mObject.name).append ("'");
					mReturn.append (" name='").append (mObject.name).append ("'");
					mReturn.append (" value='").append (value).append ("'");					
					mReturn.append ("/>");
					break;
				case ComponentConst.PASSWORD:					
					mReturn.append ("<input type='password'");	
					mReturn.append (" id='").append (mObject.name).append ("'");
					mReturn.append (" name='").append (mObject.name).append ("'");
					mReturn.append (" value='").append (value).append ("'");
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					mReturn.append (" size='").append (size).append ("'");
					mReturn.append (" maxlength='").append (mObject.length).append ("'");
					mReturn.append ("/>");
					break;
				case ComponentConst.TEXTBOX:					
					mReturn.append ("<input type='text'");	
					mReturn.append (" id='").append (mObject.name).append ("'");
					mReturn.append (" name='").append (mObject.name).append ("'");
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					if(mObject.type == Types.DOUBLE){
						if(size==0){
							size = mObject.length + mObject.precision;
						}
						mReturn.append (" size='").append (size + 1).append ("'");
						mReturn.append (" maxlength='").append (mObject.length + mObject.precision + 1).append ("'");
					}else if(mObject.type == Types.DATE){
						size = 10;
						mReturn.append (" size='").append (size + 1).append ("'");
						mReturn.append (" maxlength='").append (size).append ("'");
					}else if(mObject.type == Types.CLOB){		
						size = 30;
						mReturn.append (" size='").append (size + 1).append ("'");
						mReturn.append (" maxlength='").append (size).append ("'");
					}else{
						if(mObject.type == Types.INTEGER){
							mReturn.append (" size='").append ("10").append ("'");
							mReturn.append (" maxlength='").append ("10").append ("'");
						}else{
							if(size==0){
								size = mObject.length;
							}
							mReturn.append (" size='").append (size).append ("'");
							mReturn.append (" maxlength='").append (mObject.length).append ("'");
						}
						
					}
					
					mReturn.append (" value='").append (value).append ("'");
					mReturn.append ("/>");					
					
					break;
				
				case ComponentConst.CHECKBOX:
					mReturn.append ("<input type='checkbox'");	
					mReturn.append (" id='").append (mObject.name).append ("'");
					mReturn.append (" name='").append (mObject.name).append ("'");
					mReturn.append (" value='").append (value).append ("'");
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					mReturn.append ("/>");	
					break;
				case ComponentConst.RADIO:
					mReturn.append ("<input type='radio'");	
					mReturn.append (" id='").append (mObject.name).append ("'");
					mReturn.append (" name='").append (mObject.name).append ("'");
					mReturn.append (" value='").append (value).append ("'");
					if(cssClass!=null){
						mReturn.append (" class='").append (cssClass).append ("'");
					}
					mReturn.append ("/>");	
					break;
				default:
					throw new OwnException("9999");	
			}
				
		}
		return mReturn.toString ();
	}
	
	public StringBuffer buildDate(Locale locale, String dayName, int dayValue, String monthName, int monthValue, String yearName, int yearValue){
		StringBuffer mBuffer = new StringBuffer();		
		Calendar mCal = GregorianCalendar.getInstance ();
		int currentYear = mCal.get (Calendar.YEAR);
		int untilYear = currentYear - 100;
		
		mBuffer.append ("<select name='").append (dayName).append ("' id='").append (dayName).append ("'");
		mBuffer.append (" class='").append (ValidationConst.REQUIRED).append ("'");
		mBuffer.append (">");
		mBuffer.append ("<option value=''>").append (ResourceUtil.getMallResourceValue (null, "display.day", locale)).append ("</option>");
		for(int i=1;i<=31; i++){			
			if(dayValue==i){
				mBuffer.append ("<option value='").append (i).append ("' selected>").append (i).append ("</option>");
			}else{
				mBuffer.append ("<option value='").append (i).append ("'>").append (i).append ("</option>");
			}
		}
		mBuffer.append ("</select>&nbsp;");
		
		mBuffer.append ("<select name='").append (monthName).append ("' id='").append (monthName).append ("'");
		mBuffer.append (" class='").append (ValidationConst.REQUIRED).append ("'");
		mBuffer.append (">");
		mBuffer.append ("<option value=''>").append (ResourceUtil.getMallResourceValue (null, "display.month", locale)).append ("</option>");
		for(int i=1;i<=12; i++){
			if(monthValue==i){
				mBuffer.append ("<option value='").append (i).append ("' selected>").append (i).append ("</option>");
			}else{
				mBuffer.append ("<option value='").append (i).append ("'>").append (i).append ("</option>");
			}
		}
		mBuffer.append ("</select>&nbsp;");
		
		mBuffer.append ("<select name='").append (yearName).append ("' id='").append (yearName).append ("'");
		mBuffer.append (" class='").append (ValidationConst.REQUIRED).append ("'");
		mBuffer.append (">");
		mBuffer.append ("<option value=''>").append (ResourceUtil.getMallResourceValue (null, "display.year", locale)).append ("</option>");
		for(int i=currentYear;i>=untilYear; i--){
			if(yearValue==i){
				mBuffer.append ("<option value='").append (i).append ("' selected>").append (i).append ("</option>");
			}else{
				mBuffer.append ("<option value='").append (i).append ("'>").append (i).append ("</option>");
			}
		}
		mBuffer.append ("</select>");
		
		return mBuffer;
	}
	
	
}
