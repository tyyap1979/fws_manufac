package com.forest.cron;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;

public class CronCurrency
{
	private Connection conn = null;
	private Logger logger = Logger.getLogger (CronCurrency.class);
	
	public static void main(String[] args) throws Exception{		
		CronCurrency t = new CronCurrency();	
		t.conn = new DBUtil().getDirectConnection ();
		t.readMain();	
	}			
	
	
	private void readMain()throws Exception{				
		PreparedStatement pstmtUpdate = null;
		StringBuffer queryUpdate = new StringBuffer();
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");	 
	    
	    String currencylist = "ADF_ADP_AED_AFN_ALL_AMD_ANG_AOA_AON_ARS_ATS_AUD_AWG_AZM_AZN_BAM_BBD_BDT_BEF_BGN_BHD_BIF_BMD_BND_BOB_BRL_BSD_BTN_BWP_BYR_BZD_CAD_CDF_CHF_CLP_CNY_COP_CRC_CUC_CUP_CVE_CYP_CZK_DEM_DJF_DKK_DOP_DZD_ECS_EEK_EGP_ESP_ETB_EUR_FIM_FJD_FKP_FRF_GBP_GEL_GHC_GHS_GIP_GMD_GNF_GRD_GTQ_GYD_HKD_HNL_HRK_HTG_HUF_IDR_IEP_ILS_INR_IQD_IRR_ISK_ITL_JMD_JOD_JPY_KES_KGS_KHR_KMF_KPW_KRW_KWD_KYD_KZT_LAK_LBP_LKR_LRD_LSL_LTL_LUF_LVL_LYD_MAD_MDL_MGA_MGF_MKD_MMK_MNT_MOP_MRO_MTL_MUR_MVR_MWK_MXN_MYR_MZM_MZN_NAD_NGN_NIO_NLG_NOK_NPR_NZD_OMR_PAB_PEN_PGK_PHP_PKR_PLN_PTE_PYG_QAR_ROL_RON_RSD_RUB_RWF_SAR_SBD_SCR_SDD_SDG_SDP_SEK_SGD_SHP_SIT_SKK_SLL_SOS_SRD_SRG_STD_SVC_SYP_SZL_THB_TJS_TMM_TMT_TND_TOP_TRL_TRY_TTD_TWD_TZS_UAH_UGX_USD_UYU_UZS_VEB_VEF_VND_VUV_WST_XAF_XAG_XAU_XCD_XEU_XOF_XPD_XPF_XPT_YER_YUN_ZAR_ZMK_ZWD";
	    String paypallist = "AUD_CAD_EUR_GBP_JPY_USD_NZD_CHF_HKD_SGD_SEK_DKK_PLN_NOK_HUF_CZK_ILS_MXN_BRL_MYR_PHP_TWD_THB_TRY";
	    
		String url = "http://www.oanda.com/currency/table?date="+sdf.format(cal.getTime())+"&date_fmt=us&exch=USD&sel_list="+currencylist+"&value=1&format=HTML&redirected=1";
		try{
			queryUpdate.append("Update currency Set ");
			queryUpdate.append("rate=?, updatedate=now()");
			queryUpdate.append(" Where currencycode=?");
			pstmtUpdate = conn.prepareStatement(queryUpdate.toString());
			
			logger.debug("Processing "+url);
			
			Document doc = Jsoup.parse(readURL(url).toString());     
	        Elements media = doc.select("table[id=converter_table]").select("TR");
	        
	        Element src = null;
	        Elements td = null;
	        String color = null;
	        String code = null;
	        String rate = null;	        
	        int count = 0;

	        
	        count = media.size();
	        if(count>0){	        	
		        for (int i=0; i<count;i++) {
		        	src = media.get(i);
		        	color = src.attr("BGCOLOR");
		        	if(!CommonUtil.isEmpty(color) && (color.equals("#efefef") || color.equals("#dfdfdf"))){		        		
		        		td = src.select("TD");		        
		        		code = td.get(1).text();
		        		rate = td.get(2).text();
		        		
		        		try{		        		
		        			pstmtUpdate.setString(1, rate);
		        			pstmtUpdate.setString(2, code);
		        			pstmtUpdate.executeUpdate();		        		
			        	}catch(Exception e){
			        		logger.error(e);
			        	}
		        	}
		        	
		        }
//		        SelectBuilder.removeCache("", SelectBuilder.CODE_CURRENCY);
	        }	        
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			logger.debug("Currencies Updated");
			DBUtil.free(conn, null, null);
		}
	}	
	
	public StringBuffer readURL(String address){
		StringBuffer content = new StringBuffer();
		try{
			URL url = new URL(address);
	        BufferedReader in = new BufferedReader(
	        new InputStreamReader(url.openStream()));
	
	        String inputLine;
	        while ((inputLine = in.readLine()) != null)
	        	content.append(inputLine);
	            
	        in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return content;
	}
}
