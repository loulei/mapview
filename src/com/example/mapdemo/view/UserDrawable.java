package com.example.mapdemo.view;

import com.example.mapdemo.R;
import com.example.mapdemo.view.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class UserDrawable extends MultiTouchDrawable {
	
	protected static BitmapDrawable icon;
	
	public UserDrawable(Context context, MultiTouchDrawable superDrawable) {
		super(context, superDrawable);
		init();
		// TODO Auto-generated constructor stub
	}
	
	protected void init(){
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.user);
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
		this.setPivot(0.5F, 0.5F);
		
	}

	@Override
	public Drawable getDrawable() {
		// TODO Auto-generated method stub
		return icon;
	}

	@Override
	public boolean isScalable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRotatable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDragable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isOnlyInSuper() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		// TODO Auto-generated method stub
		bringToFront();
		return true;
	}

	@Override
	protected void onRelativePositionUpdate() {
		// TODO Auto-generated method stub
		super.onRelativePositionUpdate();
	}
}
