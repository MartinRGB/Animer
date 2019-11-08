package com.martinrgb.animer.core.property;

public abstract class AnProperty<T> {
    final String mPropertyName;
    public AnProperty(String name) {
        mPropertyName = name;
    }

    public abstract float getValue(T object);
    public abstract void setValue(T object, float value);
}
