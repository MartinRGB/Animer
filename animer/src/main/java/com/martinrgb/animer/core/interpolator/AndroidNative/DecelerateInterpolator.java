package com.martinrgb.animer.core.interpolator.AndroidNative;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class DecelerateInterpolator extends AnInterpolator {



    private float mFactor = 1.0f;
    public DecelerateInterpolator() {
        setArg(0,1,"factor",0,10);
    }

    public DecelerateInterpolator(float factor) {
        mFactor = factor;
        setArg(0,factor,"factor",0,10);
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

}
