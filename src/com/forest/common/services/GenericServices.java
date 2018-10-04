package com.forest.common.services;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;
import com.forest.common.util.AutoNumberBuilder;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.OwnException;

public class GenericServices {
	private Logger logger = Logger.getLogger (GenericServices.class);
	protected Connection _dbConnection = null;
	protected ShopInfoBean _shopInfoBean = null;
	protected ClientBean _clientBean = null;
	
	public GenericServices(Connection conn, ShopInfoBean shopInfoBean, ClientBean clientBean){
		_dbConnection = conn;
		_shopInfoBean = shopInfoBean;
		_clientBean = clientBean;
	}
	
	public int create(Class defClass, ArrayList addList, HashMap dataMap) throws OwnException{				
		String tableName = null;
		int autoIncrement = 0;
		try{			
			tableName = (String) BeanDefUtil.getField(defClass, BeanDefUtil.TABLE);
			autoIncrement = executeInsert (tableName, addList, dataMap);
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}		
		return autoIncrement;
	}	
	
	
	public int update(Class defClass, ArrayList addList, HashMap dataMap) throws OwnException{		
		String tableName = null;	
		int row = 0;
//		ArrayList addList = null;
		DataObject key = null;
		try{
//			addList = BeanDefUtil.getArrayList(defClass, _dbConnection, listName, _clientBean);
			key = BeanDefUtil.getKeyObject(defClass);
			tableName = (String) BeanDefUtil.getField(defClass, BeanDefUtil.TABLE);		
			
			row = executeUpdate (tableName, addList, dataMap, key);
		}catch(Exception e){			
			throw new OwnException(e);
		}		
		return row;
	}	
	
	public void updateDetail(Class defClass, HashMap[] dataMap, DataObject key, ArrayList addList) throws OwnException{		
		updateDetail(defClass, dataMap, key, addList, false);
	}
	public void updateDetail(Class defClass, HashMap[] dataMap, DataObject key, ArrayList addList, boolean updateCreate) throws OwnException{		
		String tableName = null;
		
		String rowStatus = null;
		StringBuffer delKeyBuffer = new StringBuffer();
		String[] delKey = null;
		int autoNum = 0;		
		try{								
			tableName = (String) BeanDefUtil.getField(defClass, BeanDefUtil.TABLE);
			
			for(int i=0; i<dataMap.length; i++){
				if(dataMap[i]!=null){
					logger.debug("updateDetail dataMap["+i+"]="+dataMap[i]);
					rowStatus = (String) dataMap[i].get("row-status");
					if("N".equals(rowStatus)){
//						logger.debug(" New = "+dataMap[i]);
						autoNum = create (defClass, addList, dataMap[i]);
						dataMap[i].put(key.name, String.valueOf(autoNum));						
					}else if("U".equals(rowStatus)){
//						logger.debug(" Update = "+dataMap[i]);
//						logger.debug("key = "+key.name);
						executeUpdate (tableName, addList, dataMap[i], key);
//						if(updatedRow==0 && updateCreate){
//							autoNum = create (defClass, addList, dataMap[i]);
//							dataMap[i].put(key.name, String.valueOf(autoNum));
//						}						
					}else if("D".equals(rowStatus)){
						delKeyBuffer.append(dataMap[i].get(key.name)).append(",");
//						logger.debug(" Delete = "+dataMap[i]);
					}
				}
			}
	    	if(delKeyBuffer.length()>0){	    		
	    		delKey = delKeyBuffer.deleteCharAt(delKeyBuffer.length()-1).toString().split(",");	    		
	    		delete(defClass, key, delKey);
	    	}
			
			
			
		}catch(Exception e){		
			logger.error(e, e);
			throw new OwnException(e);
		}		
	}	
		
	public void delete(Class defClass, DataObject key, String[] arrCode) throws OwnException{		
		String tableName = null;
		
		StringBuffer deleteBuffer = new StringBuffer();
		boolean permanentDelete = false;
		String permDel = null;
		try{						
			tableName = (String) BeanDefUtil.getField(defClass, BeanDefUtil.TABLE);
						
			permDel = (String) BeanDefUtil.getField(defClass, BeanDefUtil.PERMANENT_DELETE);
			if("true".equalsIgnoreCase(permDel)){
				permanentDelete = true;
			}
			logger.debug("["+defClass.getName()+"] "+"permanentDelete = "+permanentDelete);						
			
			if(permanentDelete==false){
				deleteBuffer.append("Update ").append(tableName);
				deleteBuffer.append(" Set ").append(BaseDef.STATUS).append("='").append(GeneralConst.DELETED).append("'");
				deleteBuffer.append(" Where ").append(key.name).append(" In (").append(DBUtil.arrayToString(arrCode, key)).append(")");				
			}else{
				deleteBuffer.append("Delete From ").append(tableName);				
				deleteBuffer.append(" Where ").append(key.name).append(" In (").append(DBUtil.arrayToString(arrCode, key)).append(")");
			}
			logger.debug("deleteBuffer = "+deleteBuffer);
				
			execute (deleteBuffer);
		}catch(Exception e){
			throw new OwnException(e);
		}		
	}
	
	public ArrayList search(Class defClass, StringBuffer additionalCriteria, 
			ArrayList searchList, ArrayList searchDisplayList, HashMap[] dataMap, 
			String orderBy, String totalRecordPerPage, String action){
		return search(defClass, additionalCriteria, searchList, searchDisplayList, dataMap, orderBy, totalRecordPerPage, action, "getSearchScript");
	}
	public ArrayList search(Class defClass, StringBuffer additionalCriteria, 
			ArrayList searchList, ArrayList searchDisplayList, HashMap[] dataMap, 
			String orderBy, String totalRecordPerPage, String action, String searchFunction){
		ArrayList listRecord = new ArrayList();		
		
		StringBuffer[] query = new StringBuffer[2];
		String tableName = null;
		boolean permanentDelete = false;
		String permDel = null;
		
		Class[] paramType = null;
		Object[] paramValue = null;	
		Method mthd = null;
		DataObject statusObj = null;
		
		try{			
			tableName = (String) BeanDefUtil.getField(defClass, BeanDefUtil.TABLE);							
			permDel = (String) BeanDefUtil.getField(defClass, BeanDefUtil.PERMANENT_DELETE);
			if("true".equalsIgnoreCase(permDel)){
				permanentDelete = true;
			}
			logger.debug("["+defClass.getName()+"] "+"permanentDelete = "+permanentDelete);	
							
			if(additionalCriteria==null) additionalCriteria = new StringBuffer();
			
			try{			
				if(orderBy==null){					
					orderBy = (String) BeanDefUtil.getField(defClass, BeanDefUtil.DEFAULT_SORT);												
//					orderBy += " " + (String) BeanDefUtil.getField(defClass, BeanDefUtil.DEFAULT_SORT_DIRECTION);
				}				
				
				if(!permanentDelete){							
					statusObj = BeanDefUtil.getDataObject(defClass, BaseDef.STATUS);
					String statusFieldName = null;
					if(CommonUtil.isEmpty(statusObj.prefix)){
						statusFieldName = statusObj.name;
					}else{
						statusFieldName = statusObj.prefix + "." + statusObj.name;
					}
					if(additionalCriteria.length()==0){
						additionalCriteria.append(statusFieldName).append("!='").append(GeneralConst.DELETED).append("' ");
					}else{
						additionalCriteria.append(" And ").append(statusFieldName).append("!='").append(GeneralConst.DELETED).append("'");
					}
				}
			}catch(Exception e){
				
			}

			try{
				// Custom Search 
				paramType = new Class[1];
				paramValue = new Object[1];	
				paramType[0] = ArrayList.class;				
				paramValue[0] = searchDisplayList;
				mthd = defClass.getDeclaredMethod(searchFunction, paramType);
				query = (StringBuffer[]) mthd.invoke(defClass, paramValue);
			}catch(Exception e){								
				// Default Search
				logger.debug("No getSearchScript for "+defClass.getName()+".................");
				paramType = new Class[2];
				paramValue = new Object[2];	
				paramType[0] = ArrayList.class;
				paramType[1] = String.class;
				paramValue[0] = searchDisplayList;
				paramValue[1] = "From "+tableName;
				mthd = defClass.getSuperclass().getDeclaredMethod(searchFunction, paramType);
				query = (StringBuffer[]) mthd.invoke(defClass.getSuperclass(), paramValue);
			}						
			if(GeneralConst.SEARCH.equals(action)){
				listRecord.add(retrieveKeys(query[0], additionalCriteria, searchList, dataMap, orderBy, action));
			}else{
				listRecord.add("");
			}
			listRecord.add(paginationSearch(query[1], additionalCriteria, searchList, dataMap, orderBy, totalRecordPerPage, action));
		}catch(Exception e){
			logger.error(e, e);
		}
		return listRecord;
	}
	
	public ArrayList searchDirect(StringBuffer[] query, StringBuffer additionalCriteria, 
			ArrayList searchList, HashMap[] dataMap, 
			String orderBy, String totalRecordPerPage, String action)throws Exception{
		ArrayList listRecord = new ArrayList();	
		listRecord.add(retrieveKeys(query[0], additionalCriteria, searchList, dataMap, orderBy, action));
		listRecord.add(paginationSearch(query[1], additionalCriteria, searchList, dataMap, orderBy, totalRecordPerPage, action));
		return listRecord;
	}
		
	protected ArrayList retrieveKeys(StringBuffer mainQuery, StringBuffer additionalCriteria, 
			ArrayList searchList, HashMap[] dataMap, String orderBy, String action) throws OwnException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList listRecord = new ArrayList();
		StringBuffer searchBuffer = new StringBuffer();
		try{
			if(mainQuery!=null){
				searchBuffer = new StringBuffer(mainQuery.toString()); 
			
				pstmt = preparedSearch(searchBuffer, additionalCriteria, searchList, dataMap, orderBy, "*", action);
				rs = pstmt.executeQuery();			
				while(rs.next ()){														
					listRecord.add (rs.getString(1));
				}	
			}
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}finally{
			free(null, pstmt, rs);
		}
		return listRecord;
	}
	
	public ArrayList searchDataArray(StringBuffer mainQuery) throws Exception{		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList listRecord = new ArrayList();
		HashMap mapRecord = null;
		ResultSetMetaData rsMeta = null;
		int metaCount = 0;
		try{
			if(mainQuery!=null){		
				logger.debug("searchDataArray mainQuery = "+mainQuery);
				pstmt = _dbConnection.prepareStatement(mainQuery.toString());;
				rs = pstmt.executeQuery();
				String value = null;
				rsMeta = rs.getMetaData();
				metaCount = rsMeta.getColumnCount();
				while(rs.next ()){								
					mapRecord = new HashMap();
					for(int i=0; i<metaCount; i++){			
						value = rs.getString (i+1);	
//						if(rsMeta.getColumnType(i+1)==Types.DOUBLE){
//							mapRecord.put (rsMeta.getColumnLabel(i+1), rs.getObject(i+1));
//						}else{
							mapRecord.put (rsMeta.getColumnLabel(i+1), value);
//						}
					}							
					listRecord.add (mapRecord);
				}	
			}
		}catch(Exception e){			
			throw e;
		}finally{
			free(null, pstmt, rs);
		}
		return listRecord;
	}
	
	public HashMap searchDataMap(StringBuffer mainQuery, LinkedHashMap paramMap) throws Exception{	
		ArrayList dataArray = searchDataArray(mainQuery, paramMap);
		HashMap dataMap = null;
		if(dataArray.size()>0){
			dataMap = (HashMap) dataArray.get(0);
		}
//		logger.debug("dataMap = "+dataMap);
		return dataMap;
	}
	
	public void executeQuery(StringBuffer mainQuery, LinkedHashMap paramMap) throws Exception{		
		PreparedStatement pstmt = null;			
		DataObject key = null;
		Iterator it = null;
		int paramCounter = 1;
		try{
			if(mainQuery!=null){		
				logger.debug("executeQuery mainQuery = "+mainQuery);
				logger.debug("executeQuery paramMap = "+paramMap);
				pstmt = _dbConnection.prepareStatement(mainQuery.toString());;
				
				// Put in paramMap
				if(paramMap!=null){
					it = paramMap.keySet().iterator();
					while(it.hasNext()){
						key = (DataObject) it.next();
						pstmt.setObject(paramCounter, paramMap.get(key), key.type);
						paramCounter++;
					}
				}
				pstmt.execute();
				
			}
		}catch(Exception e){			
			throw e;
		}finally{
			free(null, pstmt, null);
		}		
	}
	
	public ArrayList searchDataArray(StringBuffer mainQuery, LinkedHashMap paramMap) throws Exception{		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList listRecord = new ArrayList();
		HashMap mapRecord = null;
		ResultSetMetaData rsMeta = null;
		int metaCount = 0;
		String value = null;
		DataObject key = null;
		Iterator it = null;
		int paramCounter = 1;
		try{
			if(mainQuery!=null){		
				logger.debug("searchDataArray mainQuery = "+mainQuery);
				logger.debug("searchDataArray paramMap = "+paramMap);
				pstmt = _dbConnection.prepareStatement(mainQuery.toString());;
				
				// Put in paramMap
				if(paramMap!=null){
					it = paramMap.keySet().iterator();
					while(it.hasNext()){
						key = (DataObject) it.next();
						pstmt.setObject(paramCounter, paramMap.get(key), key.type);
						paramCounter++;
					}
				}
				rs = pstmt.executeQuery();
				
				
				rsMeta = rs.getMetaData();
				metaCount = rsMeta.getColumnCount();
				while(rs.next ()){								
					mapRecord = new HashMap();
					for(int i=0; i<metaCount; i++){			
						value = rs.getString (i+1);	
//						if(rsMeta.getColumnType(i+1)==Types.DOUBLE){
//							mapRecord.put (rsMeta.getColumnLabel(i+1), rs.getObject(i+1));
//						}else{
							mapRecord.put (rsMeta.getColumnLabel(i+1), value);
//						}
					}							
					listRecord.add (mapRecord);
				}	
			}
		}catch(Exception e){			
			throw e;
		}finally{
			free(null, pstmt, rs);
		}
		return listRecord;
	}
	
	protected ArrayList paginationSearch(StringBuffer mainQuery, StringBuffer additionalCriteria, 
			ArrayList searchList, HashMap[] dataMap, String orderBy, String totalRecordPerPage, String action) throws OwnException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList listRecord = new ArrayList();
		HashMap mapRecord = null;
		ResultSetMetaData rsMeta = null;
		
		StringBuffer searchBuffer = null;
		try{
			if(mainQuery!=null){
				searchBuffer = new StringBuffer(mainQuery.toString()); 
				pstmt = preparedSearch(searchBuffer, additionalCriteria, searchList, dataMap, orderBy, totalRecordPerPage, action);
				rs = pstmt.executeQuery();
				String value = null;
				rsMeta = rs.getMetaData();
				while(rs.next ()){								
					mapRecord = new HashMap();
					for(int i=0; i<rsMeta.getColumnCount(); i++){			
						value = rs.getString (i+1);	
						mapRecord.put (rsMeta.getColumnLabel(i+1), value);
					}							
					listRecord.add (mapRecord);
				}	
			}
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}finally{
			free(null, pstmt, rs);
		}
		return listRecord;
	}
	
	// dataMap.length always equals to 1 
	private PreparedStatement preparedSearch(StringBuffer mainQuery, StringBuffer additionalCriteria, 
			ArrayList searchList, HashMap[] dataMap, String OrderBy, String totalRecordPerPage, String action) throws OwnException{
		
		PreparedStatement pstmt = null;		
		StringBuffer mSearchCriteria = new StringBuffer();		
		
		int i=0;
		int psmstCount = 1;
		
		DataObject field = null;
		HashMap criteriaMap = new HashMap();		
		
		HashMap searchFieldMap = null;
		String value = null;
		String defaultvalue = null;
		String prefix = null;
		try{		
			// Build [field.name] = ?			
			logger.info("------------------------------------------");
			logger.info("preparedSearch dataMap = "+dataMap[0]);
			for(i=0; i<searchList.size(); i++){
				searchFieldMap = (HashMap) searchList.get(i);	
//				logger.debug("searchFieldMap = "+searchFieldMap);
				field = (DataObject) searchFieldMap.get("object");
				value = (String) dataMap[0].get(field.name);
				if(isEmpty(value)){
					value = (String) searchFieldMap.get("defaultvalue");
				}
				prefix = (String) searchFieldMap.get("prefix");
//				logger.debug(prefix+"."+field.name+":"+value);
				if(field!=null && !isEmpty(value)){
					if(GeneralConst.RETRIEVE.equals(action) && field.key) continue;
											
					buildSearchFieldNew(mSearchCriteria, "And", field, prefix);
					criteriaMap.put (field.name, "");					
				}
			}
			
			if(additionalCriteria!=null && additionalCriteria.length () > 0){
				if(mSearchCriteria.toString().trim().length()>0){
					mSearchCriteria.append (" And ").append (additionalCriteria).append (" ");
				}else{
					mSearchCriteria.append (" ").append (additionalCriteria).append (" ");
				}				
			}

			if(isEmpty (totalRecordPerPage)){
				totalRecordPerPage = "50";
			}else if("*".equals (totalRecordPerPage)){
				totalRecordPerPage = "";
			}			
//			logger.debug("mainQuery = "+mainQuery);
			logger.info("mSearchCriteria = "+mSearchCriteria);
			if(!isEmpty (mSearchCriteria.toString ())){
				if(mainQuery.lastIndexOf (")") < mainQuery.toString ().toUpperCase ().lastIndexOf (" WHERE ")){
					// Got Where before )
					mainQuery.append (" And (").append (mSearchCriteria).append (")");
				}else{
					mainQuery.append (" Where ").append (mSearchCriteria);
				}
				
			}
			
			if(OrderBy!=null && OrderBy.length ()>0){
				mainQuery.append (" Order By ").append (OrderBy);
			}			
			
			if(!isEmpty (totalRecordPerPage)){
				mainQuery.append (" Limit ").append (totalRecordPerPage);
			}
						
			logger.info ("preparedSearch mainQuery: "+mainQuery);
			pstmt = _dbConnection.prepareStatement (mainQuery.toString ());
			StringBuffer fieldValueBuffer = new StringBuffer("["); 
			if(searchList!=null){
				for(i=0; i<searchList.size(); i++){
					searchFieldMap = (HashMap) searchList.get(i);						
					field = (DataObject) searchFieldMap.get("object");
					value = (String) dataMap[0].get(field.name);
					if(isEmpty(value)){
						value = (String) searchFieldMap.get("defaultvalue");
					}
					prefix = (String) searchFieldMap.get("prefix");					
					if(field!=null && !isEmpty(value)){
						if(criteriaMap.get (field.name)!=null){
							fieldValueBuffer.append(value).append(",");							
							psmstCount = setParameter (pstmt, psmstCount, field, value, true);
						}
					}
				}
				if(fieldValueBuffer.length()>1) fieldValueBuffer.deleteCharAt(fieldValueBuffer.length()-1);
				fieldValueBuffer.append("]");
				logger.info("value:"+fieldValueBuffer);
			}
			logger.debug("------------------------------------------");
//			if(createSortObject){
//				psmstCount = setParameter (pstmt, psmstCount, sortDataObject);
//			}
			
			// Sort Field Put Last
			
		}catch(Exception e){
			logger.error (e, e);
			throw new OwnException(e);
		}
		
		return pstmt;
	}
	
	protected void buildSearchFieldNew(StringBuffer mSearchCriteria, String operator, DataObject pObject, String prefix) throws OwnException{
		StringBuffer mReturn = new StringBuffer();
		
		if(prefix!=null && prefix.length ()>0){
			prefix += ".";
		}else{
			prefix = "";
		}		
		int searchType = pObject.searchOption;
		mReturn.append(" ").append (prefix).append (pObject.name);		
		switch(searchType){
			case SearchConst.EQUAL: 
				if(pObject.type == Types.VARCHAR || pObject.type == Types.CLOB || pObject.type == Types.DATE){
//					if(pObject.stringValue.equalsIgnoreCase ("null")){
//						mReturn.append (" Is ").append ("NULL");
//					}else{
						mReturn.append (" = ?");
//					}						
				}					
				if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
//					if(pObject.stringValue.equalsIgnoreCase ("null")){
//						mReturn.append (" Is ").append ("NULL");
//					}else{
						mReturn.append (" = ?");
//					}						
				}
				break;						
			case SearchConst.LIKE: 
				if(pObject.type == Types.VARCHAR || pObject.type == Types.CLOB){
					mReturn.append (" Like ?");
				}else{
					throw new OwnException(pObject.name + " field cannot be " + pObject.type);
				}
				break;
			case SearchConst.GREATTHAN: 
				if(pObject.type == Types.VARCHAR || pObject.type == Types.DATE){
					mReturn.append (" > ?");
				}
				if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
					mReturn.append (" > ?");
				}
				break;
			case SearchConst.GREATTHAN_EQUAL: 
				if(pObject.type == Types.VARCHAR || pObject.type == Types.DATE){
					mReturn.append (" >= ?");
				}
				if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
					mReturn.append (" >= ?");
				}
				break;
			case SearchConst.LESSTHAN: 
				if(pObject.type == Types.VARCHAR || pObject.type == Types.DATE){
					mReturn.append (" < ?");
				}
				if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
					mReturn.append (" < ?");
				}
				break;
			case SearchConst.LESSTHAN_EQUAL: 
				if(pObject.type == Types.VARCHAR || pObject.type == Types.DATE){
					mReturn.append (" <= ?");
				}
				if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
					mReturn.append (" <= ?");
				}
				break;
			default:
				throw new OwnException("9999");					
		}			

	
		if(mSearchCriteria.length ()>0){
			if(mReturn.length ()>0){						
				mReturn.insert (0, " " + operator + " ");					
			}				
		}
		mSearchCriteria.append (mReturn);
	}
	
	protected int setParameter(PreparedStatement pStmt, int paramIndex, DataObject pObject, String value, boolean isSearch)throws OwnException{
		try{
			if(value==null){
				pStmt.setNull (paramIndex, pObject.type);
			}else{					
				logger.info(pObject.name + "[" + paramIndex + "],[" + pObject.type + "] = " + value);
				switch (pObject.type) {
				case Types.TIMESTAMP:
				case Types.DATE:
					if("".equals (value)){
						pStmt.setNull (paramIndex, pObject.type);
					}else{
						pStmt.setString (paramIndex, value);
					}
					break;
				case Types.DOUBLE:
					value = (CommonUtil.isEmpty(value))?"0":value;
					pStmt.setDouble (paramIndex, Double.parseDouble (value));
					break;
				case Types.INTEGER:
				case Types.SMALLINT:
				case Types.TINYINT:
					if("".equals (value) || "null".equals (value)){							
						pStmt.setNull (paramIndex, pObject.type);
					}else{
						pStmt.setInt (paramIndex, Integer.parseInt (value));	
					}
					break;
				case Types.VARCHAR:
				case Types.CLOB:
					if(pObject.searchOption==SearchConst.EQUAL){
						pStmt.setString (paramIndex, value);							
					}else{
						if(isSearch){
							pStmt.setString (paramIndex, "%" + value + "%");
						}else{
							pStmt.setString (paramIndex, value);
						}
					}
					break;							
				default:
					break;
				} 				
			}
			paramIndex++;
		}catch(Exception e){
			logger.debug("paramIndex = "+paramIndex+", pObject.name="+pObject.name + ", pObject.type = "+pObject.type + ", value = "+value);
			throw new OwnException(e);
		}
		return paramIndex;
	}
	
	protected int executeInsert(String TABLE, ArrayList addList, HashMap dataMap) throws OwnException{		
		StringBuffer m_oQuery = new StringBuffer();		
//		DataObject dataObject = null;
		m_oQuery.append ("Insert Into ").append (TABLE).append (" (");
		
		int i = 0;				
		int psmstCount = 1;		
		int autoIncValue = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		HashMap fieldMap = null;
		DataObject field = null;
		String value = null;
		String shadowField = null;		
		String autoNumber = null;
	
		try{						
			for(i=0; i<addList.size(); i++){
				fieldMap = (HashMap) addList.get(i);		
				logger.debug("executeInsert fieldMap = "+fieldMap);
				field = (DataObject) fieldMap.get("object");
				if(field==null) continue;
				shadowField = (String)  fieldMap.get("shadow");
				autoNumber = (String) fieldMap.get("xref"); 
				
				if("S".equals(fieldMap.get("systemfield")))continue;
				
				if(!CommonUtil.isEmpty(autoNumber) && autoNumber.indexOf("AUTO-")!=-1){
					
				}else{
					value = (String) dataMap.get(field.name);
					if(value==null && !BaseDef.UPDATEDATE.equals (field.name)) continue;
				}
				
				
				if(!field.autoIncrement && !"Y".equals(shadowField)){
					m_oQuery.append (field.name);
//					logger.debug("field "+i+":"+field.name);
//					if((i+1)<count){
						m_oQuery.append (",");
//					}
				}
			}
			m_oQuery.deleteCharAt(m_oQuery.length()-1); //delete last comma
			
			m_oQuery.append (") Values(");
			
			for(i=0; i<addList.size(); i++){
				fieldMap = (HashMap) addList.get(i);						
				field = (DataObject) fieldMap.get("object");				
				if(field==null) continue;
				shadowField = (String)  fieldMap.get("shadow");		
				autoNumber = (String) fieldMap.get("xref"); 
				
				if("S".equals(fieldMap.get("systemfield")))continue;
								
				if(!CommonUtil.isEmpty(autoNumber) && autoNumber.indexOf("AUTO-")!=-1){
					
				}else{
					value = (String) dataMap.get(field.name);
					if(value==null && !BaseDef.UPDATEDATE.equals (field.name)) continue;
				}
				
				if(!field.autoIncrement && !"Y".equals(shadowField)){
					if(BaseDef.UPDATEDATE.equals (field.name) || "salesdate".equals (field.name)){
						m_oQuery.append ("Now()");
					}else{
						m_oQuery.append ("?");
					}
					
//					if((i+1)<count){
						m_oQuery.append (",");
//					}
				}
			}
			m_oQuery.deleteCharAt(m_oQuery.length()-1); //delete last comma
			
			m_oQuery.append (")");
			logger.debug ("executeInsert = "+m_oQuery);
			pstmt = _dbConnection.prepareStatement (m_oQuery.toString (),PreparedStatement.RETURN_GENERATED_KEYS);
			StringBuffer fieldValueBuffer = new StringBuffer("[");
			for(i=0; i<addList.size(); i++){
				fieldMap = (HashMap) addList.get(i);						
				field = (DataObject) fieldMap.get("object");
				if(field==null) continue;
				value = (String) dataMap.get(field.name);
				shadowField = (String)fieldMap.get("shadow");
				autoNumber = (String) fieldMap.get("xref"); 
				
				if("S".equals(fieldMap.get("systemfield")))continue;
				if(!CommonUtil.isEmpty(autoNumber) && autoNumber.indexOf("AUTO-")!=-1){
					
				}else{
					value = (String) dataMap.get(field.name);
					if(value==null) continue;
				}
				
				if(!field.autoIncrement && !"Y".equals(shadowField)){				
					if(!BaseDef.UPDATEDATE.equals (field.name) && !"salesdate".equals (field.name)){	
						if(!CommonUtil.isEmpty(autoNumber) && autoNumber.indexOf("AUTO-")!=-1){
							value = new AutoNumberBuilder().AUTO_NUM(_dbConnection, (String) dataMap.get("companyid"), autoNumber, dataMap);
							dataMap.put(field.name, value);
						}
						fieldValueBuffer.append(value).append(",");	
//						logger.debug("Set value "+psmstCount+":"+value);
						psmstCount = setParameter (pstmt, psmstCount, field, value, false);	
					}
				}
			}
			if(fieldValueBuffer.length()>1) fieldValueBuffer.deleteCharAt(fieldValueBuffer.length()-1);
			fieldValueBuffer.append("]");
			logger.debug("value:"+fieldValueBuffer);
			pstmt.execute ();
			
			rs = pstmt.getGeneratedKeys ();
			if (rs.next()) {
				autoIncValue = rs.getInt(1);
		    }
			
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}finally{
			free(null, pstmt, null);
		}
		
		return autoIncValue;
	}
	
	
	public int executeUpdate(String TABLE, ArrayList addList, HashMap dataMap, DataObject key) throws OwnException{		
		StringBuffer m_oQuery = new StringBuffer();		
//		DataObject dataObject = null;
		m_oQuery.append ("Update ").append (TABLE).append (" Set ");
		
		int i = 0;		
		int x = 0;		
		int psmstCount = 1;				
		
		PreparedStatement pstmt = null;
				
		HashMap fieldMap = null;
		DataObject field = null;
		String value = null;
		String whereQuery = "";
		int updateValue = 0;
		
		String keyValue = null;
		String shadowField = null;
		try{						
			for(i=0; i<addList.size(); i++){
				fieldMap = (HashMap) addList.get(i);						
				field = (DataObject) fieldMap.get("object");
				if(field==null) continue;
				shadowField = (String)  fieldMap.get("shadow");
				logger.debug("executeUpdate fieldMap="+fieldMap);
				
				if("S".equals(fieldMap.get("systemfield")))continue;
				
				value = (String) dataMap.get(field.name);
				if(value==null) continue;
				
				if(!"Y".equalsIgnoreCase(shadowField)){
					if(BaseDef.UPDATEDATE.equals (field.name)){
						m_oQuery.append (field.name).append (" = NOW(),");
					}else{
						if(field!=key){
							m_oQuery.append (field.name).append (" = ?,");
						}else{						
							keyValue = (String) dataMap.get(key.name);
							whereQuery = " Where "+key.name+"=? ";
						}
					}	
				}
			}
			
			m_oQuery = new StringBuffer(m_oQuery.substring (0, m_oQuery.length () - 1));
			
			m_oQuery.append (whereQuery);
			logger.debug ("executeUpdate = "+m_oQuery);
//			logger.debug("dataMap = "+dataMap);
			pstmt = _dbConnection.prepareStatement (m_oQuery.toString ());
			
			StringBuffer fieldValueBuffer = new StringBuffer("[");
			for(x=0; x<addList.size(); x++){
				fieldMap = (HashMap) addList.get(x);						
				field = (DataObject) fieldMap.get("object");
				if(field==null) continue;
				shadowField = (String)  fieldMap.get("shadow");
				
				if("S".equals(fieldMap.get("systemfield")))continue;
				value = (String) dataMap.get(field.name);				
				if(value==null) continue;
				
				if(!"Y".equalsIgnoreCase(shadowField)){
					if(!BaseDef.UPDATEDATE.equals (field.name) && field!=key){
						fieldValueBuffer.append(value).append(",");								
						psmstCount = setParameter (pstmt, psmstCount, field, value, false);	
					}				
				}
			}
			psmstCount = setParameter (pstmt, psmstCount, key, keyValue, false);
			fieldValueBuffer.append(keyValue).append("]");
			logger.debug("value:"+fieldValueBuffer);
			updateValue = pstmt.executeUpdate ();
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}finally{
			free(null, pstmt, null);
		}
		
		return updateValue;
	}
	
	protected int execute(StringBuffer p_oQuery)throws OwnException{		
		PreparedStatement m_oStmt = null;
		ResultSet rs = null;
		int autoIncValue = 0;
		try{			
			m_oStmt = _dbConnection.prepareStatement (p_oQuery.toString (),PreparedStatement.RETURN_GENERATED_KEYS);
			m_oStmt.execute ();
						
			rs = m_oStmt.getGeneratedKeys ();
			if (rs.next()) {
				autoIncValue = rs.getInt(1);
		    }
			return autoIncValue;
		}catch(Exception e){
			throw new OwnException(e);
		}finally{
			free(null, m_oStmt, rs);
		}
	}
	
	public void free(Connection conn, PreparedStatement pstmt, ResultSet rs){
		try{
			if(rs!=null){
				rs.close ();				
			}
			
			if(pstmt!=null){
				pstmt.close ();
			}
			
			if(conn!=null && !conn.isClosed ()){
				conn.close ();
			}
			
			rs = null;
			pstmt = null;
			conn=null;
		}catch(Exception e){
			logger.error (e, e);
		}
	}
	
	protected boolean isEmpty(String pStr){
		return (pStr==null || pStr.trim ().length () == 0);
	}
	
	
}
