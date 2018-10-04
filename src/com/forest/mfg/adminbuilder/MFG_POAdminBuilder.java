package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.builder.SupplierProfileAdminBuilder;
import com.forest.common.services.GenericServices;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.ReportUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.beandef.MFG_PODef;
import com.forest.mfg.beandef.MFG_PODetailDef;

public class MFG_POAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_POAdminBuilder.class);
	public MFG_POAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JasperPrint buildReport()throws Exception{
		JasperPrint jprint = null;
		Map parameters = new HashMap();
		ArrayList arrayListSearch = null;
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		String poid = _req.getParameter(MFG_PODef.poid.name);
		
		subClass = BeanDefUtil.getSubClass(_defClass);
		
		ArrayList addArray = null;
		ArrayList searchArray = null;
				
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
		_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, searchArray, _accessByCompany);
		
		arrayListSearch = (ArrayList)_gs.search(_defClass, null, searchArray, addArray, _attrReqDataMap, null, "1", _reqAction).get(1);				
		if(arrayListSearch.size()>0){
			parameters = (HashMap) arrayListSearch.get(0);							
		}
		
		// Get Customer Info
		SupplierProfileAdminBuilder spBuilder = new SupplierProfileAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, null, null, null);
		parameters.putAll(spBuilder.getSupplierInfo((String) parameters.get(MFG_PODef.supplierid.name)));
		
		query.append("Select");
		query.append(" a.").append(MFG_PODetailDef.name).append(",");
		query.append(" a.").append(MFG_PODetailDef.price).append(",");
		query.append(" a.").append(MFG_PODetailDef.qty);		
		query.append(" From ").append(MFG_PODetailDef.TABLE).append(" a");		
		query.append(" Where a.").append(MFG_PODetailDef.poid).append(" = ").append(poid);
		
		arrayListSearch = gs.searchDataArray(query);
		
		logger.debug("arrayListSearch = "+arrayListSearch);
		
		ReportUtil rptUtil = new ReportUtil();
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String subReportDir = ResourceUtil.getSubReportDirectory(_shopInfoBean);
		
		String logoFile = subReportDir + "logo.jpg";
		String reportFilename = reportDir + "PO.jasper"; 
		parameters.put("REPORT_DIR", reportDir);
		parameters.put("SUBREPORT_DIR", subReportDir);		
		parameters.put("LOGO", logoFile);
		
		logger.debug("parameters = "+parameters);
		jprint = rptUtil.getReportPrint(reportFilename, arrayListSearch, parameters);
		
		return jprint;
	}
}
