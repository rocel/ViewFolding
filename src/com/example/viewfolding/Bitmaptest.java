package com.example.viewfolding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Bitmaptest extends Activity {
	private static final int MODE_TO_RIGHT = 0;
	private static final int MODE_TO_LEFT = 1;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        LinearLayout linLayout = new LinearLayout(this);

        // load the origial BitMap (500 x 500 px)
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.rio);

        final Matrix matrix2 = getMatrix(bitmapOrg.getHeight(), MODE_TO_RIGHT);
        
        Bitmap bitmapT = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix2, true);
        bitmapOrg.recycle();
        
        /**
         * DISPLAY 
         */
        ImageView imageView = new ImageView(this);
        
        // set the Drawable on the ImageView
        imageView.setImageDrawable(new BitmapDrawable(bitmapT));

        // center the Image
        imageView.setScaleType(ScaleType.CENTER);

        // add ImageView to the Layout
        linLayout.addView(imageView,
                new LinearLayout.LayoutParams(
                      LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
                )
        );

        // set LinearLayout as ContentView
        setContentView(linLayout);
    }

	private Matrix getMatrix(final float centerY, final int mode) {
		Camera camera = new Camera();
		final Matrix matrix = new Matrix();
        camera.save();
//        if (mode == MODE_TO_RIGHT) {
//        	camera.rotateY(135);
//		} else if (mode == MODE_TO_LEFT) {
			camera.rotateY(45);
//		}
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(0, -centerY/2);
        if (mode == MODE_TO_RIGHT) {
        	matrix.postScale(-1.0f, 1.0f);
        }
		return matrix;
	}
}
