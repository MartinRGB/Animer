package com.martinrgb.animer.component.scrollview;

import android.content.Context;
import android.util.Log;
import android.view.animation.Interpolator;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.math.calculator.FlingCalculator;

public class AnOverScroller {

    private FlingAnimation flingAnimation;
    private SpringAnimation springAnimation;
    private Animer flingAnimer;
    private Animer springAnimer;
    private Animer springAsFlingAnimer;
    private FloatValueHolder scrollValue;
    private FloatValueHolder scrollSpeed;
    private boolean canSpringBack = true;
    private boolean isDyanmicDamping = false;
    private boolean isVertical = true;
    private boolean isFixedScroll = false;
    private float fixedCellWidth = 0;
    private final Animer.AnimerSolver defaultSpring = Animer.springDroid(150,0.99f);
    private final Animer.AnimerSolver defaultFling = Animer.flingDroid(4000,0.8f);
    private final Animer.AnimerSolver springAsFling = Animer.springDroid(50,0.99f);



    public AnOverScroller(Context context) {
        this(context, null);
    }

    public AnOverScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public AnOverScroller(Context context, Interpolator interpolator,
                          float bounceCoefficientX, float bounceCoefficientY) {
        this(context, interpolator, true);
    }

    public AnOverScroller(Context context, Interpolator interpolator,
                          float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) {
        this(context, interpolator, flywheel);
    }

    public AnOverScroller(Context context, Interpolator interpolator, boolean flywheel) {

        springAnimer = new Animer();
        springAnimer.setSolver(defaultSpring);

        flingAnimer = new Animer();
        flingAnimer.setSolver(defaultFling);

        springAsFlingAnimer = new Animer();
        springAsFlingAnimer.setSolver(springAsFling);

        scrollValue = new FloatValueHolder();
        scrollSpeed = new FloatValueHolder();
        scrollValue.setValue(0);
        flingAnimation = new FlingAnimation(scrollValue);
        flingAnimation.setFriction((float)flingAnimer.getArgument2());
        flingAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                scrollSpeed.setValue(velocity);
            }
        });

        springAnimation = new SpringAnimation(scrollValue);
        springAnimation.setSpring(new SpringForce());
        springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
        springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());
        springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                if(isSpringBack()){
                    setSpringBack(false);
                }
                scrollSpeed.setValue(velocity);
            }
        });
        springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                setSpringBack(true);
                scrollSpeed.setValue(0);
            }
        });

    }

    public final int getCurrX() {
        if(scrollerisVertScroll()){
            return 0;
        }
        else {
            return Math.round(scrollValue.getValue());
        }
    }

    public final int getCurrY() {
        if(scrollerisVertScroll()){
            return Math.round(scrollValue.getValue());
        }
        else {
            return 0;
        }

    }

    public float getCurrVelocityY() {
        if(scrollerisVertScroll()){
            return (float) scrollSpeed.getValue();
        }
        else {
            return 0;
        }
    }

    public float getCurrVelocityX() {
        if(scrollerisVertScroll()){
            return 0;
        }
        else {
            return (float) scrollSpeed.getValue();
        }
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        if(scrollerisVertScroll()){
            scrollValue.setValue(startY);
        }
        else {
            scrollValue.setValue(startX);
        }
    }

    public boolean springBack(int startX, int startY, int velocityX,int velocityY,int minX, int maxX, int minY, int maxY) {

        Log.e("springBack","SpringBack");
        Log.e("speed",String.valueOf(scrollSpeed.getValue()));
        if(scrollerisVertScroll()){

            if (startY > maxY || startY < minY) {

                if(!isFixedScroll){
                    flingAnimation.cancel();


                    if (isSpringBack()) {
                        scrollValue.setValue(startY);
                        springAnimation.setStartValue(startY);
                        springAnimation.setStartVelocity(scrollSpeed.getValue());

                        springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
                        springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

                        if (startY > maxY) {
                            springAnimation.getSpring().setFinalPosition(maxY);
                        } else if (startY < minY) {
                            springAnimation.getSpring().setFinalPosition(minY);
                        }
                        springAnimation.start();
                    }
                }
                else {
                    scrollValue.setValue(startY);
                    springAnimation.setStartValue(startY);
                    springAnimation.setStartVelocity(scrollSpeed.getValue());

                    springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
                    springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

                    if (startY > maxY) {
                        springAnimation.getSpring().setFinalPosition(maxY);
                    } else if (startY < minY) {
                        springAnimation.getSpring().setFinalPosition(minY);
                    }
                    springAnimation.start();
                }

                return true;
            }

        }
        else {
            if (startX > maxX || startX < minX) {

                if(!isFixedScroll){
                    flingAnimation.cancel();

                    if (isSpringBack()) {
                        scrollValue.setValue(startX);
                        springAnimation.setStartValue(startX);
                        springAnimation.setStartVelocity(scrollSpeed.getValue());

                        springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
                        springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

                        if (startX > maxX) {
                            springAnimation.getSpring().setFinalPosition(maxX);
                        } else if (startX < minX) {
                            springAnimation.getSpring().setFinalPosition(minX);
                        }
                        springAnimation.start();
                    }
                }
                else {

                    scrollValue.setValue(startX);
                    springAnimation.setStartValue(startX);
                    springAnimation.setStartVelocity(scrollSpeed.getValue());

                    springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
                    springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

                    if (startX > maxX) {
                        springAnimation.getSpring().setFinalPosition(maxX);
                    } else if (startX < minX) {
                        springAnimation.getSpring().setFinalPosition(minX);
                    }
                    springAnimation.start();

                }

                return true;
            }
        }

        return true;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY, int overX, int overY) {

        if(isFixedScroll()){
            FlingCalculator flingCalculator;
            if(scrollerisVertScroll()){
                flingCalculator = new FlingCalculator(velocityY,(float)flingAnimer.getArgument2());
            }
            else {
                flingCalculator = new FlingCalculator(velocityX,(float)flingAnimer.getArgument2());
            }

            float flingTransition = flingCalculator.getTransiton();

            if(scrollerisVertScroll()){
                springAnimation.setStartVelocity(velocityY);
                scrollValue.setValue(startY);
                springAnimation.setStartValue(startY);
            }
            else {
                springAnimation.setStartVelocity(velocityX);
                scrollValue.setValue(startX);
                springAnimation.setStartValue(startX);
            }

            springAnimation.getSpring().setStiffness((float)springAsFlingAnimer.getArgument1());
            springAnimation.getSpring().setDampingRatio((float)springAsFlingAnimer.getArgument2());

            if(scrollerisVertScroll()){
                float roundValue = Math.round(((startY + flingTransition)/fixedCellWidth))*fixedCellWidth;
                springAnimation.getSpring().setFinalPosition(roundValue);
            }
            else {
                float roundValue = Math.round(((startX + flingTransition)/fixedCellWidth))*fixedCellWidth;
                springAnimation.getSpring().setFinalPosition(roundValue);
            }
            springAnimation.start();

        }
        else {
            if(scrollerisVertScroll()){
                flingAnimation.setStartVelocity(velocityY);
                scrollValue.setValue(startY);
            }
            else {
                flingAnimation.setStartVelocity(velocityX);
                scrollValue.setValue(startX);
            }

            if(getDynamicFlingFrictionState()){
                float dynamicDamping = (scrollerisVertScroll())? (float) mapValueFromRangeToRange(Math.abs(velocityY),0,24000,1.35,0.5) :  (float) mapValueFromRangeToRange(Math.abs(velocityX),0,24000,1.35,0.5);
                flingAnimation.setFriction(dynamicDamping);
            }
            else {
                flingAnimation.setFriction((float)flingAnimer.getArgument2());
            }
            flingAnimation.start();
        }

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

    public boolean computeScrollOffset() {
        return (flingAnimation.isRunning() || springAnimation.isRunning());
    }

    public final boolean isFinished() {
        return !(springAnimation.isRunning() || flingAnimation.isRunning());
    }

    public void abortAnimation() {
        springAnimation.cancel();
        flingAnimation.cancel();
    }

    public void setDynamicFlingFrictionState(boolean dynamicDampingState){
        isDyanmicDamping = dynamicDampingState;
    }

    public boolean getDynamicFlingFrictionState(){
        return isDyanmicDamping;
    }

    private void setSpringBack(boolean triggered) {
        canSpringBack = triggered;
    }

    private boolean isSpringBack() {
        return canSpringBack;
    }

    public Animer getSpringAnimer(){
        return springAnimer;
    }
    public Animer getFlingAnimer(){
        return flingAnimer;
    }
    public Animer getFakeFlingAnimer(){
        return springAsFlingAnimer;
    }
    public void setVertScroll(boolean is_vertical) {
        this.isVertical = is_vertical;
    }
    private boolean scrollerisVertScroll(){
        return this.isVertical;
    }

    public void setFixedScroll(boolean fixedScroll,float cellWidth){
        isFixedScroll = fixedScroll;
        fixedCellWidth = cellWidth;
    }
    private boolean isFixedScroll(){
        return  isFixedScroll;
    }

}
