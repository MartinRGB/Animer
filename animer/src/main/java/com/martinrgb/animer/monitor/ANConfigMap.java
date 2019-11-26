package com.martinrgb.animer.monitor;

import com.martinrgb.animer.Animer;

import java.util.LinkedHashMap;

public class ANConfigMap<K,V> extends LinkedHashMap {

    private ANConfigMap mLinkedHashMap;

    public ANConfigMap() {
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

            if(string == key){
                return i;
            }
        }
        return -1;
    }
}
