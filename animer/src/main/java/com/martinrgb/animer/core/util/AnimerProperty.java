package com.martinrgb.animer.core.util;

public abstract class AnimerProperty<T> {
    final String mPropertyName;
    public AnimerProperty(String name) {
        mPropertyName = name;
    }

    public abstract float getValue(T object);
    public abstract void setValue(T object, float value);
}
