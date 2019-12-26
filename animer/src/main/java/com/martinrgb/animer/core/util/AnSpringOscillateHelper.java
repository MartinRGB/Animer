package com.martinrgb.animer.core.util;

import androidx.dynamicanimation.animation.SpringAnimation;
import android.os.SystemClock;


//limit SpringAnimation's Oscillation time,rapid cancel the animation 
public class AnSpringOscillateHelper {

    private float mTimer = 0;
    private int mOscillateCounter = 0;
    private int mLimitedTimes = 2;
    private boolean mShouldReset = true;

    public AnSpringOscillateHelper(){
    }

    public AnSpringOscillateHelper(int times){
        setLimitedTimes(times);
    }

    public void setLimitedTimes(int times) {
        this.mLimitedTimes = times;
    }

    public void observe(SpringAnimation springAnimation,float currentValue,float startValue,float endValue){
        if(mShouldReset){
            reset();
            mShouldReset = false;
        }

        float finalPos = springAnimation.getSpring().getFinalPosition();
        // from -> to
        if(finalPos == endValue){

            if(mOscillateCounter %2==0 && currentValue < finalPos){
                mOscillateCounter++;
            }
            if(mOscillateCounter %2!=0 && currentValue > finalPos){
                mOscillateCounter++;
            }

            if((mOscillateCounter +1)/2 == mLimitedTimes && currentValue > finalPos){
                springAnimation.cancel();
                mShouldReset = true;
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

            if((mOscillateCounter +1)/2 == mLimitedTimes && currentValue < finalPos){
                springAnimation.cancel();
                mShouldReset = true;
            }
        }
    }

    public void reset(){
        mTimer = SystemClock.elapsedRealtime();
        mOscillateCounter = 0;
    }

    public float getSpringTime(){
        return SystemClock.elapsedRealtime() - mTimer;
    }


}
