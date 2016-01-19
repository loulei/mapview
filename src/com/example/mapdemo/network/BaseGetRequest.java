package com.example.mapdemo.network;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.example.mapdemo.utils.Logger;

public class BaseGetRequest<T> extends JsonRequest<T> {
	
	private Response.Listener<T> listener;
	private Class<T> clazz;
	
	public BaseGetRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener, Class<T> respClass) {
		super(Method.GET, url, null, listener, errorListener);
		this.listener = listener;
		this.clazz = respClass;
		Logger.d("url:"+url);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		try {
			String ret = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			Logger.d("ret:"+ret);
			T t = JSON.parseObject(ret, clazz);
			return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}
	
	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		listener.onResponse(response);
	}
}




























