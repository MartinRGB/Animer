package com.martinrgb.animer.core.interpolator.AndroidNative;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class AccelerateInterpolator extends AnInterpolator {
    private float mFactor;
    private double mDoubleFactor;

    public AccelerateInterpolator() {
        mFactor = 1.0f;
        mDoubleFactor = 2.0;
        initArgData(0,1,"factor",0,10);
    }

    public AccelerateInterpolator(float factor) {
        mFactor = factor;
        mDoubleFactor = 2 * mFactor;
        initArgData(0,factor,"factor",0,10);
    }

    public float getInterpolation(float input) {
        if (mFactor == 1.0f) {
            return input * input;
        } else {
            return (float)Math.pow(input, mDoubleFactor);
        }
    }

    @Override
    public void resetArgValue(int i, float value){
        setArgValue(i,value);
        if(i == 0){
            mFactor = value;
        }
    }

}
