package com.example.viewfolding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivityNoGesture extends Activity {
	private static final int ANGLE = 85;
	private static final int DURATION = 1000;
	private static final int NB_CUTS = 2; // EVEN NUMBER
	
	private TextView txtv;
	private LinearLayout holder;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		holder = (LinearLayout) findViewById(R.id.holder_layout);
		
		txtv = (TextView) findViewById(R.id.textview);
		txtv.setDrawingCacheEnabled(true);
		txtv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fold();				
			}
		});
	}

	private void fold() {
		bitmap = txtv.getDrawingCache();
		Log.d(CustomRotate3dAnimation.TAG_END, bitmap.getWidth()+ "");
		holder.removeAllViews();
		LayoutParams params = new LayoutParams(bitmap.getWidth()/NB_CUTS, bitmap.getHeight());
		
		int startX = 0;
		final int width = bitmap.getWidth()/4;
		for(int i = 1; i <= NB_CUTS; i++){
			if (i > 1) {
				startX = bitmap.getWidth()*(i-1)/4;
			}
			Log.d(this.getClass().getName(), "startX = " + startX);
			Bitmap bmp = Bitmap.createBitmap(i%2==0?bitmap:doBrightness(bitmap,-30), startX, 0, width, bitmap.getHeight());
			ImageView img = new ImageView(this);
			img.setBackgroundDrawable(new BitmapDrawable(bmp));
			CustomRotate3dAnimation rotation = new CustomRotate3dAnimation(
					0,
					i%2==1?ANGLE:-ANGLE,
					i%2==1?0:bitmap.getWidth()/NB_CUTS,
					bitmap.getHeight() / 2,
					310.0f,
					i,
					CustomRotate3dAnimation.DIRECTION_LEFT,
					NB_CUTS,
					bitmap.getWidth()
			);
			rotation.setDuration(DURATION);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new AccelerateDecelerateInterpolator());
		    img.startAnimation(rotation);
		    holder.addView(img,params);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	

	class AfterAnimation implements AnimationListener{
		View mView;
		
		public AfterAnimation(View view){
			this.mView = view;
		}
		
		@Override
		public void onAnimationStart(Animation animation) { }
	
		@Override
		public void onAnimationEnd(Animation animation) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, bitmap.getHeight());
			mView.setLayoutParams(params);
		}
	
		@Override
		public void onAnimationRepeat(Animation animation) { }
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
