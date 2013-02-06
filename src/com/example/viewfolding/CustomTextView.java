package com.example.viewfolding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomTextView extends TouchView {
	private TextView txtv;
	private ImageView imgvL;
	private ImageView imgvR;

	private Bitmap left;
	private Bitmap right;
	
	public CustomTextView(Context context) {
		super(context);
		
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
	}

}
