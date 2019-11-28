package com.martinrgb.animer.core.math.converter;

public class AndroidSpringConverter extends AnSpringConverter {

    private boolean otherParaCalculation = false;

    public AndroidSpringConverter(double stiffness,double dampingratio) {
        super();
        calculate(stiffness,dampingratio,1,0);
    }

    public AndroidSpringConverter(double stiffness,double dampingratio,double mass,double velocity) {
        super();
        calculate(stiffness,dampingratio,mass,velocity);
    }

    private void calculate(double s,double d,double m,double v){

        mStiffness = s;
        mDampingRatio = d;
        mMass = m;
        mVelocity = v;



        mDamping = this.computeDamping(mStiffness, mDampingRatio,mMass);
        mTension = mStiffness;
        mFriction = mDamping;

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


}
