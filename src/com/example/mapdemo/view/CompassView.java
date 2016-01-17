package com.example.mapdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.example.mapdemo.R;
import com.example.mapdemo.compass.CompassListener;
import com.example.mapdemo.compass.CompassMonitor;
import com.example.mapdemo.utils.ToolBox;

public class CompassView extends View implements CompassListener {
	
	private static BitmapDrawable icon;
	
	private double azimuth = 0;
	
	private static final float minAngleChange = (float) Math.toRadians(3);

	private static final float minAngleChangeForPopup = (float) Math.toRadians(1);
	
	private int width;
	
	private int height;
	
	private Context ctx;

	private float angle;

	public CompassView(Context context) {
		super(context);
		this.ctx = context;
		init();
		// TODO Auto-generated constructor stub
	}

	public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.ctx = context;
		init();
		// TODO Auto-generated constructor stub
	}

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		init();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.save();
		if (icon != null) {
			icon.setBounds((int) 0, (int) 0, (int) width, (int) height);
			canvas.translate(width/2, height/2);
			canvas.rotate((float) Math.toDegrees(angle));
			canvas.translate(-width/2, -height/2);
			icon.draw(canvas);
		}
		canvas.restore();
		super.onDraw(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		if(width != 0 && height != 0){
			int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
			int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
			super.onMeasure(widthSpec, heightSpec);
		}else{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
		
	}
	
	private void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.compass);
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
	}
	
	public void start() {
		CompassMonitor.registerListener(ctx, this);
	}

	public void stop() {
		CompassMonitor.unregisterListener(this);
	}

	@Override
	public void onCompassChanged(float azimuth, float angle, String direction) {
		// TODO Auto-generated method stub
		float newAngle = ToolBox.normalizeAngle((float) -Math.toRadians(azimuth));
		this.azimuth=azimuth;
		
		if (Math.abs(this.angle - newAngle) > minAngleChange) {
			this.angle=ToolBox.normalizeAngle(newAngle);
			invalidate();
		}
	}

}
