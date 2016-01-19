package com.example.mapdemo.base;

import android.app.Application;
import android.content.Context;

public class MainApp extends Application {

	private static Context context;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
	}

	public static Context getContext() {
		return context;
	}
}
