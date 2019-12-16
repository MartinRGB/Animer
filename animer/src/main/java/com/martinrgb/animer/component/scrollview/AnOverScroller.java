package com.martinrgb.animer.component.scrollview;

import android.content.Context;
import android.util.Log;
import android.view.animation.Interpolator;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.math.calculator.FlingCalculator;

public class AnOverScroller {

    private FlingAnimation flingAnimation;
    private SpringAnimation springAnimation;
    private Animer flingAnimer;
    private Animer springAnimer;
    private FloatValueHolder scrollValue;
    private FloatValueHolder scrollSpeed;
    private boolean isSpringBackLocked = false;
    private boolean isDyanmicDamping = false;
    private boolean isVertical = true;
    private boolean isFixedScroll = false;
    private float fixedCellWidth = 0;

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

        springAnimer = new Animer();
        springAnimer.setSolver(Animer.springDroid(150,0.99f));

        flingAnimer = new Animer();
        flingAnimer.setSolver(Animer.flingDroid(4000,0.8f));

        scrollValue = new FloatValueHolder();
        scrollSpeed = new FloatValueHolder();
        scrollValue.setValue(0);
        flingAnimation = new FlingAnimation(scrollValue);
        flingAnimation.setFriction((float)flingAnimer.getArgument2());
        flingAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                scrollSpeed.setValue(velocity);
            }
        });

        springAnimation = new SpringAnimation(scrollValue);
        springAnimation.setSpring(new SpringForce());
        springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
        springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());
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
                scrollSpeed.setValue(0);
            }
        });

    }

    public final int getCurrX() {
        if(ScrollerisVertScroll()){
            return 0;
        }
        else {
            return Math.round(scrollValue.getValue());
        }
    }

    public final int getCurrY() {
        if(ScrollerisVertScroll()){
            return Math.round(scrollValue.getValue());
        }
        else {
            return 0;
        }

    }

    public float getCurrVelocityY() {
        if(ScrollerisVertScroll()){
            return (float) scrollSpeed.getValue();
        }
        else {
            return 0;
        }
    }

    public float getCurrVelocityX() {
        if(ScrollerisVertScroll()){
            return 0;
        }
        else {
            return (float) scrollSpeed.getValue();
        }
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        if(ScrollerisVertScroll()){
            scrollValue.setValue(startY);
        }
        else {
            scrollValue.setValue(startX);
        }
    }

    public boolean springBack(int startX, int startY, int velocityX,int velocityY,int minX, int maxX, int minY, int maxY) {

        if(ScrollerisVertScroll()){

            if (startY > maxY || startY < minY) {

                flingAnimation.cancel();


                if (!getSpringBackLockedState()) {
                    scrollValue.setValue(startY);
                    springAnimation.setStartValue(startY);
                    springAnimation.setStartVelocity(scrollSpeed.getValue());

                    springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
                    springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

                    if (startY > maxY) {
                        springAnimation.getSpring().setFinalPosition(maxY);
                    } else if (startY < minY) {
                        springAnimation.getSpring().setFinalPosition(minY);
                    }
                    springAnimation.start();
                }

                return true;
            }

        }
        else {
            if (startX > maxX || startX < minX) {

                flingAnimation.cancel();


                if (!getSpringBackLockedState()) {
                    scrollValue.setValue(startX);
                    springAnimation.setStartValue(startX);
                    springAnimation.setStartVelocity(scrollSpeed.getValue());

                    springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
                    springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

                    if (startX > maxX) {
                        springAnimation.getSpring().setFinalPosition(maxX);
                    } else if (startX < minX) {
                        springAnimation.getSpring().setFinalPosition(minX);
                    }
                    springAnimation.start();
                }

                return true;
            }
        }

        return true;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY, int overX, int overY) {


        // TODO:springBack 机制
        if(isFixedScroll()){
            FlingCalculator flingCalculator = new FlingCalculator(velocityX,(float)flingAnimer.getArgument2());
            float flingTransition = flingCalculator.getTransiton();


            if(ScrollerisVertScroll()){
                springAnimation.setStartVelocity(velocityY);
                scrollValue.setValue(startY);
                springAnimation.setStartValue(startY);
            }
            else {
                springAnimation.setStartVelocity(velocityX);
                scrollValue.setValue(startX);
                springAnimation.setStartValue(startX);
            }

            springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
            springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

            if(ScrollerisVertScroll()){

                float roundValue = Math.round(((startY + flingCalculator.getTransiton())/fixedCellWidth))*fixedCellWidth;
                springAnimation.getSpring().setFinalPosition(roundValue);
            }
            else {
                float roundValue = Math.round(((startX + flingCalculator.getTransiton())/fixedCellWidth))*fixedCellWidth;
                springAnimation.getSpring().setFinalPosition(roundValue);
            }
            springAnimation.start();

        }
        else {
            if(ScrollerisVertScroll()){
                flingAnimation.setStartVelocity(velocityY);
                scrollValue.setValue(startY);
            }
            else {
                flingAnimation.setStartVelocity(velocityX);
                scrollValue.setValue(startX);
            }

            if(getDynamicFlingFrictionState()){
                float dynamicDamping = (ScrollerisVertScroll())? (float) mapValueFromRangeToRange(Math.abs(velocityY),0,24000,1.35,0.5) :  (float) mapValueFromRangeToRange(Math.abs(velocityX),0,24000,1.35,0.5);
                flingAnimation.setFriction(dynamicDamping);
            }
            else {
                flingAnimation.setFriction((float)flingAnimer.getArgument2());
            }
            flingAnimation.start();
        }





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

    public void setFixedScroll(boolean fixedScroll,float cellWidth){
        isFixedScroll = fixedScroll;
        fixedCellWidth = cellWidth;
    }
    private boolean isFixedScroll(){
        return  isFixedScroll;
    }

    private void setSpringBackLockedState(boolean springBackLocked) {
        isSpringBackLocked = springBackLocked;
    }

    private boolean getSpringBackLockedState() {
        return isSpringBackLocked;
    }

    public boolean computeScrollOffset() {
        return (flingAnimation.isRunning() || springAnimation.isRunning());
    }

    public final boolean isFinished() {
        return !(springAnimation.isRunning() || flingAnimation.isRunning());
    }

    public void abortAnimation() {
        springAnimation.cancel();
        flingAnimation.cancel();
    }

    public Animer getSpringAnimer(){
        return springAnimer;
    }
    public Animer getFlingAnimer(){
        return flingAnimer;
    }
    public void setVertScroll(boolean is_vertical) {
        this.isVertical = is_vertical;
    }
    private boolean ScrollerisVertScroll(){
        return this.isVertical;
    }
}
