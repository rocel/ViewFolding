package com.example.viewfolding.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;

public class DaSurfaceView extends SurfaceView {
	private static final int NB_CUTS = 8; // EVEN NUMBER
	private Bitmap[] panels = new Bitmap[NB_CUTS];
	private int[] panelsCoords = new int[NB_CUTS];
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int mWidth;
	
	int ANGLE = 0;
	
	private ScaleGestureDetector mScaleDetector;
	private float mFoldFactor = 1.f;
	private float mPosX;
	private float mPosY;

	private float mLastTouchX;
	private float mLastTouchY;
	private static final int INVALID_POINTER_ID = -1;
	// The ‘active pointer’ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;
	
	public DaSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		// Create our ScaleGestureDetector
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				final float x = ev.getX();
				final float y = ev.getY();
	
				mLastTouchX = x;
				mLastTouchY = y;
				mActivePointerId = ev.getPointerId(0);
				break;
			}
	
			case MotionEvent.ACTION_UP: {
				mActivePointerId = INVALID_POINTER_ID;
				break;
			}
	
			case MotionEvent.ACTION_CANCEL: {
				mActivePointerId = INVALID_POINTER_ID;
				break;
			}
		}

		return true;
	}

	@Override
	public void setBackground(Drawable drawable) {
		if(drawable instanceof BitmapDrawable){
			Bitmap fullBmp = ((BitmapDrawable)drawable).getBitmap();
			int startX = 0;
			mWidth = fullBmp.getWidth()/NB_CUTS;
			for(int i = 1; i <= NB_CUTS; i++){
				if (i > 1) {
					startX = fullBmp.getWidth()*(i-1)/NB_CUTS;
				}
				Log.d(this.getClass().getName(), "setBackground --> startX = " + startX + " | width = " + mWidth);
//				Bitmap bmp = Bitmap.createBitmap(i%2==0?fullBmp:doBrightness(fullBmp,-30), startX, 0, width, fullBmp.getHeight());
				Bitmap bmp = Bitmap.createBitmap(fullBmp, startX, 0, mWidth, fullBmp.getHeight());
				panels[i-1] = bmp;
				panelsCoords[i-1] = startX;
			}
			invalidate();
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
//		super.draw(canvas);
		if(panels != null){
			for(int i = 0; i < NB_CUTS; i++) {
				Bitmap bitmap = panels[i];
//				Log.d(this.getClass().getName(), "draw --> startX = " + panelsCoords[i] + " | width = " + bitmap.getWidth()); 
//				if(i%2==1)
				canvas.drawBitmap(
					bitmap,
					getSkewMatrix(
						i%2==0?ANGLE:-ANGLE,
						panelsCoords[i],
						i
					),
					paint
				);
			}
		}
		
	}
	
	private Matrix getSkewMatrix(int angle, int startFrom,int position) {
        final Camera camera = new Camera();
        final Matrix matrix = new Matrix();
        camera.save();
        camera.rotateY(angle);
        camera.getMatrix(matrix);
        camera.restore();
        
//		matrix.preTranslate(-mWidth / 2, 0);
//		matrix.postRotate(angle, mWidth, 0);
        
		int decal = (int) (
			(mWidth - (Math.cos(angle * Math.PI / 180) * mWidth)) * position
		) ;
		
        matrix.postTranslate(startFrom-decal, 0f);
		return matrix;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mFoldFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mFoldFactor = Math.max(0.05f, Math.min(mFoldFactor, 1.0f));
			
			ANGLE = (int) Math.toDegrees(Math.acos(mFoldFactor));
			
			Log.d(this.getClass().getName(),
					"   mScaleFactor  ="+mFoldFactor +
					" | getScaleFactor =" + detector.getScaleFactor() +
					" | acos = " + Math.toDegrees(Math.acos(mFoldFactor)));
			
			invalidate();
			return true;
		}
	}
	public static Bitmap doBrightness(Bitmap src, int value) {
	    // image size
	    int width = src.getWidth();
	    int height = src.getHeight();
	    // create output bitmap
	    Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
	    // color information
	    int A, R, G, B;
	    int pixel;
	 
	    // scan through all pixels
	    for(int x = 0; x < width; ++x) {
	        for(int y = 0; y < height; ++y) {
	            // get pixel color
	            pixel = src.getPixel(x, y);
	            A = Color.alpha(pixel);
	            R = Color.red(pixel);
	            G = Color.green(pixel);
	            B = Color.blue(pixel);
	 
	            // increase/decrease each channel
	            R += value;
	            if(R > 255) { R = 255; }
	            else if(R < 0) { R = 0; }
	 
	            G += value;
	            if(G > 255) { G = 255; }
	            else if(G < 0) { G = 0; }
	 
	            B += value;
	            if(B > 255) { B = 255; }
	            else if(B < 0) { B = 0; }
	 
	            // apply new pixel color to output bitmap
	            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
	        }
	    }
	 
	    // return final image
	    return bmOut;
	}

}