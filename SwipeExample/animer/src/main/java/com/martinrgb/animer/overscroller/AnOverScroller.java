package com.martinrgb.animer.overscroller;

//package com.example.martinrgb.scrollview_test_android.scrollview;


import android.content.Context;
import android.util.Log;
import android.view.animation.Interpolator;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class AnOverScroller {

    private FlingAnimation flingAnimation;
    private SpringAnimation springAnimation;
    private FloatValueHolder scrollY;
    private FloatValueHolder speedY;
    private boolean isSpringBackLocked = false;
    private boolean isDyanmicDamping = false;

    public AnOverScroller(Context context) {
        this(context, null);
    }

    public AnOverScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public AnOverScroller(Context context, Interpolator interpolator,
                          float bounceCoefficientX, float bounceCoefficientY) {
        this(context, interpolator, true);
    }

    public AnOverScroller(Context context, Interpolator interpolator,
                          float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) {
        this(context, interpolator, flywheel);
    }

    public AnOverScroller(Context context, Interpolator interpolator, boolean flywheel) {
        scrollY = new FloatValueHolder();
        speedY = new FloatValueHolder();
        scrollY.setValue(0);
        flingAnimation = new FlingAnimation(scrollY);
        flingAnimation.setFriction(0.5f);
        flingAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                speedY.setValue(velocity);
            }
        });

        springAnimation = new SpringAnimation(scrollY);
        springAnimation.setSpring(new SpringForce());
        springAnimation.getSpring().setDampingRatio(0.99f);
        springAnimation.getSpring().setStiffness(100);
        springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                if(!getSpringBackLockedState()){
                    setSpringBackLockedState(true);
                }
            }
        });
        springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                setSpringBackLockedState(false);
                speedY.setValue(0);
            }
        });

    }

    public final int getCurrX() {
        return 0;
    }

    public final int getCurrY() {
        return (int) Math.round(scrollY.getValue());
    }

    public float getCurrVelocityY() {
        return (float) speedY.getValue();
    }


    public boolean computeScrollOffset() {
        return (flingAnimation.isRunning() || springAnimation.isRunning());
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        scrollY.setValue(startY);
    }

    public boolean springBack(int startX, int startY, int velocityX,int velocityY,int minX, int maxX, int minY, int maxY) {

        if (startY > maxY || startY < minY) {

            flingAnimation.cancel();
            scrollY.setValue(startY);

            if (!getSpringBackLockedState()) {

                springAnimation.setStartValue(startY);
                springAnimation.setStartVelocity(speedY.getValue());

                if (startY > maxY) {
                    springAnimation.getSpring().setFinalPosition(maxY);
                } else if (startY < minY) {
                    springAnimation.getSpring().setFinalPosition(minY);
                }

                springAnimation.start();
            }

            return true;
        }
        return true;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY, int overX, int overY) {
        Log.e("Velocity",String.valueOf(velocityY));


        scrollY.setValue(startY);
        flingAnimation.setStartVelocity(velocityY);

        if(getDynamicFlingFrictionState()){
            float dynamicDamping = (float) mapValueFromRangeToRange(Math.abs(velocityY),0,24000,1.35,0.5);
            flingAnimation.setFriction(dynamicDamping);
        }
        flingAnimation.start();
    }

    private static double mapValueFromRangeToRange(
            double value,
            double fromLow,
            double fromHigh,
            double toLow,
            double toHigh) {
        double fromRangeSize = fromHigh - fromLow;
        double toRangeSize = toHigh - toLow;
        double valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }

    public void setDynamicFlingFrictionState(boolean dynamicDampingState){
        isDyanmicDamping = dynamicDampingState;
    }

    public boolean getDynamicFlingFrictionState(){
        return isDyanmicDamping;
    }

    public void setSpringBackLockedState(boolean springBackLocked) {
        isSpringBackLocked = springBackLocked;
    }

    public boolean getSpringBackLockedState() {
        return isSpringBackLocked;
    }

    public final boolean isFinished() {
        return !(springAnimation.isRunning() || flingAnimation.isRunning());
    }

    public void abortAnimation() {
        springAnimation.cancel();
        flingAnimation.cancel();
    }
}
