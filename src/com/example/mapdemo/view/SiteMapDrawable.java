package com.example.mapdemo.view;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.mapdemo.compass.CompassListener;
import com.example.mapdemo.compass.CompassMonitor;
import com.example.mapdemo.utils.Logger;
import com.example.mapdemo.utils.ToolBox;
import com.example.mapdemo.view.MultiTouchController.PointInfo;

public class SiteMapDrawable extends MultiTouchDrawable implements CompassListener {

	protected Bitmap backgroundImage;

	float angleAdjustment = 0.0f;

	protected float lastAngle;

	protected Vector<PointF> steps;

	protected static final double MIN_ANGLE_CHANGE = Math.toRadians(5);

	public SiteMapDrawable(Context ctx, RefreshableView refresher) {
		super(ctx, refresher);
		init();
	}

	public SiteMapDrawable(Context ctx, MultiTouchDrawable superDrawable) {
		super(ctx, superDrawable);
		init();
	}

	protected void init() {
		width = displayWidth;
		height = displayHeight;
		backgroundImage = null;
		steps = new Vector<PointF>();
		this.resetXY();
	}

	public void startAutoRotate() {
		CompassMonitor.registerListener(ctx, this);
		Logger.d("Auto rotate started. North value: " + angleAdjustment);
	}

	public void stopAutoRotate() {
		CompassMonitor.unregisterListener(this);
	}

	public Drawable getDrawable() {
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bmp);
		canvas.drawColor(Color.rgb(250, 250, 250));

		return new BitmapDrawable(ctx.getResources(), bmp);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		float dx = (maxX + minX) / 2;
		float dy = (maxY + minY) / 2;

		canvas.translate(dx, dy);
		canvas.rotate(angle * 180.0f / (float) Math.PI);
		canvas.translate(-dx, -dy);

		canvas.drawColor(Color.rgb(250, 250, 250));

		if (backgroundImage != null)
			canvas.drawBitmap(backgroundImage, new Rect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight()), new Rect((int) minX,
					(int) minY, (int) maxX, (int) maxY), null);

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.rgb(230, 230, 230));

		int counterX = 0;
		for (float x = minX; Math.floor(x) <= maxX; x += gridSpacingX * scaleX) {
			if (counterX % 10 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
				paint.setStrokeWidth(2);
			} else if (counterX % 5 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
			}

			canvas.drawLine(x, minY, x, maxY, paint);
			paint.setStrokeWidth(0);
			paint.setColor(Color.rgb(230, 230, 230));
			counterX++;
		}

		int counterY = 0;
		for (float y = minY; Math.floor(y) <= maxY; y += gridSpacingY * scaleY) {
			if (counterY % 10 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
				paint.setStrokeWidth(2);
			} else if (counterY % 5 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
			}

			canvas.drawLine(minX, y, maxX, y, paint);
			paint.setStrokeWidth(0);
			paint.setColor(Color.rgb(230, 230, 230));
			counterY++;
		}

		canvas.restore();

		synchronized (steps) {

			if (steps != null) {

				Paint pointPaint = new Paint();
				pointPaint.setStyle(Paint.Style.FILL);
				pointPaint.setColor(Color.RED);

				canvas.save();

				canvas.translate(dx, dy);
				canvas.rotate((float) Math.toDegrees(angle));
				canvas.translate(-dx, -dy);

				canvas.translate(minX, minY);

				int i = -1;
				for (PointF step : steps) {
					canvas.drawCircle(step.x * scaleX, step.y * scaleY, 3, pointPaint);
					if (i >= 0) {
						canvas.drawLine(steps.get(i).x * scaleX, steps.get(i).y * scaleY, step.x * scaleX, step.y * scaleY, pointPaint);
					}
					i++;
				}

				canvas.restore();

			}

		}

		this.drawSubdrawables(canvas);
	}

	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	@Override
	public void setRelativePosition(float xPos, float yPos) {
	}

	@Override
	public boolean isScalable() {
		return true;
	}

	@Override
	public boolean isRotatable() {
		return true;
	}

	@Override
	public boolean isDragable() {
		return true;
	}

	@Override
	public boolean isOnlyInSuper() {
		return false;
	}

	@Override
	public boolean hasSuperDrawable() {
		return false;
	}

	@Override
	public MultiTouchDrawable getSuperDrawable() {
		return null;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.recalculatePositions();

	}

	public Bitmap getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Bitmap backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public Vector<PointF> getSteps() {
		return steps;
	}

	public void setSteps(Vector<PointF> steps) {
		this.steps = steps;
	}

	public void addStep(PointF step) {
		if (steps == null) {
			steps = new Vector<PointF>();
		}
		steps.add(step);
	}

	@Override
	protected void bringSubDrawableToFront(MultiTouchDrawable drawable) {
		super.bringSubDrawableToFront(drawable);
	}

	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		hidePopups();
		return true;
	}

	public void setAngleAdjustment(float adjustment) {
		this.angleAdjustment = adjustment;
		this.angleChangeCallback = null;
	}

	@Override
	public void onCompassChanged(float azimuth, float angle, String direction) {
		float adjusted = ToolBox.normalizeAngle((angle + angleAdjustment) * -1.0f);

		if (Math.abs(lastAngle - adjusted) > MIN_ANGLE_CHANGE) {
			this.setAngle(adjusted);
			this.recalculatePositions();
			this.lastAngle = adjusted;
		}
	}

	public void deleteAllSteps() {
		steps = new Vector<PointF>();
	}

}
