package com.beabow.http;

import org.json.JSONObject;

import android.content.Context;

public class HttpParser {

	public static JsonResult parse(Context context, String content) {
		JsonResult jsonResult = new JsonResult();
		if(content != null){
			try {
				JSONObject json = new JSONObject(content);
				if (json.has("status")) {
					jsonResult.setStatus(json.getInt("status"));
				}
				if (json.has("message")) {
					jsonResult.setMessage(json.getString("message"));
				}
				
			} catch (Exception e) {
				
			}
		}
		
		return jsonResult;
	}

}
