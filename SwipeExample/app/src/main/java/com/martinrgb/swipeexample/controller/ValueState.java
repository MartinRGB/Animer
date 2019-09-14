package com.martinrgb.swipeexample.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class ValueState {

  public ArrayList<KeyValue> kvArrayList = new ArrayList<KeyValue>();

  public ValueState() {
  }



  public void setState(String key,float value){
    if(getIndexOFValue(key,kvArrayList) != -1) {
      kvArrayList.get(getIndexOFValue(key, kvArrayList)).setValue(value);
    }
    else{
      Log.e("ADD State", key);
      Log.e("ADD State", String.valueOf(value));
      addState(key, value);
    }
  }

  private void addState(String key,float value){
    kvArrayList.add(new KeyValue(key,value));
  }

  public float getStateValue(String key){
    return (float) kvArrayList.get(getIndexOFValue(key,kvArrayList)).getValue();
  }

  private static int getIndexOFValue(String value, ArrayList<KeyValue> listMap) {
    for(int i = 0;i<listMap.size();i++){
      if(listMap.get(i).getKey() == value){
        return i;
      }
    }
    return -1;
  }


  public class KeyValue<K, V> implements Map.Entry<K, V>
  {
    private K key;
    private V value;

    public KeyValue(K key, V value)
    {
      this.key = key;
      this.value = value;
    }

    public K getKey()
    {
      return this.key;
    }

    public V getValue()
    {
      return this.value;
    }

    public K setKey(K key)
    {
      return this.key = key;
    }

    public V setValue(V value)
    {
      return this.value = value;
    }
  }
}


