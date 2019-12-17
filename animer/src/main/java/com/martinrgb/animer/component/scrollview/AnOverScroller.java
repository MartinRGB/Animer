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
    private Animer flingAnimer,springAnimer,flingSpringAnimer,scrollAnimer;
    private FloatValueHolder scrollValue,scrollSpeed;
    private boolean isDyanmicFling = false,isVertScroll = true,isFixedScroll = false;
    private float fixedCellWidth = 0;
    private final Animer.AnimerSolver defaultSpring = Animer.springDroid(150,0.99f);
    private final Animer.AnimerSolver defaultFling = Animer.flingDroid(4000,0.8f);
    private final Animer.AnimerSolver springAsFling = Animer.springDroid(50,0.99f);

    private boolean isFling = false;
    private boolean isSpringBack = false;
    private boolean isAnimerDriven = false;

    // ############################################
    // Constructor
    // ############################################

    public AnOverScroller(Context context) {
        this(context, null);
    }

    public AnOverScroller(Context context, Interpolator interpolator) {this(context, interpolator, true);}

    public AnOverScroller(Context context, Interpolator interpolator,float bounceCoefficientX, float bounceCoefficientY) {this(context, interpolator, true); }

    public AnOverScroller(Context context, Interpolator interpolator, float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) { this(context, interpolator, flywheel); }

    public AnOverScroller(Context context, Interpolator interpolator, boolean flywheel) {

        if(!isAnimerDriven()){
            springAnimer = new Animer();
            springAnimer.setSolver(defaultSpring);

            flingAnimer = new Animer();
            flingAnimer.setSolver(defaultFling);

            flingSpringAnimer = new Animer();
            flingSpringAnimer.setSolver(springAsFling);

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

        if(isAnimerDriven()){
            scrollValue = new FloatValueHolder();
            scrollSpeed = new FloatValueHolder();
            scrollValue.setValue(0);

            springAnimer = new Animer();
            springAnimer.setSolver(defaultSpring);

            flingSpringAnimer = new Animer();
            flingSpringAnimer.setSolver(springAsFling);

            flingAnimer = new Animer();
            flingAnimer.setSolver(defaultFling);

            scrollAnimer = new Animer(scrollValue.getValue());
            scrollAnimer.setArgument1(springAnimer.getArgument1());
            scrollAnimer.setArgument2(springAnimer.getArgument2());

            scrollAnimer.setUpdateListener(new Animer.UpdateListener() {
                @Override
                public void onUpdate(float value, float velocity, float progress) {
                    if(isSpringBack()){
                        setSpringBack(false);
                    }
                    scrollValue.setValue(value);
                    scrollSpeed.setValue(velocity);
                }
            });

            scrollAnimer.setEndListener(new Animer.EndListener() {
                @Override
                public void onEnd(float value, float velocity, boolean canceled) {
                    setSpringBack(true);
                    scrollValue.setValue(value);
                    scrollSpeed.setValue(0);
                }
            });
        }

    }

    // ############################################
    // Value Getter
    // ############################################

    public final int getCurrX() {
        return (isVertScroll())?0:Math.round(scrollValue.getValue());
    }

    public final int getCurrY() {
        return (isVertScroll())?Math.round(scrollValue.getValue()):0;
    }

    public float getCurrVelocityY() {
        return (isVertScroll())?scrollSpeed.getValue():0;
    }

    public float getCurrVelocityX() {
        return (isVertScroll())?0:scrollSpeed.getValue();
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        scrollValue.setValue((isVertScroll())?startY:startX);
    }

    public boolean computeScrollOffset() {
        return (!isAnimerDriven())?(flingAnimation.isRunning() || springAnimation.isRunning()):scrollAnimer.isRunning();
    }

    public final boolean isFinished() {
        return (!isAnimerDriven())?!(flingAnimation.isRunning() || springAnimation.isRunning()):!scrollAnimer.isRunning();
    }

    public void abortAnimation() {
        if(!isAnimerDriven()){
            springAnimation.cancel();
            flingAnimation.cancel();
        }
        if(isAnimerDriven()) {
            scrollAnimer.cancel();
        }
    }

    // ############################################
    // SpringBack Functions
    // ############################################

    public boolean springBack(int startX, int startY, int velocityX,int velocityY,int minX, int maxX, int minY, int maxY) {

        float start =  (isVertScroll())?startY:startX;
        float min = (isVertScroll())?minY:minX;
        float max = (isVertScroll())?maxY:maxX;

        if (start > max || start < min) {
            if (!isAnimerDriven() && !isFixedScroll()) {
                if (isFling()) {
                    flingAnimation.cancel();
                    setFling(false);
                }

                if (isSpringBack()) {
                    springFunctions(start, min, max);
                }
            }

            if (!isAnimerDriven() && isFixedScroll()) {
                if (isFling()) {
                    float tempSpeed = scrollSpeed.getValue();
                    springAnimation.cancel();
                    scrollSpeed.setValue(tempSpeed);
                    setFling(false);
                }

                if (isSpringBack()) {
                    springFunctions(start, min, max);
                }
            }

            if (isAnimerDriven() && !isFixedScroll()) {
                if (isFling()) {
                    float tempSpeed = scrollSpeed.getValue();
                    scrollAnimer.cancel();
                    scrollSpeed.setValue(tempSpeed);
                    setFling(false);
                }

                if (isSpringBack()) {
                    springFunctions(start, min, max);
                }
            }

            if (isAnimerDriven() && isFixedScroll()) {
                if (isFling()) {
                    float tempSpeed = scrollSpeed.getValue();
                    scrollAnimer.cancel();
                    scrollSpeed.setValue(tempSpeed);
                    setFling(false);
                }

                if (isSpringBack()) {
                    springFunctions(start, min, max);
                }
            }

            return true;
        }

        return true;
    }

    private  int i = 0;

    private void springFunctions(float val,float min,float max){

        //Log.e("i",String.valueOf(i++));
        if(!isAnimerDriven()){
            scrollValue.setValue(val);
            springAnimation.setStartValue(val);
            springAnimation.setStartVelocity(scrollSpeed.getValue());

            springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
            springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

            springAnimation.getSpring().setFinalPosition(((val > max))?max:min);
            springAnimation.start();
        }else {

            // TODO: When fixedScroll, velocity is not correct;
            // setFrom | setVelocity didnt work cause when start() triggered:
            // mSpringAnimation.setStartValue(getCurrentPhysicsValue());
            // mSpringAnimation.setStartVelocity(getCurrentPhysicsVelocity());
            // mSpringAnimation.animateToFinalPosition(getStateValue(state));
            // scrollAnimer.setFrom(val);
            // scrollAnimer.setVelocity(scrollSpeed.getValue());

            // TODO: Fix bug in Animer.java
//            scrollValue.setValue(val);
//            scrollAnimer.setFrom(val);
//            scrollAnimer.setVelocity(scrollSpeed.getValue());

            scrollAnimer.setSolver(springAnimer.getCurrentSolver());

//            scrollAnimer.setTo(((val > max))?max:min);

            scrollAnimer.getCurrentPhysicsState().updatePhysicsValue(val);
            scrollAnimer.getCurrentPhysicsState().updatePhysicsVelocity(scrollSpeed.getValue());

            //scrollAnimer.start();
            scrollAnimer.setEndValue(((val > max))?max:min);
        }

    }

    // ############################################
    // Fling Functions
    // ############################################

    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY, int overX, int overY) {

        setFling(true);
        setSpringBack(true);

        float startVelocity = (isVertScroll())?velocityY:velocityX;
        float startValue =  (isVertScroll())?startY:startX;


        if (!isAnimerDriven() && !isFixedScroll()) {
            flingAnimation.setStartVelocity(startVelocity);
            scrollValue.setValue(startValue);

            if (isDynamicFlingFriction()) {
                float dynamicDamping = (isVertScroll()) ? (float) mapValueFromRangeToRange(Math.abs(velocityY), 0, 24000, 1.35, 0.5) : (float) mapValueFromRangeToRange(Math.abs(velocityX), 0, 24000, 1.35, 0.5);
                flingAnimation.setFriction(dynamicDamping);
            } else {
                flingAnimation.setFriction((float) flingAnimer.getArgument2());
            }
            flingAnimation.start();
        }

        if (!isAnimerDriven() && isFixedScroll()) {
            FlingCalculator flingCalculator = new FlingCalculator(startVelocity, (float) flingAnimer.getArgument2());
            float flingTransition = flingCalculator.getTransiton();
            springAnimation.setStartVelocity(startVelocity);
            scrollValue.setValue(startValue);
            springAnimation.setStartValue(startValue);
            float roundValue = Math.round(((startValue + flingTransition) / fixedCellWidth)) * fixedCellWidth;
            springAnimation.getSpring().setFinalPosition(roundValue);

            springAnimation.getSpring().setStiffness((float) flingSpringAnimer.getArgument1());
            springAnimation.getSpring().setDampingRatio((float) flingSpringAnimer.getArgument2());
            springAnimation.start();
        }

        if (isAnimerDriven() && !isFixedScroll()) {
            scrollAnimer.setSolver(flingAnimer.getCurrentSolver());
            scrollAnimer.setArgument1((float)startVelocity);
            scrollAnimer.getCurrentPhysicsState().updatePhysicsVelocity(startVelocity);
            scrollAnimer.setFrom(startValue);
            scrollAnimer.getCurrentPhysicsState().updatePhysicsValue(startValue);

            if(isDynamicFlingFriction()){
                float dynamicDamping = (isVertScroll()) ? (float) mapValueFromRangeToRange(Math.abs(velocityY), 0, 24000, 1.35, 0.5) : (float) mapValueFromRangeToRange(Math.abs(velocityX), 0, 24000, 1.35, 0.5);;
                scrollAnimer.setArgument2((float)dynamicDamping);
            }
            else {
                //flingAnimation.setFriction((float)flingAnimer.getArgument2());
            }
            scrollAnimer.start();
        }

        if (isAnimerDriven() && isFixedScroll()) {
            scrollAnimer.cancel();
            FlingCalculator flingCalculator = new FlingCalculator(startVelocity,(float)flingAnimer.getArgument2());
            float flingTransition = flingCalculator.getTransiton();

            scrollAnimer.setVelocity(startVelocity);
            scrollAnimer.getCurrentPhysicsState().updatePhysicsVelocity(startVelocity);
            scrollAnimer.setFrom(startValue);
            scrollAnimer.getCurrentPhysicsState().updatePhysicsValue(startValue);
            float roundValue = Math.round(((startValue + flingTransition)/fixedCellWidth))*fixedCellWidth;
            scrollAnimer.setTo(roundValue);

            scrollAnimer.setSolver(flingSpringAnimer.getCurrentSolver());

            scrollAnimer.start();
        }


    }

    // ############################################
    // Utils
    // ############################################

    private boolean isAnimerDriven(){
        return isAnimerDriven;
    }

    private boolean isFling(){
        return isFling;
    }

    private void setFling(boolean boo){ isFling = boo; }

    private boolean isSpringBack() { return isSpringBack; }

    private void setSpringBack(boolean boo) {
        isSpringBack = boo;
    }

    private static double mapValueFromRangeToRange(double value,double fromLow,
            double fromHigh,
            double toLow,
            double toHigh) {
        double fromRangeSize = fromHigh - fromLow;
        double toRangeSize = toHigh - toLow;
        double valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }

    public void setDynamicFlingFriction(boolean dynamicDampingState){
        isDyanmicFling = dynamicDampingState;
    }
    public boolean isDynamicFlingFriction(){
        return isDyanmicFling;
    }

    public Animer getSpringAnimer(){
        return springAnimer;
    }
    public Animer getFlingAnimer(){
        return flingAnimer;
    }
    public Animer getFakeFlingAnimer(){
        return flingSpringAnimer;
    }


    public void setVertScroll(boolean isVertical) {
        isVertScroll = isVertical;
    }
    public boolean isVertScroll(){
        return isVertScroll;
    }

    public void setFixedScroll(boolean fixedScroll,float cellWidth){
        isFixedScroll = fixedScroll;
        fixedCellWidth = cellWidth;
    }
    private boolean isFixedScroll(){
        return  isFixedScroll;
    }

}
