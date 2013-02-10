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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

public class FoldingLayout extends FrameLayout {
	private static final int MODE_TO_RIGHT = 0;
	private static final int MODE_TO_LEFT = 1;
	private static final int FOLDS_SIZE = 50;
	
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
				nbFolds = mBitmap.getWidth()/FOLDS_SIZE;
				Log.d(this.getClass().getName(),"nbFolds = " + nbFolds);
				for(int i = 0; i < nbFolds ; i++){
//					Bitmap.createBitmap(bitmap, 	0, 					 0, bitmap.getWidth()/2, bitmap.getHeight()); //LEFT
//					Bitmap.createBitmap(bitmap, 	bitmap.getWidth()/2, 0, bitmap.getWidth()/2, bitmap.getHeight()); //RIGHT
					Log.d(this.getClass().getName(), i + " -> x : " + (i==0?0:mBitmap.getWidth()/nbFolds*i));
					Log.d(this.getClass().getName(), i + " -> width : " + (mBitmap.getWidth()/nbFolds));
					
					Bitmap nbitmap = Bitmap.createBitmap(mBitmap,
							(i==0?0:mBitmap.getWidth()/nbFolds*i),
							0,
							mBitmap.getWidth()/nbFolds,
							mBitmap.getHeight());
					if(i%2==0){
						mBitmaps.add(doBrightness(nbitmap,-50));
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
		        canvas.drawBitmap(mBitmaps.get(i),getMatrix(0, MODE_TO_LEFT,i), null);
			}
		} else {
			for (int i = 0; i < getChildCount(); i++) {
				getChildAt(i).setVisibility(View.VISIBLE);
			}
		}
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		if(widthMeasureSpec>0){
//			mWidthMeasureSpec = widthMeasureSpec;
//		}
//		if(heightMeasureSpec>0){
//			mHeightMeasureSpec = heightMeasureSpec;
//		}
//		if(mWidthMeasureSpec>0 && mHeightMeasureSpec>0){
//			this.setMeasuredDimension(mWidthMeasureSpec, mHeightMeasureSpec);
//		}
//	}

	private Matrix getMatrix(final float centerY, final int mode,final int position) {
		Camera camera = new Camera();
		final Matrix matrix = new Matrix();
        camera.save();
        if (mode == MODE_TO_RIGHT) {
        	camera.rotateY(135);
		} else if (mode == MODE_TO_LEFT) {
			camera.rotateY(45);
		}
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(0, -centerY/2);
        matrix.postTranslate(position==0?0:mBitmap.getWidth()/nbFolds*position, 0);
//        if (mode == MODE_TO_RIGHT) {
//        	matrix.postScale(-1.0f, 1.0f);
//        }
		return matrix;
	}
}