package com.martinrgb.swipeexample;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.martinrgb.animation_engine.controller.AnimationProperty;
import com.martinrgb.animation_engine.controller.AnimationController;

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

        //mOpenAnimController = new AnimationController(AnimationController.createSpringSolver(400,0.25f),card,AnimationProperty.TRANSLATION_Y, 0,700);

        mOpenAnimController = new AnimationController(AnimationController.createSpringSolver(400,0.25f),card,AnimationProperty.TRANSLATION_Y, 0,700);

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
//                        mOpenAnimController.useOrigamiPOPSpring(30,10);
                        mOpenAnimController.getSpringSolver().useAndroidSpring(1500,0.5f);
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
//                        mOpenAnimController.useOrigamiPOPSpring(5,10);
                        mOpenAnimController.getSpringSolver().useOrigamiPOPSpring(30,10);
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
