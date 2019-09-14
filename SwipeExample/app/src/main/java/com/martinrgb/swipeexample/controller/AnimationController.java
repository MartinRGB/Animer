package com.martinrgb.swipeexample.controller;


import android.animation.ObjectAnimator;
import android.util.Log;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.martinrgb.swipeexample.converter.DHOConverter;
import com.martinrgb.swipeexample.converter.OrigamiPOPConverter;
import com.martinrgb.swipeexample.converter.RK4Converter;
import com.martinrgb.swipeexample.converter.UIViewSpringConverter;

public class AnimationController {

    final Object mTarget;
    final FloatPropertyCompat mProperty;
    private PhysicsState mPhysicsState;
    private SpringAnimation mSpringAnimation;
    private float mDampingRatio = 0.7f,mStiffness = 400f;
    private int VALUE_ANIMATOR_MODE = 0;
    private int OBJECT_ANIMAOTR_MODE = 1;
    private int ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
    private ValueState mValueState = new ValueState();

    public AnimationController() {
        mTarget = null;
        mProperty = null;
        mPhysicsState = new PhysicsState();
        ANIMATOR_MODE = VALUE_ANIMATOR_MODE;
        setupSpringAnimator(ANIMATOR_MODE);
    }

    public <K> AnimationController(K object, FloatPropertyCompat<K> property) {
        mTarget = object;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue);
        mValueState.setState("Start",proertyValue);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupSpringAnimator(ANIMATOR_MODE);
    }

    public <K> AnimationController(K object, FloatPropertyCompat<K> property,float to) {
        mTarget = object;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue);
        mValueState.setState("Start",proertyValue);
        mValueState.setState("End",to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupSpringAnimator(ANIMATOR_MODE);
    }

    private void initSpring(Object object){
        Log.e("FinalPos",String.valueOf(object));
        if (object instanceof Float) {
            Log.e("Float",String.valueOf(object));
        }
    }

    private void setupSpringAnimator(int MODE){

        mSpringAnimation = (MODE == VALUE_ANIMATOR_MODE)? new SpringAnimation(new FloatValueHolder()):new SpringAnimation(mTarget,mProperty);
        mSpringAnimation.setSpring(new SpringForce());
        mSpringAnimation.getSpring().setStiffness(mStiffness);
        mSpringAnimation.getSpring().setDampingRatio(mDampingRatio);
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

    // ###########  Animation Play|Pause|End Control ###########

    public void start(){
        //mSpringAnimation.start();
        setCurrenetValue(mPhysicsState.getValue());
        animateToState("End");
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

    public void reverse(){
        animateToState("Start");
    }

    public void setState(String key,float value){
        mValueState.setState(key,value);
    }

    public void switchToState(String state){
        setCurrenetValue(mValueState.getStateValue(state));
    }

    public void animateToState(String state){
        mSpringAnimation.setStartVelocity(mPhysicsState.getVelocity());
        mSpringAnimation.animateToFinalPosition(mValueState.getStateValue(state));
    }

    public void setStartValue(float value){
        mPhysicsState.updateValue(value);
        mSpringAnimation.setStartValue(value);
        mValueState.setState("Start",value);

    }
    public void setEndValue(float value){
        mValueState.setState("End",value);
    }

    public void animateTo(float value){
        mSpringAnimation.setStartVelocity(mPhysicsState.getVelocity());
        mSpringAnimation.animateToFinalPosition(value);
    }

    // ########### Animation Controller PhysicsState Control ###########

    public void setCurrenetValue(float value){
        mPhysicsState.updateValue(value);
        if(mProperty !=null){
            mProperty.setValue(mTarget,value);
        }
    }
    public float getCurrentValue(){
        return mPhysicsState.getValue();
    }
    public void setCurrentVelocity(float velocity){
        mPhysicsState.updateVelocity(velocity);
    }
    public float getCurrentVelocity(){
        return  mPhysicsState.getVelocity();
    }
    public void setCurrentState(float value,float velocity){
        setCurrenetValue(value);
        setCurrentVelocity(velocity);
    }
    public PhysicsState getCurrentState(){
       return mPhysicsState;
    }

    // ########### Spring Animation Converter ###########

    private void resetSpring(float stiffness,float dampingratio){
        mSpringAnimation.getSpring().setStiffness(stiffness);
        mSpringAnimation.getSpring().setDampingRatio(dampingratio);
    }

    public void useAndroidSpring(float stiffness,float dampingratio){
        mStiffness = stiffness;
        mDampingRatio = dampingratio;
        resetSpring(mStiffness,mDampingRatio);
    }

    public void useRK4Spring(float tension,float friction){
        RK4Converter rk4Converter = new RK4Converter(tension,friction);
        mStiffness = rk4Converter.getStiffness();
        mDampingRatio = rk4Converter.getDampingRatio();
        resetSpring(mStiffness,mDampingRatio);
    }

    public void useDHOSpring(float stiffness,float damping){
        DHOConverter dhoConverter = new DHOConverter(stiffness,damping);
        mStiffness = dhoConverter.getStiffness();
        mDampingRatio = dhoConverter.getDampingRatio();
        resetSpring(mStiffness,mDampingRatio);
    }

    public void useOrigamiPOPSpring(float bounciness,float speed){
        OrigamiPOPConverter origamiPOPConverter = new OrigamiPOPConverter(bounciness,speed);
        mStiffness = origamiPOPConverter.getStiffness();
        mDampingRatio = origamiPOPConverter.getDampingRatio();
        resetSpring(mStiffness,mDampingRatio);
    }

    public void useiOSUIViewSpring(float dampingratio,float duration){
        UIViewSpringConverter uiViewSpringConverter = new UIViewSpringConverter(dampingratio,duration);
        mStiffness = uiViewSpringConverter.getStiffness();
        mDampingRatio = uiViewSpringConverter.getDampingRatio();
        resetSpring(mStiffness,mDampingRatio);
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
