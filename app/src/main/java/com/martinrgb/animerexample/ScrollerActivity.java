package com.martinrgb.animerexample;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.martinrgb.animer.component.scrollview.AnScrollView;
import com.martinrgb.animer.monitor.AnConfigRegistry;
import com.martinrgb.animer.monitor.AnConfigView;

import java.util.ArrayList;

public class ScrollerActivity extends AppCompatActivity {


    private final int ROW_COUNT = 50;
    private int[] imageViews = new int[]{R.drawable.img_1,R.drawable.img_2,R.drawable.img_3,R.drawable.img_4};
    private AnConfigView mAnimerConfiguratorView;
    private  int  cellSize;
    private float screenWidth,screenHeight;
    private AnScrollView customScrollViewV,customScrollViewH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_scroller);
        measureDisplay();
        createLayout();
        addAnimerConfig();
    }

    private void createLayout(){

        // Vertical content
        ViewGroup contentV = (ViewGroup) findViewById(R.id.content_view_v);
        for (int i = 0; i < ROW_COUNT; i++) {

            ExampleRowView exampleRowView = new ExampleRowView(getApplicationContext());
            exampleRowView.setHeader("Header " + i);
            exampleRowView.setSub("Sub " + i);
            exampleRowView.setImage(imageViews[i%4]);
            contentV.addView(exampleRowView);
        }

        // Horizontal content
        ViewGroup contentH = (ViewGroup) findViewById(R.id.content_view_h);
        for (int i = 0; i < ROW_COUNT; i++) {

            ExampleRowView exampleRowView = new ExampleRowView(getApplicationContext());
            exampleRowView.setHeader("Header " + i);
            exampleRowView.setSub("Sub " + i);
            exampleRowView.setImage(imageViews[i%4]);
            if(i == 0){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        cellSize,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins( (int)(screenWidth - cellSize)/2, 0, 0, 0);
                exampleRowView.setLayoutParams(params);
            }
            else if(i == ROW_COUNT - 1){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        cellSize,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins( 0, 0, (int)(screenWidth - cellSize)/2, 0);
                exampleRowView.setLayoutParams(params);
            }
            contentH.addView(exampleRowView);
        }


        // Vertical sv
        customScrollViewV = findViewById(R.id.scrollView_v);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) screenHeight/2
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        customScrollViewV.setLayoutParams(params);
        customScrollViewV.setVerticalScroll(true);

        // Horizontal sv
        customScrollViewH = findViewById(R.id.scrollView_h);
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) screenHeight/2
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        customScrollViewH.setLayoutParams(params);
        customScrollViewH.setVerticalScroll(false);
        customScrollViewH.setFixedScroll(true,cellSize);
    }

    private void addAnimerConfig(){

        mAnimerConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer("V_Fling",customScrollViewV.getFlingAnimer());
        AnConfigRegistry.getInstance().addAnimer("V_SpringBack",customScrollViewV.getSpringAnimer());
        AnConfigRegistry.getInstance().addAnimer("V_FakeFling When Fixed Scroll",customScrollViewV.getFakeFlingAnimer());
        AnConfigRegistry.getInstance().addAnimer("H_Fling",customScrollViewH.getFlingAnimer());
        AnConfigRegistry.getInstance().addAnimer("H_SpringBack",customScrollViewH.getSpringAnimer());
        AnConfigRegistry.getInstance().addAnimer("H_FakeFling When Fixed Scroll",customScrollViewH.getFakeFlingAnimer());
        mAnimerConfiguratorView.refreshAnimerConfigs();
    }

    private class ExampleRowView extends LinearLayout {
        private final TextView mHeaderView;
        private final TextView mSubView;
        private final SmoothCornersImage mImageView;

        public ExampleRowView(Context context) {
            super(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup view = (ViewGroup) inflater.inflate(R.layout.custom_cell_view, this, false);
            mHeaderView = (TextView) view.findViewById(R.id.head_view);
            mSubView = (TextView) view.findViewById(R.id.sub_view);
            mImageView = view.findViewById(R.id.img_view);
            mImageView.setRoundRadius(60);
            addView(view);
        }

        public void setHeader(String text) {
            mHeaderView.setText(text);
        }
        public void setSub(String text) {
            mSubView.setText(text);
        }
        public void setImage(int id) {
            mImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),id));
            mImageView.setScaleType(ImageView.ScaleType.MATRIX);
        }
    }

    private void deleteBars() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
    }

    private void measureDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;
        screenHeight = dpToPx(dpHeight,getResources());
        screenWidth = dpToPx(dpWidth,getResources());
        cellSize =  (int) getResources().getDimension(R.dimen.cell_size_dp);
        Log.e("inDP","doHeight"+ dpHeight + "dpWidth" + dpWidth);
    }
    public static int dpToPx(float dp, Resources res) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,res.getDisplayMetrics());
    }
}