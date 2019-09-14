package com.martinrgb.animation_engine.converter;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public abstract class SpringConverter {

    public double mMass = 1;
    public double mStiffness;
    public double mDamping;
    public double mDampingRatio;
    public double mTension;
    public double mFriction;
    public double mBouncyTension;
    public double mBouncyFriction;
    public double mDuration;
    public double mS;
    public double mB;
    public double mBounciness;
    public double mSpeed;
    public double mVelocity;

    public SpringConverter() {

    }


    public double computeDamping(double stiffness,double dampingRatio,double mass){
        //double mass = mMass;
        return dampingRatio * (2 * Math.sqrt(mass * stiffness));
    }

    public double computeDampingRatio(double tension,double friction,double mass) {
        //double mass = mMass;
        return friction / (2 * Math.sqrt(mass * tension));
    }

    public double computeDuration(double tension, double friction,double mass) {
        double epsilon = 0.001;
        double velocity = 0.0;
        //double mass = mMass;
        double dampingRatio = this.computeDampingRatio(tension, friction,mass);
        double undampedFrequency = Math.sqrt(tension / mass);
        if (dampingRatio < 1) {
            double a = Math.sqrt(1 - Math.pow(dampingRatio, 2));
            double b = velocity / (a * undampedFrequency);
            double c = dampingRatio / a;
            double d = -((b - c) / epsilon);
            if (d <= 0) {
                return 0.0;
            }
            return Math.log(d) / (dampingRatio * undampedFrequency);
        } else {
            return 0.0;
        }
    }

    public double computeTension(double dampingratio,double duration,double mass) {
        //let mass = this.mass;
        double a = Math.sqrt(1 - Math.pow(dampingratio, 2));
        double d = (dampingratio/a)*1000.;
        double tension = Math.pow(Math.log(d)/(dampingratio * duration),2)*mass;
        return tension;
    }

    public double computeFriction(double dampingratio,double tension,double mass){
        //let mass = this.mass;
        double a = (2 * Math.sqrt(mass * tension));
        double friction = dampingratio * a;
        return friction;
    }

    public double tensionConversion(double oValue) {
        return (oValue - 30.0) * 3.62 + 194.0;
    }

    public double frictionConversion(double oValue) {
        return (oValue - 8.0) * 3.0 + 25.0;
    }

    public double bouncyTesnionConversion(double tension){
        return (tension - 194.0)/3.62 + 30.;
    }

    public double bouncyFrictionConversion(double friction){
        return (friction - 25.)/3. + 8.;
    }

    public double getParaS(double n,double start,double end){
        return (n - start)/(end - start);
    }

    public double getParaB(double finalVal, double start,double end) {

        double a = 1;
        double b = -2;
        double c = (finalVal - start)/(end-start);

        double root_part = Math.sqrt(b * b - 4 * a * c);
        double denom = 2 * a;

        double root1 = ( -b + root_part ) / denom;
        double root2 = ( -b - root_part ) / denom;


        if(root2 <0) return root1;
        if(root1 <0) return root2;
        return Math.min(root1,root2);

    }

    public double computeSpeed(double value,double startValue,double endValue){
        return (value * (endValue - startValue) + startValue)*1.7 ;
    }

    public double normalize(double value, double startValue, double endValue) {
        return (value - startValue) / (endValue - startValue);
    }

    public double projectNormal(double n, double start, double end) {
        return start + (n * (end - start));
    }

    public double linearInterpolation(double t, double start, double end) {
        return t * end + (1.0 - t) * start;
    }

    public double quadraticOutInterpolation(double t, double start, double end) {
        return linearInterpolation(2 * t - t * t, start, end);
    }



    public double b3Friction1(double x) {
        return (0.0007 * Math.pow(x, 3)) -
                (0.031 * Math.pow(x, 2)) + 0.64 * x + 1.28;
    }

    public double b3Friction2(double x) {
        return (0.000044 * Math.pow(x, 3)) -
                (0.006 * Math.pow(x, 2)) + 0.36 * x + 2.;
    }

    public double b3Friction3(double x) {
        return (0.00000045 * Math.pow(x, 3)) -
                (0.000332 * Math.pow(x, 2)) + 0.1078 * x + 5.84;
    }

    public double b3Nobounce(double tension) {
        double friction = 0;
        if (tension <= 18) {
            friction = this.b3Friction1(tension);
        } else if (tension > 18 && tension <= 44) {
            friction = this.b3Friction2(tension);
        } else {
            friction = this.b3Friction3(tension);
        }
        return friction;
    }
}
