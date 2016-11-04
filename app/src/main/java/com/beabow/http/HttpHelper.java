package com.beabow.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.dshu.plugin.http.HttpExecutor;
import com.dshu.plugin.http.HttpResult;
import com.dshu.plugin.utils.Tracker;

/**
 * Created by Dshu.shu on 14-4-9.
 */
public class HttpHelper {

	private static final String TAG = "HttpHelper";
	private static ConnectivityManager mConnectivityManager;
	private static NetworkInfo mNetworkInfo;
	
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	
	public static String executeGet(String url) {
        String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
 
            StringBuffer strBuffer = new StringBuffer("");
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
            Log.i("ximoon", result + " ---  excute");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
 
        return result;
    }

	
	/**
	 * 
	 * @param context
	 * @return 10 没有网络 20:WIFI 5:手机网络
	 */
	public static int getNetworkType(Context context){
		if(isNetworkConnected(context)){
			if(mNetworkInfo !=null&&mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
				return 30;
			}else if(mNetworkInfo !=null && mNetworkInfo.getType() ==  ConnectivityManager.TYPE_MOBILE){
				return 5;
			}else{
				return 10;
			}
		}else{
			return 10;
		}
		
	}
	
	public static String executePost(String url, Map<String, String> params,
			int timeout) {
		try {
			//Tracker.debug("executePost: " + url);
			HttpEntity entity = null;
			if (params != null) {
				Tracker.debug("params: " + params.toString());
				entity = HttpExecutor.createHttpEntity(params);
			}
			HttpResult result = HttpExecutor.executePost(url, entity, timeout);
			if (HttpStatus.SC_OK == result.getStatusCode()) {
				return EntityUtils.toString(result.getEntity());
			} else {
				Tracker.debug("Http status error: " + result.getStatusCode());
			}
		} catch (SocketException e) {
			Tracker.debug("request failed", e);
		} catch (SocketTimeoutException e) {
			Tracker.debug("response timeout", e);
		} catch (IOException e) {
			Tracker.debug("Unknown", e);
		}
		return null;
	}

	public static void download(Context context, String url, File file,
			int timeout) {
		Intent intent = new Intent();
		intent.putExtra("EXTRA_DOWNLOAD_ID", url);
		FileOutputStream out = null;
		InputStream in = null;
		try {
			try {
				HttpResult result = HttpExecutor.executeGet(url, timeout);
				if (200 == result.getStatusCode()) {
					HttpEntity entity = result.getEntity();
					if (!entity.getContentType().toString()
							.contains("text/html")) {
						in = entity.getContent();
						out = new FileOutputStream(file);
						intent.setAction("ACTION_DOWNLOAD_PROGRESS");
						long currentByte = 0L;
						long totalByte = entity.getContentLength();

						byte[] buffer = new byte[10240];
						int length;
						while ((length = in.read(buffer)) != -1) {
							out.write(buffer, 0, length);
							currentByte += length;
							intent.putExtra("EXTRA_DOWNLOAD_CURRENT_BYTE",
									currentByte);
							intent.putExtra("EXTRA_DOWNLOAD_TOTAL_BYTE",
									totalByte);
							context.sendBroadcast(intent);
						}
						in.close();
						intent.setAction("ACTION_DOWNLOAD_COMPLETE");
						context.sendBroadcast(intent);

						out.close();

						if (in != null) {
							in.close();
						}
						return;
					}
					throw new IOException("Http file error: \n"
							+ EntityUtils.toString(entity));
				}

				throw new IOException("Http status code error: "
						+ result.getStatusCode());
			} finally {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			}
		} catch (Exception e) {
			Tracker.debug("Download failed: " + url, e);
			if ((file != null) && (file.exists()) && (file.isFile())) {
				file.delete();
			}

			intent.setAction("ACTION_DOWNLOAD_FAILED");
			context.sendBroadcast(intent);
		}
	}

	

	
	
	

}
