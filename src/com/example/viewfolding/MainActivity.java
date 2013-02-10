package com.example.viewfolding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folding);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
