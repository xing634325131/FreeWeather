package com.kingtime.freeweather.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.kingtime.freeweather.R;
import com.kingtime.freeweather.api.ApiClient;
import com.kingtime.freeweather.app.WeatherApplication;
import com.kingtime.freeweather.entity.BaseLocation;
import com.kingtime.freeweather.entity.WeatherInfo;
import com.kingtime.freeweather.utils.FileUtils;
import com.kingtime.freeweather.utils.StreamTool;
import com.kingtime.freeweather.utils.StringUtils;
import com.kingtime.freeweather.utils.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private WeatherApplication weatherApplication;

	private TextView statusTextView;
	private Button parseDataButton;
	private Spinner provinceSpinner;
	private Spinner citySpinner;
	private TextView weatherInfoTextView;
	private Button showSuggestButton;
	private Button showWeatherButton;
	private final static String TAG = "MainActivity";

	private List<BaseLocation> provinceList;
	private List<BaseLocation> cityList;
	private List<String> weatherList;
	private WeatherInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		System.out.println(TAG + " start!");
		weatherApplication = (WeatherApplication) getApplication();

		initView();
		// loadData();
	}

	private void initView() {
		statusTextView = (TextView) findViewById(R.id.status_tv);
		parseDataButton = (Button) findViewById(R.id.prase_btn);
		provinceSpinner = (Spinner) findViewById(R.id.province_sp);
		citySpinner = (Spinner) findViewById(R.id.city_sp);
		weatherInfoTextView = (TextView) findViewById(R.id.weather_info_tv);
		showSuggestButton = (Button) findViewById(R.id.suggest_btn);
		showWeatherButton = (Button) findViewById(R.id.weather_btn);

		parseDataButton.setOnClickListener(listener);
		provinceSpinner.setOnItemSelectedListener(spListener);
		citySpinner.setOnItemSelectedListener(cpListener);
		showSuggestButton.setOnClickListener(listener);
		showWeatherButton.setOnClickListener(listener);

		provinceList = new ArrayList<BaseLocation>();
		cityList = new ArrayList<BaseLocation>();
		weatherList = new ArrayList<String>();

	}

	private void loadData() {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				InputStream provinceStream = (InputStream) msg.obj;
				String provinceXML = null;
				try {
					byte data[] = null;
					data = StreamTool.read(provinceStream);
					provinceXML = new String(data);
					System.out.println(provinceXML);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String dataLoadTips = "Data not load!";
				if (!StringUtils.isEmpty(provinceXML)) {
					FileUtils.write(getApplicationContext(),
							"RegionProvince.xml", provinceXML);
					dataLoadTips = "Data has loaded!";
				}
				statusTextView.setText(dataLoadTips);
				super.handleMessage(msg);
			}

		};

		new Thread() {
			public void run() {
				InputStream provinceStream = null;
				try {
					provinceStream = ApiClient.getRegionProvince();
					byte[] data1 = StreamTool.read(ApiClient
							.getSupportCity(31118));
					byte[] data2 = StreamTool.read(ApiClient.getWeather(1701));
					FileUtils.write(getApplicationContext(), "SupportCity.xml",
							new String(data1));
					FileUtils.write(getApplicationContext(),
							"FengHuangWeather.xml", new String(data2));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.obj = provinceStream;
				handler.sendMessage(msg);
			};
		}.start();
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.prase_btn) {
				String provinceString = FileUtils.read(getApplicationContext(),
						"RegionProvince.xml");
				// System.out.println(provinceString);
				if (!StringUtils.isEmpty(provinceString)) {
					try {
						System.out.println("Start parsing province...");
						provinceList = BaseLocation.toBaseLocations(XMLReader
								.readToStringList(provinceString));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getApplicationContext(),
							android.R.layout.simple_spinner_item,
							BaseLocation.baseNames(provinceList));
					provinceSpinner.setAdapter(adapter);
				}
			}
			if (v.getId() == R.id.suggest_btn) {
				weatherApplication.dataMap.put("localWeatherInfo", info);
				startActivity(new Intent(MainActivity.this,
						SecondActivity.class));
			}
			if (v.getId() == R.id.weather_btn) {
				startActivity(new Intent(MainActivity.this,
						WeatherActivity.class));
			}
		}
	};

	private OnItemSelectedListener spListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				final int position, long id) {
			Toast.makeText(getApplicationContext(),
					"Get " + provinceList.get(position).getBaseName(),
					Toast.LENGTH_SHORT).show();

			final Handler handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {

					String cityString = FileUtils.read(getApplicationContext(),
							provinceList.get(position).getBaseName() + ".xml");
					if (!StringUtils.isEmpty(cityString)) {
						try {
							System.out.println("Start parsing city...");
							cityList = BaseLocation.toBaseLocations(XMLReader
									.readToStringList(cityString));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								getApplicationContext(),
								android.R.layout.simple_spinner_dropdown_item,
								BaseLocation.baseNames(cityList));
						citySpinner.setAdapter(adapter);
					}

					super.handleMessage(msg);
				}

			};

			new Thread() {
				public void run() {
					try {
						byte[] data1 = StreamTool.read(ApiClient
								.getSupportCity(provinceList.get(position)
										.getBaseCode()));
						FileUtils.write(getApplicationContext(), provinceList
								.get(position).getBaseName() + ".xml",
								new String(data1));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler.sendEmptyMessage(0);
				};
			}.start();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	private OnItemSelectedListener cpListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				final int position, long id) {

			final Handler handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {

					String weatherString = FileUtils.read(
							getApplicationContext(), cityList.get(position)
									.getBaseName() + ".xml");
					if (!StringUtils.isEmpty(weatherString)) {
						try {
							System.out.println("Start parsing weather info...");
							weatherList = XMLReader
									.readToStringList(weatherString);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						StringBuffer weatherBuffer = new StringBuffer();
						weatherBuffer.append("Lines:" + weatherList.size())
								.append("\n");
						info = new WeatherInfo(weatherList);

						weatherBuffer.append("LastRefresh time:"
								+ info.getLastRefreshTime());
						for (String weatherInfo : weatherList) {
							weatherBuffer.append(weatherInfo).append("\n");
						}
						weatherInfoTextView.setText(weatherBuffer.toString());

					}

					super.handleMessage(msg);
				}

			};

			new Thread() {
				public void run() {
					try {
						byte[] data1 = StreamTool.read(ApiClient
								.getWeather(cityList.get(position)
										.getBaseCode()));
						FileUtils.write(getApplicationContext(),
								cityList.get(position).getBaseName() + ".xml",
								new String(data1));
						handler.sendEmptyMessage(0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(getApplicationContext(),
								"Failed to get the weather infomation!",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				};
			}.start();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};
}
