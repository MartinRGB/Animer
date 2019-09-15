package com.martinrgb.swipeexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.martinrgb.animation_engine.controller.AnimationProperty;
import com.martinrgb.animation_engine.controller.AnimationController;
import com.martinrgb.animation_engine.solver.FlingSolver;
import com.martinrgb.animation_engine.solver.SpringSolver;
import com.martinrgb.animation_engine.solver.TimingSolver;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout card;
    private ImageView card_mask;
    private AnimationController mOpenAnimController;
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


        //SpringSolver springSolver = new SpringSolver(200,30);

        mOpenAnimController = new AnimationController(new TimingSolver(new FastOutSlowInInterpolator(),500),card,AnimationProperty.TRANSLATION_Y,0,700);

        mOpenAnimController.setAnimationListener(new AnimationController.AnimationListener() {
            @Override
            public void onAnimationUpdate(float value, float velocity) {

                Log.e("Canceled",String.valueOf(velocity));
            }

            @Override
            public void onAnimationEnd(boolean canceled, float value, float velocity) {

            }
        });

        

        card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("TAG", "touched down");
                        mOpenAnimController.setSolver(new FlingSolver(2500,0.5f));
                        mOpenAnimController.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("TAG", "touched move");
                        break;
                    case MotionEvent.ACTION_UP:
                        mOpenAnimController.setSolver(SpringSolver.createOrigamiSpring(40f,5f));
                        mOpenAnimController.animateToState("Start");
                        Log.i("TAG", "touched up");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.i("TAG", "touched cancel");
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
