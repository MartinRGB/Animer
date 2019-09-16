package com.martinrgb.swipeexample;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.martinrgb.animer.controller.AnProperty;
import com.martinrgb.animer.controller.Animer;
import com.martinrgb.animer.solver.AnSolver;
import com.martinrgb.animer.solver.SpringSolver;
import com.martinrgb.animer.solver.TimingSolver;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout card;
    private ImageView card_mask;
    private Animer mAnim;
    private boolean isClicked =false;
    private View view;
    private float mPrevVelocity = 0,mCurrentVelocity = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        card = findViewById(R.id.page_1_card);
        card_mask = findViewById(R.id.page_1_card_mask);


        // # Animation Solver
        AnSolver mTimingSolver = new TimingSolver(new DecelerateInterpolator(),500);
        mAnim = new Animer(card,new TimingSolver(new DecelerateInterpolator(), 2000), AnProperty.TRANSLATION_X,0,700);
        mAnim.start();
        card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float startX = 0,fixX = 0,transX = 0,transY = 0;
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = view.getX() - motionEvent.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        transX = motionEvent.getRawX() + startX - (view.getWidth() / 2);
                        if(mAnim.isRunning()){
                            mAnim.cancel();
                        }
                        mAnim.switchTo(transX);
                        break;
                    case MotionEvent.ACTION_UP:
                        mAnim.setSolver(SpringSolver.createAndroidSpring(500,0.5f));
                        mAnim.animateToState("Start");
                        break;
                }
                return true;
            }
        });

    }

    private void deleteBars() {
        //Delete Title Bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Delete Action Bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}
