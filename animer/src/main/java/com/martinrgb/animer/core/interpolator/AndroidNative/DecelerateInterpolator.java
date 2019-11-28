package com.martinrgb.animer.core.interpolator.AndroidNative;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class DecelerateInterpolator extends AnInterpolator {



    private float mFactor = 1.0f;
    public DecelerateInterpolator() {
        initArgData(0,1,"factor",0,10);
    }

    public DecelerateInterpolator(float factor) {
        mFactor = factor;
        initArgData(0,factor,"factor",0,10);
    }


    public float getInterpolation(float input) {
        float result;
        if (mFactor == 1.0f) {
            result = (float)(1.0f - (1.0f - input) * (1.0f - input));
        } else {
            result = (float)(1.0f - Math.pow((1.0f - input), 2 * mFactor));
        }
        return result;
    }

    @Override
    public void resetArgValue(int i, float value){
        setArgValue(i,value);
        if(i == 0){
            mFactor = value;
        }
    }

}
