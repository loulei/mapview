package com.example.mapdemo.view;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.mapdemo.utils.Logger;
import com.example.mapdemo.utils.ToolBox;
import com.example.mapdemo.view.MultiTouchController.PointInfo;
import com.example.mapdemo.view.MultiTouchController.PositionAndScale;

public abstract class MultiTouchDrawable {

	protected static int counter = 0;

	protected int id;

	protected float angle = 0;

	protected float scaleX = 1.0f;

	protected float scaleY = 1.0f;

	protected float relX = 0;

	protected float relY = 0;

	protected float pivotX = 0.5f;

	protected float pivotY = 0.5f;

	protected MultiTouchDrawable superDrawable = null;

	protected static float gridSpacingX = 30;

	protected static float gridSpacingY = 30;

	protected Context ctx;

	protected static final int UI_MODE_ROTATE = 1;

	protected static final int UI_MODE_ANISOTROPIC_SCALE = 2;

	protected static final int FLAG_FORCEXY = 1;

	protected static final int FLAG_FORCESCALE = 2;

	protected static final int FLAG_FORCEROTATE = 4;

	protected static final int FLAG_FORCEALL = 7;

	protected int mUIMode = UI_MODE_ROTATE;

	protected static boolean firstLoad= true;

	protected int width;

	protected int height;
	
	protected static int displayWidth=0;

	protected static int displayHeight=0;

	protected float centerX;

	protected float centerY;

	protected float minX;

	protected float maxX;

	protected float minY;

	protected float maxY;

	protected static final float SCREEN_MARGIN = 0;

	protected Resources resources;

	protected ArrayList<MultiTouchDrawable> subDrawables;

	protected RefreshableView refresher;

	protected AngleChangeCallback angleChangeCallback = null;
	
	public MultiTouchDrawable(Context context, RefreshableView containingView) {
		id = counter++;
		this.ctx = context;
		
		this.resources = context.getResources();
		subDrawables = new ArrayList<MultiTouchDrawable>();
		this.refresher = containingView;
		load();
	}

	public MultiTouchDrawable(Context context, MultiTouchDrawable superDrawable) {
		id = counter++;

		this.ctx = context;
		this.superDrawable = superDrawable;

		
		this.resources = context.getResources();
		subDrawables = new ArrayList<MultiTouchDrawable>();

		superDrawable.addSubDrawable(this);
	}

	public abstract Drawable getDrawable();

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public String getId() {
		return this.getClass().getName() + ":" + id;
	}

	public boolean onTouch(PointInfo pointinfo) {
		boolean handleEvent = false;

		for (int i = subDrawables.size() - 1; i >= 0 && !handleEvent; i--) {

			MultiTouchDrawable sub = subDrawables.get(i);

			if (sub.containsPoint(pointinfo.getX(), pointinfo.getY())) {
				handleEvent = sub.onTouch(pointinfo);
			}
		}

		if (!handleEvent && pointinfo.isMultiTouch() == false && pointinfo.getNumTouchPoints() == 1
				&& pointinfo.getAction() == MotionEvent.ACTION_DOWN) {
			handleEvent = this.onSingleTouch(pointinfo);
		}

		return handleEvent;
	}

	public boolean onSingleTouch(PointInfo pointinfo) {
		return false;
	}

	public void setAngle(float angle) {
		this.angle = ToolBox.normalizeAngle(angle);
		
		if (this.angleChangeCallback != null) {
			angleChangeCallback.angleChanged(angle, this);
		}
	}

	public void setScale(float scaleX, float scaleY) {
		if (isScalable()) {
			this.scaleX = scaleX;
			this.scaleY = scaleY;
		}
	}

	public void setRelativePosition(PointF relativePosition) {
		this.setRelativePosition(relativePosition.x, relativePosition.y);
	}

	public void setRelativePosition(float relX, float relY) {
		this.relX = relX;
		this.relY = relY;
		if (superDrawable != null) {
			superDrawable.recalculatePositions();
		}
		onRelativePositionUpdate();
	}

	public float getRelativeX() {
		return this.relX;
	}

	public float getRelativeY() {
		return this.relY;
	}

	public boolean isCustomPivotUsed() {
		return (this.pivotX != 0.5f || this.pivotY != 0.5f) ? true : false;
	}

	public void setPivot(float pivotX, float pivotY) {
		this.pivotX = pivotX;
		this.pivotY = pivotY;
	}

	public float getPivotX() {
		return this.pivotX;
	}

	public float getPivotY() {
		return this.pivotY;
	}

	public float getPivotXRelativeToCenter() {
		return this.getWidth() * this.pivotX - this.getWidth() / 2;
	}

	public float getPivotYRelativeToCenter() {
		return this.getHeight() * this.pivotY - this.getHeight() / 2;
	}

	public abstract boolean isScalable();

	public abstract boolean isRotatable();

	public abstract boolean isDragable();

	public abstract boolean isOnlyInSuper();

	public boolean hasSuperDrawable() {
		return (superDrawable == null ? false : true);
	}

	public MultiTouchDrawable getSuperDrawable() {
		return superDrawable;
	}


	protected void getMetrics() {
		DisplayMetrics metrics = resources.getDisplayMetrics();
		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;

		displayWidth = resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
				metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		displayHeight = resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
				metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
	}

	public void load() {
		if(firstLoad){
			getMetrics();
			firstLoad=false;
		}
		
		for(MultiTouchDrawable sub:subDrawables){
			sub.load();
		}
			
	}

	public void resetXY() {
		this.centerX = this.width / 2;
		this.centerY = this.height / 2;
	}

	public void resetScale() {
		scaleX = scaleY = 1;
	}

	public void resetAngle() {
		this.setAngle(0.0f);
	}

	public void unload() {
		for(MultiTouchDrawable sub: subDrawables){
			sub.unload();
		}
	}

	public boolean setPos(PositionAndScale newImgPosAndScale, boolean isDraggedOrPinched) {
		return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleX() : newImgPosAndScale.getScale(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleY() : newImgPosAndScale.getScale(),
				newImgPosAndScale.getAngle(), isDraggedOrPinched);
	}

	protected boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle, boolean isDraggedOrPinched) {
		return setPos(centerX, centerY, scaleX, scaleY, angle, 0, isDraggedOrPinched);
	}

	protected boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle, int flags, boolean isDraggedOrPinched) {

		if (!isScalable()) {
			scaleX = 1.0f;
			scaleY = 1.0f;
		}

		if (!isRotatable()) {
			this.setAngle(0.0f);
		}

		float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
		float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY + hs;

		float scaleXChange = 1.0f, scaleYChange = 1.0f, angleChange = 0.0f;

		if ((flags & FLAG_FORCEXY) != 0 || this.isDragable() || (flags & FLAG_FORCESCALE) != 0 || this.isScalable()) {
			this.minX = newMinX;
			this.minY = newMinY;
			this.maxX = newMaxX;
			this.maxY = newMaxY;
		}

		if ((flags & FLAG_FORCEXY) != 0 || this.isDragable()) {

			this.centerX = centerX;
			this.centerY = centerY;
		}

		if ((flags & FLAG_FORCESCALE) != 0 || this.isScalable()) {
			scaleXChange = scaleX / this.scaleX;
			scaleYChange = scaleY / this.scaleY;
			this.setScale(scaleX, scaleY);
		}

		if ((flags & FLAG_FORCEROTATE) != 0 || this.isRotatable()) {
			angleChange = angle - this.angle;
			this.setAngle(angle);
		}

		if (isDraggedOrPinched && this.hasSuperDrawable()) {
			PointF relativePosition = getRelativePositionToSuperobject();
			this.setRelativePosition(relativePosition);
		}

		for (MultiTouchDrawable subobject : subDrawables) {
			PointF absolutePosition = this.getAbsolutePositionOfSubobject(subobject);
			subobject.setPos(absolutePosition.x, absolutePosition.y, subobject.scaleX * scaleXChange, subobject.scaleY * scaleYChange,
					subobject.angle + angleChange, FLAG_FORCEXY, false);
		}

		return true;
	}

	protected void recalculateSubDrawables() {

	}

	protected void onRelativePositionUpdate() {

	}

	protected PointF getAbsolutePositionOfSubobject(MultiTouchDrawable subobject) {

		float xBeforeRotate = this.minX + subobject.getRelativeX() * scaleX;
		float yBeforeRotate = this.minY + subobject.getRelativeY() * scaleY;

		float radius = (float) Math.sqrt(Math.pow(Math.abs(centerX - xBeforeRotate), 2) + Math.pow(Math.abs(centerY - yBeforeRotate), 2));

		float angleBeforeRotate = (float) Math.atan2(yBeforeRotate - centerY, xBeforeRotate - centerX);

		float newAngle = angle + angleBeforeRotate;

		float newY = (float) (centerY + radius * Math.sin(newAngle));
		float newX = (float) (centerX + radius * Math.cos(newAngle));

		if (subobject.isCustomPivotUsed()) {
			if (subobject.angle == 0.0f) {
				newX -= subobject.getPivotXRelativeToCenter() * subobject.scaleX;
				newY -= subobject.getPivotYRelativeToCenter() * subobject.scaleY;
			} else {
				PointF pivotPosition = subobject.getPivotPointPositionConsideringScalingAndAngle();

				newX -= pivotPosition.x;
				newY -= pivotPosition.y;
			}
		}

		return new PointF(newX, newY);
	}

	protected PointF getRelativePositionToSuperobject() {

		float x = centerX;
		float y = centerY;

		if (this.isCustomPivotUsed()) {
			if (this.angle == 0.0f) {
				x += this.getPivotXRelativeToCenter() * this.scaleX;
				y += this.getPivotYRelativeToCenter() * this.scaleY;
			} else {
				PointF pivotPosition = this.getPivotPointPositionConsideringScalingAndAngle();

				x += pivotPosition.x;
				y += pivotPosition.y;
			}
		}

		float superAngle = superDrawable.angle;
		float angleToCenter = (float) Math.atan2(y - superDrawable.centerY, x - superDrawable.centerX);

		float angle = superAngle - angleToCenter;

		float radius = (float) Math.sqrt(Math.pow(Math.abs(x - superDrawable.centerX), 2) + Math.pow(Math.abs(y - superDrawable.centerY), 2))
				/ superDrawable.scaleX;

		float newX = (float) (radius * Math.cos(angle) + superDrawable.getWidth() / 2);
		float newY = (float) (radius * Math.sin(angle * -1) + superDrawable.getHeight() / 2);

		return new PointF(newX, newY);
	}

	public PointF getPivotPointPositionConsideringScalingAndAngle() {
		float absolutePivotX = this.getPivotXRelativeToCenter() * this.scaleX;
		float absolutePivotY = this.getPivotYRelativeToCenter() * this.scaleY;

		float pivotRadius = (float) Math.sqrt(Math.pow(absolutePivotX, 2) + Math.pow(absolutePivotY, 2));

		float pivotAngleAfterRotation = this.angle + (float) Math.atan2(absolutePivotY, absolutePivotX);

		float x = (float) (pivotRadius * Math.cos(pivotAngleAfterRotation));
		float y = (float) (pivotRadius * Math.sin(pivotAngleAfterRotation));

		return new PointF(x, y);
	}

	public void recalculatePositions() {
		this.setPos(centerX, centerY, scaleX, scaleY, angle, false);
	}

	public boolean containsPoint(float scrnX, float scrnY) {
		boolean inside = (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);

		if (inside)
			return true;

		Iterator<MultiTouchDrawable> it = this.subDrawables.iterator();
		while (it.hasNext()) {
			MultiTouchDrawable sub = it.next();
			if (sub.containsPoint(scrnX, scrnY)) {
				return true;
			}
		}

		return false;
	}

	public MultiTouchDrawable getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = subDrawables.size();
		for (int i = n - 1; i >= 0; i--) {
			MultiTouchDrawable im = subDrawables.get(i);

			if (im.isDragable() && im.containsPoint(x, y)) {
				return im.getDraggableObjectAtPoint(pt);
			}
		}

		if (this.containsPoint(pt.getX(), pt.getY())) {
			return this;
		}

		return null;
	}

	public void draw(Canvas canvas) {

		drawFromDrawable(canvas);
		
		this.drawSubdrawables(canvas);
	}

	protected void drawFromDrawable(Canvas canvas) {
		canvas.save();
		Drawable d = this.getDrawable();
		if (d != null) {
			d.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			canvas.translate(centerX, centerY);
			canvas.rotate((float) Math.toDegrees(angle));
			canvas.translate(-centerX, -centerY);
			d.draw(canvas);

		}
		canvas.restore();
	}

	public void drawSubdrawables(Canvas canvas) {
		for (int i = 0; i < subDrawables.size(); i++) {
			subDrawables.get(i).draw(canvas);
		}
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public float getAngle() {
		return angle;
	}

	public float getMinX() {
		return minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMinY() {
		return minY;
	}

	public float getMaxY() {
		return maxY;
	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	@Override
	public String toString() {
		return this.getId() + " " + this.getWidth() + "x" + this.getHeight() + "px (" + centerX + "[" + minX + "-" + maxX + "]," + centerY + "["
				+ minY + "-" + maxY + "]) scale (" + scaleX + "," + scaleY + ") angle " + angle * 180.0f / Math.PI
				+ (superDrawable != null ? " super: " + superDrawable.id + " rel: (" + relX + "," + relY + ")" : "");
	}

	public void addSubDrawable(MultiTouchDrawable subObject) {
		subDrawables.add(subObject);
		subObject.refresher = this.refresher;
		this.setPos(centerX, centerY, scaleX, scaleY, angle, false);
	}

	public void removeSubDrawable(MultiTouchDrawable subObject) {
		subDrawables.remove(subObject);
	}

	public void snapPositionToGrid() {
		float unfittingX = this.relX % gridSpacingX;
		float unfittingY = this.relY % gridSpacingY;

		float newRelX = this.relX;
		float newRelY = this.relY;

		if (unfittingX >= (gridSpacingX / 2.0f))
			newRelX += gridSpacingX - unfittingX;
		else
			newRelX -= unfittingX;

		if (unfittingY >= (gridSpacingY / 2.0f))
			newRelY += gridSpacingY - unfittingY;
		else
			newRelY -= unfittingY;

		this.setRelativePosition(newRelX, newRelY);
	}

	public ArrayList<MultiTouchDrawable> getSubDrawables() {
		return subDrawables;
	}

	public boolean bringToFront() {
		if (superDrawable != null) {
			superDrawable.bringSubDrawableToFront(this);
			return true;
		} else {
			Logger.d("we can't bring ourselfs to front, because we are not attached to a super drawable");
		}
		return false;
	}

	protected void bringSubDrawableToFront(MultiTouchDrawable drawable) {
		subDrawables.remove(drawable);
		subDrawables.add(drawable);
	}

	public void deleteDrawable() {
		if (this.superDrawable == null) {
			Logger.d("don't know how to delete myself, if I have not super Drawable");
		} else {
			superDrawable.subDrawables.remove(this);

		}
		onDelete();
	}

	public void onDelete() {

	}

	public void hidePopups() {
		if (this instanceof Popup) {
			((Popup) this).setActive(false);
		}

		for (MultiTouchDrawable d : subDrawables) {
			d.hidePopups();
		}
	}

	public static void setGridSpacing(float gridSpacingX, float gridSpacingY) {
		MultiTouchDrawable.gridSpacingX = gridSpacingX;
		MultiTouchDrawable.gridSpacingY = gridSpacingY;
	}

	public static float getGridSpacingX() {
		return gridSpacingX;
	}

	public static float getGridSpacingY() {
		return gridSpacingY;
	}

	public void setAngleChangeCallback(AngleChangeCallback callback) {
		this.angleChangeCallback = callback;
	}
	
}
