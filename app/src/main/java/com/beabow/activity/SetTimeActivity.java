package com.beabow.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.beabow.AppContext;
import com.beabow.register.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class SetTimeActivity extends Activity implements OnClickListener {

	private Button button_next;
	private EditText edit_date;
	private EditText edit_time;
	private DatePickerDialog dateDialog;
	private TimePickerDialog timeDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settime);

		button_next = (Button) findViewById(R.id.button_next);
		button_next.setOnClickListener(this);

		edit_date = (EditText) findViewById(R.id.edit_date);// 日期选择器
		edit_date.setFocusable(false);
		edit_date.setOnClickListener(this);// /点击事件

		edit_time = (EditText) findViewById(R.id.edit_time);// 时间选择器
		edit_time.setFocusable(false);
		edit_time.setOnClickListener(this);// /点击事件

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next:
			/*if(edit_date.getText().toString().isEmpty()){
				Toast.makeText(this, getString(R.string.please_select_a_date), Toast.LENGTH_SHORT).show();
				return;
			}
			if(edit_time.getText().toString().isEmpty()){
				Toast.makeText(this, getString(R.string.please_select_time), Toast.LENGTH_SHORT).show();
				return;
			}*/
			
			if(!edit_date.getText().toString().isEmpty() && !edit_time.getText().toString().isEmpty()){
				String datestr = edit_date.getText().toString()+" "+edit_time.getText().toString();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm ");  
				try {
					Date date = sdf.parse(datestr);
					AppContext.date = date;
					
				} catch (ParseException e) {
					e.printStackTrace();
				}  
			}
			

			Intent intent = new Intent(SetTimeActivity.this, SetWifiActivity.class);
			startActivity(intent);
			break;
		case R.id.edit_date:
			showDatePickerDialog();
			break;
		case R.id.edit_time:
			showTimePickerDialog();
			break;
		default:
			break;
		}

	}

	/**
	 * 时间选择器
	 */
	private void showTimePickerDialog() {
		if (timeDialog == null) {
			Calendar mycalendar = Calendar.getInstance(Locale.US);
			Date mydate = new Date(); // 获取当前日期Date对象
			mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期
			timeDialog = new TimePickerDialog(SetTimeActivity.this, new OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					edit_time.setText(hourOfDay+":"+minute);

				}
			}, mycalendar.get(Calendar.HOUR), mycalendar.get(Calendar.MINUTE), true);
		}

		timeDialog.show();

	}

	/**
	 * 日期选择器
	 */
	private void showDatePickerDialog() {

		if (dateDialog == null) {
			Calendar mycalendar = Calendar.getInstance(Locale.US);
			Date mydate = new Date(); // 获取当前日期Date对象
			mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期

			dateDialog = new DatePickerDialog(SetTimeActivity.this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

					edit_date.setText(year + "-" + (monthOfYear + 1 < 9 ? "0" + (monthOfYear + 1) : "" + (monthOfYear + 1)) + "-"
							+ ((dayOfMonth) < 9 ? "0" + dayOfMonth : "" + dayOfMonth));
					Log.i("TonyMemberActivity", year + "-" + monthOfYear + "-" + dayOfMonth);

				}
			}, mycalendar.get(Calendar.YEAR), mycalendar.get(Calendar.MONTH), mycalendar.get(Calendar.DAY_OF_MONTH));
		}

		dateDialog.show();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		AppContext.activityList.add(this);
	}
}
