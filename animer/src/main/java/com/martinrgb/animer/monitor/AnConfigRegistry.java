package com.martinrgb.animer.monitor;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.interpolator.AndroidNative.AccelerateDecelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.AccelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.AnticipateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.AnticipateOvershootInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.BounceInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.FastOutLinearInInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.FastOutSlowInInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.LinearInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.LinearOutSlowInInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.PathInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidSpringInterpolator;
import com.martinrgb.animer.core.interpolator.CustomBounceInterpolator;
import com.martinrgb.animer.core.interpolator.CustomDampingInterpolator;
import com.martinrgb.animer.core.interpolator.CustomMocosSpringInterpolator;
import com.martinrgb.animer.core.interpolator.CustomSpringInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.CycleInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.DecelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.OvershootInterpolator;

public class AnConfigRegistry {

    private static final AnConfigRegistry INSTANCE = new AnConfigRegistry();

    public static AnConfigRegistry getInstance() {
        return INSTANCE;
    }

    private final AnConfigMap<String,Animer> mAnimerMap;


    AnConfigRegistry() {
        mAnimerMap = new AnConfigMap<String,Animer>();
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

    public AnConfigMap<String,Animer> getAllAnimer() {
        return mAnimerMap;
    }

    public AnConfigMap<String,Animer.AnimerSolver> getAllSolverTypes(){
        AnConfigMap<String,Animer.AnimerSolver> map = new AnConfigMap<String,Animer.AnimerSolver>();
        map.put("AndroidSpring",Animer.springDroid(1500,0.5f));
        map.put("AndroidFling",Animer.flingDroid(4000,0.8f));
        map.put("iOSUIViewSpring",Animer.springiOSUIView(0.5f,0.5f));
        map.put("iOSCoreAnimationSpring",Animer.springiOSCoreAnimation(100,10));
        map.put("OrigamiPOPSpring",Animer.springOrigamiPOP(5,10));
        map.put("RK4Spring",Animer.springRK4(200,25));
        map.put("DHOSpring",Animer.springDHO(50,2f));
        map.put("ProtopieSpring",Animer.springProtopie(300,15f));
        map.put("PrincipleSpring",Animer.springPrinciple(380,20f));
        map.put("CubicBezier",Animer.interpolatorDroid(new PathInterpolator(0.5f,0.5f,0.5f,0.5f),500));
        map.put("LinearInterpolator",Animer.interpolatorDroid(new LinearInterpolator(),500));
        map.put("AccelerateDecelerateInterpolator",Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(),500));
        map.put("AccelerateInterpolator",Animer.interpolatorDroid(new AccelerateInterpolator(2),500));
        map.put("DecelerateInterpolator",Animer.interpolatorDroid(new DecelerateInterpolator(2),500));
        map.put("AnticipateInterpolator",Animer.interpolatorDroid(new AnticipateInterpolator(2),500));
        map.put("OvershootInterpolator",Animer.interpolatorDroid(new OvershootInterpolator(2),500));
        map.put("AnticipateOvershootInterpolator",Animer.interpolatorDroid(new AnticipateOvershootInterpolator(2),500));
        map.put("BounceInterpolator",Animer.interpolatorDroid(new BounceInterpolator(),500));
        map.put("CycleInterpolator",Animer.interpolatorDroid(new CycleInterpolator(2),500));
        map.put("FastOutSlowInInterpolator",Animer.interpolatorDroid(new FastOutSlowInInterpolator(),500));
        map.put("LinearOutSlowInInterpolator",Animer.interpolatorDroid(new LinearOutSlowInInterpolator(),500));
        map.put("FastOutLinearInInterpolator",Animer.interpolatorDroid(new FastOutLinearInInterpolator(),500));
        map.put("CustomMocosSpringInterpolator",Animer.interpolatorDroid(new CustomMocosSpringInterpolator(100,15,0),500));
        map.put("CustomSpringInterpolator",Animer.interpolatorDroid(new CustomSpringInterpolator(0.5f),500));
        map.put("CustomBounceInterpolator",Animer.interpolatorDroid(new CustomBounceInterpolator(0,0),500));
        map.put("CustomDampingInterpolator",Animer.interpolatorDroid(new CustomDampingInterpolator(0,0),500));
        map.put("AndroidSpringInterpolator",Animer.interpolatorDroid(new AndroidSpringInterpolator(1500,0.5f,500),500));
        return map;
    }


}
