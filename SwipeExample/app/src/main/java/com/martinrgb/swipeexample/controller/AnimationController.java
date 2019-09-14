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
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupSpringAnimator(ANIMATOR_MODE);
    }

    public <K> AnimationController(K object, FloatPropertyCompat<K> property,float to) {
        mTarget = object;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupSpringAnimator(ANIMATOR_MODE);
    }

    public <K> AnimationController(K object, FloatPropertyCompat<K> property,float from,float to) {
        mTarget = object;
        mProperty = property;
        mPhysicsState = new PhysicsState(from,to);
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
                mPhysicsState.updatePhysics(value,velocity);
                if(mListener !=null){
                    mListener.onAnimationUpdate(value,velocity);
                }
            }
        });
        mSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                mPhysicsState.updatePhysics(value,velocity);
                if(mListener !=null){
                    mListener.onAnimationEnd(canceled,value,velocity);
                }
            }
        });

    }

    // ###########  Animation Play|Pause|End Control ###########

    // # Android Style Animaton Interface,driven by a state machine in PhysicsState
    public void setStartAndEnd(float start,float end){
        mPhysicsState.updatePhysicsValue(start);
        mSpringAnimation.setStartValue(start);

        mPhysicsState.setStartState(start);
        mPhysicsState.setEndState(end);
    }

    public void start(){
        animateToState("End");
    }
    public void cancel(){
        mSpringAnimation.cancel();
    }
    public void end(){
        if(mSpringAnimation.canSkipToEnd()){
            mSpringAnimation.cancel();
            mSpringAnimation.skipToEnd();
            switchToState("End");
        }
    }

    public void reverse(){
        animateToState("Prev");
    }

    // # FramerJS Style Animation Interface,driven by a state machine in PhysicsState

    public void setState(String key,float value){
        mPhysicsState.setStateValue(key,value);
    }

    public void switchToState(String state){
        //TODO: No Event and no Animation then set the prev State
        //mPhysicsState.setPrevState(mPhysicsState.getStateValue(state));
        setCurrenetPhysicsValue(mPhysicsState.getStateValue(state));
    }
    public void animateToState(String state){
        setCurrenetPhysicsValue(mPhysicsState.getPhysicsValue());
        mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
        mSpringAnimation.animateToFinalPosition(mPhysicsState.getStateValue(state));
    }

    // # Origami-POP-Rebound Style Animation Interface,driven by PhysicsState

    public void setEndValue(float value){
        mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
        mSpringAnimation.animateToFinalPosition(value);
    }

    public void setCurrentValue(float value){
        setCurrenetPhysicsValue(value);
    }

    // ########### Animation Controller PhysicsState Control ###########

    public void setCurrenetPhysicsValue(float value){
        mPhysicsState.updatePhysicsValue(value);
        if(mProperty !=null){
            mProperty.setValue(mTarget,value);
        }
    }
    public float getCurrentPhysicsValue(){
        return mPhysicsState.getPhysicsValue();
    }
    public void setCurrentPhysicsVelocity(float velocity){
        mPhysicsState.updatePhysicsVelocity(velocity);
    }
    public float getCurrentPhysicsVelocity(){
        return  mPhysicsState.getPhysicsVelocity();
    }
    public void setCurrentPhysicsState(float value,float velocity){
        setCurrenetPhysicsValue(value);
        setCurrentPhysicsVelocity(velocity);
    }
    public PhysicsState getCurrentPhysicsState(){
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
