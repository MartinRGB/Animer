package com.martinrgb.animer.core.interpolator.AndroidNative;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class AnticipateOvershootInterpolator extends AnInterpolator {
    private final float mTension;

    public AnticipateOvershootInterpolator() {
        mTension = 2.0f * 1.5f;
        setArg(0,2,"factor",0,10);
    }

    public AnticipateOvershootInterpolator(float tension) {
        mTension = tension * 1.5f;
        setArg(0,tension,"factor",0,10);
    }

    public AnticipateOvershootInterpolator(float tension, float extraTension) {
        mTension = tension * extraTension;
        setArg(0,tension,"factor",0,10);
    }

    private static float a(float t, float s) {
        return t * t * ((s + 1) * t - s);
    }

    private static float o(float t, float s) {
        return t * t * ((s + 1) * t + s);
    }

    public float getInterpolation(float t) {
        // a(t, s) = t * t * ((s + 1) * t - s)
        // o(t, s) = t * t * ((s + 1) * t + s)
        // f(t) = 0.5 * a(t * 2, tension * extraTension), when t < 0.5
        // f(t) = 0.5 * (o(t * 2 - 2, tension * extraTension) + 2), when t <= 1.0
        if (t < 0.5f) return 0.5f * a(t * 2.0f, mTension);
        else return 0.5f * (o(t * 2.0f - 2.0f, mTension) + 2.0f);
    }

}
