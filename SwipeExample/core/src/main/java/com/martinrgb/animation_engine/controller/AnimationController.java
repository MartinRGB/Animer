package com.martinrgb.animation_engine.controller;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Interpolator;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.martinrgb.animation_engine.converter.DHOConverter;
import com.martinrgb.animation_engine.converter.OrigamiPOPConverter;
import com.martinrgb.animation_engine.converter.RK4Converter;
import com.martinrgb.animation_engine.converter.UIViewSpringConverter;



public class AnimationController<T> {

    private T mAnimatorObject;
    final Object mTarget;
    final FloatPropertyCompat mProperty;
    private PhysicsState mPhysicsState;


    private SpringAnimation mSpringAnimation;
    private FlingAnimation mFlingAnimation;
    private ObjectAnimator mValueAnimator;

    private float mStiffness = 300f,mDampingRatio = 0.6f;
    private float mStartVelocity = 1000,mFriction = 0.5f;
    private float mPrevVelocity = 0,mCurrentVelocity = 0,mValueAnimEndValue = 1000,mValueAnimDuration = 1000;

    private int VALUE_ANIMATOR_MODE = 0;
    private int OBJECT_ANIMAOTR_MODE = 1;
    private int ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;

    // ###########################################
    // Constructor
    // ###########################################

    public AnimationController(T animatorObject) {
        mAnimatorObject = animatorObject;
        mTarget = null;
        mProperty = null;
        mPhysicsState = new PhysicsState();
        ANIMATOR_MODE = VALUE_ANIMATOR_MODE;
        setupAnimator(mAnimatorObject);
    }

    public <K> AnimationController(T animatorObject,K target, FloatPropertyCompat<K> property) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupAnimator(mAnimatorObject);
    }

    public <K> AnimationController(T animatorObject,K target, FloatPropertyCompat<K> property,float to) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupAnimator(mAnimatorObject);
    }

    public <K> AnimationController(T animatorObject,K target, FloatPropertyCompat<K> property,float from,float to) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        mProperty = property;
        mPhysicsState = new PhysicsState(from,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupAnimator(animatorObject);
    }

//    private void initSpring(Object object){
//        Log.e("FinalPos",String.valueOf(object));
//        if (object instanceof Float) {
//            Log.e("Float",String.valueOf(object));
//        }
//    }

    // ############################################
    // Setup Aniamtor
    // ############################################

    private void setupAnimator(Object animator){

        Log.e("Cons",String.valueOf(animator.getClass().getSimpleName()));
        switch(animator.getClass().getSimpleName())
        {
            case "FlingAnimation":
                mFlingAnimation = new FlingAnimation(new FloatValueHolder());
                mFlingAnimation.setStartVelocity(mStartVelocity);
                mFlingAnimation.setFriction(mFriction);
                mFlingAnimation.setMinimumVisibleChange(0.001f);
                mFlingAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                    @Override
                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                        mPhysicsState.updatePhysics(value,velocity);

                        if(ANIMATOR_MODE != VALUE_ANIMATOR_MODE){
                            mProperty.setValue(mTarget,mPhysicsState.getPhysicsValue());
                        }

                        if(mListener !=null){
                            mListener.onAnimationUpdate(value,velocity);
                        }
                    }
                });
                mFlingAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                        mPhysicsState.updatePhysics(value,velocity);
                        if(mListener !=null){
                            mListener.onAnimationEnd(canceled,value,velocity);
                        }
                    }
                });
                break;
            case "SpringAnimation":
                mSpringAnimation = new SpringAnimation(new FloatValueHolder());
                mSpringAnimation.setSpring(new SpringForce());
                mSpringAnimation.getSpring().setStiffness(mStiffness);
                mSpringAnimation.getSpring().setDampingRatio(mDampingRatio);
                mSpringAnimation.setMinimumVisibleChange(0.001f);
                mSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                    @Override
                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                        mPhysicsState.updatePhysics(value,velocity);

                        if(ANIMATOR_MODE != VALUE_ANIMATOR_MODE){
                            mProperty.setValue(mTarget,mPhysicsState.getPhysicsValue());
                    }

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
                break;
            case "ValueAnimator":
                mValueAnimator = new ObjectAnimator();
                mValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
                mValueAnimator.setFloatValues(0,mValueAnimEndValue);
                mValueAnimator.setDuration((long) mValueAnimDuration);
                mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                        mPrevVelocity = mCurrentVelocity;
                        mCurrentVelocity = (float) valueAnimator.getAnimatedValue();

                        float value = mCurrentVelocity;
                        float velocity = mCurrentVelocity-mPrevVelocity;

                        mPhysicsState.updatePhysics(value,velocity);

                        if(mListener !=null){
                            mListener.onAnimationUpdate(value,velocity);
                        }

                        if(ANIMATOR_MODE != VALUE_ANIMATOR_MODE){
                            mProperty.setValue(mTarget,mPhysicsState.getPhysicsValue());
                        }

                    }
                });

                mValueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mPhysicsState.updatePhysics(mValueAnimEndValue,0);
                        if(mListener !=null){
                            mListener.onAnimationEnd(true,mValueAnimEndValue,0);
                        }
                    }
                });

                break;
            default:
                break;
        }
    }

    // ############################################
    // nimation Control Interface
    // ############################################

    // ## Android Style Animaton Interface,driven by PhysicsState's State Machine
    public void startValue(float start){
        mPhysicsState.updatePhysicsValue(start);
        mSpringAnimation.setStartValue(start);
        mPhysicsState.setStateValue("Start",start);
    }
    public void endValue(float end){
        mPhysicsState.setStateValue("End",end);
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
        animateToState("Start");
    }

    // ## FramerJS Style Animation Interface,driven by PhysicsState's State Machine

    public void setState(String key,float value){
        mPhysicsState.setStateValue(key,value);
    }

    public void switchToState(String state){
        setCurrenetPhysicsValue(mPhysicsState.getStateValue(state));
    }
    public void animateToState(String state){
        setCurrenetPhysicsValue(mPhysicsState.getPhysicsValue());
        mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
        mSpringAnimation.animateToFinalPosition(mPhysicsState.getStateValue(state));
    }

    // ## Origami-POP-Rebound Style Animation Interface,driven by PhysicsState's Value

    // # Equal to [setEndVlaue]
    public void animateTo(float value){
        mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
        mSpringAnimation.animateToFinalPosition(value);
    }
    // # Equal to [ssetCurrentValue]
    public void switchTo(float value){
        setCurrenetPhysicsValue(value);
    }

    // ############################################
    // PhysicsState's Getter & Setter
    // ############################################

    private void setCurrenetPhysicsValue(float value){
        mPhysicsState.updatePhysicsValue(value);
        if(mProperty !=null){
            mProperty.setValue(mTarget,value);
        }
    }
    private float getCurrentPhysicsValue(){
        return mPhysicsState.getPhysicsValue();
    }
    private void setCurrentPhysicsVelocity(float velocity){
        mPhysicsState.updatePhysicsVelocity(velocity);
    }
    private float getCurrentPhysicsVelocity(){
        return  mPhysicsState.getPhysicsVelocity();
    }
    private void setCurrentPhysicsState(float value,float velocity){
        setCurrenetPhysicsValue(value);
        setCurrentPhysicsVelocity(velocity);
    }
    private PhysicsState getCurrentPhysicsState(){
       return mPhysicsState;
    }

    // ############################################
    // Spring Animation Converter
    // ############################################

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

    // ############################################
    // Animation Listener
    // ############################################

    private AnimationListener mListener;

    public void setAnimationListener(AnimationListener listener) {
        mListener = listener;
    }

    public interface AnimationListener {
        void onAnimationUpdate(float value, float velocity);

        void onAnimationEnd(boolean canceled, float value, float velocity);
    }

}
