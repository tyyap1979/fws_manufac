package com.forest.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import com.forest.common.util.DBUtil;

public class StatementPatch {
	private static Logger logger = Logger.getLogger(StatementPatch.class);
	
	public void patchStatementSalesAmount(Connection conn){
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;			
		StringBuffer query = new StringBuffer();
		StringBuffer queryUpdate = new StringBuffer();
		try{
			logger.info("Patch Statement Amount Start");						
		
			queryUpdate.append("Update ");
			queryUpdate.append("mfg_statement_detail");
			queryUpdate.append(" Set ");
			queryUpdate.append("amount").append("=?");
			queryUpdate.append(" Where ");
			queryUpdate.append("stmtdtlid").append("=?");
			
			pstmtUpdate = conn.prepareStatement(queryUpdate.toString());
			
			query.append("select c.stmtdtlid, a.transid, a.transno, a.salesdate,");
			query.append(" sum(b.sellsubtotal) - a.discount + COALESCE(a.gst_amt,0) As grandtotal,");
			query.append(" sum(b.sellsubtotal) As sellsubtotal, a.discount, a.gst_amt,");
			query.append(" c.amount as stmtamount");
			query.append(" from mfg_transaction a");
			query.append(" Inner Join mfg_transaction_detail b On b.transid = a.transid");
			query.append(" Inner Join mfg_statement_detail c on c.transid = a.transid");
			query.append(" Where a.status = 3 And b.status = 'A' and c.status = 0"); 
			query.append(" group by a.transid, c.amount, a.salesdate");
			query.append(" having sum(b.sellsubtotal) > 0 And c.amount != sum(b.sellsubtotal) - a.discount + COALESCE(a.gst_amt, 0)");
			
			
			pstmt = conn.prepareStatement(query.toString());
			rs = pstmt.executeQuery();
			while(rs.next()){				
				logger.info("Stament Dtl ID: "+rs.getInt("stmtdtlid")+", Grand Total: "+rs.getDouble("grandtotal")+", Statement Amount: "+rs.getString("stmtamount"));
				pstmtUpdate.setDouble(1, rs.getDouble("grandtotal"));
				pstmtUpdate.setInt(2, rs.getInt("stmtdtlid"));
				pstmtUpdate.executeUpdate();
			}
		}catch(Exception e){
			logger.debug(e, e);
		}finally{
			DBUtil.free(null, pstmtUpdate, null);
			DBUtil.free(null, pstmt, null);
			logger.info("Patch Statement Amount End");
		}
	}
}
