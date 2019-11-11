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
import com.martinrgb.animer.core.util.AnUtil;
import com.martinrgb.animer.monitor.AnConfigRegistry;
import com.martinrgb.animer.monitor.AnConfigView;


public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private Animer animer;
    private boolean isOpen = false;
    private  AnConfigView mSpringConfiguratorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);

        animer = new Animer(iv,Animer.springDroid(500,0.4f),Animer.TRANSLATION_X,0,500);
//        Log.e("DefaultSolver",String.valueOf(animer.getDefaultSolveArg1()));
//        Log.e("DefaultSolver",String.valueOf(animer.getDefaultSolveArg2()));

        mSpringConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer(animer, "origami animation spring");
        mSpringConfiguratorView.refreshAnConfigs();




        iv.setOnClickListener(view -> {

            if(!isOpen){
                //animer.setSolver(Animer.springDroid(1500,0.25f));
                animer.setEndvalue(600);

            }
            else{
                //animer.setSolver(Animer.interpolatorDroid(new FastOutSlowInInterpolator(),300));
                animer.setEndvalue(0);
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
