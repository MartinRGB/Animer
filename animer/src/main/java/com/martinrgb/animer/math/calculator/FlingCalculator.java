package com.martinrgb.animer.math.calculator;

public class FlingCalculator {

    private float mFriction;
    private float mVelocity;
    private float mDuration;
    private float mTransiton;

    public FlingCalculator(float friction,float velocity) {
        mFriction = friction*-4.2f;
        mVelocity = velocity;
        mDuration = calculate()[0];
        mTransiton = calculate()[1];
    }

    private float[] calculate() {
        float sampleScale = 1.5f;
        float maxItertation = 0;
        float maxValue = 0;
        float sampeScale = 1.5f;

        for (float i = 1 / (60 * sampleScale); i < 20.; i += 1 / (60 * sampleScale)) {

            float currentVelocity = mVelocity * (float) Math.exp(i * mFriction);
            float currentTransition = (mVelocity / mFriction) * (float) (Math.exp(mFriction * i) - 1);
            float speedThereshold = 2.3f;

            if (Math.abs(currentVelocity) <= speedThereshold) {

                maxItertation = i;
                maxValue = (currentTransition);

            }
            else{

            }

        }

        return new float[]{maxItertation, maxValue};
    }

    public float getDuration() {
        return mDuration;
    }

    public float getTransiton() {
        return mTransiton;
    }
}
