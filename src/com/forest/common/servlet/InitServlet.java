package com.forest.common.servlet;

import java.sql.Connection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.forest.admin.builder.GlobalResetBuilder;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.DBUtil;
import com.forest.common.util.LoadTableObject;
import com.forest.common.util.ResourceUtil;

public class InitServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{	
	private Logger logger = Logger.getLogger (InitServlet.class);
	static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException {
		logger.debug ("-------------------------InitServlet------------------------------");
//		ThemesServices.removeInstance ();
//		GlobalParameterBuilder.removeInstance ();
//		ResourceUtil.clearCustomProperties ();
//		SendMail.getInstance ();
		Connection conn = null;
		try{		
			conn = new DBUtil().getDirectConnection();
			GlobalResetBuilder.getInstance(conn); 
			LoadTableObject.loadTable(conn);			
			
			// Set Domain
			GeneralConst.DOMAIN = ResourceUtil.getSettingValue ("domain.name");
			GeneralConst.SUBDOMAIN = ResourceUtil.getSettingValue ("subdomain.name");
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			DBUtil.free(conn, null, null);
		}
//		logger.info(ProductBeanDef.TABLE + ": " +ProductBeanDef.getTableObject());
	}

}
