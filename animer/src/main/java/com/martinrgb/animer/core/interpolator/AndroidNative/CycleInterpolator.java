package com.martinrgb.animer.core.interpolator.AndroidNative;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class CycleInterpolator extends AnInterpolator {

    public CycleInterpolator(float cycles) {
        mCycles = cycles;
        initArgData(0,cycles,"factor",0,10);
    }

    public float getInterpolation(float input) {
        return (float)(Math.sin(2 * mCycles * Math.PI * input));
    }

    private float mCycles;

    @Override
    public void resetArgValue(int i, float value){
        setArgValue(i,value);
        if(i == 0){
            mCycles = value;
        }
    }

}
