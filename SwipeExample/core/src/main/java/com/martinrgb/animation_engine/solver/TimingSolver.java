package com.martinrgb.animation_engine.solver;

import android.animation.TimeInterpolator;
import android.graphics.Interpolator;
import android.util.Log;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.martinrgb.animation_engine.converter.DHOConverter;
import com.martinrgb.animation_engine.converter.OrigamiPOPConverter;
import com.martinrgb.animation_engine.converter.RK4Converter;
import com.martinrgb.animation_engine.converter.UIViewSpringConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TimingSolver extends AnimationSolver {

    private TimeInterpolator mInterpolator;
    private long mDuration;
    private Object timingSolver;

    public TimingSolver(TimeInterpolator interpolator, long duration) {
        setInerpolator(interpolator);
        setDuration(duration);
        timingSolver= this;
    }



    // ############################################
    // Getter & Setter
    // ############################################

    public TimeInterpolator getInterpolator() {
        return mInterpolator;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setInerpolator(TimeInterpolator interpolator){
        mInterpolator = interpolator;
        if(mListener !=null){
            mListener.onSolverUpdate(mInterpolator,mDuration);
        }
    }

    public void setDuration(long duration){
        mDuration = duration;
        if(mListener !=null){
            mListener.onSolverUpdate(mInterpolator,mDuration);
        }
    }

    // ############################################
    // Override
    // ############################################

    @Override
    public void setSolver(AnimationSolver solver){
        timingSolver = solver;
    }

    @Override
    public Object getSolver(){
        return timingSolver;
    }

    @Override
    public int getSolverMode() {
        return 2;
    }
}




