package com.example.mapdemo;

import com.example.mapdemo.view.CompassDrawable;
import com.example.mapdemo.view.MultiTouchDrawable;
import com.example.mapdemo.view.MultiTouchView;
import com.example.mapdemo.view.RefreshableView;
import com.example.mapdemo.view.SiteMapDrawable;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements RefreshableView {
	private MultiTouchView touchView;
	private SiteMapDrawable mapDrawable;
	private CompassDrawable compassDrawable;
	private LinearLayout ll_bg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ll_bg = (LinearLayout) findViewById(R.id.ll_bg);
		touchView = (MultiTouchView) findViewById(R.id.touchview);
		MultiTouchDrawable.setGridSpacing(30.0F, 30.0F);
		mapDrawable = new SiteMapDrawable(this, this);
		mapDrawable.setAngleAdjustment(0.0F);
		compassDrawable = new CompassDrawable(this, mapDrawable);
		compassDrawable.setRelativePosition(mapDrawable.getWidth()/2, mapDrawable.getHeight()/2);
		touchView.setRearrangable(false);
		touchView.addDrawable(mapDrawable);
		mapDrawable.load();
//		mapDrawable.startAutoRotate();
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		if(touchView != null){
			touchView.invalidate();
		}
	}
}
