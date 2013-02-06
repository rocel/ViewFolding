package com.example.viewfolding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Bitmaptest extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        LinearLayout linLayout = new LinearLayout(this);

        // load the origial BitMap (500 x 500 px)
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        int newWidth = 400;
        int newHeight = 400;

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap
        matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);

        // make a Drawable from Bitmap to allow to set the BitMap
        // to the ImageView, ImageButton or what ever
        BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);

        ImageView imageView = new ImageView(this);

        // set the Drawable on the ImageView
        imageView.setImageDrawable(bmd);

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
}
