package com.martinrgb.animation_engine.controller;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
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

import com.martinrgb.animation_engine.overscroller.FlingCalculator;
import com.martinrgb.animation_engine.solver.AnimationSolver;
import com.martinrgb.animation_engine.solver.FlingSolver;
import com.martinrgb.animation_engine.solver.SpringSolver;
import com.martinrgb.animation_engine.solver.TimingSolver;
import com.martinrgb.animation_engine.converter.DHOConverter;
import com.martinrgb.animation_engine.converter.OrigamiPOPConverter;
import com.martinrgb.animation_engine.converter.RK4Converter;
import com.martinrgb.animation_engine.converter.UIViewSpringConverter;



public class AnimationController<T> {

    private T mAnimatorObject;
    final Object mTarget;
    final FloatPropertyCompat mProperty;
    private PhysicsState mPhysicsState;

    private FlingAnimation mFlingAnimation;
    private SpringAnimation mSpringAnimation;
    private ObjectAnimator mTimingAnimation;

    private float mStartVelocity = 1000,mFriction = 0.5f;
    private float mStiffness = 300f,mDampingRatio = 0.6f;

    private float mPrevVelocity = 0,mCurrentVelocity = 0;
    private TimeInterpolator mTimingInterpolator = new LinearInterpolator();
    private float mTimingDuration = 500;

    private static AnimationSolver currentSolver;
    private static int FLING_SOLVER_MODE = 0;
    private static int SPRING_SOLVER_MODE = 1;
    private static int TIMING_SOLVER_MODE = 2;
    private static int SOLVER_MODE = -1;

    private int VALUE_ANIMATOR_MODE = 0;
    private int OBJECT_ANIMAOTR_MODE = 1;
    private int ANIMATOR_MODE = -1;

    // ###########################################
    // Constructor
    // ###########################################

    public AnimationController(T animatorObject) {
        mAnimatorObject = animatorObject;
        mTarget = null;
        mProperty = null;
        mPhysicsState = new PhysicsState();
        ANIMATOR_MODE = VALUE_ANIMATOR_MODE;
        setupAnimatorByCreator(mAnimatorObject);
    }

    public <K> AnimationController(T animatorObject,K target, FloatPropertyCompat<K> property) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupAnimatorByCreator(mAnimatorObject);
    }

    public <K> AnimationController(T animatorObject,K target, FloatPropertyCompat<K> property,float to) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupAnimatorByCreator(mAnimatorObject);
    }

    public <K> AnimationController(T animatorObject,K target, FloatPropertyCompat<K> property,float from,float to) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        mProperty = property;
        mPhysicsState = new PhysicsState(from,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupAnimatorByCreator(animatorObject);
    }

    public <K> AnimationController(AnimationSolver solver, K target, FloatPropertyCompat<K> property, float from, float to) {
        mTarget = target;
        mProperty = property;
        mPhysicsState = new PhysicsState(from,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupAnimatorBySolver(SOLVER_MODE);
    }

    // ############################################
    // Setup Solver
    // ############################################

    public static final AnimationSolver createFlingSolver(float velocity,float friction){
        SOLVER_MODE = FLING_SOLVER_MODE;
        currentSolver = new FlingSolver(velocity,friction);
        return currentSolver;
    }

    public static final AnimationSolver createSpringSolver(float stiffness,float dampingratio){
        SOLVER_MODE = SPRING_SOLVER_MODE;
        currentSolver = new SpringSolver(stiffness,dampingratio);
        return currentSolver;
    }

    public static final AnimationSolver createTimingSolver(TimeInterpolator interpolator, long duration){
        SOLVER_MODE = TIMING_SOLVER_MODE;
        currentSolver = new TimingSolver(interpolator,duration);
        return currentSolver;
    }

    public void setSolver(AnimationSolver solver){
        currentSolver = solver;
        cancelAllAnimation();
        setupAnimatorBySolver(SOLVER_MODE);
    }

    public static SpringSolver getSpringSolver() {
        return (SpringSolver) currentSolver;
    }

    public static FlingSolver getFlingSolver() {
        return (FlingSolver) currentSolver;
    }

    public static TimingSolver getTimingSolver() {
        return (TimingSolver) currentSolver;
    }

    // ############################################
    // Setup Aniamtor
    // ############################################

    private void setupAnimatorBySolver(int solver_mode) {
        switch(solver_mode)
        {
            case 0:
                setupFlingAnimator();
                break;
            case 1:
                setupSpringAnimator();
                break;
            case 2:
                setupTimingAnimator();
                break;
            default:
                break;
        }
    }

    private void setupAnimatorByCreator(Object animator){
        switch(animator.getClass().getSimpleName())
        {
            case "FlingAnimation":
                currentSolver = createFlingSolver(mStartVelocity,mFriction);
                break;
            case "SpringAnimation":
                currentSolver = createSpringSolver(mStiffness,mDampingRatio);
                break;
            case "ValueAnimator":
                currentSolver = createTimingSolver(mTimingInterpolator,(long)mTimingDuration);
                break;
            default:
                break;
        }
        setupAnimatorBySolver(SOLVER_MODE);
    }

    private void setupFlingAnimator(){
        mFlingAnimation = new FlingAnimation(new FloatValueHolder());
        mFlingAnimation.setStartVelocity(((FlingSolver)currentSolver).getVelocity());
        mFlingAnimation.setFriction(((FlingSolver)currentSolver).getFriction());

        currentSolver.setSolverListener(new AnimationSolver.SolverListener() {
            @Override
            public void onSolverUpdate(Object arg1, Object arg2) {
                mFlingAnimation.setStartVelocity((float) arg1);
                mFlingAnimation.setFriction((float) arg2);
            }
        });

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
    }

    private void setupSpringAnimator(){
        mSpringAnimation = new SpringAnimation(new FloatValueHolder());
        mSpringAnimation.setSpring(new SpringForce());
        mSpringAnimation.getSpring().setStiffness(((SpringSolver)currentSolver).getStiffness());
        mSpringAnimation.getSpring().setDampingRatio(((SpringSolver)currentSolver).getDampingRatio());

        currentSolver.setSolverListener(new AnimationSolver.SolverListener() {
            @Override
            public void onSolverUpdate(Object arg1, Object arg2) {
                mSpringAnimation.getSpring().setStiffness((float) arg1);
                mSpringAnimation.getSpring().setDampingRatio((float) arg2);
            }
        });

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
    }

    private void setupTimingAnimator(){
        mTimingAnimation = new ObjectAnimator();
        mTimingAnimation.setInterpolator(((TimingSolver)currentSolver).getInterpolator());
        mTimingAnimation.setDuration((long) ((TimingSolver)currentSolver).getDuration());

        currentSolver.setSolverListener(new AnimationSolver.SolverListener() {
            @Override
            public void onSolverUpdate(Object arg1, Object arg2) {
                mTimingAnimation.setInterpolator((TimeInterpolator) arg1);
                mTimingAnimation.setDuration((long) arg2);
            }
        });

        mTimingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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

        mTimingAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPhysicsState.updatePhysics(mPhysicsState.getPhysicsValue(),0);
                if(mListener !=null){
                    mListener.onAnimationEnd(true,mPhysicsState.getPhysicsValue(),0);
                }
            }
        });
    }


    // ############################################
    // Animation Control Interface
    // ############################################

    // ## Android Style Animaton Interface,driven by PhysicsState's State Machine
    public void startValue(float start){
        mPhysicsState.updatePhysicsValue(start);
        mPhysicsState.setStateValue("Start",start);
        switch(SOLVER_MODE)
        {
            case 0:
                mFlingAnimation.setStartValue(mPhysicsState.getStateValue("Start"));
                break;
            case 1:
                mSpringAnimation.setStartValue(mPhysicsState.getStateValue("Start"));
                break;
            case 2:
                mTimingAnimation.setFloatValues(mPhysicsState.getStateValue("Start"),mPhysicsState.getStateValue("End"));
                break;
            default:
                break;
        }
    }
    public void endValue(float end){
        mPhysicsState.setStateValue("End",end);
        switch(SOLVER_MODE)
        {
            case 0:
                if(mSpringAnimation == null){
                    currentSolver = createSpringSolver(50,0.99f);
                    setupSpringAnimator();
                    mSpringAnimation.getSpring().setFinalPosition(mPhysicsState.getStateValue("End"));
                }
                else{
                    mSpringAnimation.getSpring().setFinalPosition(mPhysicsState.getStateValue("End"));
                }
                break;
            case 1:
                mSpringAnimation.getSpring().setFinalPosition(mPhysicsState.getStateValue("End"));
                break;
            case 2:
                mTimingAnimation.setFloatValues(mPhysicsState.getStateValue("Start"),mPhysicsState.getStateValue("End"));
                break;
            default:
                break;
        }
    }
    public void start(){
        animateToState("End");
    }
    public void cancel(){
        cancelAllAnimation();
    }

    private void cancelAllAnimation(){

        if(mFlingAnimation !=null){
            mFlingAnimation.cancel();
        }

        if(mSpringAnimation !=null){
            mSpringAnimation.cancel();
        }

        if(mTimingAnimation !=null){
            mTimingAnimation.cancel();
        }

    }

    public void end(){

        switch(SOLVER_MODE)
        {
            case 0:
                mFlingAnimation.cancel();
                if(mSpringAnimation == null){
                    currentSolver = createSpringSolver(50,0.99f);
                    setupSpringAnimator();
                    if(mSpringAnimation.canSkipToEnd()){
                        mSpringAnimation.cancel();
                        mSpringAnimation.skipToEnd();
                    }
                }
                else{
                    if(mSpringAnimation.canSkipToEnd()){
                        mSpringAnimation.cancel();
                        mSpringAnimation.skipToEnd();
                    }
                }
                break;
            case 1:
                if(mSpringAnimation.canSkipToEnd()){
                    mSpringAnimation.cancel();
                    mSpringAnimation.skipToEnd();
                }
                break;
            case 2:
                mTimingAnimation.end();
                break;
            default:
                break;
        }
        switchToState("End");
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
        switch(SOLVER_MODE)
        {
            case 0:
                if(mSpringAnimation == null){
                    currentSolver = createSpringSolver(50,0.99f);
                    setupSpringAnimator();
                    mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                    mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                    mSpringAnimation.animateToFinalPosition(mPhysicsState.getStateValue(state));
                }
                else{
                    mSpringAnimation.getSpring().setDampingRatio(0.99f);
                    mSpringAnimation.getSpring().setStiffness(50);
                    mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                    mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                    mSpringAnimation.animateToFinalPosition(mPhysicsState.getStateValue(state));
                }
                break;
            case 1:
                mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(mPhysicsState.getStateValue(state));
                break;
            case 2:
                mTimingAnimation.setFloatValues(mPhysicsState.getPhysicsValue(),mPhysicsState.getStateValue(state));
                mTimingAnimation.start();
                break;
            default:
                break;
        }

    }

    // ## Origami-POP-Rebound Style Animation Interface,driven by PhysicsState's Value

    // # Equal to [setEndVlaue]
    public void animateTo(float value){
        switch(SOLVER_MODE)
        {
            case 0:
                if(mSpringAnimation == null){
                    currentSolver = createSpringSolver(50,0.99f);
                    setupSpringAnimator();
                    mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                    mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                    mSpringAnimation.animateToFinalPosition(value);
                }
                else{
                    mSpringAnimation.getSpring().setDampingRatio(0.99f);
                    mSpringAnimation.getSpring().setStiffness(50);
                    mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                    mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                    mSpringAnimation.animateToFinalPosition(value);
                }
                break;
            case 1:
                mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(value);
                break;
            case 2:
                mTimingAnimation.setFloatValues(mPhysicsState.getPhysicsValue(),value);
                mTimingAnimation.start();
                break;
            default:
                break;
        }
    }

    // # Equal to [setCurrentValue]
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
