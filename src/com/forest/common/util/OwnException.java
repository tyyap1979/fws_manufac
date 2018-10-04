package com.forest.common.util;
import com.forest.common.constants.ExceptionConst;

public class OwnException extends Exception
{
	static final long serialVersionUID = 1L;
	private String errorCode = null;
	private String errorMsg = null;		
	
	public OwnException(Exception e){				
		if (e.getClass ().getName ().indexOf ("MySQLIntegrityConstraintViolationException")!=-1){			
			setErrorCode(ExceptionConst.DB_DUPLICATE);	// Duplicate Key		
		}else{
			setErrorCode(ExceptionConst.GENERAL_EXCEPTION);		
		}				
	}
	
//	public OwnException(String p_sErrorCode, String p_sExceptionCls, String p_sExceptionMsg){
//		setErrorCode(p_sErrorCode);		
//		setExceptionCls (p_sExceptionCls);
//		setExceptionMsg (p_sExceptionMsg);
//		logger.debug (p_sErrorCode+":"+p_sExceptionCls+":"+p_sExceptionMsg);
//	}
	
	public OwnException(String p_sErrorCode){		
		setErrorCode(p_sErrorCode);		
	}
	
	public OwnException(String p_sErrorCode, String errorMsg){		
		setErrorCode(p_sErrorCode);	
		setErrorMsg (errorMsg);
	}
	
	public String getErrorCode ()
	{
		return errorCode;
	}
	public void setErrorCode (String errorCode)
	{
		this.errorCode = errorCode;		
	}
	public String getErrorMsg ()
	{
		return errorMsg;
	}
	public void setErrorMsg (String errorMsg)
	{		
		this.errorMsg = errorMsg;
	}

	
}
