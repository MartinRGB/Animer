package com.martinrgb.animerexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.monitor.AnConfigRegistry;
import com.martinrgb.animer.monitor.AnConfigView;


public class MainActivity extends AppCompatActivity {

    private ImageView iv,iv2,iv3;
    private Animer animer1,animer2,animer3,animer4,animer5,animer6;
    private boolean isOpen,isOpen2,isOpen3 = false;
    private  AnConfigView mSpringConfiguratorView;
    private Animer.AnimerSolver solverA,solverB,solverC,solverD,solverE,solverF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);
        iv2 = findViewById(R.id.iv2);;
        iv3 = findViewById(R.id.iv3);

        solverA = Animer.springDroid(10,0.95f);
        solverB = Animer.springPrinciple(1000,25f);
        solverC = Animer.springProtopie(500,25);
        solverD = Animer.springiOSCoreAnimation(200,1.95f);
        solverE = Animer.springOrigamiPOP(20,10);
        solverF = Animer.springOrigamiPOP(20,10);

        animer1 = new Animer(iv,solverA,Animer.TRANSLATION_X,0,600);
        animer1.setCurrentValue(200);
        animer2 = new Animer(iv,solverB,Animer.TRANSLATION_Y,0,500);
        animer2.setCurrentValue(-400);
        animer3 = new Animer(iv,solverC,Animer.ROTATION,0,720);
        animer4 = new Animer(iv2,solverD,Animer.SCALE,1,1.2f);
        animer5 = new Animer(iv2,solverE,Animer.ROTATION_X,0,720);
        animer6 = new Animer(iv3,solverF,Animer.TRANSLATION_X,200,800);

        mSpringConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer("G色 - X",animer6);
        AnConfigRegistry.getInstance().addAnimer("R色 - X",animer1);
        AnConfigRegistry.getInstance().addAnimer("红色 - Y",animer2);
        AnConfigRegistry.getInstance().addAnimer("红色 - R",animer3);
        AnConfigRegistry.getInstance().addAnimer("蓝色 - S",animer4);
        AnConfigRegistry.getInstance().addAnimer("蓝色 - R_x",animer5);

        mSpringConfiguratorView.refreshAnimerConfigs();

        iv.setOnClickListener(view -> {

            if(!isOpen){
                animer1.setEndvalue(600);
                animer2.setEndvalue(-1200);
                animer3.setEndvalue(720);

            }
            else{
                animer1.setEndvalue(200);
                animer2.setEndvalue(-600);
                animer3.setEndvalue(0);

            }
            isOpen = !isOpen;
        });

        iv2.setOnClickListener(view -> {

            if(!isOpen2){
                animer4.setEndvalue(1.2f);
                animer5.setEndvalue(360);

            }
            else{
                animer4.setEndvalue(1f);
                animer5.setEndvalue(0);
            }
            isOpen2 = !isOpen2;
        });

        animer6.setCurrentValue(200);
        iv3.setOnClickListener(view -> {

            if(!isOpen3){
                animer6.setEndvalue(800);

            }
            else{
                animer6.setEndvalue(200);
            }
            isOpen3 = !isOpen3;
        });



    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}
