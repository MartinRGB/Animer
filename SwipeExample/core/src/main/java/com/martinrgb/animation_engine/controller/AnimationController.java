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
    private ObjectAnimator mTimingAnimator;

    private float mStartVelocity = 1000,mFriction = 0.5f;
    private float mStiffness = 300f,mDampingRatio = 0.6f;
    private float mPrevVelocity = 0,mCurrentVelocity = 0;
    private float mTimingAnimDuration = 1000;
    private TimeInterpolator mTimingAnimInterpolator = new LinearInterpolator();
    private float mTimingAnimEndValue = 1000;

    private static AnimationSolver springSolver;
    private static AnimationSolver flingSolver;
    private static AnimationSolver timingSolver;
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
        return flingSolver;
    }

    public static final AnimationSolver createSpringSolver(float stiffness,float dampingratio){
        SOLVER_MODE = SPRING_SOLVER_MODE;
        currentSolver = new SpringSolver(stiffness,dampingratio);
        return springSolver;
    }

    public static final AnimationSolver createTimingSolver(TimeInterpolator interpolator, long duration){
        SOLVER_MODE = TIMING_SOLVER_MODE;
        currentSolver = new TimingSolver(interpolator,duration);
        return timingSolver;
    }

    public void setSolver(AnimationSolver solver){
        currentSolver = solver;
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
                break;
            case 1:

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
                break;
            case 2:

                mTimingAnimator = new ObjectAnimator();
                mTimingAnimator.setInterpolator(((TimingSolver)currentSolver).getInterpolator());
                mTimingAnimator.setDuration((long) ((TimingSolver)currentSolver).getDuration());

                currentSolver.setSolverListener(new AnimationSolver.SolverListener() {
                    @Override
                    public void onSolverUpdate(Object arg1, Object arg2) {
                        mTimingAnimator.setInterpolator((TimeInterpolator) arg1);
                        mTimingAnimator.setDuration((long) arg2);
                    }
                });

                mTimingAnimator.setFloatValues(0,mTimingAnimEndValue);

                mTimingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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

                mTimingAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mPhysicsState.updatePhysics(mTimingAnimEndValue,0);
                        if(mListener !=null){
                            mListener.onAnimationEnd(true,mTimingAnimEndValue,0);
                        }
                    }
                });

                break;
            default:
                break;
        }
    }

    private void setupAnimatorByCreator(Object animator){

        switch(animator.getClass().getSimpleName())
        {
            case "FlingAnimation":

                currentSolver = new FlingSolver(mStartVelocity,mFriction);
                mFlingAnimation = new FlingAnimation(new FloatValueHolder());
                mFlingAnimation.setStartValue(0);
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
                mFlingAnimation.start();
                break;
            case "SpringAnimation":

                currentSolver = new SpringSolver(mStiffness,mDampingRatio);

                mSpringAnimation = new SpringAnimation(new FloatValueHolder());
                mSpringAnimation.setSpring(new SpringForce());
                mSpringAnimation.getSpring().setStiffness(((SpringSolver)currentSolver).getStiffness());
                mSpringAnimation.getSpring().setDampingRatio(((SpringSolver)currentSolver).getDampingRatio());

                currentSolver.setSolverListener(new AnimationSolver.SolverListener() {
                    @Override
                    public void onSolverUpdate(Object arg1, Object arg2) {
                        mSpringAnimation.getSpring().setStiffness((float)arg1);
                        mSpringAnimation.getSpring().setDampingRatio((float)arg2);
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
                break;
            case "ValueAnimator":

                currentSolver = new TimingSolver(mTimingAnimInterpolator,(long)mTimingAnimDuration);

                mTimingAnimator = new ObjectAnimator();
                mTimingAnimator.setInterpolator(((TimingSolver)currentSolver).getInterpolator());
                mTimingAnimator.setDuration((long) ((TimingSolver)currentSolver).getDuration());

                currentSolver.setSolverListener(new AnimationSolver.SolverListener() {
                    @Override
                    public void onSolverUpdate(Object arg1, Object arg2) {
                        mTimingAnimator.setInterpolator((TimeInterpolator)arg1);
                        mTimingAnimator.setDuration((long)arg2);
                    }
                });

                mTimingAnimator.setFloatValues(0,mTimingAnimEndValue);

                mTimingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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

                mTimingAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mPhysicsState.updatePhysics(mTimingAnimEndValue,0);
                        if(mListener !=null){
                            mListener.onAnimationEnd(true,mTimingAnimEndValue,0);
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
