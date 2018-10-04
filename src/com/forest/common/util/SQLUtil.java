package com.forest.common.util;

import java.sql.Types;

import com.forest.common.bean.DataObject;

public class SQLUtil
{
	public static synchronized String processType(DataObject p_oVariable){
		String fieldValue = p_oVariable.stringValue;
		if(fieldValue!=null){
			fieldValue = fieldValue.replaceAll ("'", "''");
		}
		
		String m_sParam = "";
		if(p_oVariable.type == Types.VARCHAR){
			if(fieldValue == null){
				m_sParam = "null";
			}else{
				m_sParam = "'" + fieldValue + "'";
			}
		}
		
		if(p_oVariable.type == Types.CLOB){
			if(fieldValue == null){
				m_sParam = "null";
			}else{
				m_sParam = "'" + fieldValue + "'";
			}
		}
		
		if(p_oVariable.type == Types.DATE){
			if(fieldValue == null || "".equals (fieldValue)){
				m_sParam = "null";
			}else{
				m_sParam = "'" +  fieldValue + "'";
			}
		}
		
		if(p_oVariable.type == Types.INTEGER){
			if(fieldValue == null){
				m_sParam = "null";
			}else{
				m_sParam = fieldValue;
			}
		}
		
		if(p_oVariable.type == Types.DOUBLE){
			if(fieldValue == null || fieldValue.equals ("")){
				m_sParam = "null";
			}else{
				m_sParam = fieldValue;
			}
		}
		
		return m_sParam;
	}
}
