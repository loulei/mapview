package com.example.mapdemo.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.mapdemo.utils.Logger;
import com.example.mapdemo.view.MultiTouchController.MultiTouchObjectCanvas;
import com.example.mapdemo.view.MultiTouchController.PointInfo;
import com.example.mapdemo.view.MultiTouchController.PositionAndScale;

public class MultiTouchView extends View implements
		MultiTouchObjectCanvas<MultiTouchDrawable> {
	private static final int UI_MODE_ROTATE = 1;

	private static final int UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	private ArrayList<MultiTouchDrawable> drawables = new ArrayList<MultiTouchDrawable>();

	private MultiTouchController<MultiTouchDrawable> multiTouchController = new MultiTouchController<MultiTouchDrawable>(
			this);

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = false;


	private Paint mLinePaintTouchPointCircle = new Paint();

	public boolean rearrangable = true;


	public MultiTouchView(Context context) {
		super(context);
		init();

	}

	public MultiTouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MultiTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected void init() {

		Logger.d("initializing MultiTouchView");

		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
	}

	public void loadImages(Context context) {
	}

	public void unloadImages() {
		int n = drawables.size();
		for (int i = 0; i < n; i++)
			drawables.get(i).unload();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = drawables.size();

		for (int i = 0; i < n; i++)
			drawables.get(i).draw(canvas);
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}


	public void trackballClicked() {
		mUIMode = (mUIMode == UI_MODE_ROTATE ? UI_MODE_ANISOTROPIC_SCALE
				: UI_MODE_ROTATE);
		invalidate();
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoint.isDown()) {
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			for (int i = 0; i < numPoints; i++)
				canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80,
						mLinePaintTouchPointCircle);
			if (numPoints == 2)
				canvas.drawLine(xs[0], ys[0], xs[1], ys[1],
						mLinePaintTouchPointCircle);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = multiTouchController.onTouchEvent(event);
		invalidate();
		return handled;
	}

	public MultiTouchDrawable getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = drawables.size();
		for (int i = n - 1; i >= 0; i--) {
			MultiTouchDrawable im = drawables.get(i);
			if (im.containsPoint(x, y)) {
				
				
				if (!im.onTouch(pt)) {
					return im.getDraggableObjectAtPoint(pt);
				}
			}
		}
		return null;
	}

	public void selectObject(MultiTouchDrawable drawable, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (drawable != null) {

			if (rearrangable) {
				drawables.remove(drawable);
				drawables.add(drawable);
			}
		} else {
		}
	}

	public void getPositionAndScale(MultiTouchDrawable drawable,
			PositionAndScale objPosAndScaleOut) {
		objPosAndScaleOut.set(drawable.getCenterX(), drawable.getCenterY(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(drawable.getScaleX() + drawable.getScaleY()) / 2,
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0,
				drawable.getScaleX(), drawable.getScaleY(),
				(mUIMode & UI_MODE_ROTATE) != 0, drawable.getAngle());
	}

	public boolean setPositionAndScale(MultiTouchDrawable drawable,
			PositionAndScale newImgPosAndScale, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		boolean ok = drawable.setPos(newImgPosAndScale, true);
		if (ok)
			invalidate();
		return ok;
	}

	public void resetAllXY() {
		for (int i = 0; i < drawables.size(); i++) {
			drawables.get(i).resetXY();
		}
		invalidate();
	}

	public void resetAllAngle() {
		for (int i = 0; i < drawables.size(); i++) {
			drawables.get(i).resetAngle();
		}
		invalidate();
	}

	public void resetAllScale() {
		for (int i = 0; i < drawables.size(); i++) {
			drawables.get(i).resetScale();
		}
		invalidate();
	}

	public void recalculateDrawablePositions() {
		int n = drawables.size();
		for (int i = n - 1; i >= 0; i--) {
			MultiTouchDrawable im = drawables.get(i);
			im.recalculatePositions();
		}
	}
	
	public void addDrawable(MultiTouchDrawable drawable) {
		if (!drawable.hasSuperDrawable()) {
			drawables.add(drawable);
			drawable.recalculatePositions();
		} else {
			Logger.w("only drawables without a superdrawable have to be added to the view!");
		}
	}

	public void removeDrawable(MultiTouchDrawable drawable) {
		for (int i = 0; i < drawables.size(); i++) {
			if (drawables.get(i).getId() == drawable.getId()) {
				drawables.remove(i);
			}
		}
	}

	public boolean isRearrangable() {
		return rearrangable;
	}

	public void setRearrangable(boolean rearrangable) {
		this.rearrangable = rearrangable;
	}

}