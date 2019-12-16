package com.martinrgb.animerexample;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.martinrgb.animer.component.scrollview.AnScrollView;
import com.martinrgb.animer.monitor.AnConfigRegistry;
import com.martinrgb.animer.monitor.AnConfigView;

import java.util.ArrayList;

public class ScrollerActivity extends AppCompatActivity {


    private final int ROW_COUNT = 10;
    private int[] imageViews = new int[]{R.drawable.img_1,R.drawable.img_2,R.drawable.img_3,R.drawable.img_4};
    private AnConfigView mAnimerConfiguratorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteBars();
        setContentView(R.layout.activity_scroller);

        ViewGroup content = (ViewGroup) findViewById(R.id.content_view);
        for (int i = 0; i < ROW_COUNT; i++) {
            ExampleRowView exampleRowView = new ExampleRowView(getApplicationContext());
            exampleRowView.setHeader("Header " + i);
            exampleRowView.setSub("Sub " + i);
            exampleRowView.setImage(imageViews[i%4]);
            content.addView(exampleRowView);
        }

        AnScrollView customScrollView = findViewById(R.id.scrollView);
        customScrollView.setVerticalScroll(false);
        mAnimerConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
        AnConfigRegistry.getInstance().addAnimer("Fling",customScrollView.getFlingAnimer());
        AnConfigRegistry.getInstance().addAnimer("SpringBack",customScrollView.getSpringAnimer());
        mAnimerConfiguratorView.refreshAnimerConfigs();
    }

    private class ExampleRowView extends LinearLayout {
        private final TextView mHeaderView;
        private final TextView mSubView;
        private final SmoothCornersImage mImageView;

        public ExampleRowView(Context context) {
            super(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup view = (ViewGroup) inflater.inflate(R.layout.example_row_view, this, false);
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
}