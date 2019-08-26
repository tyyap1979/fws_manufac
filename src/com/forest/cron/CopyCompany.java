package com.forest.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.forest.common.services.BaseService;

public class CopyCompany {
	public Connection conn = null;
	private static final String NEW_COMPANY_ID = "lsm";
	private static final String COMPANY_IDS = "('ls', 'eurotrend')";
	private static Logger logger = Logger.getLogger(CopyCompany.class);
	
	public CopyCompany() {
		BaseService baseServ = new BaseService();	
		try {
			conn = baseServ.getDirectConnection();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {		
		CopyCompany cc = new CopyCompany();
		try{			
			logger.info("Copy Company Start");						
			cc.conn.setAutoCommit(false);
			
//			Map<Integer, Integer> from2NewProdOptMap = cc.copyProductOption();
//			cc.copyProductOptionDetail(from2NewProdOptMap);
//			
//			Map<Integer, Integer> from2NewGroupingMap = cc.copyGrouping();
//			Map<Integer, Integer> from2NewProductMap = cc.copyProduct(from2NewGroupingMap);
//			
//			cc.copyProductSelection(from2NewProductMap, from2NewProdOptMap);
//			cc.copyProductPrice(from2NewProductMap);
			
			cc.copyAgentProfile();
			cc.conn.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally{			
			logger.info("Copy Company End");
		}
	}

	private Map<Integer, Integer> copyProduct(Map<Integer, Integer> from2NewGroupingMap) {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		ResultSet rsInsert = null;
		Map<Integer, Integer> from2NewMap = new HashMap<>();
		String insertSql = "INSERT INTO mfg_custproduct ( companyid, code, name, description, grouptype, sellunittype, minorder, customise, customformula, status, updateby, updatedate ) " + 
				"VALUES ('"+NEW_COMPANY_ID+"',?,?,?,?,?,?,?,?,'A','SYSTEM', now())";
		String sql = "Select * from mfg_custproduct where status = 'A' and companyid In " + COMPANY_IDS;
		try{			
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				pstmtInsert.setString(1, rs.getString("code"));
				pstmtInsert.setString(2, rs.getString("name"));
				pstmtInsert.setString(3, rs.getString("description"));
				pstmtInsert.setString(4, String.valueOf(from2NewGroupingMap.get(rs.getInt("grouptype"))));
				pstmtInsert.setString(5, rs.getString("sellunittype"));
				pstmtInsert.setString(6, rs.getString("minorder"));
				pstmtInsert.setString(7, rs.getString("customise"));
				pstmtInsert.setString(8, rs.getString("customformula"));				
				pstmtInsert.execute ();
				
				rsInsert = pstmtInsert.getGeneratedKeys ();
				if (rsInsert.next()) {
					logger.debug(rs.getInt("prodid") + " -> " + rsInsert.getInt(1));
					from2NewMap.put(rs.getInt("prodid"), rsInsert.getInt(1)) ;
			    }
			}
			logger.info("Copied Product...");
		}catch(Exception e){
			e.printStackTrace();
		}
		return from2NewMap;
	}
	
	private void copyProductSelection(Map<Integer, Integer> from2NewProductMap, Map<Integer, Integer> from2NewProdOptMap) {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;

		String insertSql = "INSERT INTO mfg_custproductselection ( companyid, prodid, prodoptid, position ) VALUES ('"+NEW_COMPANY_ID+"',?, ?, ?)";
		String sql = "Select * from mfg_custproductselection where companyid In " + COMPANY_IDS;
		try{			
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				if(from2NewProductMap.get(rs.getInt("prodid"))==null) {
					continue;
				}				
				pstmtInsert.setInt(1, from2NewProductMap.get(rs.getInt("prodid")));
				pstmtInsert.setInt(2, from2NewProdOptMap.get(rs.getInt("prodoptid")));
				pstmtInsert.setInt(3, rs.getInt("position"));
				pstmtInsert.execute ();				
			}
			logger.info("Copied Product Selection...");
		}catch(Exception e){			
			e.printStackTrace();
		}
	}
	
	private void copyProductPrice(Map<Integer, Integer> from2NewProductMap) {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		String insertSql = "INSERT INTO mfg_custproductprice " + 
				"( prodid, orderfrom, orderto, cost, dealerprice, clientprice, publicprice, dealer1price, client1price, public1price, dealer2price, client2price, public2price ) " + 
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String sql = "Select * from mfg_custproductprice";
		try{			
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				if(from2NewProductMap.get(rs.getInt("prodid"))==null) {
					continue;
				}
				pstmtInsert.setInt(1, from2NewProductMap.get(rs.getInt("prodid")));
				pstmtInsert.setDouble(2, rs.getDouble("orderfrom"));
				pstmtInsert.setDouble(3, rs.getDouble("orderto"));
				pstmtInsert.setDouble(4, rs.getDouble("cost"));
				pstmtInsert.setDouble(5, rs.getDouble("dealerprice"));
				pstmtInsert.setDouble(6, rs.getDouble("clientprice"));
				pstmtInsert.setDouble(7, rs.getDouble("publicprice"));
				pstmtInsert.setDouble(8, rs.getDouble("dealer1price"));
				pstmtInsert.setDouble(9, rs.getDouble("client1price"));
				pstmtInsert.setDouble(10, rs.getDouble("public1price"));
				pstmtInsert.setDouble(11, rs.getDouble("dealer2price"));
				pstmtInsert.setDouble(12, rs.getDouble("client2price"));
				pstmtInsert.setDouble(13, rs.getDouble("public2price"));

				pstmtInsert.execute ();				
			}
			logger.info("Copied Product Price...");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Map<Integer, Integer> copyGrouping() {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		ResultSet rsInsert = null;
		Map<Integer, Integer> from2NewMap = new HashMap<>();
		String insertSql = "INSERT INTO mfg_grouping ( companyid, groupname, grouptype, status, updateby, updatedate ) VALUES ('"+NEW_COMPANY_ID+"',?, ?,'A', 'SYSTEM', now())";
		String sql = "Select * from mfg_grouping where status = 'A' and companyid In " + COMPANY_IDS;
		try{			
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				pstmtInsert.setString(1, rs.getString("groupname"));
				pstmtInsert.setString(2, rs.getString("grouptype"));
				pstmtInsert.execute ();
				
				rsInsert = pstmtInsert.getGeneratedKeys ();
				if (rsInsert.next()) {
					logger.debug(rs.getInt("groupid") + " -> " + rsInsert.getInt(1));
					from2NewMap.put(rs.getInt("groupid"), rsInsert.getInt(1)) ;
			    }
			}
			logger.info("Copied Group...");
		}catch(Exception e){
			e.printStackTrace();
		}
		return from2NewMap;
	}
	
	public Map<Integer, Integer> copyProductOption() {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		ResultSet rsInsert = null;
		Map<Integer, Integer> from2NewMap = new HashMap<>();
		String insertSql = "Insert Into mfg_productopt ( companyid, groupname, description, updateby, updatedate, status ) Values('" + NEW_COMPANY_ID +"', ?, ?, 'SYSTEM', now(), 'A')";
		String sql = "Select prodoptid, groupname, description from mfg_productopt where status = 'A' and companyid In " + COMPANY_IDS;
		try{
			logger.info("Start copying ");
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				pstmtInsert.setString(1, rs.getString("groupname"));
				pstmtInsert.setString(2, rs.getString("description"));
				pstmtInsert.execute ();
				
				rsInsert = pstmtInsert.getGeneratedKeys ();
				if (rsInsert.next()) {
					logger.debug(rs.getInt("prodoptid") + " -> " + rsInsert.getInt(1));
					from2NewMap.put(rs.getInt("prodoptid"), rsInsert.getInt(1)) ;
			    }
			}
			logger.info("Copied Product Option...");
		}catch(Exception e){
			e.printStackTrace();
		}
		return from2NewMap;
	}
	
	public void copyProductOptionDetail(Map<Integer, Integer> from2NewProdOptMap) {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;		
		
		String insertSql = "INSERT INTO mfg_productopt_detail ( companyid, prodoptid, code, name, cost, dealerprice, clientprice, publicprice, sellunittype, position, formula, status ) " + 
				"VALUES ('"+NEW_COMPANY_ID+"', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,'A')";
		String sql = "Select * from mfg_productopt_detail where status = 'A' and companyid In " + COMPANY_IDS;
		try{
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				pstmtInsert.setInt(1, from2NewProdOptMap.get(rs.getInt("prodoptid")));
				pstmtInsert.setString(2, rs.getString("code"));
				pstmtInsert.setString(3, rs.getString("name"));
				pstmtInsert.setDouble(4, rs.getDouble("cost"));
				pstmtInsert.setDouble(5, rs.getDouble("dealerprice"));
				pstmtInsert.setDouble(6, rs.getDouble("clientprice"));
				pstmtInsert.setDouble(7, rs.getDouble("publicprice"));
				pstmtInsert.setString(8, rs.getString("sellunittype"));
				pstmtInsert.setInt(9, rs.getInt("position"));
				pstmtInsert.setString(10, rs.getString("formula"));
				pstmtInsert.execute ();
			}
			
			logger.info("Copied Product Option Detail...");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void copyAgentProfile() {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;		
		
		String insertSql = "INSERT INTO agentprofile ( companyid, name, mobileno, email, gender, dob, address, city, state, postcode, country, status, updateby, updatedate ) " + 
				"VALUES ('"+NEW_COMPANY_ID+"', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,'A', 'SYSTEM', now())";
		String sql = "Select * from agentprofile where status = 'A' and companyid In ('ls')";
		try{
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				pstmtInsert.setString(1, rs.getString("name"));
				pstmtInsert.setString(2, rs.getString("mobileno"));
				pstmtInsert.setString(3, rs.getString("email"));
				pstmtInsert.setString(4, rs.getString("gender"));
				pstmtInsert.setString(5, rs.getString("dob"));
				pstmtInsert.setString(6, rs.getString("address"));
				pstmtInsert.setString(7, rs.getString("city"));
				pstmtInsert.setString(8, rs.getString("state"));
				pstmtInsert.setString(9, rs.getString("postcode"));
				pstmtInsert.setString(10, rs.getString("country"));
				pstmtInsert.execute ();
			}
			
			logger.info("Copied Product Option Detail...");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
