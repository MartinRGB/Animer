package com.martinrgb.animer.core.solver;

import android.animation.TimeInterpolator;
import android.view.animation.LinearInterpolator;


public class AnSolver extends Object{

    // ############################################
    // Construct
    // ############################################

    private static Object arg1,arg2;
    private static SolverListener mListener = null;
    private static int SOLVER_MODE = -1;

    public AnSolver(Object val1,Object val2,int mode){
        unBindSolverListener();
        setSolverMode(mode);
        setArg1(val1);
        setArg2(val2);
    }

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

    public void setArg1(Object val){
        arg1 = val;
        if(mListener !=null){
            mListener.onSolverUpdate(arg1,arg2);
        }
    }

    public Object getArg1(){
        return arg1;
    }

    public void setArg2(Object val){
        arg2 = val;
        if(mListener !=null){
            mListener.onSolverUpdate(arg1,arg2);
        }
    }

    public Object getArg2(){
        return arg2;
    }

    public int getSolverMode() {
        return SOLVER_MODE;
    }

    public void setSolverMode(int solverMode) {
        if(getSolverMode() != solverMode){
            unBindSolverListener();
            SOLVER_MODE = solverMode;
        }
    }
}




