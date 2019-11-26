package com.martinrgb.animer.monitor;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.martinrgb.animer.Animer;

public class AnConfigRegistry {

    private static final AnConfigRegistry INSTANCE = new AnConfigRegistry();

    public static AnConfigRegistry getInstance() {
        return INSTANCE;
    }

    private final ANConfigMap<String,Animer> mAnimerMap;


    AnConfigRegistry() {
        mAnimerMap = new ANConfigMap<String,Animer>();
    }


    public boolean addAnimer(String configName,Animer animer) {
        if (animer == null) {
            throw new IllegalArgumentException("animer is required");
        }
        if (configName == null) {
            throw new IllegalArgumentException("configName is required");
        }
        if (mAnimerMap.containsKey(animer)) {
            return false;
        }
        mAnimerMap.put(configName,animer);
        return true;
    }

    public boolean removeAnimerConfig(Animer animer) {
        if (animer == null) {
            throw new IllegalArgumentException("animer is required");
        }
        return mAnimerMap.remove(animer) != null;
    }

    public void removeAllAnimerConfig() {
        mAnimerMap.clear();
    }

    public ANConfigMap<String,Animer> getAllAnimer() {
        return mAnimerMap;
    }

    public ANConfigMap<String,Animer.AnimerSolver> getAllSolverTypes(){
        ANConfigMap<String,Animer.AnimerSolver> map = new ANConfigMap<String,Animer.AnimerSolver>();
        map.put("AndroidSpring",Animer.springDroid(1500,0.5f));
        map.put("AndroidFling",Animer.flingDroid(4000,0.8f));
        map.put("iOSUIViewSpring",Animer.springiOSUIView(0.5f,0.5f));
        map.put("iOSCoreAnimationSpring",Animer.springiOSCoreAnimation(100,10));
        map.put("OrigamiPOPSpring",Animer.springOrigamiPOP(5,10));
        map.put("RK4Spring",Animer.springRK4(200,25));
        map.put("DHOSpring",Animer.springDHO(50,2f));
        map.put("ProtopieSpring",Animer.springProtopie(300,15f));
        map.put("PrincipleSpring",Animer.springPrinciple(380,20f));
        return map;
    }

    public ANConfigMap<String,TimeInterpolator> getAllInterpolatorTypes(){
        ANConfigMap<String, TimeInterpolator> map = new ANConfigMap<String,TimeInterpolator>();
        map.put("AccelerateDecelerate",new AccelerateDecelerateInterpolator());
        map.put("Accelerate",new AccelerateInterpolator());
        map.put("Decelerate",new DecelerateInterpolator());
        map.put("Anticipate",new AnticipateInterpolator());
        map.put("Overshoot",new OvershootInterpolator());
        map.put("AnticipateOvershoot",new AnticipateOvershootInterpolator());
        map.put("Bounce",new BounceInterpolator());
        map.put("Cycle",new CycleInterpolator(2));
        map.put("Linear",new LinearInterpolator());
        map.put("FastOutSlowIn",new FastOutSlowInInterpolator());
        map.put("LinearOutSlowIn",new LinearOutSlowInInterpolator());
        map.put("FastOutLinear",new FastOutLinearInInterpolator());
        return map;
    }


}
