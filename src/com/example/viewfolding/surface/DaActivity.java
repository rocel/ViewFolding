package com.example.viewfolding.surface;

import java.io.File;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.example.viewfolding.R;

@SuppressLint("NewApi")
public class DaActivity extends Activity {

	private static final int INVALID_POINTER_ID = -1;

	private ScaleGestureDetector mScaleDetector;
	private float mLastTouchX;
	private float mLastTouchY;
	private int mActivePointerId = INVALID_POINTER_ID;

	private RelativeLayout mainLayout;
	private Bitmap bitmap;
	private DaSurfaceView surfaceView;

	private View daview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.da_layout);
		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);	
		surfaceView = (DaSurfaceView) findViewById(R.id.surface);
		daview = (View) findViewById(R.id.daview);
		
    	surfaceView.setVisibility(View.GONE);
		
		ViewTreeObserver vto = mainLayout.getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
		    @Override 
		    public void onGlobalLayout() { 
		    	mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this); 
		        createBitmapView();
				switchView(true);
				saveBitmap();
		    }
		});
		
	}

	private void saveBitmap() {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/cache_images");    
		myDir.mkdirs();
		String fname = "cache.jpg";
		File file = new File (myDir, fname);
		FileOutputStream out = null;
		if (file.exists ()) file.delete (); 
		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void createBitmapView() {
		bitmap = loadBitmapFromView(mainLayout);
	}

	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(
			v.getMeasuredWidth(), 
			v.getMeasuredHeight(), 
			Bitmap.Config.ARGB_8888
		);                
		Canvas c = new Canvas(b);
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		v.draw(c);
		return b;
	}

	private void setDrawable(ViewGroup viewGroup) {
		for(int i = 0 ; i< viewGroup.getChildCount(); i++){
			viewGroup.getChildAt(i).setDrawingCacheEnabled(true);
			if(viewGroup.getChildAt(i) instanceof ViewGroup){
				setDrawable((ViewGroup)viewGroup.getChildAt(i));
			}
		}
	}

	private void switchView(boolean showSurface) {
		if (showSurface){
			surfaceView.setBackground(new BitmapDrawable(bitmap));
			surfaceView.setVisibility(View.VISIBLE);
			daview.setVisibility(View.GONE);
		} else {
			daview.setVisibility(View.VISIBLE);
			surfaceView.setVisibility(View.GONE);
		}
	}

}
