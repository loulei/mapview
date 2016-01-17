package com.example.mapdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.example.mapdemo.view.CompassView;
import com.example.mapdemo.view.MultiTouchDrawable;
import com.example.mapdemo.view.MultiTouchView;
import com.example.mapdemo.view.RefreshableView;
import com.example.mapdemo.view.SiteMapDrawable;

public class MainActivity extends Activity implements RefreshableView {
	private MultiTouchView touchView;
	private SiteMapDrawable mapDrawable;
	private ToggleButton tgbtn_auto;
	private CompassView compassview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tgbtn_auto = (ToggleButton) findViewById(R.id.tgbtn_auto);
		compassview = (CompassView) findViewById(R.id.compassview);
		touchView = (MultiTouchView) findViewById(R.id.touchview);
		MultiTouchDrawable.setGridSpacing(30.0F, 30.0F);
		mapDrawable = new SiteMapDrawable(this, this);
		mapDrawable.setAngleAdjustment(0.0F);
		touchView.setRearrangable(false);
		touchView.addDrawable(mapDrawable);
		mapDrawable.load();
		compassview.start();
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
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		if(touchView != null){
			touchView.invalidate();
		}
	}
}
