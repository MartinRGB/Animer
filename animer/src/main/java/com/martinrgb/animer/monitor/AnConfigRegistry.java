package com.martinrgb.animer.monitor;

import com.martinrgb.animer.Animer;

public class AnConfigRegistry {

    private static final AnConfigRegistry INSTANCE = new AnConfigRegistry();

    public static AnConfigRegistry getInstance() {
        return INSTANCE;
    }

    private final ANConfigMap<String,Animer.AnimerSolver> mSolverMap;
    private final ANConfigMap<String,Animer> mAnimerMap;


    AnConfigRegistry() {
        mSolverMap = new ANConfigMap<String,Animer.AnimerSolver>();
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

    public boolean addSolver(String configName,Animer.AnimerSolver animerSolver) {
        if (animerSolver == null) {
            throw new IllegalArgumentException("animer is required");
        }
        if (configName == null) {
            throw new IllegalArgumentException("configName is required");
        }
        if (mSolverMap.containsKey(animerSolver)) {
            return false;
        }
        mSolverMap.put(configName,animerSolver);
        return true;
    }


    public boolean removeSolverConfig(Animer.AnimerSolver animerSolver) {
        if (animerSolver == null) {
            throw new IllegalArgumentException("animer is required");
        }
        return mSolverMap.remove(animerSolver) != null;
    }

    public ANConfigMap<String,Animer> getAllAnimer() {
        return mAnimerMap;
    }

    public ANConfigMap<String,Animer.AnimerSolver> getAllSolver() {
        return mSolverMap;
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

    public void removeAllSpringConfig() {
        mAnimerMap.clear();
    }

}
