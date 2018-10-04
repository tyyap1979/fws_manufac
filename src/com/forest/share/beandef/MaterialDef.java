package com.forest.share.beandef;


import java.util.ArrayList;
import com.forest.common.bean.DataObject;
import com.forest.common.beandef.BaseDef;

public class MaterialDef extends BaseDef{
//	private static Logger logger = Logger.getLogger (GalleryDef.class);
	
	public static DataObject mid = null;	
	public static DataObject companyid = null;
	public static DataObject title = null;
	public static DataObject description = null;
	public static DataObject updatedate = null;
	
	public static ArrayList _searchDisplayList = new ArrayList();
	public static ArrayList _searchList = new ArrayList();
	public static ArrayList _addList = new ArrayList();	
	
	public static final String TABLE = "material";
	public static final String MODULE_NAME = "material";	
	public static final String DEFAULT_SORT = "title";
	public static final String DEFAULT_SORT_DIRECTION = "asc";	
	public static final String PERMANENT_DELETE = "true";
	public static String KEY = "mid";	
	public static final Class[] subTableClass = {MaterialDetailDef.class};	
	
	public static void setAdditional() {				
    	
	}		
}
