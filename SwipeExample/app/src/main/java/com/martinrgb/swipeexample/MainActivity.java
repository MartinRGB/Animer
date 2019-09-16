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
import com.martinrgb.animation_engine.solver.AnimationSolver;
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



        // # Animation Solver
        AnimationSolver mTimingSolver = new TimingSolver(new DecelerateInterpolator(), 500);
        // # Init the AnimController
        mOpenAnimController = new AnimationController(card,mTimingSolver,AnimationProperty.SCALE,1,0.5f);
        // # Add a state into state Machine
        mOpenAnimController.setState("Bigger",3);
        // # Animation Listener;
        mOpenAnimController.setUpdateListener(new AnimationController.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity) {
                //Log.e("Velocity",String.valueOf(velocity));
            }
        });

        card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // # Reset The Solver;
                        mOpenAnimController.setSolver(SpringSolver.createOrigamiSpring(5,2));
                        // # Start The Animation to End
                        mOpenAnimController.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        // # Reset The Solver;
                        mOpenAnimController.setSolver(SpringSolver.createOrigamiSpring(20,2));
                        // # Animate to State "Bigger"
                        mOpenAnimController.animateTo(4);
                        break;
                }
                return true;
            }
        });


//        card.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Log.i("TAG", "touched down");
//                        mOpenAnimController.setSolver(new TimingSolver(new FastOutSlowInInterpolator(),200));
//                        mOpenAnimController.start();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        Log.i("TAG", "touched move");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        mOpenAnimController.setSolver(new TimingSolver(new DecelerateInterpolator(),200));
//                        mOpenAnimController.reverse();
//                        Log.i("TAG", "touched up");
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        Log.i("TAG", "touched cancel");
//                        break;
//                }
//
//                return true;
//            }
//        });

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
