package com.martinrgb.animer.monitor;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class AnConfigData {

    private int DATA_MODE = 0;
    Map<String,Object> configMap = new HashMap<>();


    public AnConfigData(Object o1,Object o2,int mode) {
        setMode(mode);
        setConfig("Arg1",o1);
        setConfig("Arg2",o2);
    }

    public AnConfigData(Object o1,Object o2) {
        setConfig("Arg1",o1);
        setConfig("Arg2",o2);
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

    public void getConfigByIndex(int index){
        Object firstKey = configMap.keySet().toArray()[index];
        Object valueForFirstKey = configMap.get(firstKey);
        Log.e("key: ",String.valueOf(firstKey));
        Log.e("value: ",String.valueOf(valueForFirstKey));
        //return valueForFirstKey;
    }

    public int getMode() {
        return DATA_MODE;
    }

    public void setMode(int mode) {
         DATA_MODE = mode;
    }



}
