package com.forest.mfg.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.WebsiteDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DateUtil;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;

public class AdminSQLServices extends GenericServices{

	
	private Logger logger = Logger.getLogger (this.getClass ());
	
	public AdminSQLServices(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn){
		super(conn, shopInfoBean, clientBean);

//		gs = new GenericServices(_dbConnection);
	}

	public void addList(ArrayList list, Object obj, String prefix){
		HashMap fieldMap = new HashMap();
		fieldMap.put("object", obj); 
		fieldMap.put("prefix", prefix); 
		list.add(fieldMap);
	}
	
	public ArrayList getMonthlySales(){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HashMap dataRow = null;
		ArrayList list = new ArrayList();
		try{			
			query.append("Select x.SALESMONTH, x.SALESYEAR, SUM(x.SALES) As SALES From (");
				query.append("Select ");	
				query.append(" a.").append(MFG_TransactionDef.transno).append(",");			
				query.append(" MONTHNAME(a.").append(MFG_TransactionDef.salesdate).append(") SALESMONTH,");			
				query.append(" YEAR(a.").append(MFG_TransactionDef.salesdate).append(") SALESYEAR,");	
				query.append(" MONTH(a.salesdate) AS Mon, ");
				query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(" - a.").append(MFG_TransactionDef.discount).append(" + COALESCE(a.").append(MFG_TransactionDef.gst_amt).append(", 0)) As SALES");
	//			query.append(" Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(") As SALES");			
				query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
				query.append(" Inner Join (" + 
						"Select transid, Sum(sellsubtotal) as sellsubtotal From mfg_transaction_detail Where status!='D'  Group By transid\r\n" + 
						") b");
				query.append(" On b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
				
				query.append(" Where a.").append(MFG_TransactionDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
				query.append(" And a.").append(MFG_TransactionDef.status).append("=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");
//				query.append(" And b.").append(MFG_TransactionDetailDef.status).append("!='").append(GeneralConst.DELETED).append("'");
				query.append(" And a.").append(MFG_TransactionDef.salesdate);
	//			query.append(" BETWEEN CURDATE() - INTERVAL 1 YEAR AND CURDATE()");
				query.append(" BETWEEN '").append(Calendar.getInstance().get(Calendar.YEAR)-1).append("-").append(DateUtil.getCurrentMonth()).append("-01'");
				query.append(" And CURDATE()");
				query.append(" Group By a.").append(MFG_TransactionDef.transno).append(", YEAR(a.salesdate), MONTH(a.salesdate),  MONTHNAME(a.salesdate)");	
			query.append(") x Group by x.SALESMONTH, x.SALESYEAR, x.Mon");
			query.append(" Order By x.SALESYEAR, x.Mon");
//			query.append(" And YEAR(a.").append(MFG_TransactionDef.salesdate).append(")=").append(DateUtil.getCurrentYear()).append("");
//			query.append(" Group By YEAR(a.").append(MFG_TransactionDef.salesdate).append(")");
//			query.append(" , MONTH(a.").append(MFG_TransactionDef.salesdate).append(")");
			logger.debug("getMonthlySales query = "+query);
			pstmt = _dbConnection.prepareStatement (query.toString ());			

			rs = pstmt.executeQuery ();
			while(rs.next ()){				
				dataRow = new HashMap();				
				dataRow.put (MFG_TransactionDef.salesdate.name, rs.getString ("SALESMONTH").substring(0, 3) + " " + rs.getString ("SALESYEAR").substring(2, 4));				
				dataRow.put ("SALES", rs.getString ("SALES"));
				list.add (dataRow);
			}	
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			free(null, pstmt, rs);
		}		
		return list;
	}
	
	public ArrayList getCurrentMonthlySales(){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HashMap dataRow = null;
		ArrayList list = new ArrayList();
		try{			
			query.append("Select DAY(a.").append(MFG_TransactionDef.salesdate).append(") As ").append(MFG_TransactionDef.salesdate);			
			query.append(", Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append(" - a.").append(MFG_TransactionDef.discount).append(" + COALESCE(a.").append(MFG_TransactionDef.gst_amt).append(", 0)) As SALES");
		
			query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
			query.append(" Inner Join (" + 
					"Select transid, Sum(sellsubtotal) as sellsubtotal From mfg_transaction_detail Where status!='D'  Group By transid" + 
					") b");
			query.append(" On b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
			
			query.append(" Where a.").append(MFG_TransactionDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
			query.append(" And a.").append(MFG_TransactionDef.status).append("=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");
			//query.append(" And b.").append(MFG_TransactionDetailDef.status).append("!='").append(GeneralConst.DELETED).append("'");
			query.append(" And a.").append(MFG_TransactionDef.salesdate);
			query.append(" BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE()");
//			query.append(" And MONTH(a.").append(MFG_TransactionDef.salesdate).append(")=").append(DateUtil.getCurrentMonth()).append("");
//			query.append(" And YEAR(a.").append(MFG_TransactionDef.salesdate).append(")=").append(DateUtil.getCurrentYear()).append("");
			query.append(" Group By a.").append(MFG_TransactionDef.salesdate);
//			query.append(" , MONTH(a.").append(MFG_TransactionDef.salesdate).append(")");
			query.append(" Order By a.").append(MFG_TransactionDef.salesdate);
			
			logger.debug("getCurrentMonthlySales query = "+query);
			pstmt = _dbConnection.prepareStatement (query.toString ());			

			rs = pstmt.executeQuery ();
			while(rs.next ()){				
				dataRow = new HashMap();				
				dataRow.put (MFG_TransactionDef.salesdate.name, rs.getString (MFG_TransactionDef.salesdate.name));				
				dataRow.put ("SALES", rs.getString ("SALES"));
				list.add (dataRow);
			}	
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			free(null, pstmt, rs);
		}		
		return list;
	}
	
//	public ArrayList getCurrentMonthlyProfitAndCost(){
//		StringBuffer query = new StringBuffer();
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		HashMap dataRow = null;
//		ArrayList list = new ArrayList();
//		try{			
//			query.append("Select DAY(a.").append(MFG_TransactionDef.salesdate).append(") As ").append(MFG_TransactionDef.salesdate);
//			query.append(", Sum(b.").append(MFG_TransactionDetailDef.sellsubtotal).append("-b.").append(MFG_TransactionDetailDef.costsubtotal).append(") As PROFIT");
//			query.append(", Sum(b.").append(MFG_TransactionDetailDef.costsubtotal).append(") As ").append(MFG_TransactionDetailDef.costsubtotal);
//			query.append(" From ").append(MFG_TransactionDef.TABLE).append(" a");
//			query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b");
//			query.append(" On b.").append(MFG_TransactionDetailDef.transid).append("=a.").append(MFG_TransactionDef.transid);
//			
//			query.append(" Where a.").append(MFG_TransactionDef.companyid).append("='").append(_shopInfoBean.getShopName()).append("'");
//			query.append(" And a.").append(MFG_TransactionDef.status).append("=").append(MFG_SelectBuilder.TRANS_INVOICE).append("");
//			query.append(" And b.").append(MFG_TransactionDetailDef.status).append("!='").append(GeneralConst.DELETED).append("'");
//			query.append(" And MONTH(a.").append(MFG_TransactionDef.salesdate).append(")=").append(DateUtil.getCurrentMonth()).append("");
//			query.append(" And YEAR(a.").append(MFG_TransactionDef.salesdate).append(")=").append(DateUtil.getCurrentYear()).append("");
//			query.append(" Group By DAY(a.").append(MFG_TransactionDef.salesdate).append(")");
//			
//			logger.debug("getMonthlySales query = "+query);
//			pstmt = _dbConnection.prepareStatement (query.toString ());			
//
//			rs = pstmt.executeQuery ();
//			while(rs.next ()){				
//				dataRow = new HashMap();				
//				dataRow.put (MFG_TransactionDef.salesdate.name, rs.getString (MFG_TransactionDef.salesdate.name));
//				dataRow.put (MFG_TransactionDetailDef.costsubtotal.name, rs.getString (MFG_TransactionDetailDef.costsubtotal.name));
//				dataRow.put ("PROFIT", rs.getString ("PROFIT"));
//				list.add (dataRow);
//			}	
//		}catch(Exception e){
//			logger.error(e, e);
//		}finally{
//			free(null, pstmt, rs);
//		}		
//		return list;
//	}
	public ArrayList getCompanyGroup(String shopName)throws Exception{
		ArrayList companyGroup = null;
		
		// Get Company Group
		StringBuffer query = new StringBuffer();
		query.append("Select");			
		query.append(" ").append(ShopBeanDef.companyid);
		query.append(",").append(ShopBeanDef.shopshortname);
		query.append(",").append(ShopBeanDef.shoplongname);
		query.append(",").append(ShopBeanDef.shopdomain);
		query.append(",").append(ShopBeanDef.theme);
		query.append(",").append(ShopBeanDef.admincontext);
		
		query.append(" From ").append(ShopBeanDef.TABLE);
		query.append(" Where ").append(BaseDef.STATUS).append("='").append(GeneralConst.ACTIVE).append("'");
		query.append(" And ").append(ShopBeanDef.companygroup).append("='").append(shopName).append("'");
		query.append(" Order By ").append("seq");
		companyGroup = searchDataArray(query);
		return companyGroup;
	}
	
	public HashMap getCompanyMap(String shopName){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		ArrayList list = new ArrayList();	
		HashMap data = null;
		try{			
			query.append("Select");
			query.append(" a.").append(ShopBeanDef.companyid);
			query.append(",a.").append(ShopBeanDef.shoplongname);
			query.append(",a.").append(ShopBeanDef.shopdomain);
			query.append(",a.").append(ShopBeanDef.admincontext);
			query.append(",a.").append(ShopBeanDef.theme);		
			query.append(",a.").append(ShopBeanDef.business);
			query.append(",a.").append(ShopBeanDef.companygroup);			
			query.append(",b.").append(WebsiteDef.countryiso);
			query.append(",b.").append(WebsiteDef.paycurrencyiso);
			query.append(",coalesce(").append(ShopBeanDef.language).append(",'en') As ").append(ShopBeanDef.language);
			query.append(",").append(ShopBeanDef.default_template);			
			query.append(" From ").append(ShopBeanDef.TABLE).append(" a");;
			query.append(" Inner Join ").append(WebsiteDef.TABLE).append(" b On b.");
			query.append(WebsiteDef.companyid).append("=a.").append(ShopBeanDef.companyid);
			
			query.append(" Where a.").append(BaseDef.STATUS).append("='").append(GeneralConst.ACTIVE).append("'");
			if(!CommonUtil.isEmpty(shopName)){
				query.append(" And ").append(ShopBeanDef.companyid).append("='").append(shopName).append("'");
			}
			logger.debug("getShopsSetting query = "+query);
			list = searchDataArray(query);	
			data = (HashMap) list.get(0);
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			free(null, pstmt, rs);
		}		
		return data;
	}
	
	public ArrayList getShopsSetting(String shopName){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		ArrayList list = new ArrayList();	
		try{			
			query.append("Select");
			query.append(" a.").append(ShopBeanDef.companyid);
			query.append(",a.").append(ShopBeanDef.shoplongname);
			query.append(",a.").append(ShopBeanDef.shopdomain);
			query.append(",a.").append(ShopBeanDef.admincontext);
			query.append(",a.").append(ShopBeanDef.theme);		
			query.append(",a.").append(ShopBeanDef.business);
			query.append(",a.").append(ShopBeanDef.companygroup);			
			query.append(",b.").append(WebsiteDef.countryiso);
			query.append(",b.").append(WebsiteDef.paycurrencyiso);
			query.append(",coalesce(").append(ShopBeanDef.language).append(",'en') As ").append(ShopBeanDef.language);
			query.append(",").append(ShopBeanDef.default_template);			
			query.append(" From ").append(ShopBeanDef.TABLE).append(" a");;
			query.append(" Left Join ").append(WebsiteDef.TABLE).append(" b On b.");
			query.append(WebsiteDef.companyid).append("=a.").append(ShopBeanDef.companyid);
			
			query.append(" Where a.").append(BaseDef.STATUS).append("='").append(GeneralConst.ACTIVE).append("'");
			if(!CommonUtil.isEmpty(shopName)){
				query.append(" And ").append(ShopBeanDef.companyid).append("='").append(shopName).append("'");
			}
			logger.debug("getShopsSetting query = "+query);
			list = searchDataArray(query);			
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			free(null, pstmt, rs);
		}		
		return list;
	}
	
	public ArrayList getShopsWebSite(String shopName){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		ArrayList list = new ArrayList();	
		try{			
			query.append("Select");
			query.append(" a.").append(WebsiteDef.companyid);			
			query.append(",a.").append(WebsiteDef.domain);
			query.append(",a.").append(WebsiteDef.theme);
			query.append(",a.").append(WebsiteDef.email);
			query.append(",a.").append(WebsiteDef.email_passwd);
			query.append(",b.").append(ShopBeanDef.shoplongname);										
			query.append(",b.").append(ShopBeanDef.companygroup);
			query.append(",b.").append(ShopBeanDef.business);
			query.append(",a.").append(WebsiteDef.countryiso);
			query.append(",a.").append(WebsiteDef.paycurrencyiso);
			query.append(" From ").append(WebsiteDef.TABLE).append(" a");
			query.append(" Inner Join ").append(ShopBeanDef.TABLE).append(" b On b.");
			query.append(ShopBeanDef.companyid).append("=a.").append(WebsiteDef.companyid);
			
			if(!CommonUtil.isEmpty(shopName)){
				query.append(" Where a.").append(WebsiteDef.companyid).append("='").append(shopName).append("'");
			}
			logger.debug("getShopsWebSite query = "+query);
			list = searchDataArray(query);			
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			free(null, pstmt, rs);
		}		
		return list;
	}
	
}
