package com.beabow;

import com.beabow.activity.StartupAvtivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	static final String action_boot = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
//		if (intent.getAction().equals(action_boot)) {
//			SharedPreferences sharedata = context.getSharedPreferences("data", 0);
//			String data = sharedata.getString("name", "false");
//			if(!"true".equals(data)){
//				Intent ootStartIntent = new Intent(context, SetTimeActivity.class);
//				ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				context.startActivity(ootStartIntent);
//
//			}
//
//		}
		Intent start=new Intent(context, StartupAvtivity.class);
		start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(start);

	}

}
