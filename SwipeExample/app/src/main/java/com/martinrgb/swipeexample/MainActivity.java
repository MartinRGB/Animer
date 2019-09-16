package com.martinrgb.swipeexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.SpringAnimation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.martinrgb.animer.controller.AnProperty;
import com.martinrgb.animer.controller.Animer;
import com.martinrgb.animer.solver.AnSolver;
import com.martinrgb.animer.solver.SpringSolver;
import com.martinrgb.animer.solver.TimingSolver;
import com.martinrgb.animer.util.AnUtil;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout card,page2;
    private ImageView card_mask,click_area,drag_area;
    private Animer mClickAnim,mOpenAnim;
    private VelocityTracker velocityTracker;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        card = findViewById(R.id.page_1_card);
        card_mask = findViewById(R.id.page_1_card_mask);
        click_area = findViewById(R.id.click_area);
        drag_area = findViewById(R.id.drag_area);
        page2 = findViewById(R.id.page_2);

        setupAnimer();

        Animer animer = new Animer(new SpringSolver(200,0.5f));
        animer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {
                Log.e("Value",String.valueOf(value));
            }
        });
        animer.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupAnimer(){
        mOpenAnim = new Animer(page2,new SpringSolver(450,0.92f), AnProperty.TRANSLATION_X,1080,0f);

        mOpenAnim.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity,float progress) {

                //Log.e("Velocity",String.valueOf(velocity));
                float alphaValue = (float) AnUtil.mapClampedValueFromRangeToRange(progress,0,1,0,0.5f);
                float transValue = (float) AnUtil.mapClampedValueFromRangeToRange(progress,0,1,0,-200);
                findViewById(R.id.page_mask).setAlpha(alphaValue);
                findViewById(R.id.page_1).setTranslationX(transValue);

                if(progress > 0.5){
                    float bottomTrans = (float) AnUtil.mapClampedValueFromRangeToRange(progress,0.5,1,240,0);
                    findViewById(R.id.page_2_bottom).setTranslationY(bottomTrans);
                }
            }
        });

        mClickAnim = new Animer(card_mask,SpringSolver.createOrigamiSpring(10,12), AnProperty.ALPHA,0,0.3f);
        mClickAnim.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity,float progress) {


                float scaleValue = (float) AnUtil.mapValueFromRangeToRange(progress,0,1,1,1.03);
                card.setScaleX(scaleValue);
                card.setScaleY(scaleValue);
            }
        });



        card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mClickAnim.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        mClickAnim.reverse();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                mOpenAnim.setSolver(new SpringSolver(450, 0.92f));
                                mOpenAnim.start();
                            }
                        },50 );
                        break;
                }
                return true;
            }
        });

        click_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOpenAnim.setSolver(new SpringSolver(850, 0.92f));
                mOpenAnim.reverse();
                mClickAnim.start();
                mClickAnim.reverse();
            }
        });

        drag_area.setOnTouchListener(new View.OnTouchListener() {
            float dX = 0,initXVel = 0.f,initXPos,isBack;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mOpenAnim.setSolver(new TimingSolver(new LinearInterpolator(),0));
                        dX = view.getX() - motionEvent.getRawX();
                        if (velocityTracker == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            velocityTracker = VelocityTracker.obtain();
                        } else {
                            // Reset the velocity tracker back to its initial state.
                            velocityTracker.clear();
                        }
                        velocityTracker.addMovement(motionEvent);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        velocityTracker.addMovement(motionEvent);
                        velocityTracker.computeCurrentVelocity(1000);
                        initXVel = velocityTracker.getXVelocity();
                        initXPos = motionEvent.getRawX() + dX;

                        if(initXPos > 0)
                            mOpenAnim.switchTo(motionEvent.getRawX() + dX);

                        break;
                    case MotionEvent.ACTION_UP:

                        float initVelocity = initXVel / (mOpenAnim.getStateValue("Start") - initXPos);
                        Log.e("Val",String.valueOf(initXVel));
                        mOpenAnim.setSolver(new SpringSolver(450, 0.92f));
                        mOpenAnim.setVelocity(initXVel);
                        if(initXPos > 350) {
                            mClickAnim.reverse();
                            mOpenAnim.reverse();
                        }
                        else{
                            mOpenAnim.start();
                        }
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
