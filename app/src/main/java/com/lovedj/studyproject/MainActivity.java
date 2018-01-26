package com.lovedj.studyproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

   // static {System.loadLibrary("native-lib");}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
   // public native String stringFromJNI();

    public void addLove(View view) {
        LoveLayout love_layout =findViewById(R.id.love_layout);
        love_layout.addLove();
    }
}
