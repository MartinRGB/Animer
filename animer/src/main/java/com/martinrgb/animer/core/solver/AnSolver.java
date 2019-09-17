package com.martinrgb.animer.core.solver;

public abstract class AnSolver extends  Object{

    public AnSolver(){}
    public abstract void setSolver(AnSolver solver);
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




