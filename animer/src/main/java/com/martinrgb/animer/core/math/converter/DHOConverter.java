package com.martinrgb.animer.core.math.converter;

public class DHOConverter extends AnSpringConverter {

    private boolean otherParaCalculation = false;

    public DHOConverter(double stiffness,double damping) {
        super();
        calculate(stiffness,damping,1,0);
    }

    public DHOConverter(double stiffness,double damping,double mass,double velocity) {
        super();
        calculate(stiffness,damping,mass,velocity);
    }

    private void calculate(double s,double d,double m,double v){

        mStiffness = s;
        mDamping = d;
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
