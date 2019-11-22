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

    private final Map<Animer.AnimerSolver, String> mAnimerMap;

    AnConfigRegistry() {
        mAnimerMap = new HashMap<Animer.AnimerSolver, String>();

    }

    public boolean addSolver(Animer.AnimerSolver animerSolver, String configName) {
        if (animerSolver == null) {
            throw new IllegalArgumentException("animer is required");
        }
        if (configName == null) {
            throw new IllegalArgumentException("configName is required");
        }
        if (mAnimerMap.containsKey(animerSolver)) {
            return false;
        }
        mAnimerMap.put(animerSolver, configName);
        return true;
    }

    public boolean removeSpringConfig(Animer.AnimerSolver animerSolver) {
        if (animerSolver == null) {
            throw new IllegalArgumentException("animer is required");
        }
        return mAnimerMap.remove(animerSolver) != null;
    }

    public Map<Animer.AnimerSolver, String> getAllAnimer() {
        return Collections.unmodifiableMap(mAnimerMap);
    }

    public void removeAllSpringConfig() {
        mAnimerMap.clear();
    }
}
