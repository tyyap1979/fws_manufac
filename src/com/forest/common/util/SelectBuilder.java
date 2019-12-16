package com.forest.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.CountryBeanDef;
import com.forest.common.beandef.CurrencyBeanDef;
import com.forest.common.builder.BaseSelectBuilder;
import com.forest.common.constants.BusinessConst;
import com.forest.common.constants.ComponentConst;
import com.forest.common.constants.GeneralConst;

/*
 * Use for static content that hardly change. Once change need to rebuild all company 
 */

public class SelectBuilder extends BaseSelectBuilder{
	private static Logger logger = Logger.getLogger(SelectBuilder.class);
	
	private static Map _HASH_STATUS = new LinkedHashMap();
	private static Map _HASH_GENDER = new LinkedHashMap();
	private static Map _HASH_TITLE = new LinkedHashMap();
	private static HashMap _HASH_COUNTRY = new HashMap();
	private static HashMap _HASH_CURRENCY = new HashMap();
	private static Map _HASH_PAYMENT_METHOD = new LinkedHashMap();
	private static Map _HASH_DELIVERY_METHOD = new LinkedHashMap();
	private static Map _HASH_TRANS_STATUS = new LinkedHashMap();
	
	private static Map _HASH_BUSINESS = new LinkedHashMap();
	private static Map _HASH_COMPONENT = new LinkedHashMap();
	private static Map _HASH_USER_GROUP = new LinkedHashMap();
	private static Map _HASH_PROFILE_TYPE = new LinkedHashMap();
	private static Map _HASH_COLUMN_GROUP = new LinkedHashMap();
	private static Map _HASH_THEME = new LinkedHashMap();
	private static Map _HASH_MONTH = new LinkedHashMap();
	private static Map _HASH_YEAR = new LinkedHashMap();
	private static Map _HASH_PAY_TYPE = new LinkedHashMap();
	private static Map _HASH_GATEWAY_STATUS = new LinkedHashMap();
	private static Map _HASH_GST = new LinkedHashMap();
	
	private static StringBuffer _COUNTRY_SELECT_BUFFER = null;
	private static StringBuffer _CURRENCY_SELECT_BUFFER = null;
	private static StringBuffer _GENDER_SELECT_BUFFER = null;
	private static StringBuffer _STATUS_SELECT_BUFFER = null;	
	private static StringBuffer _PAYMENT_METHOD_SELECT_BUFFER = null;
	private static StringBuffer _DELIVERY_METHOD_SELECT_BUFFER = null;
	private static StringBuffer _TRANS_STATUS_SELECT_BUFFER = null;
	private static StringBuffer _BUSINESS_SELECT_BUFFER = null;
	private static StringBuffer _COMPONENT_SELECT_BUFFER = null;
	private static StringBuffer _USER_GROUP_SELECT_BUFFER = null;
	private static StringBuffer _PROFILE_TYPE_SELECT_BUFFER = null;
	private static StringBuffer _COLUMN_GROUP_SELECT_BUFFER = null;
	private static StringBuffer _THEME_SELECT_BUFFER = null;
	private static StringBuffer _MONTH_SELECT_BUFFER = null;
	private static StringBuffer _YEAR_SELECT_BUFFER = null;
	private static StringBuffer _PAY_TYPE_SELECT_BUFFER = null;			
	private static StringBuffer _TITLE_SELECT_BUFFER = null;		
	private static StringBuffer _GATEWAY_STATUS_SELECT_BUFFER = null;
	private static StringBuffer _GST_SELECT_BUFFER = null;
	
	static{
		_HASH_STATUS.put(GeneralConst.ACTIVE, "Active");
		_HASH_STATUS.put(GeneralConst.SUSPENDED, "Suspended");		
		
		_HASH_GATEWAY_STATUS.put("N", "New");
		_HASH_GATEWAY_STATUS.put("S", "Success");	
		_HASH_GATEWAY_STATUS.put("F", "Fail");
		
		_HASH_GENDER.put("", "");
		_HASH_GENDER.put("M", "Male");
		_HASH_GENDER.put("F", "Female");
		
		_HASH_TITLE.put("", "");
		_HASH_TITLE.put("mr", "Mr");
		_HASH_TITLE.put("ms", "Ms");
		_HASH_TITLE.put("mrs", "Mrs");
		_HASH_TITLE.put("miss", "Miss");
		_HASH_TITLE.put("dr", "Dr");
		_HASH_TITLE.put("prof", "Prof");		
		
		_HASH_PAYMENT_METHOD.put("", "");
		_HASH_PAYMENT_METHOD.put(GeneralConst.PAY_CREDIT_CARD, "Credit Card");
		_HASH_PAYMENT_METHOD.put(GeneralConst.PAY_ONLINE_TRANSFER, "Online Transfer");
		_HASH_PAYMENT_METHOD.put(GeneralConst.PAY_COD, "Cash On Delivery");
		
		_HASH_DELIVERY_METHOD.put("", "");
		_HASH_DELIVERY_METHOD.put(GeneralConst.DEL_POST, "Ship To Address");		
		_HASH_DELIVERY_METHOD.put(GeneralConst.DEL_SELF_COLLECT, "Self Pickup");
		
		_HASH_TRANS_STATUS.put(GeneralConst.TRANS_PENDING_PAYMENT, "Pending Payment");
		_HASH_TRANS_STATUS.put(GeneralConst.TRANS_PAID, "Paid");
		_HASH_TRANS_STATUS.put(GeneralConst.TRANS_PROCESSING, "Processing");
		_HASH_TRANS_STATUS.put(GeneralConst.TRANS_DELIVERING, "Delivering");
		_HASH_TRANS_STATUS.put(GeneralConst.TRANS_RECEIVED, "Received");
		_HASH_TRANS_STATUS.put(GeneralConst.TRANS_RETURN, "Return");
		
		_HASH_BUSINESS.put("", "");
		_HASH_BUSINESS.put(BusinessConst.INTERIOR_DESIGN, "Interior Design");
		_HASH_BUSINESS.put(BusinessConst.RETAIL, "Retail");
		_HASH_BUSINESS.put(BusinessConst.MANUFACTURING, "Manufacturing");
		
		_HASH_COMPONENT.put("", "");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.AUTO_SUGGEST), "Auto Suggest");
//		_HASH_COMPONENT.put(String.valueOf(ComponentConst.AUTO_COMPLETE), "Auto Complete");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.CHECKBOX), "Checkbox");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.HIDDEN), "Hidden");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.IMAGE), "Image");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.PASSWORD), "Password");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.RADIO), "Radio");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.SELECT), "Select");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.TEXT), "Text");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.TEXTAREA), "Textarea");
		_HASH_COMPONENT.put(String.valueOf(ComponentConst.TEXTBOX), "Textbox");
				
		_HASH_USER_GROUP.put("1", "Manager");
		_HASH_USER_GROUP.put("2", "Supervisor");
		_HASH_USER_GROUP.put("3", "Executive");		
		
		_HASH_PROFILE_TYPE.put("", "");
		_HASH_PROFILE_TYPE.put("D", "Dealer");
		_HASH_PROFILE_TYPE.put("C", "Client");
		_HASH_PROFILE_TYPE.put("P", "Public");		
		
		_HASH_COLUMN_GROUP.put("", "");
		_HASH_COLUMN_GROUP.put("1", "Column 1");
		_HASH_COLUMN_GROUP.put("2", "Column 2");
		_HASH_COLUMN_GROUP.put("3", "Column 3");
		
		_HASH_THEME.put("blue", "Blue");
		_HASH_THEME.put("light", "Light");
		_HASH_THEME.put("red", "Red");
		_HASH_THEME.put("dark", "Dark");
		_HASH_THEME.put("green", "Green");		
		_HASH_THEME.put("purple", "Purple");
		_HASH_THEME.put("brown", "Brown");
		
		_HASH_GST.put("", "");		
		_HASH_GST.put("SST", "SST 10%");
		_HASH_GST.put("TX", "GST 6% (TX)");
		
		int i=0;
		for(i=1; i<=12; i++){
			_HASH_MONTH.put(String.valueOf(i), String.valueOf(i));
		}
		int currentYear = Integer.parseInt(DateUtil.getCurrentYear());	
		i = (currentYear>2010)?currentYear-(currentYear-2010):2010;
		for(; i<=currentYear; i++){
			_HASH_YEAR.put(String.valueOf(i), String.valueOf(i));
		}
		
		_HASH_PAY_TYPE.put("1", "Cheque");
		_HASH_PAY_TYPE.put("2", "Cash");
		_HASH_PAY_TYPE.put("5", "Contra");
		_HASH_PAY_TYPE.put("3", "Credit Card");		
		_HASH_PAY_TYPE.put("4", "Bank Transfer");
	}
	
	public static Map getHASH_PAY_TYPE(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_PAY_TYPE;
	}
	
	public static Map getHASH_YEAR(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_YEAR;
	}
	
	public static Map getHASH_MONTH(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_MONTH;
	}
	
	public static Map getHASH_THEME(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_THEME;
	}
	
	public static Map getHASH_COLUMN_GROUP(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_COLUMN_GROUP;
	}
	
	public static Map getHASH_PROFILE_TYPE(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_PROFILE_TYPE;
	}
	
	public static Map getHASH_USER_GROUP(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_USER_GROUP;
	}
	
	public static Map getHASH_COMPONENT(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_COMPONENT;
	}
	
	public static Map getHASH_BUSINESS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_BUSINESS;
	}
	
	public static Map getHASH_TRANS_STATUS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_TRANS_STATUS;
	}
	
	public static Map getHASH_STATUS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_STATUS;
	}
	
	public static Map getHASH_PAYMENT_METHOD(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_PAYMENT_METHOD;
	}
	
	public static Map getHASH_DELIVERY_METHOD(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_DELIVERY_METHOD;
	}
	
	public static Map getHASH_GENDER(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_GENDER;
	}
	
	public static Map getHASH_TITLE(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_TITLE;
	}
	
	public static HashMap getHASH_COUNTRY(Connection conn, ShopInfoBean shopInfoBean){
		if(_HASH_COUNTRY.isEmpty()){
			getSELECT_COUNTRY(conn, shopInfoBean, null, null);
		}
		
		return _HASH_COUNTRY;
	}
	
	public static Map getHASH_GATEWAY_STATUS(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_GATEWAY_STATUS;
	}
	
	public static HashMap getHASH_CURRENCY(Connection conn, ShopInfoBean shopInfoBean){		
		if(_HASH_CURRENCY.isEmpty()){
			getSELECT_CURRENCY(conn, shopInfoBean, null, null);
		}
		
		return _HASH_CURRENCY;
	}
	
	public static Map getHASH_GST(Connection conn, ShopInfoBean shopInfoBean){		
		return _HASH_GST;
	}
	
	public static StringBuffer getSELECT_PAY_TYPE(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_PAY_TYPE, _PAY_TYPE_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_YEAR(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_YEAR, _YEAR_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_MONTH(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_MONTH, _MONTH_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_THEME(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_THEME, _THEME_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_COLUMN_GROUP(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_COLUMN_GROUP, _COLUMN_GROUP_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_PROFILE_TYPE(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_PROFILE_TYPE, _PROFILE_TYPE_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_USER_GROUP(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_USER_GROUP, _USER_GROUP_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_COMPONENT(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_COMPONENT, _COMPONENT_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_TRANS_STATUS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_TRANS_STATUS, _TRANS_STATUS_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_BUSINESS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_BUSINESS, _BUSINESS_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_GENDER(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_GENDER, _GENDER_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_PAYMENT_METHOD(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_PAYMENT_METHOD, _PAYMENT_METHOD_SELECT_BUFFER, fieldName, defaultvalue);
	}
		
	public static StringBuffer getSELECT_DELIVERY_METHOD(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_DELIVERY_METHOD, _DELIVERY_METHOD_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_STATUS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_STATUS, _STATUS_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_TITLE(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_TITLE, _TITLE_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer getSELECT_GATEWAY_STATUS(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_GATEWAY_STATUS, _GATEWAY_STATUS_SELECT_BUFFER, fieldName, defaultvalue);
	}
		
	public static StringBuffer getSELECT_COUNTRY(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer query = new StringBuffer();
		StringBuffer selectBuffer = new StringBuffer();
		int selectedIndex = 0;
		try{
			logger.debug("getSELECT_COUNTRY = "+_COUNTRY_SELECT_BUFFER);
			if(_COUNTRY_SELECT_BUFFER==null){
				query.append("Select ").append(CountryBeanDef.countrycode.name).append(",").append(CountryBeanDef.countrydesc.name);
				query.append(" From ").append(CountryBeanDef.TABLE);
				query.append(" Where ").append(CountryBeanDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
				query.append(" Order By ").append(CountryBeanDef.countrydesc.name);
				logger.debug("query = "+query);
				pstmt = conn.prepareStatement(query.toString());
				rs = pstmt.executeQuery();
				
				selectBuffer.append("<Select name=''>");
				selectBuffer.append("<option value=''></option>");
				while(rs.next()){
					_HASH_COUNTRY.put(rs.getString(CountryBeanDef.countrycode.name), rs.getString(CountryBeanDef.countrydesc.name));
					selectBuffer.append("<option value='").append(rs.getString(CountryBeanDef.countrycode.name)).append("'");
					selectBuffer.append(">");
					
					selectBuffer.append(rs.getString(CountryBeanDef.countrydesc.name));
					selectBuffer.append("</option>");				
				}
				selectBuffer.append("</select>");
				_COUNTRY_SELECT_BUFFER = selectBuffer;
			}
			if(defaultvalue!=null){
				selectBuffer = new StringBuffer(_COUNTRY_SELECT_BUFFER.toString());
				selectedIndex = selectBuffer.indexOf("'"+defaultvalue+"'");
				selectBuffer.insert(selectedIndex+("'"+defaultvalue+"'").length(), " selected");
			}
			if(fieldName!=null){
				selectedIndex = selectBuffer.indexOf("name=''");
				selectBuffer.insert(selectedIndex+("name='").length(), fieldName);
			}
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return selectBuffer;
	}
	
	public static StringBuffer getSELECT_CURRENCY(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer query = new StringBuffer();
		StringBuffer selectBuffer = new StringBuffer();
		int selectedIndex = 0;
		String[] currencyArray = new String[4];
		try{
			if(_CURRENCY_SELECT_BUFFER==null){
				query.append("Select ");
				query.append(CurrencyBeanDef.currencycode.name).append(",");
				query.append(CurrencyBeanDef.currencydesc.name).append(",");
				query.append(CurrencyBeanDef.rate.name).append(",");
				query.append(CurrencyBeanDef.symbol.name).append(",");
				query.append(CurrencyBeanDef.updatedate.name);
				query.append(" From ").append(CurrencyBeanDef.TABLE);
				query.append(" Where ").append(CurrencyBeanDef.status.name).append("='").append(GeneralConst.ACTIVE).append("'");
				query.append(" Order By ").append(CurrencyBeanDef.currencydesc.name);
				logger.debug("query = "+query);
				pstmt = conn.prepareStatement(query.toString());
				rs = pstmt.executeQuery();
				
				selectBuffer.append("<Select name=''>");
				selectBuffer.append("<option value=''></option>");
				while(rs.next()){
					currencyArray = new String[4];
					currencyArray[0] = rs.getString(CurrencyBeanDef.currencydesc.name);
					currencyArray[1] = rs.getString(CurrencyBeanDef.rate.name);
					currencyArray[2] = rs.getString(CurrencyBeanDef.updatedate.name);
					currencyArray[3] = rs.getString(CurrencyBeanDef.symbol.name);
					
					_HASH_CURRENCY.put(rs.getString(CurrencyBeanDef.currencycode.name), currencyArray);
					selectBuffer.append("<option value='").append(rs.getString(CurrencyBeanDef.currencycode.name)).append("'");
					selectBuffer.append(">");
					
					selectBuffer.append(rs.getString(CurrencyBeanDef.currencydesc.name));
					selectBuffer.append("</option>");				
				}
				selectBuffer.append("</select>");
				_CURRENCY_SELECT_BUFFER = selectBuffer;
			}
			if(defaultvalue!=null){
				selectBuffer = new StringBuffer(_CURRENCY_SELECT_BUFFER.toString());
				selectedIndex = selectBuffer.indexOf("'"+defaultvalue+"'");
				selectBuffer.insert(selectedIndex+("'"+defaultvalue+"'").length(), " selected");
			}
			if(fieldName!=null){
				selectedIndex = selectBuffer.indexOf("name=''");
				selectBuffer.insert(selectedIndex+("name='").length(), fieldName);
			}
		}catch(Exception e){
			logger.error(e, e);
		}
				
		return selectBuffer;
	}
	
	public static StringBuffer getSELECT_GST(Connection conn, ShopInfoBean shopInfoBean, String fieldName, String defaultvalue){
		return buildHashSelect(_HASH_GST, _GST_SELECT_BUFFER, fieldName, defaultvalue);
	}
	
	public static StringBuffer buildSelect(HashMap map, String name){
		StringBuffer buffer = new StringBuffer();
		String key = null;
		Iterator keyIt = map.keySet().iterator();
		buffer.append("<Select name='").append(name).append("'>");
		while(keyIt.hasNext()){
			key = (String) keyIt.next();
			buffer.append("<option value='").append(key).append("'>");
			buffer.append((String) map.get(key));
			buffer.append("</option>");
		}
		buffer.append("</select>");
				
		return buffer;
	}
	
	
}
