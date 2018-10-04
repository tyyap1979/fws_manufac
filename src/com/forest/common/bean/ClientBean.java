package com.forest.common.bean;

import java.util.Locale;

public class ClientBean
{
//	private Logger logger = Logger.getLogger (this.getClass ());
	private String loginUserId = null;
	private String loginUserName = null;
	private String loginUserEmail = null;
	private String userGroup = null;
	private String requestFilename = null;
	private String language = null;
	private String selectedCurrency = null;
	private Locale locale = null;

	public String toString(){
		StringBuffer mBuffer = new StringBuffer();
		mBuffer.append ("loginUserId: "+loginUserId +"\n");		
		mBuffer.append ("loginUserName: "+loginUserName +"\n");
		mBuffer.append ("loginUserEmail: "+loginUserEmail +"\n");
		mBuffer.append ("requestFilename: "+requestFilename +"\n");
		mBuffer.append ("selectedCurrency: "+selectedCurrency +"\n");
		mBuffer.append ("locale: "+locale +"\n");		
		return mBuffer.toString ();
	}
	
	public String getLoginUserId ()
	{
		return loginUserId;
	}
	public void setLoginUserId (String loginUserId)
	{
		this.loginUserId = loginUserId;
	}
	public String getLoginUserName ()
	{
		return loginUserName;
	}
	public void setLoginUserName (String loginUserName)
	{
		this.loginUserName = loginUserName;
	}
	public String getRequestFilename ()
	{
		return requestFilename;
	}
	public void setRequestFilename (String requestFilename)
	{
		this.requestFilename = requestFilename;
	}
	public String getSelectedCurrency ()
	{
		return selectedCurrency;
	}
	public void setSelectedCurrency (String selectedCurrency)
	{
		this.selectedCurrency = selectedCurrency;
	}
	public Locale getLocale ()
	{
		return locale;
	}

	public void setLocale (Locale locale)
	{
		this.locale = locale;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getLoginUserEmail() {
		return loginUserEmail;
	}

	public void setLoginUserEmail(String loginUserEmail) {
		this.loginUserEmail = loginUserEmail;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
