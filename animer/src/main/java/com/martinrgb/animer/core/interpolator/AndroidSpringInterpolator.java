package com.martinrgb.animer.core.interpolator;

// Interpolator Version of Android's SpringAnimation

import android.util.Log;

import com.martinrgb.animer.core.math.calculator.SpringInterpolatorCalculator;

public class AndroidSpringInterpolator extends AnInterpolator{

    //Parameters
    private float mStiffness = 1500.f;
    private float mDampingRatio = 0.5f;
    private float mVelocity = 0.f;
    private float mDuration = 1.f;
    private float mLastPlacement = 0.f;



    public AndroidSpringInterpolator(float stiffness, float dampingratio,float velocity,float duration) {

        this.mStiffness = stiffness;
        this.mDampingRatio = dampingratio;
        this.mVelocity = velocity;
        this.mDuration = duration/1000.f;
        //this.mDuration = new SpringInterpolatorCalculator(stiffness,dampingratio).getDuration();

        initArgData(0,(float) stiffness,"stiffness",0.01f,3000);
        initArgData(1,(float) dampingratio,"dampingratio",0.01f,3000);
        initArgData(2,(float) velocity,"velocity",-5000,5000);
        initArgData(3,(float) duration,"duration",0,5000);
    }

    public AndroidSpringInterpolator(float stiffness, float dampingratio,float duration) {
        this.mStiffness = stiffness;
        this.mDampingRatio = dampingratio;
        this.mVelocity = 0.f;
        this.mDuration = duration/1000.f;
        //this.mDuration = new SpringInterpolatorCalculator(stiffness,dampingratio).getDuration();

        initArgData(0,(float) stiffness,"stiffness",0.01f,3000);
        initArgData(1,(float) dampingratio,"dampingratio",0.01f,1);
        initArgData(2,(float) 0,"velocity",-5000,5000);
        initArgData(3,(float) duration,"duration",0,5000);
    }

    @Override
    public void resetArgValue(int i, float value){
        setArgValue(i,value);
        if(i == 0){
            mStiffness = value;
        }
        if(i == 1){
            mDampingRatio = value;
        }
        if(i == 2){
            mVelocity = value;
        }
        if(i == 3){
            mDuration = value/1000;
        }
    }


    @Override
    public float getInterpolation(float ratio) {
        if (ratio == 0.0f || ratio == 1.0f)
            return ratio;
        else {
            float deltaT = ratio * mDuration;
            float starVal = 0;
            float endVal = 1;

            float mNaturalFreq = (float) Math.sqrt(mStiffness);
            float mDampedFreq = (float)(mNaturalFreq*Math.sqrt(1.0 - mDampingRatio* mDampingRatio));
            float lastVelocity =  mVelocity;
            //float lastDisplacement  = ratio - endVal* deltaT/60 - endVal;
            float lastDisplacement  = ratio -  endVal;
            float coeffB = (float) (1.0 / mDampedFreq * (mDampingRatio * mNaturalFreq * lastDisplacement + lastVelocity));
            float displacement = (float) (Math.pow(Math.E,-mDampingRatio * mNaturalFreq * deltaT) * (lastDisplacement * Math.cos(mDampedFreq * deltaT) + coeffB * Math.sin(mDampedFreq * deltaT)));
            float mValue = displacement + endVal;

            Log.e("ratio",String.valueOf(displacement));
            if(mDuration == 0){
                return starVal;
            }
            else{
                return mValue;
            }
        }
    }
}