package com.forest.mfg.adminbuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.FileUtil;
import com.forest.common.util.NumberUtil;
import com.forest.common.util.ReportUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.cron.AccountingPatch;
import com.forest.mfg.beandef.MFG_CreditNoteDef;
import com.forest.mfg.beandef.MFG_DebitNoteDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDetailDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_StatementDetailDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;

// Update statement amount
//Select concat('Update mfg_statement_detail Set amount = ',cast(sellsubtotal as char),' Where transid = ',cast(transid as char),';')
//From (
//select a.transid, c.amount, a.salesdate, sum(b.sellsubtotal) as sellsubtotal from mfg_transaction a
//Inner Join mfg_transaction_detail b On b.transid = a.transid
//Inner Join mfg_statement_detail c on c.transid = a.transid
//Where b.status = 'A'
//group by a.transid, c.amount, a.salesdate
//having c.amount != sum(b.sellsubtotal)
//) a

//Insert Into mfg_statement (companyid,customerid,stmtmonth,stmtyear, stmttotal,updatedate,status)  
//Select  a.companyid, a.customerid, MONTH(a.salesdate), YEAR(a.salesdate), Sum(b.sellsubtotal), now(),'A'  
//From mfg_transaction a 
//Inner Join mfg_transaction_detail b On b.transid=a.transid 
//Left Join mfg_statement c On c.companyid=a.companyid And c.customerid=a.customerid And c.stmtmonth= MONTH(a.salesdate) 
//Where a.status!= 'D' And a.status>2 And YEAR(a.salesdate) = 2010 And c.stmtid is null 
//Group By a.companyid, a.customerid, MONTH(a.salesdate)

//Detail
//Insert Into mfg_statement_detail (stmtid, transid, transno, salesdate, terms, duedate,amount,status,updatedate)
//Select  c.stmtid, a.transid, a.transno, a.salesdate, a.terms,  
//date_add(a.salesdate, INTERVAL terms DAY) As duedate,
//Sum(COALESCE(b.sellsubtotal,0)),'0', now()  
//From mfg_transaction a 
//Inner Join mfg_transaction_detail b On b.transid=a.transid 
//Left Join mfg_statement c On c.companyid=a.companyid And c.customerid=a.customerid And c.stmtmonth= MONTH(a.salesdate) 
//Where a.status!= 'D' And a.status>2 And YEAR(a.salesdate) = 2010
//Group By a.companyid, a.customerid, a.transid

// Delete transaction no that deleted 
//Select concat('Delete From mfg_statement_detail Where transid = ',cast(transid as char),';')
//From (
//select a.transid
//From mfg_transaction a
//Left Join mfg_statement_detail b on b.transid = a.transid
//Where a.status = 'D' and b.transid is not null) a

// Get all wrong customer stmt detail
//select b.stmtdtlid, b.transno
//From mfg_statement a 
//Inner Join mfg_statement_detail b on a.stmtid = b.stmtid
//Left Join mfg_transaction c on b.transid = c.transid and a.customerid = c.customerid
//Where c.transid is null

public class MFG_StatementAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_StatementAdminBuilder.class);
	public MFG_StatementAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		
		if(GeneralConst.EDIT.equals (_reqAction)){
			StringBuffer searchCriteria = new StringBuffer();			
			String detailStatus = _req.getParameter("mfgStmtDtlstatus");
			String customerID = _req.getParameter(MFG_StatementDef.customerid.name);
			String stmtid =  _req.getParameter(MFG_StatementDef.stmtid.name);
			
			addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
			_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, 
					searchArray, _accessByCompany);
			arrayListSearch = _gs.search(_defClass, searchCriteria, searchArray, addArray, _attrReqDataMap, null, "1", _reqAction);
			
			json.put ("md", (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME));
			json.put ("rc", RETURN_EDIT);
			json.put ("isCopy", "false");
			json.put ("R", buildEdit((ArrayList) arrayListSearch.get(1), _defClass, null, false));
			
			
//			if(!CommonUtil.isEmpty(stmtid)){
//				if(detailStatus==null){				
//					detailSearchCriteria.append("a.status=").append(MFG_SelectBuilder.PAY_STATUS_OPEN);
//				}else if(!CommonUtil.isEmpty(detailStatus)){				
//					detailSearchCriteria.append("a.status=").append(detailStatus);
//				}
//				
//				json = requestEdit(null, detailSearchCriteria);	
//			}else{
				StringBuffer query = new StringBuffer();
				query.append("Select a.stmtdtlid,a.transid,a.stmtid,a.updateby,TIMESTAMPDIFF(SECOND, a.updatedate, now()) As updatedate,a.transno,a.custrefno,a.terms,a.salesdate,a.duedate,");
				//query.append(" a.amount,Sum(c.payamount) as payamount,d.creditamount,e.debitamount,a.status");
				query.append(" a.amount, c.payamount,d.creditamount,e.debitamount,a.status");
				query.append(" From mfg_statement_detail a");
				query.append(" Left Join mfg_statement b on b.stmtid = a.stmtid");
				query.append(" Left Join (Select stmtdtlid, sum(payamount) as payamount from mfg_receivepaymentdetail group by stmtdtlid) c on c.stmtdtlid = a.stmtdtlid"); 
				query.append(" Left Join mfg_creditnote d on d.stmtdtlid = a.stmtdtlid And d.status!='D'"); 
				query.append(" Left Join mfg_debitnote e on e.stmtdtlid = a.stmtdtlid And e.status!='D'");
				query.append(" Where  ");
				if(!CommonUtil.isEmpty(stmtid)){
					query.append(" b.stmtid = ").append(stmtid);
				}else{
					query.append(" b.customerid = ").append(customerID);
				}
				query.append(" And  b.companyid = '").append(_shopInfoBean.getShopName()).append("'"); 
				if(detailStatus==null){				
					query.append(" And a.status=").append(MFG_SelectBuilder.PAY_STATUS_OPEN);
				}else if(!CommonUtil.isEmpty(detailStatus)){				
					query.append(" And a.status=").append(detailStatus);
				}
//				query.append(" Group by a.stmtdtlid");
				query.append(" Order By a.transno");
				
				ArrayList dataArray = _gs.searchDataArray(query);
				json.put("subclasslength", 1);
				json.put ("SUBR0", buildEditDetail(dataArray, 
						(String) BeanDefUtil.getField(MFG_StatementDetailDef.class, BeanDefUtil.KEY), 
						(String) BeanDefUtil.getField(MFG_StatementDetailDef.class, BeanDefUtil.TABLE_PREFIX), null, false));
//			}		
		}else{
			json = super.requestJsonHandler();
		}
		return json;
	}
	
	public StringBuffer displayHandler()throws Exception{		
		StringBuffer buffer = super.displayHandler();
		
		// Read Additional Content From File
		buffer.append(FileHandler.readAdminFile (_shopInfoBean, _clientBean));	
		return buffer;
	}

	public void transCreateStatement(HashMap transMap){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		HashMap[] dataDetailMap = new HashMap[1];
		int stmtid = 0;
		int stmtdetailid = 0;
		String transid = null;
		String transno = null;		
		String customerid = null;
		String customerid_ori = null;		
		String totalAmount = null;		
		String terms = null;
		String custRefNo = null;
		String salesDate = null;
		String gstAmount = null;
		String grandTotal = null;
		try{
			logger.debug("transMap = "+transMap);
			transid = (String) transMap.get(MFG_TransactionDef.transid.name);
			transno = (String) transMap.get(MFG_TransactionDef.transno.name);			
			customerid = (String) transMap.get(MFG_TransactionDef.customerid.name);
			customerid_ori = (String) transMap.get(MFG_TransactionDef.customerid.name+"_ori");
			terms = (String) transMap.get(MFG_TransactionDef.terms.name);
			terms = (CommonUtil.isEmpty(terms))?"30":terms;
			custRefNo = (String) transMap.get(MFG_TransactionDef.custrefno.name);
			salesDate = (String) transMap.get(MFG_TransactionDef.salesdate.name);
			totalAmount = (String) transMap.get("GRANDTOTAL");
			
			dataDetailMap[0] = new HashMap();
			logger.debug("createStatement transid = "+transid);
			logger.debug(" Salese Date: "+salesDate);
			logger.debug("totalAmount = "+totalAmount);
			
			// Check If Statement of the month created And statement detail created
			// Search By transid
			query = new StringBuffer();
			query.append("Select");
			query.append(" a.").append(MFG_StatementDef.stmtid);
			query.append(", b.").append(MFG_StatementDetailDef.stmtdtlid);
			query.append(" From ").append(MFG_StatementDef.TABLE).append(" a");
			query.append(" Left Join ").append(MFG_StatementDetailDef.TABLE).append(" b");
			query.append(" On b.").append(MFG_StatementDetailDef.stmtid).append("=a.").append(MFG_StatementDef.stmtid);
			query.append(" And b.").append(MFG_StatementDetailDef.transid).append("=?");
			query.append(" Where a.").append(MFG_StatementDef.customerid).append("=?");
			
			logger.debug(" -- aass query = "+query);
			pstmt = _dbConnection.prepareStatement(query.toString());
			pstmt.setString(1, transid);
			pstmt.setString(2, customerid);
			rs = pstmt.executeQuery();
			if(rs.next()){
				stmtid = rs.getInt(MFG_StatementDef.stmtid.name);
				stmtdetailid = rs.getInt(MFG_StatementDetailDef.stmtdtlid.name);				
			}
			
			// Create Statement If No existing statement
			if(stmtid==0){
				query = new StringBuffer();
				query.append("Insert Into ").append(MFG_StatementDef.TABLE).append(" (");
				query.append(MFG_StatementDef.companyid).append(",");
				query.append(MFG_StatementDef.customerid).append(",");
				query.append(MFG_StatementDef.updatedate).append(","); 
				query.append(MFG_StatementDef.status).append(") ");
				query.append(" Values(?, ?, now(), '").append(GeneralConst.ACTIVE).append("')");
				pstmt = _dbConnection.prepareStatement(query.toString(),PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, _shopInfoBean.getShopName());
				pstmt.setString(2, customerid);				
				pstmt.execute();
				rs = pstmt.getGeneratedKeys ();
				if (rs.next()) {
					stmtid = rs.getInt(1);
			    }
			}
			
			// Get Total Amount From Transaction Detail			
//			query = new StringBuffer();
//			query.append("select Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(") - ").append(" a.").append(MFG_TransactionDef.discount).append(" + a.").append(MFG_TransactionDef.gst_amt).append(" As sellsubtotal,");
//			query.append(" a.").append(MFG_TransactionDef.salesdate).append(" ");			
//			query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
//			query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
//			query.append(" On b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
//			query.append(" Where a.").append(MFG_TransactionDetailDef.transid).append("=?");
//			query.append(" And a.").append(MFG_TransactionDef.status).append(">").append(MFG_SelectBuilder.TRANS_ORDER_CONFIRM);
//			query.append(" And b.").append(MFG_TransactionDetailDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
//			query.append(" Group By a.").append(MFG_TransactionDef.salesdate);	
//			logger.debug("Get Transaction Amount = "+query);
//			pstmt = _dbConnection.prepareStatement(query.toString());
//			pstmt.setString(1, transid);							
//			rs = pstmt.executeQuery();
//			if(rs.next()){
//				totalAmount = rs.getString(MFG_TransactionDetailDef.sellsubtotal.name);
//				salesDate = rs.getString(MFG_TransactionDef.salesdate.name);
//			}
			
			
			logger.debug("stmtdetailid = "+stmtdetailid);
			
			// Create Or Update Statememt Detail
			if(stmtdetailid==0){ // Create New
				if(CommonUtil.isEmpty(totalAmount)){
					totalAmount = "0";
				}
				query = new StringBuffer();
				query.append("Insert Into mfg_statement_detail (stmtid,transid,transno, custrefno, salesdate,terms,duedate,amount, updatedate, status)");
				query.append(" values(?,?,?,?, ?, ?, date_add(");
				if(CommonUtil.isEmpty(salesDate)){
					query.append("now()");
				}else{
					query.append("'").append(salesDate).append("'");
				}
				query.append(", INTERVAL ").append(terms).append(" DAY)").append(",?,now(),?)");
				logger.debug("Insert query = "+query);
				pstmt = _dbConnection.prepareStatement(query.toString());
				pstmt.setInt(1, stmtid);				
				pstmt.setString(2, transid);
				pstmt.setString(3, transno);
				pstmt.setString(4, custRefNo);
				if(CommonUtil.isEmpty(salesDate)){
					pstmt.setString(5, DateUtil.getCurrentDate());
				}else{
					pstmt.setString(5, salesDate);
				}
				pstmt.setString(6, terms);		
				pstmt.setString(7, totalAmount);	
				pstmt.setString(8, MFG_SelectBuilder.PAY_STATUS_OPEN);
				pstmt.execute();
			}else{
				query = new StringBuffer();
				query.append("Update ").append(MFG_StatementDetailDef.TABLE);
				query.append(" Set ");
				query.append(MFG_StatementDetailDef.amount).append("=?, ");
				query.append(MFG_StatementDetailDef.custrefno).append("=?, ");
				query.append(MFG_StatementDetailDef.salesdate).append("=?, ");
				query.append(MFG_StatementDetailDef.duedate).append("=date_add('").append(salesDate).append("', INTERVAL ").append(terms).append(" DAY)");
				query.append(" Where ").append(MFG_StatementDetailDef.stmtdtlid).append("=?");
				logger.debug("Update query = "+query);
				pstmt = _dbConnection.prepareStatement(query.toString());
				pstmt.setString(1, totalAmount);
				pstmt.setString(2, custRefNo);				
				pstmt.setString(3, salesDate);
				pstmt.setInt(4, stmtdetailid);
				pstmt.executeUpdate();
				
			}
			
			updateStatementDetail(transid);
			
			// Update Account Detail
			AccountingPatch acc = new AccountingPatch();
			
			if(!CommonUtil.isEmpty(customerid_ori) && !customerid.equals(customerid_ori)){
				deleteWrongCustomerStatement(transid, customerid_ori);
				acc.accountAgingPatchSingle(_dbConnection, _shopInfoBean.getShopName(), customerid_ori);	
			}
			
			acc.accountAgingPatchSingle(_dbConnection, _shopInfoBean.getShopName(), customerid);
			
			
			
//			StringBuffer stmtDtlIdBuffer = new StringBuffer();
//			StringBuffer stmtBuffer = new StringBuffer();
//			query = new StringBuffer();
//			query.append("select b.stmtdtlid, a.stmtid");
//			query.append(" From mfg_statement a");
//			query.append(" Inner Join mfg_statement_detail b On b.stmtid = a.stmtid");
//			query.append(" Left Join mfg_transaction c On c.transid = b.transid And c.customerid = a.customerid");
//			query.append(" Where c.status = '").append(GeneralConst.DELETED).append("'");
//			pstmt = _dbConnection.prepareStatement(query.toString());
//			rs = pstmt.executeQuery();
//			while(rs.next()){
//				stmtDtlIdBuffer.append(rs.getString(MFG_StatementDetailDef.stmtdtlid.name)).append(",");
//				stmtBuffer.append(rs.getString(MFG_StatementDef.customerid.name)).append(",");
//			}
//			if(stmtDtlIdBuffer.length()>0){
//				stmtDtlIdBuffer.deleteCharAt(stmtDtlIdBuffer.length()-1);
//				stmtBuffer.deleteCharAt(stmtBuffer.length()-1);
//				
//				query = new StringBuffer();
//				query.append("Delete From ").append(MFG_StatementDetailDef.TABLE);
//				query.append(" Where ").append(MFG_StatementDetailDef.stmtdtlid).append(" In (").append(stmtDtlIdBuffer).append(")");
//				pstmt = _dbConnection.prepareStatement(query.toString());
//				pstmt.execute();
//			}
						
		}catch (Exception e) {
			logger.error(e, e);
		}finally{
			DBUtil.free(null, pstmt, rs);
		}
	}
	
	public void transDeleteStatement(String[] transId){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		
		try{			
			if(transId!=null && transId.length>0){
				query = new StringBuffer();
				query.append("Delete From ").append(MFG_StatementDetailDef.TABLE);
				query.append(" Where ").append(MFG_StatementDetailDef.transid).append(" In ");
				query.append("(").append(DBUtil.arrayToString(transId, MFG_StatementDetailDef.transid));
				query.append(")");
				pstmt = _dbConnection.prepareStatement(query.toString());				
				pstmt.execute();
			}
						
		}catch (Exception e) {
			logger.error(e, e);
		}finally{
			DBUtil.free(null, pstmt, rs);
		}
	}
	
	public void deleteWrongCustomerStatement(String transId, String customerid){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		String stmtDtlId = null;
		try{						
			query = new StringBuffer();
			query.append("select b.").append(MFG_StatementDetailDef.stmtdtlid);
			query.append(" From ").append(MFG_StatementDef.TABLE).append(" a");
			query.append(" Inner Join ").append(MFG_StatementDetailDef.TABLE).append(" b on a.").append(MFG_StatementDef.stmtid).append(" = b.").append(MFG_StatementDetailDef.stmtid);			
			query.append(" Where a.").append(MFG_StatementDef.customerid).append("=? and b.").append(MFG_StatementDetailDef.transid).append("=?");
			logger.debug("deleteWrongCustomerStatement = "+query);
			pstmt = _dbConnection.prepareStatement(query.toString());	
			pstmt.setString(1, customerid);
			pstmt.setString(2, transId);
			rs = pstmt.executeQuery();			
			if(rs.next()){
				stmtDtlId = rs.getString(MFG_StatementDetailDef.stmtdtlid.name);				
			}
			if(stmtDtlId!=null){
				query = new StringBuffer();
				query.append("Delete From ").append(MFG_StatementDetailDef.TABLE).append(" Where ").append(MFG_StatementDetailDef.stmtdtlid).append(" = ?");
				logger.debug("deleteWrongCustomerStatement = "+query);
				pstmt = _dbConnection.prepareStatement(query.toString());			
				pstmt.setString(1, stmtDtlId);
				pstmt.execute();				
			}
			
		}catch (Exception e) {
			logger.error(e, e);
		}finally{
			DBUtil.free(null, pstmt, rs);
		}
	}
	
	public void updateCreditAmountToStatement(String transId){
		PreparedStatement pstmt = null;		
		StringBuffer query = new StringBuffer();
		
		query.append("Update ").append(MFG_StatementDetailDef.TABLE);
		query.append(" Set ").append(MFG_StatementDetailDef.creditamount).append(" = (");
		query.append(" Select Sum(").append(MFG_CreditNoteDef.creditamount).append(") ");
		query.append(" From ").append(MFG_CreditNoteDef.TABLE);
		query.append(" Where ").append(MFG_CreditNoteDef.transid).append("='").append(transId).append("'");
		query.append(" And ").append(MFG_CreditNoteDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		query.append(")");
		query.append(" Where ").append(MFG_CreditNoteDef.transid).append("='").append(transId).append("'");
		logger.debug("updateCreditAmountToStatement query = "+query);
		try{
			pstmt = _dbConnection.prepareStatement(query.toString());
			pstmt.executeUpdate();					
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			pstmt = null;
		}
	}
	
	public void updateDebitAmountToStatement(String transId){
		PreparedStatement pstmt = null;		
		StringBuffer query = new StringBuffer();
		
		query.append("Update ").append(MFG_StatementDetailDef.TABLE);
		query.append(" Set ").append(MFG_StatementDetailDef.debitamount).append(" = (");
		query.append(" Select Sum(").append(MFG_DebitNoteDef.debitamount).append(") ");
		query.append(" From ").append(MFG_DebitNoteDef.TABLE);
		query.append(" Where ").append(MFG_DebitNoteDef.transid).append("='").append(transId).append("'");
		query.append(" And ").append(MFG_DebitNoteDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		query.append(")");
		query.append(" Where ").append(MFG_DebitNoteDef.transid).append("='").append(transId).append("'");
		
		try{
			pstmt = _dbConnection.prepareStatement(query.toString());
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			pstmt = null;
		}
	}
	public void updatePaymentAmountToStatement(String transID)throws Exception{
		String[] array = {transID};
		updatePaymentAmountToStatement(array);
	}
	
	public void updatePaymentAmountToStatement(String[] transIdArray)throws Exception{
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;
		StringBuffer query = new StringBuffer();
		StringBuffer queryUpdate = new StringBuffer();
		HashMap payMap = new HashMap();
		
		try{
			if(transIdArray!=null){
				query.append(" Select Sum(a.").append(MFG_ReceivePaymentDetailDef.payamount).append(") As ").append(MFG_ReceivePaymentDetailDef.payamount).append(",");
				query.append(MFG_StatementDetailDef.transid);
				query.append(" From ").append(MFG_ReceivePaymentDetailDef.TABLE).append(" a");		
				query.append(" Inner Join ").append(MFG_StatementDetailDef.TABLE).append(" b On b.");
				query.append(MFG_StatementDetailDef.stmtdtlid).append("=a.").append(MFG_ReceivePaymentDetailDef.stmtdtlid);		
				query.append(" Where b.").append(MFG_StatementDetailDef.transid);
				query.append(" In (").append(DBUtil.arrayToString(transIdArray, MFG_StatementDetailDef.transid)).append(")");
				query.append(" Group By ").append(MFG_StatementDetailDef.transid);
				logger.debug("Select query = "+query);
				pstmt = _dbConnection.prepareStatement(query.toString());
				rs = pstmt.executeQuery();
				
				queryUpdate.append("Update ").append(MFG_StatementDetailDef.TABLE);
				queryUpdate.append(" Set ").append(MFG_StatementDetailDef.payamount).append(" = ? ");
				queryUpdate.append(" Where ").append(MFG_StatementDetailDef.transid).append("=?");
				logger.debug("Update queryUpdate = "+queryUpdate);
				pstmtUpdate = _dbConnection.prepareStatement(queryUpdate.toString());
				
				while(rs.next()){
					payMap.put(rs.getString(MFG_StatementDetailDef.transid.name), rs.getString(MFG_ReceivePaymentDetailDef.payamount.name));					
				}
				
				for(int i=0; i<transIdArray.length; i++){
					pstmtUpdate.setDouble(1, CommonUtil.parseDouble((String)payMap.get(transIdArray[i])));
					pstmtUpdate.setString(2, transIdArray[i]);
					pstmtUpdate.executeUpdate();
				}
			}
		}catch(Exception e){
			logger.error(e, e);
			throw e;
		}finally{
			DBUtil.free(null, pstmt, rs);
			DBUtil.free(null, pstmtUpdate, null);
		}
//		query.append("Update ").append(MFG_StatementDetailDef.TABLE);
//		query.append(" Set ").append(MFG_StatementDetailDef.payamount).append(" = (");
//		query.append(" Select Sum(").append(MFG_ReceivePaymentDetailDef.payamount).append(") ");
//		query.append(" From ").append(MFG_ReceivePaymentDetailDef.TABLE).append(" a");
//		
//		query.append(" Inner Join ").append(MFG_StatementDetailDef.TABLE).append(" b On b.");
//		query.append(MFG_StatementDetailDef.stmtdtlid).append("=a.").append(MFG_ReceivePaymentDetailDef.stmtdtlid);
//		
//		query.append(" Where b.").append(MFG_StatementDetailDef.transno).append("='").append(transNo).append("'");		
//		query.append(")");
//		query.append(" Where ").append(MFG_StatementDetailDef.transno).append("='").append(transNo).append("'");
//		logger.debug("updatePaymentAmountToStatement query = "+query);
//		try{
//			pstmt = _dbConnection.prepareStatement(query.toString());
//			pstmt.executeUpdate();
//		}catch(Exception e){
//			logger.error(e, e);
//		}finally{
//			pstmt = null;
//		}
	}
	
	
	public void updateStatementDetail(String transId)throws Exception{
		String[] transIdArray = {transId};
		updateStatementDetail(transIdArray);
	}
	public void updateStatementDetail(String[] transId)throws Exception{
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;		
		try{						
			query.append("Update ").append(MFG_StatementDetailDef.TABLE).append(" Set ").append(MFG_StatementDetailDef.status).append("=");
			query.append(" (if((").append(MFG_StatementDetailDef.amount);
			query.append("+COALESCE(").append(MFG_StatementDetailDef.debitamount);
			query.append(",0)-COALESCE(").append(MFG_StatementDetailDef.creditamount);
			query.append(",0))>COALESCE(").append(MFG_StatementDetailDef.payamount).append(",0),'0','2'))");
			query.append(" Where ").append(MFG_StatementDetailDef.transid).append(" in (");
			query.append(DBUtil.arrayToString(transId, MFG_StatementDetailDef.transid));
			query.append(")");
			logger.debug("updateStatementDetail query = "+query);
			pstmtUpdate = _dbConnection.prepareStatement(query.toString()); 
			pstmtUpdate.executeUpdate();
		}catch (Exception e) {
			throw e;
		}finally{
			DBUtil.free(null, pstmt, rs);
			DBUtil.free(null, pstmtUpdate, null);
		}
	}
	
	public JasperPrint buildReport()throws Exception{
		JasperPrint jprintMain = null;
		JasperPrint jprint = null;
		ArrayList dataArray = null;
		HashMap dataRow = null;
		List pages = null;
		String customerid = _req.getParameter(MFG_StatementDef.customerid.name);	
		String stmtid = _req.getParameter(MFG_StatementDef.stmtid.name);
		StringBuffer mainQuery = new StringBuffer();
		if(CommonUtil.isEmpty(customerid) && CommonUtil.isEmpty(stmtid)){
			mainQuery.append("Select ");
			mainQuery.append(MFG_StatementDef.customerid);
			mainQuery.append(" From ");
			mainQuery.append(MFG_StatementDef.TABLE);
			mainQuery.append(" Where ");
			mainQuery.append(MFG_StatementDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("' And ");
			mainQuery.append(MFG_StatementDef.totalamt).append(">0 And ");
			mainQuery.append(MFG_StatementDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
			dataArray = _gs.searchDataArray(mainQuery);
			for(int i=0; i<dataArray.size(); i++){
				dataRow = (HashMap) dataArray.get(i);
				customerid = (String) dataRow.get(MFG_StatementDef.customerid.name);								
				jprint = buildReport(customerid);				
				pages = jprint.getPages();
				if(jprintMain==null){
					jprintMain = jprint;
				}else{
					for(int p=0;p<pages.size(); p++){
						jprintMain.addPage((JRPrintPage)pages.get(p));
					}
				}
			}
		}else{
			jprintMain = buildReport(customerid);
		}
		return jprintMain;
	}
	
	public JasperPrint buildReport(String customerid)throws Exception{
		JasperPrint jprint = null;
		Map parameters = new HashMap();
		ArrayList arrayListSearch = null;
		ArrayList detailArrayListSearch = null;
		subClass = BeanDefUtil.getSubClass(_defClass);

		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		
		Map termMap = MFG_SelectBuilder.getHASH_TERM(_dbConnection, _shopInfoBean);
		
		int count=0;
		int pastDue=0;
		double current = 0;
		double past1 = 0;
		double past30 = 0;
		double past60 = 0;
		double past90 = 0;
		double balAmount = 0;
		HashMap rowData = null;
		
		String stmtid = _req.getParameter(MFG_StatementDef.stmtid.name);
		
		
		// Get Customer Information
		query.append("Select a.").append(MFG_StatementDef.stmtid);
		query.append(",b.").append(CustomerProfileDef.name).append(" As customerid_value");
		query.append(",b.").append(CustomerProfileDef.code);
		query.append(",b.").append(CustomerProfileDef.contactperson);
		query.append(",b.").append(CustomerProfileDef.terms);
		
		query.append(", a.").append(MFG_StatementDef.customerid).append(",");
		query.append(" COALESCE(b.").append(CustomerProfileDef.address).append(",'') as ").append(CustomerProfileDef.address).append(",");
		query.append(" COALESCE(b.").append(CustomerProfileDef.city).append(",'') as ").append(CustomerProfileDef.city).append(",");
		query.append(" COALESCE(b.").append(CustomerProfileDef.state).append(",'') as ").append(CustomerProfileDef.state).append(",");		
		query.append(" COALESCE(b.").append(CustomerProfileDef.postcode).append(",'') as ").append(CustomerProfileDef.postcode).append(",");
		query.append(" c.").append(AgentProfileDef.name).append(" As ").append(CustomerProfileDef.salesby).append(",");
		query.append(" b.").append(CustomerProfileDef.contactperson).append(",");
		query.append(" b.").append(CustomerProfileDef.country).append(",");
		query.append(" b.").append(CustomerProfileDef.phoneno).append(",");
		query.append(" b.").append(CustomerProfileDef.mobileno).append(",");
		query.append(" b.").append(CustomerProfileDef.faxno).append("");
		query.append(" From ").append(MFG_StatementDef.TABLE).append(" a");
		query.append(" Inner Join ").append(CustomerProfileDef.TABLE).append(" b");
		query.append(" on b.").append(CustomerProfileDef.customerid).append(" = a.").append(MFG_StatementDef.customerid);
		
		query.append(" Left Join ").append(AgentProfileDef.TABLE).append(" c");
		query.append(" on c.").append(AgentProfileDef.id).append(" = b.").append(CustomerProfileDef.salesby);
		
		if(!CommonUtil.isEmpty(stmtid)){
			query.append(" Where a.").append(MFG_StatementDef.stmtid).append(" = ").append(stmtid);
		}else if(!CommonUtil.isEmpty(customerid)){
			query.append(" Where a.").append(CustomerProfileDef.customerid).append(" = ").append(customerid);
		}
		
		logger.debug("select customer query = "+query);
		arrayListSearch = gs.searchDataArray(query);
						
		Map countryMap = SelectBuilder.getHASH_COUNTRY(_dbConnection, _shopInfoBean);
		
		if(arrayListSearch.size()>0){
			parameters = (HashMap) arrayListSearch.get(0);
			parameters.put("stmtdate", DateUtil.getCurrentDate());
			parameters.put(MFG_TransactionDef.terms.name, termMap.get(parameters.get(MFG_TransactionDef.terms.name)));
			parameters.put(CustomerProfileDef.country.name, CommonUtil.nullToEmpty((String) countryMap.get(parameters.get(CustomerProfileDef.country.name))));
			logger.debug("parameters = "+parameters);			
		}
		
		// Previous Balance
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		HashMap prevBalance = null;
		
		if(subClass!=null){
			if("homade".equals(_shopInfoBean.getShopName())){
				if(!CommonUtil.isEmpty(fromDate)){
					prevBalance = getPreviousBalance((String)parameters.get(MFG_StatementDef.stmtid.name), fromDate);
					if(CommonUtil.isEmpty((String) prevBalance.get("balamount"))){
						prevBalance = null;
					}else{
						prevBalance.put(MFG_TransactionDef.salesdate.name, fromDate);
						prevBalance.put(MFG_TransactionDef.transno.name, "");
						prevBalance.put("stmtdesc", "BF");
					}
				}
				
				query = buildYearToDateStatementQuery(parameters);
			}else{
				query = buildUnpaidStatementQuery(parameters);
			}
			
			logger.debug("buildReport query = "+query);
			detailArrayListSearch = gs.searchDataArray(query);
			
			// Calculate Past Due
			count = detailArrayListSearch.size();
			for(int i=0; i<count; i++){
				rowData = (HashMap) detailArrayListSearch.get(i);								
				balAmount = NumberUtil.parseDouble((String) rowData.get("balamount"));				
				if(rowData.get("pastdue")==null){
					// Previous Past Due
					if(pastDue>0){
						if(pastDue>0 && pastDue<31) past1 += balAmount;
						if(pastDue>30 && pastDue<61) past30 += balAmount;
						if(pastDue>60 && pastDue<91) past60 += balAmount;
						if(pastDue>90) past90 += balAmount;
					}else{
						current+=balAmount;
					}					
				}else{
					pastDue = Integer.parseInt((String) rowData.get("pastdue"));
					if(pastDue>0){
						if(pastDue>0 && pastDue<31) past1 += balAmount;
						if(pastDue>30 && pastDue<61) past30 += balAmount;
						if(pastDue>60 && pastDue<91) past60 += balAmount;
						if(pastDue>90) past90 += balAmount;
					}else{
						current+=balAmount;
					}
				}		
			}
			if(prevBalance!=null){
				detailArrayListSearch.add(0,prevBalance);
			}
			
			MFG_ReportAdminBuilder rptBuilder = new MFG_ReportAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, null, null, null);
			HashMap reportMap = _gs.searchDataMap(rptBuilder.rptStmtQuery((String)parameters.get(MFG_StatementDef.customerid.name), GeneralConst.ACTIVE), null);
			
			parameters.put("CURRENT", new BigDecimal((String) reportMap.get("currentamt")).setScale(2, BigDecimal.ROUND_UP).toString());
			parameters.put("PAST1", new BigDecimal((String) reportMap.get("minus1month")).setScale(2, BigDecimal.ROUND_UP).toString());
			parameters.put("PAST30", new BigDecimal((String) reportMap.get("minus2month")).setScale(2, BigDecimal.ROUND_UP).toString());
			parameters.put("PAST60", new BigDecimal((String) reportMap.get("minus3month")).setScale(2, BigDecimal.ROUND_UP).toString());
			parameters.put("PAST90", new BigDecimal((String) reportMap.get("monththerest")).setScale(2, BigDecimal.ROUND_UP).toString());
			
			int currentMonth = Integer.parseInt(DateUtil.getCurrentMonth());		
			
			parameters.put("PASS1MONTH",DateUtil.getMonthInWord(currentMonth-1));
			parameters.put("PASS2MONTH",DateUtil.getMonthInWord(currentMonth-2));
			parameters.put("PASS3MONTH",DateUtil.getMonthInWord(currentMonth-3));						
		}
		
		ReportUtil rptUtil = new ReportUtil();	
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String subReportDir = ResourceUtil.getSubReportDirectory(_shopInfoBean);
		if(toDate!=null && DateUtil.getDate(toDate).after(DateUtil.getDate("2013-12-31"))){			
			if(FileUtil.isExist(subReportDir+"2014")){
				subReportDir += "2014/";
			}
		}
		String logoFile = subReportDir + "logo.jpg";
		String reportFilename = reportDir + "Statement.jasper"; 
		parameters.put("title", "Statement");
		parameters.put("LOGO", logoFile);
		parameters.put("REPORT_DIR", reportDir);
		parameters.put("SUBREPORT_DIR", subReportDir);		
		jprint = rptUtil.getReportPrint(reportFilename, detailArrayListSearch, parameters);
		
		_resp.setHeader("content-disposition", "inline; filename=\""+(String)parameters.get("customerid_value")+"_"+DateUtil.getCurrentDate()+".pdf\"");
		
		return jprint;
	}
	
	private StringBuffer buildUnpaidStatementQuery(Map parameters){
		StringBuffer query = new StringBuffer();
		String stmtMonth = _req.getParameter("stmtmonth");
		String stmtYear = _req.getParameter("stmtyear");
		
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		
		// Get Statement Detail
		query = new StringBuffer();
		query.append("Select 'INV' As stmtdesc, a.stmtdtlid,a.transno, null as cnno, a.salesdate, a.duedate, a.amount as orgamount,");
		query.append(" DATEDIFF(now(), a.duedate) as pastdue, ");
		query.append(" null as description,");
		query.append(" (a.amount-COALESCE(c.payamount,0)) As balamount");
		query.append(" From mfg_statement_detail a");
		query.append(" Inner Join mfg_statement b on b.stmtid = a.stmtid");			
		query.append (" Left Join (Select stmtdtlid, sum(payamount) as payamount from mfg_receivepaymentdetail group by stmtdtlid) c on c.").append(MFG_ReceivePaymentDetailDef.stmtdtlid.name);
		query.append (" = a.").append(MFG_StatementDetailDef.stmtdtlid);
		query.append (" Left Join ").append(MFG_CreditNoteDef.TABLE).append(" d on d.").append(MFG_CreditNoteDef.stmtdtlid.name);
		query.append (" = a.").append(MFG_StatementDetailDef.stmtdtlid);
		query.append(" Where  b.companyid = '").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And (a.stmtid = ").append(parameters.get(MFG_StatementDef.stmtid.name));
		query.append(" Or b.customerid=").append(parameters.get(MFG_StatementDef.customerid.name)).append(")");
		query.append(" And a.status!='2'");
		query.append(" And a.status!='D'");
		query.append(" And (a.amount-COALESCE(c.payamount,0)-COALESCE(d.creditamount,0))>0");
		if(!CommonUtil.isEmpty(stmtMonth) && !CommonUtil.isEmpty(stmtYear)){
			query.append(" And MONTH(salesdate) = ").append(stmtMonth);
			query.append(" And YEAR(salesdate) = ").append(stmtYear);
		}
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And salesdate Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
		}else{ 
			if(!CommonUtil.isEmpty(fromDate)){
				query.append(" And salesdate >= ");
				query.append("'").append(fromDate).append("'");
			}else if(!CommonUtil.isEmpty(toDate)){
				query.append(" And salesdate <= ");
				query.append("'").append(toDate).append("'");
			}
		}
//		query.append(" Group By a.transno");
		
		query.append(" Union ");
		
		// Credit Note
		query.append(" Select");
		query.append(" 'CN' As stmtdesc, a.").append(MFG_CreditNoteDef.stmtdtlid).append(",");
		query.append(" a.").append(MFG_CreditNoteDef.transno).append(",");
		query.append(" a.").append(MFG_CreditNoteDef.cnno).append(",");
		query.append(" a.").append(MFG_CreditNoteDef.creditdate).append(" as salesdate,");
		query.append(" null as duedate,");
		query.append(" null as orgamount,");
		query.append(" null as pastdue,");
		query.append(" a.").append(MFG_CreditNoteDef.description).append(",");
		query.append(" (a.").append(MFG_CreditNoteDef.creditamount).append(" * -1) as balamount");
		query.append(" From ").append(MFG_CreditNoteDef.TABLE).append(" a");
		query.append(" Inner Join mfg_statement_detail b On b.stmtdtlid = a.stmtdtlid");
		query.append(" Left Join (Select stmtdtlid, sum(payamount) as payamount from mfg_receivepaymentdetail group by stmtdtlid) c on c.").append(MFG_ReceivePaymentDetailDef.stmtdtlid.name);
		query.append(" = a.").append(MFG_StatementDetailDef.stmtdtlid);
		query.append(" Where  a.companyid = '").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And (b.stmtid = ").append(parameters.get(MFG_StatementDef.stmtid.name));
		query.append(" Or a.customerid=").append(parameters.get(MFG_StatementDef.customerid.name)).append(")");
		query.append(" And b.status!='2'");
		query.append(" And a.status!='D'");
		query.append(" And (b.amount-COALESCE(c.payamount,0)-COALESCE(a.creditamount,0))>0");
		
		if(!CommonUtil.isEmpty(stmtMonth) && !CommonUtil.isEmpty(stmtYear)){
			query.append(" And MONTH(salesdate) = ").append(stmtMonth);
			query.append(" And YEAR(salesdate) = ").append(stmtYear);
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.creditdate Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
		}else{
			if(!CommonUtil.isEmpty(fromDate)){			
				query.append(" And a.creditdate >= ");
				query.append("'").append(fromDate).append("'");
			}else if(!CommonUtil.isEmpty(toDate)){			
				query.append(" And a.creditdate <= ");
				query.append("'").append(toDate).append("'");
			} 
		}
		
		query.append(" Union ");
		
		// Debit Note
		query.append(" Select");
		query.append(" 'DN' As stmtdesc, a.").append(MFG_DebitNoteDef.stmtdtlid).append(",");
		query.append(" a.").append(MFG_DebitNoteDef.transno).append(",");
		query.append(" a.").append(MFG_DebitNoteDef.dnno).append(" As cnno,");
		query.append(" a.").append(MFG_DebitNoteDef.debitdate).append(" as salesdate,");
		query.append(" null as duedate,");
		query.append(" null as orgamount,");
		query.append(" null as pastdue,");
		query.append(" a.").append(MFG_DebitNoteDef.description).append(",");
		query.append(" (a.").append(MFG_DebitNoteDef.debitamount).append(" * 1) as balamount");
		query.append(" From ").append(MFG_DebitNoteDef.TABLE).append(" a");
		query.append(" Inner Join mfg_statement_detail b On b.stmtdtlid = a.stmtdtlid");
		query.append(" Left Join (Select stmtdtlid, sum(payamount) as payamount from mfg_receivepaymentdetail group by stmtdtlid) c on c.").append(MFG_ReceivePaymentDetailDef.stmtdtlid.name);
		query.append(" = a.").append(MFG_StatementDetailDef.stmtdtlid);
		query.append(" Where  a.companyid = '").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And (b.stmtid = ").append(parameters.get(MFG_StatementDef.stmtid.name));
		query.append(" Or a.customerid=").append(parameters.get(MFG_StatementDef.customerid.name)).append(")");
		query.append(" And b.status!='2'");
		query.append(" And a.status!='D'");

		
		if(!CommonUtil.isEmpty(stmtMonth) && !CommonUtil.isEmpty(stmtYear)){
			query.append(" And MONTH(salesdate) = ").append(stmtMonth);
			query.append(" And YEAR(salesdate) = ").append(stmtYear);
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And salesdate Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
		}else{
			if(!CommonUtil.isEmpty(fromDate)){			
				query.append(" And salesdate >= ");
				query.append("'").append(fromDate).append("'");
			}else if(!CommonUtil.isEmpty(toDate)){			
				query.append(" And salesdate <= ");
				query.append("'").append(toDate).append("'");
			} 
		}
		
		query.append(" Order By salesdate,transno");
		
		return query;
	}
	
	private HashMap getPreviousBalance(String stmtid, String fromDate){
		StringBuffer query = new StringBuffer();
		HashMap dataMap = null;
		// Get Statement Detail
		if(!CommonUtil.isEmpty(fromDate)){
			query = new StringBuffer();
			query.append("Select");
			query.append(" (Sum(a.amount) - ");
			query.append(" COALESCE(Sum(b.payamount),0) -");
			query.append(" COALESCE(Sum(c.creditamount),0) +");
			query.append(" COALESCE(Sum(d.debitamount),0)) As balamount");
			query.append(" from mfg_statement_detail a");

			query.append(" Left Join (");
			query.append(" select rd.stmtdtlid, sum(rd.payamount) as payamount");
			query.append(" From mfg_receivepayment r");
			query.append(" Inner Join mfg_receivepaymentdetail rd On rd.rpayid = r.rpayid");
			query.append(" Where r.paydate < '").append(fromDate).append("' And r.status='A'");
			query.append(" Group by rd.stmtdtlid");
			query.append(" ) b on b.stmtdtlid = a.stmtdtlid");

			query.append(" Left Join (");
			query.append(" select cn.stmtdtlid, sum(cn.creditamount) as creditamount");
			query.append(" From mfg_creditnote cn");
			query.append(" Where cn.creditdate < '").append(fromDate).append("' And cn.status='A'");
			query.append(" Group by cn.stmtdtlid");
			query.append(" ) c on c.stmtdtlid = a.stmtdtlid");

			query.append(" Left Join (");
			query.append(" select dn.stmtdtlid, sum(dn.debitamount) as debitamount");
			query.append(" From mfg_debitnote dn");
			query.append(" Where dn.debitdate < '").append(fromDate).append("' And dn.status='A'");
			query.append(" Group by dn.stmtdtlid");
			query.append(" ) d on d.stmtdtlid = a.stmtdtlid");

			query.append(" Where a.stmtid = ").append(stmtid);
			query.append(" And a.salesdate < '").append(fromDate).append("'");
			
			try{
				dataMap = _gs.searchDataMap(query, null);
			}catch(Exception e){
				logger.error(e, e);
			}
		}
		
		
		return dataMap;
	}
	
	private StringBuffer buildYearToDateStatementQuery(Map parameters){
		StringBuffer query = new StringBuffer();
		String stmtMonth = _req.getParameter("stmtmonth");
		String stmtYear = _req.getParameter("stmtyear");
		
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		
		// Get Statement Detail
		query = new StringBuffer();
		query.append("Select 'INV' As stmtdesc");		
		query.append(", a.").append(MFG_StatementDetailDef.transno);
		query.append(", a.").append(MFG_StatementDetailDef.salesdate);
		query.append(", a.").append(MFG_StatementDetailDef.duedate);
		query.append(", a.").append(MFG_StatementDetailDef.amount);		
		query.append(", DATEDIFF(now(), a.").append(MFG_StatementDetailDef.duedate).append(") as pastdue");
		query.append(", '' as description");
		query.append(", (a.").append(MFG_StatementDetailDef.amount).append(" * 1) as balamount");
		
		query.append(" From mfg_statement_detail a");
		query.append(" Inner Join mfg_statement b on b.stmtid = a.stmtid");					
		query.append(" Where  b.companyid = '").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And (a.stmtid = ").append(parameters.get(MFG_StatementDef.stmtid.name));
		query.append(" Or b.customerid=").append(parameters.get(MFG_StatementDef.customerid.name)).append(")");		
		query.append(" And a.status!='D'");

		if(!CommonUtil.isEmpty(stmtMonth) && !CommonUtil.isEmpty(stmtYear)){
			query.append(" And MONTH(salesdate) = ").append(stmtMonth);
			query.append(" And YEAR(salesdate) = ").append(stmtYear);
		}
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And salesdate Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
		}else{ 
			if(!CommonUtil.isEmpty(fromDate)){
				query.append(" And salesdate >= ");
				query.append("'").append(fromDate).append("'");
			}else if(!CommonUtil.isEmpty(toDate)){
				query.append(" And salesdate <= ");
				query.append("'").append(toDate).append("'");
			}
		}
//		query.append(" Group By a.transno");
		
		query.append(" Union ");
		
		// Receive Payment
		query.append(" Select");
		query.append(" 'OR' As stmtdesc");		
		query.append(", a.").append(MFG_ReceivePaymentDef.rpayno).append(" as transno");		
		query.append(", a.").append(MFG_ReceivePaymentDef.paydate).append(" as salesdate");
		query.append(", null as duedate");
		query.append(", a.").append(MFG_ReceivePaymentDef.payamount).append(" as amount");
		query.append(", null as pastdue");
		query.append(", a.").append(MFG_ReceivePaymentDef.payrefno).append(" As description");
		query.append(", (a.").append(MFG_ReceivePaymentDef.payamount).append(" * -1) as balamount");
		
		query.append(" From ").append(MFG_ReceivePaymentDef.TABLE).append(" a");		
		
		query.append(" Where  a.companyid = '").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.customerid=").append(parameters.get(MFG_StatementDef.customerid.name));		
		query.append(" And a.status!='D'");
//		query.append(" And (b.amount-COALESCE(c.payamount,0)-COALESCE(a.creditamount,0))>0");
		
		if(!CommonUtil.isEmpty(stmtMonth) && !CommonUtil.isEmpty(stmtYear)){
			query.append(" And MONTH(").append(MFG_ReceivePaymentDef.paydate).append(") = ").append(stmtMonth);
			query.append(" And YEAR(").append(MFG_ReceivePaymentDef.paydate).append(") = ").append(stmtYear);
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_ReceivePaymentDef.paydate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
		}else{
			if(!CommonUtil.isEmpty(fromDate)){			
				query.append(" And a.").append(MFG_ReceivePaymentDef.paydate).append(" >= ");
				query.append("'").append(fromDate).append("'");
			}else if(!CommonUtil.isEmpty(toDate)){			
				query.append(" And a.").append(MFG_ReceivePaymentDef.paydate).append(" <= ");
				query.append("'").append(toDate).append("'");
			} 
		}
		
		query.append(" Union ");
		
		// Credit Note
		query.append(" Select");
		query.append(" 'CN' As stmtdesc");
		query.append(", a.").append(MFG_CreditNoteDef.cnno).append(" as transno");		
		query.append(", a.").append(MFG_CreditNoteDef.creditdate).append(" as salesdate");
		query.append(", null as duedate");
		query.append(", a.").append(MFG_CreditNoteDef.creditamount).append(" as amount");
		query.append(", null as pastdue");
		query.append(", a.").append(MFG_CreditNoteDef.description);
		query.append(", (a.").append(MFG_CreditNoteDef.creditamount).append(" * -1) as balamount");
		
		query.append(" From ").append(MFG_CreditNoteDef.TABLE).append(" a");
		query.append(" Inner Join mfg_statement_detail b On b.stmtdtlid = a.stmtdtlid");
		
		query.append(" Where  a.companyid = '").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And (b.stmtid = ").append(parameters.get(MFG_StatementDef.stmtid.name));
		query.append(" Or a.customerid=").append(parameters.get(MFG_StatementDef.customerid.name)).append(")");		
		query.append(" And a.status!='D'");
//		query.append(" And (b.amount-COALESCE(c.payamount,0)-COALESCE(a.creditamount,0))>0");
		
		if(!CommonUtil.isEmpty(stmtMonth) && !CommonUtil.isEmpty(stmtYear)){
			query.append(" And MONTH(a.creditdate) = ").append(stmtMonth);
			query.append(" And YEAR(a.creditdate) = ").append(stmtYear);
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.creditdate Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
		}else{
			if(!CommonUtil.isEmpty(fromDate)){			
				query.append(" And a.creditdate >= ");
				query.append("'").append(fromDate).append("'");
			}else if(!CommonUtil.isEmpty(toDate)){			
				query.append(" And a.creditdate <= ");
				query.append("'").append(toDate).append("'");
			} 
		}
		
		query.append(" Union ");
		
		// Debit Note
		query.append(" Select");
		query.append(" 'DN' As stmtdesc");		
		query.append(", a.").append(MFG_DebitNoteDef.dnno).append(" as transno");		
		query.append(", a.").append(MFG_DebitNoteDef.debitdate).append(" as salesdate");
		query.append(", null as duedate");
		query.append(", a.").append(MFG_DebitNoteDef.debitamount).append(" as amount");
		query.append(", null as pastdue");
		query.append(", a.").append(MFG_DebitNoteDef.description);
		query.append(", (a.").append(MFG_DebitNoteDef.debitamount).append(" * 1) as balamount");
		
		query.append(" From ").append(MFG_DebitNoteDef.TABLE).append(" a");
		query.append(" Inner Join mfg_statement_detail b On b.stmtdtlid = a.stmtdtlid");
		
		query.append(" Where  a.companyid = '").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And (b.stmtid = ").append(parameters.get(MFG_StatementDef.stmtid.name));
		query.append(" Or a.customerid=").append(parameters.get(MFG_StatementDef.customerid.name)).append(")");		
		query.append(" And a.status!='D'");

		
		if(!CommonUtil.isEmpty(stmtMonth) && !CommonUtil.isEmpty(stmtYear)){
			query.append(" And MONTH(a.debitdate) = ").append(stmtMonth);
			query.append(" And YEAR(a.debitdate) = ").append(stmtYear);
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.debitdate Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
		}else{
			if(!CommonUtil.isEmpty(fromDate)){			
				query.append(" And a.debitdate >= ");
				query.append("'").append(fromDate).append("'");
			}else if(!CommonUtil.isEmpty(toDate)){			
				query.append(" And a.debitdate <= ");
				query.append("'").append(toDate).append("'");
			} 
		}
		
		query.append(" Order By salesdate, transno");
		
		return query;
	}
}
