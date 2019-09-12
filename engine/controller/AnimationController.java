//package com.martinrgb.swipeexample.controller;

import android.util.Log;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class AnimationController {

    final Object mTarget;
    final FloatPropertyCompat mProperty;
    private PhysicsState mPhysicsState;
    private SpringAnimation mSpringAnimation;
    private float mDampingRatio = 0.7f;
    private float mStiffness = 400;

    public AnimationController() {
        mTarget = null;
        mProperty = null;
        mPhysicsState = new PhysicsState();
        setupAnimator(true);
    }

    public <K> AnimationController(K object, FloatPropertyCompat<K> property) {
        mTarget = object;
        mProperty = property;
        mPhysicsState = new PhysicsState();
        setupAnimator(false);
    }

    private void setupAnimator(boolean isValueAnimator){

        mSpringAnimation = (isValueAnimator)? new SpringAnimation(new FloatValueHolder()):new SpringAnimation(mTarget,mProperty,mPhysicsState.getValue());
        mSpringAnimation.setSpring(new SpringForce());
        mSpringAnimation.getSpring().setDampingRatio(mDampingRatio);
        mSpringAnimation.getSpring().setStiffness(mStiffness);
        mSpringAnimation.setMinimumVisibleChange(0.001f);
        mSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                mPhysicsState.updateState(value,velocity);
                if(mListener !=null){
                    mListener.onAnimationUpdate(value,velocity);
                }
            }
        });
        mSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                mPhysicsState.updateState(value,velocity);

                if(mListener !=null){
                    mListener.onAnimationEnd(canceled,value,velocity);
                }
            }
        });
    }

    public void start(){
        mSpringAnimation.start();
    }

    public void cancel(){
        mSpringAnimation.cancel();
    }

    public void end(){
        if(mSpringAnimation.canSkipToEnd()){
            mSpringAnimation.cancel();
            mSpringAnimation.skipToEnd();
        }
    }

    public void setCurrenetValue(float value){

        mPhysicsState.updateValue(value);

        if(mProperty !=null){
            mProperty.setValue(mTarget,value);
        }

    }

    public void setCurrentVelocity(float velocity){
        mPhysicsState.updateVelocity(velocity);
    }

    public float getCurrentValue(){
        return mPhysicsState.getValue();
    }

    public float getCurrentVelocity(){
        return  mPhysicsState.getVelocity();
    }

    public void setCurrentState(float value,float velocity){
        setCurrenetValue(value);
        setCurrentVelocity(velocity);
    }

    public void setEndValue(float value){
        mPhysicsState.updateValue(value);
        mSpringAnimation.setStartVelocity(mPhysicsState.getVelocity());
        mSpringAnimation.animateToFinalPosition(mPhysicsState.getValue());
    }

    // ########### Spring Animation Converter ###########

    public void useAndroidSpring(float stiffness,float dampingratio){
        mStiffness = stiffness;
        mDampingRatio = dampingratio;
    }

    public void useRK4Spring(float tension,float friction){
        RK4Converter rk4Converter = new RK4Converter(tension,friction);
        mStiffness = rk4Converter.getStiffness();
        mDampingRatio = rk4Converter.getDampingRatio();
    }

    public void useDHOSpring(float stiffness,float damping){
        DHOConverter dhoConverter = new DHOConverter(stiffness,damping);
        mStiffness = dhoConverter.getStiffness();
        mDampingRatio = dhoConverter.getDampingRatio();
    }

    public void useOrigamiPOPSpring(float bounciness,float speed){
        OrigamiPOPConverter origamiPOPConverter = new OrigamiPOPConverter(bounciness,speed);
        mStiffness = origamiPOPConverter.getStiffness();
        mDampingRatio = origamiPOPConverter.getDampingRatio();
    }

    public void useiOSUIViewSpring(float dampingratio,float duration){
        UIViewSpringConverter uiViewSpringConverter = new UIViewSpringConverter(dampingratio,duration);
        mStiffness = uiViewSpringConverter.getStiffness();
        mDampingRatio = uiViewSpringConverter.getDampingRatio();
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

    // ########### Spring Animation Listener ###########

    private AnimationListener mListener;

    public void setAnimationListener(AnimationListener listener) {
        mListener = listener;
    }

    public interface AnimationListener {
        void onAnimationUpdate(float value, float velocity);

        void onAnimationEnd(boolean canceled, float value, float velocity);
    }

}
