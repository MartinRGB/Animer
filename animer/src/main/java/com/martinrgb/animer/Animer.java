package com.martinrgb.animer;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.martinrgb.animer.solver.AnSolver;
import com.martinrgb.animer.solver.FlingSolver;
import com.martinrgb.animer.solver.SpringSolver;
import com.martinrgb.animer.solver.InterpolatorSolver;
import com.martinrgb.animer.state.PhysicsState;
import com.martinrgb.animer.util.AnimerProperty;


public class Animer<T> {

    public abstract static class AnProperty extends AnimerProperty<View> {
        private AnProperty(String name) {
            super(name);
        }
    }


    public static final AnProperty TRANSLATION_X = new AnProperty("translationX") {
        @Override
        public void setValue(View view, float value) {
            view.setTranslationX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getTranslationX();
        }
    };

    public static final AnProperty TRANSLATION_Y = new AnProperty("translationY") {
        @Override
        public void setValue(View view, float value) {
            view.setTranslationY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getTranslationY();
        }
    };

    public static final AnProperty TRANSLATION_Z = new AnProperty("translationZ") {
        @Override
        public void setValue(View view, float value) {
            ViewCompat.setTranslationZ(view, value);
        }

        @Override
        public float getValue(View view) {
            return ViewCompat.getTranslationZ(view);
        }
    };

    public static final AnProperty SCALE = new AnProperty("scale") {
        @Override
        public void setValue(View view, float value) {
            view.setScaleX(value);
            view.setScaleY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getScaleX();
        }
    };

    public static final AnProperty SCALE_X = new AnProperty("scaleX") {
        @Override
        public void setValue(View view, float value) {
            view.setScaleX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getScaleX();
        }
    };

    public static final AnProperty SCALE_Y = new AnProperty("scaleY") {
        @Override
        public void setValue(View view, float value) {
            view.setScaleY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getScaleY();
        }
    };

    public static final AnProperty ROTATION = new AnProperty("rotation") {
        @Override
        public void setValue(View view, float value) {
            view.setRotation(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotation();
        }
    };

    public static final AnProperty ROTATION_X = new AnProperty("rotationX") {
        @Override
        public void setValue(View view, float value) {
            view.setRotationX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotationX();
        }
    };

    public static final AnProperty ROTATION_Y = new AnProperty("rotationY") {
        @Override
        public void setValue(View view, float value) {
            view.setRotationY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotationY();
        }
    };

    public static final AnProperty X = new AnProperty("x") {
        @Override
        public void setValue(View view, float value) {
            view.setX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getX();
        }
    };

    public static final AnProperty Y = new AnProperty("y") {
        @Override
        public void setValue(View view, float value) {
            view.setY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getY();
        }
    };

    public static final AnProperty Z = new AnProperty("z") {
        @Override
        public void setValue(View view, float value) {
            ViewCompat.setZ(view, value);
        }

        @Override
        public float getValue(View view) {
            return ViewCompat.getZ(view);
        }
    };

    public static final AnProperty ALPHA = new AnProperty("alpha") {
        @Override
        public void setValue(View view, float value) {
            view.setAlpha(value);
        }

        @Override
        public float getValue(View view) {
            return view.getAlpha();
        }
    };

    private Object mTarget;
    private AnimerProperty mProperty;
    private PhysicsState mPhysicsState;

    private FlingAnimation mFlingAnimation;
    private SpringAnimation mSpringAnimation;
    private ObjectAnimator mTimingAnimation;

    private static AnSolver currentSolver;
    private static final AnSolver springDefaultSolver = SpringSolver.createAndroidSpring(50,0.99f);
    private static final int FLING_SOLVER_MODE = 0;
    private static final int SPRING_SOLVER_MODE = 1;
    private static final int TIMING_SOLVER_MODE = 2;
    private static int SOLVER_MODE = -1;

    private static final int VALUE_ANIMATOR_MODE = 0;
    private static final int OBJECT_ANIMAOTR_MODE = 1;
    private int ANIMATOR_MODE = -1;

    private boolean HARDWAREACCELERATION_IS_ENABLED = false;
    private float velocityFactor = 1.0f;

    private float minimumVisValue = 0.001f;

    // ###########################################
    // Constructor
    // ###########################################

    public <K> Animer() {
        mTarget = null;
        mProperty = null;
        mPhysicsState = new PhysicsState();
        ANIMATOR_MODE = VALUE_ANIMATOR_MODE;
        currentSolver = springDefaultSolver;
        setupBySolver(currentSolver);
    }

    public <K> Animer(AnSolver solver) {
        mTarget = null;
        mProperty = null;
        mPhysicsState = new PhysicsState();
        ANIMATOR_MODE = VALUE_ANIMATOR_MODE;
        currentSolver = solver;
        setupBySolver(currentSolver);
    }

    public <K> Animer(K target, AnSolver solver, AnimerProperty<K> property) {
        mTarget = target;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        currentSolver = solver;
        setupBySolver(currentSolver);
    }

    public <K> Animer(K target, AnSolver solver, AnimerProperty<K> property, float end) {
        mTarget = target;
        mProperty = property;
        float proertyValue = mProperty.getValue(mTarget);
        mPhysicsState = new PhysicsState(proertyValue,end);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        currentSolver = solver;
        setupBySolver(currentSolver);
    }

    public <K> Animer(K target, AnSolver solver, AnimerProperty<K> property, float start, float end) {
        mTarget = target;
        mProperty = property;
        mPhysicsState = new PhysicsState(start,end);
        ANIMATOR_MODE = OBJECT_ANIMAOTR_MODE;
        currentSolver = solver;
        setupBySolver(currentSolver);
    }


    public void setTarget(Object target) {
        this.mTarget = target;
    }

    public void setProperty(AnimerProperty mProperty) {
        this.mProperty = mProperty;
    }

    // ############################################
    // Setup Solver
    // ############################################

    // TODO:MORE Accuracy Setting
    public void setSolver(AnSolver solver){
        cancel();
        currentSolver = solver;
        setupBySolver(currentSolver);
    }

    // ############################################
    // Setup Aniamtor
    // ############################################

    private void setupBySolver(AnSolver solver) {
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

    private void setupFlingAnimator(AnSolver solver){
        if(mFlingAnimation == null) {
            mFlingAnimation = new FlingAnimation(new FloatValueHolder());
            mFlingAnimation.setMinimumVisibleChange(minimumVisValue);
            mFlingAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                    float progress = (value - getStateValue("Start"))/(getStateValue("End") - getStateValue("Start"));
                    updateCurrentPhysicsState(value,velocity,progress);
                }
            });
            mFlingAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    endCurrentPhysicsState(value,0,true);
                }
            });
        }
        attachSolverToFling(solver,mFlingAnimation);
    }

    private void attachSolverToFling(AnSolver solver, FlingAnimation flingAnimation){
        final FlingAnimation flingAnim = flingAnimation;
        flingAnim.setStartVelocity(((FlingSolver)solver).getVelocity());
        flingAnim.setFriction(((FlingSolver)solver).getFriction());

        solver.setSolverListener(new AnSolver.SolverListener() {
            @Override
            public void onSolverUpdate(Object arg1, Object arg2) {
                flingAnim.setStartVelocity((float) arg1);
                flingAnim.setFriction((float) arg2);
            }
        });
    }

    private void setupSpringAnimator(AnSolver solver){
        if(mSpringAnimation == null) {
            mSpringAnimation = new SpringAnimation(new FloatValueHolder());
            mSpringAnimation.setSpring(new SpringForce());
            mSpringAnimation.setMinimumVisibleChange(minimumVisValue);
            mSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                    float progress = (value - getStateValue("Start"))/(getStateValue("End") - getStateValue("Start"));
                    updateCurrentPhysicsState(value,velocity,progress);
                }
            });
            mSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    endCurrentPhysicsState(value,0,true);
                }
            });
        }
        attachSolverToSpring(solver,mSpringAnimation);
    }

    private void attachSolverToSpring(AnSolver solver, SpringAnimation springAnimation){
        final SpringAnimation springAnim = springAnimation;
        springAnim.getSpring().setStiffness(((SpringSolver)solver).getStiffness());
        springAnim.getSpring().setDampingRatio(((SpringSolver)solver).getDampingRatio());

        solver.setSolverListener(new AnSolver.SolverListener() {
            @Override
            public void onSolverUpdate(Object arg1, Object arg2) {
                springAnim.getSpring().setStiffness((float) arg1);
                springAnim.getSpring().setDampingRatio((float) arg2);
            }
        });
    }

    private void setupTimingAnimator(AnSolver solver){


        if(mTimingAnimation == null) {
            mTimingAnimation = new ObjectAnimator();
            mTimingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                float mCurrentVelocity,mPrevVelocity;
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    //#1

                    mCurrentVelocity = (float) valueAnimator.getAnimatedValue();

                    float value = mCurrentVelocity;
                    float velocity = mCurrentVelocity - mPrevVelocity;
                    float progress = (value - getStateValue("Start"))/(getStateValue("End") - getStateValue("Start"));

                    updateCurrentPhysicsState(value,velocity,progress);

                    mPrevVelocity = (float) valueAnimator.getAnimatedValue();
                }
            });

            mTimingAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    endCurrentPhysicsState(getCurrentPhysicsValue(),0,true);
                }
            });
        }
        attachSolverToTiming(solver,mTimingAnimation);
    }

    private void attachSolverToTiming(AnSolver solver, ObjectAnimator timingAnimation){
        final ObjectAnimator timingAnim = timingAnimation;
        timingAnim.setInterpolator(((InterpolatorSolver)solver).getInterpolator());
        timingAnim.setDuration((long) ((InterpolatorSolver)solver).getDuration());

        solver.setSolverListener(new AnSolver.SolverListener() {
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
    public void setFrom(float start){
        setStateValue("Start",start);
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                mFlingAnimation.setStartValue(getStateValue("Start"));
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.setStartValue(getStateValue("Start"));
                break;
            case TIMING_SOLVER_MODE:
                mTimingAnimation.setFloatValues(getStateValue("Start"),getStateValue("End"));
                break;
            default:
                break;
        }
    }
    public void setTo(float end){
        setStateValue("End",end);
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                SOLVER_MODE = SPRING_SOLVER_MODE;
                currentSolver = springDefaultSolver;
                setupSpringAnimator(currentSolver);
                mSpringAnimation.getSpring().setFinalPosition(getStateValue("End"));
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.getSpring().setFinalPosition(getStateValue("End"));
                break;
            case TIMING_SOLVER_MODE:
                mTimingAnimation.setFloatValues(getStateValue("Start"),getStateValue("End"));
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
        if(mFlingAnimation !=null && mFlingAnimation.isRunning()){
            mFlingAnimation.cancel();
        }

        if(mSpringAnimation !=null && mSpringAnimation.isRunning()){
            mSpringAnimation.cancel();
        }

        if(mTimingAnimation !=null && mTimingAnimation.isRunning()){
            mTimingAnimation.cancel();
        }

    }

    public boolean isRunning(){
        boolean isRunning = false;
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                if(mFlingAnimation !=null)
                    isRunning = mFlingAnimation.isRunning();
                break;
            case SPRING_SOLVER_MODE:
                if(mSpringAnimation !=null)
                    isRunning = mSpringAnimation.isRunning();
                break;
            case TIMING_SOLVER_MODE:
                if(mTimingAnimation !=null)
                    isRunning = mTimingAnimation.isRunning();
                break;
            default:
                break;
        }
        return isRunning;
    }

    public void end(){

        cancel();
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                SOLVER_MODE = SPRING_SOLVER_MODE;
                currentSolver = springDefaultSolver;
                setupSpringAnimator(currentSolver);
                if(mSpringAnimation.canSkipToEnd()){
                    mSpringAnimation.skipToEnd();
                }
                break;
            case SPRING_SOLVER_MODE:
                if(mSpringAnimation.canSkipToEnd()){
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

    public void switchToState(String state){
        cancel();
        float progress = (getStateValue(state) - getStateValue("Start"))/(getStateValue("End") - getStateValue("Start"));
        updateCurrentPhysicsState(getStateValue(state),getCurrentPhysicsVelocity(),progress);
    }

    public void animateToState(String state){
        setHardwareAcceleration(true);
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                SOLVER_MODE = SPRING_SOLVER_MODE;
                currentSolver = springDefaultSolver;
                setupSpringAnimator(currentSolver);
                mSpringAnimation.setStartValue(getCurrentPhysicsValue());
                mSpringAnimation.setStartVelocity(getCurrentPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(getStateValue(state));
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.setStartValue(getCurrentPhysicsValue());
                mSpringAnimation.setStartVelocity(getCurrentPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(getStateValue(state));
                break;
            case TIMING_SOLVER_MODE:
                mTimingAnimation.setFloatValues(getCurrentPhysicsValue(),getStateValue(state));
                mTimingAnimation.start();
                break;
            default:
                break;
        }

    }

    // ## Origami-POP-Rebound Style Animation Interface,driven by PhysicsState's Value

    // # Equal to [setCurrentValue]
    public void setCurrentValue(float value){
        float velocity = value - getCurrentPhysicsValue();
        float progress = (value - getStateValue("Start"))/(getStateValue("End") - getStateValue("Start"));
        updateCurrentPhysicsState(value,velocity*velocityFactor,progress);

    }

    // # Equal to [setEndVlaue]
    public void setEndvalue(float value){
        setHardwareAcceleration(true);
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                SOLVER_MODE = SPRING_SOLVER_MODE;
                currentSolver = springDefaultSolver;
                setupSpringAnimator(currentSolver);
                mSpringAnimation.setStartValue(getCurrentPhysicsValue());
                mSpringAnimation.setStartVelocity(getCurrentPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(value);
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.setStartValue(getCurrentPhysicsValue());
                mSpringAnimation.setStartVelocity(getCurrentPhysicsVelocity());
                mSpringAnimation.animateToFinalPosition(value);
                break;
            case TIMING_SOLVER_MODE:
                mTimingAnimation.setFloatValues(getCurrentPhysicsValue(),value);
                mTimingAnimation.start();
                break;
            default:
                break;
        }
    }

    // ############################################
    // Setup Velocity
    // ############################################

    // ## State
    public void setVelocity(float velocity){
        setCurrentPhysicsVelocity(velocity);
    }

    public void setVelocityInfluence(float factor){
        velocityFactor = factor;
    }


    // ############################################
    // minVisChange
    // ############################################


    public void setMinimumVisibleChange(float minimumVisValue) {
        this.minimumVisValue = minimumVisValue;
        switch(SOLVER_MODE)
        {
            case FLING_SOLVER_MODE:
                mFlingAnimation.setMinimumVisibleChange(minimumVisValue);
                break;
            case SPRING_SOLVER_MODE:
                mSpringAnimation.setMinimumVisibleChange(minimumVisValue);
                break;
            case TIMING_SOLVER_MODE:
                break;
            default:
                break;
        }

    }

    // ############################################
    // PhysicsState's Getter & Setter
    // ############################################



    public void setStateValue(String key,float value){
        mPhysicsState.setStateValue(key,value);
    }
    public float getStateValue(String state){
        return mPhysicsState.getStateValue(state);
    }

    // ## Value
    private void setCurrentPhysicsVelocity(float velocity){
        mPhysicsState.updatePhysicsVelocity(velocity);
    }
    private void setCurrenetPhysicsValue(float value){
        mPhysicsState.updatePhysicsValue(value);
    }

    private float getCurrentPhysicsVelocity(){
        return  mPhysicsState.getPhysicsVelocity();
    }
    private float getCurrentPhysicsValue(){
        return mPhysicsState.getPhysicsValue();
    }

    private void endCurrentPhysicsState(float value,float velocity,boolean canceled){
        setCurrenetPhysicsValue(value);
        setCurrentPhysicsVelocity(velocity);
        setHardwareAcceleration(false);
        if (endListener != null) {
            endListener.onEnd(value,velocity,canceled);
        }
    }

    // ## Mainly use this for listener-mode
    private void updateCurrentPhysicsState(float value,float velocity,float progress){
        setCurrenetPhysicsValue(value);
        setCurrentPhysicsVelocity(velocity);

        if (ANIMATOR_MODE != VALUE_ANIMATOR_MODE) {
            mProperty.setValue(mTarget, value);
        }

        if (updateListener != null) {
            updateListener.onUpdate(value,velocity,progress);
        }
    }

    public PhysicsState getCurrentPhysicsState(){
       return mPhysicsState;
    }

    // ############################################
    // Hardware Acceleration
    // ############################################

    private void setHardwareAcceleration(boolean bool){

        if(HARDWAREACCELERATION_IS_ENABLED && mTarget !=null){
            if(bool){
                if(((View)mTarget).getLayerType() == View.LAYER_TYPE_NONE){
                    ((View)mTarget).setLayerType(View.LAYER_TYPE_HARDWARE,null);
                }
            }
            else{
                if(((View)mTarget).getLayerType() == View.LAYER_TYPE_HARDWARE){
                    ((View)mTarget).setLayerType(View.LAYER_TYPE_NONE,null);
                }
            }
        }
    }

    public boolean isHardwareAccelerationEnabled() {
        return HARDWAREACCELERATION_IS_ENABLED;
    }

    public void enableHardwareAcceleration(boolean enable){
        HARDWAREACCELERATION_IS_ENABLED = enable;
    }

    // ############################################
    // TODO: Optim Actioner
    // ############################################

    private boolean ENABLE_ACTIONER_POSTION_VELOCITY = false;
    private Object mActioner;

    public void enableActionerInfluenceOnVelocity(boolean boo){
        ENABLE_ACTIONER_POSTION_VELOCITY =  boo;
    }

    private boolean getActionerInfluenceOnVelocity(){
        return ENABLE_ACTIONER_POSTION_VELOCITY;
    }

    public <K> void setActionerAndListener(K actioner,ActionTouchListener listener){
        mActioner = actioner;

        setActionTouchListener(listener);

        ((View)mActioner).setOnTouchListener(new View.OnTouchListener() {
            VelocityTracker velocityTracker;
            float initX = 0,initY = 0;
            float startX,startY,posX = 0,posY = 0,velX = 0,velY = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        if (velocityTracker == null) {
                            velocityTracker = VelocityTracker.obtain();
                        } else {
                            velocityTracker.clear();
                        }
                        velocityTracker.addMovement(motionEvent);

                        startX = view.getX() - motionEvent.getRawX();
                        startY = view.getY() - motionEvent.getRawY();

                        actionListener.onDown(view,motionEvent);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {

                        velocityTracker.addMovement(motionEvent);
                        velocityTracker.computeCurrentVelocity(1000);

                        velX = velocityTracker.getXVelocity();
                        velY = velocityTracker.getYVelocity();
                        posX = motionEvent.getRawX() + startX;
                        posY = motionEvent.getRawY() + startY;

                        actionListener.onMove(view,motionEvent,velX,velY);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        Log.e("velX",String.valueOf(velX));
                        if(getActionerInfluenceOnVelocity()){
                            setVelocity(velX);
                        }
                        actionListener.onUp(view,motionEvent);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        actionListener.onCancel(view,motionEvent);
                        break;
                    }
                }
                return  true;
            }
        });
    }
    // ############################################
    // Animation Listener
    // ############################################

    private UpdateListener updateListener;
    private EndListener endListener;
    private ActionTouchListener actionListener;

    public void setUpdateListener(UpdateListener listener) {
        updateListener = listener;
    }

    public void setEndListener(EndListener listener) {
        endListener = listener;
    }

    public void setActionTouchListener(ActionTouchListener listener) {
        actionListener = listener;
    }

    public interface UpdateListener {
        void onUpdate(float value, float velocity,float progress);
    }
    public interface EndListener{
        void onEnd(float value, float velocity,boolean canceled);
    }

    public interface ActionTouchListener{
        void onDown(View view,MotionEvent event);
        void onMove(View view,MotionEvent event,float velocityX,float velocityY);
        void onUp(View view,MotionEvent event);
        abstract void onCancel(View view,MotionEvent event);
    }

}
