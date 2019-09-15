package com.martinrgb.animation_engine.solver;

import android.util.Log;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;

import com.martinrgb.animation_engine.controller.AnimationController;
import com.martinrgb.animation_engine.converter.DHOConverter;
import com.martinrgb.animation_engine.converter.OrigamiPOPConverter;
import com.martinrgb.animation_engine.converter.RK4Converter;
import com.martinrgb.animation_engine.converter.UIViewSpringConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class AnimationSolver extends  Object{

    public AnimationSolver(){}
    public abstract void setSolver(AnimationSolver solver);
    public abstract Object getSolver();

    public SolverListener mListener;

    public void setSolverListener(SolverListener listener) {
        mListener = listener;
    }

    public interface SolverListener {
        void onSolverUpdate(Object arg1, Object arg2);
    }
}




