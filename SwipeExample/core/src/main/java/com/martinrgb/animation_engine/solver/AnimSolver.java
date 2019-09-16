package com.martinrgb.animation_engine.solver;

public abstract class AnimSolver extends  Object{

    public AnimSolver(){}
    public abstract void setSolver(AnimSolver solver);
    public abstract Object getSolver();
    public abstract int getSolverMode();

    public SolverListener mListener;
    public void setSolverListener(SolverListener listener) {
        mListener = listener;
    }
    public interface SolverListener {
        void onSolverUpdate(Object arg1, Object arg2);
    }
}




