package com.martinrgb.animation_engine.solver;

import com.martinrgb.animation_engine.converter.DHOConverter;
import com.martinrgb.animation_engine.converter.OrigamiPOPConverter;
import com.martinrgb.animation_engine.converter.RK4Converter;
import com.martinrgb.animation_engine.converter.UIViewSpringConverter;

public class SpringSolver extends AnimSolver {

    private float mStiffness = 300.f,mDampingRatio = 0.6f;
    private Object springSolver;

    public SpringSolver(float stiffness,float dampingratio) {
        setStiffness(stiffness);
        setDampingRatio(dampingratio);
        springSolver= this;
    }

    // ############################################
    // Spring Animation Solver's Converter
    // ############################################

    public static SpringSolver createAndroidSpring(float stiffness,float dampingratio){
        return new SpringSolver(stiffness,dampingratio);
    }

    public static SpringSolver createRK4Spring(float tension,float friction){
        RK4Converter rk4Converter = new RK4Converter(tension,friction);
        return new SpringSolver(rk4Converter.getStiffness(),rk4Converter.getDampingRatio());
    }

    public static SpringSolver createDHOSpring(float stiffness,float damping){
        DHOConverter dhoConverter = new DHOConverter(stiffness,damping);
        return new SpringSolver(dhoConverter.getStiffness(),dhoConverter.getDampingRatio());
    }

    public static SpringSolver createOrigamiSpring(float bounciness,float speed){
        OrigamiPOPConverter origamiPOPConverter = new OrigamiPOPConverter(bounciness,speed);
        return new SpringSolver(origamiPOPConverter.getStiffness(),origamiPOPConverter.getDampingRatio());
    }

    public static SpringSolver createUIViewSpring(float dampingratio,float duration){
        UIViewSpringConverter uiViewSpringConverter = new UIViewSpringConverter(dampingratio,duration);
        return new SpringSolver(uiViewSpringConverter.getStiffness(),uiViewSpringConverter.getDampingRatio());
    }

    public static SpringSolver createCASpring(float stiffness,float damping){
        return createDHOSpring(stiffness,damping);
    }

    public static SpringSolver createProtopieSpring(float tension,float friction){
        return createRK4Spring(tension,friction);
    }

    public static SpringSolver createPrincipleSpring(float tension,float friction){
        return createRK4Spring(tension,friction);
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
    public void setSolver(AnimSolver solver){
        springSolver = solver;
    }

    @Override
    public Object getSolver(){
        return springSolver;
    }

    @Override
    public int getSolverMode() {
        return 1;
    }
}




