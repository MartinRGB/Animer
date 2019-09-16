package com.martinrgb.animation_engine.controller;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.martinrgb.animation_engine.solver.AnimationSolver;
import com.martinrgb.animation_engine.solver.FlingSolver;
import com.martinrgb.animation_engine.solver.SpringSolver;
import com.martinrgb.animation_engine.solver.TimingSolver;



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
    private static final SpringSolver springDefaultSolver = new SpringSolver(50,0.99f);
    private static final int FLING_SOLVER_MODE = 0;
    private static final int SPRING_SOLVER_MODE = 1;
    private static final int TIMING_SOLVER_MODE = 2;
    private static int SOLVER_MODE = -1;

    private static final int VALUE_ANIMATOR_MODE = 0;
    private static final int OBJECT_ANIMAOTR_MODE = 1;
    private int ANIMATOR_MODE = -1;

    private View view;

    // ###########################################
    // Constructor
    // ###########################################

    public AnimationController(T animatorObject) {
        mAnimatorObject = animatorObject;
        mTarget = null;
        mProperty = null;
        mPhysicsState = new PhysicsState();
        ANIMATOR_MODE = VALUE_ANIMATOR_MODE;
        setupByAnimator(mAnimatorObject);
    }

    public <K> AnimationController(K target, T animatorObject,FloatPropertyCompat<K> property) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        view = (View) target;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupByAnimator(mAnimatorObject);
    }

    public <K> AnimationController(K target, T animatorObject,FloatPropertyCompat<K> property,float to) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        view = (View) target;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupByAnimator(mAnimatorObject);
    }

    public <K> AnimationController(K target,T animatorObject, FloatPropertyCompat<K> property,float from,float to) {
        mAnimatorObject = animatorObject;
        mTarget = target;
        view = (View) target;
        mProperty = property;
        mPhysicsState = new PhysicsState(from,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        setupByAnimator(mAnimatorObject);
    }

    public <K> AnimationController(K target, AnimationSolver solver,FloatPropertyCompat<K> property, float from, float to) {
        mTarget = target;
        view = (View) target;
        mProperty = property;
        mPhysicsState = new PhysicsState(from,to);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        currentSolver = solver;
        setupBySolver(currentSolver);
    }

    // ############################################
    // Setup Solver
    // ############################################

    // TODO:MORE Accuracy Setting
    public void setSolver(AnimationSolver solver){
        currentSolver = solver;
        cancelAllAnimation();
        setupByAnimator(currentSolver);
    }

    // ############################################
    // Setup Aniamtor
    // ############################################

    private void setupBySolver(AnimationSolver solver) {
        SOLVER_MODE = solver.getSolverMode();
        switch(solver.getSolverMode())
        {
            case FLING_SOLVER_MODE:
                setupFlingAnimator(solver);
                break;
            case SPRING_SOLVER_MODE:
                setupSpringAnimator(solver);
                break;
            case TIMING_SOLVER_MODE:
                setupTimingAnimator(solver);
                break;
            default:
                break;
        }
    }

    private void setupByAnimator(Object animator){
        switch(animator.getClass().getSimpleName())
        {
            case "FlingAnimation":
                currentSolver = new FlingSolver(mStartVelocity,mFriction);
                break;
            case "SpringAnimation":
                currentSolver = new SpringSolver(mStiffness,mDampingRatio);
                break;
            case "ValueAnimator":
                currentSolver = new TimingSolver(mTimingInterpolator,(long)mTimingDuration);
                break;
            default:
                break;
        }
        setupBySolver(currentSolver);
    }

    private void setupFlingAnimator(AnimationSolver solver){
        if(mFlingAnimation == null) {
            mFlingAnimation = new FlingAnimation(new FloatValueHolder());
            mFlingAnimation.setMinimumVisibleChange(0.001f);
            mFlingAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                    mPhysicsState.updatePhysics(value, velocity);

                    if (ANIMATOR_MODE != VALUE_ANIMATOR_MODE) {
                        mProperty.setValue(mTarget, mPhysicsState.getPhysicsValue());
                    }

                    if (updateListener != null) {
                        updateListener.onUpdate(value, velocity);
                    }
                }
            });
            mFlingAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    mPhysicsState.updatePhysics(value, velocity);
                    setHardwareAcceleration(false);
                    if (endListener != null) {
                        endListener.onEnd(canceled, value, velocity);
                    }
                }
            });
        }
        attachSolverToFling(solver,mFlingAnimation);
    }

    private void attachSolverToFling(AnimationSolver solver,FlingAnimation flingAnimation){
        final FlingAnimation flingAnim = flingAnimation;
        flingAnim.setStartVelocity(((FlingSolver)solver).getVelocity());
        flingAnim.setFriction(((FlingSolver)solver).getFriction());

        solver.setSolverListener(new AnimationSolver.SolverListener() {
            @Override
            public void onSolverUpdate(Object arg1, Object arg2) {
                flingAnim.setStartVelocity((float) arg1);
                flingAnim.setFriction((float) arg2);
            }
        });
    }

    private void setupSpringAnimator(AnimationSolver solver){
        if(mSpringAnimation == null) {
            mSpringAnimation = new SpringAnimation(new FloatValueHolder());
            mSpringAnimation.setSpring(new SpringForce());
            mSpringAnimation.setMinimumVisibleChange(0.001f);
            mSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                    mPhysicsState.updatePhysics(value, velocity);

                    if (ANIMATOR_MODE != VALUE_ANIMATOR_MODE) {
                        mProperty.setValue(mTarget, mPhysicsState.getPhysicsValue());
                    }

                    if (updateListener != null) {
                        updateListener.onUpdate(value, velocity);
                    }
                }
            });
            mSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    mPhysicsState.updatePhysics(value, velocity);
                    setHardwareAcceleration(false);
                    if (endListener != null) {
                        endListener.onEnd(canceled, value, velocity);
                    }
                }
            });
        }
        attachSolverToSpring(solver,mSpringAnimation);
    }

    private void attachSolverToSpring(AnimationSolver solver,SpringAnimation springAnimation){
        final SpringAnimation springAnim = springAnimation;
        springAnim.getSpring().setStiffness(((SpringSolver)solver).getStiffness());
        springAnim.getSpring().setDampingRatio(((SpringSolver)solver).getDampingRatio());

        solver.setSolverListener(new AnimationSolver.SolverListener() {
            @Override
            public void onSolverUpdate(Object arg1, Object arg2) {
                springAnim.getSpring().setStiffness((float) arg1);
                springAnim.getSpring().setDampingRatio((float) arg2);
            }
        });
    }

    private void setupTimingAnimator(AnimationSolver solver){
        if(mTimingAnimation == null) {
            mTimingAnimation = new ObjectAnimator();
            mTimingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    mPrevVelocity = mCurrentVelocity;
                    mCurrentVelocity = (float) valueAnimator.getAnimatedValue();

                    float value = mCurrentVelocity;
                    float velocity = mCurrentVelocity - mPrevVelocity;

                    mPhysicsState.updatePhysics(value, velocity);

                    if (updateListener != null) {
                        updateListener.onUpdate(value, velocity);
                    }

                    if (ANIMATOR_MODE != VALUE_ANIMATOR_MODE) {
                        mProperty.setValue(mTarget, mPhysicsState.getPhysicsValue());
                    }

                }
            });

            mTimingAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mPhysicsState.updatePhysics(mPhysicsState.getPhysicsValue(), 0);
                    setHardwareAcceleration(false);
                    if (endListener != null) {
                        endListener.onEnd(true, mPhysicsState.getPhysicsValue(), 0);
                    }
                }
            });
        }
        attachSolverToTiming(solver,mTimingAnimation);
    }

    private void attachSolverToTiming(AnimationSolver solver,ObjectAnimator timingAnimation){
        final ObjectAnimator timingAnim = timingAnimation;
        timingAnim.setInterpolator(((TimingSolver)solver).getInterpolator());
        timingAnim.setDuration((long) ((TimingSolver)solver).getDuration());

        solver.setSolverListener(new AnimationSolver.SolverListener() {
            @Override
            public void onSolverUpdate(Object arg1, Object arg2) {
                timingAnim.setInterpolator((TimeInterpolator) arg1);
                timingAnim.setDuration((long) arg2);
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
            case FLING_SOLVER_MODE:
                mFlingAnimation.setStartValue(mPhysicsState.getStateValue("Start"));
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.setStartValue(mPhysicsState.getStateValue("Start"));
                break;
            case TIMING_SOLVER_MODE:
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
            case FLING_SOLVER_MODE:
                currentSolver = springDefaultSolver;
                setupSpringAnimator(currentSolver);
                mSpringAnimation.getSpring().setFinalPosition(mPhysicsState.getStateValue("End"));
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.getSpring().setFinalPosition(mPhysicsState.getStateValue("End"));
                break;
            case TIMING_SOLVER_MODE:
                mTimingAnimation.setFloatValues(mPhysicsState.getStateValue("Start"),mPhysicsState.getStateValue("End"));
                break;
            default:
                break;
        }
    }
    public void start(){
        setHardwareAcceleration(true);
        if(SOLVER_MODE == FLING_SOLVER_MODE)
            mFlingAnimation.start();
        else
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
            case FLING_SOLVER_MODE:
                mFlingAnimation.cancel();
                currentSolver = springDefaultSolver;
                setupSpringAnimator(currentSolver);
                if(mSpringAnimation.canSkipToEnd()){
                    mSpringAnimation.cancel();
                    mSpringAnimation.skipToEnd();
                }
                break;
            case SPRING_SOLVER_MODE:
                if(mSpringAnimation.canSkipToEnd()){
                    mSpringAnimation.cancel();
                    mSpringAnimation.skipToEnd();
                }
                break;
            case TIMING_SOLVER_MODE:
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
        setHardwareAcceleration(true);
        setCurrenetPhysicsValue(mPhysicsState.getPhysicsValue());
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                currentSolver = springDefaultSolver;
                setupSpringAnimator(currentSolver);
                mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(mPhysicsState.getStateValue(state));
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(mPhysicsState.getStateValue(state));
                break;
            case TIMING_SOLVER_MODE:
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
        setHardwareAcceleration(true);
        // ##Need Ob
        setCurrenetPhysicsValue(mPhysicsState.getPhysicsValue());
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                currentSolver = springDefaultSolver;
                setupSpringAnimator(currentSolver);
                mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(value);
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.setStartValue(mPhysicsState.getPhysicsValue());
                mSpringAnimation.setStartVelocity(mPhysicsState.getPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(value);
                break;
            case TIMING_SOLVER_MODE:
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

    private void setHardwareAcceleration(boolean enable){

        if(enable){
            if(view.getLayerType() == View.LAYER_TYPE_NONE){
                Log.e("Enabled","Enabled");
                view.setLayerType(View.LAYER_TYPE_HARDWARE,null);
            }
        }
        else{
            if(view.getLayerType() == View.LAYER_TYPE_HARDWARE){
                Log.e("Disabled","Disabled");
                view.setLayerType(View.LAYER_TYPE_NONE,null);
            }
        }

    }

    private UpdateListener updateListener;
    private EndListener endListener;

    public void setUpdateListener(UpdateListener listener) {
        updateListener = listener;
    }

    public void setEndListener(EndListener listener) {
        endListener = listener;
    }

    public interface UpdateListener {
        void onUpdate(float value, float velocity);
    }
    public interface EndListener{

        void onEnd(boolean canceled, float value, float velocity);
    }

}
