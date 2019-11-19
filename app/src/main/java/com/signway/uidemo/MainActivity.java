package com.signway.uidemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ImageView imageView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     /*   imageView = findViewById(R.id.test_img);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"image被点击了",Toast.LENGTH_SHORT).show();
            }
        });
        imageView.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                Log.d(TAG,"image 被移动过了，"+event.getAction());
                return false;
            }
        });
        button = findViewById(R.id.test_button);

        button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG,"button 被选中了，"+hasFocus);
            }
        });
*/

        new Thread(run2).start();

    }
    boolean isRunningThread = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunningThread = false;
    }

    Runnable run2 = new Runnable() {
        @Override
        public void run() {

            while (isRunningThread) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                View rootview = MainActivity.this.getWindow().getDecorView();
                View aaa = rootview.findFocus();
                if (aaa != null)
                    Log.i(TAG,aaa.toString());
            }
        }
    };
}
