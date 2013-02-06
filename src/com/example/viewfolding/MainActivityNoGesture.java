package com.example.viewfolding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivityNoGesture extends Activity {
	private TextView txtv;
	private ImageView imgvL;
	private ImageView imgvR;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		imgvL = (ImageView) findViewById(R.id.imageviewL);
		imgvR = (ImageView) findViewById(R.id.imageviewR);
		
		txtv = (TextView) findViewById(R.id.textview);
		txtv.setDrawingCacheEnabled(true);
//		txtv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//			@Override
//			public void onGlobalLayout() {
//			}
//		});
		txtv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fold();				
			}
		});
	}

	private void fold() {
		Bitmap bitmap = txtv.getDrawingCache();
		Bitmap right = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth()/2, bitmap.getHeight());
		Bitmap left = Bitmap.createBitmap(bitmap, bitmap.getWidth()/2, 0, bitmap.getWidth()/2, bitmap.getHeight());
		imgvL.setBackgroundDrawable(new BitmapDrawable(right));
		imgvR.setBackgroundDrawable(new BitmapDrawable(left));
		
		Rotate3dAnimation rotationL = new Rotate3dAnimation(0, 90, bitmap.getWidth()/2, bitmap.getHeight()/2, 310.0f, true);
	    rotationL.setDuration(800);
	    rotationL.setFillAfter(true);
	    rotationL.setInterpolator(new AccelerateInterpolator());
	    imgvL.startAnimation(rotationL);
	    
	    Rotate3dAnimation rotationR = new Rotate3dAnimation(0, -90, 0, bitmap.getHeight()/2, 310.0f, true);
	    rotationR.setDuration(800);
	    rotationR.setFillAfter(true);
	    rotationR.setInterpolator(new AccelerateInterpolator());
	    imgvR.startAnimation(rotationR);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
