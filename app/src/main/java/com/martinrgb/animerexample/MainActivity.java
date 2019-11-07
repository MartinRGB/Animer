package com.martinrgb.animerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.solver.AnSolver;
import com.martinrgb.animer.solver.FlingSolver;
import com.martinrgb.animer.solver.SpringSolver;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private  Animer animer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);
        //animer = new Animer(iv , SpringSolver.createAndroidSpring(500,0.5f),Animer.TRANSLATION_X,0,500);

        animer = new Animer(iv , FlingSolver.createAndroidFling(1500,0.9f),Animer.TRANSLATION_X);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iv.getTranslationX() != 800){
                    animer.setEndvalue(800);
                }
                else{
                    animer.setEndvalue(0);
                }
            }
        });

    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }
}
