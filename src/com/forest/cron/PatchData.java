package com.forest.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.forest.common.services.BaseService;

public class PatchData {
	public Connection conn = null;
	private static Logger logger = Logger.getLogger(PatchData.class);
	
	public PatchData() {
		BaseService baseServ = new BaseService();	
		try {
			conn = baseServ.getDirectConnection();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {		
		PatchData cc = new PatchData();
		try{			
			logger.info("Start Patch");
			cc.conn.setAutoCommit(false);
			cc.patchProductRoundUpPrefix();
			cc.conn.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally{			
			logger.info("End Patch");
		}
	}
	
	private void patchProductRoundUpPrefix() {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;
		String updateSql = "Update mfg_custproduct set roundup_prefix = 'ls' where companyid = 'lsm' and code = ? and name = ?";
		String sql = "select code, name from mfg_custproduct where companyid = 'ls' and status = 'A'";
		try{			
			pstmtUpdate = conn.prepareStatement(updateSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				pstmtUpdate.setString(1, rs.getString("code"));
				pstmtUpdate.setString(2, rs.getString("name"));				
				pstmtUpdate.execute ();				
			}
			logger.info("Patch Product...");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
