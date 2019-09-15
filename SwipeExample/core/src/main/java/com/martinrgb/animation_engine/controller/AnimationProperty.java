package com.martinrgb.animation_engine.controller;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.FloatPropertyCompat;

public abstract class AnimationProperty extends FloatPropertyCompat<View> {

    private String mName;

    private AnimationProperty(String name) {
        super(name);
        mName= name;
    }

    public static final AnimationProperty TRANSLATION_X = new AnimationProperty("translationX") {
        @Override
        public void setValue(View view, float value) {
            view.setTranslationX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getTranslationX();
        }
    };

    public static final AnimationProperty TRANSLATION_Y = new AnimationProperty("translationY") {
        @Override
        public void setValue(View view, float value) {
            view.setTranslationY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getTranslationY();
        }
    };

    public static final AnimationProperty TRANSLATION_Z = new AnimationProperty("translationZ") {
        @Override
        public void setValue(View view, float value) {
            ViewCompat.setTranslationZ(view, value);
        }

        @Override
        public float getValue(View view) {
            return ViewCompat.getTranslationZ(view);
        }
    };

    public static final AnimationProperty SCALE = new AnimationProperty("scale") {
        @Override
        public void setValue(View view, float value) {
            view.setScaleX(value);
            view.setScaleY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getScaleX();
        }
    };

    public static final AnimationProperty SCALE_X = new AnimationProperty("scaleX") {
        @Override
        public void setValue(View view, float value) {
            view.setScaleX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getScaleX();
        }
    };

    public static final AnimationProperty SCALE_Y = new AnimationProperty("scaleY") {
        @Override
        public void setValue(View view, float value) {
            view.setScaleY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getScaleY();
        }
    };

    public static final AnimationProperty ROTATION = new AnimationProperty("rotation") {
        @Override
        public void setValue(View view, float value) {
            view.setRotation(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotation();
        }
    };

    public static final AnimationProperty ROTATION_X = new AnimationProperty("rotationX") {
        @Override
        public void setValue(View view, float value) {
            view.setRotationX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotationX();
        }
    };

    public static final AnimationProperty ROTATION_Y = new AnimationProperty("rotationY") {
        @Override
        public void setValue(View view, float value) {
            view.setRotationY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotationY();
        }
    };

    public static final AnimationProperty X = new AnimationProperty("x") {
        @Override
        public void setValue(View view, float value) {
            view.setX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getX();
        }
    };

    public static final AnimationProperty Y = new AnimationProperty("y") {
        @Override
        public void setValue(View view, float value) {
            view.setY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getY();
        }
    };

    public static final AnimationProperty Z = new AnimationProperty("z") {
        @Override
        public void setValue(View view, float value) {
            ViewCompat.setZ(view, value);
        }

        @Override
        public float getValue(View view) {
            return ViewCompat.getZ(view);
        }
    };

    public static final AnimationProperty ALPHA = new AnimationProperty("alpha") {
        @Override
        public void setValue(View view, float value) {
            view.setAlpha(value);
        }

        @Override
        public float getValue(View view) {
            return view.getAlpha();
        }
    };

}




