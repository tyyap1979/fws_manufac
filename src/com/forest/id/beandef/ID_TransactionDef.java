package com.forest.id.beandef;


import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;
import com.forest.common.constants.GeneralConst;
import com.forest.common.constants.SearchConst;

public class ID_TransactionDef extends BaseDef{
	private static Logger logger = Logger.getLogger (ID_TransactionDef.class);
	
	public static DataObject companyid = null;
	public static DataObject transid = null; // Primary Key
	public static DataObject transno = null;	// Number, Customize auto increase
	public static DataObject customername = null;
	public static DataObject customerinfo = null; 
	public static DataObject comment = null;
	public static DataObject referer = null;
	public static DataObject commission = null;
	public static DataObject totaldiscount = null;
	public static DataObject totalcost = null;
	public static DataObject totalsales = null;			
	public static DataObject salesdate = null;
		
	public static DataObject status = null;
	public static DataObject updateby = null;
	public static DataObject updatedate = null;
	
	// Shadow Field	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "id_transaction";
	public static final String MODULE_NAME = "id_transaction";	
	public static final String DEFAULT_SORT = "salesdate";
	public static final String DEFAULT_SORT_DIRECTION = "desc";	
	public static final String PERMANENT_DELETE = "false";
	public static String KEY = "transid";
	public static final Class[] subTableClass = {ID_TransactionDetailDef.class, ID_TransactionDetailGroupDef.class};
	
	public static final String[] OPTIONS = {GeneralConst.REPORT};	
	
	public static int columns = 2;
	
	public static void setAdditional() {				
		transno.searchOption = SearchConst.LIKE;		
	}	
}
