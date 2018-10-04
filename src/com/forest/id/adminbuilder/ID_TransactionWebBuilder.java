package com.forest.id.adminbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.ReportUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.id.beandef.ID_TransactionDef;
import com.forest.id.beandef.ID_TransactionDetailDef;
import com.forest.id.beandef.ID_TransactionDetailGroupDef;
import com.forest.mfg.adminbuilder.MFG_JobSheetAdminBuilder;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.adminbuilder.MFG_StatementAdminBuilder;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_CustProductSelectionDef;
import com.forest.mfg.beandef.MFG_JobSheetDef;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_StatementDef;

public class ID_TransactionWebBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (ID_TransactionWebBuilder.class);
	
	public ID_TransactionWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);		
		
	}

	public StringBuffer displayHandler()throws Exception{
//		MFG_JobSheetWebBuilder jobBuilder = new MFG_JobSheetWebBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, MFG_JobSheetDef.class, localAccessByShop, _resources);
//		jobBuilder.createJobSheet(_shopInfoBean.getShopName(), "168", "170");
		ArrayList searchArray = null;
		ArrayList addArray = null;
		ArrayList listingArray = null;
		int columns = 1;
		Object columnObj = null;
		String MODULE_NAME = (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME);
		Class[] subClass = BeanDefUtil.getSubClass(_defClass);
		
		columnObj = BeanDefUtil.getField(_defClass, "columns");	
		if(columnObj!=null){
			columns = Integer.parseInt(columnObj.toString());
		}
		searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
		listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			
		StringBuffer buffer = new StringBuffer();
		StringBuffer scriptBuffer = new StringBuffer();
		scriptBuffer.append ("<script>");
//		scriptBuffer.append ("var _companyid = '").append (_shopInfoBean.getShopName()).append ("';\n");
//		scriptBuffer.append ("var productMainjson = ").append (buildProductJson(null)).append (";\n");
//		logger.debug("scriptBuffer = "+scriptBuffer);
		
//		scriptBuffer.append ("var productOptionjson = ").append (buildProductOptionJson()).append (";\n");
//		scriptBuffer.append ("var productPricejson;");//.append (buildProductPriceJson()).append (";\n");
//		scriptBuffer.append ("var productOptionPriceJson = ").append (buildProductOptionPriceJson(null)).append (";\n");
		
		String customJsPath = ResourceUtil.getCompanyAdminPath(_shopInfoBean) + "js/custom.js";
		logger.debug("customJsPath = "+customJsPath);
		scriptBuffer.append(FileHandler.readFile(null, customJsPath));
		scriptBuffer.append ("</script>");
		
		
		buffer.append("<p:useTemplate>simplplan-template.htm</p:useTemplate>").append("\n");
		buffer.append("<p:component value=\"titlebar\">");
		buffer.append("%i18n.system.").append(MODULE_NAME).append(".title% - ");
		buffer.append(_shopInfoBean.getShopNameDesc());
		buffer.append("</p:component>").append("\n");
		buffer.append("<p:component value=\"body\">").append("\n");	

		buffer.append("<div id='").append(
				MODULE_NAME).append("Module' class='box_content'>").append("\n");
				
		buffer.append ("<form id='").append (GeneralConst.LISTING_FORM).append ("' method='POST'>");
			buffer.append(buildListing(listingArray, MODULE_NAME, true, getControlOptionNoDelete())).append("\n");	
		buffer.append ("</form>");
		
		// Add Form		    
		buffer.append("<div id='").append(
				MODULE_NAME).append("ModuleEntry' class='entryScreen' style='width: 1020px; height:100%;display: none;'>").append("\n");
				
		buffer.append ("<form id='").append (GeneralConst.ENTRY_FORM).append ("' method='POST'>");	
			buffer.append(buildEntryForm(MODULE_NAME, addArray, columns)).append("\n");	
			// Preload product option
			

//			buffer.append("<ul id='preload' style='display:none'>").append(getProductOption()).append("</ul>");
			if(subClass!=null){
				// Ignore id_transactionDetailGroupDef
				for(int i=0; i<1; i++){					
					buffer.append (buildDetailEntryForm (subClass[i],"detail-listingTable", getControlOptionGroup())).append("<br>");
				}
			}	    		    		  
			buffer.append ("</form>");	
			
		buffer.append("</div>").append("\n");		
		buffer.append(scriptBuffer);		
		buffer.append("</div>").append("\n");
		buffer.append("</p:component>").append("\n");
		
		// Read Additional Content From File
		
		StringBuffer fileContent = FileHandler.readAdminFile (_shopInfoBean, _clientBean, "id_transaction.htm");						
		buffer.append(fileContent);
		return buffer;
	}	
	
	public JasperPrint buildReport()throws Exception{		
		if(GeneralConst.REPORT.equals(_reqAction)){
			return buildTransactionReport(GeneralConst.REPORT);
		}else if(GeneralConst.REPORT_INV.equals(_reqAction)){
			return buildTransactionReport(GeneralConst.REPORT_INV);
		}else if(GeneralConst.REPORT_DO.equals(_reqAction)){
			return buildTransactionReport(GeneralConst.REPORT_DO);
		}else{
			return new MFG_JobSheetAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, MFG_JobSheetDef.class, _reqAction, "Y").buildReport();
		}		
	}
	
	public JasperPrint buildTransactionReport(String reportType)throws Exception{
		JasperPrint jprint = null;
		LinkedHashMap paramMap = new LinkedHashMap();
		HashMap mainMap = null;
		ArrayList detailList = null;
		StringBuffer queryMain = new StringBuffer();
		StringBuffer queryDetail = new StringBuffer();
		String transID = _req.getParameter(ID_TransactionDef.transid.name);
		queryMain.append("Select * From ").append(ID_TransactionDef.TABLE);
		queryMain.append(" Where ").append(ID_TransactionDef.transid).append("=?");
		
		paramMap.put(ID_TransactionDef.transid, transID);
		mainMap = _gs.searchDataMap(queryMain, paramMap);
		
		// Query Detail 
		queryDetail.append("Select a.*");
		queryDetail.append(", b.").append(ID_TransactionDetailGroupDef.grpdesc);
		queryDetail.append(" From ").append(ID_TransactionDetailDef.TABLE).append(" a");
		queryDetail.append(" Inner Join ").append(ID_TransactionDetailGroupDef.TABLE).append(" b On b.").append(ID_TransactionDetailGroupDef.grpcode);
		queryDetail.append(" = a.").append(ID_TransactionDetailDef.grpcode);
		queryDetail.append(" And b.").append(ID_TransactionDetailGroupDef.transid).append(" = a.").append(ID_TransactionDetailDef.transid);
		queryDetail.append(" Where a.").append(ID_TransactionDetailDef.transid).append("=?");
		
		detailList = _gs.searchDataArray(queryDetail, paramMap);
		
		ReportUtil rptUtil = new ReportUtil();
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String logoFile = reportDir + "logo.jpg";
		String reportFilename = reportDir + "Quotation.jasper";
		mainMap.put("salesby", _clientBean.getLoginUserName());
		mainMap.put("title", "QUOTATION");
		mainMap.put("LOGO", logoFile);
		jprint = rptUtil.getReportPrint(reportFilename, detailList, mainMap);
		
		return jprint;
	}

}
