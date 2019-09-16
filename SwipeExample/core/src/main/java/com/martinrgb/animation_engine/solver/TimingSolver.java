package com.martinrgb.animation_engine.solver;

import android.animation.TimeInterpolator;

public class TimingSolver extends AnimSolver {

    private TimeInterpolator mInterpolator;
    private long mDuration;
    private Object timingSolver;

    public TimingSolver(TimeInterpolator interpolator, long duration) {
        setInerpolator(interpolator);
        setDuration(duration);
        timingSolver= this;
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

    // ############################################
    // Override
    // ############################################

    @Override
    public void setSolver(AnimSolver solver){
        timingSolver = solver;
    }

    @Override
    public Object getSolver(){
        return timingSolver;
    }

    @Override
    public int getSolverMode() {
        return 2;
    }
}




