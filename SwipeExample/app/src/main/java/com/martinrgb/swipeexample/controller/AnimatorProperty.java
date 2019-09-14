package com.martinrgb.swipeexample.controller;

import android.util.FloatProperty;
import android.view.View;
import android.view.animation.Animation;

import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;

public abstract class AnimatorProperty<T> {

    private String mAnimatorName;

    private AnimatorProperty(String name) {
        mAnimatorName = name;
    }

    public abstract Object getAnimator();

    public static <T> AnimatorProperty<T> createAnimator(
            final String name) {
        return new AnimatorProperty<T>(name) {
            Object animator = null;
            @Override
            public Object getAnimator() {
                try {
                    animator = Class.forName(name).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return animator;
            }
        };
    }

    public static final AnimatorProperty SpringAnimation = createAnimator("2300");


}




