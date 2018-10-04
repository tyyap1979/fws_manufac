package com.forest.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.forest.common.util.DBUtil;

public class PatchSalesDate {	
	private Logger logger = Logger.getLogger (PatchSalesDate.class);
	
	public void patch(Connection conn){
		PreparedStatement pstmtSelect = null;
		PreparedStatement pstmtUpdate = null;
		StringBuffer queryUpdate = new StringBuffer();
		StringBuffer querySelect = new StringBuffer();
		ResultSet rs = null;
		int rowUpdated = 0;
		try{
			querySelect.append("select b.stmtdtlid, a.salesdate, b.duedate, a.terms from mfg_transaction a inner join mfg_statement_detail b on b.transid = a.transid where a.salesdate != b.salesdate and b.status = 0");
			queryUpdate.append("Update mfg_statement_detail Set Salesdate = ?, duedate = date_add(?, INTERVAL ? DAY) Where stmtdtlid = ?");
			pstmtUpdate = conn.prepareStatement(queryUpdate.toString());
			
			pstmtSelect = conn.prepareStatement(querySelect.toString());
			rs = pstmtSelect.executeQuery();
			while(rs.next()){
				logger.debug("stmtdtlid= "+rs.getString("stmtdtlid"));
				pstmtUpdate.setString(1, rs.getString("salesdate"));
				pstmtUpdate.setString(2, rs.getString("salesdate"));
				
				pstmtUpdate.setString(3, rs.getString("terms"));
				pstmtUpdate.setString(4, rs.getString("stmtdtlid"));
				rowUpdated = pstmtUpdate.executeUpdate();
				logger.debug("rowUpdated = "+rowUpdated);				
			}
			
		}catch (Exception e) {
			logger.error(e, e);
		}finally{
			DBUtil.free(null, pstmtSelect, null);
			DBUtil.free(null, pstmtUpdate, null);
		}
		
	}
}
