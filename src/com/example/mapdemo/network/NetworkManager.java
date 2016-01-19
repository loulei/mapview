package com.example.mapdemo.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkManager {
	public static RequestQueue queue = Volley.newRequestQueue(com.example.mapdemo.base.MainApp.getContext());
	
	private NetworkManager(){}
	
	public static void addRequest(Request<?> request, Object tag){
		if(tag != null){
			request.setTag(tag);
		}
		queue.add(request);
	}
	
	public static void cancelAll(Object tag){
		queue.cancelAll(tag);
	}
	
	
}















