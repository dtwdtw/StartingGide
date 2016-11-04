package com.beabow;



import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.dshu.plugin.utils.ExceptionHandler;
import com.dshu.plugin.utils.Tracker;


public class AppContext extends Application {
	
	public static AppContext context;
	
	public static Date date = new Date();
	public static List<Activity> activityList=new LinkedList<Activity>();
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        init();
       // initImageLoader(context);
    }
    
    public static AppContext getInstance() {
        return context;
    }
    private void init() {
        Tracker.DEBUG_ABLE = false;
        Tracker.packageName = getPackageName();
        ExceptionHandler.hook(this);
    }
    
    /*public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }*/
}
