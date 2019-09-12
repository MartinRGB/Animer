//package com.martinrgb.swipeexample.controller;

import androidx.dynamicanimation.animation.FloatValueHolder;

public class PhysicsState {
  private FloatValueHolder value;
  private FloatValueHolder velocity;

  public PhysicsState() {
    initState();
  }

  public void initState(){
    value = new FloatValueHolder();
    velocity = new FloatValueHolder();
    //updateValue(value.getValue());
    //updateVelocity(velocity.getValue());
  }

  public void updateState(float val,float vel){
    updateValue(val);
    updateVelocity(vel);
  }

  public void updateVelocity(float vel){
    velocity.setValue(vel);
  }

  public void updateValue(float val){
    value.setValue(val);
  }

  public float getValue() {
    return value.getValue();
  }

  public FloatValueHolder getValueHoder() {
    return value;
  }

  public float getVelocity() {
    return velocity.getValue();
  }

  public FloatValueHolder getVelocityHolder() {
    return velocity;
  }



}
