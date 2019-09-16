package com.martinrgb.animer.controller;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.FloatPropertyCompat;

public abstract class AnProperty extends FloatPropertyCompat<View> {

    private String mName;

    private AnProperty(String name) {
        super(name);
        mName= name;
    }

    public static final AnProperty TRANSLATION_X = new AnProperty("translationX") {
        @Override
        public void setValue(View view, float value) {
            view.setTranslationX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getTranslationX();
        }
    };

    public static final AnProperty TRANSLATION_Y = new AnProperty("translationY") {
        @Override
        public void setValue(View view, float value) {
            view.setTranslationY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getTranslationY();
        }
    };

    public static final AnProperty TRANSLATION_Z = new AnProperty("translationZ") {
        @Override
        public void setValue(View view, float value) {
            ViewCompat.setTranslationZ(view, value);
        }

        @Override
        public float getValue(View view) {
            return ViewCompat.getTranslationZ(view);
        }
    };

    public static final AnProperty SCALE = new AnProperty("scale") {
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

    public static final AnProperty SCALE_X = new AnProperty("scaleX") {
        @Override
        public void setValue(View view, float value) {
            view.setScaleX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getScaleX();
        }
    };

    public static final AnProperty SCALE_Y = new AnProperty("scaleY") {
        @Override
        public void setValue(View view, float value) {
            view.setScaleY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getScaleY();
        }
    };

    public static final AnProperty ROTATION = new AnProperty("rotation") {
        @Override
        public void setValue(View view, float value) {
            view.setRotation(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotation();
        }
    };

    public static final AnProperty ROTATION_X = new AnProperty("rotationX") {
        @Override
        public void setValue(View view, float value) {
            view.setRotationX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotationX();
        }
    };

    public static final AnProperty ROTATION_Y = new AnProperty("rotationY") {
        @Override
        public void setValue(View view, float value) {
            view.setRotationY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getRotationY();
        }
    };

    public static final AnProperty X = new AnProperty("x") {
        @Override
        public void setValue(View view, float value) {
            view.setX(value);
        }

        @Override
        public float getValue(View view) {
            return view.getX();
        }
    };

    public static final AnProperty Y = new AnProperty("y") {
        @Override
        public void setValue(View view, float value) {
            view.setY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getY();
        }
    };

    public static final AnProperty Z = new AnProperty("z") {
        @Override
        public void setValue(View view, float value) {
            ViewCompat.setZ(view, value);
        }

        @Override
        public float getValue(View view) {
            return ViewCompat.getZ(view);
        }
    };

    public static final AnProperty ALPHA = new AnProperty("alpha") {
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




