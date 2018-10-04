package com.forest.common.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.forest.common.bean.DBBaseBean;
import com.forest.common.bean.DataObject;
import com.forest.common.constants.SearchConst;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;

public class BaseService
{
	private Logger logger = Logger.getLogger (BaseService.class);
	protected Connection _dbConnection = null;
	
	protected int countRecord(StringBuffer defaultQuery, Object[][] whereFields) throws OwnException{
		return countRecord (defaultQuery, null, whereFields);
	}
	
	protected int countRecord(StringBuffer defaultQuery, String criteria, Object[][] whereFields) throws OwnException{		
		int totalRecord = 0;		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int i = 0;
		int psmstCount = 1;
		DataObject field = null;
		StringBuffer mSearchCriteria = new StringBuffer();
		HashMap criteriaMap = new HashMap();
		try{
			if(isNotEmpty (defaultQuery.toString ())){		
				if(whereFields!=null){
					for(i=0; i<whereFields.length; i++){
						field = (DataObject) whereFields[i][0];
						if(field!=null && field.stringValue!=null && !field.stringValue.equals ("")){
							buildSearchFieldNew(mSearchCriteria, "And", (DataObject) whereFields[i][0], (String) whereFields[i][1]);
							if(!field.stringValue.equals ("null")){
								criteriaMap.put (field.name, "");
							}	
						}
					}
				}
				
				if(isNotEmpty (mSearchCriteria.toString ())){
					defaultQuery.append (" Where ").append (mSearchCriteria);
				}
				
				if(criteria!=null)
					defaultQuery.append (" And ").append (criteria).append (" ");
				
				defaultQuery.insert (0, "Select Count(*) As totalRecord From (");
				defaultQuery.append (") tableAlias");
				
				logger.debug ("prepareCount defaultQuery = "+defaultQuery);
				pstmt = _dbConnection.prepareStatement (defaultQuery.toString ());
							
				if(whereFields!=null){
					for(i=0; i<whereFields.length; i++){
						field = (DataObject) whereFields[i][0];
						if(field!=null && field.stringValue!=null && !field.stringValue.equals ("")){
							if(criteriaMap.get (field.name)!=null){
								psmstCount = setParameter (pstmt, psmstCount, field);
							}
						}
					}
				}
				
//				pstmt = prepareCount (query, whereFields);						
				rs = pstmt.executeQuery ();
				if(rs.next ()){
					totalRecord = rs.getInt ("totalRecord");
				}
			}
		}catch(Exception e){
			throw new OwnException(e);
		}finally{
			free (null, pstmt, rs);
		}
		
		return totalRecord;
	}
	
	protected ArrayList retrieveKeys(StringBuffer defaultQuery, String criteria, Object[][] whereFields) throws OwnException{				
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int i = 0;
		int psmstCount = 1;
		DataObject field = null;
		StringBuffer mSearchCriteria = new StringBuffer();
		HashMap criteriaMap = new HashMap();
		ArrayList keyList = new ArrayList();
		HashMap data = new HashMap();
		ResultSetMetaData rsMeta = null;
		try{
			if(isNotEmpty (defaultQuery.toString ())){		
				if(whereFields!=null){
					for(i=0; i<whereFields.length; i++){
						field = (DataObject) whereFields[i][0];
						if(field!=null && field.stringValue!=null && !field.stringValue.equals ("")){
							buildSearchFieldNew(mSearchCriteria, "And", (DataObject) whereFields[i][0], (String) whereFields[i][1]);
							if(!field.stringValue.equals ("null")){
								criteriaMap.put (field.name, "");
							}	
						}
					}
				}
				
				if(isNotEmpty (mSearchCriteria.toString ())){
					defaultQuery.append (" Where ").append (mSearchCriteria);
				}
				
				if(criteria!=null)
					defaultQuery.append (" And ").append (criteria).append (" ");
				
//				defaultQuery.insert (0, "Select Count(*) As totalRecord From (");
//				defaultQuery.append (") tableAlias");
				
				logger.debug ("Retrieve Key Query = "+defaultQuery);
				pstmt = _dbConnection.prepareStatement (defaultQuery.toString ());
							
				if(whereFields!=null){
					for(i=0; i<whereFields.length; i++){
						field = (DataObject) whereFields[i][0];
						if(field!=null && field.stringValue!=null && !field.stringValue.equals ("")){
							if(criteriaMap.get (field.name)!=null){
								psmstCount = setParameter (pstmt, psmstCount, field);
							}
						}
					}
				}
				
//				pstmt = prepareCount (query, whereFields);						
				rs = pstmt.executeQuery ();
				rsMeta = rs.getMetaData();
				while(rs.next ()){
					data = new HashMap();
					data.put(rsMeta.getColumnName(1), rs.getString (1));					
					keyList.add(data);
				}
			}
		}catch(Exception e){
			throw new OwnException(e);
		}finally{
			free (null, pstmt, rs);
		}
		
		return keyList;
	}

	protected PreparedStatement buildPaginationSearch(DBBaseBean bean, StringBuffer mDefaultSearchQuery, Object[][] whereFields, 
			DataObject sortDataObject, String sortDirection, String totalRecordPerPage) throws OwnException{
		return buildPaginationSearch (mDefaultSearchQuery, null, whereFields, null, sortDataObject, sortDirection, totalRecordPerPage);
	}
	
	protected ArrayList paginationSearch(DBBaseBean bean, StringBuffer mDefaultSearchQuery, StringBuffer additionalCriteria, 
			Object[][] whereFields, String[] secondOrderBy, 
			DataObject sortDataObject, String sortDirection, String totalRecordPerPage) throws OwnException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList listRecord = new ArrayList();
		HashMap mapRecord = null;
		ResultSetMetaData rsMeta = null;
		try{
			pstmt = buildPaginationSearch(mDefaultSearchQuery, additionalCriteria, whereFields, secondOrderBy, sortDataObject, sortDirection, totalRecordPerPage);
			rs = pstmt.executeQuery();
			String value = null;
			rsMeta = rs.getMetaData();
			while(rs.next ()){								
				mapRecord = new HashMap();
				for(int i=0; i<rsMeta.getColumnCount(); i++){			
					value = rs.getString (i);	
					mapRecord.put (rsMeta.getColumnName(i), value);
				}								
				listRecord.add (mapRecord);
			}	
		}catch(Exception e){
			throw new OwnException(e);
		}finally{
			free(null, pstmt, rs);
		}
		return listRecord;
	}
	
	protected PreparedStatement buildPaginationSearch(StringBuffer mDefaultSearchQuery, StringBuffer additionalCriteria, 
			Object[][] whereFields, String[] secondOrderBy, 
			DataObject sortDataObject, String sortDirection, String totalRecordPerPage) throws OwnException{
		
		PreparedStatement pstmt = null;		
		// Common Search Pagination
//		DataObject sortDataObject = null;			
		StringBuffer OrderBy = new StringBuffer();
		StringBuffer mSearchCriteria = new StringBuffer();		
		
		int i=0;
		int psmstCount = 1;
		
		DataObject field = null;
		boolean createSortObject = false;
		HashMap criteriaMap = new HashMap();
		
		int numberOfField = 0;
		
		StringBuffer fieldBuffer = new StringBuffer();
		
		
		try{		
			// Build [field.name] = ?			
			for(i=0; i<whereFields.length; i++){
				numberOfField = whereFields[i].length / 2;

				fieldBuffer = new StringBuffer();
				for(int x=0; x<numberOfField; x++){
					field = (DataObject) whereFields[i][(x*2)];	
					
					if(field!=null && field.stringValue!=null && !field.stringValue.equals ("")){												
						buildSearchFieldNew(fieldBuffer, field.joinType, field, (String) whereFields[i][(x*2)+1]);
						if(!field.stringValue.equals ("null")){
							criteriaMap.put (field.name, "");
						}						
					}
				}
				
				if(numberOfField>1){
					if(fieldBuffer.length ()>0){
						if(mSearchCriteria.toString ().trim ().length ()>0){
							mSearchCriteria.append (" And");
						}
						mSearchCriteria.append (" (").append (fieldBuffer).append (")");							
					}
				}else{
					if(fieldBuffer.length ()>0){
						if(mSearchCriteria.length ()>0){
							mSearchCriteria.append (" ").append (field.joinType).append (" ").append (fieldBuffer);
						}else{
							mSearchCriteria.append (" ").append (fieldBuffer).append (" ");
						}
					}						
				}

			}
			if(additionalCriteria!=null && additionalCriteria.length () > 0){
				mSearchCriteria.append (" And ").append (additionalCriteria).append (" ");
			}
//			logger.debug ("mSearchCriteria = "+mSearchCriteria);
			
			if(isEmpty (sortDirection)){
				sortDirection = "";
			}else{
				sortDirection = " " + sortDirection + " ";
			}
			if(isEmpty (totalRecordPerPage)){
				totalRecordPerPage = "50";
			}else if("*".equals (totalRecordPerPage)){
				totalRecordPerPage = "";
			}			
			
//			if(sortFieldName!=null && !"".equals (sortFieldName)){			
//				logger.debug ("sortFieldName = "+sortFieldName);
//				sortDataObject = bean.getDataObject (sortFieldName.substring (sortFieldName.indexOf (".")+1, sortFieldName.length ()));
//				sortDataObject.prefix = sortFieldName.substring (0, sortFieldName.indexOf (".")+1);
//			}			

//			if(isNotEmpty (firstView) && isNotEmpty (lastView)){
//				if(sortDataObject!=null){
//					sortDataObject.searchOption = SearchConst.BETWEEN;
//					buildSearchBetweenField(mSearchCriteria, "And", sortDataObject, firstView, lastView);
//					OrderBy.append (" ASC ");
//				}
//			}else{
//				if(isNotEmpty (lastView)){ // When click on Next					
//					if(sortDataObject!=null){
//						sortDataObject.searchOption = SearchConst.GREATTHAN;
//						sortDataObject.stringValue = lastView;
//						
//						buildSearchFieldNew (mSearchCriteria, "And", sortDataObject, sortDataObject.prefix);				
//						OrderBy.append (" ASC ");
//						createSortObject = true;
//					}
//				}
//				
//				if(isNotEmpty (firstView)){ // When click on prev		
//					if(sortDataObject!=null){
//						sortDataObject.searchOption = SearchConst.LESSTHAN;
//						sortDataObject.stringValue = firstView;
//						
//						buildSearchFieldNew(mSearchCriteria, "And", sortDataObject, sortDataObject.prefix);						
//						OrderBy.append (" DESC ");
//						createSortObject = true;
//					}
//				}
//			}
			if(isNotEmpty (mSearchCriteria.toString ())){
				if(mDefaultSearchQuery.lastIndexOf (")") < mDefaultSearchQuery.toString ().toUpperCase ().lastIndexOf (" WHERE ")){
					// Got Where before )
					mDefaultSearchQuery.append (" And (").append (mSearchCriteria).append (")");
				}else{
					mDefaultSearchQuery.append (" Where ").append (mSearchCriteria);
				}
				
			}
			if(OrderBy.length ()>0){
				mDefaultSearchQuery.append (" Order By ").append (sortDataObject.name).append (OrderBy);
			}
			else if(sortDataObject!=null){
				mDefaultSearchQuery.append (" Order By ");
				if(isNotEmpty (sortDataObject.prefix)){
					mDefaultSearchQuery.append (sortDataObject.prefix).append (".");
				}
				mDefaultSearchQuery.append (sortDataObject.name).append (sortDirection);
			}
			
			if(isNotEmpty (totalRecordPerPage)){
				mDefaultSearchQuery.append (" Limit ").append ((Integer.parseInt (totalRecordPerPage) + 1));
			}
			
			mDefaultSearchQuery.insert (0, "Select * From (");
			mDefaultSearchQuery.append(") srch ");
			if(secondOrderBy!=null){
				mDefaultSearchQuery.append ("Order By ").append (secondOrderBy[0]).append (" ").append (secondOrderBy[1]);
			}
						
			logger.debug ("sql:"+mDefaultSearchQuery);
			pstmt = _dbConnection.prepareStatement (mDefaultSearchQuery.toString ());
						
			for(i=0; i<whereFields.length; i++){
				for(int x=0; x<(whereFields[i].length / 2); x++){
					field = (DataObject) whereFields[i][(x*2)];				
					if(criteriaMap.get (field.name)!=null){
						psmstCount = setParameter (pstmt, psmstCount, field);
					}
				}
			}		
			if(createSortObject){
				psmstCount = setParameter (pstmt, psmstCount, sortDataObject);
			}
			
			// Sort Field Put Last
			
		}catch(Exception e){
			logger.error (e, e);
			throw new OwnException(e);
		}
		
		return pstmt;
	}
	
	private DataSource getDataSource() throws OwnException {
		DataSource dataSource = null;
		try {			
			String m_sJNDI = ResourceUtil.getSettingValue ("database.jndi");
//			logger.debug ("m_sJNDI = "+m_sJNDI);
			Context ctx = new InitialContext();
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			dataSource = (DataSource)envContext.lookup(m_sJNDI);		
			
		} catch (Exception e) {
			throw new OwnException("9000");
		}
		return dataSource;
	}
	
	public Connection getDbConnection() throws OwnException{
		Connection conn = null;
		try{
			conn = getDataSource ().getConnection ();
		}catch(Exception e){
			throw new OwnException(e);
		}
		return conn;
	}	
	
	public Connection getDirectConnection() throws Exception{
	    Connection connection = null;	   
	    
	    String url = ResourceUtil.getSettingValue ("db.url");
	    String driver = ResourceUtil.getSettingValue ("db.driver");	    
	    String userID = ResourceUtil.getSettingValue ("db.userid");
	    String password = ResourceUtil.getSettingValue ("db.password");

	    Properties properties = new Properties();
	    properties.put ( "user", userID );
	    properties.put ( "password", password );
	    properties.put ( "charset", "utf8" );	    
	    
	    logger.debug ( "Connection url: '" + url + "'" );
	    try {	    	
	    	Class.forName (driver).newInstance();
	    	connection = DriverManager.getConnection ( url, properties );
	    	if(connection == null) {
	    		throw new Exception("Connection Failed.");	      
	    	}
	    }
	    catch ( Exception e ) {
	      throw e;
	    }
	    
	    return connection;
	}
	
	public void free(Connection p_oConn, PreparedStatement p_oPstmt, ResultSet p_oRs){
		try{
			if(p_oRs!=null){
				p_oRs.close ();				
			}
			
			if(p_oPstmt!=null){
				p_oPstmt.close ();
			}
			
			if(p_oConn!=null && !p_oConn.isClosed ()){
				p_oConn.close ();
			}
			
			p_oRs = null;
			p_oPstmt = null;
			p_oConn=null;
		}catch(Exception e){
			logger.error (e, e);
		}
	}
	
	protected void free(Connection p_oConn, Statement p_oPstmt, ResultSet p_oRs){
		try{
			if(p_oRs!=null){
				p_oRs.close ();				
			}
			
			if(p_oPstmt!=null){
				p_oPstmt.close ();
			}
			
			if(p_oConn!=null && !p_oConn.isClosed ()){
				p_oConn.close ();
			}
			
			p_oRs = null;
			p_oPstmt = null;
			p_oConn=null;
		}catch(Exception e){
			logger.error (e, e);
		}
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
	
	protected int executeInsert(String TABLE, Object[] createFields) throws OwnException{		
		StringBuffer m_oQuery = new StringBuffer();		
		DataObject dataObject = null;
		m_oQuery.append ("Insert Into ").append (TABLE).append (" (");
		
		int i = 0;		
		int count = createFields.length;
		int psmstCount = 1;		
		int autoIncValue = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{						
			for(i=0; i<count; i++){
				dataObject = (DataObject) createFields[i];
				m_oQuery.append (dataObject.name);
				if((i+1)<count){
					m_oQuery.append (",");
				}
			}
			
			m_oQuery.append (") Values(");
			for(i=0; i<count; i++){
				dataObject = (DataObject) createFields[i];
				if(DBBaseBean.UPDATEDATE.equals (dataObject.name)){
					m_oQuery.append ("Now()");
				}else{
					m_oQuery.append ("?");
				}
				
				if((i+1)<count){
					m_oQuery.append (",");
				}
			}
			m_oQuery.append (")");
			logger.debug ("executeInsert = "+m_oQuery);
			pstmt = _dbConnection.prepareStatement (m_oQuery.toString (),PreparedStatement.RETURN_GENERATED_KEYS);
			
			for(i=0; i<count; i++){
				dataObject = (DataObject) createFields[i];
				if(!DBBaseBean.UPDATEDATE.equals (dataObject.name)){
					psmstCount = setParameter (pstmt, psmstCount, dataObject);	
				}
			}
			
			pstmt.execute ();
			
			rs = pstmt.getGeneratedKeys ();
			if (rs.next()) {
				autoIncValue = rs.getInt(1);
		    }
			
		}catch(Exception e){
			throw new OwnException(e);
		}finally{
			free(null, pstmt, null);
		}
		
		return autoIncValue;
	}
	
	protected int executeUpdate(String TABLE, Object[] updateFields, Object[] whereFields) throws OwnException{		
		StringBuffer m_oQuery = new StringBuffer();
		StringBuffer mCriteria = new StringBuffer();
		DataObject updateObject = null;
		m_oQuery.append ("Update ").append (TABLE).append (" Set ");
		
		int i = 0;
		int x = 0;
		int count = updateFields.length;
		int psmstCount = 1;
		int updateValue = 0;
		
		HashMap searchField = new HashMap();
		PreparedStatement pstmt = null;
		try{
			for(x=0; x<whereFields.length; x++){
				updateObject = (DataObject) whereFields[x];				
				searchField.put (updateObject.name, "");
			}			
			for(i=0; i<count; i++){
				updateObject = (DataObject) updateFields[i];
//				logger.debug (updateObject.name + "=" + updateObject.stringValue);
				if(DBBaseBean.UPDATEDATE.equals (updateObject.name)){
					m_oQuery.append (updateObject.name).append (" = NOW() ").append (", ");
				}else{
					if(updateObject.stringValue!=null && searchField.get (updateObject.name)==null){
						m_oQuery.append (updateObject.name).append (" = ?, ");
					}
				}								
			}
			
			m_oQuery = new StringBuffer(m_oQuery.substring (0, m_oQuery.length () - 2));
			if(whereFields.length>0){
				for(i=0; i<whereFields.length; i++){					
					buildSearchFieldNew(mCriteria, "And", (DataObject) whereFields[i], "");
				}
				
				if(mCriteria.length ()>0){					
					m_oQuery.append (" Where ").append (mCriteria);
				}
				logger.debug ("Update m_oQuery = "+m_oQuery);
			}
			
			pstmt = _dbConnection.prepareStatement (m_oQuery.toString ());
			
			for(i=0; i<count; i++){
				updateObject = (DataObject) updateFields[i];
				
				if(updateObject.stringValue!=null && searchField.get (updateObject.name)==null){	
					if(!DBBaseBean.UPDATEDATE.equals (updateObject.name)){						
						psmstCount = setParameter (pstmt, psmstCount, updateObject);						
					}								
				}
			}
			
			for(i=0; i<whereFields.length; i++){
				psmstCount = setParameter (pstmt, psmstCount, (DataObject) whereFields[i]);
			}			
			
			updateValue = pstmt.executeUpdate ();
		}catch(Exception e){
			throw new OwnException(e);
		}finally{
			free(null, pstmt, null);
		}
		
		return updateValue;
	}
	
//	protected int executeUpdate(StringBuffer p_oQuery)throws OwnException{		
//		PreparedStatement m_oStmt = null;
//		ResultSet rs = null;
//		int updateValue = 0;
//		try{
//			m_oStmt = _dbConnection.prepareStatement (p_oQuery.toString ());
//			updateValue = m_oStmt.executeUpdate ();					
//			return updateValue;
//		}catch(Exception e){
//			throw new OwnException(e);
//		}finally{
//			free(null, m_oStmt, rs);
//		}
//	}
	
//	protected boolean isTableExist(String p_sTableName)throws OwnException{
//		boolean m_bExist = false;		
//		PreparedStatement m_oStmt = null;
//		ResultSet m_oRs = null;
//		try{			
//			m_oStmt = _dbConnection.prepareStatement ("Select * From " + p_sTableName + " Where 1=1");
//			m_oRs = m_oStmt.executeQuery ();
//			if(m_oRs.next ()){
//				m_bExist = true;
//			}
//		}catch(Exception e){			
//			m_bExist = false;
//		}finally{
//			free(null, m_oStmt, null);
//		}
//		
//		return m_bExist;
//	}
	
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
	
	protected int setParameter(PreparedStatement pStmt, int paramIndex, DataObject pObject)throws OwnException{
		try{
//			if(pObject.stringValue!=null && !"".equals (pObject.stringValue)){
//				logger.debug ("Set " + pObject.name + " " + paramIndex + ", " + pObject.stringValue);
				if(pObject.stringValue==null){
					pStmt.setNull (paramIndex, pObject.type);
				}else{					
					if(pObject.type == Types.DATE){
						if("".equals (pObject.stringValue)){
							pStmt.setNull (paramIndex, pObject.type);
						}else{
							pStmt.setString (paramIndex, pObject.stringValue);
						}
					}
					if(pObject.type == Types.DOUBLE){
						pStmt.setDouble (paramIndex, Double.parseDouble (pObject.stringValue));
					}
					if(pObject.type == Types.INTEGER){
						if("".equals (pObject.stringValue) || "null".equals (pObject.stringValue)){							
							pStmt.setNull (paramIndex, pObject.type);
						}else{
							pStmt.setInt (paramIndex, Integer.parseInt (pObject.stringValue));	
						}						
					}
					if(pObject.type == Types.VARCHAR || pObject.type == Types.CLOB){
						if(pObject.searchOption==SearchConst.LIKE){
							pStmt.setString (paramIndex, "%" + pObject.stringValue + "%");
						}else{
							pStmt.setString (paramIndex, pObject.stringValue);
						}
					}
//					if(pObject.type == Types.CLOB){
//						pStmt.setString (paramIndex, pObject.stringValue);
//					}
				}
				paramIndex++;
//			}
		}catch(Exception e){
			throw new OwnException(e);
		}
		return paramIndex;
	}
	
	protected void buildSearchBetweenField(StringBuffer mSearchCriteria, String operator, DataObject pObject, String betweenFrom, String betweenTo) throws OwnException{
		StringBuffer mReturn = new StringBuffer();
		
		if(pObject!=null){
			int searchType = pObject.searchOption;
			mReturn.append(" ");
			if(!"".equals (pObject.prefix)){
				mReturn.append (pObject.prefix);	
			}
			mReturn.append (pObject.name);		
			switch(searchType){
				case SearchConst.BETWEEN: 
					if(pObject.type == Types.VARCHAR || pObject.type == Types.CLOB || pObject.type == Types.DATE){
						mReturn.append (" Between '").append(betweenFrom).append ("' And '").append (betweenTo).append ("'");
					}					
					if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
						if(pObject.stringValue.equalsIgnoreCase ("null")){
							mReturn.append (" Is ").append (pObject.stringValue);
						}else{
							mReturn.append (" = ").append (pObject.stringValue);
						}						
					}
					break;
			}
			
			if(mSearchCriteria.length ()>0){
				if(mReturn.length ()>0){
					mReturn.insert (0, " " + operator + " ");
				}
			}
			mSearchCriteria.append (mReturn);
		}
	}
//	protected void buildSearchField(StringBuffer mSearchCriteria, String operator, DataObject pObject) throws OwnException{
//		buildSearchField(mSearchCriteria, operator, pObject, null);
//	}
	
	protected void buildSearchFieldNew(StringBuffer mSearchCriteria, String operator, DataObject pObject, String prefix) throws OwnException{
		StringBuffer mReturn = new StringBuffer();
		
		if(prefix!=null && prefix.length ()>0){
			prefix += ".";
		}else{
			prefix = "";
		}
		try{
//		if(pObject!=null && pObject.stringValue!=null && !pObject.stringValue.equals ("")){
			int searchType = pObject.searchOption;
			mReturn.append(" ").append (prefix).append (pObject.name);		
			switch(searchType){
				case SearchConst.EQUAL: 
					if(pObject.type == Types.VARCHAR || pObject.type == Types.CLOB || pObject.type == Types.DATE){
						if(pObject.stringValue.equalsIgnoreCase ("null")){
							mReturn.append (" Is ").append (pObject.stringValue);
						}else{
							mReturn.append (" = ?");
						}						
					}					
					if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
						if(pObject.stringValue.equalsIgnoreCase ("null")){
							mReturn.append (" Is ").append (pObject.stringValue);
						}else{
							mReturn.append (" = ?");
						}						
					}
					break;						
				case SearchConst.LIKE: 
					if(pObject.type == Types.VARCHAR || pObject.type == Types.CLOB){
						mReturn.append (" Like ?");
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
		}catch (Exception e) {
			logger.debug(pObject.name);
			logger.error(e, e);
			throw new OwnException(e);
		}
	}
	
	
	
//	public PreparedStatement prepare(StringBuffer defaultQuery, Object[][] searchFields)throws OwnException{
//		PreparedStatement pstmt = null;
//		int i=0;
//		int psmstCount = 1;
//		StringBuffer mSearchCriteria = new StringBuffer();
//		try{
//			for(i=0; i<searchFields.length; i++){
//				buildSearchFieldNew(mSearchCriteria, "And", (DataObject) searchFields[i][0], (String) searchFields[i][1]);
//			}
//			
//			if(isNotEmpty (mSearchCriteria.toString ())){
//				defaultQuery.append (" Where ").append (mSearchCriteria);
//			}
//			
//			logger.debug ("prepare defaultQuery = "+defaultQuery);
//			pstmt = _dbConnection.prepareStatement (defaultQuery.toString ());
//						
//			for(i=0; i<searchFields.length; i++){
//				psmstCount = setParameter (pstmt, psmstCount, (DataObject) searchFields[i][0]);
//			}			
//		}catch(Exception e){			
//			throw e;
//		}
//		
//		return pstmt;
//	}
//	protected void buildSearchField(StringBuffer mSearchCriteria, String operator, DataObject pObject, String prefix) throws OwnException{
//		StringBuffer mReturn = new StringBuffer();
//		
//		if(prefix!=null && prefix.length ()>0){
//			prefix += ".";
//		}else{
//			prefix = "";
//		}
//		if(pObject!=null && pObject.stringValue!=null && !pObject.stringValue.equals ("")){
//			int searchType = pObject.searchOption;
//			mReturn.append(" ").append (prefix).append (pObject.name);		
//			switch(searchType){
//				case SearchConst.EQUAL: 
//					if(pObject.type == Types.VARCHAR || pObject.type == Types.CLOB || pObject.type == Types.DATE){
//						if(pObject.stringValue.equalsIgnoreCase ("null")){
//							mReturn.append (" Is ").append (pObject.stringValue);
//						}else{
//							mReturn.append (" = '").append (pObject.stringValue).append ("'");
//						}						
//					}					
//					if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
//						if(pObject.stringValue.equalsIgnoreCase ("null")){
//							mReturn.append (" Is ").append (pObject.stringValue);
//						}else{
//							mReturn.append (" = ").append (pObject.stringValue);
//						}						
//					}
//					break;						
//				case SearchConst.LIKE: 
//					if(pObject.type == Types.VARCHAR || pObject.type == Types.CLOB){
//						mReturn.append (" Like '%").append (pObject.stringValue).append ("%'");
//					}						
//					break;
//				case SearchConst.BETWEEN: 
//					
//					break;
//				case SearchConst.GREATTHAN: 
//					if(pObject.type == Types.VARCHAR || pObject.type == Types.DATE){
//						mReturn.append (" > '").append (pObject.stringValue).append ("'");
//					}
//					if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
//						mReturn.append (" > ").append (pObject.stringValue);
//					}
//					break;
//				case SearchConst.GREATTHAN_EQUAL: 
//					if(pObject.type == Types.VARCHAR || pObject.type == Types.DATE){
//						mReturn.append (" >= '").append (pObject.stringValue).append ("'");
//					}
//					if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
//						mReturn.append (" >= ").append (pObject.stringValue);
//					}
//					break;
//				case SearchConst.LESSTHAN: 
//					if(pObject.type == Types.VARCHAR || pObject.type == Types.DATE){
//						mReturn.append (" < '").append (pObject.stringValue).append ("'");
//					}
//					if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
//						mReturn.append (" < ").append (pObject.stringValue);
//					}
//					break;
//				case SearchConst.LESSTHAN_EQUAL: 
//					if(pObject.type == Types.VARCHAR || pObject.type == Types.DATE){
//						mReturn.append (" <= '").append (pObject.stringValue).append ("'");
//					}
//					if(pObject.type == Types.DOUBLE || pObject.type == Types.INTEGER){
//						mReturn.append (" <= ").append (pObject.stringValue);
//					}
//					break;
//				default:
//					throw new OwnException("9999");					
//			}				
//		}
//		
//		if(mSearchCriteria.length ()>0){
//			if(mReturn.length ()>0){
//				mReturn.insert (0, " " + operator + " ");
//			}
//		}
//		
//		mSearchCriteria.append (mReturn);		
//	}
	
	protected String nullToEmpty(String pObj){		
		return (pObj==null)?"":pObj;		
	}
	
	protected boolean isEmpty(String pStr){
		return (pStr==null || pStr.trim ().length () == 0);
	}
	
	protected boolean isNotEmpty(String pStr){
		return (pStr!=null && pStr.trim ().length () > 0);
	}
}
