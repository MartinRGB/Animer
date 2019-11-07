package com.martinrgb.animer.core.solver;

import android.animation.TimeInterpolator;


public class AnSolver extends Object{

    // ############################################
    // Construct
    // ############################################

    public AnSolver(){

    }

    private static SolverListener mListener = null;
    public interface SolverListener {
        void onSolverUpdate(Object arg1, Object arg2);
    }
    public static void bindSolverListener(SolverListener listener) {
        mListener = listener;
    }
    public static void unBindSolverListener(){
        if(mListener !=null){
            mListener = null;
        }
    }

    // ############################################
    // Fling Solver
    // ############################################

    public static class FlingSolver extends AnSolver {

        private float mStartVelocity,mFriction;

        public FlingSolver(float velocity,float friction) {
            setStartVelocity(velocity);
            setFriction(friction);
            //setSolverMode(0);
        }

        // ############################################
        // Getter & Setter
        // ############################################

        public float getStartVelocity() {
            return mStartVelocity;
        }

        public float getFriction() {
            return mFriction;
        }

        public void setStartVelocity(float velocity){
            mStartVelocity = velocity;
            if(mListener !=null){
                mListener.onSolverUpdate(mStartVelocity,mFriction);
            }
        }

        public void setFriction(float friction){
            mFriction = friction;
            if(mListener !=null){
                mListener.onSolverUpdate(mStartVelocity,mFriction);
            }
        }

    }

    // ############################################
    // Spring Solver
    // ############################################

    public static class SpringSolver extends AnSolver {

        private float mStiffness,mDampingRatio;

        public SpringSolver(float stiffness,float dampingratio) {
            setStiffness(stiffness);
            setDampingRatio(dampingratio);
            //setSolverMode(1);
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
    }

    // ############################################
    // Interpolator Solver
    // ############################################

    public static class InterpolatorSolver extends AnSolver {

        private TimeInterpolator mInterpolator;
        private long mDuration;

        public InterpolatorSolver(TimeInterpolator interpolator, long duration) {
            setInerpolator(interpolator);
            setDuration(duration);
            //setSolverMode(2);
        }

        // ############################################
        // Getter & Setter
        // ############################################

        public TimeInterpolator getInterpolator() {
            return mInterpolator;
        }

        public long getDuration() {
            return mDuration;
        }

        public void setInerpolator(TimeInterpolator interpolator){
            mInterpolator = interpolator;
            if(mListener !=null){
                mListener.onSolverUpdate(mInterpolator,mDuration);
            }
        }

        public void setDuration(long duration){
            mDuration = duration;
            if(mListener !=null){
                mListener.onSolverUpdate(mInterpolator,mDuration);
            }
        }

    }
}




