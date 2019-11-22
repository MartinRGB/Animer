package com.martinrgb.animer.monitor;
import android.util.Log;

import com.martinrgb.animer.Animer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnConfigRegistry {

    private static final AnConfigRegistry INSTANCE = new AnConfigRegistry();

    public static AnConfigRegistry getInstance() {
        return INSTANCE;
    }

    private final LinkedHashMap<String,Animer.AnimerSolver> mAnimerMap;

    AnConfigRegistry() {
        mAnimerMap = new LinkedHashMap<String,Animer.AnimerSolver>();

    }

    public boolean addSolver(String configName,Animer.AnimerSolver animerSolver) {
        if (animerSolver == null) {
            throw new IllegalArgumentException("animer is required");
        }
        if (configName == null) {
            throw new IllegalArgumentException("configName is required");
        }
        if (mAnimerMap.containsKey(animerSolver)) {
            return false;
        }
        mAnimerMap.put(configName,animerSolver);
        return true;
    }


    public boolean removeSpringConfig(Animer.AnimerSolver animerSolver) {
        if (animerSolver == null) {
            throw new IllegalArgumentException("animer is required");
        }
        return mAnimerMap.remove(animerSolver) != null;
    }

    public Map<String,Animer.AnimerSolver> getAllAnimer() {
        return Collections.unmodifiableMap(mAnimerMap);
    }

    public void removeAllSpringConfig() {
        mAnimerMap.clear();
    }
}
