package com.martinrgb.animation_engine.solver;

import android.util.Log;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;

import com.martinrgb.animation_engine.converter.DHOConverter;
import com.martinrgb.animation_engine.converter.OrigamiPOPConverter;
import com.martinrgb.animation_engine.converter.RK4Converter;
import com.martinrgb.animation_engine.converter.UIViewSpringConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FlingSolver extends AnimationSolver {

    private float mStartVelocity = 1000.f,mFriction = 0.5f;
    private Object flingSolver;

    public FlingSolver(float velocity,float friction) {
        setStartVelocity(velocity);
        setFriction(friction);
        flingSolver= this;
    }

    // ############################################
    // Getter & Setter
    // ############################################

    public float getVelocity() {
        return mStartVelocity;
    }

    public float getFriction() {
        return mFriction;
    }

    public void setStartVelocity(float velocity){
        mStartVelocity = velocity;
        if(mListener !=null){
            mListener.onSolverUpdate(mStartVelocity,mFriction);
        }
    }

    public void setFriction(float friction){
        mFriction = friction;
        if(mListener !=null){
            mListener.onSolverUpdate(mStartVelocity,mFriction);
        }
    }

    // ############################################
    // Override
    // ############################################

    @Override
    public void setSolver(AnimationSolver solver){
        flingSolver = solver;
    }

    @Override
    public Object getSolver(){
        return flingSolver;
    }

    @Override
    public int getSolverMode() {
        return 0;
    }

}




