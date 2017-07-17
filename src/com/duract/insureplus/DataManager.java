package com.duract.insureplus;

import org.json.JSONArray;

import android.util.Log;

public class DataManager {

	private static String USER_VALUE;

	public static String getDetails(String key, String sub, String jsonString) {

		try {
			USER_VALUE = new JSONArray(jsonString).getJSONObject(0)
					.getJSONObject(sub).getString(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("DETAILS - " + key, USER_VALUE);
		return USER_VALUE.trim();
	}
}
