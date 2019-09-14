package com.martinrgb.animation_engine.controller;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class PhysicsState {
  private float value;
  private float velocity;
  Map<String,Float> kvMap = new HashMap<>();

  // ############### Constructor ###############
  public PhysicsState() {
    updatePhysics(0,0);
  }
  public PhysicsState(float start) {
    updatePhysics(start,0);
    setStateValue("Start",start);
  }

  public PhysicsState(float start,float end) {
    updatePhysics(start,0);
    setStateValue("Start",start);
    setStateValue("End",end);
  }

  // ############### Functions of setting Physics Value ###############

  public void updatePhysics(float val,float vel){
    updatePhysicsValue(val);
    updatePhysicsVelocity(vel);
  }

  public void updatePhysicsVelocity(float vel){
    velocity = vel;
  }

  public void updatePhysicsValue(float val){
    value = val;
  }

  public float getPhysicsValue() {
    return value;
  }

  public float getPhysicsVelocity() {
    return velocity;
  }

  // ############### Functions of setting Animation State ###############

  public void setStateValue(String key,float value){
    kvMap.put(key,value);
  }

  public float getStateValue(String key){

    try {
      return kvMap.get(key);
    } catch (Exception e) {
      e.printStackTrace();
      Log.e("The State is not exsit in HashMap,you must setStateValue first!!!", Log.getStackTraceString(e));
    }

    return -1;
  }

  // ############### Basic states of Animation ###############

  private String START_STATE = "Start";
  private String END_STATE = "End";

  //TODO: No Event and no Animation then set the prev State

//  public void setStartState(float value) {
//    setStateValue(START_STATE,value);
//  }
//
//  public void setEndState(float value) {
//    setStateValue(END_STATE,value);
//  }
//
//  public float getStartState() {
//    return getStateValue(START_STATE);
//  }
//
//  public float getEndState() {
//    return getStateValue(END_STATE);
//  }


}
