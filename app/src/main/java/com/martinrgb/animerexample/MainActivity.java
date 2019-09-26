package com.martinrgb.animerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.martinrgb.animer.core.Animer;
import com.martinrgb.animer.core.solver.SpringSolver;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private  Animer animer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);
        animer = new Animer(iv ,SpringSolver.createOrigamiSpring(5,10),Animer.TRANSLATION_X,0,500);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iv.getTranslationX() != 500){
                    animer.setEndvalue(500);
                }
                else{
                    animer.setEndvalue(0);
                }
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
