package com.martinrgb.animerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.interpolator.AndroidNative.AccelerateDecelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.DecelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.FastOutSlowInInterpolator;
import com.martinrgb.animer.monitor.AnConfigRegistry;
import com.martinrgb.animer.monitor.AnConfigView;


public class MainActivity extends AppCompatActivity {

    private ImageView iv1,iv2,iv3;
    private Animer animer1,animer2,animer3,animer4,animer5,animer6;
    private boolean isOpen,isOpen2,isOpen3 = false;
    private  AnConfigView mSpringConfiguratorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv1 = findViewById(R.id.iv);
        iv2 = findViewById(R.id.iv2);;
        iv3 = findViewById(R.id.iv3);



        Animer.AnimerSolver solverA  = Animer.interpolatorDroid(new FastOutSlowInInterpolator(),1300);
        Animer.AnimerSolver solverB  = Animer.springDroid(1000,0.5f);

        animer1 = new Animer(iv1,solverA,Animer.TRANSLATION_X,0,600);
        animer2 = new Animer(iv2,Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(),1500),Animer.TRANSLATION_X,0,500);
        animer3 = new Animer(iv3,Animer.interpolatorDroid(new DecelerateInterpolator(2),1200),Animer.TRANSLATION_X,0,720);
        animer4 = new Animer(iv1,Animer.springRK4(100,10),Animer.ROTATION,1,1.2f);
        animer5 = new Animer(iv2,Animer.springDHO(200,20),Animer.ROTATION,0,720);
        animer6 = new Animer(iv3,Animer.springOrigamiPOP(30,10),Animer.ROTATION,200,800);

        animer1.setCurrentValue(200);
        animer2.setCurrentValue(200);
        animer3.setCurrentValue(200);

        mSpringConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer("R色 - X",animer1);
        AnConfigRegistry.getInstance().addAnimer("B色 - X",animer2);
        AnConfigRegistry.getInstance().addAnimer("G色 - X",animer3);
        AnConfigRegistry.getInstance().addAnimer("R色 - R",animer4);
        AnConfigRegistry.getInstance().addAnimer("B色 - R",animer5);
        AnConfigRegistry.getInstance().addAnimer("G色 - R",animer6);

        mSpringConfiguratorView.refreshAnimerConfigs();

        iv1.setOnClickListener(view -> {

            if(!isOpen){
                animer1.setEndvalue(800);
                animer4.setEndvalue(720);

            }
            else{
                animer1.setEndvalue(200);
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



    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}
