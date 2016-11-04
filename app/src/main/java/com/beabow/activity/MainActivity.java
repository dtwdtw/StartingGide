package com.beabow.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beabow.register.R;


public class MainActivity extends Activity implements OnClickListener {

	private Spinner language_spinner;
	private String[] langs = { "English", "中文简体", "中文簡體" };
	private ArrayAdapter<String> adapter;
	private TextView tv;
	boolean b = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		language_spinner = (Spinner) findViewById(R.id.language_spinner);

		// 为下拉列表定义一个适配器，这里就用到里前面定义的list。
		adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, langs);
		// 为适配器设置下拉列表下拉时的菜单样式。
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将适配器添加到下拉列表上
		language_spinner.setAdapter(adapter);
		language_spinner.setSelection(getIntent().getIntExtra("select", 0));
		// 为下拉列表设置各种事件的响应，这个事响应菜单被选中
		language_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (b) {
					Log.i("setOnItemSelectedListener", position + "");
					switch (position) {
					case 0:
						switchLanguage(Locale.US);
						//updateLanguage(Locale.US);
						break;
					case 1:
						switchLanguage(Locale.SIMPLIFIED_CHINESE);
						//updateLanguage(Locale.SIMPLIFIED_CHINESE);
						break;
					case 2:
						switchLanguage(Locale.TAIWAN);
						//updateLanguage(Locale.TAIWAN);
						break;
					default:
						break;
					}

					Intent intent = new Intent(MainActivity.this, SetTimeActivity.class);
					intent.putExtra("select", position);
					startActivity(intent);
					Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_LONG).show();
				}
				b = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

	}


	public void switchLanguage(Locale locale) {
		Resources resources = getResources();// 获得res资源对象
		Configuration config = resources.getConfiguration();// 获得设置对象
		DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
		config.locale = locale; // 简体中文
		resources.updateConfiguration(config, dm);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
