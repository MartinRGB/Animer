package com.martinrgb.animer.converter;

public class RK4Converter extends AnSpringConverter {

    private boolean otherParaCalculation = false;

    public RK4Converter(double tension,double friction) {
        super();
        calculate(tension,friction,1,0);
    }

    public RK4Converter(double tension,double friction,double mass,double velocity) {
        super();
        calculate(tension,friction,mass,velocity);
    }

    private void calculate(double t,double f,double m,double v){

        mStiffness = t;
        mDamping = f;
        mMass = m;
        mVelocity = v;

        mTension = mStiffness;
        mFriction = mDamping;
        mDampingRatio = this.computeDampingRatio(mStiffness, mDamping,mMass);

        if(otherParaCalculation){
            mBouncyTension = this.bouncyTesnionConversion(mTension);
            mBouncyFriction = this.bouncyFrictionConversion(mFriction);
            mDuration = this.computeDuration(mTension, mFriction,mMass);
            mS = this.getParaS(mBouncyTension,0.5,200);
            mSpeed = this.computeSpeed(this.getParaS(mBouncyTension,0.5,200),0.,20.);
            mB = this.getParaB(mBouncyFriction,this.b3Nobounce(mBouncyTension), 0.01);
            mBounciness = 20*1.7*mB/0.8;
        }

    }

    public float getStiffness() {
        return (float) mStiffness;
    }

    public float getDampingRatio() {
        return (float) mDampingRatio;
    }


}
