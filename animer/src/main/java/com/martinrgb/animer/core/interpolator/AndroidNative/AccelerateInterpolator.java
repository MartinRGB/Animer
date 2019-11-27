package com.martinrgb.animer.core.interpolator.AndroidNative;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class AccelerateInterpolator extends AnInterpolator {
    private final float mFactor;
    private final double mDoubleFactor;

    public AccelerateInterpolator() {
        mFactor = 1.0f;
        mDoubleFactor = 2.0;
        setArg(0,1,"factor",0,10);
    }

    public AccelerateInterpolator(float factor) {
        mFactor = factor;
        mDoubleFactor = 2 * mFactor;
        setArg(0,factor,"factor",0,10);
    }

    public float getInterpolation(float input) {
        if (mFactor == 1.0f) {
            return input * input;
        } else {
            return (float)Math.pow(input, mDoubleFactor);
        }
    }

}
