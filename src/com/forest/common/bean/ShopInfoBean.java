package com.forest.common.bean;

import java.util.ArrayList;


public class ShopInfoBean
{
//	private Logger logger = Logger.getLogger (this.getClass ());
	private String masterShopName = null;
	private String shopName = null;
	private String shopDomain = null;
	private String shopContext = null;
	private String shopNameDesc = null;
	private String selectedShop = null;
	private String shopTemplate = null;
	private String templatePath = null;
//	private StringBuffer themeHTML = null;
//	private String defaultCurrency = null;
	private String paymentCurrency = null;	
	private String shopCountryISO  = null; // For Shipment
	private String defaultLanguage = null;
	private String module = null; // Admin, Shop
	private String business = null;
	private String theme = null;
	private String companyGroup = null;
	
	public String getShopName ()
	{
		return shopName;
	}
	public void setShopName (String shopName)
	{
		this.shopName = shopName;
	}

//	public String getDefaultCurrency ()
//	{
//		return defaultCurrency;
//	}
//
//	public void setDefaultCurrency (String defaultCurrency)
//	{
//		this.defaultCurrency = defaultCurrency;
//	}

	public String getShopCountryISO ()
	{
		return shopCountryISO;
	}

	public void setShopCountryISO (String shopCountryISO)
	{
		this.shopCountryISO = shopCountryISO;
	}

	public String getShopTemplate ()
	{
		return shopTemplate;
	}

	public void setShopTemplate (String shopTemplate)
	{
		this.shopTemplate = shopTemplate;
	}

	public String getDefaultLanguage ()
	{
		return defaultLanguage;
	}

	public void setDefaultLanguage (String defaultLanguage)
	{
		this.defaultLanguage = defaultLanguage;
	}
	public String getTemplatePath ()
	{
		return templatePath;
	}
	public void setTemplatePath (String templatePath)
	{
		this.templatePath = templatePath;
	}
	public String getPaymentCurrency() {
		return paymentCurrency;
	}
	public void setPaymentCurrency(String paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}

	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getCompanyGroup() {
		return companyGroup;
	}
	public void setCompanyGroup(String companyGroup) {
		this.companyGroup = companyGroup;
	}
	public String getMasterShopName() {
		return masterShopName;
	}
	public void setMasterShopName(String masterShopName) {
		this.masterShopName = masterShopName;
	}
	public String getSelectedShop() {
		return selectedShop;
	}
	public void setSelectedShop(String selectedShop) {
		this.selectedShop = selectedShop;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getShopNameDesc() {
		return shopNameDesc;
	}
	public void setShopNameDesc(String shopNameDesc) {
		this.shopNameDesc = shopNameDesc;
	}
	public String getShopDomain() {
		return shopDomain;
	}
	public void setShopDomain(String shopDomain) {
		this.shopDomain = shopDomain;
	}
	public String getShopContext() {
		return shopContext;
	}
	public void setShopContext(String shopContext) {
		this.shopContext = shopContext;
	}
}
