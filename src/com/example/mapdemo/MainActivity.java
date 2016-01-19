package com.example.mapdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.example.mapdemo.bean.FindLocationResp;
import com.example.mapdemo.bean.RouterBean;
import com.example.mapdemo.bean.RouterListResp;
import com.example.mapdemo.location.WifiReceiver;
import com.example.mapdemo.network.BaseGetRequest;
import com.example.mapdemo.network.BasePostRequest;
import com.example.mapdemo.network.NetworkManager;
import com.example.mapdemo.network.NetworkUtil;
import com.example.mapdemo.utils.CachWifiQue;
import com.example.mapdemo.utils.DataReference;
import com.example.mapdemo.utils.Logger;
import com.example.mapdemo.view.CompassView;
import com.example.mapdemo.view.MultiTouchDrawable;
import com.example.mapdemo.view.MultiTouchView;
import com.example.mapdemo.view.RefreshableView;
import com.example.mapdemo.view.SiteMapDrawable;
import com.example.mapdemo.view.UserDrawable;

public class MainActivity extends Activity implements RefreshableView {
	private MultiTouchView touchView;
	private SiteMapDrawable mapDrawable;
	private ToggleButton tgbtn_auto;
	private CompassView compassview;
	private Button btn_resize;
	private TextView tv_log;
	private UserDrawable userDrawable;
	private WifiReceiver mReceiver;
	
	private static final int GRID_SPAC_LEN = 30;
	private int width;
	private int height;
	
	private int index = 0;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
//				userDrawable.setRelativePosition(mapDrawable.getWidth()*msg.arg1*1.0F/100, mapDrawable.getHeight()*msg.arg2*1.0F/100);
				++index;
				StringBuilder builder = new StringBuilder();
				builder.append("index:"+index+" ");
				builder.append("x="+msg.arg1+" y="+msg.arg2);
				tv_log.setText(builder.toString());
				userDrawable.setRelativePosition(GRID_SPAC_LEN*msg.arg1, GRID_SPAC_LEN*msg.arg2);
				break;
			case 2000:
				List<RouterBean> scanResults=(List<RouterBean>) msg.obj;
				processData(scanResults);
				break;
			default:
				break;
			}
		};
	};
	
	private List<RouterBean> mLstRouters;
	private List<RouterBean> mParams=new ArrayList<RouterBean>();
	private WifiManager mWifiManager;
	private int mDrop;
	private boolean isReg = false;
	private CachWifiQue cachWifiQue=CachWifiQue.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Logger.setLogLevel(Log.VERBOSE);
		Logger.tag = "Location";
		initView();
		initEvent();
		initData();
//		test();
	}

	private void initData() {
		mWifiManager=(WifiManager) getSystemService(Context.WIFI_SERVICE);
	 	mReceiver=new WifiReceiver(mWifiManager, handler);
	 	register();
		executeRequest(new BaseGetRequest<RouterListResp>(NetworkUtil.URL_BASE+NetworkUtil.URL_LIST, new Response.Listener<RouterListResp>() {

			@Override
			public void onResponse(RouterListResp arg0) {
				// TODO Auto-generated method stub
				if(arg0!=null){
					try{						
						mLstRouters=arg0.getRouters();
						handler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mWifiManager.startScan();
							}
						}, 1000*10);
						
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					Toast.makeText(MainActivity.this, "服务器返回数据为空!", Toast.LENGTH_SHORT).show();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "服务器连接失败!"+ arg0.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}, RouterListResp.class));
	}

	private void test() {
		new Thread(new Runnable() {
			int i = 0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(i<100){
						Thread.sleep(200);
						i++;
						handler.sendMessage(handler.obtainMessage(1000, i, i));
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void initEvent() {
		tgbtn_auto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					mapDrawable.startAutoRotate();
				}else{
					mapDrawable.stopAutoRotate();
				}
			}
		});
		btn_resize.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setView(LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_size, null));
				builder.setPositiveButton("Confirm", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText et_width = (EditText) ((AlertDialog)dialog).findViewById(R.id.et_width);
						EditText et_height = (EditText) ((AlertDialog)dialog).findViewById(R.id.et_height);
						width = Integer.valueOf(et_width.getText().toString());
						height = Integer.valueOf(et_height.getText().toString());
						System.out.println(width+"*"+height);
						mapDrawable.setSize(width*GRID_SPAC_LEN*10, height*GRID_SPAC_LEN*10);
						DataReference.getInstance(getApplicationContext()).saveData(DataReference.WIDTH, width);
						DataReference.getInstance(getApplicationContext()).saveData(DataReference.HEIGHT, height);
					}
				});
				builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				builder.create().show();
			}
		});
	}

	private void initView() {
		btn_resize = (Button) findViewById(R.id.btn_resize);
		tgbtn_auto = (ToggleButton) findViewById(R.id.tgbtn_auto);
		compassview = (CompassView) findViewById(R.id.compassview);
		touchView = (MultiTouchView) findViewById(R.id.touchview);
		tv_log = (TextView) findViewById(R.id.tv_log);
		MultiTouchDrawable.setGridSpacing(GRID_SPAC_LEN, GRID_SPAC_LEN);
		mapDrawable = new SiteMapDrawable(this, this);
		mapDrawable.setAngleAdjustment(-45.0F);
		width = DataReference.getInstance(getApplicationContext()).loadInt(DataReference.WIDTH);
		height = DataReference.getInstance(getApplicationContext()).loadInt(DataReference.HEIGHT);
		if(width > 0 && height > 0){
			mapDrawable.setSize(width*GRID_SPAC_LEN*10, height*GRID_SPAC_LEN*10);
		}
		userDrawable = new UserDrawable(this, mapDrawable);
		userDrawable.setRelativePosition(mapDrawable.getWidth()/2, mapDrawable.getHeight()/2);
		touchView.setRearrangable(false);
		touchView.addDrawable(mapDrawable);
		mapDrawable.load();
		compassview.setRefreshableView(this);
		compassview.start();
	}
	
	private void register(){
		if(!isReg){
			registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			isReg = true;
		}
	}
	
	private void unregister(){
		if(isReg){
			unregisterReceiver(mReceiver);
			isReg = false;
		}
	}
	
	private void scan() {
		if (this.mWifiManager.isWifiEnabled()) {
			this.register();
			this.mWifiManager.startScan();
		} else {
			Toast.makeText(getApplicationContext(), "WiFi not enabled", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		if(touchView != null){
			touchView.invalidate();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregister();
		super.onDestroy();
	}
	
	private void executeRequest(Request<?> request) {
		NetworkManager.addRequest(request, this);
	}
	
	private void processData(List<RouterBean> scanResults){
    	mParams.clear();
    	if(scanResults != null && scanResults.size() > 0){
			if(mLstRouters!=null&&mLstRouters.size()>0){
				for(RouterBean routerBean:mLstRouters){
					boolean flag = false;
					for(RouterBean rawRouter:scanResults){
						if(routerBean.getBssid().equals(rawRouter.getBssid())){
							mParams.add(new RouterBean(rawRouter));
							flag=true;
							break;
						}
					}
					if(!flag){
						mDrop++;
						if(mDrop==5){
							mDrop=0;
							Toast.makeText(MainActivity.this, "所需的路由器信号丢失!", Toast.LENGTH_SHORT).show();
							
						}
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								scan();
							}
						});
						return;
					}
				}
				cachWifiQue.putLstQue(mParams);
				if(cachWifiQue.getQueSize() == 10){
					List<RouterBean> result=cachWifiQue.calAverage();
					cachWifiQue.poll();
					executeRequest(new BasePostRequest<FindLocationResp>(NetworkUtil.URL_BASE+NetworkUtil.URL_FIND_LOCATION,
							JSON.toJSONString(mParams), new Listener<FindLocationResp>() {

								@Override
								public void onResponse(FindLocationResp arg0) {
									// TODO Auto-generated method stub
									if(arg0!=null){
										 int x=arg0.getX();
										 int y=arg0.getY();
//										 mapDrawable.setRelativePosition(GRID_SPAC_LEN*10*x, GRID_SPAC_LEN*10*y);
										 handler.sendMessage(handler.obtainMessage(1000, y*10, x*10));
										 handler.post(new Runnable() {
											
											@Override
											public void run() {
												// TODO Auto-generated method stub
												scan();
											}
										});
									}
								}
							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError arg0) {
									// TODO Auto-generated method stub
									Toast.makeText(MainActivity.this, "服务器连接失败!"+arg0.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}, FindLocationResp.class));
				}else{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							scan();
						}
					});
				}

			}
		}
    }
}
