package com.example.mapdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.example.mapdemo.utils.DataReference;
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
	private UserDrawable userDrawable;
	
	private static final int GRID_SPAC_LEN = 30;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn_resize = (Button) findViewById(R.id.btn_resize);
		tgbtn_auto = (ToggleButton) findViewById(R.id.tgbtn_auto);
		compassview = (CompassView) findViewById(R.id.compassview);
		touchView = (MultiTouchView) findViewById(R.id.touchview);
		MultiTouchDrawable.setGridSpacing(GRID_SPAC_LEN, GRID_SPAC_LEN);
		mapDrawable = new SiteMapDrawable(this, this);
		mapDrawable.setAngleAdjustment(0.0F);
		int width = DataReference.getInstance(getApplicationContext()).loadInt(DataReference.WIDTH);
		int height = DataReference.getInstance(getApplicationContext()).loadInt(DataReference.HEIGHT);
		userDrawable = new UserDrawable(this, mapDrawable);
		userDrawable.setRelativePosition(mapDrawable.getWidth()/2, mapDrawable.getHeight()/2);
		if(width > 0 && height > 0){
			mapDrawable.setSize(width*GRID_SPAC_LEN*10, height*GRID_SPAC_LEN*10);
		}
		touchView.setRearrangable(false);
		touchView.addDrawable(mapDrawable);
		mapDrawable.load();
		compassview.setRefreshableView(this);
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
						int width = Integer.valueOf(et_width.getText().toString());
						int height = Integer.valueOf(et_height.getText().toString());
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

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		if(touchView != null){
			touchView.invalidate();
		}
	}
}
