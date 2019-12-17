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
    private Animer flingAnimer,springAnimer,flingSpringAnimer;
    private Animer scrollAnimer;
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

    private boolean isAnimerDriven = true;


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

        if(!isAnimerDriven){
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
        else{
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
                    if (isSpringBack()) {
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

    //TODO:Should Only run one time
    public boolean springBack(int startX, int startY, int velocityX,int velocityY,int minX, int maxX, int minY, int maxY) {

        if(!isAnimerDriven){

            if(scrollerisVertScroll()){

                if (startY > maxY || startY < minY) {
                    if(!isFixedScroll){
                        flingAnimation.cancel();
                        if (isSpringBack()) {
                            springFunctions(startY,minY,maxY);
                        }
                    }
                    else {
                        springFunctions(startY,minY,maxY);
                    }

                    return true;
                }
            }
            else {
                if (startX > maxX || startX < minX) {
                    if(!isFixedScroll){
                        flingAnimation.cancel();
                        if (isSpringBack()) {
                            springFunctions(startX,minX,maxX);
                        }
                    }
                    else {
                        springFunctions(startX,minX,maxX);
                    }

                    return true;
                }
            }
        }
        else {
            if(scrollerisVertScroll()){

                if (startY > maxY || startY < minY) {
                    if(!isFixedScroll){
//                        scrollAnimer.cancel();
//                        if (isSpringBack()) {
//                            springFunctions(startY,minY,maxY);
//                        }
                        springFunctions(startY,minY,maxY);
                    }
                    else {
                        springFunctions(startY,minY,maxY);
                    }

                    return true;
                }
            }
            else {
                if (startX > maxX || startX < minX) {
                    if(!isFixedScroll){
//                        scrollAnimer.cancel();
//                        if (isSpringBack()) {
//                            springFunctions(startX,minX,maxX);
//                        }
                        springFunctions(startX,minX,maxX);
                    }
                    else {
                        springFunctions(startX,minX,maxX);
                    }

                    return true;
                }
            }
        }

        return true;
    }

    private  int i,a = 0;

    private void springFunctions(float val,float min,float max){

        if(!isAnimerDriven){
            scrollValue.setValue(val);
            springAnimation.setStartValue(val);
            springAnimation.setStartVelocity(scrollSpeed.getValue());

            springAnimation.getSpring().setStiffness((float)springAnimer.getArgument1());
            springAnimation.getSpring().setDampingRatio((float)springAnimer.getArgument2());

            if (val > max) {
                springAnimation.getSpring().setFinalPosition(max);
            } else if (val < min) {
                springAnimation.getSpring().setFinalPosition(min);
            }
            springAnimation.start();
        }else {

            // TODO: When fixedScroll, velocity is not correct;
            // setFrom | setVelocity didnt work cause when start() triggered:
            // mSpringAnimation.setStartValue(getCurrentPhysicsValue());
            // mSpringAnimation.setStartVelocity(getCurrentPhysicsVelocity());
            // mSpringAnimation.animateToFinalPosition(getStateValue(state));
            // scrollAnimer.setFrom(val);
            // scrollAnimer.setVelocity(scrollSpeed.getValue());
            scrollValue.setValue(val);
            scrollAnimer.setFrom(val);
            scrollAnimer.setVelocity(scrollSpeed.getValue());
            scrollAnimer.setArgument1((float)springAnimer.getArgument1());
            scrollAnimer.setArgument2((float)springAnimer.getArgument2());

            if (val > max) {
                scrollAnimer.setTo(max);
            } else if (val < min) {
                scrollAnimer.setTo(min);
            }

            scrollAnimer.getCurrentPhysicsState().updatePhysicsValue(val);
            scrollAnimer.getCurrentPhysicsState().updatePhysicsVelocity(scrollSpeed.getValue());

            scrollAnimer.start();
        }

    }

    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY, int overX, int overY) {

        if(!isAnimerDriven) {
            if (isFixedScroll()) {
                FlingCalculator flingCalculator;

                if (scrollerisVertScroll()) {
                    flingCalculator = new FlingCalculator(velocityY, (float) flingAnimer.getArgument2());
                    float flingTransition = flingCalculator.getTransiton();
                    springAnimation.setStartVelocity(velocityY);
                    scrollValue.setValue(startY);
                    springAnimation.setStartValue(startY);
                    float roundValue = Math.round(((startY + flingTransition) / fixedCellWidth)) * fixedCellWidth;
                    springAnimation.getSpring().setFinalPosition(roundValue);
                } else {
                    flingCalculator = new FlingCalculator(velocityX, (float) flingAnimer.getArgument2());
                    float flingTransition = flingCalculator.getTransiton();
                    springAnimation.setStartVelocity(velocityX);
                    scrollValue.setValue(startX);
                    springAnimation.setStartValue(startX);
                    float roundValue = Math.round(((startX + flingTransition) / fixedCellWidth)) * fixedCellWidth;
                    springAnimation.getSpring().setFinalPosition(roundValue);
                }

                springAnimation.getSpring().setStiffness((float) flingSpringAnimer.getArgument1());
                springAnimation.getSpring().setDampingRatio((float) flingSpringAnimer.getArgument2());
                springAnimation.start();
            } else {
                if (scrollerisVertScroll()) {
                    flingAnimation.setStartVelocity(velocityY);
                    scrollValue.setValue(startY);
                } else {
                    flingAnimation.setStartVelocity(velocityX);
                    scrollValue.setValue(startX);
                }

                if (getDynamicFlingFrictionState()) {
                    float dynamicDamping;
                    if(scrollerisVertScroll()){
                        dynamicDamping = (scrollerisVertScroll()) ? (float) mapValueFromRangeToRange(Math.abs(velocityY), 0, 24000, 1.35, 0.5) : (float) mapValueFromRangeToRange(Math.abs(velocityX), 0, 24000, 1.35, 0.5);
                    }
                    else {
                        dynamicDamping = (scrollerisVertScroll()) ? (float) mapValueFromRangeToRange(Math.abs(velocityX), 0, 24000, 1.35, 0.5) : (float) mapValueFromRangeToRange(Math.abs(velocityX), 0, 24000, 1.35, 0.5);
                    }
                    flingAnimation.setFriction(dynamicDamping);
                } else {
                    flingAnimation.setFriction((float) flingAnimer.getArgument2());
                }
                flingAnimation.start();
            }
        }
        else {

            if(isFixedScroll()){
                FlingCalculator flingCalculator;
                if(scrollerisVertScroll()){
                    flingCalculator = new FlingCalculator(velocityY,(float)flingAnimer.getArgument2());
                    float flingTransition = flingCalculator.getTransiton();

                    scrollAnimer.setVelocity(velocityY);
                    scrollAnimer.getCurrentPhysicsState().updatePhysicsVelocity(velocityY);
                    scrollAnimer.setFrom(startY);
                    scrollAnimer.getCurrentPhysicsState().updatePhysicsValue(startY);
                    float roundValue = Math.round(((startY + flingTransition)/fixedCellWidth))*fixedCellWidth;
                    scrollAnimer.setTo(roundValue);
                }
                else {

                    scrollAnimer.cancel();
                    flingCalculator = new FlingCalculator(velocityX,(float)flingAnimer.getArgument2());
                    float flingTransition = flingCalculator.getTransiton();

                    scrollAnimer.setVelocity(velocityX);
                    scrollAnimer.getCurrentPhysicsState().updatePhysicsVelocity(velocityX);
                    scrollAnimer.setFrom(startX);
                    scrollAnimer.getCurrentPhysicsState().updatePhysicsValue(startX);
                    float roundValue = Math.round(((startX + flingTransition)/fixedCellWidth))*fixedCellWidth;
                    scrollAnimer.setTo(roundValue);
                }

                scrollAnimer.setArgument1((float) flingSpringAnimer.getArgument1());
                scrollAnimer.setArgument2((float) flingSpringAnimer.getArgument2());

                scrollAnimer.start();
            }
            else {
                if(scrollerisVertScroll()){
                    scrollAnimer.setSolver(flingAnimer.getCurrentSolver());
                    scrollAnimer.setArgument1((float)velocityY);
                    scrollAnimer.getCurrentPhysicsState().updatePhysicsVelocity(velocityY);
                    scrollAnimer.setFrom(startY);
                    scrollAnimer.getCurrentPhysicsState().updatePhysicsValue(startY);
                }
                else {
                    scrollAnimer.setSolver(flingAnimer.getCurrentSolver());
                    scrollAnimer.setArgument1((float)velocityX);
                    scrollAnimer.getCurrentPhysicsState().updatePhysicsVelocity(velocityX);
                    scrollAnimer.setFrom(startX);
                    scrollAnimer.getCurrentPhysicsState().updatePhysicsValue(startX);
                }

                if(getDynamicFlingFrictionState()){
                    float dynamicDamping;
                    if(scrollerisVertScroll()){
                        dynamicDamping = (scrollerisVertScroll()) ? (float) mapValueFromRangeToRange(Math.abs(velocityY), 0, 24000, 1.35, 0.5) : (float) mapValueFromRangeToRange(Math.abs(velocityX), 0, 24000, 1.35, 0.5);
                    }
                    else {
                        dynamicDamping = (scrollerisVertScroll()) ? (float) mapValueFromRangeToRange(Math.abs(velocityX), 0, 24000, 1.35, 0.5) : (float) mapValueFromRangeToRange(Math.abs(velocityX), 0, 24000, 1.35, 0.5);
                    }
                    scrollAnimer.setArgument2((float)dynamicDamping);
                }
                else {
                    //flingAnimation.setFriction((float)flingAnimer.getArgument2());
                }
                scrollAnimer.start();
            }
        }

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

    public boolean computeScrollOffset() {
        if(!isAnimerDriven){
            return (flingAnimation.isRunning() || springAnimation.isRunning());
        }
        else {
            return scrollAnimer.isRunning();
        }
    }

    public final boolean isFinished() {
        if(!isAnimerDriven){
            return !(springAnimation.isRunning() || flingAnimation.isRunning());
        }
        else {
            return !scrollAnimer.isRunning();
        }
    }

    public void abortAnimation() {
        if(!isAnimerDriven){
            springAnimation.cancel();
            flingAnimation.cancel();
        }
        else {
            scrollAnimer.cancel();
        }
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
        return flingSpringAnimer;
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
