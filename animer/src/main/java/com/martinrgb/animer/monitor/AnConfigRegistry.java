package com.martinrgb.animer.monitor;
import android.util.Log;

import com.martinrgb.animer.Animer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnConfigRegistry {

    private static final AnConfigRegistry INSTANCE = new AnConfigRegistry();

    public static AnConfigRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<Animer, String> mAnimerMap;

    AnConfigRegistry() {
        mAnimerMap = new HashMap<Animer, String>();

    }

    public boolean addAnimer(Animer animer, String configName) {
        if (animer == null) {
            throw new IllegalArgumentException("animer is required");
        }
        if (configName == null) {
            throw new IllegalArgumentException("configName is required");
        }
        if (mAnimerMap.containsKey(animer)) {
            return false;
        }
        mAnimerMap.put(animer, configName);
        return true;
    }

    public boolean removeSpringConfig(Animer animer) {
        if (animer == null) {
            throw new IllegalArgumentException("animer is required");
        }
        return mAnimerMap.remove(animer) != null;
    }

    public Map<Animer, String> getAllAnimer() {
        return Collections.unmodifiableMap(mAnimerMap);
    }

    public void removeAllSpringConfig() {
        mAnimerMap.clear();
    }
}
