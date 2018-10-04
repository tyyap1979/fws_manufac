package com.forest.mfg.services;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.util.DateUtil;
import com.forest.common.util.ResourceUtil;
import com.forest.mfg.beandef.MFG_TransactionDef;
import com.forest.mfg.beandef.MFG_TransactionDetailDef;

public class ChartDataServices {
	private AdminSQLServices sql = null;
	private ClientBean _clientBean = null;
	private ShopInfoBean _shopInfoBean = null;
	private Connection _dbConnection = null;
	private final String MODULE_NAME = "xmlbuilder";
	public ChartDataServices(ShopInfoBean shopInfoBean, ClientBean clientBean, Connection conn){
		sql = new AdminSQLServices(shopInfoBean, clientBean, conn);
		_clientBean = clientBean;
		_shopInfoBean = shopInfoBean;
		_dbConnection = conn;
	}

	public StringBuffer xmlMonthlySales(){
		StringBuffer buffer = new StringBuffer();
		ArrayList dataList = sql.getMonthlySales();
		HashMap dataRow = null;
		int lSize = dataList.size();
		String label = null;
		String profit = null;
		String caption="Sales Summary For Past 12 Months";
		String subcaption="For the year " + DateUtil.getCurrentYear();
		String xAxisName="Month";
		String yAxisName="Sales";
		String numberPrefix="RM";
		
		buffer.append("<chart");
		buffer.append(" caption='").append(caption).append("'");
		buffer.append(" subcaption='").append(subcaption).append("'");
		buffer.append(" xAxisName='").append(xAxisName).append("'");
		buffer.append(" yAxisName='").append(yAxisName).append("'");
		buffer.append(" numberPrefix='").append(numberPrefix).append("'");		
		buffer.append(" formatNumberScale='0'");
		buffer.append(">");
		
		StringBuffer salesBuffer = new StringBuffer();
		
		for(int i=0; i<lSize; i++){
			dataRow = (HashMap)dataList.get(i);
			label = (String) dataRow.get(MFG_TransactionDef.salesdate.name);						
			profit = (String) dataRow.get("SALES");
			salesBuffer.append("<set label='").append(label).append("' value='").append(profit).append("' />");
			
		}				
		buffer.append(salesBuffer);
		buffer.append("</chart>");
	
		return buffer;
	}
	
	public StringBuffer xmlCurrentMonthSales(){
		StringBuffer buffer = new StringBuffer();
		ArrayList dataList = sql.getCurrentMonthlySales();
		HashMap dataRow = null;
		int lSize = dataList.size();		
		String profit = null;		
		String caption="Sales Summary For Past 30 Days";
		String subcaption="";
		String xAxisName="Day";
		String yAxisName="Sales";
		String numberPrefix="RM";
		String label = null;
		buffer.append("<chart");
		buffer.append(" caption='").append(caption).append("'");
		buffer.append(" subcaption='").append(subcaption).append("'");
		buffer.append(" xAxisName='").append(xAxisName).append("'");
		buffer.append(" yAxisName='").append(yAxisName).append("'");
		buffer.append(" numberPrefix='").append(numberPrefix).append("'");		
		buffer.append(" formatNumberScale='0'");
		buffer.append(">");
				
		StringBuffer salesBuffer = new StringBuffer();

		for(int i=0; i<lSize; i++){
			dataRow = (HashMap)dataList.get(i);					
			profit = (String) dataRow.get("SALES");						
			label = (String) dataRow.get(MFG_TransactionDef.salesdate.name);			
			buffer.append("<set label='").append(label).append("' value='").append(profit).append("' />");
		}		
		buffer.append(salesBuffer);
		buffer.append("</chart>");
	
		return buffer;
	}
}
