package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.builder.ModuleConfig;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.FileUtil;
import com.forest.common.util.ReportUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.cron.AccountingPatch;
import com.forest.mfg.beandef.MFG_CreditNoteDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_TransactionDef;

public class MFG_CreditNoteAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_CreditNoteAdminBuilder.class);
	public MFG_CreditNoteAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		json = super.requestJsonHandler();
		String[] cnid = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction) || GeneralConst.DELETE.equals (_reqAction)){
			MFG_StatementAdminBuilder stmtBuilder = new MFG_StatementAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, 
					MFG_StatementDef.class, ModuleConfig.COMPANY_SELECTED, _resources);
						
			if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){
				stmtBuilder.updateCreditAmountToStatement((String)_attrReqDataMap[0].get(MFG_CreditNoteDef.transid.name));
				stmtBuilder.updateStatementDetail((String)_attrReqDataMap[0].get(MFG_CreditNoteDef.transid.name));
				
				// Update Account Detail
				AccountingPatch acc = new AccountingPatch();
				acc.accountAgingPatchSingle(_dbConnection, _shopInfoBean.getShopName(), (String)_attrReqDataMap[0].get(MFG_CreditNoteDef.customerid.name));
			}else if(GeneralConst.DELETE.equals (_reqAction)){
				cnid = _req.getParameterValues(MFG_CreditNoteDef.cnid.name);
				// Get Customer and Transaction ID
				HashMap dataMap = null;
				StringBuffer query = new StringBuffer();
				query.append("Select");
				query.append(" a.").append(MFG_CreditNoteDef.transid).append(",");
				query.append(" a.").append(MFG_CreditNoteDef.customerid);
				query.append(" From ").append(MFG_CreditNoteDef.TABLE).append(" a");
				query.append(" Where a.").append(MFG_CreditNoteDef.cnid).append("=?");
				
				for(int i=0; i<cnid.length; i++){
					paramMap.put(MFG_CreditNoteDef.cnid, cnid[i]);
					dataMap = _gs.searchDataMap(query, paramMap);
					logger.debug("Get Customer and Transaction ID = "+dataMap);
					
					stmtBuilder.updateCreditAmountToStatement((String)dataMap.get(MFG_CreditNoteDef.transid.name));
					stmtBuilder.updateStatementDetail((String)dataMap.get(MFG_CreditNoteDef.transid.name));
					
					// Update Account Detail
					AccountingPatch acc = new AccountingPatch();
					acc.accountAgingPatchSingle(_dbConnection, _shopInfoBean.getShopName(), (String)dataMap.get(MFG_CreditNoteDef.customerid.name));
				}
			}
			
		}
		return json;
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
		Map countryMap = SelectBuilder.getHASH_COUNTRY(_dbConnection, _shopInfoBean);
		Map termMap = MFG_SelectBuilder.getHASH_TERM(_dbConnection, _shopInfoBean);
		String cnid = _req.getParameter(MFG_CreditNoteDef.cnid.name);
		String customerid = _req.getParameter(MFG_CreditNoteDef.customerid.name);
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		String useDate = null;
		String reportFile = null;
		
		if(CommonUtil.isEmpty(cnid)){
			reportFile = "CreditNoteReport.jasper";
			
			parameters = gs.searchDataMap(getCustomerInfoByCnId(null, customerid), null);
			arrayListSearch = gs.searchDataArray(getSingleCreditNoteSQL(null, customerid, fromDate, toDate));
			
			if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
				parameters.put(MFG_CreditNoteDef.creditdate.name, "Date From "+fromDate+" to "+toDate);
			}else {
				if(!CommonUtil.isEmpty(fromDate)){
					parameters.put(MFG_CreditNoteDef.creditdate.name, "Date From "+fromDate);
				}else if(!CommonUtil.isEmpty(toDate)){
					parameters.put(MFG_CreditNoteDef.creditdate.name, "Date To "+fromDate);
				}else{
					parameters.put(MFG_CreditNoteDef.creditdate.name,"");
				}
			}
			
		}else{
			reportFile = "CreditNote.jasper";
			
			parameters = gs.searchDataMap(getCustomerInfoByCnId(cnid, null), null);
			
			arrayListSearch = gs.searchDataArray(getSingleCreditNoteSQL(cnid, null, null, null));			
			if(arrayListSearch.size()>0){
				parameters.putAll((HashMap) arrayListSearch.get(0));
			}
		}
			
		if(CommonUtil.isEmpty((String) parameters.get(CustomerProfileDef.country.name))){
			parameters.put(CustomerProfileDef.country.name,"");
		}else{
			parameters.put(CustomerProfileDef.country.name, countryMap.get(parameters.get(CustomerProfileDef.country.name)));
		}
		parameters.put(MFG_TransactionDef.terms.name, termMap.get(parameters.get(MFG_TransactionDef.terms.name)));
		logger.debug("parameters = "+parameters);			
		
		
		
		ReportUtil rptUtil = new ReportUtil();
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String subReportDir = ResourceUtil.getSubReportDirectory(_shopInfoBean);
		
		if(!CommonUtil.isEmpty(fromDate)){
			useDate = fromDate;
		}else{
			useDate = (String)parameters.get(MFG_CreditNoteDef.creditdate.name);
		}
		
		if(!CommonUtil.isEmpty(useDate) && DateUtil.getDate(useDate).after(DateUtil.getDate("2013-12-31"))){			
			if(FileUtil.isExist(subReportDir+"2014")){
				subReportDir += "2014/";
			}
		}
		String logoFile = subReportDir + "logo.jpg";
		String reportFilename = reportDir + reportFile;
		parameters.put("REPORT_DIR", reportDir);
		parameters.put("SUBREPORT_DIR", subReportDir);		
		parameters.put("LOGO", logoFile);
		jprint = rptUtil.getReportPrint(reportFilename, arrayListSearch, parameters);
		
		return jprint;
	}
	
	private StringBuffer getCustomerInfoByCnId(String cnid, String customerid){
		StringBuffer query = new StringBuffer();
		query.append("Select");
		query.append(" b.").append(CustomerProfileDef.name).append(",");
		query.append(" b.").append(CustomerProfileDef.address).append(",");
		query.append(" b.").append(CustomerProfileDef.city).append(",");
		query.append(" b.").append(CustomerProfileDef.state).append(",");
		query.append(" b.").append(CustomerProfileDef.city).append(",");
		query.append(" b.").append(CustomerProfileDef.postcode).append(",");
		query.append(" b.").append(CustomerProfileDef.contactperson).append(",");
		query.append(" b.").append(CustomerProfileDef.country).append(",");
		query.append(" b.").append(CustomerProfileDef.phoneno).append(",");
		query.append(" b.").append(CustomerProfileDef.mobileno).append(",");
		query.append(" b.").append(CustomerProfileDef.faxno).append(",");
		query.append(" b.").append(CustomerProfileDef.code).append(",");		
		query.append(" b.").append(CustomerProfileDef.terms);
		
		query.append(" From ").append(MFG_CreditNoteDef.TABLE).append(" a");
		query.append(" Inner Join ").append(CustomerProfileDef.TABLE).append(" b");
		query.append(" on b.").append(CustomerProfileDef.customerid).append(" = a.").append(MFG_CreditNoteDef.customerid);
		
		query.append(" Where a.");
		if(!CommonUtil.isEmpty(cnid)){
			query.append(MFG_CreditNoteDef.cnid).append(" = ").append(cnid);
		}else{
			query.append(MFG_CreditNoteDef.customerid).append(" = ").append(customerid);
		}
		return query;
	}
	
	private StringBuffer getSingleCreditNoteSQL(String cnid, String customerid, String fromDate, String toDate){
		StringBuffer query = new StringBuffer();
		
		query.append("Select");
		query.append(" a.").append(MFG_CreditNoteDef.transno).append(",");
		query.append(" a.").append(MFG_CreditNoteDef.cnno).append(",");
		query.append(" a.").append(MFG_CreditNoteDef.creditdate).append(",");
		query.append(" COALESCE(a.").append(MFG_CreditNoteDef.creditamtbeforegst).append(", ").append(MFG_CreditNoteDef.creditamount).append(") As ").append(MFG_CreditNoteDef.creditamtbeforegst).append(",");
		query.append(" COALESCE(a.").append(MFG_CreditNoteDef.gst_amt).append(", 0) As ").append(MFG_CreditNoteDef.gst_amt).append(",");				
		query.append(" a.").append(MFG_CreditNoteDef.creditamount).append(",");
		query.append(" a.").append(MFG_CreditNoteDef.custrefno).append(",");
		query.append(" a.").append(MFG_CreditNoteDef.description);				
		query.append(" From ").append(MFG_CreditNoteDef.TABLE).append(" a");
		
		query.append(" Where a.");
		if(!CommonUtil.isEmpty(cnid)){
			query.append(MFG_CreditNoteDef.cnid).append(" = ").append(cnid);
		}else{
			query.append(MFG_CreditNoteDef.customerid).append(" = ").append(customerid);
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_CreditNoteDef.creditdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
		}else {
			if(!CommonUtil.isEmpty(fromDate)){
				query.append(" And a.").append(MFG_CreditNoteDef.creditdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
			}
			if(!CommonUtil.isEmpty(toDate)){
				query.append(" And a.").append(MFG_CreditNoteDef.creditdate).append(" <= ");
				query.append("'").append(toDate).append("'");
			}
		}
		return query;
	}
}
