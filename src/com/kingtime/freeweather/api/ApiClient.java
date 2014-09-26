package com.kingtime.freeweather.api;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;

import android.R.integer;
import android.util.Log;


public class ApiClient {

	private static InputStream _post(String actionURL, Map<String, String> params) throws IOException {
		Log.i("API-POST-RequestURL", actionURL);
		String enterNewLine = "\r\n";
		String fix = "--";
		String boundaryString = "######";
		InputStream inputStream = null;

		URL url = new URL(actionURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundaryString + enterNewLine);
		DataOutputStream ds = new DataOutputStream(connection.getOutputStream());

		Set<String> keySet = params.keySet();
		Iterator<String> iterator = keySet.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = params.get(key);
			System.out.println("key:" + key + ",value:" + URLEncoder.encode(value, "UTF-8") + "\n");
			ds.writeBytes(fix + boundaryString + enterNewLine);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + enterNewLine);
			ds.writeBytes(enterNewLine);
			ds.writeBytes(URLEncoder.encode(value, "UTF-8"));
			ds.writeBytes(enterNewLine);
		}

		ds.writeBytes(fix + boundaryString + fix + enterNewLine);
		ds.flush();
		if (connection.getResponseCode() == HttpStatus.SC_OK) {
			inputStream = connection.getInputStream();
		}
		ds.close();
		// System.out.println("close");
		return inputStream;
	}

	private static InputStream _get(String actionURL) throws IOException {
		Log.i("API-GET-RequestURL", actionURL);
		URL url = new URL(actionURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5000);
		connection.setRequestMethod("GET");
		if (connection.getResponseCode() == HttpStatus.SC_OK) {
			System.out.println("GET-Response ok!");
			return connection.getInputStream();
		}
		System.out.println("GET-Response not ok!");
		return null;
	}
	
	public static InputStream getRegionProvince() throws IOException{
		return _get(URLs.GET_REGION_PROVINCE);
	}
	
	public static InputStream getSupportCity(int provinceCode) throws IOException{
		String actionURL = URLs.GET_SUPPORTCITY + String.valueOf(provinceCode);
		return _get(actionURL);
	}
	
	public static InputStream getWeather(int cityCode) throws IOException{
		String actionURL = URLs.GET_WEATHER  + String.valueOf(cityCode);
		return _get(actionURL);
	}
}
