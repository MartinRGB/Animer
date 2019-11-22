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

    private ImageView iv;
    private Animer animer,animer2,animer3;
    private boolean isOpen = false;
    private  AnConfigView mSpringConfiguratorView;
    private Animer.AnimerSolver solverA,solverB,solverC,solverD,solverE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);

        solverA = Animer.springDroid(1500f,0.95f);
        solverB = Animer.springRK4(1000,25f);
        solverC = Animer.springDHO(500,25);

        animer = new Animer(iv,solverA,Animer.TRANSLATION_Y,0,500);
        animer2 = new Animer(iv,solverB,Animer.TRANSLATION_X,0,500);
        animer3 = new Animer(iv,solverC,Animer.ROTATION,0,500);
//        Log.e("DefaultSolver",String.valueOf(animer.getDefaultSolveArg1()));
//        Log.e("DefaultSolver",String.valueOf(animer.getDefaultSolveArg2()));

        mSpringConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addSolver("Y",solverA);
        AnConfigRegistry.getInstance().addSolver("X",solverB);
        AnConfigRegistry.getInstance().addSolver("R",solverC);
//        AnConfigRegistry.getInstance().addSolver(solverD, "Go");
//        AnConfigRegistry.getInstance().addSolver(solverE, "Back");

        mSpringConfiguratorView.refreshAnConfigs();

        iv.setOnClickListener(view -> {

            if(!isOpen){
                //animer.setSolver(solverD);
                animer.setEndvalue(-400);
//                animer2.setEndvalue(600);
//                animer3.setEndvalue(720);

            }
            else{
                //animer.setSolver(solverE);
                animer.setEndvalue(0);
//                animer2.setEndvalue(0);
//                animer3.setEndvalue(0);
            }
            isOpen = !isOpen;
        });

    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}
