package com.forest.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


public class ReportUtil {
	private Logger logger = Logger.getLogger(ReportUtil.class);
	
	public static void main(String[] args) throws Exception{
		ReportUtil a = new ReportUtil();
		Map parameters = new HashMap();
		ArrayList dataList = new ArrayList();
		Map dataRow = new HashMap();
		dataRow.put("num", "1");
		dataRow.put("proddesc", "S1");
		dataRow.put("opt1", "Normal Class");
		dataRow.put("description", "100mm X 200mm");
		dataRow.put("qty", "2");
		dataRow.put("unitprice", "20.10");
		dataRow.put("subtotal", "70.10");
		dataList.add(dataRow);
		
		dataRow = new HashMap();
		dataRow.put("num", "2");
		dataRow.put("proddesc", "S1");
		dataRow.put("opt1", "Normal Class");
		dataRow.put("description", "100mm X 200mm");
		dataRow.put("qty", "3");
		dataRow.put("unitprice", "29.10");
		dataRow.put("subtotal", "70.10");
		dataList.add(dataRow);
		
		parameters.put("PARAM_PAGE_HEADER_IMAGE", "C:/Adam/Developement/Project/mfg/MegaTrend/reportheader.png");
		parameters.put("PARAM_PAGE_FOOTER_IMAGE", "C:/Adam/Developement/Project/mfg/MegaTrend/reportfooter.png");
		parameters.put("attn", "1asdmkasd ad");
		parameters.put("transno", "T1");
		parameters.put("terms", "30 Days");
		parameters.put("salesdate", "20 Sep 2010");
		parameters.put("comment", "Batu Caves Project");
		parameters.put("grandtotal", "200.20");
		
		JasperPrint jp = a.getReportPrint("C:/Adam/Developement/Report/Quotation.jasper", dataList, parameters);
//		JasperViewer.viewReport(jp);
		JasperExportManager.exportReportToPdfFile(jp, "C:/Adam/Developement/Report/Quotation.pdf");

	}
	
	public JasperPrint getReportPrint(String reportFileName, ArrayList dataList, Map parameters)throws Exception{
		JasperPrint jasperPrint = null;
		
        try{	        	
        	logger.debug("getReportPrint = "+reportFileName);
			File reportFile = new File(reportFileName);
			if (!reportFile.exists())
				throw new JRRuntimeException("File WebappReport.jasper not found. The report design must be compiled first.");		
			
			JRDataSource dataSource = new JRBeanCollectionDataSource(dataList);			
			jasperPrint = JasperFillManager.fillReport(reportFileName, parameters, dataSource);				
        }catch(Exception e) {
            throw e;
        }
        return jasperPrint;
    }

}
