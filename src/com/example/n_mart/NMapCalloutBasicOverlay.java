/* 
 * NMapCalloutBasicOverlay.java $version 2010. 1. 1
 * 
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package com.example.n_mart;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;

/**
 * Basic callout overlay
 * 
 * @author kyjkim 
 */
public class NMapCalloutBasicOverlay extends NMapCalloutOverlay {

	private static final int CALLOUT_TEXT_PADDING_X = 10;
	private static final int CALLOUT_TEXT_PADDING_Y = 10;
	private static final int CALLOUT_TAG_WIDTH = 10;
	private static final int CALLOUT_TAG_HEIGHT = 10;
	private static final int CALLOUT_ROUND_RADIUS_X = 5;
	private static final int CALLOUT_ROUND_RADIUS_Y = 5;

	private final Paint mInnerPaint, mBorderPaint, mTextPaint;
	private final Path mPath;
	private float mOffsetX, mOffsetY;

	public NMapCalloutBasicOverlay(NMapOverlay itemOverlay, NMapOverlayItem item, Rect itemBounds) {
		super(itemOverlay, item, itemBounds);

		mInnerPaint = new Paint();
		mInnerPaint.setARGB(225, 75, 75, 75); //gray
		mInnerPaint.setAntiAlias(true);

		mBorderPaint = new Paint();
		mBorderPaint.setARGB(255, 255, 255, 255);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setStyle(Style.STROKE);
		mBorderPaint.setStrokeWidth(2);

		mTextPaint = new Paint();
		mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setAntiAlias(true);

		mPath = new Path();
		mPath.setFillType(Path.FillType.WINDING);
	}

	@Override
	protected boolean isTitleTruncated() {
		return false;
	}

	@Override
	protected int getMarginX() {
		return 10;
	}

	@Override
	protected Rect getBounds(NMapView mapView) {

		adjustTextBounds(mapView);

		mTempRect.set((int)mTempRectF.left, (int)mTempRectF.top, (int)mTempRectF.right, (int)mTempRectF.bottom);
		mTempRect.union(mTempPoint.x, mTempPoint.y);

		return mTempRect;
	}

	@Override
	protected PointF getSclaingPivot() {
		PointF pivot = new PointF();

		pivot.x = mTempRectF.centerX();
		pivot.y = mTempRectF.bottom + CALLOUT_TAG_HEIGHT;

		return pivot;
	}

	@Override
	protected void drawCallout(Canvas canvas, NMapView mapView, boolean shadow, long when) {

		adjustTextBounds(mapView);

		stepAnimations(canvas, mapView, when);

		// Draw inner info window
		canvas.drawRoundRect(mTempRectF, CALLOUT_ROUND_RADIUS_X, CALLOUT_ROUND_RADIUS_Y, mInnerPaint);

		// Draw border for info window
		canvas.drawRoundRect(mTempRectF, CALLOUT_ROUND_RADIUS_X, CALLOUT_ROUND_RADIUS_Y, mBorderPaint);

		// Draw bottom tag
		if (CALLOUT_TAG_WIDTH > 0 && CALLOUT_TAG_HEIGHT > 0) {
			float x = mTempRectF.centerX();
			float y = mTempRectF.bottom;

			Path path = mPath;
			path.reset();

			path.moveTo(x - CALLOUT_TAG_WIDTH, y);
			path.lineTo(x, y + CALLOUT_TAG_HEIGHT);
			path.lineTo(x + CALLOUT_TAG_WIDTH, y);
			path.close();

			canvas.drawPath(path, mInnerPaint);
			canvas.drawPath(path, mBorderPaint);
		}

		//  Draw title
		canvas.drawText(mOverlayItem.getTitle(), mOffsetX, mOffsetY, mTextPaint);
	}

	/* Internal Functions */

	private void adjustTextBounds(NMapView mapView) {

		//  Determine the screen coordinates of the selected MapLocation
		mapView.getMapProjection().toPixels(mOverlayItem.getPointInUtmk(), mTempPoint);

		final String title = mOverlayItem.getTitle();
		mTextPaint.getTextBounds(title, 0, title.length(), mTempRect);

		//  Setup the callout with the appropriate size & location
		mTempRectF.set(mTempRect);
		mTempRectF.inset(-CALLOUT_TEXT_PADDING_X, -CALLOUT_TEXT_PADDING_Y);
		mOffsetX = mTempPoint.x - mTempRect.width() / 2;
		mOffsetY = mTempPoint.y - mTempRect.height() - mItemBounds.height() - CALLOUT_TEXT_PADDING_Y;
		mTempRectF.offset(mOffsetX, mOffsetY);
	}

	@Override
	protected Drawable getDrawable(int rank, int itemState) {
		
		return null;
	}
}
