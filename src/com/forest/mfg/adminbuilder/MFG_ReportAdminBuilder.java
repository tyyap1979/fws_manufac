package com.forest.mfg.adminbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.SupplierProfileDef;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.NumberUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.mfg.beandef.MFG_CreditNoteDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_DebitNoteDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDetailDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_StatementDetailDef;
import com.forest.mfg.beandef.MFG_SupplierInvoiceDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;
import com.forest.mfg.services.AdminSQLServices;
// Some Change
public class MFG_ReportAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_ReportAdminBuilder.class);
	public MFG_ReportAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);		
	}
	
	public StringBuffer displayHandler()throws Exception{
		StringBuffer buffer = new StringBuffer();
		
		if(_defClass!=null){
			String MODULE_NAME = "mfg_report";

			buffer.append("<p:useTemplate>simplplan-template-report.htm</p:useTemplate>").append("\n");
			buffer.append("<p:component value=\"titlebar\">");
			buffer.append("%i18n.system.").append(MODULE_NAME).append(".title% - ");
			buffer.append(_shopInfoBean.getShopNameDesc());
			buffer.append("</p:component>").append("\n");
			buffer.append("<p:component value=\"body\">").append("\n");	
				buffer.append(FileHandler.readAdminFile (_shopInfoBean, _clientBean));	
			buffer.append("</p:component>").append("\n");			
		}
		
		return buffer;
	}
	public StringBuffer buildWebReport()throws Exception{
		String rptcode = _req.getParameter("rptcode");
		StringBuffer buffer = null;
		if("sales".equals(rptcode)){
			buffer = rptSales();
		}else if("aging".equals(rptcode)){
			buffer = rptAging();
		}else if("topsalesproduct".equals(rptcode)){
			buffer = rptTopSalesProduct();
		}else if("topsalescustomer".equals(rptcode)){
			buffer = rptTopSalesCustomer();
		}else if("receivepymt".equals(rptcode)){
			buffer = rptReceivePayment();
		}else if("deliverydate".equals(rptcode)){
			buffer = rptDeliveryDate();
		}else if("salesrecord".equals(rptcode)){
			buffer = rptSalesRecord();
		}else if("stmtrpt".equals(rptcode)){
			buffer = rptStatement(MFG_SelectBuilder.ACTIVE);
		}else if("stmtrpt_bd".equals(rptcode)){
			buffer = rptStatement(MFG_SelectBuilder.BAD_DEBT);
		}else if("megatrendprofit".equals(rptcode)){
			buffer = rptProfit();
		}else if("supplierinvoice".equals(rptcode)){
			buffer = rptSupplierInvoice();
		}
		
		return buffer;
	}
	
	private StringBuffer rptSupplierInvoice()throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");		
		String supplierId = _req.getParameter("supp_id");
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		String title = "";
		String subtitle = "";
		
		query.append("Select ");		
		query.append(" a.").append(MFG_SupplierInvoiceDef.invno);
		query.append(", a.").append(MFG_SupplierInvoiceDef.invdate);
		query.append(", a.").append(MFG_SupplierInvoiceDef.note);
		query.append(", a.").append(MFG_SupplierInvoiceDef.invamt);
		query.append(", a.").append(MFG_SupplierInvoiceDef.invamt).append(" - COALESCE(a.").append(MFG_SupplierInvoiceDef.gst_amt).append(", 0) As amtb4gst");
		query.append(", a.").append(MFG_SupplierInvoiceDef.invamt).append(" - COALESCE(a.").append(MFG_SupplierInvoiceDef.sst_amt).append(", 0) As amtb4sst");
//		query.append(", COALESCE(a.").append(MFG_SupplierInvoiceDef.gst_code).append(", '') as ").append(MFG_SupplierInvoiceDef.gst_code);
		query.append(", a.").append(MFG_SupplierInvoiceDef.gst_amt);
		query.append(", b.").append(SupplierProfileDef.name);		
		query.append(", COALESCE(b.").append(SupplierProfileDef.gstid).append(", '') as ").append(SupplierProfileDef.gstid);
		query.append(", COALESCE(b.").append(SupplierProfileDef.sstid).append(", '') as ").append(SupplierProfileDef.sstid);
		query.append(" From ").append(MFG_SupplierInvoiceDef.TABLE).append(" a");
		query.append(" Inner Join ").append(SupplierProfileDef.TABLE).append(" b");
		query.append(" on b.").append(SupplierProfileDef.id).append("=a.").append(MFG_SupplierInvoiceDef.supp_id);
		
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		
		title = "Supplier Invoice for "+_shopInfoBean.getShopNameDesc();
		subtitle = "Invoice Date ";
		
		if(!CommonUtil.isEmpty(supplierId)){
			query.append(" And a.").append(MFG_SupplierInvoiceDef.supp_id).append("=").append(supplierId);
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_SupplierInvoiceDef.invdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else {
			if(!CommonUtil.isEmpty(fromDate)){
				query.append(" And a.").append(MFG_SupplierInvoiceDef.invdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){
				query.append(" And a.").append(MFG_SupplierInvoiceDef.invdate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}		
		query.append(" Order By a.").append(MFG_SupplierInvoiceDef.invdate).append(" desc");
				
		logger.debug(" rptcode = "+rptcode+ ", query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		boolean isGST = false;
		if(!CommonUtil.isEmpty(fromDate)) {
		    java.util.Date salesDate = null;
		    salesDate = DateUtil.getDate(fromDate);
    		if(salesDate.before(DateUtil.getDate("2018-09-01"))){
                isGST = true;
            }
		}
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = null; 
		
		colMap = new HashMap(); 
		colMap.put("caption", "Supplier Name");
		colMap.put("name", SupplierProfileDef.name.name);
		colMap.put("style", "width='250'");
		colArray.add(colMap);
		
		colMap = new HashMap();
		if(isGST) {    		 
    		colMap.put("caption", "GST ID");
    		colMap.put("name", SupplierProfileDef.gstid.name);
		}else {
            colMap.put("caption", "SST ID");
            colMap.put("name", SupplierProfileDef.sstid.name);            
		}
		colMap.put("style", "width='80'");
        colArray.add(colMap);
        
		colMap = new HashMap(); 
		colMap.put("caption", "Invoice No.");
		colMap.put("name", MFG_SupplierInvoiceDef.invno.name);
		colMap.put("style", "width='80'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Invoice Date");
		colMap.put("name", MFG_SupplierInvoiceDef.invdate.name);
		colMap.put("style", "width='60'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Note");
		colMap.put("name", MFG_SupplierInvoiceDef.note.name);
		colMap.put("style", "");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		if(isGST) {
		    colMap.put("caption", "Amount Before GST");
		    colMap.put("name", "amtb4gst");
		} else {
		    colMap.put("caption", "Amount Before SST"); 
		    colMap.put("name", "amtb4sst");
		}		
		colMap.put("style", "width='60'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		if(isGST) {
    		colMap.put("caption", "GST Amount");
    		colMap.put("name", MFG_SupplierInvoiceDef.gst_amt.name);
		} else {
		    colMap.put("caption", "SST Amount");
	        colMap.put("name", MFG_SupplierInvoiceDef.sst_amt.name);
		}
		colMap.put("style", "width='60'");
		colMap.put("format", "currency");
		colArray.add(colMap);
				
		colMap = new HashMap(); 
		colMap.put("caption", "Invoice Amount");
		colMap.put("name", MFG_SupplierInvoiceDef.invamt.name);
		colMap.put("style", "width='60'");
		colMap.put("format", "currency");
		colArray.add(colMap);		
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}
	
	private StringBuffer rptSales()throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		String customerId = _req.getParameter("customerid");
		String salesBy = _req.getParameter("salesby");
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		Map gstCodeMap = SelectBuilder.getHASH_GST(_dbConnection, _shopInfoBean);
		Iterator gstIt = gstCodeMap.keySet().iterator();
		String gstCode = null;
		
		String title = "";
		String subtitle = "";
		
		query.append("Select x.*, x.").append(MFG_TransactionDetailDef.sellsubtotal);
		while(gstIt.hasNext()){
			gstCode = (String) gstIt.next();
			if(!CommonUtil.isEmpty(gstCode)){
				query.append("-").append(MFG_TransactionDef.gst_amt).append("_").append(gstCode);		
			}
		}
		query.append(" As subtotal From (");
		query.append("Select");
		query.append(" a.").append(MFG_TransactionDef.transno).append(",");
		query.append(" a.").append(MFG_TransactionDef.custrefno).append(",");
		query.append(" a.").append(MFG_TransactionDef.salesdate).append(",");
		query.append(" a.").append(MFG_TransactionDef.salesby).append(",");
		query.append(" a.").append(MFG_TransactionDef.status);
		
		gstIt = gstCodeMap.keySet().iterator();
		while(gstIt.hasNext()){
			gstCode = (String) gstIt.next();
			if(!CommonUtil.isEmpty(gstCode)){
				query.append(", Case When");
				query.append(" a.").append(MFG_TransactionDef.gst_code).append(" = '").append(gstCode).append("'");
				query.append(" or cn.").append(MFG_CreditNoteDef.gst_code).append(" = '").append(gstCode).append("'");
				query.append(" or dn.").append(MFG_DebitNoteDef.gst_code).append(" = '").append(gstCode).append("'");
				query.append(" Then ");
				query.append(" COALESCE(a.").append(MFG_TransactionDef.gst_amt).append(", 0)");
				query.append(" - COALESCE(cn.").append(MFG_CreditNoteDef.gst_amt).append(", 0)");
				query.append(" + COALESCE(dn.").append(MFG_DebitNoteDef.gst_amt).append(", 0)");
				query.append(" Else 0 End ");
				query.append(" As ").append(MFG_TransactionDef.gst_amt).append("_").append(gstCode);
			}
		}
				
		query.append(", c.").append(CustomerProfileDef.name).append(",");
		query.append(" (case a.status when '0' then 0 when '4' then 0 else");
		query.append(" stmt.").append(MFG_StatementDetailDef.amount);
		query.append(" - COALESCE(stmt.").append(MFG_StatementDetailDef.creditamount).append(", 0)");
		query.append(" + COALESCE(stmt.").append(MFG_StatementDetailDef.debitamount).append(", 0)");
//		query.append(")");				
		query.append(" end) As ").append(MFG_TransactionDetailDef.sellsubtotal);
//		query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(") As ").append(MFG_TransactionDetailDef.sellsubtotal);
		
		query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_StatementDetailDef.TABLE).append(" stmt");		
		query.append(" on stmt.").append(MFG_StatementDetailDef.transid).append(" = a.").append(MFG_TransactionDef.transid);
		
		query.append(" Left Join ").append(MFG_CreditNoteDef.TABLE).append(" cn");		
		query.append(" on cn.").append(MFG_CreditNoteDef.transid).append(" = a.").append(MFG_TransactionDef.transid);
		
		query.append(" Left Join ").append(MFG_DebitNoteDef.TABLE).append(" dn");		
		query.append(" on dn.").append(MFG_DebitNoteDef.transid).append(" = a.").append(MFG_TransactionDef.transid);
		
//		query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
//		query.append(" on b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
		query.append(" Left Join ").append(CustomerProfileDef.TABLE).append(" c");
		query.append(" on c.").append(CustomerProfileDef.customerid).append("=a.").append(MFG_TransactionDef.customerid);
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And (a.").append(MFG_TransactionDef.status).append(">=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");
		query.append(" Or a.").append(MFG_TransactionDef.status).append("='").append(MFG_SelectBuilder.TRANS_CANCEL).append("')");
//		query.append(" And b.").append(MFG_TransactionDetailDef.status).append("!='").append(GeneralConst.DELETED).append("'");
		title = "Sales Report For ";
		if(_shopInfoBean.getShopName().equals("eurotrend")){
			if(toDate!=null && DateUtil.getDate(toDate).after(DateUtil.getDate("2013-12-31"))){			
				title += _shopInfoBean.getShopNameDesc();
			}else{
				title += "EURO TREND";
			}
		}else{
			title += _shopInfoBean.getShopNameDesc();
		}
		
		subtitle = "Sales Date ";
		if(!CommonUtil.isEmpty(customerId)){
			query.append(" And a.").append(MFG_TransactionDef.customerid).append("=").append(customerId);
		}
		if(!CommonUtil.isEmpty(salesBy)){
			title += " (" + _req.getParameter("salesby_value") + ")";
			query.append(" And a.").append(MFG_TransactionDef.salesby).append("=").append(salesBy);
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else {
			if(!CommonUtil.isEmpty(fromDate)){
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}
//		query.append(" Group By a.").append(MFG_TransactionDef.transno);
		query.append(" Order By a.").append(MFG_TransactionDef.salesdate).append(",").append(MFG_TransactionDef.transno);
		query.append(" ) x");
		
		logger.debug(" rptcode = "+rptcode+ ", query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
		
		HashMap transStatusMap = (HashMap) MFG_SelectBuilder.getHASH_TRANS_STATUS(_dbConnection, _shopInfoBean);
		HashMap rowMap = null;
		int l = arrayListSearch.size();
		String status = null;
		for(int i=0; i<l; i++){
			rowMap = (HashMap) arrayListSearch.get(i);
			status = (String) rowMap.get(MFG_TransactionDef.status.name);
			
			status = (String) transStatusMap.get(status);
			rowMap.put(MFG_TransactionDef.status.name, status);
		}
		
		colMap.put("caption", "Trans. No");
		colMap.put("name", MFG_TransactionDef.transno.name);
		colMap.put("style", "width='60'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Customer Name");
		colMap.put("name", CustomerProfileDef.name.name);
		colMap.put("style", "");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Cust Ref No");
		colMap.put("name", MFG_TransactionDef.custrefno.name);
		colMap.put("style", "width='100'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Status");
		colMap.put("name", MFG_TransactionDef.status.name);
		colMap.put("style", "width='70'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Sales Date");
		colMap.put("name", MFG_TransactionDef.salesdate.name);
		colMap.put("style", "width='80'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Sub-Total");
		colMap.put("name", "subtotal");
		colMap.put("style", "width='80'");
		colMap.put("format", "currency");
		colArray.add(colMap);		
		
		gstIt = gstCodeMap.keySet().iterator();
		while(gstIt.hasNext()){
			gstCode = (String) gstIt.next();
			if(!CommonUtil.isEmpty(gstCode)){
				colMap = new HashMap(); 
				colMap.put("caption", "GST "+gstCode);
				colMap.put("name", MFG_TransactionDef.gst_amt.name + "_" +gstCode);
				colMap.put("format", "currency");
				colMap.put("style", "width='80'");
				colArray.add(colMap);
			}
		}				
		
		colMap = new HashMap(); 
		colMap.put("caption", "Amount");
		colMap.put("name", MFG_TransactionDetailDef.sellsubtotal.name);
		colMap.put("style", "width='80'");
		colMap.put("format", "currency");
		colArray.add(colMap);		
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}	
	
	private StringBuffer rptProfit()throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();	
		
		String title = "";
		String subtitle = "";
		 
		title = "Profit Report For "+_shopInfoBean.getShopNameDesc();
		subtitle = "Sales Date ";				
		
		query.append("select"); 
		query.append(" a.transno, a.salesdate, sum(b.sellsubtotal)-COALESCE(a.discount, 0) As MegaSubtotal,"); 
		query.append(" COALESCE(euro.transno, '') As Eurotransno, euro.sellsubtotal As EuroSubtotal,");
		query.append(" COALESCE(ls.transno,'') As Lstransno , ls.sellsubtotal As LsSubtotal, ");
		query.append(" sum(b.sellsubtotal) - COALESCE(euro.sellsubtotal,0) - COALESCE(ls.sellsubtotal,0) As Profit ");
		query.append(" From mfg_transaction a"); 
		query.append(" Inner Join mfg_transaction_detail b on b.transid = a.transid and b.status = 'A'");
		query.append(" Left Join (");
			query.append(" select x.transno, x.custrefno, sum(y.sellsubtotal)-COALESCE(x.discount, 0) As sellsubtotal");
			query.append(" From mfg_transaction x"); 
			query.append(" Inner Join mfg_transaction_detail y on y.transid = x.transid and y.status = 'A'");
			query.append(" Where x.companyid = 'eurotrend' and x.status = '3'");
			if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
				query.append(" And x.").append(MFG_TransactionDef.salesdate).append(" Between ");
				query.append("'").append(fromDate).append("' And '").append(toDate).append("'");								
			}else {
				if(!CommonUtil.isEmpty(fromDate)){
					query.append(" And x.").append(MFG_TransactionDef.salesdate).append(" >= ");
					query.append("'").append(fromDate).append("'");
				}
				if(!CommonUtil.isEmpty(toDate)){
					query.append(" And x.").append(MFG_TransactionDef.salesdate).append(" <= ");
					query.append("'").append(toDate).append("'");					
				}
			}
			query.append(" group by x.transno");
		query.append(" ) euro On euro.custrefno = a.transno");
		query.append(" Left Join (");
			query.append(" select x.transno, x.custrefno, sum(y.sellsubtotal)-COALESCE(x.discount, 0) As sellsubtotal");
			query.append(" From mfg_transaction x"); 
			query.append(" Inner Join mfg_transaction_detail y on y.transid = x.transid and y.status = 'A'");
			query.append(" Where x.companyid = 'ls' and x.status = '3'");
			if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
				query.append(" And x.").append(MFG_TransactionDef.salesdate).append(" Between ");
				query.append("'").append(fromDate).append("' And '").append(toDate).append("'");								
			}else {
				if(!CommonUtil.isEmpty(fromDate)){
					query.append(" And x.").append(MFG_TransactionDef.salesdate).append(" >= ");
					query.append("'").append(fromDate).append("'");
				}
				if(!CommonUtil.isEmpty(toDate)){
					query.append(" And x.").append(MFG_TransactionDef.salesdate).append(" <= ");
					query.append("'").append(toDate).append("'");					
				}
			}
			query.append(" group by x.transno");
		query.append(" ) ls On ls.custrefno = a.transno");
		query.append(" Where a.companyid = 'megatrend' and a.status = '3'");
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else {
			if(!CommonUtil.isEmpty(fromDate)){
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}
//		query.append(" and (euro.transno is not null or ls.transno is not null)");
		query.append(" group by a.transno");
		query.append(" Order By a.").append(MFG_TransactionDef.transno);
		
//		query.append("Select a.transno, b.transno As eurotransno, a.salesdate, a.subtotal As MegaSubtotal");
//		query.append(" , b.subtotal As EuroSubtotal, (a.subtotal - b.subtotal) as Profit");
//		query.append(" From (");
//		query.append(" select a.transno, a.salesdate, Sum(b.sellsubtotal) As Subtotal");  
//		query.append(" from mfg_transaction a");
//		query.append(" Inner Join mfg_transaction_detail b on b.transid = a.transid");
//		query.append(" Inner Join mfg_supplierproduct c On c.prodid = b.prodid");
//		query.append(" Where a.companyid = 'megatrend' and a.status = '3' and b.status = 'A'");
		
//		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
//			query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" Between ");
//			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
//			
//			subtitle += " From "+fromDate+" To "+toDate;
//		}else {
//			if(!CommonUtil.isEmpty(fromDate)){
//				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" >= ");
//				query.append("'").append(fromDate).append("'");
//				subtitle += " From "+fromDate;
//			}
//			if(!CommonUtil.isEmpty(toDate)){
//				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" <= ");
//				query.append("'").append(toDate).append("'");
//				subtitle += " To "+toDate;
//			}
//		}
//		
//		query.append(" group by a.transno, a.salesdate");
//		query.append(" ) a");
//		query.append(" Inner Join (");
//		query.append(" select a.transno, a.custrefno, Sum(b.sellsubtotal) As Subtotal");  
//		query.append(" from mfg_transaction a");
//		query.append(" Inner Join mfg_transaction_detail b on b.transid = a.transid");
//		query.append(" Where a.companyid = 'eurotrend' and a.status = '3' and b.status = 'A' and b.prodid!=0");
//		query.append(" group by a.transno");
//		query.append(" ) b on b.custrefno = a.transno");
//
//		query.append(" Order By a.").append(MFG_TransactionDef.transno);
				
		logger.debug(" rptcode = "+rptcode+ ", query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
		
		colMap = new HashMap(); 
		colMap.put("caption", "Sales Date");
		colMap.put("name", MFG_TransactionDef.salesdate.name);
		colMap.put("style", "width='100'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "MEGA Invoice No");
		colMap.put("name", MFG_TransactionDef.transno.name);
		colMap.put("style", "width='120'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "MEGA SubTotal");
		colMap.put("name", "MegaSubtotal");
		colMap.put("style", "width='150'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "EURO Invoice No");
		colMap.put("name", "Eurotransno");
		colMap.put("style", "width='120'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "EURO SubTotal");
		colMap.put("name", "EuroSubtotal");
		colMap.put("style", "width='150'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "LS Invocie No");
		colMap.put("name", "Lstransno");
		colMap.put("style", "width='120'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "LS SubTotal");
		colMap.put("name", "LsSubtotal");
		colMap.put("style", "width='150'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Profit");
		colMap.put("name", "Profit");
		colMap.put("style", "");
		colMap.put("format", "currency");
		colArray.add(colMap);		
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}	
	
	private StringBuffer rptAging()throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		String salesby = _req.getParameter(CustomerProfileDef.salesby.name);
		String customerId = _req.getParameter("customerid");
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		
		String title = "A/R Aging Summary For "+_shopInfoBean.getShopNameDesc();
		String subtitle = "As of ";		
		
		query.append("Select i.name,");
		if(!CommonUtil.isEmpty(fromDate) || !CommonUtil.isEmpty(toDate)){
			query.append(" Sum(b.amount-COALESCE(b.payamount,0)-COALESCE(b.creditamount,0)+COALESCE(b.debitamount,0)) as totalamt,");
			query.append(" Sum(d.amount-COALESCE(d.payamount,0)-COALESCE(d.creditamount,0)+COALESCE(d.debitamount,0)) as currentamt,");
			query.append(" Sum(e.amount-COALESCE(e.payamount,0)-COALESCE(e.creditamount,0)+COALESCE(e.debitamount,0)) as past1dayamt,");
			query.append(" Sum(f.amount-COALESCE(f.payamount,0)-COALESCE(f.creditamount,0)+COALESCE(f.debitamount,0)) as past30dayamt,");
			query.append(" Sum(g.amount-COALESCE(g.payamount,0)-COALESCE(g.creditamount,0)+COALESCE(g.debitamount,0)) as past60dayamt,");
			query.append(" Sum(h.amount-COALESCE(h.payamount,0)-COALESCE(h.creditamount,0)+COALESCE(h.debitamount,0)) as past90dayamt");
		}else{
			query.append(" totalamt, currentamt, past1dayamt, past30dayamt, past60dayamt, past90dayamt");			
		}
		query.append(" From mfg_statement a");
		query.append(" Left Join customerprofile i on i.customerid=a.customerid");
		
		if(!CommonUtil.isEmpty(fromDate) || !CommonUtil.isEmpty(toDate)){						
			query.append(" Left Join (");
			query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount");
			query.append(" From mfg_statement_detail a");
			query.append(" ) b On b.stmtid = a.stmtid");
			
			query.append(" Left Join (");
			query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
			query.append(" From mfg_statement_detail a");
			query.append(" Where DATEDIFF(now(), a.duedate) < 1");
			query.append(" ) d On d.stmtdtlid = b.stmtdtlid");
			
			query.append(" Left Join (");
			query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
			query.append(" From mfg_statement_detail a");	
			query.append(" Where DATEDIFF(now(), a.duedate) Between 1 And 30");
			query.append(" ) e On e.stmtdtlid = b.stmtdtlid");
			
			query.append(" Left Join (");
			query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
			query.append(" From mfg_statement_detail a");	
			query.append(" Where DATEDIFF(now(), a.duedate) Between 31 And 60");
			query.append(" ) f On f.stmtdtlid = b.stmtdtlid");
			
			query.append(" Left Join (");
			query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
			query.append(" From mfg_statement_detail a");
			query.append(" Where DATEDIFF(now(), a.duedate) Between 61 And 90");
			query.append(" ) g On g.stmtdtlid = b.stmtdtlid");
			
			query.append(" Left Join ("); 
			query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
			query.append(" From mfg_statement_detail a");
			query.append(" Where DATEDIFF(now(), a.duedate) > 90");
			query.append(" ) h On h.stmtdtlid = b.stmtdtlid");
		}
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.").append(MFG_StatementDef.status).append("='").append(MFG_SelectBuilder.ACTIVE).append("'");
		
		if(!CommonUtil.isEmpty(customerId)){
			query.append(" And a.").append(MFG_TransactionDef.customerid).append("=").append(customerId);
		}
		if(!CommonUtil.isEmpty(salesby)){
			HashMap agentMap = (HashMap)MFG_SelectBuilder.getHASH_AGENT_PROFILE(_dbConnection, _shopInfoBean).get(salesby);
			subtitle += "<br>Sales Person: "+(String) agentMap.get(salesby);
			query.append(" And i.").append(CustomerProfileDef.salesby).append(" = ").append(salesby);
		}
		
		if(CommonUtil.isEmpty(fromDate) && CommonUtil.isEmpty(toDate)){
			query.append(" And a.totalamt>0");
		}
		
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And b.").append(MFG_StatementDetailDef.salesdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else {
			if(!CommonUtil.isEmpty(fromDate)){		
				query.append(" And b.").append(MFG_StatementDetailDef.salesdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){		
				query.append(" And b.").append(MFG_StatementDetailDef.salesdate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}
		
		query.append(" Group By i.").append(CustomerProfileDef.customerid);
		if(!CommonUtil.isEmpty(fromDate) || !CommonUtil.isEmpty(toDate)){
			query.append(" Having Sum(b.amount-COALESCE(b.payamount,0)-COALESCE(b.creditamount,0)+COALESCE(b.debitamount,0)) > 0");
		}
		query.append(" Order By i.").append(CustomerProfileDef.name);
		
		logger.info(" rptcode = "+rptcode+ ",  query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
		colMap.put("caption", "Customer Name");
		colMap.put("name", CustomerProfileDef.name.name);
		colMap.put("style", "");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Current");
		colMap.put("name", "currentamt");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "1 - 30");
		colMap.put("name", "past1dayamt");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "31 - 60");
		colMap.put("name", "past30dayamt");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);	
		
		colMap = new HashMap(); 
		colMap.put("caption", "61 - 90");
		colMap.put("name", "past60dayamt");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "> 90");
		colMap.put("name", "past90dayamt");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Total");
		colMap.put("name", "totalamt");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}
	
	public StringBuffer rptStmtQuery(String customerId, String status){
		Calendar cal = Calendar.getInstance();
		StringBuffer query = new StringBuffer();
		int currentMonth = Integer.parseInt(DateUtil.getCurrentMonth());
		String currentYear = DateUtil.getCurrentYear();
		
		query.append("Select i.name");
		query.append(" ,i.").append(CustomerProfileDef.mobileno);
		query.append(" ,i.").append(CustomerProfileDef.terms);
		query.append(" ,i.").append(CustomerProfileDef.contactperson);
		query.append(", COALESCE(Sum(b.amount-COALESCE(b.payamount,0)-COALESCE(b.creditamount,0)+COALESCE(b.debitamount,0)),0) as totalamt");
		query.append(", COALESCE(Sum(d.amount-COALESCE(d.payamount,0)-COALESCE(d.creditamount,0)+COALESCE(d.debitamount,0)),0) as currentamt");
		query.append(", COALESCE(Sum(e.amount-COALESCE(e.payamount,0)-COALESCE(e.creditamount,0)+COALESCE(e.debitamount,0)),0) as minus1month");
		query.append(", COALESCE(Sum(f.amount-COALESCE(f.payamount,0)-COALESCE(f.creditamount,0)+COALESCE(f.debitamount,0)),0) as minus2month");
		query.append(", COALESCE(Sum(g.amount-COALESCE(g.payamount,0)-COALESCE(g.creditamount,0)+COALESCE(g.debitamount,0)),0) as minus3month");
		query.append(", COALESCE(Sum(h.amount-COALESCE(h.payamount,0)-COALESCE(h.creditamount,0)+COALESCE(h.debitamount,0)),0) as monththerest");
		query.append(", COALESCE(Sum(j.amount-COALESCE(j.payamount,0)-COALESCE(j.creditamount,0)+COALESCE(j.debitamount,0)),0) as dueamt");
		
		query.append(" From mfg_statement a");
		query.append(" Inner Join customerprofile i on i.customerid=a.customerid");
							
		query.append(" Left Join (");
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount");
		query.append(" From mfg_statement_detail a");
		query.append(" ) b On b.stmtid = a.stmtid");
		
		query.append(" Left Join (");
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
		query.append(" From mfg_statement_detail a");
		query.append(" Where MONTH(a.salesdate) = ").append(currentMonth);
		query.append(" And YEAR(a.salesdate) = ").append(currentYear);
		query.append(" ) d On d.stmtdtlid = b.stmtdtlid");
		
		cal.add(Calendar.MONTH, -1);
		query.append(" Left Join (");
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
		query.append(" From mfg_statement_detail a");	
		query.append(" Where MONTH(a.salesdate) = ").append((cal.get(Calendar.MONTH)+1));
		query.append(" And YEAR(a.salesdate) = ").append(cal.get(Calendar.YEAR));
		query.append(" ) e On e.stmtdtlid = b.stmtdtlid");
		
		cal.add(Calendar.MONTH, -1);
		query.append(" Left Join (");
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
		query.append(" From mfg_statement_detail a");	
		query.append(" Where MONTH(a.salesdate) = ").append((cal.get(Calendar.MONTH)+1));
		query.append(" And YEAR(a.salesdate) = ").append(cal.get(Calendar.YEAR));
		query.append(" ) f On f.stmtdtlid = b.stmtdtlid");
		
		cal.add(Calendar.MONTH, -1);
		query.append(" Left Join (");
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
		query.append(" From mfg_statement_detail a");
		query.append(" Where MONTH(a.salesdate) = ").append((cal.get(Calendar.MONTH)+1));
		query.append(" And YEAR(a.salesdate) = ").append(cal.get(Calendar.YEAR));
		query.append(" ) g On g.stmtdtlid = b.stmtdtlid");
		
		query.append(" Left Join ("); 
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
		query.append(" From mfg_statement_detail a");
		query.append(" Where a.salesdate < '").append(cal.get(Calendar.YEAR)).append("-").append((cal.get(Calendar.MONTH)+1)).append("-1'");		
		query.append(" ) h On h.stmtdtlid = b.stmtdtlid");			
		
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		query.append(" Left Join ("); 
		query.append(" Select a.stmtid, a.stmtdtlid, a.salesdate, a.amount, a.payamount, a.creditamount, a.debitamount"); 
		query.append(" From mfg_statement_detail a");
		query.append(" Where a.salesdate < '").append(cal.get(Calendar.YEAR)).append("-").append((cal.get(Calendar.MONTH)+1)).append("-1'");
//		query.append(" Where MONTH(a.salesdate) < ").append((cal.get(Calendar.MONTH)+1));
//		query.append(" And YEAR(a.salesdate) = ").append(cal.get(Calendar.YEAR));
		query.append(" ) j On j.stmtdtlid = b.stmtdtlid");	
		
		
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.").append(MFG_StatementDef.status).append(" In ('").append(status).append("')");
		if(!CommonUtil.isEmpty(customerId)){
			query.append(" And a.").append(MFG_TransactionDef.customerid).append("=").append(customerId);
		}
		
		query.append(" Group By i.").append(CustomerProfileDef.customerid);
		query.append(" Having Sum(b.amount-COALESCE(b.payamount,0)-COALESCE(b.creditamount,0)+COALESCE(b.debitamount,0)) > 0");
		query.append(" Order By i.").append(CustomerProfileDef.name);
		return query;
	}
	private StringBuffer rptStatement(String status)throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");		
//		String toDate = _req.getParameter("todate");		
		String customerId = _req.getParameter("customerid");
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		int currentMonth = Integer.parseInt(DateUtil.getCurrentMonth());		
		
		String title = "Statement Summary For "+_shopInfoBean.getShopNameDesc();
		String subtitle = "As of " + DateUtil.getCurrentDate();		
		
		if(MFG_SelectBuilder.BAD_DEBT.equals(status)){
			title += " (Bad Debt)";
		}
		query = rptStmtQuery(customerId, status);
		
		logger.info(" rptcode = "+rptcode+ ",  query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
		colMap.put("caption", "Customer Name");
		colMap.put("name", CustomerProfileDef.name.name);
		colMap.put("style", "width='300'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Current Month");
		colMap.put("name", "currentamt");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", DateUtil.getMonthInWord(currentMonth-1));
		colMap.put("name", "minus1month");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", DateUtil.getMonthInWord(currentMonth-2));
		colMap.put("name", "minus2month");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);	
		
		colMap = new HashMap(); 
		colMap.put("caption", DateUtil.getMonthInWord(currentMonth-3));
		colMap.put("name", "minus3month");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Over 3 Months");
		colMap.put("name", "monththerest");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Total");
		colMap.put("name", "totalamt");
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");		
		colArray.add(colMap);
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}
	
	
	
	private StringBuffer rptTopSalesProduct()throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		String title = "";
		String subtitle = "";
		
		query.append("Select");		
		query.append(" c.").append(MFG_CustProductDef.name).append(",");		
		query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(") As ").append(MFG_TransactionDetailDef.sellsubtotal);
		
		query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
		query.append(" on b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
		query.append(" Left Join ").append(MFG_CustProductDef.TABLE).append(" c");
		query.append(" on c.").append(MFG_CustProductDef.prodid).append("=b.").append(MFG_TransactionDetailDef.prodid);
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.").append(MFG_TransactionDef.status).append("=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");
//		query.append(" And b.").append(MFG_TransactionDetailDef.status).append("!='").append(GeneralConst.DELETED).append("'");
		
		title = "Top Sales Product Report For "+_shopInfoBean.getShopNameDesc();
		subtitle = "Sales Date ";
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else {
			if(!CommonUtil.isEmpty(fromDate)){
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}
		query.append(" Group By b.").append(MFG_TransactionDetailDef.prodid);
		query.append(" Order By Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(") desc");
				
		logger.debug(" rptcode = "+rptcode+ ", query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
		colMap.put("caption", "Product Name");
		colMap.put("name", MFG_CustProductDef.name.name);
		colMap.put("style", "width='500'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Sales");
		colMap.put("name", MFG_TransactionDetailDef.sellsubtotal.name);
		colMap.put("style", "width='100'");
		colMap.put("format", "currency");
		colArray.add(colMap);		
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}
	
	private StringBuffer rptTopSalesCustomer()throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		String title = "";
		String subtitle = "";
		
		query.append("Select");		
		query.append(" c.").append(CustomerProfileDef.name).append(",");		
		query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(") As ").append(MFG_TransactionDetailDef.sellsubtotal);
		
		query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
		query.append(" on b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
		query.append(" Left Join ").append(CustomerProfileDef.TABLE).append(" c");
		query.append(" on c.").append(CustomerProfileDef.customerid).append("=a.").append(MFG_TransactionDef.customerid);
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.").append(MFG_TransactionDef.status).append("=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");
//		query.append(" And b.").append(MFG_TransactionDetailDef.status).append("!='").append(GeneralConst.DELETED).append("'");
		
		title = "Top Sales Customer Report For "+_shopInfoBean.getShopNameDesc();
		subtitle = "Sales Date ";
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else{
			if(!CommonUtil.isEmpty(fromDate)){		
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){		
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}
		query.append(" Group By a.").append(MFG_TransactionDef.customerid);
		query.append(" Order By Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(") desc");
				
		logger.debug(" rptcode = "+rptcode+ ", query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
		colMap.put("caption", "Customer Name");
		colMap.put("name", CustomerProfileDef.name.name);
		colMap.put("style", "width='500'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Sales");
		colMap.put("name", MFG_TransactionDetailDef.sellsubtotal.name);
		colMap.put("style", "width='100'");
		colMap.put("format", "currency");
		colArray.add(colMap);		
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}
	
	private StringBuffer rptReceivePayment()throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		String customerId = _req.getParameter("customerid");
		String salesBy = _req.getParameter("salesby");
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		String title = "";
		String subtitle = "";
		
		PreparedStatement pstmt = null;
		StringBuffer dtlQuery = new StringBuffer();
		dtlQuery.append("Select");
		dtlQuery.append(" b.").append(MFG_StatementDetailDef.transno).append(",");		
		dtlQuery.append(" a.").append(MFG_ReceivePaymentDetailDef.payamount);
		dtlQuery.append(" From ").append(MFG_ReceivePaymentDetailDef.TABLE).append(" a");
		dtlQuery.append(" Inner Join ").append(MFG_StatementDetailDef.TABLE).append(" b On ");
		dtlQuery.append(" b.").append(MFG_StatementDetailDef.stmtdtlid).append("=a.").append(MFG_ReceivePaymentDetailDef.stmtdtlid);
		dtlQuery.append(" Where a.").append(MFG_ReceivePaymentDetailDef.rpayid).append("=?");
		
		logger.debug("dtlQuery = "+dtlQuery);
		pstmt = _dbConnection.prepareStatement(dtlQuery.toString());
		
		
		query.append("Select");
		query.append(" a.").append(MFG_ReceivePaymentDef.rpayid).append(",");
		query.append(" a.").append(MFG_ReceivePaymentDef.paydate).append(",");
		query.append(" a.").append(MFG_ReceivePaymentDef.payamount).append(",");
		query.append(" a.").append(MFG_ReceivePaymentDef.payrefno).append(",");
		query.append(" a.").append(MFG_ReceivePaymentDef.paynote).append(",");		
		query.append(" c.").append(CustomerProfileDef.name).append("");
		
		query.append(" From ").append(MFG_ReceivePaymentDef.TABLE).append(" a");
		query.append(" Left Join ").append(CustomerProfileDef.TABLE).append(" c");
		query.append(" on c.").append(CustomerProfileDef.customerid).append("=a.").append(MFG_ReceivePaymentDef.customerid);
		
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.").append(MFG_ReceivePaymentDef.status).append("!='").append(GeneralConst.DELETED).append("'");
		
		title = "Receive Payment Report For "+_shopInfoBean.getShopNameDesc();
		subtitle = "Payment Received Date ";
		if(!CommonUtil.isEmpty(customerId)){
			query.append(" And a.").append(MFG_ReceivePaymentDef.customerid).append("=").append(customerId);
		}
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_ReceivePaymentDef.paydate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else {
			if(!CommonUtil.isEmpty(fromDate)){
				query.append(" And a.").append(MFG_ReceivePaymentDef.paydate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){
				query.append(" And a.").append(MFG_ReceivePaymentDef.paydate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}
		
		if(!CommonUtil.isEmpty(salesBy)){
			title += " (" + _req.getParameter("salesby_value") + ")";
			query.append(" And a.").append(MFG_ReceivePaymentDef.rpayid).append(" In (");
			query.append("Select x.");
			query.append(MFG_ReceivePaymentDef.rpayid);
			query.append(" From ").append(MFG_ReceivePaymentDef.TABLE).append(" x");
			
			query.append(" Inner Join ").append(MFG_ReceivePaymentDetailDef.TABLE).append(" y");
			query.append(" on y.").append(MFG_ReceivePaymentDetailDef.rpayid).append("=x.").append(MFG_ReceivePaymentDef.rpayid);
			
			query.append(" Inner Join ").append(MFG_StatementDetailDef.TABLE).append(" y1");
			query.append(" on y1.").append(MFG_StatementDetailDef.stmtdtlid).append("=y.").append(MFG_ReceivePaymentDetailDef.stmtdtlid);
			
			query.append(" Inner Join ").append(MFG_TransactionDef.TABLE).append(" z");
			query.append(" on z.").append(MFG_TransactionDef.transid).append("=y1.").append(MFG_StatementDetailDef.transid);
			
			query.append(" Where z.").append(MFG_TransactionDef.salesby).append("=").append(salesBy);
			query.append(")");
		}
		
//		query.append(" Group By a.").append(MFG_TransactionDef.transno);
		query.append(" Order By a.").append(MFG_ReceivePaymentDef.paydate).append(" desc");
				
		logger.debug(" rptcode = "+rptcode+ ", query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		
		// Insert Invoice No
		ResultSet rs = null;
		HashMap rowData = null;
		int l = arrayListSearch.size();
		StringBuffer invoiceDetail = null;
		for(int i=0; i<l; i++){
			rowData = (HashMap) arrayListSearch.get(i);			
			pstmt.setInt(1, Integer.parseInt((String) rowData.get(MFG_ReceivePaymentDef.rpayid.name)));
			rs = pstmt.executeQuery();
			invoiceDetail = new StringBuffer();
			while(rs.next()){
				invoiceDetail.append(rs.getString(MFG_ReceivePaymentDetailDef.transno.name));
				invoiceDetail.append(" (RM ").append(rs.getString(MFG_ReceivePaymentDetailDef.payamount.name)).append(")");
				invoiceDetail.append(", ");				
			}
			if(invoiceDetail.length()>0) invoiceDetail.deleteCharAt(invoiceDetail.length()-2);
			rowData.put("invoicedetail", invoiceDetail);
		}
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
				
		colMap = new HashMap(); 
		colMap.put("caption", "Customer Name");
		colMap.put("name", CustomerProfileDef.name.name);
		colMap.put("style", "width='130'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Pay Date");
		colMap.put("name", MFG_ReceivePaymentDef.paydate.name);
		colMap.put("style", "width='50'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Amount");
		colMap.put("name", MFG_ReceivePaymentDef.payamount.name);
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Invoice");
		colMap.put("name", "invoicedetail");
		colMap.put("style", "width='350'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Reference");
		colMap.put("name", MFG_ReceivePaymentDef.payrefno.name);
		colMap.put("style", "width='150'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Note");
		colMap.put("name", MFG_ReceivePaymentDef.paynote.name);
		colMap.put("style", "width='200'");		
		colArray.add(colMap);		
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}	
	
	private StringBuffer rptSalesRecord()throws Exception{		
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		String delDate = _req.getParameter("deldate");
		String customerId = _req.getParameter("customerid");
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		toDate = (CommonUtil.isEmpty(toDate))?DateUtil.getCurrentDate():toDate;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer dtlQuery = new StringBuffer();
		
		String title = "";
		String subtitle = "";
		ArrayList companyGroup = new AdminSQLServices(_shopInfoBean, _clientBean, _dbConnection).getCompanyGroup(_shopInfoBean.getShopName());
		HashMap rowData = null;
		String id = null;
		String name = null;
		String masterName = null;
		
		dtlQuery.append("Select");
		dtlQuery.append(" b.").append(MFG_CustProductDef.name).append(",");
		dtlQuery.append(" Sum(a.").append(MFG_TransactionDetailDef.qty).append(") As ").append(MFG_TransactionDetailDef.qty).append(",");
		dtlQuery.append(" c.").append(MFG_ProductOptionDetailDef.name).append(" as optname");
		dtlQuery.append(" From ").append(MFG_TransactionDetailDef.TABLE).append(" a");
		dtlQuery.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b On ");
		dtlQuery.append(" b.").append(MFG_CustProductDef.prodid).append("=a.").append(MFG_TransactionDetailDef.prodid);
		dtlQuery.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" c On ");
		dtlQuery.append(" c.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append("=a.").append(MFG_TransactionDetailDef.opt1);
		
		dtlQuery.append(" Where a.").append(MFG_TransactionDetailDef.transid).append("=?");
		dtlQuery.append(" And a.").append(MFG_TransactionDetailDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		dtlQuery.append(" Group By b.").append(MFG_CustProductDef.name).append(", c.").append(MFG_ProductOptionDetailDef.name);
		dtlQuery.append(" Order By b.").append(MFG_CustProductDef.name);
		pstmt = _dbConnection.prepareStatement(dtlQuery.toString());
		
		
		query.append("Select");
		query.append(" a.").append(MFG_TransactionDef.transid).append(",");
		query.append(" a.").append(MFG_TransactionDef.jobcode).append(",");
		query.append(" c.").append(CustomerProfileDef.name).append(",");		
		query.append(" COALESCE(a.").append(MFG_TransactionDef.deliverydate).append(",'') As ").append(MFG_TransactionDef.deliverydate).append(",");
		query.append(" a.").append(MFG_TransactionDef.comment).append(",");
		query.append(" a.").append(MFG_TransactionDef.transno).append(",");
		query.append(" a.").append(MFG_TransactionDef.salesdate).append(",");
		query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(")-a.").append(MFG_TransactionDef.discount).append(" As ").append(MFG_TransactionDetailDef.sellsubtotal);						
		query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
		query.append(" on b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
		query.append(" And b.").append(MFG_TransactionDetailDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		
		query.append(" Inner Join ").append(CustomerProfileDef.TABLE).append(" c");
		query.append(" on c.").append(CustomerProfileDef.customerid).append("=a.").append(MFG_TransactionDef.customerid);		
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.").append(MFG_TransactionDef.status).append("=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");							
		
		title = "Sales Record Report For "+_shopInfoBean.getShopNameDesc();
		subtitle = "Sales Date: ";

		if(!CommonUtil.isEmpty(customerId)){
			query.append(" And a.").append(MFG_TransactionDef.customerid).append("=").append(customerId);
		}
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else{
			if(!CommonUtil.isEmpty(fromDate)){		
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){		
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}
		
		if(!CommonUtil.isEmpty(delDate)){
			query.append(" And a.").append(MFG_TransactionDef.deliverydate).append(" = ");
			query.append("'").append(delDate).append("'");
			
			if(!CommonUtil.isEmpty(subtitle)){
				subtitle += "<br> Delivery Date: " + delDate;
			}else{
				subtitle = "Delivery Date: " + delDate;
			}
		}
		query.append(" Group By a.").append(MFG_TransactionDef.jobcode).append(",");
		query.append(" a.").append(MFG_TransactionDef.transid).append(",");
		query.append(" c.").append(CustomerProfileDef.name).append(",");
		query.append(" a.").append(MFG_TransactionDef.deliverydate).append(",");
		query.append(" a.").append(MFG_TransactionDef.comment).append(",");
		query.append(" a.").append(MFG_TransactionDef.transno).append("");	
		
		query.append(" Order By a.").append(MFG_TransactionDef.transno);
				
		logger.debug(" rptcode = "+rptcode+ ", query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		// Insert Order Detail
		int l = arrayListSearch.size();
		StringBuffer orderDetail = null;
		for(int i=0; i<l; i++){
			rowData = (HashMap) arrayListSearch.get(i);
			pstmt.setInt(1, Integer.parseInt((String) rowData.get(MFG_TransactionDef.transid.name)));
			rs = pstmt.executeQuery();
			orderDetail = new StringBuffer();
			while(rs.next()){
				orderDetail.append("<b>").append(rs.getString(MFG_CustProductDef.name.name)).append("</b>").append("/");
				orderDetail.append(rs.getString(MFG_TransactionDetailDef.qty.name)).append(" unit");
				if(!CommonUtil.isEmpty(rs.getString("optname"))){
					orderDetail.append("/").append(rs.getString("optname")).append("<br>");
				}else{
					orderDetail.append("<br>");
				}
			}
			rowData.put("orderdetail", orderDetail);
		}
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
		
		colMap = new HashMap(); 
		colMap.put("caption", "Invoice No");
		colMap.put("name", MFG_TransactionDef.transno.name);
		colMap.put("style", "width='50'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Job Code");
		colMap.put("name", MFG_TransactionDef.jobcode.name);
		colMap.put("style", "width='50'");
		
		colArray.add(colMap);
		colMap = new HashMap(); 
		colMap.put("caption", "Customer Name");
		colMap.put("name", CustomerProfileDef.name.name);
		colMap.put("style", "width='100'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Amount");
		colMap.put("name", MFG_TransactionDetailDef.sellsubtotal.name);
		colMap.put("style", "width='50'");
		colMap.put("format", "currency");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Sales Date");
		colMap.put("name", MFG_TransactionDef.salesdate.name);
		colMap.put("style", "width='50'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Delivery Date");
		colMap.put("name", MFG_TransactionDef.deliverydate.name);
		colMap.put("style", "width='50'");
		colArray.add(colMap);

		colMap = new HashMap(); 
		colMap.put("caption", "Order Detail");
		colMap.put("name", "orderdetail");
		colMap.put("style", "width='150'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Delivery Detail");
		colMap.put("name", MFG_TransactionDef.comment.name);
		colMap.put("style", "width='150'");
		colArray.add(colMap);
				
		colMap = new HashMap(); 
		colMap.put("caption", "Remark");
		colMap.put("name", "");
		colMap.put("style", "width='150'");
		colArray.add(colMap);

		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}	
	
	
	private StringBuffer rptDeliveryDate()throws Exception{
		ArrayList arrayListSearch = null;
		String rptcode = _req.getParameter("rptcode");
		String fromDate = _req.getParameter("fromdate");
		String toDate = _req.getParameter("todate");
		String delfromDate = _req.getParameter("delfromdate");
		String deltoDate = _req.getParameter("deltodate");
		String customerId = _req.getParameter("customerid");
		String kddTag = _req.getParameter("kddTag");
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		StringBuffer query = new StringBuffer();
		
		toDate = (CommonUtil.isEmpty(toDate))?DateUtil.getCurrentDate():toDate;
		deltoDate = (!CommonUtil.isEmpty(delfromDate) && CommonUtil.isEmpty(deltoDate))?DateUtil.getCurrentDate():deltoDate;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer dtlQuery = new StringBuffer();
		
		String title = "";
		String subtitle = "";
		ArrayList companyGroup = new AdminSQLServices(_shopInfoBean, _clientBean, _dbConnection).getCompanyGroup(_shopInfoBean.getShopName());
		HashMap rowData = null;
		String id = null;
		String name = null;
		String masterName = null;
		
		dtlQuery.append("Select");
		dtlQuery.append(" b.").append(MFG_CustProductDef.name).append(",");
		dtlQuery.append(" Sum(a.").append(MFG_TransactionDetailDef.qty).append(") As ").append(MFG_TransactionDetailDef.qty).append(",");
		dtlQuery.append(" c.").append(MFG_ProductOptionDetailDef.name).append(" as optname");
		dtlQuery.append(" From ").append(MFG_TransactionDetailDef.TABLE).append(" a");
		dtlQuery.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b On ");
		dtlQuery.append(" b.").append(MFG_CustProductDef.prodid).append("=a.").append(MFG_TransactionDetailDef.prodid);
		dtlQuery.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" c On ");
		dtlQuery.append(" c.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append("=a.").append(MFG_TransactionDetailDef.opt1);
		
		dtlQuery.append(" Where a.").append(MFG_TransactionDetailDef.transid).append("=?");
		dtlQuery.append(" And a.").append(MFG_TransactionDetailDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		
		if(!CommonUtil.isEmpty(kddTag)) {
			dtlQuery.append(" And b.").append(MFG_CustProductDef.description).append(" Like '%").append(kddTag).append("%' ");
		}
		dtlQuery.append(" Group By b.").append(MFG_CustProductDef.name).append(", c.").append(MFG_ProductOptionDetailDef.name);
		dtlQuery.append(" Order By b.").append(MFG_CustProductDef.name);
		pstmt = _dbConnection.prepareStatement(dtlQuery.toString());
		
		
		query.append("Select");
		query.append(" a.").append(MFG_TransactionDef.transid).append(",");
		query.append(" a.").append(MFG_TransactionDef.jobcode).append(",");
		query.append(" c.").append(CustomerProfileDef.name).append(",");
		query.append(" COALESCE(a.").append(MFG_TransactionDef.deliverydate).append(",'') As ").append(MFG_TransactionDef.deliverydate).append(",");
		query.append(" a.").append(MFG_TransactionDef.comment).append(",");
		query.append(" a.").append(MFG_TransactionDef.transno).append(",");
		query.append(" a.").append(MFG_TransactionDef.salesdate).append(",");
		query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(")-a.").append(MFG_TransactionDef.discount).append(" As ").append(MFG_TransactionDetailDef.sellsubtotal);
		
		// If Got Child Company
		if(_shopInfoBean.getShopName().equals(_shopInfoBean.getMasterShopName())){
			for(int i=0; i<companyGroup.size(); i++){
				rowData = (HashMap) companyGroup.get(i);
				id = (String) rowData.get(ShopBeanDef.companyid.name);
				name = (String) rowData.get(ShopBeanDef.shoplongname.name);			
				
				if(!"eurotrend".equals(id) && !"ls".equals(id)) continue;
				
				if(!_shopInfoBean.getShopName().equals(id) && !id.equals(_shopInfoBean.getMasterShopName())){
					query.append(", COALESCE(a").append(i).append(".").append(MFG_TransactionDef.transno).append(",'') as ").append(MFG_TransactionDef.transno).append(i).append(",");
					query.append(" a").append(i).append(".").append(MFG_TransactionDetailDef.sellsubtotal).append(" As ").append(MFG_TransactionDetailDef.sellsubtotal).append(i);
				}				
			}
		}
		if(!_shopInfoBean.getShopName().equals(_shopInfoBean.getMasterShopName())){
			query.append(", COALESCE(m.").append(MFG_TransactionDef.transno).append(",'') as ").append(MFG_TransactionDef.transno).append("m,");
			query.append(" m.").append(MFG_TransactionDetailDef.sellsubtotal).append(" As ").append(MFG_TransactionDetailDef.sellsubtotal).append("m");
		}
		
		query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
		query.append(" on b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
		query.append(" And b.").append(MFG_TransactionDetailDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		
		if(!CommonUtil.isEmpty(kddTag)) {
			query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" prod");
			query.append(" On prod.").append(MFG_CustProductDef.prodid).append("=b.").append(MFG_TransactionDetailDef.prodid);
			query.append(" And prod.").append(MFG_CustProductDef.description).append(" Like '%").append(kddTag).append("%' ");
		}		
		
		query.append(" Inner Join ").append(CustomerProfileDef.TABLE).append(" c");
		query.append(" on c.").append(CustomerProfileDef.customerid).append("=a.").append(MFG_TransactionDef.customerid);		
		
		// If Got Child Company
		if(_shopInfoBean.getShopName().equals(_shopInfoBean.getMasterShopName())){
			for(int i=0; i<companyGroup.size(); i++){
				rowData = (HashMap) companyGroup.get(i);
				id = (String) rowData.get(ShopBeanDef.companyid.name);
				name = (String) rowData.get(ShopBeanDef.shoplongname.name);
				if(!"eurotrend".equals(id) && !"ls".equals(id)) continue;
				
				if(!_shopInfoBean.getShopName().equals(id) && !id.equals(_shopInfoBean.getMasterShopName())){
					query.append(" Left Join"); 
					query.append(" (Select");
					query.append(" a.").append(MFG_TransactionDef.transno).append(",");
					query.append(" a.").append(MFG_TransactionDef.custrefno).append(",");
					query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(")-a.").append(MFG_TransactionDef.discount).append(" As ").append(MFG_TransactionDetailDef.sellsubtotal);
					query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
					query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
					query.append(" on b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
					query.append(" Where");
					query.append(" a.").append(MFG_TransactionDef.status).append(">=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");
//					query.append(" And b.").append(MFG_TransactionDetailDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
					query.append(" And a.").append(MFG_TransactionDef.companyid).append("='").append(id).append("'");
					query.append(" Group By a.").append(MFG_TransactionDef.transno).append(",");
					query.append(" a.").append(MFG_TransactionDef.custrefno).append("");
					query.append(" ) a").append(i).append(" On a").append(i).append(".").append(MFG_TransactionDef.custrefno).append(" = a.");
					query.append(MFG_TransactionDef.transno);					
				}
			}
		}
		
		if(!_shopInfoBean.getShopName().equals(_shopInfoBean.getMasterShopName())){
			// Child Company Want to show order from parents company
			query.append(" Left Join"); 
			query.append(" (Select");
			query.append(" a.").append(MFG_TransactionDef.transno).append(",");
			query.append(" a.").append(MFG_TransactionDef.custrefno).append(",");
			query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(")-a.").append(MFG_TransactionDef.discount).append(" As ").append(MFG_TransactionDetailDef.sellsubtotal);
			query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
			query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
			query.append(" on b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
			query.append(" Where");
			query.append(" a.").append(MFG_TransactionDef.status).append(">=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");
//			query.append(" And b.").append(MFG_TransactionDetailDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" And a.").append(MFG_TransactionDef.companyid).append("='").append(_shopInfoBean.getMasterShopName()).append("'");
			query.append(" Group By a.").append(MFG_TransactionDef.transno).append(",");
			query.append(" a.").append(MFG_TransactionDef.custrefno).append("");
			query.append(" ) m On m.").append(MFG_TransactionDef.transno).append(" = a.");
			query.append(MFG_TransactionDef.custrefno);					
		}
		
		query.append(" Where");
		query.append(" a.").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
		query.append(" And a.").append(MFG_TransactionDef.status).append("=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");							
		
		title = "Delivery Date Report For "+_shopInfoBean.getShopNameDesc();
		subtitle = "Sales Date: ";
		
		if(!CommonUtil.isEmpty(customerId)){
			query.append(" And a.").append(MFG_TransactionDef.customerid).append("=").append(customerId);
		}
		if(!CommonUtil.isEmpty(fromDate) && !CommonUtil.isEmpty(toDate)){
			query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" Between ");
			query.append("'").append(fromDate).append("' And '").append(toDate).append("'");
			
			subtitle += " From "+fromDate+" To "+toDate;
		}else{
			if(!CommonUtil.isEmpty(fromDate)){		
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" >= ");
				query.append("'").append(fromDate).append("'");
				subtitle += " From "+fromDate;
			}
			if(!CommonUtil.isEmpty(toDate)){		
				query.append(" And a.").append(MFG_TransactionDef.salesdate).append(" <= ");
				query.append("'").append(toDate).append("'");
				subtitle += " To "+toDate;
			}
		}
		subtitle += "<br> Delivery Date: ";
		
		if(!CommonUtil.isEmpty(kddTag)) {
			subtitle += "<br> Product Description #Tag: " + kddTag;
		}
		
		if(!CommonUtil.isEmpty(delfromDate) && !CommonUtil.isEmpty(deltoDate)){
			query.append(" And a.").append(MFG_TransactionDef.deliverydate);
			query.append(" Between '").append(delfromDate).append("' And '").append(deltoDate).append("'");
			subtitle += " From "+delfromDate+" To "+deltoDate;
		}else if(!CommonUtil.isEmpty(delfromDate)){
			query.append(" And a.").append(MFG_TransactionDef.deliverydate).append(" = ");
			query.append("'").append(delfromDate).append("'");
			subtitle += "From " + delfromDate;
		}
		
		query.append(" Group By a.").append(MFG_TransactionDef.jobcode).append(",");
		query.append(" a.").append(MFG_TransactionDef.transid).append(",");
		query.append(" c.").append(CustomerProfileDef.name).append(",");
		query.append(" a.").append(MFG_TransactionDef.deliverydate).append(",");
		query.append(" a.").append(MFG_TransactionDef.comment).append(",");
		query.append(" a.").append(MFG_TransactionDef.transno).append("");	
		
		query.append(" Order By a.").append(MFG_TransactionDef.transno);
				
		logger.debug(" rptcode = "+rptcode+ ", query = "+query);
		arrayListSearch = gs.searchDataArray(query);
		
		// Insert Order Detail
		int l = arrayListSearch.size();
		StringBuffer orderDetail = null;
		for(int i=0; i<l; i++){
			rowData = (HashMap) arrayListSearch.get(i);
			pstmt.setInt(1, Integer.parseInt((String) rowData.get(MFG_TransactionDef.transid.name)));
			rs = pstmt.executeQuery();
			orderDetail = new StringBuffer();
			while(rs.next()){
				orderDetail.append("<b>").append(rs.getString(MFG_CustProductDef.name.name)).append("</b>").append("/");
				orderDetail.append(rs.getString(MFG_TransactionDetailDef.qty.name)).append(" unit");
				if(!CommonUtil.isEmpty(rs.getString("optname"))){
					orderDetail.append("/").append(rs.getString("optname")).append("<br>");
				}else{
					orderDetail.append("<br>");
				}
			}
			rowData.put("orderdetail", orderDetail);
		}
		
		ArrayList colArray = new ArrayList();
		HashMap colMap = new HashMap(); 
				
		colMap = new HashMap(); 
		colMap.put("caption", "Job Code");
		colMap.put("name", MFG_TransactionDef.jobcode.name);
		colMap.put("style", "width='80'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Customer Name");
		colMap.put("name", CustomerProfileDef.name.name);
		colMap.put("style", "width='200'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Order Detail");
		colMap.put("name", "orderdetail");
		colMap.put("style", "width='250'");
//		colMap.put("style", "white-space: nowrap");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Delivery Detail");
		colMap.put("name", MFG_TransactionDef.comment.name);
		colMap.put("style", "width='200'");
		colArray.add(colMap);
		
//		if(!CommonUtil.isEmpty(fromDate) && CommonUtil.isEmpty(toDate)){
		colMap = new HashMap(); 
		colMap.put("caption", "Sales Date");
		colMap.put("name", MFG_TransactionDef.salesdate.name);
		colMap.put("style", "width='80'");
		colArray.add(colMap);
//		}
		
		colMap = new HashMap(); 
		colMap.put("caption", "Delivery Date");
		colMap.put("name", MFG_TransactionDef.deliverydate.name);
		colMap.put("style", "width='80'");
		colArray.add(colMap);
		
		colMap = new HashMap(); 
		colMap.put("caption", "Invoice No");
		colMap.put("name", MFG_TransactionDef.transno.name);
		colMap.put("style", "width='80'");
		colArray.add(colMap);
		
		if(!"kdd".equals(_shopInfoBean.getShopName()) && !"megatrend".equals(_shopInfoBean.getShopName())) {
			colMap = new HashMap(); 
			colMap.put("caption", "Amount");
			colMap.put("name", MFG_TransactionDetailDef.sellsubtotal.name);
			colMap.put("style", "width='80'");
			colMap.put("format", "currency");
			colArray.add(colMap);
		}
		// If Got Child Company
		
			for(int i=0; i<companyGroup.size(); i++){
				rowData = (HashMap) companyGroup.get(i);
				id = (String) rowData.get(ShopBeanDef.companyid.name);
				name = (String) rowData.get(ShopBeanDef.shoplongname.name);			
				if(!"eurotrend".equals(id) && !"ls".equals(id)) continue;
				if(_shopInfoBean.getShopName().equals(_shopInfoBean.getMasterShopName())){
					if(!_shopInfoBean.getShopName().equals(id) && !id.equals(_shopInfoBean.getMasterShopName())){
						colMap = new HashMap(); 
						colMap.put("caption", name + " Invoice No");
						colMap.put("name", MFG_TransactionDef.transno.name+i);
						colMap.put("style", "width='80'");
						colArray.add(colMap);
						
						colMap = new HashMap(); 
						colMap.put("caption", name + " Amount");
						colMap.put("name", MFG_TransactionDetailDef.sellsubtotal.name+i);
						colMap.put("style", "width='80'");
						colMap.put("format", "currency");
						colArray.add(colMap);
					}
				}else if(id.equals(_shopInfoBean.getMasterShopName())){
					masterName = name;
				}
			}
		if(!_shopInfoBean.getShopName().equals(_shopInfoBean.getMasterShopName())){
			colMap = new HashMap(); 
			colMap.put("caption", masterName + " Invoice No");
			colMap.put("name", MFG_TransactionDef.transno.name+"m");
			colMap.put("style", "width='80'");
			colArray.add(colMap);
			
			colMap = new HashMap(); 
			colMap.put("caption", masterName + " Amount");
			colMap.put("name", MFG_TransactionDetailDef.sellsubtotal.name+"m");
			colMap.put("style", "width='80'");
			colMap.put("format", "currency");
			colArray.add(colMap);
		}
		
		return buildReport(title, subtitle, colArray, arrayListSearch, 6);
	}	
	
	public StringBuffer buildReport(String title, String subtitle, ArrayList colArray, ArrayList dataList){
		return buildReport(title, subtitle, colArray, dataList, 10);
	}
	
	public StringBuffer buildReport(String title, String subtitle, ArrayList colArray, ArrayList dataList, int fontsize){
		StringBuffer buf = new StringBuffer();
		StringBuffer colBuf = new StringBuffer();		
		StringBuffer dataBuf = new StringBuffer();
		HashMap colMap = null;
		HashMap rowMap = null;
		String[] colName = null;
		String[] colStyle = null;
		String[] colFormat = null;
		StringBuffer buffer = new StringBuffer();
		String[] arrRow = {"class='rowEven'", "class='rowOdd'"};		
		double[] total = null;
		try{
			buf.append("<div>");
			buf.append("<center><span style='font-size: 15pt; font-weight: bolder;'>").append(title).append("</span></center><br>");
			buf.append("<span style='font-size: 13pt'>").append(subtitle).append("</span>");
			
			// For Column Name
			colName = new String[colArray.size()];
			colStyle = new String[colArray.size()];
			colFormat = new String[colArray.size()];
			total = new double[colArray.size()];
			for(int i=0; i<colArray.size(); i++){
				colMap = (HashMap) colArray.get(i);
				colBuf.append("<th ");
				colBuf.append((String) colMap.get("style"));
				colBuf.append(">").append(colMap.get("caption")).append("</th>");
				colName[i] = (String)colMap.get("name");
				colStyle[i] = (String)colMap.get("style");
				colFormat[i] = (String)colMap.get("format");
			}
			
			// For data			
			for(int i=0; i<dataList.size(); i++){
				rowMap = (HashMap) dataList.get(i);				
				dataBuf.append ("<tr ").append (arrRow[(i % 2)]).append (">");
				for(int x=0; x<colName.length; x++){
					dataBuf.append("<td ");
					dataBuf.append(colStyle[x]);
					dataBuf.append(">");
					
					if(!"".equals(colName[x])){
						if("currency".equals(colFormat[x])){
							dataBuf.append(NumberUtil.formatCurrency((String) rowMap.get(colName[x])));
							total[x] += CommonUtil.parseDouble((String) rowMap.get(colName[x]));
						}else{
							dataBuf.append(rowMap.get(colName[x]));
						}
					}
					dataBuf.append("</td>");
				}
				dataBuf.append("</tr>");							
			}
			
			buf.append("<table class='listingTable' style='font-size:").append(fontsize).append("pt'>");
			
			if(!CommonUtil.isEmpty(colBuf.toString())){
				buf.append("<thead>");
				buf.append("<tr class='headings'>").append(colBuf).append("</tr>");
				buf.append("</thead>");
			}
			if(!CommonUtil.isEmpty(dataBuf.toString())){
				buf.append("<tbody>");
				buf.append(dataBuf);
				buf.append("</tbody>");
			}
			if(!CommonUtil.isEmpty(colBuf.toString())){
//				buf.append("<tfoot>");
				buf.append("<tr>");
				for(int x=0; x<total.length; x++){
					if("currency".equals(colFormat[x])){
						buf.append("<td>").append(NumberUtil.formatCurrency(String.valueOf(total[x]))).append("</td>");
					}else{
						if(x==0){
							buf.append("<td>").append("TOTAL").append("</td>");
						}else{
							buf.append("<td>").append("&nbsp;").append("</td>");
						}
					}
				}				
				buf.append("</tr>");
//				buf.append("</tfoot>");
			}
			
			buf.append("</table>");
			buf.append("</div>");
						
			buffer.append("<p:useTemplate>templatereport.htm</p:useTemplate>").append("\n");				
			buffer.append("<p:component value=\"body\">").append("\n");	
				buffer.append(buf);	
			buffer.append("</p:component>").append("\n");						
		}catch(Exception e){
			logger.error(e, e);
		}
		return buffer;
	}
}
