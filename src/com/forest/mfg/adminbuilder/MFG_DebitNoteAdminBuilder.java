package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
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
import com.forest.mfg.beandef.MFG_DebitNoteDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_TransactionDef;

public class MFG_DebitNoteAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_DebitNoteAdminBuilder.class);
	public MFG_DebitNoteAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		json = super.requestJsonHandler();
		if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction) || GeneralConst.DELETE.equals (_reqAction)){
			MFG_StatementAdminBuilder stmtBuilder = new MFG_StatementAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, 
					MFG_StatementDef.class, ModuleConfig.COMPANY_SELECTED, _resources);
			
			stmtBuilder.updateDebitAmountToStatement((String)_attrReqDataMap[0].get(MFG_DebitNoteDef.transid.name));
			stmtBuilder.updateStatementDetail((String)_attrReqDataMap[0].get(MFG_DebitNoteDef.transid.name));
			
			// Update Account Detail
			AccountingPatch acc = new AccountingPatch();
			acc.accountAgingPatchSingle(_dbConnection, _shopInfoBean.getShopName(), (String)_attrReqDataMap[0].get(MFG_DebitNoteDef.customerid.name));
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
		StringBuffer query = new StringBuffer();
		
		String dnid = _req.getParameter(MFG_DebitNoteDef.dnid.name);
		
		query.append("Select");
	
		query.append(" a.").append(MFG_DebitNoteDef.transno).append(",");
		query.append(" a.").append(MFG_DebitNoteDef.dnno).append(",");
		query.append(" a.").append(MFG_DebitNoteDef.debitdate).append(",");		
		query.append(" COALESCE(a.").append(MFG_DebitNoteDef.debitamtbeforegst).append(", ").append(MFG_DebitNoteDef.debitamount).append(") As ").append(MFG_DebitNoteDef.debitamtbeforegst).append(",");
		query.append(" COALESCE(a.").append(MFG_DebitNoteDef.gst_amt).append(", 0) As ").append(MFG_DebitNoteDef.gst_amt).append(",");
		query.append(" a.").append(MFG_DebitNoteDef.debitamount).append(",");
		query.append(" a.").append(MFG_DebitNoteDef.custrefno).append(",");
		query.append(" a.").append(MFG_DebitNoteDef.description).append(",");		
		
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
		query.append(" b.").append(CustomerProfileDef.terms).append(",");
		query.append(" c.").append(AgentProfileDef.name).append(" As ").append(CustomerProfileDef.salesby);
		
		query.append(" From ").append(MFG_DebitNoteDef.TABLE).append(" a");
		query.append(" Left Join ").append(CustomerProfileDef.TABLE).append(" b");
		query.append(" on b.").append(CustomerProfileDef.customerid).append(" = a.").append(MFG_DebitNoteDef.customerid);
		
		query.append(" Left Join ").append(AgentProfileDef.TABLE).append(" c");
		query.append(" on c.").append(AgentProfileDef.id).append(" = b.").append(CustomerProfileDef.salesby);
		
		query.append(" Where a.").append(MFG_DebitNoteDef.dnid).append(" = ").append(dnid);
		arrayListSearch = gs.searchDataArray(query);
						
		Map countryMap = SelectBuilder.getHASH_COUNTRY(_dbConnection, _shopInfoBean);
		Map termMap = MFG_SelectBuilder.getHASH_TERM(_dbConnection, _shopInfoBean);
		
		if(arrayListSearch.size()>0){
			parameters = (HashMap) arrayListSearch.get(0);			
			if(CommonUtil.isEmpty((String) parameters.get(CustomerProfileDef.country.name))){
				parameters.put(CustomerProfileDef.country.name,"");
			}else{
				parameters.put(CustomerProfileDef.country.name, countryMap.get(parameters.get(CustomerProfileDef.country.name)));
			}
			parameters.put(MFG_TransactionDef.terms.name, termMap.get(parameters.get(MFG_TransactionDef.terms.name)));
			logger.debug("parameters = "+parameters);			
		}
		
		
		ReportUtil rptUtil = new ReportUtil();
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String subReportDir = ResourceUtil.getSubReportDirectory(_shopInfoBean);
		if(DateUtil.getDate((String)parameters.get(MFG_DebitNoteDef.debitdate.name)).after(DateUtil.getDate("2013-12-31"))){			
			if(FileUtil.isExist(subReportDir+"2014")){
				subReportDir += "2014/";
			}
		}
		String logoFile = subReportDir + "logo.jpg";
		String reportFilename = reportDir + "DebitNote.jasper";
		parameters.put("REPORT_DIR", reportDir);
		parameters.put("SUBREPORT_DIR", subReportDir);		
		parameters.put("title", "DEBIT NOTES");
		parameters.put("LOGO", logoFile);
		jprint = rptUtil.getReportPrint(reportFilename, arrayListSearch, parameters);
		
		return jprint;
	}
}
