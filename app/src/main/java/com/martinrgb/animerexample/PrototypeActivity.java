package com.martinrgb.animerexample;

import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.interpolator.AndroidNative.AccelerateDecelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.FastOutSlowInInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.LinearInterpolator;
import com.martinrgb.animer.core.util.AnUtil;
import com.martinrgb.animer.monitor.AnConfigRegistry;
import com.martinrgb.animer.monitor.AnConfigView;

public class PrototypeActivity extends AppCompatActivity {

    private SmoothCornersImage smoothCornersImage;
    private AnConfigView mAnimerConfiguratorView;


    private static final Animer.AnimerSolver solverRect = Animer.springRK4(650,45);
    private static final Animer.AnimerSolver solverScale = Animer.springRK4(400,30f);
    private static final Animer.AnimerSolver solverButtonScale = Animer.springRK4(400,30f);
    private static final Animer.AnimerSolver solverRadius = Animer.interpolatorDroid(new FastOutSlowInInterpolator(),400);
    private static final Animer.AnimerSolver solverTrans = Animer.springDroid(800,0.95f);
    private static final Animer.AnimerSolver solverAlpha = Animer.interpolatorDroid(new LinearInterpolator(),150);
    private static final Animer.AnimerSolver solverNav = Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(),150);

    private Animer mRectAnimer,mTransAnimer,mRadiusAnimer,mAlphaAnimer, mNavAnimer,mScaleAnimer,mScaleButtonAnimer;
    private boolean isShowDetail = false;

    private float initW, initH, initR, initTranslationX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_prototype);

        smoothCornersImage = findViewById(R.id.smooth_iv);
        ViewTreeObserver vto = smoothCornersImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                smoothCornersImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initTranslationX = smoothCornersImage.getTranslationX();
                initW = smoothCornersImage.getMeasuredWidth();
                initH = smoothCornersImage.getMeasuredHeight();
                initR = smoothCornersImage.getRoundRadius();
            }
        });


        getDisplayPoint();
        setAnimerSystem();

        findViewById(R.id.smooth_iv).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        if (!isShowDetail) {
//                            mScaleAnimer.setEndValue(0.95f);
//                        }
                        mScaleAnimer.setEndValue(0.95f);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:

                        mRectAnimer.setEndValue(1);
                        mTransAnimer.setEndValue(1);
                        mRadiusAnimer.setEndValue(0);
                        mAlphaAnimer.setEndValue(1);
                        mNavAnimer.setEndValue(1);
                        mScaleAnimer.setEndValue(1f);

//                        if (!isShowDetail) {
//                            mRectAnimer.setEndValue(1);
//                            mTransAnimer.setEndValue(1);
//                            mRadiusAnimer.setEndValue(0);
//                            mAlphaAnimer.setEndValue(1);
//                            mNavAnimer.setEndValue(1);
//                            mScaleAnimer.setEndValue(1f);
//                        } else {
//                            mRectAnimer.setEndValue(0);
//                            mTransAnimer.setEndValue(0);
//                            mRadiusAnimer.setEndValue(initR);
//                            mAlphaAnimer.setEndValue(0);
//                            mNavAnimer.setEndValue(0);
//                        }
                        isShowDetail = !isShowDetail;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }

                return true;
            }
        });

        findViewById(R.id.arrow_iv).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        if (isShowDetail) {
//                            mScaleAnimer.setEndValue(0.95f);
//                        }
                        mScaleButtonAnimer.setEndValue(0.95f);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        mRectAnimer.setEndValue(0);
                        mTransAnimer.setEndValue(0);
                        mRadiusAnimer.setEndValue(initR);
                        mAlphaAnimer.setEndValue(0);
                        mNavAnimer.setEndValue(0);
                        mScaleButtonAnimer.setEndValue(1);
                        //isShowDetail = !isShowDetail;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }

                return true;
            }
        });
    }

    private void setAnimerSystem() {
        mRectAnimer = new Animer();
        mRectAnimer.setSolver(solverRect);
        mRectAnimer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {

                float XVal = (float) AnUtil.mapValueFromRangeToRange(value, 0, 1, initTranslationX, 0);
                float YVal = (float) AnUtil.mapValueFromRangeToRange(value, 0, 1, 0, -918);
                float widthVal = (float) AnUtil.mapValueFromRangeToRange(value, 0, 1, initW, sWidth);
                float heightVal = (float) AnUtil.mapValueFromRangeToRange(value, 0, 1, initH, sWidth);

                float reverseHeightVal = (float) AnUtil.mapValueFromRangeToRange(value, 0, 1, (sWidth - 420) / 2, 0);

                smoothCornersImage.setRectSize(widthVal, heightVal);
                smoothCornersImage.setTranslationX(XVal);
                smoothCornersImage.setTranslationY(YVal);

                findViewById(R.id.smooth_text_iv).setTranslationY(-(heightVal - 420) / 2);
                findViewById(R.id.detail_text_iv).setTranslationY(reverseHeightVal);
                findViewById(R.id.arrow_iv).setTranslationY(reverseHeightVal);

            }
        });

        mTransAnimer = new Animer();
        mTransAnimer.setSolver(solverTrans);
        mTransAnimer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {

                float transVal = (float) AnUtil.mapValueFromRangeToRange(value, 0, 1, 0, 1170);

                findViewById(R.id.top_iv).setTranslationY(-transVal);
                findViewById(R.id.bottom_iv).setTranslationY(transVal);
            }
        });

        mRadiusAnimer = new Animer();
        mRadiusAnimer.setSolver(solverRadius);
        mRadiusAnimer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {
                smoothCornersImage.setRoundRadius(value);

            }
        });

        mScaleAnimer = new Animer();
        mScaleAnimer.setSolver(solverScale);
        mScaleAnimer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {
                smoothCornersImage.setScaleX(value);
                smoothCornersImage.setScaleY(value);
                smoothCornersImage.setAlpha(value);
            }
        });

        mScaleButtonAnimer = new Animer();
        mScaleButtonAnimer.setSolver(solverButtonScale);
        mScaleButtonAnimer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {
                findViewById(R.id.arrow_iv).setScaleX(value);
                findViewById(R.id.arrow_iv).setScaleY(value);
            }
        });

        mScaleButtonAnimer.setCurrentValue(1);
        mScaleAnimer.setCurrentValue(1);



        mAlphaAnimer = new Animer();
        mAlphaAnimer.setSolver(solverAlpha);
        mAlphaAnimer.setUpdateListener(new Animer.UpdateListener() {

            @Override
            public void onUpdate(float value, float velocity, float progress) {
                float alphaVal = (float) AnUtil.mapValueFromRangeToRange(value, 0, 1, 1, 0);
                float reverseAlpha = (float) AnUtil.mapValueFromRangeToRange(value, 0.5f, 1, 0, 1);

                findViewById(R.id.top_iv).setAlpha(alphaVal);
                findViewById(R.id.bottom_iv).setAlpha(alphaVal);
                findViewById(R.id.smooth_text_iv).setAlpha(alphaVal);
                findViewById(R.id.arrow_iv).setAlpha(reverseAlpha);
                findViewById(R.id.detail_text_iv).setAlpha(reverseAlpha);
            }
        });

        mNavAnimer = new Animer();
        mNavAnimer.setSolver(solverNav);
        mNavAnimer.setUpdateListener(new Animer.UpdateListener() {

            @Override
            public void onUpdate(float value, float velocity, float progress) {
                float transVal = (float) AnUtil.mapValueFromRangeToRange(value, 0, 1, 0, 300);
                findViewById(R.id.nav_iv).setTranslationY(transVal);
            }
        });

        mAnimerConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer("Image Rect Animation",mRectAnimer);
        AnConfigRegistry.getInstance().addAnimer("Image Scale Animation",mScaleAnimer);
        AnConfigRegistry.getInstance().addAnimer("Image Radius Animation",mRadiusAnimer);
        AnConfigRegistry.getInstance().addAnimer("Button Scale Animation",mScaleButtonAnimer);
        AnConfigRegistry.getInstance().addAnimer("List Trans Animation",mTransAnimer);
        AnConfigRegistry.getInstance().addAnimer("Total Alpha Animation",mAlphaAnimer);
        AnConfigRegistry.getInstance().addAnimer("Navigation Animation",mNavAnimer);
        mAnimerConfiguratorView.refreshAnimerConfigs();

    }

    private float sWidth, sHeight;

    private void getDisplayPoint() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        sWidth = (float) point.x;
        sHeight = (float) point.y;
    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}