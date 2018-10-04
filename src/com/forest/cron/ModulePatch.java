package com.forest.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.ModuleDetailDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.WebsiteDef;
import com.forest.common.services.BaseService;
import com.forest.mfg.beandef.MFG_PaymentVoucherDef;
import com.forest.mfg.beandef.MFG_PaymentVoucherDetailDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDef;
import com.forest.mfg.beandef.MFG_SupplierInvoiceDef;
import com.forest.retail.beandef.RETAIL_CartDef;
import com.forest.retail.beandef.RETAIL_CartDetailDef;
import com.forest.retail.beandef.RETAIL_CategoryDef;
import com.forest.retail.beandef.RETAIL_CategoryDetailDef;
import com.forest.retail.beandef.RETAIL_MemberDef;
import com.forest.retail.beandef.RETAIL_ProductAttributeDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.retail.beandef.RETAIL_ProductImageDef;
import com.forest.retail.beandef.RETAIL_ProductOptionDef;
import com.forest.retail.beandef.RETAIL_TransactionDef;
import com.forest.share.beandef.EmailTemplateDef;
import com.forest.share.beandef.ShipmentDomesticDef;
import com.forest.share.beandef.ShipmentDomesticDetailDef;
import com.forest.share.beandef.ShipmentInternationalDef;
import com.forest.share.beandef.ShipmentInternationalDetailDef;

public class ModulePatch {
	private static final String SCHEMA = "forestdb";
//	private static String COMPANY = "";
	private static Logger logger = Logger.getLogger(ModulePatch.class);	
	
	public StringBuffer SECTOR_TABLE = new StringBuffer();
	
	// Check Different 
//	select a.table_schema, a.table_name, a.column_name, a.data_type, b.table_name, b.column_name
//	from information_schema.COLUMNS a
//	Left Join information_schema.COLUMNS b on b.table_name = a.table_name And b.COLUMN_NAME = a.COLUMN_NAME
//	And b.table_schema = 'dummy' and b.DATA_TYPE = a.DATA_TYPE
//	Where a.table_schema = 'forestdb'
//	And (b.TABLE_NAME is null or b.COLUMN_NAME is null)
	
	public void createMFG(){
//		SECTOR_TABLE.append("'").append(RETAIL_Category.TABLE).append("',");
//		SECTOR_TABLE.append("'").append(ShipmentDomesticDef.TABLE).append("',");
//		SECTOR_TABLE.append("'").append(ShipmentDomesticDetailDef.TABLE).append("'");
//		SECTOR_TABLE.append("'").append(ShipmentInternationalDef.TABLE).append("',");
		SECTOR_TABLE.append("'").append(MFG_PaymentVoucherDef.TABLE).append("',");
		SECTOR_TABLE.append("'").append(MFG_PaymentVoucherDetailDef.TABLE).append("'");
	}
	
	
	public static void main(String[] args) throws Exception{
		ModulePatch mp = new ModulePatch();
//		COMPANY = "megatrend";
//		mp.createDSGN();	
//		mp.createMFG();
		mp.createTable();
//		mp.patchDetailTable();
	}
	
	public void createTable() throws Exception{
		Connection conn = null;
		BaseService baseServ = new BaseService();
		StringBuffer query = new StringBuffer();
		Statement stmt = null;
		PreparedStatement pstmt = null;
		String ids = "";
		ResultSet rs = null;
		try{
			conn = baseServ.getDirectConnection ();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			// Create New Table
			query = new StringBuffer();
			query.append("insert into module (tablename, status, updatedate)");
			query.append(" Select table_name, 'A', now() From information_schema.tables");
			query.append(" Where table_schema='").append(SCHEMA).append("'");
			query.append(" and table_name not in (Select tablename from module)");			
			logger.debug("New Table query = "+query);
			stmt.execute(query.toString());
			
			query = new StringBuffer();
			query.append("insert into moduledetail (moduleid, tablename, columnname, type, systemfield, islisting, isadd)");
			query.append(" Select b.moduleid, a.TABLE_NAME, a.column_name, 2, 'N', 'N', 'Y'");
			query.append(" From information_schema.COLUMNS a");
			query.append(" inner join module b on b.tablename = a.table_name");
			query.append(" Left Join moduledetail c on c.moduleid = b.moduleid and c.columnname = a.column_name");
			query.append(" Where table_schema='").append(SCHEMA).append("' and c.columnname is null");
			logger.debug("New Column query = "+query);
			stmt.execute(query.toString());
			
//			query = new StringBuffer();
//			query.append("insert into moduledetail_client (moduleid, tablename, columnname, size, islisting, isadd)");
//			query.append(" Select a.moduleid, a.tablename, a.columnname, 10, 'N', 'Y'");
//			query.append(" From moduledetail a");
//			query.append(" Inner Join module c On c.moduleid = a.moduleid");
//			query.append(" Left Join moduledetail_client b on b.moduleid = a.moduleid and b.columnname = a.columnname");
//			query.append(" Where (b.columnname is null or a.systemfield = 'S') And b.columnname is null");
//			query.append(" and c.tablename in (").append(SECTOR_TABLE).append(")");
//			logger.debug("New Client Column query = "+query);
//			stmt.execute(query.toString());
			
			// ------------------------------------------------------------
			query = new StringBuffer();
			query.append(" Select b.moduledetailid, b.tablename, b.columnname");
			query.append(" From module a"); 
			query.append(" Inner Join ModuleDetail b on b.tablename = a.tablename");
			query.append(" Left Join information_schema.COLUMNS c on c.TABLE_NAME = a.tablename And c.COLUMN_NAME = b.COLUMNname");
			query.append(" And c.TABLE_SCHEMA = '").append(SCHEMA).append("'");
			query.append(" Where b.systemfield != 'S' And b.shadow!='Y' And c.COLUMN_NAME is null");
			
			pstmt = conn.prepareStatement(query.toString());
			
			rs = pstmt.executeQuery();
			while(rs.next()){
				ids += rs.getString("moduledetailid") + ",";
				logger.debug("[Delete] "+rs.getString("moduledetailid") + ": "+rs.getString("tablename")+"."+rs.getString("columnname"));
			}
			if(ids.length()>1){
				ids = ids.substring(0, ids.length()-1);
				query = new StringBuffer();
				query.append("Delete From moduledetail Where moduledetailid in (").append(ids).append(")");
				logger.debug("Delete column query = "+query);
//				stmt.execute(query.toString());
			}
			
			
			// ------------------------------------------------------------
//			query = new StringBuffer();
//			query.append(" Select b.moduledetailid");
//			query.append(" From module a"); 
//			query.append(" Inner Join ModuleDetail_client b on b.tablename = a.tablename");			
//			query.append(" Left Join information_schema.COLUMNS c on c.TABLE_NAME = a.tablename And c.COLUMN_NAME = b.COLUMNname");
//			query.append(" And c.TABLE_SCHEMA = '").append(SCHEMA).append("'");
//			query.append(" Where c.COLUMN_NAME is null");
//			pstmt = conn.prepareStatement(query.toString());
//			ids = "";
//			rs = pstmt.executeQuery();
//			while(rs.next()){
//				ids += rs.getString("moduledetailid") + ",";
//			}
//			if(ids.length()>1){
//				ids = ids.substring(0, ids.length()-1);
//				query = new StringBuffer();
//				query.append("Delete From moduledetail_client Where moduledetailid in (").append(ids).append(")");
//				logger.debug("Delete Client Column query = "+query);
//				stmt.execute(query.toString());
//			}
//			
//			query = new StringBuffer();
//			query.append("update moduledetail_client set islisting = 'N', issearch='Y', isadd='Y' where columnname = 'companyid'");
//			logger.debug("Update companyid query = "+query);
//			stmt.execute(query.toString());
			
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			logger.error(e, e);
		}
	}
	
	public void patchDetailTable() throws Exception{
		Connection conn = null;
		BaseService baseServ = new BaseService();
		StringBuffer query = new StringBuffer();
		StringBuffer queryUpdate = new StringBuffer();
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;
		try{
			conn = baseServ.getDirectConnection ();
			conn.setAutoCommit(false);
			
			
			query = new StringBuffer();
			query.append("select a.tablename, a.columnname, b.width, b.size, b.visibility, b.islisting, b.issearch, b.isadd, b.position");
			query.append(" From moduledetail a");
			query.append(" inner join moduledetail_client b on b.tablename=a.tablename and b.columnname=a.columnname");
			
			queryUpdate.append("Update moduledetail Set width=?,size=?,visibility=?,islisting=?,issearch=?,isadd=?,position=?");
			queryUpdate.append(" Where tablename=? And columnname=?");
			pstmtUpdate = conn.prepareStatement(queryUpdate.toString());
			logger.debug("patchDetailTable = "+query);
			pstmt = conn.prepareStatement(query.toString());
			rs = pstmt.executeQuery();
			while(rs.next()){
				pstmtUpdate.setString(1, rs.getString("width"));
				pstmtUpdate.setString(2, rs.getString("size"));
				pstmtUpdate.setString(3, rs.getString("visibility"));
				pstmtUpdate.setString(4, rs.getString("islisting"));
				pstmtUpdate.setString(5, rs.getString("issearch"));				
				pstmtUpdate.setString(6, rs.getString("isadd"));
				pstmtUpdate.setString(7, rs.getString("position"));
				pstmtUpdate.setString(8, rs.getString("tablename"));
				pstmtUpdate.setString(9, rs.getString("columnname"));
				pstmtUpdate.executeUpdate();
			}
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			logger.error(e, e);
		}
	}
	
//	public void createClientTable(String fromCompany) throws Exception{
//		Connection conn = null;
//		BaseService baseServ = new BaseService();
//		StringBuffer query = new StringBuffer();
//		Statement stmt = null;
//		try{
//			conn = baseServ.getDirectConnection ();
//			conn.setAutoCommit(false);
//			stmt = conn.createStatement();
//			
//			query = new StringBuffer();
//			query.append("insert into moduledetail_client (moduleid, tablename, columnname, width, visibility, size, islisting, isadd, issearch, managershow, supervisorshow,executiveshow,position)");
//			query.append(" Select a.moduleid, a.tablename, a.columnname,  a.width, a.visibility, a.size, a.islisting, a.isadd, a.issearch, a.managershow, a.supervisorshow,a.executiveshow,a.position ");
//			query.append(" From moduledetail_client a");
//			query.append(" Left Join moduledetail_client b On b.moduleid=a.moduleid And b.columnname=a.columnname");
//			query.append(" Where a.tablename in (").append(SECTOR_TABLE).append(")");
//			query.append(" and b.columnname is null");
//			logger.debug("New Client Column query = "+query);
//			stmt.execute(query.toString());
//						
//			conn.commit();
//		}catch(Exception e){
//			conn.rollback();
//			logger.error(e, e);
//		}
//	}
}
