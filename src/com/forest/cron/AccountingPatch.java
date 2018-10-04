package com.forest.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.BaseService;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.LoadTableObject;
import com.forest.mfg.beandef.MFG_CreditNoteDef;
import com.forest.mfg.beandef.MFG_DebitNoteDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDetailDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_StatementDetailDef;

public class AccountingPatch {
	private static Logger logger = Logger.getLogger(AccountingPatch.class);
	
	// Update Account at 00:01 everyday
	public static void main (String[] args){
		BaseService baseServ = new BaseService();
		Connection conn = null;		

		try{
			logger.info("Patch Statement Start");			
			conn = baseServ.getDirectConnection();
			
			AccountingPatch ap = new AccountingPatch();
			ap.cronPatchAccounting(conn);
			
			StatementPatch sp = new StatementPatch();
			sp.patchStatementSalesAmount(conn);
			
			PatchSalesDate t = new PatchSalesDate();			
			t.patch(conn);
			
		}catch(Exception e){
			logger.debug(e, e);
		}finally{		
			DBUtil.free(conn, null, null);
			logger.info("Patch Statement End");
		}
    }
	
	private void cronPatchAccounting(Connection conn){
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		AccountingPatch acct = new AccountingPatch();
		StringBuffer query = new StringBuffer();
		try{
			logger.info("Patch Account Aging Start");						
			LoadTableObject.loadTable(conn);
			
			query.append("Select ");
			query.append(MFG_StatementDef.customerid).append(",");
			query.append(MFG_StatementDef.companyid);
			query.append(" From ").append(MFG_StatementDef.TABLE);
			query.append(" Where ");
			query.append(MFG_StatementDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
			pstmt = conn.prepareStatement(query.toString());
			rs = pstmt.executeQuery();
			while(rs.next()){
				acct.accountAgingPatchSingle(conn, rs.getString(CustomerProfileDef.companyid.name), rs.getString(CustomerProfileDef.customerid.name));
			}
		}catch(Exception e){
			logger.debug(e, e);
		}finally{
			DBUtil.free(null, pstmt, null);
			logger.info("Patch Account Aging End");
		}
	}
	
	public void accountAgingPatchSingle(Connection conn, String companyid, String customerid)throws Exception{
		String[] array = {customerid};
		accountAgingPatch(conn, companyid, array);
	}
	
	public void accountAgingPatch(Connection conn, String companyid, String[] customerid)throws Exception{
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;			
		StringBuffer queryUpdate = new StringBuffer();
		
		queryUpdate.append("Update ").append(MFG_StatementDef.TABLE).append(" set");
		queryUpdate.append(" ").append(MFG_StatementDef.totalamt).append("=?, ").append(MFG_StatementDef.currentamt).append("=?, ").append(MFG_StatementDef.past1dayamt).append("=?, ").append(MFG_StatementDef.past30dayamt).append("=?,");
		queryUpdate.append(" ").append(MFG_StatementDef.past60dayamt).append("=?, ").append(MFG_StatementDef.past90dayamt).append("=?");
		queryUpdate.append(" Where ").append(MFG_StatementDef.companyid).append("=? And ").append(MFG_StatementDef.customerid).append("=?");		
		pstmtUpdate = conn.prepareStatement(queryUpdate.toString());
		
		if(companyid==null && customerid==null){
			// Daily Patch
			pstmt = conn.prepareStatement(accountAgingDailyQuery().toString());
		}else{
			// Every time when touch invoice, credit note, debit note or receive payment.
			pstmt = conn.prepareStatement(accountAgingQuery(companyid, customerid).toString());
		}
		
		rs = pstmt.executeQuery();
		while(rs.next()){
			pstmtUpdate.setDouble(1, rs.getDouble("totalamt"));
			pstmtUpdate.setDouble(2, rs.getDouble("currentamt"));
			pstmtUpdate.setDouble(3, rs.getDouble("past1dayamt"));
			pstmtUpdate.setDouble(4, rs.getDouble("past30dayamt"));
			pstmtUpdate.setDouble(5, rs.getDouble("past60dayamt"));
			pstmtUpdate.setDouble(6, rs.getDouble("past90dayamt"));
			pstmtUpdate.setString(7, rs.getString(MFG_StatementDef.companyid.name));
			pstmtUpdate.setString(8, rs.getString(MFG_StatementDef.customerid.name));
			pstmtUpdate.executeUpdate();			
		}		
	}
	
	private StringBuffer accountAgingQuery(String companyid, String[] customerid)throws Exception{	
		StringBuffer query = new StringBuffer();		
		StringBuffer criteriaBuffer = null;
		String key=null;
		String value=null;
		Iterator it = null;
		HashMap categoryMap = new LinkedHashMap();
		categoryMap.put("b", "");
		categoryMap.put("d", "DATEDIFF(now(), a."+MFG_StatementDetailDef.duedate+") < 1");
		categoryMap.put("e", "DATEDIFF(now(), a."+MFG_StatementDetailDef.duedate+") Between 1 And 30");
		categoryMap.put("f", "DATEDIFF(now(), a."+MFG_StatementDetailDef.duedate+") Between 31 And 60");
		categoryMap.put("g", "DATEDIFF(now(), a."+MFG_StatementDetailDef.duedate+") Between 61 And 90");
		categoryMap.put("h", "DATEDIFF(now(), a."+MFG_StatementDetailDef.duedate+") > 90");
		
		query.append("Select a.").append(MFG_StatementDef.companyid).append(", a.").append(MFG_StatementDef.customerid).append(",");
		query.append(" Sum(b.").append(MFG_StatementDetailDef.amount).append("-COALESCE(b.").append(MFG_ReceivePaymentDetailDef.payamount).append(",0)-COALESCE(b.").append(MFG_CreditNoteDef.creditamount).append(",0)+COALESCE(b.").append(MFG_DebitNoteDef.debitamount).append(",0)) as totalamt,");
		query.append(" Sum(d.").append(MFG_StatementDetailDef.amount).append("-COALESCE(d.").append(MFG_ReceivePaymentDetailDef.payamount).append(",0)-COALESCE(d.").append(MFG_CreditNoteDef.creditamount).append(",0)+COALESCE(d.").append(MFG_DebitNoteDef.debitamount).append(",0)) as currentamt,");
		query.append(" Sum(e.").append(MFG_StatementDetailDef.amount).append("-COALESCE(e.").append(MFG_ReceivePaymentDetailDef.payamount).append(",0)-COALESCE(e.").append(MFG_CreditNoteDef.creditamount).append(",0)+COALESCE(e.").append(MFG_DebitNoteDef.debitamount).append(",0)) as past1dayamt,");
		query.append(" Sum(f.").append(MFG_StatementDetailDef.amount).append("-COALESCE(f.").append(MFG_ReceivePaymentDetailDef.payamount).append(",0)-COALESCE(f.").append(MFG_CreditNoteDef.creditamount).append(",0)+COALESCE(f.").append(MFG_DebitNoteDef.debitamount).append(",0)) as past30dayamt,");
		query.append(" Sum(g.").append(MFG_StatementDetailDef.amount).append("-COALESCE(g.").append(MFG_ReceivePaymentDetailDef.payamount).append(",0)-COALESCE(g.").append(MFG_CreditNoteDef.creditamount).append(",0)+COALESCE(g.").append(MFG_DebitNoteDef.debitamount).append(",0)) as past60dayamt,");
		query.append(" Sum(h.").append(MFG_StatementDetailDef.amount).append("-COALESCE(h.").append(MFG_ReceivePaymentDetailDef.payamount).append(",0)-COALESCE(h.").append(MFG_CreditNoteDef.creditamount).append(",0)+COALESCE(h.").append(MFG_DebitNoteDef.debitamount).append(",0)) as past90dayamt");
		
		query.append(" From ").append(MFG_StatementDef.TABLE).append(" a");
		query.append(" Left Join ").append(CustomerProfileDef.TABLE).append(" i on i.").append(CustomerProfileDef.customerid).append("=a.").append(MFG_StatementDef.customerid);		
		it = categoryMap.keySet().iterator();
		while(it.hasNext()){
			criteriaBuffer = new StringBuffer();
			key = (String) it.next();
			value = (String) categoryMap.get(key);
			logger.debug(key+" = "+value);
			query.append(" Left Join (");
			query.append(" Select a.").append(MFG_StatementDetailDef.stmtid);
			query.append(", a.").append(MFG_StatementDetailDef.stmtdtlid);
			query.append(", a.").append(MFG_StatementDetailDef.amount);
			query.append(", Sum(b.").append(MFG_ReceivePaymentDetailDef.payamount).append(") as ").append(MFG_ReceivePaymentDetailDef.payamount);
			query.append(", c.").append(MFG_CreditNoteDef.creditamount);
			query.append(", d.").append(MFG_DebitNoteDef.debitamount);
			query.append(" From ").append(MFG_StatementDef.TABLE).append(" stmt");
			query.append(" Inner Join ").append(MFG_StatementDetailDef.TABLE).append(" a On a.").append(MFG_StatementDetailDef.stmtid).append(" = stmt.").append(MFG_StatementDef.stmtid);
			query.append(" Left Join ").append(MFG_ReceivePaymentDetailDef.TABLE).append(" b on b.").append(MFG_ReceivePaymentDetailDef.stmtdtlid).append(" = a.").append(MFG_StatementDetailDef.stmtdtlid);
			query.append(" Left Join ").append(MFG_CreditNoteDef.TABLE).append(" c on c.").append(MFG_CreditNoteDef.stmtdtlid).append(" = a.").append(MFG_StatementDetailDef.stmtdtlid);
			query.append(" Left Join ").append(MFG_DebitNoteDef.TABLE).append(" d on d.").append(MFG_DebitNoteDef.stmtdtlid).append(" = a.").append(MFG_StatementDetailDef.stmtdtlid);
			
			if(!CommonUtil.isEmpty(companyid)){
				criteriaBuffer.append(" stmt.").append(MFG_StatementDef.companyid).append("='").append(companyid).append("'");
			}
			if(customerid!=null){		
				if(criteriaBuffer.length()>0){
					criteriaBuffer.append(" And");
				}
				criteriaBuffer.append(" stmt.").append(MFG_StatementDef.customerid).append(" In (").append(DBUtil.arrayToString(customerid, CustomerProfileDef.customerid)).append(")");				
			}
			if(criteriaBuffer.length()>0 && !CommonUtil.isEmpty(value)){
				criteriaBuffer.append(" And").append(" ").append(value);				
			}

			if(criteriaBuffer.length()>0){
				query.append(" Where ").append(criteriaBuffer);
			}
			
			query.append(" Group by a.").append(MFG_StatementDetailDef.stmtdtlid);
			query.append(", a.").append(MFG_StatementDetailDef.amount);
			query.append(", c.").append(MFG_CreditNoteDef.creditamount).append(", d.").append(MFG_DebitNoteDef.debitamount);
			query.append(" ) ").append(key).append(" On ").append(key).append(".").append(MFG_StatementDetailDef.stmtdtlid).append(" = b.").append(MFG_StatementDetailDef.stmtdtlid);
		}				
				
		if(!CommonUtil.isEmpty(companyid) && customerid!=null){
			query.append(" Where");
			query.append(" a.").append(MFG_StatementDef.companyid).append("='").append(companyid).append("'");
			query.append(" And");
			query.append(" a.").append(MFG_StatementDef.customerid).append(" In (").append(DBUtil.arrayToString(customerid, CustomerProfileDef.customerid)).append(")");
		}

		query.append(" Group By a.").append(MFG_StatementDef.companyid).append(", a.").append(MFG_StatementDef.customerid);
		
//		Remark it because need to update even got no debt
//		query.append(" Having Sum(b.").append(MFG_StatementDetailDef.amount);
//		query.append("-COALESCE(b.").append(MFG_ReceivePaymentDetailDef.payamount).append(",0)-COALESCE(b.");
//		query.append(MFG_CreditNoteDef.creditamount).append(",0)+COALESCE(b.").append(MFG_DebitNoteDef.debitamount).append(",0)) > 0");		
		
		logger.debug("accountAgingPatch query = "+query);
		return query;
	}
	
	private StringBuffer accountAgingDailyQuery()throws Exception{	
		StringBuffer query = new StringBuffer();		
		
		String key=null;
		String value=null;
		Iterator it = null;
		HashMap categoryMap = new LinkedHashMap();
		categoryMap.put("b", "");
		categoryMap.put("d", "DATEDIFF(now(), "+MFG_StatementDetailDef.duedate+") < 1");
		categoryMap.put("e", "DATEDIFF(now(), "+MFG_StatementDetailDef.duedate+") Between 1 And 30");
		categoryMap.put("f", "DATEDIFF(now(), "+MFG_StatementDetailDef.duedate+") Between 31 And 60");
		categoryMap.put("g", "DATEDIFF(now(), "+MFG_StatementDetailDef.duedate+") Between 61 And 90");
		categoryMap.put("h", "DATEDIFF(now(), "+MFG_StatementDetailDef.duedate+") > 90");
		
		query.append("Select a.").append(MFG_StatementDef.companyid).append(", a.").append(MFG_StatementDef.customerid).append(",");
		query.append(" Sum(b.").append(MFG_StatementDetailDef.amount).append("-COALESCE(b.").append(MFG_StatementDetailDef.payamount).append(",0)-COALESCE(b.").append(MFG_StatementDetailDef.creditamount).append(",0)+COALESCE(b.").append(MFG_StatementDetailDef.debitamount).append(",0)) as totalamt,");
		query.append(" Sum(d.").append(MFG_StatementDetailDef.amount).append("-COALESCE(d.").append(MFG_StatementDetailDef.payamount).append(",0)-COALESCE(d.").append(MFG_StatementDetailDef.creditamount).append(",0)+COALESCE(d.").append(MFG_StatementDetailDef.debitamount).append(",0)) as currentamt,");
		query.append(" Sum(e.").append(MFG_StatementDetailDef.amount).append("-COALESCE(e.").append(MFG_StatementDetailDef.payamount).append(",0)-COALESCE(e.").append(MFG_StatementDetailDef.creditamount).append(",0)+COALESCE(e.").append(MFG_StatementDetailDef.debitamount).append(",0)) as past1dayamt,");
		query.append(" Sum(f.").append(MFG_StatementDetailDef.amount).append("-COALESCE(f.").append(MFG_StatementDetailDef.payamount).append(",0)-COALESCE(f.").append(MFG_StatementDetailDef.creditamount).append(",0)+COALESCE(f.").append(MFG_StatementDetailDef.debitamount).append(",0)) as past30dayamt,");
		query.append(" Sum(g.").append(MFG_StatementDetailDef.amount).append("-COALESCE(g.").append(MFG_StatementDetailDef.payamount).append(",0)-COALESCE(g.").append(MFG_StatementDetailDef.creditamount).append(",0)+COALESCE(g.").append(MFG_StatementDetailDef.debitamount).append(",0)) as past60dayamt,");
		query.append(" Sum(h.").append(MFG_StatementDetailDef.amount).append("-COALESCE(h.").append(MFG_StatementDetailDef.payamount).append(",0)-COALESCE(h.").append(MFG_StatementDetailDef.creditamount).append(",0)+COALESCE(h.").append(MFG_StatementDetailDef.debitamount).append(",0)) as past90dayamt");
		
		query.append(" From ").append(MFG_StatementDef.TABLE).append(" a");
		query.append(" Left Join ").append(CustomerProfileDef.TABLE).append(" i on i.").append(CustomerProfileDef.customerid).append("=a.").append(MFG_StatementDef.customerid);
		
		it = categoryMap.keySet().iterator();
		while(it.hasNext()){
			key = (String) it.next();
			value = (String) categoryMap.get(key);
			logger.debug(key+" = "+value);
			query.append(" Left Join (");
			query.append(" Select ").append(MFG_StatementDetailDef.stmtid);
			query.append(", ").append(MFG_StatementDetailDef.stmtdtlid);
			query.append(", ").append(MFG_StatementDetailDef.amount);
			query.append(", ").append(MFG_StatementDetailDef.payamount);
			query.append(", ").append(MFG_StatementDetailDef.creditamount);
			query.append(", ").append(MFG_StatementDetailDef.debitamount);			
			query.append(" From ").append(MFG_StatementDetailDef.TABLE);			
			
			if(!CommonUtil.isEmpty(value)){
				query.append(" Where ").append(value);
			}
						
			query.append(" ) ").append(key).append(" On ").append(key).append(".").append(MFG_StatementDetailDef.stmtdtlid).append(" = b.").append(MFG_StatementDetailDef.stmtdtlid);
		}				
				
		query.append(" Group By a.").append(MFG_StatementDef.companyid).append(", a.").append(MFG_StatementDef.customerid);
		query.append(" Having Sum(b.").append(MFG_StatementDetailDef.amount);
		query.append("-COALESCE(b.").append(MFG_ReceivePaymentDetailDef.payamount).append(",0)-COALESCE(b.");
		query.append(MFG_CreditNoteDef.creditamount).append(",0)+COALESCE(b.").append(MFG_DebitNoteDef.debitamount).append(",0)) > 0");		
		logger.debug("accountAgingDailyQuery query = "+query);
		return query;
	}
}
