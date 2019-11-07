package com.martinrgb.animer.interpolator;

import android.view.animation.Interpolator;

public class CustomMocosSpringInterpolator implements Interpolator{

    private final double mGamma, mVDiv2;
    private final boolean mOscilative;
    private final double mEps;

    private double mA, mB;
    private double mDuration;

    public CustomMocosSpringInterpolator(double tension, double damping) {
        this(tension, damping, 0.001);
        this.setInitialVelocity(20.);
    }

    public CustomMocosSpringInterpolator(double tension, double damping, double velocity) {
        //mEps = eps;
        mEps = 0.001;
        mOscilative = (4 * tension - damping * damping > 0);
        if (mOscilative) {
            mGamma = Math.sqrt(4 * tension - damping * damping) / 2;
            mVDiv2 = damping / 2;
        } else {
            mGamma = Math.sqrt(damping * damping - 4 * tension) / 2;
            mVDiv2 = damping / 2;
        }
        this.setInitialVelocity(velocity);
    }

    public void setInitialVelocity(double v0) {
        if (mOscilative) {
            mB = Math.atan(-mGamma / (v0 - mVDiv2));
            mA = -1 / Math.sin(mB);
            mDuration = Math.log(Math.abs(mA) / mEps) / mVDiv2;
        } else {
            mA = (v0 - (mGamma + mVDiv2)) / (2 * mGamma);
            mB = -1 - mA;
            mDuration = Math.log(Math.abs(mA) / mEps) / (mVDiv2 - mGamma);
        }
    }

    public double getDesiredDuration() {
        return mDuration;
    }

    @Override
    public float getInterpolation(float input) {
        if (input >= 1) {
            return 1;
        }
        double t = input * mDuration;
        return (float) (mOscilative ?
                (mA * Math.exp(-mVDiv2 * t) * Math.sin(mGamma * t + mB) + 1) :
                (mA * Math.exp((mGamma - mVDiv2) * t) + mB * Math.exp(-(mGamma + mVDiv2) * t) + 1));
    }
}