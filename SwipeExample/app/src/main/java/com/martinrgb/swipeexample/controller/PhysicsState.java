package com.martinrgb.swipeexample.controller;

public class PhysicsState {
  private float value;
  private float velocity;

  public PhysicsState() {
    initState(0);
  }
  public PhysicsState(float propertyValue) {
    initState(propertyValue);
  }

  public void initState(float propertytValue){
    updateState(propertytValue,0);
  }

  public void updateState(float val,float vel){
    updateValue(val);
    updateVelocity(vel);
  }

  public void updateVelocity(float vel){
    velocity = vel;
  }

  public void updateValue(float val){
    value = val;
  }

  public float getValue() {
    return value;
  }

  public float getVelocity() {
    return velocity;
  }




}
