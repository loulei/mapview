package com.example.mapdemo.location;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.example.mapdemo.bean.RouterBean;
import com.example.mapdemo.utils.WifiUtils;

public class WifiReceiver extends BroadcastReceiver {
	
//	private List<ScanResult> scanResults;
	private WifiManager wifiManager;
	private Handler mHandler;
	private List<RouterBean> routerBeans;
	
	public WifiReceiver(WifiManager wifiManager, Handler handler){
		this.wifiManager = wifiManager;
		this.mHandler = handler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		routerBeans = new ArrayList<RouterBean>();
		List<ScanResult> scanResults = wifiManager.getScanResults();
		for(ScanResult result : scanResults){
			RouterBean routerBean = new RouterBean();
			routerBean.setSsid(result.SSID);
			routerBean.setBssid(result.BSSID);
			routerBean.setPercent(WifiUtils.calculateSignal(result.level));
			routerBeans.add(routerBean);
		}
		mHandler.sendMessage(mHandler.obtainMessage(2000, routerBeans));
	}

}
