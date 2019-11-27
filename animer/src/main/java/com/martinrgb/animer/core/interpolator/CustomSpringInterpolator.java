package com.martinrgb.animer.core.interpolator;

import android.view.animation.Interpolator;

public class CustomSpringInterpolator extends AnInterpolator{

    private float factor = 0.5f;

    public CustomSpringInterpolator(float factor) {
        this.factor = factor;
        setArg(0,(float) factor,"factor",0,10);
    }

    public CustomSpringInterpolator() {
        setArg(0,(float) 0.5,"factor",0,10);
    }

    @Override
    public float getInterpolation(float ratio) {
        if (ratio == 0.0f || ratio == 1.0f)
            return ratio;
        else {

            float value = (float) (Math.pow(2, -10 * ratio) * Math.sin((ratio - factor / 4.0d) * (2.0d * Math.PI) / factor) + 1);
            return value;
        }
    }
}