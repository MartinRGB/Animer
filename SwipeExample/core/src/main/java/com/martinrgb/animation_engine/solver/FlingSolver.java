package com.martinrgb.animation_engine.solver;

public class FlingSolver extends AnimSolver {

    private float mStartVelocity = 1000.f,mFriction = 0.5f;
    private Object flingSolver;

    public FlingSolver(float velocity,float friction) {
        setStartVelocity(velocity);
        setFriction(friction);
        flingSolver= this;
    }

    // ############################################
    // Getter & Setter
    // ############################################

    public float getVelocity() {
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

    // ############################################
    // Override
    // ############################################

    @Override
    public void setSolver(AnimSolver solver){
        flingSolver = solver;
    }

    @Override
    public Object getSolver(){
        return flingSolver;
    }

    @Override
    public int getSolverMode() {
        return 0;
    }

}




