package com.martinrgb.animation_engine.controller;

import android.util.Log;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AnimatorCreator extends  Object{


    private Object[] mArgs = null;
    private String mClassName = null;
    private AnimatorCreator(String className,Object[] args) {
        mClassName = className;
        mArgs = args;
    }
    private Object getAnimator(){
        Object animator = null;
        try {
            //Object animator2 = Class.forName(name).newInstance();
            if(mArgs !=null){
                Class<?> clazz = Class.forName(mClassName);
                Constructor<?> constructor = clazz.getConstructor(FloatValueHolder.class);
                animator = constructor.newInstance(mArgs);
            }
            else{
                animator = Class.forName(mClassName).newInstance();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Log.e("Animsssss",String.valueOf(animator));
        return animator;
    }


    private static Object createAnimator(final String className,final Object[] args) {
        return new AnimatorCreator(className,args).getAnimator();
    }

    public static Object createSpringAnimator(){
        return createAnimator("androidx.dynamicanimation.animation.SpringAnimation",new Object[] { new FloatValueHolder()});
    }

    public static Object createValueAnimator(){
        return createAnimator("android.animation.ValueAnimator",null);
    }
    public static Object createFlingAnimation(){
        return createAnimator("androidx.dynamicanimation.animation.FlingAnimation",new Object[] { new FloatValueHolder()});
    }


}




