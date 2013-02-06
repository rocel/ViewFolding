package com.example.viewfolding;


import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

public class TouchExampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TouchExampleView view = new TouchExampleView(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        
        setContentView(view);
    }
}