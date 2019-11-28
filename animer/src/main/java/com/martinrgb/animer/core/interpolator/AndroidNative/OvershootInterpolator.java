package com.martinrgb.animer.core.interpolator.AndroidNative;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class OvershootInterpolator extends AnInterpolator {



    private float mTension;

    public OvershootInterpolator() {
        mTension = 2.0f;
        initArgData(0,2,"factor",0,10);
    }

    public OvershootInterpolator(float tension) {
        mTension = tension;
        initArgData(0,tension,"factor",0,10);
    }


    public float getInterpolation(float t) {
        // _o(t) = t * t * ((tension + 1) * t + tension)
        // o(t) = _o(t - 1) + 1
        t -= 1.0f;
        return t * t * ((mTension + 1) * t + mTension) + 1.0f;
    }

    @Override
    public void resetArgValue(int i, float value){
        setArgValue(i,value);
        if(i == 0){
            mTension = value;
        }
    }

}
