package com.martinrgb.animerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        animer = new Animer(iv ,SpringSolver.createOrigamiSpring(5,10),Animer.SCALE_Y,1,2);
        animer.setActionerAndListener(iv, new Animer.ActionTouchListener() {
            @Override
            public void onDown(View view, MotionEvent event) {
                animer.setSolver(SpringSolver.createOrigamiSpring(20,10));
                animer.start();
            }

            @Override
            public void onMove(View view, MotionEvent event, float velocityX, float velocityY) {

            }

            @Override
            public void onUp(View view, MotionEvent event) {
                animer.setSolver(SpringSolver.createOrigamiSpring(5,10));
                animer.reverse();
            }

            @Override
            public void onCancel(View view, MotionEvent event) {

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
