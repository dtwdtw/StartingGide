package com.beabow.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beabow.AppContext;
import com.beabow.http.HttpHelper;
import com.beabow.http.HttpParser;
import com.beabow.http.JsonResult;
import com.beabow.register.R;
import com.beabow.utils.CryptoTools;

public class SignUpActivity extends Activity implements OnClickListener {

	private EditText email;
	private EditText password;
	private EditText confirm_password;
	private TextView terms;
	private TextView skip;
	private Button button_next;
	private Button button_back;
	private ProgressDialog waitingDialog;

	private CheckBox checkBox;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		confirm_password = (EditText) findViewById(R.id.confirm_password);
		terms = (TextView) findViewById(R.id.terms);
		terms.setOnClickListener(this);
		button_next = (Button) findViewById(R.id.button_next);
		button_next.setOnClickListener(this);
		button_next.setClickable(false);
		button_next.setTextColor(getResources().getColor(R.color.dark_gray));
		button_back = (Button) findViewById(R.id.button_back);
		button_back.setOnClickListener(this);

		skip = (TextView) findViewById(R.id.skip);
		skip.setOnClickListener(this);

		checkBox = (CheckBox) findViewById(R.id.checkBox);
		// 选择事件
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					button_next.setClickable(true);
					button_next.setTextColor(SignUpActivity.this.getResources().getColor(R.color.text_white));
				} else {
					button_next.setClickable(false);
					button_next.setTextColor(SignUpActivity.this.getResources().getColor(R.color.dark_gray));
				}

			}
		});
	}

	@Override
	public void onClick(View v) {

		Intent intent = null;
		switch (v.getId()) {
		case R.id.button_next:
			if (email.getText().toString().isEmpty()) {
				Toast.makeText(this, getString(R.string.please_enter_the_email), Toast.LENGTH_SHORT).show();
				return;
			}
			if (password.getText().toString().isEmpty()) {
				Toast.makeText(this, getString(R.string.please_enter_the_password), Toast.LENGTH_SHORT).show();
				return;
			}
			if (confirm_password.getText().toString().isEmpty()) {
				Toast.makeText(this, getString(R.string.please_enter_the_confirm_password), Toast.LENGTH_SHORT).show();
				return;
			}
			if (!confirm_password.getText().toString().equals(password.getText().toString())) {
				Toast.makeText(this, getString(R.string.the_two_passwords_differ), Toast.LENGTH_SHORT).show();
				return;
			}
			showWaitingDialog();
			new Thread("login") {
				@Override
				public void run() {
					// 设备唯一ID
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String code = tm.getDeviceId();
					if (TextUtils.isEmpty(code)) {
						 WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);     
					     WifiInfo info = wifi.                                                                                             getConnectionInfo();
					     code = info.getMacAddress();
					}
					// String loginUrl = listUrl.getGet_user();
					try {
						CryptoTools des = new CryptoTools("95362111");// 自定义密钥
						String username = email.getText().toString();
						String key = des.encode(username);
						String pwd = des.encode(password.getText().toString());
						// String url =
						// ""http://www.api.irulu.com/api/register?lang=11&username=bb&password=cc&key=dd&token=ee&time=ff&itype=ss
						StringBuffer sb = new StringBuffer("http://api.irulu.com/api/a.a?lang=en");
						sb.append("&account=100001");
						sb.append("&username=");
						sb.append(username);
						sb.append("&password=");
						sb.append(pwd);
						sb.append("&key=");
						sb.append(key);
						sb.append("&token=");
						sb.append("1BAD9D713E674234B2ECB48949DAA10A");
						sb.append("&time=");
						sb.append(AppContext.date.getTime());
						sb.append("&itype=1");
						sb.append("&code=");
						sb.append(code);
						//sb.append("ss");
						//Map<String, String> params = new HashMap<String, String>();
						String content = HttpHelper.executeGet(sb.toString());
						//Toast.makeText(SignUpActivity.this, content, Toast.LENGTH_LONG).show();
                        Log.i("register-------",sb.toString());
						if (content != null) {
                            Log.i("register-------",content);
							JsonResult result = HttpParser.parse(SignUpActivity.this, content);
							if (1 == result.getStatus()) {
								handler.sendEmptyMessage(WHAT_REGISTER_SUCCESSFULLY);
								return;
							} else if (0 == result.getStatus()) {
								handler.sendEmptyMessage(WHAT_REGISTER_FAILED);
								return;
							} else {
								handler.sendEmptyMessage(WHAT_REGISTER_FAILED);
                                Log.i("register-------", "empty");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();

			new Thread() {
				public void run() {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(WHAT_REGISTER_FAILED);
				}
			}.start();

			// Intent intent = new Intent(SignUpActivity.this,
			// WelcomeActivity.class);
			// startActivity(intent);
			// this.finish();
			break;
		case R.id.button_back:
			intent = new Intent(SignUpActivity.this, SetWifiActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.terms:
			intent = new Intent(SignUpActivity.this, WebActivity.class);
			startActivity(intent);
			break;

		case R.id.skip:
			intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}
	}

	private static final int WHAT_REGISTER_SUCCESSFULLY = 100000;
	private static final int WHAT_REGISTER_FAILED = 100001;
	private static final int WHAT_REGISTER_ACCOUNT_THERE = 100002;
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_REGISTER_SUCCESSFULLY:
				Toast.makeText(SignUpActivity.this, getString(R.string.ok), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
				startActivity(intent);
				break;
			case WHAT_REGISTER_FAILED:
				if (waitingDialog != null) {
					if (waitingDialog.isShowing()) {
						dismissWaitingDialog();
					}
				}
				Toast.makeText(SignUpActivity.this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
				break;
			case WHAT_REGISTER_ACCOUNT_THERE:
				if (waitingDialog != null) {
					if (waitingDialog.isShowing()) {
						dismissWaitingDialog();
					}
				}
				Toast.makeText(SignUpActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

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

	@Override
	protected void onResume() {
		super.onResume();

		AppContext.activityList.add(this);
	}
}
