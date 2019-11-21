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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);

        animer = new Animer(iv,Animer.springDroid(500,0.4f),Animer.TRANSLATION_X,0,500);
        animer2 = new Animer(iv,Animer.springDroid(1000,0.25f),Animer.TRANSLATION_X,0,500);
        animer3 = new Animer(iv,Animer.interpolatorDroid(new FastOutSlowInInterpolator(),(long)25),Animer.ROTATION,0,500);
//        Log.e("DefaultSolver",String.valueOf(animer.getDefaultSolveArg1()));
//        Log.e("DefaultSolver",String.valueOf(animer.getDefaultSolveArg2()));

        mSpringConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer(animer, "origami animation spring 1");
        AnConfigRegistry.getInstance().addAnimer(animer2, "origami animation spring 2");
        AnConfigRegistry.getInstance().addAnimer(animer3, "origami animation spring 3");

        Log.e("Animer1",String.valueOf(animer.getArgument1()));
        Log.e("Animer2",String.valueOf(animer.getArgument2()));


        mSpringConfiguratorView.refreshAnConfigs();

        iv.setOnClickListener(view -> {

            if(!isOpen){
                animer.setSolver(Animer.springDroid(1500,0.25f));
                animer.setEndvalue(600);
                animer2.setEndvalue(600);
                animer3.setEndvalue(720);

            }
            else{
                animer.setSolver(Animer.flingDroid(1500,0.99f));
                animer.setEndvalue(0);
                animer2.setEndvalue(0);
                animer3.setEndvalue(0);
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
