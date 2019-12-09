package com.martinrgb.animerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;

//import com.codemonkeylabs.fpslibrary.FrameDataCallback;
//import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.interpolator.AndroidNative.AccelerateDecelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.DecelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.FastOutSlowInInterpolator;
import com.martinrgb.animer.monitor.AnConfigRegistry;
import com.martinrgb.animer.monitor.AnConfigView;
import com.martinrgb.animer.monitor.fps.FPSBuilder;
import com.martinrgb.animer.monitor.fps.FPSDetector;
import com.martinrgb.animer.monitor.fps.FrameDataCallback;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class MainActivity extends AppCompatActivity {

    private ImageView iv1,iv2,iv3,iv4;
    private Animer animer1,animer2,animer3,animer4,animer5,animer6,animer7;
    private boolean isOpen,isOpen2,isOpen3,isOpen4 = false;
    private  AnConfigView mAnimerConfiguratorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv1 = findViewById(R.id.iv);
        iv2 = findViewById(R.id.iv2);;
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);

        Animer.AnimerSolver solverB  = Animer.springDroid(1000,0.5f);

//        animer1 = new Animer();
//        animer1.setSolver(solverB);
//        animer1.setUpdateListener(new Animer.UpdateListener() {
//            @Override
//            public void onUpdate(float value, float velocity, float progress) {
//                iv1.setTranslationX(value);
//            }
//        });
//        animer1.setStateValue("stateA",300);
//        animer1.setStateValue("stateB",700);
//        animer1.setStateValue("stateC",200);

        animer1 = new Animer(iv1,solverB,Animer.TRANSLATION_X,0,500);
        animer2 = new Animer(iv2,Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(),1500),Animer.TRANSLATION_X,0,500);
        animer3 = new Animer(iv3,Animer.interpolatorDroid(new DecelerateInterpolator(2),1200),Animer.TRANSLATION_X,0,720);
        animer4 = new Animer(iv1,Animer.springRK4(100,10),Animer.ROTATION,1,1.2f);
        animer5 = new Animer(iv2,Animer.springDHO(200,20),Animer.ROTATION,0,720);
        animer6 = new Animer(iv3,Animer.springOrigamiPOP(30,10),Animer.ROTATION,200,800);
        animer7 = new Animer(iv4,Animer.springRK4(230,15),Animer.SCALE,1,0.5f);


        mAnimerConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer("Image Scale Animation",animer7);
        AnConfigRegistry.getInstance().addAnimer("Red TranslationX",animer1);
        AnConfigRegistry.getInstance().addAnimer("Blue TranslationX",animer2);
        AnConfigRegistry.getInstance().addAnimer("Green TranslationX",animer3);
        AnConfigRegistry.getInstance().addAnimer("Red Rotation",animer4);
        AnConfigRegistry.getInstance().addAnimer("Blue Rotation",animer5);
        AnConfigRegistry.getInstance().addAnimer("Green Rotation",animer6);
        mAnimerConfiguratorView.refreshAnimerConfigs();

        iv1.setOnClickListener(view -> {

            if(!isOpen){
                animer1.setEndvalue(800);
                //animer1.animateToState("stateA");
                animer4.setEndvalue(720);

            }
            else{
                animer1.setEndvalue(200);
                //animer1.animateToState("stateB");
                animer4.setEndvalue(0);
            }
            isOpen = !isOpen;
        });

        iv2.setOnClickListener(view -> {

            if(!isOpen2){
                animer2.setEndvalue(800);
                animer5.setEndvalue(720);

            }
            else{
                animer2.setEndvalue(200);
                animer5.setEndvalue(0);
            }
            isOpen2 = !isOpen2;
        });

        iv3.setOnClickListener(view -> {

            if(!isOpen3){
                animer3.setEndvalue(800);
                animer6.setEndvalue(720);

            }
            else{
                animer3.setEndvalue(200);
                animer6.setEndvalue(0);
            }
            isOpen3 = !isOpen3;
        });

        iv4.setOnClickListener(view -> {

            if(!isOpen4){
                animer7.setEndvalue(0.5f);
            }
            else{
                animer7.setEndvalue(1f);
            }
            isOpen4 = !isOpen4;
        });



    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}
