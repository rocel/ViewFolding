package com.example.viewfolding.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class DaSurfaceView extends SurfaceView {
	private static final int NB_CUTS = 4; // EVEN NUMBER
	private Bitmap[] panels = new Bitmap[NB_CUTS];
	private int[] panelsCoords = new int[NB_CUTS];
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public DaSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
	}

	@Override
	public void setBackground(Drawable drawable) {
		if(drawable instanceof BitmapDrawable){
			Bitmap fullBmp = ((BitmapDrawable)drawable).getBitmap();
			int startX = 0;
			final int width = fullBmp.getWidth()/NB_CUTS;
			for(int i = 1; i <= NB_CUTS; i++){
				if (i > 1) {
					startX = fullBmp.getWidth()*(i-1)/NB_CUTS;
				}
				Log.d(this.getClass().getName(), "setBackground --> startX = " + startX + " | width = " + width);
//				Bitmap bmp = Bitmap.createBitmap(i%2==0?fullBmp:doBrightness(fullBmp,-30), startX, 0, width, fullBmp.getHeight());
				Bitmap bmp = Bitmap.createBitmap(fullBmp, startX, 0, width, fullBmp.getHeight());
				panels[i-1] = bmp;
				panelsCoords[i-1] = startX;
			}
			invalidate();
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
//		super.draw(canvas);
		if(panels != null){
			int ANGLE = 20;
			for(int i = 0; i < NB_CUTS; i++) {
				Bitmap bitmap = panels[i];
				Log.d(this.getClass().getName(), "draw --> startX = " + panelsCoords[i] + " | width = " + bitmap.getWidth()); 
				canvas.drawBitmap(
					bitmap,
					getSkewMatrix(
						i%2==0?ANGLE:-ANGLE,
						panelsCoords[i]
					),
					paint
				);
			}
		}
	}
	
	private Matrix getSkewMatrix(int angle, int startFrom) {
        final Camera camera = new Camera();
        final Matrix matrix = new Matrix();
        camera.save();
        camera.rotateY(angle);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.postTranslate(startFrom, 0f);
		return matrix;
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