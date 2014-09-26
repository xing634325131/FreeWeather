package com.kingtime.freeweather.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.kingtime.freeweather.R;
import com.kingtime.freeweather.api.ApiClient;
import com.kingtime.freeweather.entity.Province;
import com.kingtime.freeweather.utils.FileUtils;
import com.kingtime.freeweather.utils.StreamTool;
import com.kingtime.freeweather.utils.StringUtils;
import com.kingtime.freeweather.utils.XMLReader;

import android.R.integer;
import android.app.Activity;
import android.graphics.Color;
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

	private TextView statusTextView;
	private Button parseDataButton;
	private Spinner provinceSpinner;
	private Spinner citySpinner;
	private final static String TAG = "MainActivity";

	private List<Province> provinceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		System.out.println(TAG + " start!");
		initView();
		// loadData();
	}

	private void initView() {
		statusTextView = (TextView) findViewById(R.id.status_tv);
		parseDataButton = (Button) findViewById(R.id.prase_btn);
		provinceSpinner = (Spinner) findViewById(R.id.province_sp);
		citySpinner = (Spinner) findViewById(R.id.city_sp);

		parseDataButton.setOnClickListener(listener);
		provinceSpinner.setOnItemSelectedListener(spListener);

		provinceList = new ArrayList<Province>();
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
						System.out.println("Start parsing...");
						provinceList = Province.toProvinces(XMLReader
								.readToStringList(provinceString));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getApplicationContext(),
							android.R.layout.simple_spinner_item,
							Province.provinceNames(provinceList));
					provinceSpinner.setAdapter(adapter);
				}
			}

		}
	};

	private OnItemSelectedListener spListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				final int position, long id) {
			Toast.makeText(getApplicationContext(),
					"Get " + provinceList.get(position).getProvinceName(),
					Toast.LENGTH_SHORT).show();

			final Handler handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
				}

			};

			new Thread() {
				public void run() {
					InputStream provinceStream = null;
					try {
						byte[] data1 = StreamTool.read(ApiClient
								.getSupportCity(provinceList.get(position)
										.getProvinceCode()));
						FileUtils.write(getApplicationContext(), provinceList
								.get(position).getProvinceName() + ".xml",
								new String(data1));
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

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};
}
