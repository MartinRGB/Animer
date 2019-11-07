package com.martinrgb.animer.math.converter;

public class OrigamiPOPConverter extends AnSpringConverter {

    private boolean otherParaCalculation = false;

    public OrigamiPOPConverter(double bounciness,double speed) {
        super();
        calculate(bounciness,speed,1,0);
    }

    public OrigamiPOPConverter(double bounciness,double speed,double mass,double velocity) {
        super();
        calculate(bounciness,speed,mass,velocity);
    }

    private void calculate(double b,double s,double m,double v){
        mBounciness = b;
        mSpeed = s;
        mMass = m;
        mVelocity = v;

        mB = this.normalize(mBounciness / 1.7, 0, 20.0);
        mB = this.projectNormal(mB, 0.0, 0.8);
        mS = this.normalize(mSpeed / 1.7, 0, 20.0);
        mBouncyTension = this.projectNormal(mS, 0.5, 200);
        mBouncyFriction = this.quadraticOutInterpolation(mB, this.b3Nobounce(this.mBouncyTension), 0.01);
        mTension = this.tensionConversion(mBouncyTension);
        mFriction = this.frictionConversion(mBouncyFriction);
        mStiffness = mTension;
        mDamping = mFriction;
        mDampingRatio = this.computeDampingRatio(mTension, mFriction,mMass);

        if(otherParaCalculation){
            mDuration = this.computeDuration(mTension, mFriction,mMass);
        }

    }

    public float getStiffness() {
        return (float) mStiffness;
    }

    public float getDampingRatio() {
        return (float) mDampingRatio;
    }


}
