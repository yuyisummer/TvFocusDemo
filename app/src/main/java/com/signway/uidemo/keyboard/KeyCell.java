package com.signway.uidemo.keyboard;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * 小键盘用
 */
public class KeyCell {
	private static final String TAG = "KeyCell";
	Shader shader;
	// shader =new LinearGradient(0,mBound.top,0,mBound.height()/2,
	// new int[]{0xFF666666,Color.DKGRAY},
	// null,Shader.TileMode.MIRROR);

	private static final int corner = 10;
	protected RectF mBound = null;
	protected Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	int dx, dy, updx, updy;
	public String mText;
	private String upText;
	private boolean hasUpText = false;
	private boolean hasFocus = false;

	private static final int bigsize = 28;
	private static final int smallsize = 18;

	public KeyCell(String _text, RectF rect) {
		mText = _text;
		mBound = rect;
		mPaint.setTextSize(bigsize);
		mPaint.setFakeBoldText(true);
		dx = (int) mPaint.measureText(String.valueOf(mText)) / 2;
		dy = (int) (-mPaint.ascent() + mPaint.descent()) / 2;

	}

	public KeyCell(String _text, String _upText, RectF rect) {
		mText = _text;
		upText = _upText;
		mBound = rect;
		mPaint.setFakeBoldText(true);
		if (_upText != null && !_upText.equals("")) {
			mPaint.setTextSize(bigsize);
			dx = (int) mPaint.measureText(String.valueOf(mText)) / 2;
			dy = (int) (-mPaint.ascent() + mPaint.descent());
			hasUpText = true;
			mPaint.setTextSize(smallsize);
			updx = (int) mPaint.measureText(String.valueOf(upText)) / 2;
			updy = (int) (-mPaint.ascent() + mPaint.descent());
		} else {
			mPaint.setTextSize(bigsize);
			dx = (int) mPaint.measureText(String.valueOf(mText)) / 2;
			dy = (int) (-mPaint.ascent() + mPaint.descent()) / 2;
		}
	}

	public void draw(Canvas canvas) {
		if (hasFocus) {
			mPaint.setColor(Color.LTGRAY);
		} else {
			mPaint.setColor(Color.DKGRAY);
			mPaint.setStyle(Style.FILL);
		}
		canvas.drawRoundRect(mBound, corner, corner, mPaint);
		mPaint.setColor(Color.WHITE);
		mPaint.setShadowLayer(1, 2, 1, Color.BLACK);
		if (hasUpText) {
			mPaint.setTextSize(smallsize);
			canvas.drawText(String.valueOf(upText), mBound.centerX() - updx,
					mBound.top + updy, mPaint);
			mPaint.setTextSize(bigsize);
			canvas.drawText(String.valueOf(mText), mBound.centerX() - dx,
					mBound.centerY() + dy / 4 * 3, mPaint);
		} else {
			mPaint.setTextSize(bigsize);
			canvas.drawText(String.valueOf(mText), mBound.centerX() - dx - 2,
					mBound.centerY() + dy - 4, mPaint);
		}
	}

	public void getFocus() {
		hasFocus = true;
	}

	public void clearFocus() {
		hasFocus = false;
	}

	public RectF getBound() {
		return mBound;
	}

	public boolean hitTest(int x, int y) {
		return mBound.contains(x, y);
	}
}
