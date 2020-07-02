package com.martinrgb.animerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.interpolator.AndroidNative.AccelerateDecelerateInterpolator;
import com.martinrgb.animer.core.interpolator.AndroidNative.DecelerateInterpolator;
import com.martinrgb.animer.monitor.AnConfigRegistry;
import com.martinrgb.animer.monitor.AnConfigView;


public class MainActivity extends AppCompatActivity {

    private ImageView iv1,iv2,iv3,iv4;
    private Animer animer1,animer2,animer3,animer4,animer5,animer6,animer7,animerNew;
    private boolean isOpen,isOpen2,isOpen3,isOpen4 = false;
    private  AnConfigView mAnimerConfiguratorView;
    private ImageView ivNew;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_main);

        iv1 = findViewById(R.id.iv);
        iv2 = findViewById(R.id.iv2);;
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);
        ivNew = findViewById(R.id.iv5);
        tv = findViewById(R.id.tv);

        Animer.AnimerSolver solverB  = Animer.springDroid(1000,0.5f);

        animer1 = new Animer(iv1,solverB,Animer.TRANSLATION_X,0,500);
        animer2 = new Animer(iv2,Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(),1500),Animer.TRANSLATION_X,0,500);
        animer3 = new Animer(iv3,Animer.interpolatorDroid(new DecelerateInterpolator(2),1200),Animer.TRANSLATION_X,0,720);
        animer4 = new Animer(iv1,Animer.springRK4(100,10),Animer.ROTATION,1,1.2f);
        animer5 = new Animer(iv2,Animer.springDHO(200,20),Animer.ROTATION,0,720);
        animer6 = new Animer(iv3,Animer.springOrigamiPOP(30,10),Animer.ROTATION,200,800);
        animer7 = new Animer(iv4,Animer.springRK4(230,15),Animer.SCALE,1,0.5f);

        ivNew.getLayoutParams().width = 44 * 3;
        animerNew = new Animer();
        animerNew.setSolver(Animer.springDroid(600,0.99f));
        animerNew.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {
                ivNew.getLayoutParams().height = (int) (44*3 + progress*(200-44)*3);
                ivNew.requestLayout();
            }
        });
        animerNew.setCurrentValue(0);


        animer1.setCurrentValue(200);
        animer2.setCurrentValue(200);
        animer3.setCurrentValue(200);

        mAnimerConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer("Image Scale Animation",animer7);
        AnConfigRegistry.getInstance().addAnimer("Red TranslationX",animer1);
        AnConfigRegistry.getInstance().addAnimer("Blue TranslationX",animer2);
        AnConfigRegistry.getInstance().addAnimer("Green TranslationX",animer3);
        AnConfigRegistry.getInstance().addAnimer("Red Rotation",animer4);
        AnConfigRegistry.getInstance().addAnimer("Blue Rotation",animer5);
        AnConfigRegistry.getInstance().addAnimer("Green Rotation",animer6);
        AnConfigRegistry.getInstance().addAnimer("Volume Simulation",animerNew);
        mAnimerConfiguratorView.refreshAnimerConfigs();

        iv1.setOnClickListener(view -> {

            if(!isOpen){
                animer1.setEndValue(800);
                //animer1.animateToState("stateA");
                animer4.setEndValue(720);

            }
            else{
                animer1.setEndValue(200);
                //animer1.animateToState("stateB");
                animer4.setEndValue(0);
            }
            isOpen = !isOpen;
        });

        iv2.setOnClickListener(view -> {

            if(!isOpen2){
                animer2.setEndValue(800);
                animer5.setEndValue(720);

            }
            else{
                animer2.setEndValue(200);
                animer5.setEndValue(0);
            }
            isOpen2 = !isOpen2;
        });

        iv3.setOnClickListener(view -> {

            if(!isOpen3){
                animer3.setEndValue(800);
                animer6.setEndValue(720);

            }
            else{
                animer3.setEndValue(200);
                animer6.setEndValue(0);
            }
            isOpen3 = !isOpen3;
        });

        iv4.setOnClickListener(view -> {

            if(!isOpen4){
                animer7.setEndValue(0.5f);
            }
            else{
                animer7.setEndValue(1f);
            }
            isOpen4 = !isOpen4;
        });

        ivNew.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        animerNew.setEndValue(1);
                        tv.setText("手势状态：Down");
                        Log.i("TAG", "touched down");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("TAG", "touched move");
                        break;
                    case MotionEvent.ACTION_UP:
                        animerNew.setEndValue(0);
                        tv.setText("手势状态：Up");
                        Log.i("TAG", "touched up");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.i("TAG", "touched cancel");
                        break;
                }
                return true;
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
