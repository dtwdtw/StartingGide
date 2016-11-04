package com.beabow.adapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.beabow.activity.SetWifiActivity;
import com.beabow.register.R;
import com.beabow.utils.WifiAdmin;
import com.beabow.utils.WifiCipherType;

public class WifiAdapter extends BaseAdapter {
	private List<ScanResult> mWifiList;

	private ProgressDialog waitingDialog;
	private WifiAdmin wifiAdmin;
	private SetWifiActivity context;
	private ListView listView;
	private Switch switch_wifi;
	// 结果
	private ScanResult mScanResult;
	// 密码输入框
	private EditText passwd;

	public WifiAdapter(WifiAdmin wifiAdmin, ListView listView, SetWifiActivity context, Switch switch_wifi) {

		this.wifiAdmin = wifiAdmin;
		if (wifiAdmin.checkState() != wifiAdmin.mWifiManager.WIFI_STATE_ENABLED) {
			this.mWifiList = new ArrayList<ScanResult>();
		} else {
			this.mWifiList = wifiAdmin.getWifiList();
		}

		this.context = context;
		this.listView = listView;
		// 每个item点击事件，点击弹出密码输入框
		this.listView.setOnItemClickListener(new MyOnItemClickListener());

		this.switch_wifi = switch_wifi;
		// wifi开关点击事件
		this.switch_wifi.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
		
		new Thread(){
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(REFRESH_WIFI);
				}
			}
		}.start();
		

	}

	public WifiAdapter() {

	}

	protected static final int OPEN_WIFI = 100000;
	protected static final int CLOSE_WIFI = 100001;
	protected static final int REFRESH_WIFI = 100002;
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPEN_WIFI:
				mWifiList = wifiAdmin.getWifiList();
				notifyDataSetChanged();
				break;
			case CLOSE_WIFI:
				mWifiList.clear();
				notifyDataSetChanged();
				break;
			case REFRESH_WIFI:
				if (!wifiAdmin.mWifiManager.isWifiEnabled()) {
					context.close();
				}else{
					mWifiList = wifiAdmin.getWifiList();
					if(mWifiList !=null){
						notifyDataSetChanged();
					}
				}
				break;
			default:
				break;
			}
			dismissWaitingDialog();
		}

	};

	@Override
	public int getCount() {

		return mWifiList.size();
	}

	@Override
	public Object getItem(int position) {
		return mWifiList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		mScanResult = (ScanResult) getItem(position);

		if (convertView == null) {
			ViewHolder holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_wifi, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.secured = (TextView) convertView.findViewById(R.id.secured);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.state = (ImageView) convertView.findViewById(R.id.state);
			
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.title.setText(mScanResult.SSID);
		String secured = wifiAdmin.getSecured(mScanResult);
		if("".equals(secured)){
			holder.secured.setVisibility(View.GONE);
			
			
		}else{
			holder.secured.setVisibility(View.VISIBLE);
			holder.secured.setText(secured);
		}
		Log.i(" mScanResult.SSID",  mScanResult.SSID);
		Log.i(" wifiAdmin.getSSID()",  wifiAdmin.getSSID());
		if (("\"" + mScanResult.SSID + "\"").equals(wifiAdmin.getSSID())) {
			holder.state.setVisibility(View.VISIBLE);
			holder.state.setImageResource(R.drawable.ico_connect);
		} else {
			if("".equals(secured)){
				holder.state.setVisibility(View.GONE);
			}else{
				holder.state.setVisibility(View.VISIBLE);
				holder.state.setImageResource(R.drawable.ico_lock);
			}
			
			

		}
		switch (wifiAdmin.getLevel(mScanResult, 4)) {
		case 0:
			holder.image.setImageResource(R.drawable.wifi1);
			break;
		case 1:
			holder.image.setImageResource(R.drawable.wifi2);
			break;
		case 2:
			holder.image.setImageResource(R.drawable.wifi3);
			break;
		case 3:
			holder.image.setImageResource(R.drawable.wifi);
			break;

		default:
			holder.image.setImageResource(R.drawable.wifi1);
		}
		return convertView;

	}

	public class ViewHolder {
		public ImageView image;
		public ImageView state;
		public TextView title;
		public TextView secured;
	}

	public class MyOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ScanResult mScanResult = (ScanResult) getItem(position);
			String secured = wifiAdmin.getSecured(mScanResult);
			if("".equals(secured)){
				boolean b =  wifiAdmin.Connect(mScanResult.SSID, null, WifiCipherType.WIFICIPHER_NOPASS);
				//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(mScanResult.SSID, "", WifiAdmin.TYPE_NO_PASSWD));
				//Log.i("onItemClick", "b:"+b);
			}else{
				passwd = new EditText(context);
				passwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				passwd.setHint(R.string.password);
				new AlertDialog.Builder(context).setTitle(mScanResult.SSID)
				// .setIcon(android.R.drawable.ic_dialog_info)
						.setView(passwd)
						// 连接按钮
						.setPositiveButton(R.string.connect, new MyOnClickListener(mScanResult, passwd,secured))
						// 取消按钮
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								cancel(dialog);
							}
						}).show();
			}
			
			
		}

	}

	public class MyOnClickListener implements DialogInterface.OnClickListener {
		// 结果
		private ScanResult mScanResult;
		// 密码输入框
		private EditText passwd;
		//网络加密类型
		private String secured;
		public MyOnClickListener() {

		}

		public MyOnClickListener(ScanResult mScanResult, EditText passwd,String secured) {
			this.mScanResult = mScanResult;
			this.passwd = passwd;
			this.secured = secured;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(!"".equals(secured)){
				if(this.passwd.getText().toString().isEmpty()){
					Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show();
					stop(dialog);
					return;
				}
			}
			
			
			showWaitingDialog();
			cancel(dialog);
			new Thread(){
				public void run() {
					
					
					if("".equals(secured)){
						wifiAdmin.Connect(mScanResult.SSID, passwd.getText().toString(), WifiCipherType.WIFICIPHER_NOPASS);
						//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(mScanResult.SSID, passwd.getText().toString(), WifiAdmin.TYPE_NO_PASSWD));
					}else if("WPA".equals(secured)){
						wifiAdmin.Connect(mScanResult.SSID, passwd.getText().toString(), WifiCipherType.WIFICIPHER_WPA);
						//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(mScanResult.SSID, passwd.getText().toString(), WifiAdmin.TYPE_WPA));
					}else if("WPA/WPA2".equals(secured)){
						wifiAdmin.Connect(mScanResult.SSID, passwd.getText().toString(), WifiCipherType.WIFICIPHER_WPA);
						//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(mScanResult.SSID, passwd.getText().toString(), WifiAdmin.TYPE_WPA));
					}else if("WEP".equals(secured)){
						wifiAdmin.Connect(mScanResult.SSID, passwd.getText().toString(), WifiCipherType.WIFICIPHER_WEP);
						//wifiAdmin.addNetwork(wifiAdmin.createWifiInfo(mScanResult.SSID, passwd.getText().toString(), WifiAdmin.TYPE_WEP));
					} else if("WPA2".equals(secured)) {
						Log.i("WPA2 type wifi", "secured = " + secured);
						wifiAdmin.Connect(mScanResult.SSID, passwd.getText().toString(), WifiCipherType.WIFICIPHER_WPA2);
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
	}

	
	
	/**
	 * wifi开关
	 * 
	 * @author tony
	 *
	 */
	public class MyOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// Toast.makeText(context, "onCheckedChanged",
			// Toast.LENGTH_SHORT).show();
			if (isChecked) {
				showWaitingDialog();
				// Toast.makeText(context, "openWifi",
				// Toast.LENGTH_SHORT).show();
				// 打开wifi
				new Thread() {
					public void run() {
						wifiAdmin.openWifi();
						while (true) {
							wifiAdmin.startScan();
							mWifiList = wifiAdmin.getWifiList();
							if (mWifiList.isEmpty()) {
								try {
									Thread.sleep(6000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								continue;
							}
							break;
						}
						handler.sendEmptyMessage(OPEN_WIFI);
					}
				}.start();

			} else {
				showWaitingDialog();

				handler.sendEmptyMessage(CLOSE_WIFI);
				new Thread() {
					public void run() {
						wifiAdmin.closeWifi();
					}
				}.start();
			}
		}
	}

	public void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示加载效果
	 */
	public void showWaitingDialog() {
		if (waitingDialog == null) {
			waitingDialog = new ProgressDialog(context);
			waitingDialog.setMessage(context.getString(R.string.loding));
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
}
