package com.example.mapdemo.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.mapdemo.R;
import com.example.mapdemo.compass.CompassListener;
import com.example.mapdemo.compass.CompassMonitor;
import com.example.mapdemo.utils.ToolBox;

public class CompassDrawable extends MultiTouchDrawable implements CompassListener {

	protected static BitmapDrawable icon;

	protected TextPopupDrawable popup;
	
	int popupAngle=0;

	double azimuth = 0;
	
	protected AngleChangeCallback compassAngleCallback = null;
	
	protected boolean withPopup = true;
	
	protected static final float minAngleChange = (float) Math.toRadians(3);

	protected static final float minAngleChangeForPopup = (float) Math.toRadians(1);

	public CompassDrawable(Context context, MultiTouchDrawable superDrawable) {
		super(context, superDrawable);
		init();
	}
	
	public CompassDrawable(Context context, MultiTouchDrawable superDrawable, AngleChangeCallback compassAngleCallback) {
		super(context, superDrawable);
		this.compassAngleCallback = compassAngleCallback;
		init();
	}
	
	public CompassDrawable(Context context, MultiTouchDrawable superDrawable, AngleChangeCallback compassAngleCallback, boolean withPopup) {
		super(context, superDrawable);
		this.compassAngleCallback = compassAngleCallback;
		this.withPopup = withPopup;
		init();
	}

	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.compass);
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();

		if (withPopup) {
			popup = new TextPopupDrawable(ctx, this);
			popup.setText("0°");
			popup.setActive(true);
			popup.setPersistent(true);
			popup.setWidth(80);
			popup.setRelativePosition(20,0);
		}
	}
	
	public void start() {
		CompassMonitor.registerListener(ctx, this);
	}

	public void stop() {
		CompassMonitor.unregisterListener(this);
	}

	@Override
	public Drawable getDrawable() {
		return icon;
	}

	@Override
	public boolean isScalable() {
		return false;
	}

	@Override
	public boolean isRotatable() {
		return false;
	}

	@Override
	public boolean isDragable() {
		return false;
	}

	@Override
	public boolean isOnlyInSuper() {
		return false;
	}
	
	@Override
	protected void finalize() throws Throwable {
		stop();
		super.finalize();
	}

	@Override
	public void load() {
		super.load();
		start();
	}

	@Override
	public void unload() {
		stop();
		super.unload();
	}

	@Override
	public void onCompassChanged(float azimuth,float angle, String direction) {
		float newAngle = ToolBox.normalizeAngle((float) -Math.toRadians(azimuth));
		this.azimuth=azimuth;
		if (compassAngleCallback != null) {
			compassAngleCallback.angleChanged((float) Math.toRadians(azimuth), this);
		}
		
		if (Math.abs(this.angle - newAngle) > minAngleChange) {
			this.angle=ToolBox.normalizeAngle(newAngle);
			popupAngle=(int)this.azimuth;
			if (withPopup) {
				popup.setText(ctx.getString(R.string.compass_degrees, popupAngle));
				
			}
			refresher.invalidate();
			
		}
	}

	@Override
	public void setAngle(float angle) {
	}

}
