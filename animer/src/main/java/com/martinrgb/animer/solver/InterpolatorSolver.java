package com.martinrgb.animer.solver;

import android.animation.TimeInterpolator;

public class InterpolatorSolver extends AnSolver {

    private TimeInterpolator mInterpolator;
    private long mDuration;
    private Object timingSolver;

    private InterpolatorSolver(TimeInterpolator interpolator, long duration) {
        setInerpolator(interpolator);
        setDuration(duration);
        timingSolver= this;
    }

    // ############################################
    // Interpolator Animation Solver's Converter
    // ############################################

    public static AnSolver createAndroidInterpolator(TimeInterpolator interpolator, long duration){
        return new InterpolatorSolver(interpolator,duration);
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
    public void setSolver(AnSolver solver){
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




