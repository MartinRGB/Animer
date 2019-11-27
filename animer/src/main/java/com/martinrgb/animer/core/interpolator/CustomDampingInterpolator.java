package com.martinrgb.animer.core.interpolator;

import android.view.animation.Interpolator;

public class CustomDampingInterpolator extends AnInterpolator{

    //Parameters
    private static final float maxStifness = 50.f;
    private static final float maxFrictionMultipler = 1.f;
    private float mTension = 0.f;
    private float mFriction = 0.f;

    //Curve Position parameters(No Adjust)
    private static final float amplitude = 1.f;
    private static final float phase = 0.f;

    //Original Scale parameters(Better No Adjust)
    private static final float originalStiffness = 12.f;
    private static final float originalFrictionMultipler = 0.3f;
    private static final float mass = 0.058f;

    //Internal parameters
    private float pulsation;
    private float friction;

    private void computePulsation() {
        this.pulsation = (float) Math.sqrt((originalStiffness + mTension) / mass);
    }

    private void computeFriction() {
        this.friction = (originalFrictionMultipler + mFriction) * pulsation;
    }

    private void computeInternalParameters() {
        // never call computeFriction() without
        // updating the pulsation
        computePulsation();
        computeFriction();
    }

    public CustomDampingInterpolator( float tension, float friction) {

        //this.mTension = Math.min(Math.max(tension,0.f),100.f) * (maxStifness- originalStiffness)/100.f;
        //this.mFriction = Math.min(Math.max(friction,0.f),100.f) * (maxFrictionMultipler - originalFrictionMultipler)/100.f;
        this.mFriction = friction;
        this.mTension = tension;

        computeInternalParameters();

        setArg(0,tension,"tension",0,100);
        setArg(1,friction,"friction",0,100);
    }

    public CustomDampingInterpolator() {
        computeInternalParameters();
    }

    @Override
    public float getInterpolation(float ratio) {
        if (ratio == 0.0f || ratio == 1.0f)
            return ratio;
        else {
            float value = amplitude * (float) Math.exp(-friction * ratio) *
                    (float) Math.cos(pulsation * ratio + phase) ;
            return -value+1;
        }
    }
}