package com.example.mapdemo.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.mapdemo.utils.Logger;
import com.example.mapdemo.utils.ToolBox;

public class CompassSensorWatcher implements SensorEventListener {

	protected SensorManager sensorManager;

	protected Sensor compass;

	protected Sensor accelerometer;

	protected Context context;

	float[] inR = new float[16];

	float[] I = new float[16];

	float[] gravity = new float[3];

	float[] geomag = new float[3];

	float[] orientVals = new float[3];

	float azimuth = 0;

	float angle = 0;

	int minX = 0, minY = 0, maxX = 0, maxY = 0, centerX = 0, centerY = 0, width = 0, height = 0;

	float l = 0.3f;
	
	protected CompassListener listener;

	protected float lastAzimuth = 0f;
	
	

	public CompassSensorWatcher(Context context,CompassListener cl,float lowpassFilter) {
		this.context = context;
		this.listener=cl;
		this.l=lowpassFilter;
		
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		try {
			sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		} catch (Exception e) {
			Logger.e("could not register listener", e);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			return;

		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomag = event.values.clone();

			break;
		}

		if (gravity != null && geomag != null) {

			boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
			if (success) {
				SensorManager.getOrientation(inR, orientVals);
				angle = (float) ToolBox.normalizeAngle(orientVals[0]);
				azimuth = (float) Math.toDegrees(angle);
				lowPassFilter();
				angle=(float) Math.toRadians(azimuth);
				if(listener!=null){
					listener.onCompassChanged(azimuth,angle,getAzimuthLetter(azimuth));
				}
			}
		}
	}
	
	public void stop(){
		try {
			sensorManager.unregisterListener(this);
		} catch (Exception e) {
			Logger.w("could not unregister listener", e);
		}
	}

	public String getAzimuthLetter(float azimuth) {
		String letter = "";
		int a = (int) azimuth;

		if (a < 23 || a >= 315) {
			letter = "N";
		} else if (a < 45 + 23) {
			letter = "NO";
		} else if (a < 90 + 23) {
			letter = "O";
		} else if (a < 135 + 23) {
			letter = "SO";
		} else if (a < (180 + 23)) {
			letter = "S";
		} else if (a < (225 + 23)) {
			letter = "SW";
		} else if (a < (270 + 23)) {
			letter = "W";
		} else {
			letter = "NW";
		}

		return letter;
	}

	protected void lowPassFilter() {
		float dazimuth = azimuth -lastAzimuth;
		if (dazimuth > 180) {
			dazimuth = (float) (dazimuth - 360f);
		} else if (dazimuth < -180) {
			dazimuth = (float) (360f + dazimuth);
		}
		azimuth = lastAzimuth+ dazimuth*l;
		azimuth%=360;
		if(azimuth<0){
			azimuth+=360;
		}
		lastAzimuth=azimuth;
	}

}
