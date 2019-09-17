package com.martinrgb.swipeexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.martinrgb.animer.core.Animer;
import com.martinrgb.animer.core.solver.SpringSolver;
import com.martinrgb.animer.core.solver.TimingSolver;
import com.martinrgb.animer.core.util.AnimerUtil;


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


        mOpenAnim = new Animer(page2,new SpringSolver(450,0.92f), Animer.TRANSLATION_X,1080,0f);
        mOpenAnim.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity,float progress) {
                float alphaValue = (float) AnimerUtil.mapClampedValueFromRangeToRange(progress,0,1,0,0.5f);
                float transValue = (float) AnimerUtil.mapClampedValueFromRangeToRange(progress,0,1,0,-200);
                findViewById(R.id.page_mask).setAlpha(alphaValue);
                findViewById(R.id.page_1).setTranslationX(transValue);

                if(progress > 0.5){
                    float bottomTrans = (float) AnimerUtil.mapClampedValueFromRangeToRange(progress,0.5,1,240,0);
                    findViewById(R.id.page_2_bottom).setTranslationY(bottomTrans);
                }
            }
        });

        mClickAnim = new Animer(card_mask, SpringSolver.createOrigamiSpring(10,12), Animer.ALPHA,0,0.3f);
        mClickAnim.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity,float progress) {
                float scaleValue = (float) AnimerUtil.mapValueFromRangeToRange(progress,0,1,1,1.03);
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

        mOpenAnim.setActionerAndListener(drag_area, new Animer.ActionTouchListener() {
            float dX = 0,initXPos;
            @Override
            public void onDown(View view, MotionEvent event) {
                mOpenAnim.setSolver(new TimingSolver(new LinearInterpolator(),0));
                dX = view.getX() - event.getRawX();
            }
            @Override
            public void onMove(View view, MotionEvent event,float velocityX,float velocityY) {
                initXPos = event.getRawX() + dX;
                if(initXPos > 0)
                    mOpenAnim.switchTo(event.getRawX() + dX);
            }
            @Override
            public void onUp(View view, MotionEvent event) {
                mOpenAnim.setSolver(new SpringSolver(450, 0.92f));
                if(initXPos > 350) {
                    mClickAnim.reverse();
                    mOpenAnim.reverse();
                }
                else{
                    mOpenAnim.start();
                }
            }
            @Override
            public void onCancel(View view, MotionEvent event) {
                mOpenAnim.setSolver(new SpringSolver(450, 0.92f));
                if(initXPos > 350) {
                    mClickAnim.reverse();
                    mOpenAnim.reverse();
                }
                else{
                    mOpenAnim.start();
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
