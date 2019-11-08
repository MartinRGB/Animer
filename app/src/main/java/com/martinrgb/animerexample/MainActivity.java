package com.martinrgb.animerexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.martinrgb.animer.Animer;


public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private Animer animer;
    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);

        animer = new com.martinrgb.animer.Animer(iv , com.martinrgb.animer.Animer.createInterpolatorAndroid(new FastOutSlowInInterpolator(),500),com.martinrgb.animer.Animer.TRANSLATION_X,0,900);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isOpen){
                    animer.setSolver(com.martinrgb.animer.Animer.createInterpolatorAndroid(new FastOutSlowInInterpolator(),500));
                    animer.animateToState("End");
                }
                else{
                    animer.setSolver(com.martinrgb.animer.Animer.createSpringAndroid(500,0.5f));
                    animer.setEndvalue(40);
                }
                isOpen = !isOpen;
            }
        });

    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}
