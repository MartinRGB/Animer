package com.martinrgb.animer.core.interpolator.AndroidNative;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class CycleInterpolator extends AnInterpolator {

    public CycleInterpolator(float cycles) {
        mCycles = cycles;
        setArg(0,cycles,"factor",0,10);
    }

    public float getInterpolation(float input) {
        return (float)(Math.sin(2 * mCycles * Math.PI * input));
    }

    private float mCycles;

}
