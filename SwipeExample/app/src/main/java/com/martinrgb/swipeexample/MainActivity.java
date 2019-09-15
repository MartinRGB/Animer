package com.martinrgb.swipeexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.martinrgb.animation_engine.controller.AnimationProperty;
import com.martinrgb.animation_engine.controller.AnimationController;
import com.martinrgb.animation_engine.controller.AnimatorCreator;

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

//        FlingAnimation animatorCreator = (FlingAnimation) AnimatorCreator.createFlingAnimation();
//        animatorCreator.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
//            @Override
//            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
//
//                card.setTranslationY(value);
//            }
//        });
//        animatorCreator.setSpring(new SpringForce(700));
//        animatorCreator.getSpring().setStiffness(300);
//        animatorCreator.getSpring().setDampingRatio(0.6f);

//        animatorCreator.setStartVelocity(4000f);
//        animatorCreator.setStartValue(0);
//        animatorCreator.start();


//        SpringAnimation animatorCreator2 = (SpringAnimation) AnimatorCreator.createSpringAnimator();
//        animatorCreator2.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
//            @Override
//            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
//                card.setTranslationY(value);
//            }
//        });
//        animatorCreator2.setSpring(new SpringForce(700));
//        animatorCreator2.getSpring().setStiffness(300);
//        animatorCreator2.getSpring().setDampingRatio(0.6f);
//
//        animatorCreator2.setStartValue(0);
//        animatorCreator2.start();



//        Log.e("Valuessssss",String.valueOf(animatorCreator));
//        Log.e("Valuessssss2",String.valueOf(animatorCreator2));
//        Log.e("Valuessssss3",String.valueOf(AnimationProperty.ALPHA));
//        Log.e("Valuessssss4",String.valueOf(AnimationProperty.ALPHA));

//        ValueAnimator valueAnimator = new ValueAnimator();
//        valueAnimator.setTarget(card);
//        valueAnimator.setFloatValues(-200);
//        valueAnimator.setObjectValues();
//        valueAnimator.setupStartValues();
//        valueAnimator.setDuration(1000);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                card.setTranslationX((float)valueAnimator.getAnimatedValue());
//            }
//        });
//        valueAnimator.start();

//        ObjectAnimator mValueAnimator = new ObjectAnimator();
//        mValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
//        mValueAnimator.setFloatValues(0,1000);
//        mValueAnimator.setDuration(1000);
//        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                mPrevVelocity = mCurrentVelocity;
//                //mPhysicsState.updatePhysics((float)valueAnimator.getAnimatedValue(),mCurrentVelocity-mPrevVelocity);
//                mCurrentVelocity = (float) valueAnimator.getAnimatedValue();
//                Log.e("Velocity",String.valueOf(mCurrentVelocity - mPrevVelocity));
//                card.setTranslationX((float)valueAnimator.getAnimatedValue());
//
//
//
//            }
//        });
//        mValueAnimator.start();

        mOpenAnimController = new AnimationController(AnimatorCreator.createSpringAnimator(),card,AnimationProperty.TRANSLATION_Y,0,700);

        mOpenAnimController.setAnimationListener(new AnimationController.AnimationListener() {
            @Override
            public void onAnimationUpdate(float value, float velocity) {
            }

            @Override
            public void onAnimationEnd(boolean canceled, float value, float velocity) {

            }
        });



        card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float startX = 0,startY = 0,transX = 0,transY = 0;
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("TAG", "touched down");
                        mOpenAnimController.useOrigamiPOPSpring(30,10);
                        mOpenAnimController.animateTo(700);
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        transX = motionEvent.getX() - startX;
                        transY = motionEvent.getY() - startY;
                        Log.i("TAG", "touched move");
                        break;
                    case MotionEvent.ACTION_UP:
                        mOpenAnimController.useOrigamiPOPSpring(5,10);
                        mOpenAnimController.animateTo(0);
                        Log.i("TAG", "touched up");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.i("TAG", "touched cancel");
                        break;
                }

                return true;
            }
        });

//        card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(!isClicked){
//                    mOpenAnimController.setEndValue(0.4f);
//                }
//                else{
//                    mOpenAnimController.setEndValue(1);
//                }
//
//                isClicked = !isClicked;
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
