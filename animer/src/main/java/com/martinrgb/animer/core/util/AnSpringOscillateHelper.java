package com.martinrgb.animer.core.util;

import android.os.SystemClock;
import androidx.dynamicanimation.animation.SpringAnimation;

//Examples
//
//final AnSpringOscillateHelper anSpringOscillateHelper = new AnSpringOscillateHelper(AnSpringOscillateHelper.TIME,500);
//final AnSpringOscillateHelper anSpringOscillateHelper = new AnSpringOscillateHelper(AnSpringOscillateHelper.COUNT,4);
//
//springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
//    @Override
//    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
//        anSpringOscillateHelper.observe(springAnimation_2,velocity,value,startValue,endValue);
//    }
//});
//
//springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
//    @Override
//    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
//        anSpringOscillateHelper.reset();
//    }
//});

// for rapid stop spring animation
public class AnSpringOscillateHelper {

    public abstract static class OscillateLimitedMode{ }
    public static final OscillateLimitedMode TIME = new OscillateLimitedMode() {};
    public static final OscillateLimitedMode COUNT = new OscillateLimitedMode() {};
    private OscillateLimitedMode mLimitedMode;

    private int mOscillateCounter = 0;
    private int mLimitedCounts ;

    private long mOscillateTimer = 0;
    private long mLimitedTime ;

    private boolean mShouldTriggerOnce = true;

    private float prevStiffness,prevDampingRatio;
    private static final float ACCELERATION_STIFFNESS = 3000f;
    private static final float ACCELERATION_DAMPINGRATIO = 0.99f;

    public AnSpringOscillateHelper(OscillateLimitedMode oscillateLimitedMode,int value){
        if(oscillateLimitedMode == TIME){
            setLimitedMode(TIME);
            setLimitedTime(value);
        }
        else if(oscillateLimitedMode == COUNT){
            setLimitedMode(COUNT);
            setLimitedCounts(value);
        }
    }

    public void observe(SpringAnimation springAnimation,float currentVelocity,float currentValue,float startValue,float endValue){

        if(mShouldTriggerOnce){
            mOscillateTimer = SystemClock.elapsedRealtime();
            resetAccelerationAnimation(springAnimation);
            mShouldTriggerOnce = false;
        }


        if(getLimitedMode() == TIME){
            float animationElapsedTime =  SystemClock.elapsedRealtime() - mOscillateTimer;
            if(animationElapsedTime > getLimitedTime()){
                springAnimation.getSpring().setStiffness(ACCELERATION_STIFFNESS);
                springAnimation.getSpring().setDampingRatio(ACCELERATION_DAMPINGRATIO);
            }
        }


        if(getLimitedMode() == COUNT){
            float finalPos = springAnimation.getSpring().getFinalPosition();
            // from -> to
            if(finalPos == endValue){

                if(mOscillateCounter %2==0 && currentValue < finalPos){
                    mOscillateCounter++;
                }
                if(mOscillateCounter %2!=0 && currentValue > finalPos){
                    mOscillateCounter++;
                }

                if((mOscillateCounter +1)/2 == getLimitedCounts() && currentValue > finalPos){
                    springAnimation.getSpring().setStiffness(ACCELERATION_STIFFNESS);
                    springAnimation.getSpring().setDampingRatio(ACCELERATION_DAMPINGRATIO);
                }
            }
            // to -> from
            if(finalPos == startValue){

                if(mOscillateCounter %2==0 && currentValue > finalPos){
                    mOscillateCounter++;
                }
                if(mOscillateCounter %2!=0 && currentValue < finalPos){
                    mOscillateCounter++;
                }

                if((mOscillateCounter +1)/2 == getLimitedCounts() && currentValue < finalPos){
                    springAnimation.getSpring().setStiffness(ACCELERATION_STIFFNESS);
                    springAnimation.getSpring().setDampingRatio(ACCELERATION_DAMPINGRATIO);
                }
            }
        }

    }

    public void reset(){
        mOscillateCounter = 0;
        mShouldTriggerOnce = true;
    }

    private void resetAccelerationAnimation(SpringAnimation springAnimation){
        if(springAnimation.getSpring().getStiffness() == ACCELERATION_STIFFNESS){
            springAnimation.getSpring().setStiffness(prevStiffness);
        }
        else{
            prevStiffness = springAnimation.getSpring().getStiffness();
        }

        if(springAnimation.getSpring().getDampingRatio() == ACCELERATION_DAMPINGRATIO){
            springAnimation.getSpring().setDampingRatio(prevDampingRatio);
        }
        else{
            prevDampingRatio = springAnimation.getSpring().getDampingRatio();
        }
    }

    // getter & setter
    public void setLimitedCounts(int counts) {
        this.mLimitedCounts = counts;
    }

    public float getLimitedCounts(){return this.mLimitedCounts; }

    public void setLimitedTime(long time) {
        this.mLimitedTime = time;
    }

    public long getLimitedTime(){return this.mLimitedTime; }

    private void setLimitedMode(OscillateLimitedMode oscillateLimitMode) {
        this.mLimitedMode = oscillateLimitMode;
    }
    private OscillateLimitedMode getLimitedMode() {
        return this.mLimitedMode;
    }

}
