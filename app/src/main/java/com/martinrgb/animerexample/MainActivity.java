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

    private ImageView iv,iv2;
    private Animer animer,animer2,animer3,animer4,animer5;
    private boolean isOpen,isOpen2 = false;
    private  AnConfigView mSpringConfiguratorView;
    private Animer.AnimerSolver solverA,solverB,solverC,solverD,solverE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);
        iv2 = findViewById(R.id.iv2);;

        solverA = Animer.springDroid(10,0.95f);
        solverB = Animer.springPrinciple(1000,25f);
        solverC = Animer.springProtopie(500,25);
        solverD = Animer.springiOSCoreAnimation(200,1.95f);
        solverE = Animer.springOrigami(20,10);

        animer = new Animer(iv,solverA,Animer.TRANSLATION_X,0,600);
        animer.setCurrentValue(200);
        animer2 = new Animer(iv,solverB,Animer.TRANSLATION_Y,0,500);
        animer2.setCurrentValue(-400);
        animer3 = new Animer(iv,solverC,Animer.ROTATION,0,720);
        animer4 = new Animer(iv2,solverD,Animer.SCALE,1,1.2f);
        animer5 = new Animer(iv2,solverE,Animer.ROTATION_X,0,720);

        mSpringConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addSolver("红色 - X",solverA);
        AnConfigRegistry.getInstance().addSolver("红色 - Y",solverB);
        AnConfigRegistry.getInstance().addSolver("红色 - R",solverC);
        AnConfigRegistry.getInstance().addSolver("蓝色 - S",solverD);
        AnConfigRegistry.getInstance().addSolver("蓝色 - R_x",solverE);
        mSpringConfiguratorView.refreshAnimerConfigs();

        iv.setOnClickListener(view -> {

            if(!isOpen){
                //animer.setSolver(solverD);
                animer.setEndvalue(600);
                animer2.setEndvalue(-1200);
                animer3.setEndvalue(720);

            }
            else{
                //animer.setSolver(solverE);
                animer.setEndvalue(200);
                animer2.setEndvalue(-600);
                animer3.setEndvalue(0);

            }
            isOpen = !isOpen;
        });

        iv2.setOnClickListener(view -> {

            if(!isOpen2){
                //animer.setSolver(solverD);
                animer4.setEndvalue(1.2f);
                animer5.setEndvalue(360);

            }
            else{
                //animer.setSolver(solverE);
                animer4.setEndvalue(1f);
                animer5.setEndvalue(0);
            }
            isOpen2 = !isOpen2;
        });

    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}
