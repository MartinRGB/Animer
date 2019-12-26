package com.martinrgb.animer.core.interpolator;

import android.animation.TimeInterpolator;
import android.util.Log;


public class AndroidSpringInterpolator2 implements TimeInterpolator {

    //Parameters
    private float mStiffness;
    private float mDampingRatio;
    private float mVelocity;
    private float mDuration;
    private boolean canGetDuration = true;

    public AndroidSpringInterpolator2(float stiffness, float dampingratio,float velocity) {
        this.mStiffness = stiffness;
        this.mDampingRatio = dampingratio;
        this.mVelocity = velocity/1000.f;
    }

    @Override
    public float getInterpolation(float ratio) {

        if(canGetDuration){
            getDuration(ratio);
        }

        float starVal = 0;
        float endVal = 1;

        float mDeltaT = ratio * mDuration;
        float lastDisplacement = ratio - endVal;

        float mNaturalFreq = (float) Math.sqrt(mStiffness);
        float mDampedFreq = (float)(mNaturalFreq*Math.sqrt(1.0 - mDampingRatio* mDampingRatio));

        float cosCoeff = lastDisplacement;
        float sinCoeff = (float) (1.0 / mDampedFreq * (mDampingRatio * mNaturalFreq * lastDisplacement + mVelocity));
        float displacement = (float) (Math.pow(Math.E,-mDampingRatio * mNaturalFreq * mDeltaT) * (cosCoeff * Math.cos(mDampedFreq * mDeltaT) + sinCoeff * Math.sin(mDampedFreq * mDeltaT)));
        mVelocity = (float) (displacement * (-mNaturalFreq) * mDampingRatio
                + Math.pow(Math.E, -mDampingRatio * mNaturalFreq * mDeltaT)
                * (-mDampedFreq * cosCoeff * Math.sin(mDampedFreq * mDeltaT)
                + mDampedFreq * sinCoeff * Math.cos(mDampedFreq * mDeltaT)));
        float mValue = displacement + endVal;
        Log.e("mValue",String.valueOf(mValue));

        return mValue;
    }

    public void setVelocityInSeconds(float velocity){
        mVelocity = velocity/1000.f;
    }

    public void setDampingRatio(float dampingRatio){
        mDampingRatio = dampingRatio;
    }

    private void getDuration(float ratio){
        if(ratio !=0){
            float oneFrameRatio = ratio - 0;
            float timeInMs = (1.f/oneFrameRatio)*(1000.f/60.f);
            mDuration = timeInMs/1000.f;
            canGetDuration = false;
        }
    }
}
