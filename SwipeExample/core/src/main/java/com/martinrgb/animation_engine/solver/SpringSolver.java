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

public class SpringSolver extends AnimationSolver {

    private float mStiffness = 300.f,mDampingRatio = 0.6f;
    private Object springSolver;

    public SpringSolver() {
        springSolver= this;
    }

    public SpringSolver(float stiffness,float dampingratio) {
        setStiffness(stiffness);
        setDampingRatio(dampingratio);
        springSolver= this;
    }

    // ############################################
    // Spring Animation Converter
    // ############################################

    public void useAndroidSpring(float stiffness,float dampingratio){
        setStiffness(stiffness);
        setDampingRatio(dampingratio);
    }

    public void useRK4Spring(float tension,float friction){
        RK4Converter rk4Converter = new RK4Converter(tension,friction);
        setStiffness(rk4Converter.getStiffness());
        setDampingRatio(rk4Converter.getDampingRatio());
    }

    public void useDHOSpring(float stiffness,float damping){
        DHOConverter dhoConverter = new DHOConverter(stiffness,damping);
        setStiffness(dhoConverter.getStiffness());
        setDampingRatio(dhoConverter.getDampingRatio());
    }

    public void useOrigamiPOPSpring(float bounciness,float speed){
        OrigamiPOPConverter origamiPOPConverter = new OrigamiPOPConverter(bounciness,speed);
        setStiffness(origamiPOPConverter.getStiffness());
        setDampingRatio(origamiPOPConverter.getDampingRatio());
    }

    public void useiOSUIViewSpring(float dampingratio,float duration){
        UIViewSpringConverter uiViewSpringConverter = new UIViewSpringConverter(dampingratio,duration);
        setStiffness(uiViewSpringConverter.getStiffness());
        setDampingRatio(uiViewSpringConverter.getDampingRatio());
    }

    public void useiOSCASpring(float stiffness,float damping){
        useDHOSpring(stiffness,damping);
    }

    public void useProtopieSpring(float tension,float friction){
        useRK4Spring(tension,friction);
    }

    public void usePrincipleSpring(float tension,float friction){
        useRK4Spring(tension,friction);
    }

    // ############################################
    // Getter & Setter
    // ############################################

    public float getStiffness() {
        return mStiffness;
    }

    public float getDampingRatio() {
        return mDampingRatio;
    }

    public void setStiffness(float stiffness){
        mStiffness = stiffness;
        if(mListener !=null){
            mListener.onSolverUpdate(mStiffness,mDampingRatio);
        }
    }

    public void setDampingRatio(float dampingratio){
        mDampingRatio = dampingratio;
        if(mListener !=null){
            mListener.onSolverUpdate(mStiffness,mDampingRatio);
        }
    }

    // ############################################
    // Override
    // ############################################

    @Override
    public void setSolver(AnimationSolver solver){
        springSolver = solver;
    }

    @Override
    public Object getSolver(){
        return springSolver;
    }
}




