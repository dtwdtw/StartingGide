package com.beabow.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.beabow.AppContext;
import com.beabow.register.R;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				for(Activity activity : AppContext.activityList)
					activity.finish();
				deleteFromPackageManger();
			}
		}, 500);  
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		AppContext.activityList.add(this);
	}
	
	private void deleteFromPackageManger() {
		PackageManager pm = getPackageManager();
		ComponentName name = new ComponentName(this, StartupAvtivity.class);
		pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
	}
}
