package com.martinrgb.animer.core.interpolator;

public class CustomMocosSpringInterpolator extends AnInterpolator{

    private double mGamma, mVDiv2;
    private boolean mOscilative;
    private double mEps;

    private double mA, mB;
    private double mDuration;
    private double tension,damping,velocity;

    public CustomMocosSpringInterpolator(double tension, double damping) {
        this(tension, damping, 0.001);
        this.setInitialVelocity(20.);

        setArgData(0,(float) tension,"tension",0,200);
        setArgData(1,(float) damping,"damping",0,100);
        setArgData(2,(float) 20,"velocity",0,1000);
    }

    public CustomMocosSpringInterpolator(double tension, double damping, double velocity) {
        //mEps = eps;
        this.tension = tension;
        this.damping = damping;
        this.velocity = velocity;
        init();


        setArgData(0,(float) tension,"tension",0,200);
        setArgData(1,(float) damping,"damping",0,100);
        setArgData(2,(float) velocity,"velocity",0,1000);
    }

    private void init(){
        mEps = 0.001;
        mOscilative = (4 * this.tension - this.damping * this.damping > 0);
        if (mOscilative) {
            mGamma = Math.sqrt(4 * this.tension - this.damping * this.damping) / 2;
            mVDiv2 = this.damping / 2;
        } else {
            mGamma = Math.sqrt(this.damping * this.damping - 4 * this.tension) / 2;
            mVDiv2 = this.damping / 2;
        }
        this.setInitialVelocity(velocity);
    }

    @Override
    public void resetData(int i,float value){
        setArgValue(i,value);
        if(i == 0){
            this.tension = (double) value;
            init();
        }
        if(i == 1){
            this.damping = (double) value;
            init();
        }
        if(i == 2){
            this.velocity = (double) value;
            init();
        }
    }

    public void setInitialVelocity(double v0) {
        if (mOscilative) {
            mB = Math.atan(-mGamma / (v0 - mVDiv2));
            mA = -1 / Math.sin(mB);
            mDuration = Math.log(Math.abs(mA) / mEps) / mVDiv2;
        } else {
            mA = (v0 - (mGamma + mVDiv2)) / (2 * mGamma);
            mB = -1 - mA;
            mDuration = Math.log(Math.abs(mA) / mEps) / (mVDiv2 - mGamma);
        }
    }

    public double getDesiredDuration() {
        return mDuration;
    }

    @Override
    public float getInterpolation(float input) {
        if (input >= 1) {
            return 1;
        }
        double t = input * mDuration;
        return (float) (mOscilative ?
                (mA * Math.exp(-mVDiv2 * t) * Math.sin(mGamma * t + mB) + 1) :
                (mA * Math.exp((mGamma - mVDiv2) * t) + mB * Math.exp(-(mGamma + mVDiv2) * t) + 1));
    }
}