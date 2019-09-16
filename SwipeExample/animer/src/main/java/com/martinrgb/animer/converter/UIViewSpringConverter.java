package com.martinrgb.animer.converter;

public class UIViewSpringConverter extends AnSpringConverter {

    private boolean otherParaCalculation = false;

    public UIViewSpringConverter(double dampingratio,double duration) {
        super();
        calculate(dampingratio,duration,1,0);
    }

    public UIViewSpringConverter(double dampingratio,double duration,double mass,double velocity) {
        super();
        calculate(dampingratio,duration,mass,velocity);
    }

    private void calculate(double dampingRatio,double duration,double m,double v){
        mDampingRatio = dampingRatio;
        mDuration = duration;
        mMass = m;
        mVelocity = v;

        mTension = this.computeTension(mDampingRatio,mDuration,mMass);
        mStiffness = mTension;


        if(otherParaCalculation){
            mFriction = this.computeFriction(mDampingRatio,mTension,mMass);
            mDamping = mFriction;
            mBouncyTension = this.bouncyTesnionConversion(mTension);
            mBouncyFriction = this.bouncyFrictionConversion(mFriction);
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
