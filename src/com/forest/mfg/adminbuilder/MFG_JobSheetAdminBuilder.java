package com.forest.mfg.adminbuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import bsh.Interpreter;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.AutoNumberBuilder;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.DateUtil;
import com.forest.common.util.ReportUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_JobSheetDef;
import com.forest.mfg.beandef.MFG_JobSheetDetailDef;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_RawToProductDef;
import com.forest.mfg.beandef.MFG_RawToProductDetailDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;

public class MFG_JobSheetAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_JobSheetAdminBuilder.class);
	public MFG_JobSheetAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);		
	}
	
	public String createJobCode(String companyid, String transId){
		logger.debug("createJobCode("+companyid+","+transId+")");		
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String jobCode = "";
		try{
			// Create job code for all EKC invoice.
			if(!"ekc".equals(companyid)){
				// Check if need to create job sheet
				query.append("select a.").append(MFG_TransactionDetailDef.transid);			
				query.append(" From ").append(MFG_TransactionDetailDef.TABLE).append(" a");			
				query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b on b.");
				query.append(MFG_CustProductDef.prodid).append(" = a.").append(MFG_TransactionDetailDef.prodid).append("");
				query.append(" Where a.").append(MFG_TransactionDetailDef.transid).append(" = ?");
				query.append(" And b.").append(MFG_CustProductDef.companyid).append("  = ?");			
				
				logger.debug("createJobCode query = "+query);
				pstmt = _dbConnection.prepareStatement(query.toString());		
				pstmt.setInt(1, Integer.parseInt(transId));
				pstmt.setString(2, companyid);
				rs = pstmt.executeQuery();
				if(!rs.next()){				
					return null;
				}
			}		
			jobCode = new AutoNumberBuilder().AUTO_NUM(_dbConnection, companyid, "AUTO-JOBS", null);
			
			// Update Transaction Table
			query = new StringBuffer();
			query.append("Update ").append(MFG_TransactionDef.TABLE).append(" Set ");
			query.append(MFG_TransactionDef.jobcode).append("='").append(jobCode).append("'");
			query.append(" Where ").append(MFG_TransactionDef.transid).append(" = ").append(transId);			
			logger.debug("Update Transaction query = "+query);
			pstmt = _dbConnection.prepareStatement(query.toString());
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			DBUtil.free(null, pstmt, rs);
		}
		return jobCode;
	}
	
	
	public String createJobSheet(String companyid, String supplierTransId){
		/*
		 * If create from master transaction, mainTransId is master shop, supplierTransId is sub shop 
		 */
		int jobid = 0;
		String jobCode = null;		
		HashMap jobsheetMap = new HashMap();
		HashMap jobsheetDetailMap = new HashMap();		
		ArrayList jobsheetList = new ArrayList();		
		
		String transdetailid = null;
		String customisedata = null;
		String prodid = null;
		String rawid = null;
		String org_rawid = null;
//		String opt = null;
		String widthformula = null;
		String heightformula = null;
		String qty = null;
		String rtpdtlid = null;
		String opt1 = null;
		String opt2 = null;
		String opt3 = null;
		String opt4 = null;
		String opt5 = null;
		String opt6 = null;
		String opt7 = null;
		String opt8 = null;
		String comment = null;
		double width = 0;
		double height = 0;
		
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		HashMap prodOptionRaw = new HashMap();
		Class defClass = MFG_JobSheetDef.class;
		int update = 0;
		String measurement = null;
		
		JSONObject json = null;
		JSONArray colw = null;
		JSONArray rowh = null;
		JSONArray rowhDetail = null;
		JSONArray span = null;
		JSONArray spanDetail = null;
		int colwCount = 0;
		int rowhCount = 0;
		
		String jobdetailid = null;
		String status = null;
		String qtyFormula = null;
		String performBy = null;
		
		HashMap dataHash = null;
		HashMap[] dataDetailMap = null;
		try{
			dataHash = MFG_SelectBuilder.getHASH_PRODUCT_OPTION(_dbConnection, _shopInfoBean);
			
			// Check Existing job 
			query = new StringBuffer();
			query.append("select ").append(MFG_TransactionDef.jobcode);
			query.append(" From ").append(MFG_TransactionDef.TABLE);
			query.append(" Where ").append(MFG_TransactionDef.transid).append("=").append(supplierTransId);
			pstmt = _dbConnection.prepareStatement(query.toString());			
			rs = pstmt.executeQuery();
			if(rs.next()){				
				jobCode = rs.getString(MFG_TransactionDef.jobcode.name).split(",")[0];
				jobsheetMap.put(MFG_JobSheetDef.jobid.name, String.valueOf(jobid));
			}else{
				return null;
			}
			logger.debug(" Adam jobCode = "+jobCode);
			jobsheetMap.put(MFG_JobSheetDef.companyid.name, companyid);
			jobsheetMap.put(MFG_JobSheetDef.transid.name, supplierTransId);
			jobsheetMap.put(MFG_JobSheetDef.jobcode.name, jobCode);
			jobsheetMap.put(MFG_JobSheetDef.status.name, GeneralConst.ACTIVE);
			jobsheetMap.put(MFG_JobSheetDef.updatedate.name, "");
			
			addArray = BeanDefUtil.getArrayList(defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
//			if(jobid>0){
//				update = _gs.update(defClass, addArray, jobsheetMap);
//			}else{
				Random randomGenerator = new Random();
				jobid = randomGenerator.nextInt(1000);
				jobsheetMap.put(MFG_JobSheetDef.jobid.name, String.valueOf(jobid));
				_gs.create(defClass, addArray, jobsheetMap);					
//			}
			
			logger.debug("update = "+update+", jobid="+jobid);
			// Retrieve Product Option
			query = new StringBuffer();
			query.append("select b.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(",");
			query.append(" b.").append(MFG_ProductOptionDetailDef.rawid);
			query.append(" From ").append(MFG_ProductOptionDef.TABLE).append(" a");
			query.append(" Inner Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" b On ");
			query.append(" b.").append(MFG_ProductOptionDetailDef.prodoptid).append("=a.").append(MFG_ProductOptionDef.prodoptid);
			query.append(" Where a.").append(MFG_ProductOptionDef.companyid).append("='").append(companyid).append("'");
			query.append(" And a.").append(MFG_ProductOptionDef.status).append("!='").append(GeneralConst.DELETED).append("'");
			logger.debug(" query = "+query);
			pstmt = _dbConnection.prepareStatement(query.toString());			
			rs = pstmt.executeQuery();
			while(rs.next()){
				prodOptionRaw.put(rs.getString(MFG_ProductOptionDetailDef.prodoptdetailid.name), rs.getString(MFG_ProductOptionDetailDef.rawid.name));				
			}
			
//			// Remove All job sheet detail 1st
//			query = new StringBuffer();
//			query.append("Delete From mfg_jobsheet_detail Where jobid = ?");
//			pstmt = _dbConnection.prepareStatement(query.toString());
//			pstmt.setInt(1, jobid);
//			pstmt.execute();
			
			// Build Job Sheet
			query = new StringBuffer();
			query.append("select h.rtpdtlid, a.transdetailid, a.width, a.height, a.customisedata,");
			query.append(" a.opt1, a.opt2, a.opt3, a.opt4, a.opt5, a.opt6, a.opt7, a.opt8, a.status,");
			query.append(" a.prodid, h.rawid, h.comment, "); 
			query.append(" h.widthformula, h.heightformula,"); 
			query.append(" a.qty, h.qty as qtyFormula, h.perform_by");
			query.append(" from mfg_transaction_detail a ");
			query.append(" Inner Join mfg_transaction b On b.transid=a.transid"); 			
			query.append(" Left Join mfg_rawtoproduct g on g.prodid = a.prodid and g.status='A'"); 
			query.append(" Left Join mfg_rawtoproduct_detail h on h.rtpid = g.rtpid ");
//			query.append(" Left Join mfg_jobsheet_detail j on j.transdetailid = a.transdetailid And j.prodid=a.prodid ");
//			query.append(" And (j.org_rawid=h.rawid Or h.rawid is null) And (j.rtpdtlid = h.rtpdtlid Or h.rtpdtlid is null) ");
			query.append(" Where a.transid = ? And a.status = '").append(GeneralConst.ACTIVE).append("'");
			query.append(" And g.companyid = '").append(companyid).append("'");
			query.append(" Order by g.prodid, h.rawid");
			logger.debug(" Build Job Sheet query = "+query);
			pstmt = _dbConnection.prepareStatement(query.toString());
			pstmt.setString(1, supplierTransId);
			rs = pstmt.executeQuery();
			while(rs.next()){
				measurement = "mm";
				transdetailid = rs.getString("transdetailid");
				customisedata = rs.getString("customisedata");
				prodid = rs.getString("prodid");
				rawid = rs.getString("rawid");				
				org_rawid = rs.getString("rawid");	
				widthformula = rs.getString("widthformula");
				heightformula = rs.getString("heightformula");
				qty = rs.getString("qty");
				qtyFormula = rs.getString("qtyFormula");
				width = rs.getDouble("width");
				height = rs.getDouble("height");
				rtpdtlid = rs.getString("rtpdtlid");
				
				opt1 = rs.getString("opt1");
				opt2 = rs.getString("opt2");
				opt3 = rs.getString("opt3");
				opt4 = rs.getString("opt4");
//				opt5 = rs.getString("opt5");
//				opt6 = rs.getString("opt6");
//				opt7 = rs.getString("opt7");
//				opt8 = rs.getString("opt8");
				jobdetailid = null;
				status = rs.getString("status");
				performBy = rs.getString(MFG_RawToProductDetailDef.perform_by.name);
				comment = rs.getString("comment");
				
				// Convert all opt to code
				logger.debug("dataHash = "+dataHash);
				if(!CommonUtil.isEmpty(opt1)) opt1 = (String) ((HashMap)dataHash.get(opt1)).get(MFG_ProductOptionDetailDef.code.name);
				if(!CommonUtil.isEmpty(opt2)) opt2 = (String) ((HashMap)dataHash.get(opt2)).get(MFG_ProductOptionDetailDef.code.name);
				if(!CommonUtil.isEmpty(opt3)) opt3 = (String) ((HashMap)dataHash.get(opt3)).get(MFG_ProductOptionDetailDef.code.name);
				if(!CommonUtil.isEmpty(opt4)) opt4 = (String) ((HashMap)dataHash.get(opt4)).get(MFG_ProductOptionDetailDef.code.name);
//				if(!CommonUtil.isEmpty(opt5)) opt5 = (String) ((HashMap)dataHash.get(opt5)).get(MFG_ProductOptionDetailDef.code.name);
//				if(!CommonUtil.isEmpty(opt6)) opt6 = (String) ((HashMap)dataHash.get(opt6)).get(MFG_ProductOptionDetailDef.code.name);
//				if(!CommonUtil.isEmpty(opt7)) opt7 = (String) ((HashMap)dataHash.get(opt7)).get(MFG_ProductOptionDetailDef.code.name);
//				if(!CommonUtil.isEmpty(opt8)) opt8 = (String) ((HashMap)dataHash.get(opt8)).get(MFG_ProductOptionDetailDef.code.name);
				
				if(!CommonUtil.isEmpty(customisedata)){		
//					logger.debug("rawid="+rawid+",rs="+rs.getString(rawid)+", prodOptionRaw = "+prodOptionRaw);
										
					json = new JSONObject(customisedata.replaceAll("\\(", "").replaceAll("\\)", ""));
					colw = (JSONArray)json.get("colw");
					rowh = (JSONArray)json.get("rowh");
					span = (JSONArray)json.get("span");
					
					colwCount = colw.length();
					rowhCount = rowh.length();
					
					if(MFG_SelectBuilder.PERFORM_BY_COL.equals(performBy)){
						height = 1;
						for(int a=1; a<=colwCount; a++){																												
							width = colw.getDouble(a-1);							
							jobsheetList.add(createJSDetailHash(measurement, width, height, opt1, opt2, opt3, opt4, opt5, opt6, opt7, opt8, 
									widthformula, heightformula, jobid, transdetailid, 
									prodid, rawid, org_rawid, qty, qtyFormula, rtpdtlid, jobdetailid, status, colwCount, rowhCount, comment));							
						}
					}else if(MFG_SelectBuilder.PERFORM_BY_SPAN.equals(performBy)){
						//int counter=0;
						for(int a=0; a<colwCount; a++){		
							width = colw.getDouble(a);
							
							rowhDetail = rowh.getJSONArray(a);
							spanDetail = span.getJSONArray(a);
							rowhCount = rowhDetail.length();
							logger.debug("------------- rowhCount = "+rowhCount);
							for(int b=0; b<rowhCount; b++){
								opt1 = spanDetail.getString(b);
								rawid = (String) prodOptionRaw.get(opt1);																
								height = rowhDetail.getDouble(b);	
								opt1 = (String) ((HashMap)dataHash.get(opt1)).get(MFG_ProductOptionDetailDef.code.name);
								logger.debug("prodid = "+prodid+", opt1 = "+opt1);
								jobsheetDetailMap = createJSDetailHash(measurement, width, height, opt1, opt2, opt3, opt4, opt5, opt6, opt7, opt8, 
										widthformula, heightformula, jobid, transdetailid, 
										prodid, rawid, org_rawid, qty, qtyFormula, rtpdtlid, jobdetailid, status, colwCount, rowhCount, comment);								
								
								
								if(jobsheetDetailMap!=null)	jobsheetList.add(jobsheetDetailMap);
								//counter++;
							}
						}
					}else{			
						jobsheetDetailMap = createJSDetailHash(measurement, width, height, opt1, opt2, opt3, opt4, opt5, opt6, opt7, opt8, 
								widthformula, heightformula, jobid, transdetailid, 
								prodid, rawid, org_rawid, qty, qtyFormula, rtpdtlid, jobdetailid, status, colwCount, rowhCount, comment);
						if(jobsheetDetailMap!=null)	jobsheetList.add(jobsheetDetailMap);
					}
				}else{
					if(!CommonUtil.isEmpty(rawid) && rawid.indexOf("opt")!=-1){						
						rawid = (String) prodOptionRaw.get(rs.getString(rawid));
					}
					colwCount = 1;
					rowhCount = 1;
					jobsheetDetailMap = createJSDetailHash(measurement, width, height, opt1, opt2, opt3, opt4, opt5, opt6, opt7, opt8, 
							widthformula, heightformula, jobid, transdetailid, 
							prodid, rawid, org_rawid, qty, qtyFormula, rtpdtlid, jobdetailid, status, colwCount, rowhCount, comment);
					if(jobsheetDetailMap!=null)	jobsheetList.add(jobsheetDetailMap);
				}
				
				
//				logger.debug("----jobsheetDetailMap = "+jobsheetDetailMap);
				
			}
			logger.debug("jobsheetList.size() = "+jobsheetList.size());
			dataDetailMap = new HashMap[jobsheetList.size()];
			for(int i=0; i<jobsheetList.size(); i++){
				logger.debug(" i = "+i);
				dataDetailMap[i] = (HashMap) jobsheetList.get(i);
				
			}			
			_gs.updateDetail(MFG_JobSheetDetailDef.class, dataDetailMap, BeanDefUtil.getKeyObject(MFG_JobSheetDetailDef.class), BeanDefUtil.getArrayList(MFG_JobSheetDetailDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));									
		}catch (Exception e) {
			logger.error(e, e);
		}finally{
			DBUtil.free(null, pstmt, rs);
		}
		return jobCode;
	}
	
	private HashMap createJSDetailHash(String measurement, double width, double height,
			String opt1,String opt2,String opt3,String opt4,String opt5,String opt6,String opt7,String opt8,
			String widthformula, String heightformula, int jobid, String transdetailid,
			String prodid, String rawid, String org_rawid, String qty, String qtyFormula, String rtpdtlid, 
			String jobdetailid, String status, int colwCount, int rowhCount, String comment) throws Exception{
		Interpreter itp = new Interpreter();
		String rawwidth = null;
		String rawheight = null;
		String qtyResult = null;
		
		itp.set("w", width);
		itp.set("h", height);
		itp.set("colwcount", colwCount);
		itp.set("rowhcount", rowhCount);
		itp.set("opt1", opt1);
		itp.set("opt2", opt2);
		itp.set("opt3", opt3);
//		itp.set("opt4", opt4);
//		itp.set("opt5", opt5);
//		itp.set("opt6", opt6);
//		itp.set("opt7", opt7);
//		itp.set("opt8", opt8);
		
		logger.debug("opt1 = "+opt1+", opt2 = "+opt2+", opt3 = "+opt3);
		
		if(!CommonUtil.isEmpty(qtyFormula)){			
			qtyResult = String.valueOf(itp.eval(qtyFormula));
			qtyResult = String.valueOf(Integer.parseInt(qtyResult) * Integer.parseInt(qty));
			logger.debug(qtyFormula+" = "+qtyResult);
		}else{
			qtyResult = qty;
		}
		if(CommonUtil.parseInt(qtyResult)==0){
			return null;
		}
		if(!CommonUtil.isEmpty(widthformula)){
			logger.debug("widthformula = "+widthformula);
			logger.debug("colwCount="+colwCount+",rowhcount="+rowhCount + ", w="+width+", h="+height);
			rawwidth = String.valueOf(itp.eval(widthformula));
			logger.debug("rawwidth = "+rawwidth);
//			logger.debug("prodid = "+prodid+", formula="+widthformula + ", Result="+rawwidth);					
		}
		if(!CommonUtil.isEmpty(heightformula)){					
			logger.debug("heightformula = "+heightformula);
			rawheight = String.valueOf(itp.eval(heightformula));
			logger.debug("rawheight = "+rawheight);
		}
		
		HashMap jobsheetDetailMap = new HashMap();
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.jobid.name, String.valueOf(jobid));
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.jobdetailid.name, jobdetailid);
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.transdetailid.name, transdetailid);
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.prodid.name, prodid);
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.rawid.name, rawid);
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.org_rawid.name, org_rawid);
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.qty.name, qtyResult);
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.width.name, rawwidth);
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.height.name, rawheight);
		jobsheetDetailMap.put(MFG_JobSheetDetailDef.rtpdtlid.name, rtpdtlid);
		logger.debug("comment = "+comment);
		if(!CommonUtil.isEmpty(comment) && comment.indexOf("opt")!=-1){
			
			jobsheetDetailMap.put(MFG_JobSheetDetailDef.remark.name, itp.eval(comment));	
		}else{
			jobsheetDetailMap.put(MFG_JobSheetDetailDef.remark.name, comment);	
		}
		if(jobdetailid==null){
			jobsheetDetailMap.put("row-status", "N");
		}else{
			if(GeneralConst.DELETED.equals(status)){
				jobsheetDetailMap.put("row-status", "D");
			}else{
				jobsheetDetailMap.put("row-status", "U");
			}
			
		}
		logger.debug("----jobsheetDetailMap = "+jobsheetDetailMap);
		return jobsheetDetailMap;
	}
	
	public JasperPrint buildReport()throws Exception{
		Map groupMap = new HashMap();
		Map parameters = new HashMap();
		Map dataMap = null;
//		String measurement = null;
		String jobId = null;
		String prodMeasurementDesc = null;
		String prodMeasurementDetailDesc = null;
		String customisedata = null;
		JSONObject customizeJson = null;
		JSONArray colw = null;
		String transId = _req.getParameter(MFG_TransactionDef.transid.name);		
		// Recreate Job Detail
		createJobSheet(_shopInfoBean.getShopName(), transId);
		
		subClass = BeanDefUtil.getSubClass(_defClass);
		StringBuffer query = new StringBuffer();
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
				
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
		_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, searchArray, _accessByCompany);
		
		arrayListSearch = (ArrayList)_gs.search(_defClass, null, searchArray, addArray, _attrReqDataMap, null, "1", _reqAction).get(1);				
		if(arrayListSearch.size()>0){
			parameters = (HashMap) arrayListSearch.get(0);							
			parameters.put("jobdate", DateUtil.getCurrentDate());
//			measurement = (String)parameters.get(MFG_TransactionDef.measurement.name);
			jobId = (String)parameters.get(MFG_JobSheetDef.jobid.name);
			logger.debug("parameters = "+parameters);			
		}
						
		if(subClass!=null){
			query.append(" Select a.rtpdtlid, b.transdetailid, b.width As prod_width, b.height As prod_height, b.qty As prod_qty,");
			query.append(" b.opt1,b.opt2,b.opt3,b.opt4,b.opt5,b.opt6,b.opt7,b.opt8, b.customisedata, ");
			query.append(" a.prodid,a.rawid,a.width,a.height, Sum(a.qty) as qty, a.remark");  
			query.append(" From mfg_jobsheet_detail a"); 
			query.append(" Left Join mfg_transaction_detail b on b.transdetailid = a.transdetailid");
			query.append(" Left Join mfg_rawtoproduct_detail c on c.rtpdtlid = a.rtpdtlid");
			query.append(" Where a.jobid = ").append(jobId);			
			query.append(" group by b.width, b.height, b.qty, b.opt1,b.opt2,b.opt3,b.opt4,b.opt5,b.opt6,b.opt7,b.opt8,");
			query.append(" a.prodid,a.rawid,a.width,a.height, a.remark");
			query.append(" Order By b.prodid, b.opt1, b.opt2, b.opt3, b.transdetailid, c.position, a.rawid ");
			logger.debug("buildReport query = "+query);
			arrayListSearch = gs.searchDataArray(query);
			
			HashMap prodMap = MFG_SelectBuilder.getHASH_CUST_PRODUCT(_dbConnection, _shopInfoBean);
			HashMap optMap = MFG_SelectBuilder.getHASH_PRODUCT_OPTION(_dbConnection, _shopInfoBean);
			HashMap rawMap = MFG_SelectBuilder.getHASH_RAW(_dbConnection, _shopInfoBean);
			String prodGroupDesc = null;
			
			String prodid = null;
			
			HashMap optMapDetail = null;
			String transDetailId = "";
			int groupCounter = 0;
			for(int i=0; i<arrayListSearch.size(); i++){				
				dataMap = (HashMap) arrayListSearch.get(i);				
				if(!transDetailId.equals((String) dataMap.get(MFG_TransactionDetailDef.transdetailid.name))){
					groupCounter++;			
					groupMap.put((String) dataMap.get(MFG_TransactionDetailDef.transdetailid.name), String.valueOf(groupCounter) + ".");
				}
				transDetailId = (String) dataMap.get(MFG_TransactionDetailDef.transdetailid.name);
				prodid = (String) dataMap.get(MFG_TransactionDetailDef.prodid.name);
				prodGroupDesc = "<b>"+getHashStringValue(prodMap, prodid, null)+"</b>";
				
				dataMap.put("name", getHashStringValue(prodMap, prodid, null));
				dataMap.put("groupcounter", String.valueOf(groupCounter) + ".");
			
				if(!CommonUtil.isEmpty((String)dataMap.get(MFG_TransactionDetailDef.opt1.name))){
					optMapDetail = (HashMap) optMap.get((String) dataMap.get(MFG_TransactionDetailDef.opt1.name));					
					prodGroupDesc += " / " + optMapDetail.get(MFG_ProductOptionDef.groupname.name) + ": <b>" + 
					optMapDetail.get(MFG_ProductOptionDetailDef.name.name)+"</b>";
					
					dataMap.put("opt1_groupname", optMapDetail.get(MFG_ProductOptionDef.groupname.name));
					dataMap.put("opt1_name", optMapDetail.get(MFG_ProductOptionDetailDef.name.name));
				}
				if(!CommonUtil.isEmpty((String)dataMap.get(MFG_TransactionDetailDef.opt2.name))){
					optMapDetail = (HashMap) optMap.get((String) dataMap.get(MFG_TransactionDetailDef.opt2.name));					
					prodGroupDesc += " / " + optMapDetail.get(MFG_ProductOptionDef.groupname.name) + ": <b>" + 
					optMapDetail.get(MFG_ProductOptionDetailDef.name.name)+"</b>";
					
					dataMap.put("opt2_groupname", optMapDetail.get(MFG_ProductOptionDef.groupname.name));
					dataMap.put("opt2_name", optMapDetail.get(MFG_ProductOptionDetailDef.name.name));
				}
				if(!CommonUtil.isEmpty((String)dataMap.get(MFG_TransactionDetailDef.opt3.name))){
					optMapDetail = (HashMap) optMap.get((String) dataMap.get(MFG_TransactionDetailDef.opt3.name));					
					prodGroupDesc += " / " + optMapDetail.get(MFG_ProductOptionDef.groupname.name) + ": <b>" + 
					optMapDetail.get(MFG_ProductOptionDetailDef.name.name)+"</b>";
					
					
					dataMap.put("opt3_groupname", optMapDetail.get(MFG_ProductOptionDef.groupname.name));
					dataMap.put("opt3_name", optMapDetail.get(MFG_ProductOptionDetailDef.name.name));
				}
				
				if(!CommonUtil.isEmpty((String)dataMap.get(MFG_TransactionDetailDef.opt4.name))){
					optMapDetail = (HashMap) optMap.get((String) dataMap.get(MFG_TransactionDetailDef.opt4.name));					
					prodGroupDesc += " / " + optMapDetail.get(MFG_ProductOptionDef.groupname.name) + ": <b>" + 
					optMapDetail.get(MFG_ProductOptionDetailDef.name.name)+"</b>";
					
					
					dataMap.put("opt4_groupname", optMapDetail.get(MFG_ProductOptionDef.groupname.name));
					dataMap.put("opt4_name", optMapDetail.get(MFG_ProductOptionDetailDef.name.name));
				}
				
				prodMeasurementDesc = "";
				prodMeasurementDetailDesc = "";
				
				if(!CommonUtil.isEmpty((String)dataMap.get(MFG_TransactionDetailDef.width.name))){
					dataMap.put(MFG_TransactionDetailDef.width.name, new BigDecimal((String)dataMap.get(MFG_TransactionDetailDef.width.name)).setScale(0, BigDecimal.ROUND_UP).toString());
					prodMeasurementDetailDesc = (String)dataMap.get(MFG_TransactionDetailDef.width.name) + "(w)"; 
				}
				if(!CommonUtil.isEmpty((String)dataMap.get(MFG_TransactionDetailDef.height.name))){
					dataMap.put(MFG_TransactionDetailDef.height.name, new BigDecimal((String)dataMap.get(MFG_TransactionDetailDef.height.name)).setScale(0, BigDecimal.ROUND_UP).toString());
					prodMeasurementDetailDesc += " x " + (String)dataMap.get(MFG_TransactionDetailDef.height.name) + "(h)";
				}
				
				if(!CommonUtil.isEmpty((String)dataMap.get("prod_"+MFG_TransactionDetailDef.width.name))){
					dataMap.put("prod_"+MFG_TransactionDetailDef.width.name, new BigDecimal((String)dataMap.get("prod_"+MFG_TransactionDetailDef.width.name)).setScale(0, BigDecimal.ROUND_UP).toString());
					prodMeasurementDesc += (String)dataMap.get("prod_"+MFG_TransactionDetailDef.width.name) + "(w)";					
				}
				if(!CommonUtil.isEmpty((String)dataMap.get("prod_"+MFG_TransactionDetailDef.height.name))){
					dataMap.put("prod_"+MFG_TransactionDetailDef.height.name, new BigDecimal((String)dataMap.get("prod_"+MFG_TransactionDetailDef.height.name)).setScale(0, BigDecimal.ROUND_UP).toString());					
					prodMeasurementDesc += " x " + (String)dataMap.get("prod_"+MFG_TransactionDetailDef.height.name) + "(h)";
				}
				if(!CommonUtil.isEmpty((String)dataMap.get("prod_"+MFG_TransactionDetailDef.qty.name))){					
					prodMeasurementDesc += " / Qty:" + dataMap.get("prod_"+MFG_TransactionDetailDef.qty.name);
				}
				
				// Customized Data
				customisedata = (String) dataMap.get(MFG_TransactionDetailDef.customisedata.name);
				if(!CommonUtil.isEmpty(customisedata)){		
					customizeJson = new JSONObject(customisedata.replaceAll("\\(", "").replaceAll("\\)", ""));
					colw = (JSONArray)customizeJson.get("colw");
					dataMap.put("panelnum", String.valueOf(colw.length()));
					dataMap.put("panelwidth", (new BigDecimal(String.valueOf(colw.get(0))).setScale(0,BigDecimal.ROUND_UP)).toString());
				}
				
				dataMap.put("measurement", prodMeasurementDetailDesc);
				dataMap.put("prodgroupdesc", prodGroupDesc+"   ("+prodMeasurementDesc+")");
				dataMap.put(MFG_JobSheetDetailDef.rawid.name, getHashStringValue(rawMap, (String) dataMap.get(MFG_JobSheetDetailDef.rawid.name), null));
				
				logger.debug("dataMap = "+dataMap);
			}
		}
		
		
		// Construct Job Itemized
		query = new StringBuffer();
		query.append("Select a.transdetailid, d.code as productcode, c.groupname, a.width, a.height,");
		query.append(" Sum(a.qty) qty, a.rawid, b.name, a.remark");
		query.append(" From mfg_jobsheet_detail a");
		query.append(" Inner Join mfg_raw b on b.rawid = a.rawid");
		query.append(" Left Join mfg_grouping c on c.groupid = b.groupid");
		query.append(" Left Join mfg_custproduct d on d.prodid = a.prodid");
		query.append(" Where a.jobid = ").append(jobId);
		query.append(" group by a.transdetailid, a.width, a.height, a.rawid, a.remark");
		query.append(" Order By c.groupname, d.code, b.name, a.remark, a.width");
		logger.debug("Construct Job Itemized query = "+query);
		ArrayList jobItemizeArray = gs.searchDataArray(query);
				
		for(int i=0; i<jobItemizeArray.size(); i++){		
			prodMeasurementDesc = "";
			dataMap = (HashMap) jobItemizeArray.get(i);
			dataMap.put("groupcounter", groupMap.get((String)dataMap.get(MFG_JobSheetDetailDef.transdetailid.name)));
			if(!CommonUtil.isEmpty((String)dataMap.get(MFG_JobSheetDetailDef.width.name))){
				dataMap.put(MFG_JobSheetDetailDef.width.name, new BigDecimal((String)dataMap.get(MFG_JobSheetDetailDef.width.name)).setScale(0, BigDecimal.ROUND_UP).toString());				
				prodMeasurementDesc += (String)dataMap.get(MFG_JobSheetDetailDef.width.name);
				prodMeasurementDesc += "(w)";
			}
			if(!CommonUtil.isEmpty((String)dataMap.get(MFG_JobSheetDetailDef.height.name))){
				dataMap.put(MFG_JobSheetDetailDef.height.name, new BigDecimal((String)dataMap.get(MFG_JobSheetDetailDef.height.name)).setScale(0, BigDecimal.ROUND_UP).toString());				
				prodMeasurementDesc += " x "+(String)dataMap.get(MFG_JobSheetDetailDef.height.name) + "(h)";
			}
			// Remark use to see which option color selected
			if(CommonUtil.isEmpty((String) dataMap.get(MFG_JobSheetDetailDef.remark.name))){
				dataMap.put(MFG_JobSheetDetailDef.remark.name, "");				
			}
			dataMap.put("measurement", prodMeasurementDesc);
			logger.debug("Construct Job Itemized dataMap = "+dataMap);
		}
		
		// Build Report
		ReportUtil rptUtil = new ReportUtil();		
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String subReportDir = ResourceUtil.getSubReportDirectory(_shopInfoBean);
		
		parameters.put("SUBREPORT_DIR", subReportDir);
		parameters.put("REPORT_CONNECTION", _dbConnection);
		parameters.put("jobid", jobId);
		logger.debug("parameters = "+parameters);
		
		JasperPrint jprint = rptUtil.getReportPrint(reportDir + "JobSheet.jasper", arrayListSearch, parameters);		
		
		if(jobItemizeArray.size()>0){
			JasperPrint jprint2 = rptUtil.getReportPrint(reportDir + "JobSheetItemize.jasper", jobItemizeArray, parameters);		
			List pages = jprint2.getPages();
			for(int i=0;i<pages.size(); i++){
				jprint.addPage((JRPrintPage)pages.get(i));
			}
		}
		// Force Rollback, data need not save to db
		_dbConnection.rollback ();
		return jprint;
	}

	
}
