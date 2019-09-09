//package com.example.martinrgb.scrollview_test_android.scrollview;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.animation.FloatValueHolder;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.Log;
import android.view.animation.Interpolator;

public class DynamicOverScroller {

  private  FlingAnimation flingAnimation;
  private  SpringAnimation springAnimation;
  private FloatValueHolder valY;
  private FloatValueHolder velY;
  private boolean canSpringBack = false;

  public DynamicOverScroller(Context context) {
    this(context, null);
  }

  public DynamicOverScroller(Context context, Interpolator interpolator) {
    this(context, interpolator, true);
  }

  public DynamicOverScroller(Context context, Interpolator interpolator,
                             float bounceCoefficientX, float bounceCoefficientY) {
    this(context, interpolator, true);
  }

  public DynamicOverScroller(Context context, Interpolator interpolator,
                             float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) {
    this(context, interpolator, flywheel);
  }

  public DynamicOverScroller(Context context, Interpolator interpolator, boolean flywheel) {
    valY = new FloatValueHolder();
    velY = new FloatValueHolder();
    valY.setValue(0);
    flingAnimation = new FlingAnimation(valY);
    flingAnimation.setFriction(0.5f);
    flingAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
      @Override
      public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
          velY.setValue(velocity);
      }
    });

    springAnimation = new SpringAnimation(valY);
    springAnimation.setSpring(new SpringForce());
    springAnimation.getSpring().setDampingRatio(0.95f);
    springAnimation.getSpring().setStiffness(150);
    springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
      @Override
      public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
      }
    });

  }

  public final boolean isFinished() {
    return !(springAnimation.isRunning() || flingAnimation.isRunning());
  }

  public final int getCurrX() {
    return 0;
  }

  public final int getCurrY() {
    return (int) Math.round(valY.getValue());
  }

  public float getCurrVelocityY() {
    return (float) velY.getValue();
  }


  public boolean computeScrollOffset() {
    return (flingAnimation.isRunning() || springAnimation.isRunning());
  }

  public void startScroll(int startX, int startY, int dx, int dy) {
    valY.setValue(startY);
  }

  public boolean springBack(int startX, int startY, int velocityX,int velocityY,int minX, int maxX, int minY, int maxY) {

    if (startY > maxY || startY < minY) {

      valY.setValue(startY);
      springAnimation.setStartValue(startY);

      if (startY > maxY) {

        if(canSpringBack){
          flingAnimation.cancel();
          springAnimation.getSpring().setFinalPosition(maxY);
          springAnimation.setStartVelocity(velY.getValue());
          springAnimation.start();
          canSpringBack = false;
        }


      } else if (startY < minY) {

        if(canSpringBack){
          flingAnimation.cancel();
          springAnimation.getSpring().setFinalPosition(minY);
          springAnimation.setStartVelocity(velY.getValue());
          springAnimation.start();
          canSpringBack = false;
        }
      }
      return true;
    }
    return true;
  }

  public void fling(int startX, int startY, int velocityX, int velocityY,
                    int minX, int maxX, int minY, int maxY, int overX, int overY) {
    Log.e("Velocity",String.valueOf(velocityY));

    canSpringBack = true;

    float dynamicDamping = (float) mapValueFromRangeToRange(Math.abs(velocityY),0,24000,1.35,0.5);
    valY.setValue(startY);
    flingAnimation.setStartVelocity(velocityY);
    //flingAnimation.setFriction(dynamicDamping);
    flingAnimation.start();
  }

  private static double mapValueFromRangeToRange(
          double value,
          double fromLow,
          double fromHigh,
          double toLow,
          double toHigh) {
    double fromRangeSize = fromHigh - fromLow;
    double toRangeSize = toHigh - toLow;
    double valueScale = (value - fromLow) / fromRangeSize;
    return toLow + (valueScale * toRangeSize);
  }

  public void abortAnimation() {
    springAnimation.cancel();
    flingAnimation.cancel();
  }
}
