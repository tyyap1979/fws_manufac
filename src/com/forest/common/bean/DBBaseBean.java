package com.forest.common.bean;

import java.lang.reflect.Method;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.util.ResourceUtil;

public class DBBaseBean
{
	private Logger logger = Logger.getLogger (this.getClass ());
	protected DataObject companyid = null;
//	protected DataObject id = null;
//	protected DataObject createBy = null;
//	protected DataObject createdate = null;
	protected DataObject updateBy = null;
	protected DataObject updatedate = null;
	
	public static final String COMPANYID = "companyid";
//	public static final String ID = "id";
//	public static final String CREATEBY = "createby";
//	public static final String CREATEDATE = "createdate";	
	public static final String UPDATEBY = "updateby";
	public static final String UPDATEDATE = "updatedate";
	
	protected final String FIELD_KEY_CLASS = "fieldKey";	
	
	protected DBBaseBean (){
		companyid 	= new DataObject(COMPANYID, Types.VARCHAR, 	false, 30, 0);
//		createBy 	= new DataObject(CREATEBY, Types.VARCHAR, true, 50, 0);
//		createdate 	= new DataObject(CREATEDATE, Types.DATE, true, 0, 0);
		updateBy 	= new DataObject(UPDATEBY, Types.VARCHAR, true, 50, 0);
		updatedate 	= new DataObject(UPDATEDATE, Types.DATE, true, 0, 0);		
//		id.autoIncrement = true;
	}
	
	
	
	protected String arrayToString(String[] pArray, String dataType){
		StringBuffer mBuffer = new StringBuffer();
		
		if(pArray!=null){				
			for(int i=0; i<pArray.length; i++){
				if("STRING".equals (dataType)){
					mBuffer.append ("'").append (pArray[i]).append ("'");
				}else{
					mBuffer.append (pArray[i]);
				}
				if((i+1) < pArray.length){
					mBuffer.append (",");
				}
			}
		}
		logger.debug ("arrayToString: "+mBuffer.toString ());
		return mBuffer.toString ();
	}
	
	protected StringBuffer createTable(String pTableName, ArrayList pColumnList, String pOther){
		DataObject mColumnObject = null;
		StringBuffer m_oQuery = new StringBuffer();
		m_oQuery.append ("CREATE TABLE IF NOT EXISTS ").append (pTableName);
		m_oQuery.append ("(");
		
		for(int i=0; i<pColumnList.size (); i++){
			mColumnObject = (DataObject) pColumnList.get (i);
			m_oQuery.append (mColumnObject.name).append (" ");
			m_oQuery.append (type2String(mColumnObject.type));
						
			if(mColumnObject.length != 0){
				m_oQuery.append (" ");
				m_oQuery.append ("(").append (mColumnObject.length);
				
				if(mColumnObject.precision != 0){
					m_oQuery.append (",").append (mColumnObject.precision);
				}
				m_oQuery.append (")");
			}			
			
			if(mColumnObject.nullable == false){
				m_oQuery.append (" ");
				m_oQuery.append ("not null");
			}
			
			if(mColumnObject.autoIncrement){
				m_oQuery.append (" ");
				m_oQuery.append ("AUTO_INCREMENT");
			}
			
			if((i + 1) < pColumnList.size ()){
				m_oQuery.append (", ");
			}
		}
		if(pOther != null && !pOther.equals ("")){
			m_oQuery.append (", ").append (pOther);
		}
		
		m_oQuery.append (")");		
		
		logger.debug ("Create Table: \n "+ m_oQuery.toString ());
		return m_oQuery;
	}
	
	private String type2String(int sqlType){
		String mReturn = "";
		switch(sqlType){
			case Types.VARCHAR:
				mReturn = "VARCHAR";
				break;
			case Types.INTEGER:
				mReturn = "INT";
				break;
			case Types.CLOB:
				mReturn = "TEXT";
				break;
			case Types.DATE:
				mReturn = "DATE";
				break;
			case Types.DOUBLE:
				mReturn = "DOUBLE";
				break;
				
		}
		return mReturn;
	}

//	public DataObject getCreateBy ()
//	{
//		return createBy;
//	}
//
//	public void setCreateBy (String createBy)
//	{
//		this.createBy.stringValue = createBy;
//	}
//
//	public DataObject getCreatedate ()
//	{
//		return createdate;
//	}
//
//	public void setCreateDate (String createdate)
//	{
//		this.createdate.stringValue = createdate;
//	}

	public DataObject getUpdateBy ()
	{
		return updateBy;
	}

	public void setUpdateBy (String updateBy)
	{
		this.updateBy.stringValue = updateBy;
	}

	public DataObject getUpdatedate ()
	{
		return updatedate;
	}

	public void setUpdatedate (String updatedate)
	{
		this.updatedate.stringValue = updatedate;
	}

	public DataObject getCompanyid ()
	{
		return companyid;
	}

	public void setCompanyid (String companyid)
	{
		this.companyid.stringValue = companyid;
	}
	
	public DataObject getDataObject(String name){
		DataObject mDataObject = null;
		Method mthd = null;
		Class[] paramType = null;
		Object[] paramValue = null;
		try{
			if(name!=null && !"".equals (name)){
				name = name.substring (0,1).toUpperCase () + name.substring (1, name.length ());
				mthd = this.getClass ().getMethod ("get"+name, paramType);
				mDataObject = (DataObject) mthd.invoke (this, paramValue);
			}
		}catch(Exception e){
			logger.error (e, e);
		}
		return mDataObject;
	}	
	
//	private static final String TEXT_FIELD =  "^(\\S)(.){1,75}(\\S)$";
	private static final String NON_NEGATIVE_INTEGER_FIELD = "(\\d){1,9}";
	private static final String INTEGER_FIELD = "(-)?" + NON_NEGATIVE_INTEGER_FIELD;
	private static final String NON_NEGATIVE_FLOATING_POINT_FIELD = "(\\d){1,10}\\.(\\d){1,10}";
	private static final String FLOATING_POINT_FIELD =  "(-)?" + NON_NEGATIVE_FLOATING_POINT_FIELD;
	private static final String DATE_FIELD = "\\d{4}-\\d{1,2}-\\d{1,2}";
	
	public StringBuffer validateBean(ShopInfoBean shopInfoBean, String module, Locale locale, String[] ignoreFields) throws Exception{
		StringBuffer errorMsg = new StringBuffer();
		StringBuffer fieldBuffer = new StringBuffer();
		StringBuffer resultBuffer = new StringBuffer();
		
		DataObject obj = new DataObject();		
		Method[] methods = this.getClass ().getDeclaredMethods ();
		Object[] paramValue = null;
		
		int x = 0;
		boolean found = false;
		try{
			for(int i=0; i<methods.length; i++){
				if(methods[i].getReturnType ()==DataObject.class){														
					obj = (DataObject) methods[i].invoke (this, paramValue);
					
					found = false;
					if(ignoreFields!=null && ignoreFields.length>0){
						for(x=0; x<ignoreFields.length; x++){	
							if(ignoreFields[x].equals (obj.name)){
								found = true;
								break;
							}
						}
					}
					if(found) continue;
					
					fieldBuffer = new StringBuffer();
					errorMsg = new StringBuffer();
					
					fieldBuffer.append ("<span class=\"").append (FIELD_KEY_CLASS).append ("\">");
						fieldBuffer.append (ResourceUtil.getAdminResourceValue (shopInfoBean.getBusiness(), module, obj.name, locale));
					fieldBuffer.append (" </span>");
					
					if(obj.nullable==false && (obj.stringValue==null || obj.stringValue.length ()==0)){						
						errorMsg.append (ResourceUtil.getAdminResourceValue(shopInfoBean.getBusiness(),"", "field.cannot.null", locale));					
					}
					
					if(obj.stringValue!=null && !"".equals (obj.stringValue)){
						if(errorMsg.length ()==0 && obj.type==Types.DATE){
							if(!Pattern.matches(DATE_FIELD, obj.stringValue)){
								errorMsg.append (ResourceUtil.getAdminResourceValue(shopInfoBean.getBusiness(),"", "field.invalid.date", locale));					
							}
						}
						if(errorMsg.length ()==0 && obj.type==Types.INTEGER){
							if(!Pattern.matches(INTEGER_FIELD, obj.stringValue)){
								errorMsg.append (ResourceUtil.getAdminResourceValue(shopInfoBean.getBusiness(),"", "field.invalid.integer", locale));					
							}
						}
						if(errorMsg.length ()==0 && obj.type==Types.DOUBLE){
							if(!Pattern.matches(FLOATING_POINT_FIELD, obj.stringValue) && !Pattern.matches(INTEGER_FIELD, obj.stringValue)){
								errorMsg.append (ResourceUtil.getAdminResourceValue(shopInfoBean.getBusiness(),"", "field.invalid.decimal", locale));					
							}
						}
						
						if(errorMsg.length ()==0 && (obj.type==Types.VARCHAR || obj.type==Types.DECIMAL)){
							if(obj.stringValue.length () > obj.length + obj.precision){
								logger.debug (obj.name + ", " + obj.stringValue.length () + " > " + obj.length + obj.precision);
								errorMsg.append (ResourceUtil.getAdminResourceValue(shopInfoBean.getBusiness(),"", "field.value.too.long", locale));					
							}
						}
		 
					}
					
					if(errorMsg.length ()>0){
						resultBuffer.append (fieldBuffer);
						resultBuffer.append (errorMsg);
						resultBuffer.append ("<br>");
					}
				}			
			}
		}catch(Exception e){
			throw e;
		}
		
		return resultBuffer;
	}	
}
