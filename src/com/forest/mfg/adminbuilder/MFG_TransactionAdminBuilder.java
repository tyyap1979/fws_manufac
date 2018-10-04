package com.forest.mfg.adminbuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.CustomerProfileDef;
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
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_CustProductSelectionDef;
import com.forest.mfg.beandef.MFG_JobSheetDef;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_SupplierProductSelectionDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;

public class MFG_TransactionAdminBuilder extends GenericAdminBuilder {
	private Logger logger = Logger.getLogger (MFG_TransactionAdminBuilder.class);
	
	private String localAccessByShop = null;	
	public MFG_TransactionAdminBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Class defClass, String accessByShop,
			String resources) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, defClass, accessByShop,
				resources);
		
		localAccessByShop = accessByShop;
		
	}
	
	public JSONObject requestJsonHandler() throws Exception {
		JSONObject json = new JSONObject();		
		int stage = 0;
		String jobCode = "";
		
		logger.debug("Master Shop: "+_shopInfoBean.getMasterShopName()+", Selected Shop: "+_shopInfoBean.getSelectedShop());
		if(GeneralConst.CREATE.equals(_reqAction) || GeneralConst.UPDATE.equals(_reqAction)){			
			json = super.requestJsonHandler();
			if(RETURN_VALIDATION.equals(json.get("rc"))){
				return json;
			}
			String status = (String) _attrReqDataMap[0].get(MFG_TransactionDef.status.name);
			stage = CommonUtil.parseInt(status);			
			int key = Integer.parseInt((String)_attrReqDataMap[0].get((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY)));			
			
			// Create Statement 1st
			MFG_StatementAdminBuilder stmtBuilder = new MFG_StatementAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, MFG_StatementDef.class, localAccessByShop, _resources);
			
			if(stage==CommonUtil.parseInt(MFG_SelectBuilder.TRANS_INVOICE)){
				_attrReqDataMap[0].put("GRANDTOTAL", _req.getParameter("grandtotal"));
				stmtBuilder.transCreateStatement(_attrReqDataMap[0]);				
			}else if(stage==CommonUtil.parseInt(MFG_SelectBuilder.TRANS_CANCEL)){				
				cancelOrder((String) _attrReqDataMap[0].get(MFG_TransactionDef.transno.name));
			}
			
			ArrayList keyArray = null;
			// Create JobSheet
			if(stage==CommonUtil.parseInt(MFG_SelectBuilder.TRANS_INVOICE)){
				String factoryJobCode = "";
				String transid = (String) _attrReqDataMap[0].get((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY));
				// Create PO
				
//				if("megatrend".equals(_shopInfoBean.getShopName())){
//					keyArray = createPO(key);
//				}
				// Create Job Sheet
				MFG_JobSheetAdminBuilder jobBuilder = new MFG_JobSheetAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, MFG_JobSheetDef.class, localAccessByShop, _resources);
				String[] keyArrayData = null;
				jobCode = (String)_attrReqDataMap[0].get(MFG_TransactionDef.jobcode.name);
				
//				logger.debug("keyArray.size() = "+keyArray.size()); 
				if(CommonUtil.isEmpty(jobCode)){					
//					if(keyArray!= null && keyArray.size()>0){
//						// Create Factory JobSheet					
//						
//						for(int i=0; i<keyArray.size(); i++){												
//							keyArrayData = (String[]) keyArray.get(i);		
//							logger.debug(keyArrayData[0]+", "+keyArrayData[1]);
//							factoryJobCode += jobBuilder.createJobCode(keyArrayData[0], keyArrayData[1]); 						
//							if(i+1<keyArray.size()){
//								factoryJobCode+=",";
//							}		
//						}
//					}
					
					// Create Own JobSheet				
					jobCode = jobBuilder.createJobCode(_shopInfoBean.getShopName(), transid);	
					jobCode = (jobCode==null)?"":jobCode;
					if(!CommonUtil.isEmpty(factoryJobCode)){
						if(CommonUtil.isEmpty(jobCode)){
							jobCode = factoryJobCode;
						}else{
							jobCode += "," + factoryJobCode;
						}						
					}
				}
				
				if(!CommonUtil.isEmpty(jobCode)){
					_attrReqDataMap[0].put(MFG_TransactionDef.jobcode.name, jobCode);
					if(CommonUtil.isEmpty((String) _attrReqDataMap[0].get(MFG_TransactionDef.salesdate.name))){
						_attrReqDataMap[0].put(MFG_TransactionDef.salesdate.name, DateUtil.getCurrentDate());
					}
//					logger.debug("Json record = "+json.getString("record"));
					if(GeneralConst.CREATE.equals(_reqAction)){ 
						json.put ("record", buildAddRecordReturn (_defClass, (String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY),listingArray, _attrReqDataMap[0]));
					}else{
						json.put ("record", buildAddEditRecord (_defClass, (String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY),listingArray, _attrReqDataMap[0]));
					}
					// Update Job Code to Current Order
					_gs.update(_defClass, addArray,	_attrReqDataMap[0]);
				}				
			}							
		}else{
			json = super.requestJsonHandler();
		}
		return json;
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
		scriptBuffer.append ("var _companyid = '").append (_shopInfoBean.getShopName()).append ("';\n");
		scriptBuffer.append ("var productMainjson = ").append (buildProductJson(null)).append (";\n");
		logger.debug("scriptBuffer = "+scriptBuffer);
		
		scriptBuffer.append ("var productOptionjson = ").append (buildProductOptionJson()).append (";\n");
//		scriptBuffer.append ("var productPricejson;");//.append (buildProductPriceJson()).append (";\n");
		scriptBuffer.append ("var productOptionPriceJson = ").append (buildProductOptionPriceJson(null)).append (";\n");
		
		String customJsPath = ResourceUtil.getCompanyMainPath(_shopInfoBean) + "js/custom.js";
		logger.debug("customJsPath = "+customJsPath);
		scriptBuffer.append(FileHandler.readFile(null, customJsPath));
		scriptBuffer.append ("</script>");
		
		
		buffer.append("<p:useTemplate>simplplan-template.htm</p:useTemplate>").append("\n");
		buffer.append("<p:component value=\"titlebar\">");
		buffer.append("%i18n.system.").append(MODULE_NAME).append(".title% - ");
		buffer.append(_shopInfoBean.getShopNameDesc());
		buffer.append("</p:component>").append("\n");
		
		buffer.append("<p:component value=\"title_right\">");
		buffer.append("<ul id=\"entryButtonBar\" class=\"sorting\" style=\"display: none;\">");
		buffer.append("<li><a href=\"#\" id=\"btnCancel\">Cancel</a></li>");
		buffer.append("<li><a href=\"#\" id=\"btnSave\">Save</a></li>");
		buffer.append("</ul>");
		buffer.append("</p:component>").append("\n");
		
		buffer.append("<p:component value=\"body\">").append("\n");	

			buffer.append("<div id='").append(MODULE_NAME).append("Module' class='box_content'>").append("\n");				
				buffer.append("<div id='searchDiv'>");
				buffer.append ("<form id='").append (GeneralConst.LISTING_FORM).append ("' method='POST'>");
				if("megatrend".equals(_shopInfoBean.getShopName()) || "kdd".equals(_shopInfoBean.getShopName())){
					buffer.append(buildListing(listingArray, MODULE_NAME, true, getControlOptionNoDeleteNoCopy())).append("\n");	
				}else {
					buffer.append(buildListing(listingArray, MODULE_NAME, true, getControlOptionNoDelete())).append("\n");	
				}
					
				buffer.append ("</form>");
			buffer.append("</div>");
		// Add Form
		// Preload product option
			buffer.append("<ul id='preload' style='display:none'>").append(getProductOption()).append("</ul>");
			buffer.append("<div id='entryDiv' style='display: none;overflow:scroll;'>");	
				buffer.append("<div id='").append(MODULE_NAME).append("ModuleEntry' class='entryScreen'>").append("\n");
						
				buffer.append ("<form id='").append (GeneralConst.ENTRY_FORM).append ("' method='POST'>");	
					//buffer.append(buildEntryForm(MODULE_NAME, addArray, columns)).append("\n");	
					buffer.append(buildCustomEntryForm());
					if(subClass!=null){
						// Ignore MFG_TransactionDetailGroupDef
						for(int i=0; i<1; i++){					
							buffer.append (buildDetailEntryForm (subClass[i])).append("<br>");
						}
					}	    		    		  
					buffer.append ("</form>");	
					
				buffer.append("</div>").append("\n");	
			buffer.append("</div>");
		buffer.append(scriptBuffer);		
		buffer.append("</div>").append("\n");
		buffer.append("</p:component>").append("\n");
		
		// Read Additional Content From File
		
		StringBuffer fileContent = FileHandler.readAdminFile (_shopInfoBean, _clientBean, "mfg_transaction.htm");		
		buffer.append(fileContent.toString().replaceAll("%data.item.select%", MFG_SelectBuilder.getSELECT_CUST_PRODUCT(_dbConnection, _shopInfoBean,MFG_TransactionDetailDef.TABLE_PREFIX+MFG_TransactionDetailDef.prodid.name, "").toString()));		
		
		return buffer;
	}
	
	private StringBuffer buildCustomEntryForm(){
		StringBuffer bufferTab = new StringBuffer();
		StringBuffer buffer = new StringBuffer();
		StringBuffer webBuffer = new StringBuffer(); //
		StringBuffer custBuffer = new StringBuffer();
		StringBuffer salesPersonBuffer = MFG_SelectBuilder.getSELECT_AGENT_PROFILE(_dbConnection, _shopInfoBean, "salesby", "");
		salesPersonBuffer.insert(7, " class='validate[required]'");
		
		StringBuffer gstCodeBuffer = SelectBuilder.getSELECT_GST(_dbConnection, _shopInfoBean, "gst_code", "");
		
//		logger.debug("salesPersonBuffer = "+salesPersonBuffer);
		buffer.append("<table width='100%' class='validateForm listTable'>");
		buffer.append("<tr>");
		buffer.append("<th>Transaction No.</th><td><input type='text' id='transno' name='transno' class='' size='20' maxlength='50' value=''  readonly='readonly' /></td>");
		buffer.append("<th>Job Code</th><td><input type='text' id='jobcode' name='jobcode' class='' size='20' maxlength='50' value='' readonly='readonly' /></td>");
		buffer.append("</tr>");
		
		buffer.append("<tr>");
		buffer.append("<th>Customer</th><td><input type='hidden' name='customerid'/><input type='text' id='customerid_value' name='customerid_value' class='validate[required] entryfocus customer-autosuggest' size='30' value=''/></td>");
		buffer.append("<th>Customer Type</th><td><select name='custtype' id='custtype' class=''><option value=''></option><option value='D'>Dealer</option><option value='C'>Client</option><option value='P'>Public</option></select></td>");
		buffer.append("</tr>");
		
		buffer.append("<tr>");
		buffer.append("<th>Customer Ref. No.</th><td><input type='text' id='custrefno' name='custrefno' class='' size='20' maxlength='50' value=''/></td>");
		buffer.append("<th>Attn.</th><td><input type='text' id='attn' name='attn' class='' size='40' maxlength='100' value=''/></td>");
		buffer.append("</tr>");
		
		buffer.append("<tr>");
		buffer.append("<th>Terms</th><td><select name='terms' id='terms' class='validate[required]'><option value=''></option><option value='0'>Cash</option><option value='30'>30 Days</option><option value='45'>45 Days</option><option value='60'>60 Days</option><option value='90'>90 Days</option></select></td>");
		buffer.append("<th> </th><td> </td>");
		buffer.append("</tr>");
		
		buffer.append("<tr>");
		buffer.append("<th>Delivery Detail</th><td><textarea id='comment' name='comment' class='' cols='35' rows='5' ></textarea></td>");
		buffer.append("<th>Sales Date</th><td><input type='text' id='salesdate' name='salesdate' class='datepicker' size='10' maxlength='10' value=''/></td>");
		buffer.append("</tr>");
		
		buffer.append("<tr>");
		buffer.append("<th>Salesperson</th><td>").append(salesPersonBuffer).append("</td>");
		buffer.append("<th>Delivery Date</th><td><input type='text' id='deliverydate' name='deliverydate' class='datepicker' size='10' maxlength='10' value=''/></td>");
		buffer.append("</tr>");
		
		buffer.append("<tr>");
		buffer.append("<th>Tax</th><td>").append(gstCodeBuffer).append("</td>");
		buffer.append("<th>GST Amount</th><td><input type='text' id='gst_amt' name='gst_amt' size='10' maxlength='10' value='' readonly='readonly'/></td>");
		buffer.append("</tr>");
		
		buffer.append("<tr>");
		buffer.append("<th>Status</th><td><select name='status' id='status' class='validate[required]'><option value=''></option><option value='1'>Quotation</option><option value='2'>Order Confirmation</option><option value='3'>Invoice</option><option value='4'>Delivery Order</option><option value='0'>Cancel Order</option></select></td>");
		buffer.append("<th nowrap> </th><td> </td>");
		buffer.append("</tr>");	
		
		
		buffer.append("<tr><td colspan='4'>&nbsp;</td></tr>");
		
		buffer.append("<tr>");
		buffer.append("<th>Discount</th><td><input type='text' name='discount' id='discount'></td>");
		buffer.append("<th>Total</th><td><input type='hidden' name='grandtotal' id='grandtotalinput'><span id='grandtotal'></span></td>");
		buffer.append("</tr>");	
		
		buffer.append("</table>");
		buffer.append("<input type='hidden' id='action1' name='action1' value='' />");
		buffer.append("<input type='hidden' id='customerid_ori' name='customerid_ori' value=''/>");
		buffer.append("<input type='hidden' id='companyid' name='companyid' value=''/>");
		buffer.append("<input type='hidden' id='transid' name='transid' value=''/>");
		buffer.append("<input type='hidden' id='shippingmethod' name='shippingmethod' value=''/>");
		buffer.append("<input type='hidden' id='updateby' name='updateby' value=''/>");
		buffer.append("<input type='hidden' id='updatedate' name='updatedate' value=''/>");
		
//		webBuffer.append("<br>Web Transaction Info.");
		webBuffer.append("<table width='100%' class='validateForm listTable'>");
		webBuffer.append("<tr><th>Web Client</th><td><input type='hidden' name='clientid'/><input type='text' id='clientid_value' name='clientid_value' class='clientuser-autosuggest' size='40' value=''/></td><th></th><td></td></tr>");
		webBuffer.append("<tr><th>Payment Method</th><td><select name='paymentmethod' id='paymentmethod' class=''><option value=''></option><option value='CC'>Credit Card</option><option value='OT'>Online Transfer</option><option value='COD'>Cash On Delivery</option></select></td><th>Shipping or Transportation Charge</th><td><input type='text' id='shippingcharge' name='shippingcharge' class='' size='10' maxlength='9' value=''/></td></tr>");
		
		webBuffer.append("</table>");
		
		custBuffer.append("<table width='100%' class='validateForm listTable'>");
		custBuffer.append("<tr><th>Notes</th><td><textarea id='note' name='note' cols='100' rows='30' readonly></textarea></td></tr>");			
		custBuffer.append("</table>");
		
		bufferTab.append("<div class='tabs'>");
		bufferTab.append("<ul>");
		bufferTab.append("<li><a href='#tabs-1'>General</a></li>");
		bufferTab.append("<li><a href='#tabs-2'>Web Information</a></li>");
		bufferTab.append("<li><a href='#tabs-3'>Customer Information</a></li>");
		bufferTab.append("</ul>");
		
		bufferTab.append("<div id='tabs-1'>");
		bufferTab.append(buffer);
		bufferTab.append("</div>");
		
		bufferTab.append("<div id='tabs-2'>");
		bufferTab.append(webBuffer);
		bufferTab.append("</div>");
		
		bufferTab.append("<div id='tabs-3'>");
		bufferTab.append(custBuffer);
		bufferTab.append("</div>");
		
		bufferTab.append("</div>");
		return bufferTab;
	}

	private String buildProductJson(String prodid){
		JSONObject json = new JSONObject();				
		try{			 		
			json.put("data", getProduct(prodid));	
		}catch(Exception e){
			logger.error(e, e);
		}
		
		return json.toString();
	}
	
	public HashMap getProduct(String prodid){
		HashMap productHash = new HashMap();
		HashMap dataRow = new HashMap();
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{			
			if("megatrend".equals(_shopInfoBean.getShopName()) || "kdd".equals(_shopInfoBean.getShopName())){
				query.append("Select b.").append(MFG_CustProductDef.prodid).append(",");
				query.append(" b.").append(MFG_CustProductDef.sellunittype).append(",b.").append(MFG_CustProductDef.minorder).append(",");
				query.append(" b.").append(MFG_CustProductDef.customise).append(", b.").append(MFG_CustProductDef.name).append(",");
				query.append(" b.").append(MFG_CustProductDef.customformula).append(", 'Y' as factory");  
				query.append(" From ").append(MFG_SupplierProductDef.TABLE).append(" a"); 
				query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b on b.").append(MFG_CustProductDef.prodid).append(" = a.").append(MFG_SupplierProductDef.prodid);
	//			query.append(" a.").append(MFG_SupplierProductDef.companyid).append(" = ?"); // Need to show deleted Product also
				if(!CommonUtil.isEmpty(prodid)){
					query.append(" Where And a.").append(MFG_CustProductDef.prodid).append("='").append(prodid).append("'");
				}
				
				query.append(" union");
			}
			query.append(" Select ").append(MFG_CustProductDef.prodid).append(", ").append(MFG_CustProductDef.sellunittype);
			query.append(", ").append(MFG_CustProductDef.minorder).append(", ").append(MFG_CustProductDef.customise);
			query.append(", ").append(MFG_CustProductDef.name).append(", ").append(MFG_CustProductDef.customformula);
			query.append(", 'N' as factory");  
			query.append(" From ").append(MFG_CustProductDef.TABLE);
			query.append(" Where ").append(MFG_CustProductDef.companyid).append(" = ? And ").append(MFG_CustProductDef.status).append("!='").append(GeneralConst.DELETED).append("'");
			if(!CommonUtil.isEmpty(prodid)){
				query.append(" And ").append(MFG_CustProductDef.prodid).append("='").append(prodid).append("'");
			}
			query.append(" Order By ").append(MFG_CustProductDef.name);
			
			pstmt = _dbConnection.prepareStatement(query.toString());
			pstmt.setString(1, _shopInfoBean.getShopName());
//			pstmt.setString(2, _shopInfoBean.getShopName());
			rs = pstmt.executeQuery();
			
			dataRow = new HashMap();
			dataRow.put(MFG_CustProductDef.prodid.name, "0");
			dataRow.put(MFG_CustProductDef.sellunittype.name, "");
			dataRow.put(MFG_CustProductDef.minorder.name, "0");
			dataRow.put(MFG_CustProductDef.customise.name, "");
			dataRow.put(MFG_CustProductDef.name.name, "[Custom Product]");
			dataRow.put(MFG_CustProductDef.customformula.name, "");
			productHash.put("prod0", dataRow);
			
			while(rs.next()){
				dataRow = new HashMap();
				dataRow.put(MFG_CustProductDef.prodid.name, rs.getString(MFG_CustProductDef.prodid.name));
				dataRow.put(MFG_CustProductDef.sellunittype.name, rs.getString(MFG_CustProductDef.sellunittype.name));
				dataRow.put(MFG_CustProductDef.minorder.name, rs.getString(MFG_CustProductDef.minorder.name));
				dataRow.put(MFG_CustProductDef.customise.name, rs.getString(MFG_CustProductDef.customise.name));
				dataRow.put(MFG_CustProductDef.name.name, rs.getString(MFG_CustProductDef.name.name));
				dataRow.put(MFG_CustProductDef.customformula.name, rs.getString(MFG_CustProductDef.customformula.name));
				dataRow.put("factory", rs.getString("factory"));
				
//				logger.debug("getProduct dataRow = "+dataRow);
				productHash.put("prod"+rs.getString(MFG_CustProductDef.prodid.name), dataRow);
			}
			
		}catch(Exception e){
			logger.error(e, e);
		}
		
		return productHash;
	}
	
	private String buildProductOptionJson(){
		JSONObject json = new JSONObject();				
		HashMap productHash = new HashMap();				
		Class subClass = MFG_CustProductSelectionDef.class;
		try{			 					
			addArray = BeanDefUtil.getArrayList(subClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);		 		
			searchArray = BeanDefUtil.getArrayList(subClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
			_attrReqDataMap = new HashMap[1];	
			_attrReqDataMap[0] = new HashMap();
			_attrReqDataMap[0].put(BaseDef.COMPANYID, _shopInfoBean.getShopName());
			arrayListSearch = (ArrayList) _gs.search(subClass, null, searchArray, addArray, _attrReqDataMap, MFG_CustProductSelectionDef.prodid.name + ", " + MFG_CustProductSelectionDef.position.name, "*", _reqAction).get(1);
			buildOption(arrayListSearch, productHash);
			
			
			// Supplier product option
			subClass = MFG_SupplierProductSelectionDef.class;
			addArray = BeanDefUtil.getArrayList(subClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);		 		
			searchArray = BeanDefUtil.getArrayList(subClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
			_attrReqDataMap = new HashMap[1];	
			_attrReqDataMap[0] = new HashMap();
			_attrReqDataMap[0].put(BaseDef.COMPANYID, _shopInfoBean.getShopName());
//			_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, 
//					searchArray, _accessByCompany);
			
			arrayListSearch = (ArrayList) _gs.search(subClass, null, searchArray, addArray, _attrReqDataMap, MFG_CustProductSelectionDef.prodid.name + ", " + MFG_CustProductSelectionDef.position.name, "*", _reqAction).get(1);
			buildOption(arrayListSearch, productHash);
		
			json.put("data", productHash);
			logger.debug("buildProductOptionJson productHash = "+productHash);
		}catch(Exception e){
			logger.error(e, e);
		}
		return json.toString();
	}
	
	private void buildOption(ArrayList arrayListSearch, HashMap productHash){
		HashMap dataRow = null;		
		String prodid = null;
		int count = 0;
		StringBuffer prodBuffer = new StringBuffer();
		count = arrayListSearch.size();
		for(int i=0; i<count; i++){
			dataRow = (HashMap) arrayListSearch.get(i);		
			if(prodid!=null && !prodid.equals((String) dataRow.get(MFG_CustProductSelectionDef.prodid.name))){
				if(prodBuffer.length()>0){
					if(prodBuffer.charAt(prodBuffer.length()-1)==','){
						productHash.put("prod"+prodid, prodBuffer.substring(0, prodBuffer.length()-1));		
					}else{
						productHash.put("prod"+prodid, prodBuffer);	
					}
					
				}
				
				prodBuffer = new StringBuffer();						
			}
			prodBuffer.append(dataRow.get(MFG_CustProductSelectionDef.prodoptid.name)).append(",");
		
			prodid = (String) dataRow.get(MFG_CustProductSelectionDef.prodid.name);					
		}
		if(prodBuffer.length()>0){
			if(prodBuffer.charAt(prodBuffer.length()-1)==','){
				productHash.put("prod"+prodid, prodBuffer.substring(0, prodBuffer.length()-1));		
			}else{
				productHash.put("prod"+prodid, prodBuffer);	
			}
		}
	}
	
	public String buildProductOptionPriceJson(String prodoptid){
		JSONObject json = new JSONObject();			
		try{			 						
			json = new JSONObject(getProductOptionHash(prodoptid));			
			logger.debug("buildProductOptionPriceJson json = "+json);
		}catch(Exception e){
			logger.error(e, e);
		}
		return json.toString();
	}
	
	private HashMap getProductOptionHash(String prodoptid){
		HashMap dataMap = null;
		int count=0;
		HashMap dataRow = null;
		HashMap productHash = new HashMap();		
		String prodoptdetailid = null;		
		Class[] subClass = {MFG_ProductOptionDetailDef.class, MFG_SupplierProductOptionDetailDef.class};
		ArrayList addArray = null;
		ArrayList searchArray = null;
		HashMap[] attrReqDataMap = null;
		ArrayList arrayListSearch = null;
		StringBuffer additionalBuffer = null;
		int len = 0;
		
		try{	
			if("megatrend".equals(_shopInfoBean.getShopName())){
				len = 2;
			}else{
				len = 1;
			}
			for(int a=0; a<len; a++){
				addArray = BeanDefUtil.getArrayList(subClass[a], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);		 		
				searchArray = BeanDefUtil.getArrayList(subClass[a], _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
				attrReqDataMap = new HashMap[1];	
				attrReqDataMap[0] = new HashMap();
				attrReqDataMap[0].put(BaseDef.COMPANYID, _shopInfoBean.getShopName());
				
				additionalBuffer = new StringBuffer();
				if(!CommonUtil.isEmpty(prodoptid)){	
					if(subClass[a]==MFG_ProductOptionDetailDef.class){
						additionalBuffer.append(MFG_ProductOptionDetailDef.prodoptid.name).append(" In (").append(prodoptid).append(")");
					}else{
						additionalBuffer.append("a.").append(MFG_ProductOptionDetailDef.prodoptid.name).append(" In (").append(prodoptid).append(")");
					}
				}
				
				arrayListSearch = (ArrayList) _gs.search(subClass[a], additionalBuffer, searchArray, addArray, attrReqDataMap, 
						MFG_ProductOptionDetailDef.prodoptid.name + ", " + MFG_ProductOptionDetailDef.name.name, "*", _reqAction).get(1);
				count = arrayListSearch.size();
				for(int i=0; i<count; i++){
					dataRow = (HashMap) arrayListSearch.get(i);	
					dataMap = new HashMap();
					dataMap.put(MFG_ProductOptionDetailDef.name.name, (String) dataRow.get(MFG_ProductOptionDetailDef.name.name));
					dataMap.put(MFG_ProductOptionDetailDef.cost.name, (String) dataRow.get(MFG_ProductOptionDetailDef.cost.name));
					dataMap.put(MFG_ProductOptionDetailDef.dealerprice.name, (String) dataRow.get(MFG_ProductOptionDetailDef.dealerprice.name));
					dataMap.put(MFG_ProductOptionDetailDef.clientprice.name, (String) dataRow.get(MFG_ProductOptionDetailDef.clientprice.name));
					dataMap.put(MFG_ProductOptionDetailDef.publicprice.name, (String) dataRow.get(MFG_ProductOptionDetailDef.publicprice.name));					
					dataMap.put(MFG_ProductOptionDetailDef.formula.name, (String) dataRow.get(MFG_ProductOptionDetailDef.formula.name));
					dataMap.put(MFG_ProductOptionDetailDef.sellunittype.name, (String) dataRow.get(MFG_ProductOptionDetailDef.sellunittype.name));				
					prodoptdetailid = (String) dataRow.get(MFG_ProductOptionDetailDef.prodoptdetailid.name);
					productHash.put("optdtl"+prodoptdetailid, dataMap);		
//					logger.debug("getProductOptionHash "+prodoptdetailid+", dataMap = "+dataMap);
				}
			}
			
		}catch(Exception e){
			logger.error(e, e);
		}
		
		return productHash;
	}
	
//	private String buildProductPriceJson(){
//		JSONObject json = new JSONObject();				
//		int count=0;
//		HashMap dataRow = null;
//		HashMap productHash = new HashMap();
//		HashMap productPriceHash = new HashMap();
//		String prodid = null;
//		ArrayList arrList = new ArrayList();
//		StringBuffer[] criteriaBuffer = new StringBuffer[2];
//		Class[] subClass = {MFG_CustProductPriceDef.class, MFG_SupplierProductPriceDef.class};
//		try{			 
//			criteriaBuffer[0] = new StringBuffer("prodid in (Select prodid From ");
//			criteriaBuffer[0].append(MFG_CustProductDef.TABLE);
//			criteriaBuffer[0].append(" Where ").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
//			criteriaBuffer[0].append(" And ").append(BaseDef.STATUS).append("='").append(GeneralConst.ACTIVE).append("'");
//			criteriaBuffer[0].append(")");
//			
//			criteriaBuffer[1] = new StringBuffer("prodid in (Select prodid From ");
//			criteriaBuffer[1].append(MFG_SupplierProductDef.TABLE);
//			criteriaBuffer[1].append(" Where ").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
//			criteriaBuffer[1].append(" And ").append(BaseDef.STATUS).append("='").append(GeneralConst.ACTIVE).append("'");
//			criteriaBuffer[1].append(")");
//			
//			for(int a=0; a<subClass.length; a++){
//				arrList = new ArrayList();				
//				
//				addArray = BeanDefUtil.getArrayList(subClass[a], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);		 		
//				searchArray = BeanDefUtil.getArrayList(subClass[a], _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
//				_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, 
//						searchArray, (_accessByShop?_shopInfoBean.getShopName():null));
//				
//				arrayListSearch = (ArrayList) _gs.search(subClass[a], criteriaBuffer[a], searchArray, addArray, _attrReqDataMap, 
//						MFG_CustProductPriceDef.prodid.name + ", " + MFG_CustProductPriceDef.orderfrom.name, "*", _reqAction).get(1);
//				count = arrayListSearch.size();
//				
//				prodid = null;
//				for(int i=0; i<count; i++){
//					dataRow = (HashMap) arrayListSearch.get(i);		
////					logger.debug("---- dataRow = "+dataRow);
//					if(prodid!=null && !prodid.equals((String) dataRow.get(MFG_CustProductPriceDef.prodid.name))){	
//						productHash.put("prod"+prodid, arrList);
//						logger.debug("buildProductPriceJson Last, prodid = "+prodid+", arrList="+arrList);	
//						arrList = new ArrayList();					
//					}
//					productPriceHash = new HashMap();					
//					productPriceHash.put(MFG_CustProductPriceDef.orderfrom.name, dataRow.get(MFG_CustProductPriceDef.orderfrom.name));
//					productPriceHash.put(MFG_CustProductPriceDef.orderto.name, dataRow.get(MFG_CustProductPriceDef.orderto.name));
//					productPriceHash.put(MFG_CustProductPriceDef.cost.name, dataRow.get(MFG_CustProductPriceDef.cost.name));
//					productPriceHash.put(MFG_CustProductPriceDef.dealerprice.name, dataRow.get(MFG_CustProductPriceDef.dealerprice.name));
//					productPriceHash.put(MFG_CustProductPriceDef.clientprice.name, dataRow.get(MFG_CustProductPriceDef.clientprice.name));
//					productPriceHash.put(MFG_CustProductPriceDef.publicprice.name, dataRow.get(MFG_CustProductPriceDef.publicprice.name));
////					productPriceHash.put(MFG_CustProductPriceDef.dealer1price.name, dataRow.get(MFG_CustProductPriceDef.dealer1price.name));
////					productPriceHash.put(MFG_CustProductPriceDef.client1price.name, dataRow.get(MFG_CustProductPriceDef.client1price.name));
////					productPriceHash.put(MFG_CustProductPriceDef.public1price.name, dataRow.get(MFG_CustProductPriceDef.public1price.name));
////					productPriceHash.put(MFG_CustProductPriceDef.dealer2price.name, dataRow.get(MFG_CustProductPriceDef.dealer2price.name));
////					productPriceHash.put(MFG_CustProductPriceDef.client2price.name, dataRow.get(MFG_CustProductPriceDef.client2price.name));
////					productPriceHash.put(MFG_CustProductPriceDef.public2price.name, dataRow.get(MFG_CustProductPriceDef.public2price.name));
////					logger.debug("buildProductPriceJson productPriceHash = "+productPriceHash);
//					arrList.add(productPriceHash);
//					
//					prodid = (String) dataRow.get(MFG_CustProductPriceDef.prodid.name);					
//				}
//				if(arrList.size()>0){
//					logger.debug("buildProductPriceJson Last, prodid = "+prodid+", arrList="+arrList);					
//					productHash.put("prod"+prodid, arrList);					
//				}
//			}
////			logger.debug("productHash = "+productHash);
//			json = new JSONObject(productHash);			
//		}catch(Exception e){
//			logger.error(e, e);
//		}
//		return json.toString();
//	}
		
	
	private StringBuffer getProductOption(){
		StringBuffer buffer = new StringBuffer();		
		int count=0;
		HashMap dataRow = null;
		String prodoptid = null;	
		StringBuffer selectBuffer = null;
		
		StringBuffer query = new StringBuffer();
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		try{
			query.append("Select ");
			query.append(MFG_ProductOptionDef.prodoptid);
			query.append(" From ").append(MFG_ProductOptionDef.TABLE);
			query.append(" Where ").append(BaseDef.COMPANYID).append("='").append(_shopInfoBean.getShopName()).append("'");
			query.append(" And ").append(BaseDef.STATUS).append("='").append(GeneralConst.ACTIVE).append("'");
			
			query.append(" Union");
			query.append(" Select ");
			query.append(MFG_SupplierProductOptionDef.prodoptid);
			query.append(" From ").append(MFG_SupplierProductOptionDef.TABLE);
			query.append(" Where ").append(BaseDef.STATUS).append("='").append(GeneralConst.ACTIVE).append("'");
			query.append(" Group By ");
			query.append(MFG_ProductOptionDef.prodoptid);
			
			
			logger.debug("getProductOption query = "+query);
			arrayListSearch = gs.searchDataArray(query);
		
			count = arrayListSearch.size();
			for(int i=0; i<count; i++){
				dataRow = (HashMap) arrayListSearch.get(i);
				
				logger.debug("getProductOption dataRow = "+dataRow);
				prodoptid = (String) dataRow.get(MFG_CustProductSelectionDef.prodoptid.name);				
				selectBuffer = MFG_SelectBuilder.getSELECT_PRODUCT_OPTION(_dbConnection, _shopInfoBean, 
						MFG_ProductOptionDef.prodoptid.name, "", prodoptid);
						
				buffer.append("<li id='opt").append(prodoptid).append("'>").append(selectBuffer);
				buffer.append("</li>");
			}
			
		}catch(Exception e){
			logger.error(e, e);
		}
		return buffer;
	}
	
	private ArrayList createPO(int transid){
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String suppCompanyId = "";
		int key = 0;
		HashMap[] dataDetailMap = new HashMap[1];
		ArrayList keyArray = new ArrayList();
		String[] keyArrayData = null;
		HashMap reqDataMap = null;
		
		StringBuffer customerQuery = null;
		ArrayList customerArray = null;
		HashMap customerHash = null;
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		
		try{
			reqDataMap = new HashMap(_attrReqDataMap[0]);
			logger.debug("createPO transid = "+transid);
			addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			
			// Delete All Existing Order At Supplier Site
			query = new StringBuffer();
			query.append("select a.").append(MFG_TransactionDef.transno);
			query.append(",b.*");
			query.append(",c.").append(MFG_SupplierProductDef.suppcompanyid);
			query.append(",d.").append(MFG_TransactionDef.transno).append(" supptransno");
			query.append(",d.").append(MFG_TransactionDef.transid).append(" supptransid");
			query.append(",e.").append(MFG_TransactionDetailDef.transdetailid).append(" as supptransdetailid");
			query.append(",e.").append(MFG_TransactionDetailDef.custtransdetailid);
									
			query.append(" from ").append(MFG_TransactionDef.TABLE).append(" a");
			query.append(" Inner Join ").append(MFG_TransactionDetailDef.TABLE).append(" b On b.").append(MFG_TransactionDetailDef.transid).append(" = a.").append(MFG_TransactionDef.transid);
			query.append(" Inner Join ").append(MFG_SupplierProductDef.TABLE).append(" c on c.").append(MFG_SupplierProductDef.prodid).append(" = b.").append(MFG_TransactionDetailDef.prodid).append("");
			query.append(" Left Join ").append(MFG_TransactionDef.TABLE).append(" d On d.").append(MFG_TransactionDef.custrefno).append(" = a.").append(MFG_TransactionDef.transno);
			query.append(" And d.").append(MFG_TransactionDef.companyid).append(" = c.").append(MFG_SupplierProductDef.suppcompanyid);
			query.append(" Left Join ").append(MFG_TransactionDetailDef.TABLE).append(" e On e.").append(MFG_TransactionDetailDef.transid).append(" =d.").append(MFG_TransactionDef.transid);
			query.append(" And e.").append(MFG_TransactionDetailDef.custtransdetailid).append(" = b.").append(MFG_TransactionDetailDef.transdetailid).append("");
			query.append(" Where b.").append(MFG_TransactionDetailDef.transid).append(" = ?");
			query.append(" Order by c.").append(MFG_SupplierProductDef.suppcompanyid);
			query.append(", b.").append(MFG_TransactionDetailDef.transdetailid);
			logger.debug("createPO query = "+query);
			pstmt = _dbConnection.prepareStatement(query.toString());			
			pstmt.setInt(1, transid);
			rs = pstmt.executeQuery();
			logger.debug("Start Create PO-----------------------------------------------------------------------------");
			reqDataMap.put(MFG_TransactionDef.comment.name, reqDataMap.get(MFG_TransactionDef.comment.name) + "("+ reqDataMap.get(MFG_TransactionDef.customerid.name+"_value") + ")");
			while(rs.next()){
				logger.debug("-----"+suppCompanyId + ", "+rs.getString("suppcompanyid"));
				
				if(!suppCompanyId.equals(rs.getString("suppcompanyid"))){
					// New Company	
					suppCompanyId = rs.getString("suppcompanyid");
					
					reqDataMap.put(BaseDef.COMPANYID, suppCompanyId);
					reqDataMap.put(MFG_TransactionDef.custrefno.name, rs.getString(MFG_TransactionDef.transno.name));
					
					customerQuery = new StringBuffer("Select ");
					customerQuery.append(CustomerProfileDef.customerid).append(",").append(CustomerProfileDef.type).append(",");
					customerQuery.append(CustomerProfileDef.terms).append(",");
					customerQuery.append(CustomerProfileDef.contactperson);
					customerQuery.append(" From ").append(CustomerProfileDef.TABLE);
					customerQuery.append(" Where ").append(CustomerProfileDef.code).append("='").append(_shopInfoBean.getShopName()).append("'");
					customerQuery.append(" And ").append(CustomerProfileDef.companyid).append("='").append(suppCompanyId).append("'");
					customerQuery.append(" And ").append(CustomerProfileDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
					customerArray = gs.searchDataArray(customerQuery);
				
					if(customerArray!=null){
						customerHash = (HashMap) customerArray.get(0);
						logger.debug("customerHash = "+customerHash);
						
						reqDataMap.put(MFG_TransactionDef.customerid.name, customerHash.get(CustomerProfileDef.customerid.name));
						reqDataMap.put(MFG_TransactionDef.custtype.name, customerHash.get(CustomerProfileDef.type.name));
						reqDataMap.put(MFG_TransactionDef.terms.name, customerHash.get(CustomerProfileDef.terms.name));
						reqDataMap.put(MFG_TransactionDef.attn.name, customerHash.get(CustomerProfileDef.contactperson.name));						
					}
					if(rs.getString("supptransid")!=null){
						reqDataMap.put(MFG_TransactionDef.transid.name, rs.getString("supptransid"));
						reqDataMap.put(MFG_TransactionDef.transno.name, rs.getString("supptransno"));												
						
						_gs.update(_defClass, addArray, reqDataMap);
						key = rs.getInt("supptransid");
						logger.debug(" Update Existing PO key = "+key);
					}else{
						logger.debug("suppCompanyId = "+suppCompanyId+", reqDataMap = "+reqDataMap);
						key = _gs.create(_defClass, addArray, reqDataMap);
						logger.debug(" Create New PO key = "+key);
						keyArrayData = new String[2];
						keyArrayData[0] = suppCompanyId;
						keyArrayData[1] = String.valueOf(key);
						keyArray.add(keyArrayData);
					}										
				}
				suppCompanyId = rs.getString("suppcompanyid");	
				
				dataDetailMap[0] = new HashMap();
				dataDetailMap[0].put(MFG_TransactionDetailDef.transid.name, String.valueOf(key));
				dataDetailMap[0].put(MFG_TransactionDetailDef.prodid.name, rs.getString(MFG_TransactionDetailDef.prodid.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.custtransdetailid.name, rs.getString(MFG_TransactionDetailDef.custtransdetailid.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.measurement.name, rs.getString(MFG_TransactionDetailDef.measurement.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.width.name, rs.getString(MFG_TransactionDetailDef.width.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.height.name, rs.getString(MFG_TransactionDetailDef.height.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.depth.name, rs.getString(MFG_TransactionDetailDef.depth.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.qty.name, rs.getString(MFG_TransactionDetailDef.qty.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.unit.name, rs.getString(MFG_TransactionDetailDef.unit.name));
				
				dataDetailMap[0].put(MFG_TransactionDetailDef.opt1.name, rs.getString(MFG_TransactionDetailDef.opt1.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.opt2.name, rs.getString(MFG_TransactionDetailDef.opt2.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.opt3.name, rs.getString(MFG_TransactionDetailDef.opt3.name));
				dataDetailMap[0].put(MFG_TransactionDetailDef.status.name, rs.getString(MFG_TransactionDetailDef.status.name));
				
				dataDetailMap[0].put(MFG_TransactionDetailDef.customisedata.name, rs.getString(MFG_TransactionDetailDef.customisedata.name));
				
				if(rs.getString("status").equals(GeneralConst.DELETED)){
					dataDetailMap[0].put(MFG_TransactionDetailDef.transdetailid.name, rs.getString("supptransdetailid"));
					dataDetailMap[0].put("row-status", "D");
					
					logger.debug(" Delete Detail key = "+rs.getString("supptransdetailid"));
				}else{
					if(rs.getString("supptransdetailid")!=null){
						dataDetailMap[0].put(MFG_TransactionDetailDef.transdetailid.name, rs.getString("supptransdetailid"));
						dataDetailMap[0].put(MFG_TransactionDetailDef.cost.name, "0");
						dataDetailMap[0].put(MFG_TransactionDetailDef.price.name, "0");
						dataDetailMap[0].put(MFG_TransactionDetailDef.discount.name, "0");
						dataDetailMap[0].put(MFG_TransactionDetailDef.sellsubtotal.name, "0");
						dataDetailMap[0].put(MFG_TransactionDetailDef.costsubtotal.name, "0");
						dataDetailMap[0].put("row-status", "U");
						logger.debug(" Update dataDetailMap[0] = "+dataDetailMap[0]);
						logger.debug(" Update Detail key = "+rs.getString("supptransdetailid"));
					}else{						
						dataDetailMap[0].put("custtransdetailid", rs.getString("transdetailid"));
						dataDetailMap[0].put("row-status", "N");
						logger.debug(" Create dataDetailMap[0] = "+dataDetailMap[0]);						
					}
				}
				
				_gs.updateDetail(MFG_TransactionDetailDef.class, dataDetailMap, 
						BeanDefUtil.getKeyObject(MFG_TransactionDetailDef.class), 
						BeanDefUtil.getArrayList(MFG_TransactionDetailDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
			}			
			
			logger.debug("End Create PO---------------------------------------------------------------");
			// Create Group 
		}catch (Exception e) {
			logger.error(e, e);
		}
		
		return keyArray;
	}
		
	private void cancelOrder(String transno)throws Exception{
		PreparedStatement pstmt = null;		
		StringBuffer query = new StringBuffer();		
		ArrayList dataArray = null;
		HashMap rowMap = null;
		
		try{
			MFG_StatementAdminBuilder stmtBuilder = new MFG_StatementAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, MFG_StatementDef.class, localAccessByShop, _resources);			
			
			// Select all statement for this invoice
			query.append("Select ").append(MFG_TransactionDef.transid);
			query.append(" From ").append(MFG_TransactionDef.TABLE);
			query.append(" Where (").append(MFG_TransactionDef.transno).append(" = '").append(transno).append("'");
			query.append(" Or ").append(MFG_TransactionDef.custrefno).append(" = '").append(transno).append("')");
			if("megatrend".equals(_shopInfoBean.getShopName())){
				query.append(" And ").append(MFG_TransactionDef.companyid).append(" In ('megatrend','eurotrend')");
			}else{
				query.append(" And ").append(MFG_TransactionDef.companyid).append(" = '").append(_shopInfoBean.getShopName()).append("'");
			}
			
			dataArray = _gs.searchDataArray(query);
			int size = dataArray.size();
			String[] transidArray = new String[size];
			for(int i=0; i<size; i++){
				rowMap = (HashMap) dataArray.get(i);
				transidArray[i] = (String) rowMap.get(MFG_TransactionDef.transid.name);
			}
			
			// Update Factory Invoice. For Megatrend use only
			query = new StringBuffer();
			query.append("Update ").append(MFG_TransactionDef.TABLE).append(" Set ").append(MFG_TransactionDef.status).append("=?");
			query.append(" Where ").append(MFG_TransactionDef.transid).append(" In (").append(DBUtil.arrayToString(transidArray, MFG_TransactionDef.transid)).append(")");
			
			pstmt = _dbConnection.prepareStatement(query.toString());
			pstmt.setString(1, MFG_SelectBuilder.TRANS_CANCEL);			
			pstmt.executeUpdate();
			
			// Delete All Statement By TRANSID
			stmtBuilder.transDeleteStatement(transidArray);
			
//			// Select all transid for this transaction
//			query = new StringBuffer();
//			query.append("Select ").append(MFG_TransactionDef.transid).append(" From ");
//			query.append(MFG_TransactionDef.TABLE).append(" Where ");
//			query.append(MFG_TransactionDef.transno).append("=? ");
//			query.append(" Or ").append(MFG_TransactionDef.custrefno).append("=?");
//			logger.debug("Select all transaction query = "+query);
//			pstmt = _dbConnection.prepareStatement(query.toString());
//			pstmt.setString(1, transno);
//			pstmt.setString(2, transno);
//			rs = pstmt.executeQuery();
//			while(rs.next()){
//				ids.append(rs.getString(MFG_TransactionDef.transid.name)).append(",");
//			}
//			// Remove last char (,)
//			if(ids.length()>0){
//				ids.deleteCharAt(ids.length());
//			}
//			
//			// Update sellsubtotal and costsubtotal to 0
//			query = new StringBuffer();
//			query.append("Update ").append(MFG_TransactionDetailDef.TABLE).append(" Set ").append(MFG_TransactionDetailDef.sellsubtotal).append("=0");
//			query.append(MFG_TransactionDetailDef.costsubtotal).append("=0");
//			query.append(" Where ").append(MFG_TransactionDetailDef.transid).append(" In (").append(ids).append(")");
//			logger.debug("Update sellsubtotal query = "+query);
//			pstmt = _dbConnection.prepareStatement(query.toString());
//			pstmt.executeUpdate();			
		}catch(Exception e){
			throw e;
		}finally{
			DBUtil.free(null, pstmt, null);
		}
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
		HashMap parameters = new HashMap();
		Map dataMap = null;
		Map termMap = MFG_SelectBuilder.getHASH_TERM(_dbConnection, _shopInfoBean);
		Map agentMap = MFG_SelectBuilder.getHASH_AGENT_PROFILE(_dbConnection, _shopInfoBean);
//		String measurement = null;
		String transType = null;
		java.util.Date salesDate = null;
		logger.debug("termMap = "+termMap);
		subClass = BeanDefUtil.getSubClass(_defClass);
		
		GenericServices gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		ArrayList addArray = null;
		ArrayList searchArray = null;
				
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
		_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, searchArray, _accessByCompany);
		
		arrayListSearch = (ArrayList)_gs.search(_defClass, null, searchArray, addArray, _attrReqDataMap, null, "1", _reqAction).get(1);				
		if(arrayListSearch.size()>0){
			parameters = (HashMap) arrayListSearch.get(0);			
			String salesBy = (String) parameters.get(MFG_TransactionDef.salesby.name);
			salesDate = DateUtil.getDate((String)parameters.get(MFG_TransactionDef.salesdate.name));
			parameters.put(MFG_TransactionDef.terms.name, termMap.get(parameters.get(MFG_TransactionDef.terms.name)));
			parameters.put(MFG_TransactionDef.salesby.name, ((HashMap)agentMap.get(salesBy)).get(salesBy));
			transType = (String)parameters.get(MFG_TransactionDef.status.name);						
		}
		
		// Get Customer Info
		CustomerProfileAdminBuilder cpBuilder = new CustomerProfileAdminBuilder(_shopInfoBean, _clientBean, _dbConnection, _req, _resp, null, null, null);
		parameters.putAll(cpBuilder.getCustomerInfo((String) parameters.get(MFG_TransactionDef.customerid.name)));
		
		// Get Transaction Detail
		if(subClass!=null){										
			StringBuffer query = new StringBuffer();
			query.append("Select a.").append(MFG_TransactionDetailDef.transdetailid);
			query.append(",a.").append(MFG_TransactionDetailDef.transid);
			query.append(",a.").append(MFG_TransactionDetailDef.custtransdetailid);
			query.append(",a.").append(MFG_TransactionDetailDef.customisedata);
			query.append(",a.").append(MFG_TransactionDetailDef.prodid);
			query.append(",a.").append(MFG_TransactionDetailDef.measurement);
			query.append(",a.").append(MFG_TransactionDetailDef.width);
			query.append(",a.").append(MFG_TransactionDetailDef.depth);
			query.append(",a.").append(MFG_TransactionDetailDef.height);
			query.append(",a.").append(MFG_TransactionDetailDef.unit);
			query.append(",a.").append(MFG_TransactionDetailDef.qty);
			query.append(",a.").append(MFG_TransactionDetailDef.cost);
			query.append(",a.").append(MFG_TransactionDetailDef.price);
			query.append(",a.").append(MFG_TransactionDetailDef.discount);
			query.append(",a.").append(MFG_TransactionDetailDef.costsubtotal);
			query.append(",a.").append(MFG_TransactionDetailDef.sellsubtotal);
			query.append(",a.").append(MFG_TransactionDetailDef.opt1);
			query.append(",a.").append(MFG_TransactionDetailDef.opt2);
			query.append(",a.").append(MFG_TransactionDetailDef.opt3);
			query.append(",a.").append(MFG_TransactionDetailDef.opt4);
			query.append(",a.").append(MFG_TransactionDetailDef.status);			
			query.append(",a.").append(MFG_TransactionDetailDef.prodname);
			query.append(",a.").append(MFG_TransactionDetailDef.remark);
			query.append(",b.").append(MFG_CustProductDef.name);
			query.append(",b.").append(MFG_CustProductDef.sellunittype);
			query.append(",c.").append(MFG_ProductOptionDetailDef.name).append(" opt1_name");
			query.append(",d.").append(MFG_ProductOptionDetailDef.name).append(" opt2_name");
			query.append(",e.").append(MFG_ProductOptionDetailDef.name).append(" opt3_name");
			query.append(",c1.").append(MFG_ProductOptionDef.groupname).append(" opt1_groupname");
			query.append(",d1.").append(MFG_ProductOptionDef.groupname).append(" opt2_groupname");
			query.append(",e1.").append(MFG_ProductOptionDef.groupname).append(" opt3_groupname");			
			
			query.append(" From ").append(MFG_TransactionDetailDef.TABLE).append(" a ");			
			query.append(" Left Join ").append(MFG_CustProductDef.TABLE).append(" b on b.").append(MFG_CustProductDef.prodid).append(" = a.").append(MFG_TransactionDetailDef.prodid);
			
			query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" c on c.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_TransactionDetailDef.opt1);			
			query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" c1 on c1.").append(MFG_ProductOptionDef.prodoptid).append(" = c.").append(MFG_ProductOptionDetailDef.prodoptid);
			
			query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" d on d.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_TransactionDetailDef.opt2);			
			query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" d1 on d1.").append(MFG_ProductOptionDef.prodoptid).append(" = d.").append(MFG_ProductOptionDetailDef.prodoptid);
			
			query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" e on e.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_TransactionDetailDef.opt3);			
			query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" e1 on e1.").append(MFG_ProductOptionDef.prodoptid).append(" = e.").append(MFG_ProductOptionDetailDef.prodoptid);
			
			query.append(" Where a.").append(MFG_TransactionDetailDef.transid).append(" = ").append(parameters.get(MFG_TransactionDef.transid.name));
			query.append(" And a.").append(MFG_TransactionDetailDef.status).append("!='").append(GeneralConst.DELETED).append("'");
			query.append(" Order By a.").append(MFG_TransactionDetailDef.prodid);
			query.append(",a.").append(MFG_TransactionDetailDef.opt1).append(",a.");
			query.append(MFG_TransactionDetailDef.opt2);
			query.append(",a.").append(MFG_TransactionDetailDef.opt3);
			query.append(",a.").append(MFG_TransactionDetailDef.position);
			logger.debug("buildReport query = "+query);
			arrayListSearch = gs.searchDataArray(query);
//			addArray = BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _clientBean);
//			searchArray = BeanDefUtil.getArrayList(subClass[0], _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _clientBean);
//			
//			arrayListSearch = (ArrayList) _gs.search(subClass[0], null, searchArray, addArray, _attrReqDataMap, null, "*", _reqAction).get(1);	
			
			HashMap prodMap = getProduct(null);
//			HashMap optMap = MFG_SelectBuilder.getHASH_PRODUCT_OPTION(_dbConnection, _shopInfoBean);
//			logger.debug(" ========== prodMap = "+prodMap);
//			logger.debug(" ----------- optMap = "+optMap);
			
			
			String prodGroupDesc = null;
			StringBuffer prodDesc = null; 
			String customisedata = null;
			JSONObject customizeJson = null;
			JSONArray colw = null;
//			HashMap optMapDetail = null;
			
			BigDecimal discount = null;
			BigDecimal sellsubtotal = null;
			BigDecimal totalPayable = new BigDecimal("0");
			String prodId = null;
			String prodName = null;
			String[] measurement = null;
			int measurementLength = 0;
			for(int i=0; i<arrayListSearch.size(); i++){
				// Initialize
				prodDesc = new StringBuffer();
				
				dataMap = (HashMap) arrayListSearch.get(i);
				dataMap.put("num", String.valueOf(i+1)+".");
				prodId = (String) dataMap.get(MFG_TransactionDetailDef.prodid.name);
				prodName = (String) dataMap.get(MFG_TransactionDetailDef.prodname.name);
				
//				logger.debug("---- buildReport dataMap = "+dataMap);
				if("0".equals(prodId) || !CommonUtil.isEmpty(prodName)){
					prodGroupDesc = "<b>"+prodName+"</b>";		
				}else{
					prodGroupDesc = "<b>"+getHashStringValue(prodMap, "prod"+prodId, MFG_CustProductDef.name.name)+"</b>";					
				}				
				
				if(!"u".equals((String)dataMap.get(MFG_CustProductDef.sellunittype.name))){
				    if(!CommonUtil.isEmpty((String)dataMap.get("opt1_name"))){
                        prodGroupDesc += " / <b>" +
                        (String)dataMap.get("opt1_name")+"</b>";
                    }
                    if(!CommonUtil.isEmpty((String)dataMap.get("opt2_name"))){          
                        prodGroupDesc += " / " + (String)dataMap.get("opt2_groupname") + ": <b>" + 
                        (String)dataMap.get("opt2_name")+"</b>";
                    }
                    if(!CommonUtil.isEmpty((String)dataMap.get("opt3_name"))){
                        prodGroupDesc += " / " + (String)dataMap.get("opt3_groupname") + ": <b>" + 
                        (String)dataMap.get("opt3_name")+"</b>";
                    }
				}

				// Change measurement
				measurement = ((String)dataMap.get(MFG_TransactionDetailDef.measurement.name)).toString().split(" x ");
//				logger.debug((String)dataMap.get(MFG_TransactionDetailDef.measurement.name) + ", measurement.length = "+measurement.length);
				measurementLength = measurement.length;
				if(measurementLength>0){					
					if(!CommonUtil.isEmpty(measurement[0])){
						prodDesc.append(measurement[0]).append("(w)");
					}else{						
						if("0".equals(prodId) || !CommonUtil.isEmpty(prodName)){
							prodDesc.append(prodName);		
						}else{
						    if("u".equals((String)dataMap.get(MFG_CustProductDef.sellunittype.name))){
						        if(!CommonUtil.isEmpty((String)dataMap.get("opt1_name"))){
						            prodDesc.append((String)dataMap.get("opt1_name"));
			                    }
			                    if(!CommonUtil.isEmpty((String)dataMap.get("opt2_name"))){          
			                        prodDesc.append(" / ").append((String)dataMap.get("opt2_name"));
			                    }
			                    if(!CommonUtil.isEmpty((String)dataMap.get("opt3_name"))){
			                        prodDesc.append(" / ").append((String)dataMap.get("opt3_name"));
			                    }						   						        
						    }
						    
						    if(prodDesc.length()==0) {
						        prodDesc.append(getHashStringValue(prodMap, "prod"+prodId, MFG_CustProductDef.name.name));
						    }
						}			
					}
				}
				if(measurementLength>1){
					prodDesc.append(" x ").append(measurement[1]).append("(h)");
					if("0".equals(prodId) || !CommonUtil.isEmpty(prodName)){
						dataMap.put(MFG_CustProductDef.sellunittype.name, "sf");
					}
				}
				if(measurementLength>2){
					prodDesc.append(" x ").append(measurement[2]).append("(d)");
				}			
				
				if("0".equals(prodId) || !CommonUtil.isEmpty(prodName)){
					if(measurementLength>1){
						dataMap.put(MFG_CustProductDef.sellunittype.name, "sf");
					}else{
						dataMap.put(MFG_CustProductDef.sellunittype.name, "u");
					}
				}
				
				// Customized Data
				customisedata = (String) dataMap.get(MFG_TransactionDetailDef.customisedata.name);
				if(!CommonUtil.isEmpty(customisedata)){		
					customizeJson = new JSONObject(customisedata.replaceAll("\\(", "").replaceAll("\\)", ""));
					colw = (JSONArray)customizeJson.get("colw");
					dataMap.put("panelnum", String.valueOf(colw.length()));
				}
//				prodDesc.append("   (").append((String)dataMap.get(MFG_TransactionDetailDef.unit.name)).append(" sqft)");
				dataMap.put("prodgroupdesc", prodGroupDesc);
				dataMap.put("proddesc", prodDesc.toString());
				discount = new BigDecimal(CommonUtil.parseDouble((String)dataMap.get(MFG_TransactionDetailDef.discount.name)));
				sellsubtotal = new BigDecimal(CommonUtil.parseDouble((String)dataMap.get(MFG_TransactionDetailDef.sellsubtotal.name)));
				sellsubtotal = sellsubtotal.add(discount);
				totalPayable = totalPayable.add(sellsubtotal);
				
				dataMap.put(MFG_TransactionDetailDef.discount.name, discount);
				dataMap.put(MFG_TransactionDetailDef.price.name, new BigDecimal(CommonUtil.parseDouble((String)dataMap.get(MFG_TransactionDetailDef.price.name))));
				dataMap.put(MFG_TransactionDetailDef.sellsubtotal.name, sellsubtotal);
				logger.debug("Transaction Detail = "+dataMap);
			}
			logger.debug("totalPayable = "+totalPayable);
			parameters.put("totalinword", NumberUtil.convert(totalPayable.setScale(2, BigDecimal.ROUND_UP).toString()));			
		}
		
		
		ReportUtil rptUtil = new ReportUtil();
		String reportDir = ResourceUtil.getReportDirectory(_shopInfoBean);
		String subReportDir = ResourceUtil.getSubReportDirectory(_shopInfoBean);
		String logoFile = subReportDir + "logo.jpg";
		String reportFilename = ""; 			
		String quotationJasper = "Quotation.jasper";
		boolean isGST = false;
		
		if(salesDate.before(DateUtil.getDate("2018-09-01"))){
		    isGST = true;
		}
		
//		if("kdd".equals(_shopInfoBean.getShopName())){
//			quotationJasper = "Quotation_noGST.jasper";
//		}
		if(salesDate.before(DateUtil.getDate("2018-09-01"))){			
			if(isGST){
				subReportDir += "GST/";
			}
		}
		parameters.put("SUBREPORT_DIR", subReportDir);		
		if(MFG_SelectBuilder.TRANS_QUOTATION.equals(transType)){
			reportFilename = reportDir + quotationJasper;
			parameters.put("title", "QUOTATION");
			parameters.put("transnodesc", "Quot. No:");
		}else if(MFG_SelectBuilder.TRANS_ORDER_CONFIRM.equals(transType)){
			reportFilename = reportDir + quotationJasper;
			parameters.put("title", "ORDER CONFIRMATION");
			parameters.put("transnodesc", "Order No:");
		}else if(MFG_SelectBuilder.TRANS_INVOICE.equals(transType)){
			reportFilename = reportDir + quotationJasper;
			if(isGST) {
			    parameters.put("title", "TAX INVOICE");    
			}else {
			    parameters.put("title", "INVOICE");    
			}			
			parameters.put("transnodesc", "Invoice No:");		
		}else if(MFG_SelectBuilder.TRANS_DELIVER.equals(transType)){
			reportFilename = reportDir + "Do.jasper";
			parameters.put("title", "DELIVERY ORDER");
			parameters.put("transnodesc", "D.O No:");
		}else if(MFG_SelectBuilder.TRANS_CANCEL.equals(transType)){
			reportFilename = reportDir + quotationJasper;
			parameters.put("title", "CANCEL ORDER");
			parameters.put("transnodesc", "Invoice No:");
		}				
//		reportFilename = reportDir + "header.jasper";
		
//		parameters.put("SUBREPORT_DIR", reportDir);
		parameters.put("LOGO", logoFile);
		parameters.put("REPORT_DIR", reportDir);
		parameters.put("companyname", _shopInfoBean.getShopNameDesc());
		
		
		if(GeneralConst.REPORT.equals(reportType) || GeneralConst.REPORT_INV.equals(reportType)){
			jprint = rptUtil.getReportPrint(reportFilename, arrayListSearch, parameters);
		}else if(GeneralConst.REPORT_DO.equals(reportType)){
			reportFilename = reportDir + "Do.jasper";
			parameters.put("title", "DELIVERY ORDER");
			parameters.put("transnodesc", "D.O No:");
			jprint = rptUtil.getReportPrint(reportFilename, arrayListSearch, parameters);
		}		
		
		if(GeneralConst.REPORT.equals(reportType)&& MFG_SelectBuilder.TRANS_INVOICE.equals(transType)){
			reportFilename = reportDir + "Do.jasper";
			parameters.put("title", "DELIVERY ORDER");
			parameters.put("transnodesc", "D.O No:");
			
			JasperPrint jprint2 = rptUtil.getReportPrint(reportFilename, arrayListSearch, parameters);//		
			List pages = jprint2.getPages();
			
			for(int i=0;i<pages.size(); i++){
				jprint.addPage((JRPrintPage)pages.get(i));
			}
			logger.debug("--------------- Finish --------------- ");
		}
		_resp.setHeader("content-disposition", "inline; filename=\""+(String)parameters.get(MFG_TransactionDef.transno.name)+".pdf\"");
		jprint.setName((String)parameters.get(MFG_TransactionDef.transno.name));
		return jprint;
	}
}
