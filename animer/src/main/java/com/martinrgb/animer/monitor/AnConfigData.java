package com.martinrgb.animer.monitor;

import android.util.Log;

import java.util.LinkedHashMap;

public class AnConfigData {

    private int DATA_MODE = 0;
    LinkedHashMap<String,Object> configMap = new LinkedHashMap<String,Object>();


    public AnConfigData(Object o1,Object o2,int mode) {
        setMode(mode);
        addConfig("arg1",o1);
        addConfig("arg2",o2);
    }

    public AnConfigData(Object o1,Object o2) {
        addConfig("arg1",o1);
        addConfig("arg2",o2);
    }

    public void setArguments(String type,String arg1,Object arg1_min,Object arg1_max,String arg2,Object arg2_min,Object arg2_max){
        addConfig("converter_type",type);
        addConfig("arg1_name",arg1);
        addConfig("arg1_min",arg1_min);
        addConfig("arg1_max",arg1_max);
        addConfig("arg2_name",arg2);
        addConfig("arg2_min",arg2_min);
        addConfig("arg2_max",arg2_max);
    }



    // ############################################
    // PhysicsState State's Getter & Setter
    // ############################################

    public void addConfig(String key, Object value){
        configMap.put(key,value);
    }

    public LinkedHashMap getConfigs(){
        return configMap;
    }

    public void clearConfigs(){
        configMap.clear();
    }

    public void cloneConfigFrom(LinkedHashMap<String,Object> targetMap){
        if(targetMap instanceof LinkedHashMap){
            configMap.clear();
            configMap =  (LinkedHashMap) targetMap.clone();
        }
        else{
        }
    }

    public Object getKeyByString(String key){

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
