package com.kingtime.freeweather.api;

public class URLs {
	public final static String HTTP_CONNECT= "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/";
	
	public final static String GET_REGION_COUNTRY = HTTP_CONNECT + "getRegionCountry";
	public final static String GET_REGION_PROVINCE = HTTP_CONNECT + "getRegionProvince";
	public final static String GET_SUPPORTCITY = HTTP_CONNECT + "getSupportCityString?theRegionCode=";
	public final static String GET_WEATHER = HTTP_CONNECT + "getWeather?theUserID=03511f3497fd4e58ad9a427bd0377081&theCityCode=";
}
