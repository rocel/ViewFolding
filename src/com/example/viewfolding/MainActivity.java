package com.example.viewfolding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView txtv;
	private ImageView imgvL;
	private ImageView imgvR;

	private Bitmap left;
	private Bitmap right;
	
	private static int ANGLE = 0;
	
	private final static int LEFT = 1;
	private final static int RIGHT = 2;

	private final static int MODE_FOLD = 1;
	private final static int MODE_UNFOLD = 2;
	
	private static boolean STATE_FOLDED = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		imgvL = (ImageView) findViewById(R.id.imageviewL);
		imgvR = (ImageView) findViewById(R.id.imageviewR);
		
		txtv = (TextView) findViewById(R.id.textview);
		txtv.setDrawingCacheEnabled(true);
		txtv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Bitmap bitmap = txtv.getDrawingCache();
				left = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth()/2, bitmap.getHeight());
				right = Bitmap.createBitmap(bitmap, bitmap.getWidth()/2, 0, bitmap.getWidth()/2, bitmap.getHeight());
				imgvL.setBackgroundDrawable(new BitmapDrawable(left));
				imgvR.setBackgroundDrawable(new BitmapDrawable(right));
			}
		});
		txtv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				foldWithAnimation();
				if (STATE_FOLDED){
					foldWithControledAnimation(left, MODE_UNFOLD, imgvL,LEFT);
					foldWithControledAnimation(right, MODE_UNFOLD, imgvR,RIGHT);
				} else {
					foldWithControledAnimation(left, MODE_FOLD, imgvL,LEFT);
					foldWithControledAnimation(right, MODE_FOLD, imgvR,RIGHT);
				}
				STATE_FOLDED = !STATE_FOLDED;
			}
		});
		txtv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				return false;
			}
		});
	}

	private void foldWithControledAnimation(Bitmap bitmap, int mode, ImageView imageview, int side){
		int angle = 0;
		int centerX = 0;
		
		if(mode == MODE_FOLD && side == LEFT){
			angle = 90;
			centerX = imageview.getWidth();
		} else if(mode == MODE_UNFOLD && side == LEFT){
			angle = -90;
			centerX = -imageview.getWidth();
			
		} else if(mode == MODE_FOLD && side == RIGHT){
			angle = -90;
			centerX = 0;
		} else if(mode == MODE_UNFOLD && side == RIGHT){
			angle = 0;
			centerX = -imageview.getWidth();
		}
		
		String sideS = side==RIGHT?"RIGHT":"LEFT";
		Log.d(this.getClass().getName(), "Rotating with angle:"+angle + " for " + sideS);
		Rotate3dAnimation rotationL = new Rotate3dAnimation(0, angle, centerX, bitmap.getHeight(), 310.0f, true);
	    rotationL.setDuration(800);
	    rotationL.setFillAfter(true);
	    rotationL.setInterpolator(new AccelerateInterpolator());
	    imageview.startAnimation(rotationL);
	}
	
	private void foldWithAnimation() {
		Rotate3dAnimation rotationL = new Rotate3dAnimation(0, 90, left.getWidth(), left.getHeight(), 310.0f, true);
	    rotationL.setDuration(800);
	    rotationL.setFillAfter(true);
	    rotationL.setInterpolator(new AccelerateInterpolator());
	    imgvL.startAnimation(rotationL);
	    
	    Rotate3dAnimation rotationR = new Rotate3dAnimation(0, -90, 0, right.getHeight(), 310.0f, true);
	    rotationR.setDuration(800);
	    rotationR.setFillAfter(true);
	    rotationR.setInterpolator(new AccelerateInterpolator());
	    imgvR.startAnimation(rotationR);
	}
	
	private Bitmap rotate(Bitmap bitmap, int angle){
		Log.d(this.getClass().getName(), "Rotating with angle:"+angle);
		Matrix matrix = new Matrix();
		matrix.postRotate(angle,bitmap.getWidth(),bitmap.getHeight()/2);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
