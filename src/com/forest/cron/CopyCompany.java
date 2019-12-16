package com.forest.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
//			cc.deleteOldRecord();
			cc.conn.setAutoCommit(false);					
			
//			Map<Integer, Integer> from2NewProdOptMap = cc.copyProductOption();
//			cc.copyProductOptionDetail(from2NewProdOptMap);
//			
//			Map<Integer, Integer> from2NewGroupingMap = cc.copyGrouping();
//			Map<Integer, Integer> from2NewProductMap = cc.copyProduct(from2NewGroupingMap);
//			
//			cc.copyProductSelection(from2NewProductMap, from2NewProdOptMap);
//			Map<Integer, Integer> from2NewPriceMap = cc.copyProductPrice(from2NewProductMap);
//			
//			Map<Integer, Integer> agentMap = cc.copyAgentProfile();
//			Map<Integer, Integer> customerMap = cc.copyCustomerProfile(agentMap);
//			
//			cc.copyCustomerPrice(customerMap, from2NewProductMap, from2NewPriceMap);
			
//			cc.copySupplierProduct();
//			cc.copySupplierProductDetailOption();
//			cc.copySupplierProductOption();
//			cc.copySupplierProductPrice();
//			cc.copySupplierProductSelection();
			cc.copyProductCustomerPriceSupplier();
			cc.conn.commit();
		}catch(Exception e){
			try {
				cc.conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{			
			logger.info("Copy Company End");
		}
	}

	private void deleteOldRecord() throws Exception {
		Statement st = conn.createStatement();
		st.addBatch("Delete From mfg_custproduct Where companyid = '"+NEW_COMPANY_ID+"'");
		st.addBatch("Delete From mfg_productopt Where companyid = '"+NEW_COMPANY_ID+"'");
		st.addBatch("Delete From mfg_productopt_detail Where companyid = '"+NEW_COMPANY_ID+"'");
		st.addBatch("Delete From mfg_grouping Where companyid = '"+NEW_COMPANY_ID+"'");
		st.addBatch("Delete From mfg_custproductselection Where companyid = '"+NEW_COMPANY_ID+"'");
		st.addBatch("Delete From mfg_custproductprice Where prodid IN (Select prodid from mfg_custproduct where companyid = '" + NEW_COMPANY_ID +"')");
		st.addBatch("Delete From mfg_custproductcustomerprice Where companyid = '"+NEW_COMPANY_ID+"'");
		st.addBatch("Delete From agentprofile Where companyid = '"+NEW_COMPANY_ID+"'");
		st.addBatch("Delete From customerprofile Where companyid = '"+NEW_COMPANY_ID+"'");		
		st.executeBatch();
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
	
	private Map<Integer, Integer> copyProductPrice(Map<Integer, Integer> from2NewProductMap) {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		ResultSet rsInsert = null;
		Map<Integer, Integer> from2NewMap = new HashMap<>();
		String insertSql = "INSERT INTO mfg_custproductprice " + 
				"( prodid, orderfrom, orderto, cost, dealerprice, clientprice, publicprice, dealer1price, client1price, public1price, dealer2price, client2price, public2price ) " + 
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String sql = "Select * from mfg_custproductprice where prodid IN (Select prodid from mfg_custproduct where companyid In " + COMPANY_IDS +")";
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
				
				rsInsert = pstmtInsert.getGeneratedKeys ();
				if (rsInsert.next()) {
					logger.debug(rs.getInt("priceid") + " -> " + rsInsert.getInt(1));
					from2NewMap.put(rs.getInt("priceid"), rsInsert.getInt(1)) ;
			    }		
			}
			logger.info("Copied Product Price...");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return from2NewMap;
	}
	
	private void copyCustomerPrice(Map<Integer, Integer> customerMap, Map<Integer, Integer> productMap, Map<Integer, Integer> priceMap) {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;

		String insertSql = "INSERT INTO mfg_custproductcustomerprice ( companyid, customerid, prodid, priceid, price ) "+
							"VALUES ('"+NEW_COMPANY_ID+"',?, ?, ?, ?)";
		String sql = "Select * from mfg_custproductcustomerprice where companyid In " + COMPANY_IDS;
		try{			
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {	
				if(productMap.get(rs.getInt("prodid"))==null || customerMap.get(rs.getInt("customerid"))==null) {
					continue;
				}
				pstmtInsert.setInt(1, customerMap.get(rs.getInt("customerid")));
				pstmtInsert.setInt(2, productMap.get(rs.getInt("prodid")));
				pstmtInsert.setInt(3, priceMap.get(rs.getInt("priceid")));
				pstmtInsert.setDouble(4, rs.getDouble("price"));
				pstmtInsert.execute ();				
			}
			logger.info("Copied Customer Pricing...");
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
	
	public Map<Integer, Integer> copyAgentProfile() {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;	
		ResultSet rsInsert = null;
		Map<Integer, Integer> from2NewMap = new HashMap<>();
		
		String insertSql = "INSERT INTO agentprofile ( companyid, name, mobileno, email, gender, dob, address, city, state, postcode, country, status, updateby, updatedate ) " + 
				"VALUES ('"+NEW_COMPANY_ID+"', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,'A', 'SYSTEM', now())";
		String sql = "Select * from agentprofile where status = 'A' and companyid In " + COMPANY_IDS;
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
				
				rsInsert = pstmtInsert.getGeneratedKeys ();
				if (rsInsert.next()) {
					logger.debug(rs.getInt("id") + " -> " + rsInsert.getInt(1));
					from2NewMap.put(rs.getInt("id"), rsInsert.getInt(1)) ;
			    }
			}
			
			logger.info("Copied Agent Profile...");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return from2NewMap;
	}
	
	public Map<Integer, Integer> copyCustomerProfile(Map<Integer, Integer> agentMap) {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;		
		ResultSet rsInsert = null;
		Map<Integer, Integer> from2NewMap = new HashMap<>();
		String insertSql = "INSERT INTO customerprofile ( companyid, code, name, contactperson, type, creditlimit, terms, address,  "+
				"city, state, postcode, country, email, mobileno, phoneno, faxno, salesby, note, status, updateby, updatedate ) " + 
				"VALUES ('"+NEW_COMPANY_ID+"', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,'A', 'SYSTEM', now())";
		String sql = "Select * from customerprofile where status = 'A' and companyid In " + COMPANY_IDS;
		try{
			pstmtInsert = conn.prepareStatement(insertSql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				pstmtInsert.setString(1, rs.getString("code"));
				pstmtInsert.setString(2, rs.getString("name"));
				pstmtInsert.setString(3, rs.getString("contactperson"));
				pstmtInsert.setString(4, rs.getString("type"));
				pstmtInsert.setString(5, rs.getString("creditlimit"));
				pstmtInsert.setInt(6, rs.getInt("terms"));
				pstmtInsert.setString(7, rs.getString("address"));
				pstmtInsert.setString(8, rs.getString("city"));
				pstmtInsert.setString(9, rs.getString("state"));
				pstmtInsert.setString(10, rs.getString("postcode"));
				pstmtInsert.setString(11, rs.getString("country"));
				pstmtInsert.setString(12, rs.getString("email"));
				pstmtInsert.setString(13, rs.getString("mobileno"));
				pstmtInsert.setString(14, rs.getString("phoneno"));
				pstmtInsert.setString(15, rs.getString("faxno"));
				if(agentMap.get(rs.getInt("salesby"))!=null) {
					pstmtInsert.setInt(16, agentMap.get(rs.getInt("salesby")));
				}else {
					pstmtInsert.setInt(16, 0);
				}				
				pstmtInsert.setString(17, rs.getString("note"));
				pstmtInsert.execute ();
				
				rsInsert = pstmtInsert.getGeneratedKeys ();
				if (rsInsert.next()) {
					logger.debug(rs.getInt("customerid") + " -> " + rsInsert.getInt(1));
					from2NewMap.put(rs.getInt("customerid"), rsInsert.getInt(1)) ;
			    }
			}
			
			logger.info("Copied Customer Profile...");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return from2NewMap;
	}
	
	private void copySupplierProduct() throws Exception{
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		String insertSql = "INSERT INTO mfg_supplierproduct ( prodid, suppcompanyid, status, updateby, updatedate ) " + 
				"VALUES (?,?,'A','SYSTEM', now())";
		String sql = "Select * from mfg_custproduct where status = 'A' and companyid In " + COMPANY_IDS;
		pstmtInsert = conn.prepareStatement(insertSql);
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while(rs.next()) {
			pstmtInsert.setString(1, rs.getString("prodid"));
			pstmtInsert.setString(2, rs.getString("companyid"));
			pstmtInsert.execute ();
		}
		logger.info("Copied Supplier Product...");
	}
	
	private void copySupplierProductOption() throws Exception{
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		String insertSql = "INSERT INTO mfg_supplierproductopt ( prodoptid, suppcompanyid, status, updateby, updatedate ) " + 
				"VALUES (?,?,'A','SYSTEM', now())";
		String sql = "Select * from mfg_productopt where status = 'A' and companyid In " + COMPANY_IDS;
		
		pstmtInsert = conn.prepareStatement(insertSql);
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while(rs.next()) {
			pstmtInsert.setInt(1, rs.getInt("prodoptid"));
			pstmtInsert.setString(2, rs.getString("companyid"));
			pstmtInsert.execute ();
		}
		logger.info("Copied Supplier Product Option...");
	}

	private void copySupplierProductDetailOption() throws Exception {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		
		String insertSql = "INSERT INTO mfg_supplierproductopt_detail ( prodoptdetailid, prodoptid, cost, dealerprice, clientprice, publicprice, status ) " + 
				"VALUES (?,?,?,?,?,?,'A')";
		String sql = "Select * from mfg_productopt_detail where status = 'A' and companyid In " + COMPANY_IDS;
		
		pstmtInsert = conn.prepareStatement(insertSql);
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while(rs.next()) {
			pstmtInsert.setInt(1, rs.getInt("prodoptdetailid"));
			pstmtInsert.setInt(2, rs.getInt("prodoptid"));
			pstmtInsert.setDouble(3, rs.getDouble("cost"));
			pstmtInsert.setDouble(4, rs.getDouble("dealerprice"));
			pstmtInsert.setDouble(5, rs.getDouble("clientprice"));
			pstmtInsert.setDouble(6, rs.getDouble("publicprice"));
			pstmtInsert.execute ();
		}
		logger.info("Copied Supplier Product Detail Option...");
	}
	
	private void copySupplierProductPrice() throws Exception {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		
		String insertSql = "INSERT INTO mfg_supplierproductprice ( priceid, prodid, orderfrom, orderto, cost, dealerprice, clientprice, publicprice, dealer1price, client1price, public1price, dealer2price, client2price, public2price ) " + 
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String sql = "Select * from mfg_custproductprice where prodid In ( " + 
				"Select prodid from mfg_custproduct Where status = 'A' and companyid In " + COMPANY_IDS +")";

		pstmtInsert = conn.prepareStatement(insertSql);
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while(rs.next()) {
			pstmtInsert.setInt(1, rs.getInt("priceid"));
			pstmtInsert.setInt(2, rs.getInt("prodid"));
			pstmtInsert.setDouble(3, rs.getDouble("orderfrom"));
			pstmtInsert.setDouble(4, rs.getDouble("orderto"));				
			pstmtInsert.setDouble(5, rs.getDouble("cost"));
			pstmtInsert.setDouble(6, rs.getDouble("dealerprice"));
			pstmtInsert.setDouble(7, rs.getDouble("clientprice"));
			pstmtInsert.setDouble(8, rs.getDouble("publicprice"));
			pstmtInsert.setDouble(9, rs.getDouble("dealer1price"));
			pstmtInsert.setDouble(10, rs.getDouble("client1price"));
			pstmtInsert.setDouble(11, rs.getDouble("public1price"));
			pstmtInsert.setDouble(12, rs.getDouble("dealer2price"));
			pstmtInsert.setDouble(13, rs.getDouble("client2price"));
			pstmtInsert.setDouble(14, rs.getDouble("public2price"));
			pstmtInsert.execute ();
		}
		logger.info("Copied Supplier Product Price...");
	}
	
	private void copySupplierProductSelection() throws Exception {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		String insertSql = "INSERT INTO mfg_supplierproductselection ( selectid, prodid, prodoptid, position ) " + 
				"VALUES (?,?,?,?)";
		String sql = "Select * from mfg_custproductselection where companyid In " + COMPANY_IDS;

		pstmtInsert = conn.prepareStatement(insertSql);
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while(rs.next()) {
			pstmtInsert.setInt(1, rs.getInt("selectid"));
			pstmtInsert.setInt(2, rs.getInt("prodid"));
			pstmtInsert.setInt(3, rs.getInt("prodoptid"));
			pstmtInsert.setInt(4, rs.getInt("position"));
			pstmtInsert.execute ();
		}
		logger.info("Copied Supplier Product Selection...");
	}
	
	private void copyProductCustomerPriceSupplier() throws Exception {
		PreparedStatement pstmt = null;
		PreparedStatement pstmtSelect = null;
		PreparedStatement pstmtInsert = null;
		
		ResultSet rs = null;
		ResultSet rsSelect = null;
		String insertSql = "Insert into mfg_custproductcustomerprice (companyid, customerid, prodid, priceid, price)" + 
				"Select 'lsm', ?, prodid, priceid, price From mfg_custproductcustomerprice Where companyid = ? and customerid = ?";
		
		String sql = "Select * from customerprofile where companyid = 'lsm' And status = 'A'";

		pstmtInsert = conn.prepareStatement(insertSql);
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while(rs.next()) {			
			pstmtSelect = conn.prepareStatement("Select * From customerprofile where companyid In " + COMPANY_IDS + " And Status = 'A' And Code = ? And Name = ? and Contactperson = ? and Address = ?");
			pstmtSelect.setString(1, rs.getString("code"));
			pstmtSelect.setString(2, rs.getString("name"));
			pstmtSelect.setString(3, rs.getString("contactperson"));
			pstmtSelect.setString(4, rs.getString("address"));
			rsSelect = pstmtSelect.executeQuery ();
			if(rsSelect.next()){
				pstmtInsert.setInt(1, rs.getInt("customerid"));
				pstmtInsert.setString(2, rsSelect.getString("companyid"));
				pstmtInsert.setInt(3, rsSelect.getInt("customerid"));
				pstmtInsert.executeUpdate();
	        }
			
		}
		logger.info("Copied Customer Price...");
	}
}
