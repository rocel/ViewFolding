package com.example.viewfolding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Bitmaptest2 extends Activity {
	static final int MODE_NORMAL = 0;
	static final int MODE_CLOCK = 1;
	static final int MODE_COUNTER = 2;
	
	Button btnClock;
	Button btnNormal;
	Button btnCounter;
	ImageView imageView;
	Bitmap bitmapOrg;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.bitmaptest_activity);

        btnCounter = (Button) findViewById(R.id.btn_counter);
        btnCounter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        setImage(MODE_COUNTER, imageView.getWidth()/2f, imageView.getHeight()/2f);
			}
		});
        btnNormal = (Button) findViewById(R.id.btn_normal);
        btnNormal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        setImage(MODE_NORMAL, imageView.getWidth()/2f, imageView.getHeight()/2f);
			}
		});
        btnClock = (Button) findViewById(R.id.btn_clock);
        btnClock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        setImage(MODE_CLOCK, imageView.getWidth()/2f, imageView.getHeight()/2f);
			}
		});
        imageView = (ImageView) findViewById(R.id.imageView);

        bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.rio);

        setImage(MODE_NORMAL, imageView.getWidth()/2f, imageView.getHeight()/2f);
        
    }

	private void setImage(int modeNormal, float centerX, float centerY) {
		Bitmap bitmap = null;
		Canvas ca = null;
		final Matrix matrix;
		switch (modeNormal) {
		case MODE_CLOCK:
			matrix = getMatrix(centerY, MODE_CLOCK, centerX);
			bitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);
			break;
		case MODE_NORMAL:
	        bitmap = bitmapOrg;
			break;
		case MODE_COUNTER:
			matrix = getMatrix(centerY, MODE_COUNTER, centerX);
			bitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);
//			bitmap = bitmapOrg.copy(Bitmap.Config.ARGB_8888, true);
//			ca = new Canvas(bitmap);
//			ca.drawBitmap(bitmap, 0, 0, null);
//			Paint paint = new Paint();
//	        paint.setARGB(255, 0, 0, 255);
//	        paint.setAntiAlias(true);
//	        paint.setStyle(Style.FILL);
//	        paint.setStrokeWidth(2);
//			ca.drawCircle(2, 2, 2, paint);
//			ca.skew(45, 45);
//			ca.rotate(45, 0, 0);
			break;
		default:
			break;
		}
		Log.d(this.getClass().getName(), "width : " + bitmap.getWidth());
		Log.d(this.getClass().getName(), "height : " + bitmap.getHeight());
		Log.d(this.getClass().getName(),"------------------------");
		
		imageView.setImageDrawable(new BitmapDrawable(bitmap));
        imageView.invalidate();
	}
	

	private Matrix getMatrix(final float centerY, final int mode, float centerX) {
		int degree = 45;
		
		Camera camera = new Camera();
		final Matrix matrix = new Matrix();

        
        if (mode == MODE_COUNTER) {
            camera.save();
        	camera.rotateY(-degree);
		} else if (mode == MODE_CLOCK) {
	        camera.save();
			camera.rotateY(degree);
		}
        camera.getMatrix(matrix);
    	
        camera.restore();
//        if (mode == MODE_COUNTER) {
        matrix.postTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
//        }
        
		return matrix;
	}
}
