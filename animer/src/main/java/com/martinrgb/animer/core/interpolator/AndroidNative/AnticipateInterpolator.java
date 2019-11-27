package com.martinrgb.animer.core.interpolator.AndroidNative;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class AnticipateInterpolator extends AnInterpolator {



    private final float mTension;

    public AnticipateInterpolator() {
        mTension = 2.0f;
        setArg(0,2,"factor",0,10);
    }

    /**
     * @param tension Amount of anticipation. When tension equals 0.0f, there is
     *                no anticipation and the interpolator becomes a simple
     *                acceleration interpolator.
     */
    public AnticipateInterpolator(float tension) {
        mTension = tension;
        setArg(0,tension,"factor",0,10);
    }

    public float getInterpolation(float t) {
        // a(t) = t * t * ((tension + 1) * t - tension)
        return t * t * ((mTension + 1) * t - mTension);
    }

}
