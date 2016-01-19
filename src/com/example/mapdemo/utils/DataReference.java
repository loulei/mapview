package com.example.mapdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataReference {
	private static final String SHARE_PRE = "profile";

	public static final String WIDTH = "map_width";
	public static final String HEIGHT = "map_height";

	private static Context mContext;

	private static DataReference dataReference;

	private DataReference() {
	}

	public synchronized static DataReference getInstance(Context context) {
		if (dataReference == null) {
			dataReference = new DataReference();
			mContext = context;
		}
		return dataReference;
	}

	public String loadString(String key) {
		SharedPreferences sp = mContext.getSharedPreferences(SHARE_PRE,
				Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}
	
	public int loadInt(String key){
		SharedPreferences sp = mContext.getSharedPreferences(SHARE_PRE, Context.MODE_PRIVATE);
		return sp.getInt(key, -1);
	}
	
	public boolean saveData(String key, int value){
		try {
			SharedPreferences sp = mContext.getSharedPreferences(SHARE_PRE,
					Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putInt(key, value);
			editor.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean saveData(String key, String value) {
		try {
			SharedPreferences sp = mContext.getSharedPreferences(SHARE_PRE,
					Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(key, value);
			editor.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
