package com.example.viewfolding;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

public class FoldingLayout extends FrameLayout {
	private static final int MODE_TO_COUNTER_CLOCK = 0;
	private static final int MODE_TO_CLOCK = 1;
	private static final int FOLDS_SIZE = 100;
	
	private int nbFolds = 2;
	
	private static final float DEPTH8Z = 310.0f;
	
	private Paint mPaint;
	private Bitmap mBitmap;
	private boolean folding = false;
	private List<Bitmap> mBitmaps = new ArrayList<Bitmap>();
	private int interpolation = 1;
	private int mHeightMeasureSpec;
	private int mWidthMeasureSpec;
	
	
	public FoldingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setWillNotDraw(false);
		mPaint = new Paint();
		mPaint.setColor(Color.GREEN);
		this.setDrawingCacheEnabled(true);
		this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mBitmap = FoldingLayout.this.getDrawingCache();
				View v = getChildAt(0);
//				nbFolds = mBitmap.getWidth()/FOLDS_SIZE;
				nbFolds = 10;
				Log.d(this.getClass().getName(),"nbFolds = " + nbFolds);
				for(int i = 0; i < nbFolds ; i++){
//					Log.d(this.getClass().getName(), i + " -> x : " + (i==0?0:mBitmap.getWidth()/nbFolds*i));
//					Log.d(this.getClass().getName(), i + " -> width : " + (mBitmap.getWidth()/nbFolds));
					
					Bitmap nbitmap = Bitmap.createBitmap(mBitmap,
							(mBitmap.getWidth()/nbFolds)*i,
							0,
							mBitmap.getWidth()/nbFolds,
							mBitmap.getHeight());
					if(i%2==0){
						mBitmaps.add(nbitmap);
//						mBitmaps.add(doBrightness(nbitmap,-90));
					} else {
						mBitmaps.add(nbitmap);
					}
				}
			}
		});
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				folding =  !folding;
				invalidate();
			}
		});
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
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix m = new Matrix();
		if(folding){
			System.out.println("-------------------- DRAWING !!!!");
			getChildAt(0).setVisibility(View.GONE);
			for( int i = 0 ; i<nbFolds ;i++ ){
				if (i % 2 == 0){
					canvas.drawBitmap(mBitmaps.get(i),getMatrix(0, MODE_TO_CLOCK,i), null);
				} else {
					canvas.drawBitmap(mBitmaps.get(i),getMatrix(0, MODE_TO_COUNTER_CLOCK,i), null);
				}
			}
		} else {
			for (int i = 0; i < getChildCount(); i++) {
				getChildAt(i).setVisibility(View.VISIBLE);
			}
		}
	}

	private Matrix getMatrix(final float centerY, final int mode,final int position) {
		int degree = 45;
        Camera camera = new Camera();
		final Matrix matrix = new Matrix();
        camera.save();
        if (mode == MODE_TO_COUNTER_CLOCK) {
        	camera.rotateY(-degree);
		} else if (mode == MODE_TO_CLOCK) {
			camera.rotateY(degree);
		}
        camera.getMatrix(matrix);
//        matrix.preTranslate(0, -centerY/2);
        // if translation from LEFT TO RIGHT other wise + Math.cos(degree)*mBitmap.getWidth()
//        int perspective  = (int) (Math.cos(degree)*mBitmap.getWidth()/nbFolds * position);
        int perspective  = 0;
        float move = (float) ((mBitmap.getWidth()/nbFolds)*position - perspective);
        Log.d("MOVE", position + " W --> " + mBitmap.getWidth()/nbFolds);
        Log.d("MOVE", position + " M --> " + move);
        Log.d("MOVE", position + " P --> " + perspective);
        Log.d("MOVE","------------------------");
        matrix.postTranslate(
        		move,
        		0);

        camera.restore();
        
		return matrix;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(widthMeasureSpec>0){
			mWidthMeasureSpec = View.MeasureSpec.getSize(widthMeasureSpec);
		}
		if(heightMeasureSpec>0){
			mHeightMeasureSpec = View.MeasureSpec.getSize(heightMeasureSpec);
		}

		Log.d(this.getClass().getName(), "widthMeasureSpec : "+View.MeasureSpec.getSize(widthMeasureSpec));
		Log.d(this.getClass().getName(), "heightMeasureSpec : "+View.MeasureSpec.getSize(heightMeasureSpec));
		
		if(folding) {
			//TODO : once it is folded in , reMeasure with smaller size accoring to size.
			this.setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
		}
	}
	
	
	//IMPORTANT ?
	@Override
	protected boolean verifyDrawable(Drawable who) {
		return true;
	}

}
