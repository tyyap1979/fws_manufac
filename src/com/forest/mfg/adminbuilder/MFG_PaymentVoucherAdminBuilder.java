package com.forest.mfg.adminbuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.SupplierProfileDef;
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
import com.forest.mfg.beandef.MFG_PaymentVoucherDef;
import com.forest.mfg.beandef.MFG_PaymentVoucherDetailDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDetailDef;
import com.forest.mfg.beandef.MFG_StatementDetailDef;
import com.forest.mfg.beandef.MFG_SupplierInvoiceDef;
import com.forest.mfg.beandef.MFG_TransactionDef;


public class MFG_PaymentVoucherAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_PaymentVoucherAdminBuilder.class);
	public MFG_PaymentVoucherAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();		 
		String allTransNo = "";
//		AccountingPatch acc = new AccountingPatch();
		HashMap dataMap = null;
//		MFG_StatementAdminBuilder stmtBuilder = new MFG_StatementAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, 
//				MFG_StatementDef.class, ModuleConfig.COMPANY_SELECTED, _resources);
		
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){
			json = super.requestJsonHandler();		
			String[] pvIdArray = {(String)_attrReqDataMap[0].get(MFG_PaymentVoucherDef.pvid.name)};
			updatePaymentAmount(pvIdArray);
//			dataMap = getInfo(pvid);			
		}else if(GeneralConst.DELETE.equals (_reqAction)){					
//			dataMap = getInfo(pvid);
			json = super.requestJsonHandler();
			removePaymentAmount(_req.getParameterValues(MFG_PaymentVoucherDef.pvid.name));
			
		}else if("LIST_OUTSTANDING".equals (_reqAction)){		
			json = new JSONObject(getOutstandingInvoice());		
			json.put("rc", "3");
			json.put("md", "");
			json.put("subclasslength", 1);
		}else{
			json = super.requestJsonHandler();
		}
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction) || GeneralConst.DELETE.equals (_reqAction)){
			
//			stmtBuilder.updatePaymentAmountToStatement((String[])dataMap.get(MFG_StatementDetailDef.transid.name));
//			stmtBuilder.updateStatementDetail((String[])dataMap.get(MFG_StatementDetailDef.transid.name));
//			acc.accountAgingPatch(_dbConnection, _shopInfoBean.getShopName(), (String[])dataMap.get(MFG_PaymentVoucherDef.supp_id.name));
		}
		if(!CommonUtil.isEmpty(allTransNo)){			
			// Update Account Detail			
			
		}
		
		return json;
	}
	
	public HashMap getOutstandingInvoice()throws Exception{
		HashMap map = new HashMap();
		HashMap sub0Map = new HashMap();
		StringBuffer query = new StringBuffer();
		query.append("Select ");
		query.append(MFG_SupplierInvoiceDef.invid);
		query.append(",").append(MFG_SupplierInvoiceDef.invno);
		query.append(",").append(MFG_SupplierInvoiceDef.invdate);
		query.append(",").append(MFG_SupplierInvoiceDef.invamt);
		query.append(" From ").append(MFG_SupplierInvoiceDef.TABLE);
		query.append(" Where ").append(MFG_SupplierInvoiceDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And ").append(MFG_SupplierInvoiceDef.supp_id).append("=").append(_req.getParameter(MFG_SupplierInvoiceDef.supp_id.name));
		query.append(" And ").append(MFG_SupplierInvoiceDef.status).append("='").append(_req.getParameter(MFG_SupplierInvoiceDef.status.name)).append("'");
		
		ArrayList dataList = _gs.searchDataArray(query);
		ArrayList Sub0 = new ArrayList();
		ArrayList sub0List = new ArrayList();
		for(int i=0; i<dataList.size(); i++){
			Sub0 = new ArrayList();
			Sub0.add(dataList.get(i));
			sub0List.add(Sub0);
		}
		map.put("md", MFG_PaymentVoucherDetailDef.TABLE_PREFIX);
		map.put("data", sub0List);
		
		sub0Map.put("SUBR0", map);		
			
		return sub0Map;
	}
	
	public void updatePaymentAmount(String[] pvIDArray)throws Exception{
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;
		StringBuffer query = new StringBuffer();
		StringBuffer queryUpdate = new StringBuffer();
		
		try{
			if(pvIDArray!=null){
				query.append(" Select a.").append(MFG_PaymentVoucherDetailDef.invid);
				query.append(",a.").append(MFG_PaymentVoucherDetailDef.payamount);
				query.append(", If((b.").append(MFG_SupplierInvoiceDef.invamt).append(" - a.").append(MFG_PaymentVoucherDetailDef.payamount).append("=0), '2', '0') As status");				
				query.append(" From ").append(MFG_PaymentVoucherDetailDef.TABLE).append(" a");		
				query.append(" Inner Join ").append(MFG_PaymentVoucherDef.TABLE).append(" c On c.").append(MFG_PaymentVoucherDef.pvid).append("=a.").append(MFG_PaymentVoucherDetailDef.pvid);
				query.append(" Inner Join ").append(MFG_SupplierInvoiceDef.TABLE).append(" b On b.");
				query.append(MFG_SupplierInvoiceDef.invid).append("=a.").append(MFG_PaymentVoucherDetailDef.invid);		
				
				query.append(" Where a.").append(MFG_PaymentVoucherDetailDef.pvid);
				query.append(" In (").append(DBUtil.arrayToString(pvIDArray, MFG_PaymentVoucherDetailDef.pvid)).append(")");
				query.append(" And c.").append(MFG_PaymentVoucherDef.status).append("='").append(MFG_SelectBuilder.ACTIVE).append("'");
				logger.debug("Select query = "+query);
				pstmt = _dbConnection.prepareStatement(query.toString());
				rs = pstmt.executeQuery();
				
				queryUpdate.append("Update ").append(MFG_SupplierInvoiceDef.TABLE);
				queryUpdate.append(" Set ").append(MFG_SupplierInvoiceDef.paidamt).append(" = ? ");
				queryUpdate.append(",").append(MFG_SupplierInvoiceDef.status).append(" = ? ");
				queryUpdate.append(" Where ").append(MFG_SupplierInvoiceDef.invid).append("=?");
				logger.debug("updatePaymentAmount = "+queryUpdate);
				pstmtUpdate = _dbConnection.prepareStatement(queryUpdate.toString());
				
				while(rs.next()){
					pstmtUpdate.setDouble(1, rs.getDouble(MFG_PaymentVoucherDetailDef.payamount.name));
					pstmtUpdate.setString(2, rs.getString(MFG_SupplierInvoiceDef.status.name));
					pstmtUpdate.setString(3, rs.getString(MFG_SupplierInvoiceDef.invid.name));
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
	}
	
	public void removePaymentAmount(String[] pvIDArray)throws Exception{
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;
		StringBuffer query = new StringBuffer();
		StringBuffer queryUpdate = new StringBuffer();
		
		try{
			if(pvIDArray!=null){
				query.append(" Select a.").append(MFG_PaymentVoucherDetailDef.invid);
				query.append(",a.").append(MFG_PaymentVoucherDetailDef.payamount);
				query.append(", If((b.").append(MFG_SupplierInvoiceDef.invamt).append(" - a.").append(MFG_PaymentVoucherDetailDef.payamount).append("=0), '2', '0') As status");				
				query.append(" From ").append(MFG_PaymentVoucherDetailDef.TABLE).append(" a");
				query.append(" Inner Join ").append(MFG_PaymentVoucherDef.TABLE).append(" c On c.").append(MFG_PaymentVoucherDef.pvid).append("=a.").append(MFG_PaymentVoucherDetailDef.pvid);
				query.append(" Inner Join ").append(MFG_SupplierInvoiceDef.TABLE).append(" b On b.");
				query.append(MFG_SupplierInvoiceDef.invid).append("=a.").append(MFG_PaymentVoucherDetailDef.invid);		
				
				query.append(" Where a.").append(MFG_PaymentVoucherDetailDef.pvid);
				query.append(" In (").append(DBUtil.arrayToString(pvIDArray, MFG_PaymentVoucherDetailDef.pvid)).append(")");
				query.append(" And c.").append(MFG_PaymentVoucherDef.status).append("='").append(GeneralConst.DELETED).append("'");
				logger.debug("Select query = "+query);
				pstmt = _dbConnection.prepareStatement(query.toString());
				rs = pstmt.executeQuery();
				
				queryUpdate.append("Update ").append(MFG_SupplierInvoiceDef.TABLE);
				queryUpdate.append(" Set ").append(MFG_SupplierInvoiceDef.paidamt).append(" = 0 ");
				queryUpdate.append(",").append(MFG_SupplierInvoiceDef.status).append(" = '").append(MFG_SelectBuilder.PAY_STATUS_OPEN).append("' ");
				queryUpdate.append(" Where ").append(MFG_SupplierInvoiceDef.invid).append("=?");
				logger.debug("removePaymentAmount = "+queryUpdate);
				pstmtUpdate = _dbConnection.prepareStatement(queryUpdate.toString());
				
				while(rs.next()){
					pstmtUpdate.setString(1, rs.getString(MFG_SupplierInvoiceDef.invid.name));
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
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = super.displayHandler();
		// Read Additional Content From File
		buffer.append(FileHandler.readAdminFile (_shopInfoBean, _clientBean));	
		return buffer;
	}
	
	public JasperPrint buildReport()throws Exception{
		JasperPrint jprint = null;
		Map parameters = new HashMap();
		ArrayList arrayListSearch = null;
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		Map countryMap = SelectBuilder.getHASH_COUNTRY(_dbConnection, _shopInfoBean);
		Map termMap = MFG_SelectBuilder.getHASH_TERM(_dbConnection, _shopInfoBean);
		
		String pvid = _req.getParameter(MFG_PaymentVoucherDef.pvid.name);
		
		query.append("Select");
		query.append(" a.*");
		query.append(", b.*");
		query.append(", c.").append(AgentProfileDef.name).append(" as paybyname");
		
		query.append(" From ").append(MFG_PaymentVoucherDef.TABLE).append(" a");
		query.append(" Left Join ").append(SupplierProfileDef.TABLE).append(" b");
		query.append(" on b.").append(SupplierProfileDef.id).append(" = a.").append(MFG_PaymentVoucherDef.supp_id);
		
		query.append(" Left Join ").append(AgentProfileDef.TABLE).append(" c");
		query.append(" on c.").append(AgentProfileDef.id).append(" = a.").append(MFG_PaymentVoucherDef.payby);
		
		query.append(" Where a.").append(MFG_PaymentVoucherDef.pvid).append(" = ").append(pvid);
		parameters = gs.searchDataMap(query, null);
		
		if(CommonUtil.isEmpty((String) parameters.get(SupplierProfileDef.country.name))){
			parameters.put(SupplierProfileDef.country.name,"");
		}else{
			parameters.put(SupplierProfileDef.country.name, countryMap.get(parameters.get(SupplierProfileDef.country.name)));
		}
		parameters.put("totalinword", NumberUtil.convert((String)parameters.get(MFG_PaymentVoucherDef.payamount.name)));
		logger.debug("parameters: "+parameters);
//		parameters.put(MFG_TransactionDef.terms.name, termMap.get(parameters.get(MFG_TransactionDef.terms.name)));
		
		
		// Detail
		subClass = BeanDefUtil.getSubClass(_defClass);
		addArray = BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		searchArray = BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
		_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, _accessByCompany);	
		arrayListSearch = (ArrayList)_gs.search(subClass[0], null, searchArray, addArray, _attrReqDataMap, null, "*", _reqAction).get(1);
//		query = new StringBuffer();
//		query.append("Select");
//		query.append(" a.*");
//		query.append(" From ").append(MFG_PaymentVoucherDetailDef.TABLE).append(" a");
//	
//		query.append(" Where a.").append(MFG_PaymentVoucherDetailDef.pvid).append(" = ").append(pvid);
//		arrayListSearch = gs.searchDataArray(query);
		logger.debug("arrayListSearch = "+arrayListSearch);
		ReportUtil rptUtil = new ReportUtil();
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String subReportDir = ResourceUtil.getSubReportDirectory(_shopInfoBean);
		
		Date payDate = null;
		if(arrayListSearch.size()>0){
			payDate = DateUtil.getDate((String)parameters.get(MFG_PaymentVoucherDef.paydate.name));
		}
		if(payDate.after(DateUtil.getDate("2013-12-31"))){			
			if(FileUtil.isExist(subReportDir+"2014")){
				subReportDir += "2014/";
			}
		}
		
		String logoFile = subReportDir + "logo.jpg";
		String reportFilename = reportDir + "PaymentVoucher.jasper"; 
		parameters.put("REPORT_DIR", reportDir);
		parameters.put("SUBREPORT_DIR", subReportDir);		
		parameters.put("LOGO", logoFile);
		
		logger.debug("parameters = "+parameters);	
		Map rowMap = null;
		for(int i=0; i<arrayListSearch.size(); i++){
			rowMap = (HashMap)arrayListSearch.get(i);			
			rowMap.put("no", String.valueOf(i+1));			
			logger.debug(rowMap);
		}
		jprint = rptUtil.getReportPrint(reportFilename, arrayListSearch, parameters);
		
		return jprint;
	}
}
