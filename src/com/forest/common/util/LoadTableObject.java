package com.forest.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.AdminUserBeanDef;
import com.forest.common.beandef.AgentProfileDef;
import com.forest.common.beandef.AutoNumberDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.beandef.ClientUserBeanDef;
import com.forest.common.beandef.CountryBeanDef;
import com.forest.common.beandef.CurrencyBeanDef;
import com.forest.common.beandef.CustomerProfileDef;
import com.forest.common.beandef.ModuleAdminDef;
import com.forest.common.beandef.ModuleDetailDef;
import com.forest.common.beandef.ShopBeanDef;
import com.forest.common.beandef.SupplierProfileDef;
import com.forest.common.beandef.VoucherDef;
import com.forest.common.beandef.WebsiteDef;
import com.forest.mfg.beandef.MFG_CartDef;
import com.forest.mfg.beandef.MFG_CartDetailDef;
import com.forest.mfg.beandef.MFG_CreditNoteDef;
import com.forest.mfg.beandef.MFG_CustProductCustomerPriceDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_CustProductImageDef;
import com.forest.mfg.beandef.MFG_CustProductPriceDef;
import com.forest.mfg.beandef.MFG_CustProductSelectionDef;
import com.forest.mfg.beandef.MFG_DebitNoteDef;
import com.forest.mfg.beandef.MFG_Grouping;
import com.forest.mfg.beandef.MFG_JobSheetDef;
import com.forest.mfg.beandef.MFG_JobSheetDetailDef;
import com.forest.mfg.beandef.MFG_PODef;
import com.forest.mfg.beandef.MFG_PODetailDef;
import com.forest.mfg.beandef.MFG_PaymentVoucherDef;
import com.forest.mfg.beandef.MFG_PaymentVoucherDetailDef;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_RawDef;
import com.forest.mfg.beandef.MFG_RawToProductDef;
import com.forest.mfg.beandef.MFG_RawToProductDetailDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDef;
import com.forest.mfg.beandef.MFG_ReceivePaymentDetailDef;
import com.forest.mfg.beandef.MFG_StatementDef;
import com.forest.mfg.beandef.MFG_StatementDetailDef;
import com.forest.mfg.beandef.MFG_SupplierInvoiceDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDef;
import com.forest.mfg.beandef.MFG_SupplierProductOptionDetailDef;
import com.forest.mfg.beandef.MFG_SupplierProductPriceDef;
import com.forest.mfg.beandef.MFG_SupplierProductSelectionDef;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;
import com.forest.mfg.beandef.MFG_TransactionDetailGroupDef;
import com.forest.mfg.beandef.SMSDef;
import com.forest.retail.beandef.RETAIL_CartDef;
import com.forest.retail.beandef.RETAIL_CartDetailDef;
import com.forest.retail.beandef.RETAIL_CategoryDef;
import com.forest.retail.beandef.RETAIL_CategoryDetailDef;
import com.forest.retail.beandef.RETAIL_MemberDef;
import com.forest.retail.beandef.RETAIL_ProductAttributeDef;
import com.forest.retail.beandef.RETAIL_ProductDef;
import com.forest.retail.beandef.RETAIL_ProductGroupDef;
import com.forest.retail.beandef.RETAIL_ProductGroupDetailDef;
import com.forest.retail.beandef.RETAIL_ProductImageDef;
import com.forest.retail.beandef.RETAIL_ProductOptionDef;
import com.forest.retail.beandef.RETAIL_TransactionDef;
import com.forest.retail.beandef.RETAIL_TransactionDetailDef;
import com.forest.retail.beandef.RETAIL_TransactionVoucherDef;
import com.forest.share.beandef.EmailTemplateDef;
import com.forest.share.beandef.FriendlyLinkDef;
import com.forest.share.beandef.GalleryDef;
import com.forest.share.beandef.GalleryDetailDef;
import com.forest.share.beandef.MaterialDef;
import com.forest.share.beandef.MaterialDetailDef;
import com.forest.share.beandef.PageContentDef;
import com.forest.share.beandef.PaymentMethodDef;
import com.forest.share.beandef.ShipmentDomesticDef;
import com.forest.share.beandef.ShipmentDomesticDetailDef;
import com.forest.share.beandef.ShipmentInternationalDef;
import com.forest.share.beandef.ShipmentInternationalDetailDef;
import com.forest.share.beandef.SmsTemplateDef;

public class LoadTableObject {	
	private static Logger logger = Logger.getLogger (LoadTableObject.class);
	private static ArrayList defObjList = new ArrayList();
	private static void initBeanTable(){		
		defObjList.add(CountryBeanDef.class);
		defObjList.add(CurrencyBeanDef.class);
		defObjList.add(AgentProfileDef.class);
		defObjList.add(AdminUserBeanDef.class);					
		
		defObjList.add(ShopBeanDef.class);
		defObjList.add(WebsiteDef.class);
		defObjList.add(PageContentDef.class);
		defObjList.add(GalleryDef.class);
		defObjList.add(GalleryDetailDef.class);		
		defObjList.add(MaterialDef.class);
		defObjList.add(MaterialDetailDef.class);
		defObjList.add(FriendlyLinkDef.class);		
		defObjList.add(ShipmentDomesticDef.class);
		defObjList.add(ShipmentDomesticDetailDef.class);
		defObjList.add(ShipmentInternationalDef.class);
		defObjList.add(ShipmentInternationalDetailDef.class);
		defObjList.add(PaymentMethodDef.class);
		
		defObjList.add(CustomerProfileDef.class);
		defObjList.add(SupplierProfileDef.class);
		defObjList.add(EmailTemplateDef.class);	
		defObjList.add(SmsTemplateDef.class);		
		defObjList.add(ClientUserBeanDef.class);		
		
		defObjList.add(ModuleAdminDef.class);
		defObjList.add(ModuleDetailDef.class);
		
		// Manufacturing
		defObjList.add(MFG_ProductOptionDef.class);
		defObjList.add(MFG_ProductOptionDetailDef.class);
		defObjList.add(MFG_CustProductDef.class);
		defObjList.add(MFG_CustProductSelectionDef.class);
		defObjList.add(MFG_CustProductImageDef.class);
		defObjList.add(MFG_CustProductPriceDef.class);
		defObjList.add(MFG_CustProductCustomerPriceDef.class);
		
		defObjList.add(MFG_TransactionDef.class);
		defObjList.add(MFG_TransactionDetailDef.class);
		defObjList.add(MFG_TransactionDetailGroupDef.class);
		
		defObjList.add(MFG_RawDef.class);
		defObjList.add(MFG_RawToProductDef.class);
		defObjList.add(MFG_RawToProductDetailDef.class);
		
		defObjList.add(MFG_JobSheetDef.class);
		defObjList.add(MFG_JobSheetDetailDef.class);
		defObjList.add(AutoNumberDef.class);
		
		defObjList.add(MFG_SupplierProductDef.class);
		defObjList.add(MFG_SupplierProductPriceDef.class);
		defObjList.add(MFG_SupplierProductSelectionDef.class);
		defObjList.add(MFG_SupplierProductOptionDef.class);
		defObjList.add(MFG_SupplierProductOptionDetailDef.class);
		
		defObjList.add(MFG_Grouping.class);
		
		defObjList.add(MFG_PODetailDef.class);
		defObjList.add(MFG_PODef.class);
		
		defObjList.add(MFG_ReceivePaymentDef.class);	
		defObjList.add(MFG_ReceivePaymentDetailDef.class);
		
		defObjList.add(MFG_StatementDef.class);	
		defObjList.add(MFG_StatementDetailDef.class);		
		
		defObjList.add(MFG_CreditNoteDef.class);
		defObjList.add(MFG_DebitNoteDef.class);
		
		defObjList.add(MFG_SupplierInvoiceDef.class);
		defObjList.add(MFG_PaymentVoucherDef.class);
		defObjList.add(MFG_PaymentVoucherDetailDef.class);
		
		defObjList.add(SMSDef.class);
		
		// MFG WebSite
		defObjList.add(MFG_CartDef.class);
		defObjList.add(MFG_CartDetailDef.class);
		
		// Retail
//		defObjList.add(RETAIL_MemberDef.class);
//		defObjList.add(RETAIL_ProductDef.class);
//		defObjList.add(RETAIL_ProductAttributeDef.class);
//		defObjList.add(RETAIL_ProductOptionDef.class);
//		defObjList.add(RETAIL_ProductImageDef.class);
//		defObjList.add(RETAIL_CartDef.class);
//		defObjList.add(RETAIL_CartDetailDef.class);
//		defObjList.add(RETAIL_CategoryDef.class);
//		defObjList.add(RETAIL_CategoryDetailDef.class);
//		defObjList.add(RETAIL_TransactionDef.class);
//		defObjList.add(RETAIL_TransactionDetailDef.class);
//		defObjList.add(RETAIL_TransactionVoucherDef.class);
//		
//		defObjList.add(RETAIL_ProductGroupDef.class);
//		defObjList.add(RETAIL_ProductGroupDetailDef.class);
		
//		defObjList.add(VoucherDef.class);
		
		// Interior Design
//		defObjList.add(ID_TransactionDef.class);
//		defObjList.add(ID_TransactionDetailDef.class);
//		defObjList.add(ID_TransactionDetailGroupDef.class);
		
		
	}
	
	public static void loadTable(Connection conn){		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuffer query = new StringBuffer();
		String tableName = null;		
		
		DataObject fieldObject = null;
		Class clsObject= null;
		
		Class[] paramType = null;
		Object[] paramValue = null;				
		Field field = null;
		Method mthd = null;
		String dataType = null;
		
		try{
			initBeanTable();
			
			query.append("Select table_name, column_name, is_nullable, data_type, character_maximum_length,");
			query.append(" numeric_precision, numeric_scale, column_key, extra, column_comment");
			query.append(" From information_schema.COLUMNS");
			query.append(" Where table_schema='forestdb'");
			query.append(" And table_name = ?");
//			query.append(" Order By table_name");
			logger.debug("Adam query = "+query);
			
			pstmt = conn.prepareStatement(query.toString());
			
			
			for(int i=0; i<defObjList.size(); i++){
				clsObject = (Class) defObjList.get(i);
				tableName = (String) BeanDefUtil.getField(clsObject, "TABLE");
				
				pstmt.setString(1, tableName);
				
				rs = pstmt.executeQuery();
				while(rs.next()){					
					fieldObject = new DataObject();
					fieldObject.name = rs.getString("column_name");
					fieldObject.nullable = rs.getString("is_nullable").equals("YES")?true:false;
					dataType = rs.getString("data_type");
					
					if(dataType.equals("varchar")){
						fieldObject.type = Types.VARCHAR;
					}else if(dataType.equals("date")){
						fieldObject.type = Types.DATE;
					}else if(dataType.equals("datetime")){
						fieldObject.type = Types.TIMESTAMP;
					}else if(dataType.equals("double")){
						fieldObject.type = Types.DOUBLE;
					}else if(dataType.equals("int")){
						fieldObject.type = Types.INTEGER;
					}else if(dataType.equals("mediumtext") || dataType.equals("tinytext")){
						fieldObject.type = Types.CLOB;
					}else if(dataType.equals("text")){
						fieldObject.type = Types.CLOB;
					}else if(dataType.equals("smallint")){
						fieldObject.type = Types.SMALLINT;
					}else if(dataType.equals("tinyint")){
						fieldObject.type = Types.TINYINT;
					}
					
					if(fieldObject.type==Types.INTEGER || fieldObject.type==Types.SMALLINT  || fieldObject.type==Types.TINYINT || fieldObject.type==Types.DOUBLE){
						fieldObject.length = rs.getInt("numeric_precision");
						fieldObject.precision = rs.getInt("numeric_scale");
					}else{
						fieldObject.length = rs.getInt("character_maximum_length");
					}
					fieldObject.key = rs.getString("column_key").equals("PRI")?true:false;
					fieldObject.autoIncrement = rs.getString("extra").equals("auto_increment")?true:false;
					
					fieldObject.validation = rs.getString("column_comment");				

					if(clsObject!=null){
//						logger.info(" ["+clsObject.getName()+"]."+tableName+"."+fieldObject.name);	 				
						field = clsObject.getDeclaredField(fieldObject.name);
						field.set(clsObject, fieldObject);
//						if(fieldObject.key){
//							clsObject.getDeclaredField("KEY").set(clsObject, fieldObject.name);
//						}
					}else{
						logger.debug("no class for "+tableName);
					}
				}
				
				// Set Additional
//				Iterator it = beanMapTable.keySet().iterator();
//				String key = null;
//				while(it.hasNext()){
//					key = (String) it.next();
//					clsObject = (Class) beanMapTable.get(key);
//					if(clsObject!=null){
//						mthd = clsObject.getMethod ("setAdditional", paramType);						
//						mthd.invoke (clsObject, paramValue);
//					}
//				}
			}
			
			for(int i=0; i<defObjList.size(); i++){
				clsObject = (Class) defObjList.get(i);
				if(clsObject!=null){
					logger.debug("clsObject.setAdditional = "+clsObject.getName());
					mthd = clsObject.getMethod ("setAdditional", paramType);						
					mthd.invoke (clsObject, paramValue);
				}
			}							
			
			Iterator it = BeanDefUtil.beanCacheList.keySet().iterator();
			while(it.hasNext()){
				logger.debug("Key: "+(String) it.next());
			}
		}catch(Exception e){
			logger.error("clsObject: "+clsObject.toString());
			logger.error("fieldObject.name: "+fieldObject.name);
			logger.error("tableName: "+tableName);			
			logger.error(e, e);
		}finally{			
			logger.debug(" end data field");
		}
	}
}
