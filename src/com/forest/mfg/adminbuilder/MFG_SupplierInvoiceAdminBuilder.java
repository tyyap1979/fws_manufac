package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.forest.common.util.DBUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.FileUtil;
import com.forest.common.util.ReportUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.cron.AccountingPatch;
import com.forest.mfg.beandef.MFG_CreditNoteDef;
import com.forest.mfg.beandef.MFG_DebitNoteDef;
import com.forest.mfg.beandef.MFG_PaymentVoucherDetailDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_SupplierInvoiceDef;
import com.forest.mfg.beandef.MFG_TransactionDef;

public class MFG_SupplierInvoiceAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_SupplierInvoiceAdminBuilder.class);
	public MFG_SupplierInvoiceAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
	}
	
	public JSONObject requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		
		if(GeneralConst.DELETE.equals (_reqAction)){
			if(isAllOpen(_req.getParameterValues(MFG_SupplierInvoiceDef.invid.name))){
				json = super.requestJsonHandler();
			}else{
				json.put("rc", "10");
				json.put("rd", "Cannot delete paid invoice.");
			}
		}else{
			json = super.requestJsonHandler();
		}
		return json;
	}		
	
	private boolean isAllOpen(String[] invidArray){
		boolean isAllOpen = true;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer query = new StringBuffer();
		try {
			query.append("Select ").append(MFG_SupplierInvoiceDef.invid);
			query.append(" From ").append(MFG_SupplierInvoiceDef.TABLE);
			query.append(" Where ").append(MFG_SupplierInvoiceDef.invid);
			query.append(" In (").append(DBUtil.arrayToString(invidArray, MFG_SupplierInvoiceDef.invid)).append(")");
			query.append(" And ").append(MFG_SupplierInvoiceDef.status).append("='").append(MFG_SelectBuilder.PAY_STATUS_PAID).append("'");
			logger.debug("query: "+query);
			pstmt = _dbConnection.prepareStatement(query.toString());
			rs = pstmt.executeQuery();
			if(rs.next()){
				isAllOpen = false;
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return isAllOpen;
		
	}
}
