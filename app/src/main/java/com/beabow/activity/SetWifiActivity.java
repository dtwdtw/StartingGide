package com.beabow.activity;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.beabow.AppContext;
import com.beabow.adapter.WifiAdapter;
import com.beabow.register.R;
import com.beabow.utils.WifiAdmin;
import com.beabow.utils.WifiCipherType;

public class SetWifiActivity extends Activity implements OnClickListener {
	
	
	private ProgressDialog waitingDialog;
	// wifi开关控件
	private Switch switch_wifi;
	// wifi列表Adapter
	private WifiAdapter wifiAdapter;
	// wifi列表
	private ListView listView;

	// wify管理类
	private WifiAdmin wifiAdmin;

	private TextView other_networks;

	private Button button_next;

	private Spinner spinner_security;

	private ArrayAdapter<String> adapter;

	String[] securitys = { "None", "WEP", "WPA/WPA2 PSK", "WAPI PSK" };
	private Context context;
	private EditText edit_ssid;
	private EditText edit_identity;
	private EditText edit_passwd;
	private LinearLayout identity;
	private LinearLayout passwd;
	private Button button_back;
	private TextView skip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setwifi);
		wifiAdmin = new WifiAdmin(this) {
			
			@Override
			public void onNotifyWifiConnected() {
				
				
			}
			
			@Override
			public void onNotifyWifiConnectFailed() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {
				SetWifiActivity.this.unregisterReceiver(receiver);
				
			}
			
			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
				SetWifiActivity.this.registerReceiver(receiver, filter); 
				return null;
			}
		};
		wifiAdmin.startScan();
		listView = (ListView) findViewById(R.id.listView);
		switch_wifi = (Switch) findViewById(R.id.switch_wifi);
		other_networks = (TextView) findViewById(R.id.other_networks);
		other_networks.setText(Html.fromHtml("<u>" + getString(R.string.other_networks) + "</u>"));
		other_networks.setOnClickListener(this);
		button_next = (Button) findViewById(R.id.button_next);
		button_next.setOnClickListener(this);
		button_back = (Button) findViewById(R.id.button_back);
		button_back.setOnClickListener(this);
		
		skip = (TextView) findViewById(R.id.skip);
		skip.setOnClickListener(this);
		
		if (wifiAdmin.mWifiManager.WIFI_STATE_DISABLED == wifiAdmin.checkState()) {
			switch_wifi.setChecked(false);
		} else {
			switch_wifi.setChecked(true);
		}
		wifiAdapter = new WifiAdapter(wifiAdmin, listView, this, switch_wifi);
		listView.setAdapter(wifiAdapter);
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next:
			// startActivity(new Intent(
			// android.provider.Settings.ACTION_WIRELESS_SETTINGS));
			Intent intent = new Intent(this, SignUpActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.other_networks:
			showSsidDialog();
			break;
		case R.id.button_back:
			intent = new Intent(this, SetTimeActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.skip:
			intent = new Intent(this, SignUpActivity.class);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}

	}

	/**
	 * 显示输入其他网络账号密码
	 */
	public void showSsidDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.other_networks_dialog, (ViewGroup) findViewById(R.id.other_networks_dialog));

		edit_ssid = (EditText) layout.findViewById(R.id.edit_ssid);
		edit_identity = (EditText) layout.findViewById(R.id.edit_identity);
		edit_passwd = (EditText) layout.findViewById(R.id.edit_passwd);
		identity = (LinearLayout) layout.findViewById(R.id.identity);
		passwd = (LinearLayout) layout.findViewById(R.id.passwd);

		spinner_security = (Spinner) layout.findViewById(R.id.spinner_security);
		// 为下拉列表定义一个适配器，这里就用到里前面定义的list。
		adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, securitys);
		// 为适配器设置下拉列表下拉时的菜单样式。
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将适配器添加到下拉列表上
		spinner_security.setAdapter(adapter);
		spinner_security.setSelection(getIntent().getIntExtra("select", 0));
		// 为下拉列表设置各种事件的响应，这个事响应菜单被选中
		spinner_security.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String item = securitys[position];
				switch (position) {
				case 0:
					identity.setVisibility(View.GONE);
					passwd.setVisibility(View.GONE);
					break;
				case 1:
					identity.setVisibility(View.GONE);
					passwd.setVisibility(View.VISIBLE);
					break;
				case 2:
					identity.setVisibility(View.GONE);
					passwd.setVisibility(View.VISIBLE);
					break;
				case 4:
					identity.setVisibility(View.GONE);
					passwd.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		new AlertDialog.Builder(this).setTitle(R.string.add_network).setView(layout).setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String ssid = edit_ssid.getText().toString();
				final String identity = edit_identity.getText().toString();
				final String passwd = edit_passwd.getText().toString();
				if ("".equals(ssid) || null == ssid) {
					showToast(getString(R.string.password_er));
					stop(dialog);
					return;
				}

				if (!"None".equals(identity)) {
					if(passwd ==null || passwd.isEmpty()){
						Toast.makeText(SetWifiActivity.this, getString(R.string.password_er), Toast.LENGTH_SHORT);
						stop(dialog);
						return;
					}
					
				}
				cancel(dialog);
				new Thread(){
					public void run(){
						if("None".equals(identity)){
							//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(ssid, passwd, WifiAdmin.TYPE_NO_PASSWD));
							wifiAdmin.Connect(ssid, passwd, WifiCipherType.WIFICIPHER_NOPASS);
						}else if("WEP".equals(identity)){
							wifiAdmin.Connect(ssid, passwd, WifiCipherType.WIFICIPHER_WEP);
							//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(ssid, passwd, WifiAdmin.TYPE_WEP));
						}else if("WPA/WPA2 PSK".equals(identity)){
							wifiAdmin.Connect(ssid, passwd, WifiCipherType.WIFICIPHER_WPA);
							//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(ssid, passwd, WifiAdmin.TYPE_WPA));
						}else if("WAPI PSK".equals(identity)){
							wifiAdmin.Connect(ssid, passwd, WifiCipherType.WIFICIPHER_WPA);
							//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(ssid, passwd, WifiAdmin.TYPE_WPA));
						}else{
							wifiAdmin.Connect(ssid, passwd, WifiCipherType.WIFICIPHER_WPA);
						}
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						int i = 0;
						while(true){
							wifiAdmin.startScan();
							int id = wifiAdmin.getNetWordId();
							if(id ==0){
								if(i < 15){
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									i++;
									continue;
								}else{
									break;
								}
							}else{
								break;
							}
							
						}
						handler.sendEmptyMessage(OPEN_WIFI);
					}
					
				}.start();
				
				
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancel(dialog);
			}
		}).setCancelable(false).show();
	}
	
	
	protected static final int OPEN_WIFI = 100000;
	protected static final int CLOSE_WIFI = 100001;
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPEN_WIFI:
				wifiAdapter.notifyDataSetChanged();
				break;
			case CLOSE_WIFI:
				break;

			default:
				break;
			}
			dismissWaitingDialog();
		}

	};
	
	/**
	 * 阻止关闭对话框
	 * @param dialog
	 */
	public void stop(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 显示提示
	 * @param text
	 */
	public void showToast(String text){
		Toast.makeText(SetWifiActivity.this, text, Toast.LENGTH_LONG);
	}
	
	/**
	 * 关闭对话框
	 * @param dialog
	 */
	public void cancel(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 显示加载效果
	 */
	public void showWaitingDialog() {
		if (waitingDialog == null) {
			waitingDialog = new ProgressDialog(this);
			waitingDialog.setMessage(this.getString(R.string.loding));
			waitingDialog.setCancelable(false);
		}
		waitingDialog.show();
	}
	
	/**
	 * 取消加载效果
	 */
	public void dismissWaitingDialog() {
		if (waitingDialog != null && waitingDialog.isShowing()) {
			waitingDialog.dismiss();
		}
	}
	
	public void close(){
		switch_wifi.setChecked(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		AppContext.activityList.add(this);
	}
}
