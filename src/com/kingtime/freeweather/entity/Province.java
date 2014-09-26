package com.kingtime.freeweather.entity;

import java.util.ArrayList;
import java.util.List;


public class Province {

	private String provinceName;
	private int provinceCode;
	
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public int getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(int provinceCode) {
		this.provinceCode = provinceCode;
	}
	
	public static List<Province> toProvinces(List<String> provinceStrings){
		List<Province> provincesList = new ArrayList<Province>();
		for (String provinceString : provinceStrings) {
			String [] params = provinceString.split(",");
			Province province = new Province();
			province.provinceName = params[0];
			province.provinceCode = Integer.valueOf(params[1]);
			provincesList.add(province);
		}
		return provincesList;
	}
	
	public static List<String> provinceNames(List<Province> provincesList){
		List<String> names = new ArrayList<String>();
		for (Province province : provincesList) {
			names.add(province.getProvinceName());
		}
		return names;
	}
}
