package com.martinrgb.animer.monitor;

import java.util.LinkedHashMap;

public class AnConfigMap<K,V> extends LinkedHashMap {

    private AnConfigMap mLinkedHashMap;

    public AnConfigMap() {
        mLinkedHashMap = this;
    }

    public Object getValue(int index){
        Object key = mLinkedHashMap.keySet().toArray()[index];
        Object value = mLinkedHashMap.get(key);
        return value;
    }
    public Object getKey(int index){
        Object key = mLinkedHashMap.keySet().toArray()[index];
        return key;
    }

    public void resetIndex(int index,Object value){
        Object key = mLinkedHashMap.keySet().toArray()[index];
        mLinkedHashMap.replace(key,value);
    }



    public int getIndexByString(String string){
        for(int i = 0;i< mLinkedHashMap.size();i++){
            Object key = mLinkedHashMap.keySet().toArray()[i];
            Object value = mLinkedHashMap.get(key);

            if(string.equals(key.toString())){
                return i;
            }
        }
        return -1;
    }

}
