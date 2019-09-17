package com.martinrgb.animer.core.math.calculator;


public class SpringInterpolatorCalculator{

    private double mStiffness,mMass,mDamping,mFactor,mTime;
    private float mEpsilon,mDuration,mVelocity;

    public SpringInterpolatorCalculator(double stiffness,double dampingratio) {
        mStiffness = stiffness;
        mMass = 1;
        mDamping = computeDamping(stiffness,dampingratio,mMass);
        mVelocity = 0;
        mEpsilon = 1/1000f;

    }

    public double computeDamping(double stiffness,double dampingRatio,double mass){
        //double mass = mMass;
        return dampingRatio * (2 * Math.sqrt(mass * stiffness));
    }

    private double computeMaxValue() {

        float time = 0;
        float value = 0;
        float velocity = mVelocity;
        float maxValue = 0;

        while (!(time > 0 && Math.abs(velocity) < mEpsilon)) {

            time += mEpsilon;
            float k = 0 - (float) mStiffness;
            float b = 0 - (float) mDamping;
            float F_spring = k * ((value) - 1);
            float F_damper = b * (velocity);

            velocity += ((F_spring + F_damper) / mMass) * mEpsilon;
            value += velocity * mEpsilon;

            if (maxValue < value) {
                maxValue = value;
            }
        }
        mDuration = time;
        return maxValue;
    }

    private double computeSpringMax(double factor) {
        double maxValue = 0;
        double epsilon = mEpsilon;
        double count = 1 / epsilon;
        for (int i = 0; i < count; i++) {
            double x = i * mEpsilon;
            double result = Math.pow(2, -10 * x) * Math.sin((x - factor / 4) * (2 * Math.PI) / factor) + 1;

            if (maxValue < result) {
                maxValue = result;
            }
        }
        return maxValue;
    }

    private double findCloseNum(double num) {
        int arraySize= (int) (1/mEpsilon);
        double[] arr = new double[arraySize];
        for (int i = 0; i < 1/mEpsilon - 1; i++) {
            arr[i] = this.computeSpringMax(i * mEpsilon);
        }
        int index = 0;
        double d_value = 10;
        for (int i = 0; i < arr.length; i++) {
            double new_d_value = Math.abs(arr[i] - num);
            if (new_d_value <= d_value) {
                if (new_d_value == d_value && arr[i] < arr[index]) {
                    continue;
                }
                index = i;
                d_value = new_d_value;
            }
        }
        return index / (1/mEpsilon);
    }


    public float getFactor(){
        return (float) findCloseNum(computeMaxValue());
    }

    public float getDuration(){
        return mDuration;
    }


}
