package com.example.viewfolding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivityNoGesture extends Activity {
	private static final int ANGLE = 80;
	private static final int DURATION = 800;
	private static final int NB_CUTS = 4;
	
	
	private TextView txtv;
//	private ImageView imgv1L;
//	private ImageView imgv2L;
//	private ImageView imgv1R;
//	private ImageView imgv2R;
	private LinearLayout holder;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		holder = (LinearLayout) findViewById(R.id.holder_layout);

//		imgv1L = (ImageView) findViewById(R.id.imageview1L);
//		imgv2L = (ImageView) findViewById(R.id.imageview2L);
//		imgv1R = (ImageView) findViewById(R.id.imageview1R);
//		imgv2R = (ImageView) findViewById(R.id.imageview2R);
		
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
		holder.removeAllViews();
		LayoutParams params = new LayoutParams(bitmap.getWidth()/NB_CUTS, bitmap.getHeight());
		
		for(int i = 1; i <= NB_CUTS; i++){
			Bitmap bmp = 	Bitmap.createBitmap(bitmap,	0, 0, bitmap.getWidth()*i/4, bitmap.getHeight());
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
			rotation.setInterpolator(new AccelerateInterpolator());
//			rotation.setAnimationListener(new AfterAnimation(img));
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
}
