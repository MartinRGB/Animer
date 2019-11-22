package com.martinrgb.animer.monitor;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class AnConfigData {

    private int DATA_MODE = 0;
    Map<String,Object> configMap = new HashMap<>();


    public AnConfigData(Object o1,Object o2,int mode) {
        setMode(mode);
        setConfig("arg1",o1);
        setConfig("arg2",o2);
    }

    public AnConfigData(Object o1,Object o2) {
        setConfig("arg1",o1);
        setConfig("arg2",o2);
    }

    // ############################################
    // PhysicsState State's Getter & Setter
    // ############################################

    public void setConfig(String key,Object value){
        configMap.put(key,value);
    }

    public Object getConfig(String key){

        try {
            return configMap.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setStateValue first", Log.getStackTraceString(e));
        }

        return -1;
    }

    // TODO:Cannot use this,only String works
    public Object[] getConfigByIndex(int index){
        Object key = configMap.keySet().toArray()[index];
        Object value = configMap.get(key);
        Log.e("index: ",String.valueOf(index));
        Log.e("key: ",String.valueOf(key));
        Log.e("value: ",String.valueOf(value));
        //return valueForFirstKey;
        return new Object[]{key,value};
    }

    public int getMode() {
        return DATA_MODE;
    }

    public void setMode(int mode) {
         DATA_MODE = mode;
    }



}
