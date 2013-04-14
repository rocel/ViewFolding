package com.example.viewfolding.surface;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;

public class DaSurfaceView extends SurfaceView {
	private boolean AUTOMATIC_FOLD_ON_EDGES = true;
	private boolean START_FOLDED = true;

	private static final int MINIMAL_ANGLE_AUTO_OPEN = 75;
	private static final int MINIMAL_ANGLE_AUTO_FOLD = 30;
	private static final int MAXIMAL_ANGLE = 85;
	private static final int NB_CUTS = 16; // EVEN NUMBER

	private static final String TAG = "ANIMATION";

	private Bitmap[] panels = new Bitmap[NB_CUTS];
	
	private int[] panelsCoords = new int[NB_CUTS];
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static int DECAL = 0;
	private int mWidth;
	private int mHeight;
	
	private boolean isFolded = false;
	private boolean isFolding = false;
	private static boolean iTouchingScreen = false;
	private int ANGLE = 0;
	
	private ScaleGestureDetector mScaleDetector;
	private GestureDetector gestureDetector;
	private float mFoldFactor = 1.f;
	private float mPosX;
	private float mPosY;

	private float mLastTouchX;
	private float mLastTouchY;
	private static final int INVALID_POINTER_ID = -1;
	// The ‘active pointer’ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;
	
	//ANIMATIONS
	ValueAnimator foldingAnim ;
	ValueAnimator openAnim;

	public DaSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		// Create our ScaleGestureDetector
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	    gestureDetector = new GestureDetector(context, new GestureListener());
	    initOpenAnim();
		initFoldingAnim();
	}
	
	private void initFoldingAnim() {
		foldingAnim = ValueAnimator.ofInt(ANGLE, MAXIMAL_ANGLE);
		foldingAnim.setDuration(500);
		foldingAnim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				isFolding = true;
				Integer value = (Integer) animation.getAnimatedValue();
				ANGLE = value;
				Log.d(TAG, ANGLE+"°");
				if (ANGLE >= MAXIMAL_ANGLE) {
					ANGLE = MAXIMAL_ANGLE;
					isFolded = true;
					isFolding = false;
					mFoldFactor = 0;
				} else {
					isFolded = false;
				}
				invalidate();
			}
		});
	}

	private void initOpenAnim() {
		openAnim = ValueAnimator.ofInt(ANGLE, 0);
		openAnim.setDuration(500);
		openAnim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				isFolding = true;
				Integer value = (Integer) animation.getAnimatedValue();
				ANGLE = value;
				Log.d(TAG, ANGLE+"°");
				if (ANGLE <= 0) {
					ANGLE = 0;
					isFolding = false;
					isFolded = false;
					mFoldFactor = 1;
				}
				invalidate();
			}
		});
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
	    @Override
	    public boolean onDown(MotionEvent e) {
	        return true;
	    }
	    
	    @Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
	    	open();
			return true;
		}

		@Override
	    public boolean onDoubleTap(MotionEvent e) {
	    	if(isFolded){
	    		open();
	    	} else {
	    		fold();
	    	}
	        return true;
	    }
	}
	
	private void fold(){
		Log.d(TAG, "OPENING");
		initFoldingAnim();
		foldingAnim.start();
	}

	private void open(){
		Log.d(TAG, "CLOSING");
		initOpenAnim();
		openAnim.start();
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
				iTouchingScreen = true;
				break;
			}
			case MotionEvent.ACTION_UP: {
				mActivePointerId = INVALID_POINTER_ID;
				iTouchingScreen = false;
				invalidate();
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				mActivePointerId = INVALID_POINTER_ID;
				iTouchingScreen = false;
				invalidate();
				break;
			}
		}

	    return gestureDetector.onTouchEvent(ev);
	}

	@Override
	public void setBackground(Drawable drawable) {
		if(drawable instanceof BitmapDrawable){
			Bitmap fullBmp = ((BitmapDrawable)drawable).getBitmap();
			int startX = DECAL;
			mWidth = fullBmp.getWidth()/NB_CUTS;
			mHeight = fullBmp.getHeight();
			for(int i = 1; i <= NB_CUTS; i++){
				if (i > 1) {
					startX = fullBmp.getWidth()*(i-1)/NB_CUTS;
				}
				Log.d(this.getClass().getName(), "setBackground --> startX = " + startX + " | width = " + mWidth);
//				Bitmap bmp = Bitmap.createBitmap(i%2==0?fullBmp:doBrightness(fullBmp,-30), startX, 0, mWidth, fullBmp.getHeight());
				Bitmap bmp = Bitmap.createBitmap(fullBmp, startX, 0, mWidth, fullBmp.getHeight());
				panels[i-1] = bmp;
				panelsCoords[i-1] = startX;
			}
			invalidate();
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(panels != null) {
			//Folding is in the begining and user not touching screen
			if(AUTOMATIC_FOLD_ON_EDGES && !iTouchingScreen && !isFolding && ANGLE<MINIMAL_ANGLE_AUTO_FOLD && ANGLE>0){
				open();
			//Folding is in the end and user not touching screen
			} else if(AUTOMATIC_FOLD_ON_EDGES && !iTouchingScreen && !isFolding && ANGLE<MAXIMAL_ANGLE && ANGLE>MINIMAL_ANGLE_AUTO_OPEN){
				fold();
			}else{
				int start = 0;
				if(DECAL!=0){
					start = 1;
					canvas.drawBitmap(
							panels[0],
							0,
							0,
							paint
						);
				}
				for(int i = start ; i < NB_CUTS; i++) {				
	//				if(i%2==0)
					canvas.drawBitmap(
						panels[i],
						getFoldingMatrix(
							i%2==0?ANGLE:-ANGLE,
							panelsCoords[i],
							i
						),
						getBrightnessPaint(i, ANGLE)
					);
					//ADD GradientDrawable FOR britghness
				}
			}
		}
	}
	
	private Matrix getFoldingMatrix(int angle, int startFrom,int position) {
		int move = (int) (
			(mWidth - (Math.cos(angle * Math.PI / 180) * mWidth)) * position
		) +1*position; //overlap of 1px the panels and not get space between panels
		
        final Camera camera = new Camera();
        final Matrix matrix = new Matrix();
        camera.save();
        
        camera.rotateY(angle);
        camera.getMatrix(matrix);
        
        if(position%2==1){
        	move += (int) (
    			(mWidth - (Math.cos(angle * Math.PI / 180) * mWidth))
    		) ;
        	matrix.preTranslate(-mWidth, -mHeight/2);
        	matrix.postTranslate(startFrom+mWidth-move, mHeight/2);
        } else {
        	matrix.preTranslate(0f, -mHeight/2);
        	matrix.postTranslate(startFrom-move, mHeight/2);
        }
        camera.restore();
        
		return matrix;
	}

	private Paint getBrightnessPaint(int position, int angle) {
		//BRIGHTNESS
		Log.d("BRIGHTNESS","angle:"+angle);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		if(position%2==0){
			//NORMAL
	        ColorMatrix cm = new ColorMatrix();
	        float contrast = 1;
	        float brightness = -angle;
			cm.set(new float[] { contrast, 0, 0, 0, brightness, 0, contrast, 0, 0,
					brightness, 0, 0, contrast, 0, brightness, 0, 0, 0, 1, 0 });
	        paint.setColorFilter(new ColorMatrixColorFilter(cm));
			
			//GRADIENT
//			paint.setStyle(Paint.Style.FILL);
//		    Shader mShader = new LinearGradient(0, 0, 0, mWidth, new int[] {
//                    Color.RED, Color.GREEN, Color.BLUE },
//                    null, Shader.TileMode.REPEAT);  // CLAMP MIRROR REPEAT
//		    paint.setShader(mShader);  // CLAMP MIRROR REPEAT

		}
		return paint;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mFoldFactor *= detector.getScaleFactor();
			// Don't let the object get too small or too large.
			mFoldFactor = Math.max(0.05f, Math.min(mFoldFactor, 1.0f));
			
			Log.d(TAG,"mFoldFactor="+mFoldFactor);
			
			ANGLE = (int) Math.toDegrees(Math.acos(mFoldFactor));
			if (ANGLE <= 0) {
				isFolded = true;
			} else {
				isFolded = false;
			}
			
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