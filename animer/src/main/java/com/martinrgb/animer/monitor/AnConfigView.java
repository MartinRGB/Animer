package com.martinrgb.animer.monitor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.math.converter.DHOConverter;
import com.martinrgb.animer.core.math.converter.OrigamiPOPConverter;
import com.martinrgb.animer.core.math.converter.RK4Converter;
import com.martinrgb.animer.core.math.converter.UIViewSpringConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * The SpringConfiguratorView provides a reusable view for live-editing all registered springs
 * within an Application. Each registered Spring can be accessed by its id and its tension and
 * friction properties can be edited while the user tests the effected UI live.
 */
public class AnConfigView extends FrameLayout {

    private final SpinnerAdapter spinnerAdapter;
    private final List<Animer.AnimerSolver> mAnimers = new ArrayList<Animer.AnimerSolver>();
    private AnConfigRegistry anConfigRegistry;
    private final int mTextColor = Color.argb(255, 225, 225, 225);

    private Spinner mSpringSelectorSpinner;
    private Animer.AnimerSolver mSelectedAnimerSolver;
    private Animer mRevealAnimer;

    private FrameLayout root;
    private LinearLayout listLayout;
    private  SeekbarListener seekbarListener;

    int PX_5,PX_10,PX_20,PX_120;

    public AnConfigView(Context context) {
        this(context, null);
    }

    public AnConfigView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnConfigView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        anConfigRegistry = AnConfigRegistry.getInstance();
        spinnerAdapter = new SpinnerAdapter(context);

        Resources resources = getResources();

        PX_5 = dpToPx(5, resources);
        PX_10 = dpToPx(10, resources);
        PX_20 = dpToPx(20, resources);
        PX_120 = dpToPx(120, resources);

        addView(generateHierarchy(context));

        ViewTreeObserver vto = root.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Put your code here.
                Log.e("Root",String.valueOf(root.getMeasuredHeight()));
                Log.e("Root",String.valueOf(root.getHeight()));
                mRevealAnimer = new Animer();
                mRevealAnimer.setSolver(Animer.springDroid(500,0.95f));
                mRevealAnimer.setUpdateListener(new Animer.UpdateListener() {
                    @Override
                    public void onUpdate(float value, float velocity, float progress) {
                        float val = (float) value;
                        float minTranslate = 0;
                        float maxTranslate = root.getMeasuredHeight() - dpToPx(40/2, resources);
                        float range = maxTranslate - minTranslate;
                        float yTranslate = (val * range) + minTranslate;
                        AnConfigView.this.setTranslationY(yTranslate);
                    }
                });

                mRevealAnimer.setCurrentValue(1);
            }
        });

    }



    private View generateHierarchy(Context context) {
        Resources resources = getResources();

        FrameLayout.LayoutParams params;
        int fivePx = dpToPx(5, resources);
        int tenPx = dpToPx(10, resources);
        int twentyPx = dpToPx(20, resources);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        tableLayoutParams.setMargins(0, 0, fivePx, 0);
        LinearLayout seekWrapper;

        // Root

        root = new FrameLayout(context);
        params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        root.setLayoutParams(params);


        // # Container

        LinearLayout container = new LinearLayout(context);
        params = createMatchParams();
        params.setMargins(0, twentyPx, 0, 0);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(params);
        container.setBackgroundColor(Color.argb(100, 0, 0, 0));
        root.addView(container);

        // ## Spinner
        seekWrapper = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(tenPx, tenPx, tenPx, twentyPx);
        seekWrapper.setPadding(tenPx, tenPx, tenPx, tenPx);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        container.addView(seekWrapper);

        mSpringSelectorSpinner = new Spinner(context, Spinner.MODE_DIALOG);
        params = createMatchWrapParams();
        params.setMargins(tenPx, tenPx, tenPx, tenPx);
        mSpringSelectorSpinner.setLayoutParams(tableLayoutParams);
        seekWrapper.addView(mSpringSelectorSpinner);

        mSpringSelectorSpinner.setAdapter(spinnerAdapter);
        mSpringSelectorSpinner.setOnItemSelectedListener(new SpringSelectedListener());
        refreshAnConfigs();

        // ## List LinearLayout
        listLayout = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(0, 0, 0, dpToPx(40, resources));
        listLayout.setLayoutParams(params);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        container.addView(listLayout);

        seekbarListener = new SeekbarListener();

        for (int i = 0;i<listSize;i++){
            createListByIndex(i,context);
        }

        View nub = new View(context);
        params = createLayoutParams(dpToPx(60, resources), dpToPx(40, resources));
        params.gravity = Gravity.TOP | Gravity.CENTER;
        nub.setLayoutParams(params);
        nub.setOnTouchListener(new OnNubTouchListener());
        nub.setBackgroundColor(Color.argb(255, 0, 164, 209));
        root.addView(nub);

        return root;
    }

    private int listSize = 2;
    private static int SEEKBAR_START_ID = 15000;
    private static int SEEKLABEL_START_ID_START_ID = 20000;
    private static final int MAX_SEEKBAR_VAL = 100000;
    private static final int MIN_SEEKBAR_VAL = 1;

    private SeekBar mArgument1SeekBar,mArgument2SeekBar,mArgument3SeekBar,mArgument4SeekBar;
    private TextView mArgument1SeekLabel,mArgument2SeekLabel,mArgument3SeekLabel,mArgument4SeekLabel;
    private SeekBar[] SEEKBARS = new SeekBar[]{mArgument1SeekBar,mArgument2SeekBar,mArgument3SeekBar,mArgument4SeekBar};
    private TextView[] SEEKBAR_LABElS = new TextView[]{mArgument1SeekLabel,mArgument2SeekLabel,mArgument3SeekLabel,mArgument4SeekLabel};

    private String CONVERTER_TYPE = "NULL";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat DECIMAL_FORMAT_1 = new DecimalFormat("#.#");

    private void createListByIndex(int i,Context context){
        LinearLayout seekWrapper;
        FrameLayout.LayoutParams params;
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        tableLayoutParams.setMargins(0, 0, PX_5, 0);

        seekWrapper = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(PX_10, PX_10, PX_10, PX_20);
        seekWrapper.setPadding(PX_10, PX_10, PX_10, PX_10);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        listLayout.addView(seekWrapper);

        SEEKBARS[i] = new SeekBar(context);
        SEEKBARS[i].setLayoutParams(tableLayoutParams);
        SEEKBARS[i].setId(SEEKBAR_START_ID + i);
        seekWrapper.addView(SEEKBARS[i]);

        SEEKBAR_LABElS[i] = new TextView(getContext());
        SEEKBAR_LABElS[i].setTextColor(mTextColor);
        params = createLayoutParams(PX_120, ViewGroup.LayoutParams.MATCH_PARENT);
        SEEKBAR_LABElS[i].setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        SEEKBAR_LABElS[i].setLayoutParams(params);
        SEEKBAR_LABElS[i].setMaxLines(1);
        SEEKBAR_LABElS[i].setId(SEEKLABEL_START_ID_START_ID + i);
        seekWrapper.addView(SEEKBAR_LABElS[i]);

        SEEKBARS[i].setMax(MAX_SEEKBAR_VAL);
        SEEKBARS[i].setMin(MIN_SEEKBAR_VAL);
        SEEKBARS[i].setOnSeekBarChangeListener(seekbarListener);
    }

    public void refreshAnConfigs() {
        Map<Animer.AnimerSolver, String> mAnimerMap = anConfigRegistry.getAllAnimer();

        spinnerAdapter.clear();
        mAnimers.clear();

        for (Map.Entry<Animer.AnimerSolver, String> entry : mAnimerMap.entrySet()) {
            mAnimers.add(entry.getKey());
            spinnerAdapter.add(entry.getValue());
        }
        // Add the default config in last.
        spinnerAdapter.notifyDataSetChanged();
        if (mAnimers.size() > 0) {
            mSpringSelectorSpinner.setSelection(0);
        }
    }

    private float MAX_VAL1,MAX_VAL2,MAX_VAL3,MAX_VAL4,MIN_VAL1,MIN_VAL2,MIN_VAL3,MIN_VAL4,RANGE_VAL1,RANGE_VAL2,RANGE_VAL3,RANGE_VAL4,seekBarValue1,seekBarValue2,seekBarValue3,seekBarValue4;
    private float[] MAX_VALUES = new float[]{MAX_VAL1,MAX_VAL2,MAX_VAL3,MAX_VAL4};
    private float[] MIN_VALUES = new float[]{MIN_VAL1,MIN_VAL2,MIN_VAL3,MIN_VAL4};
    private float[] RANGE_VALUES = new float[]{RANGE_VAL1,RANGE_VAL2,RANGE_VAL3,RANGE_VAL4};
    private float[] SEEKBAR_VALUES = new float[]{seekBarValue1,seekBarValue2,seekBarValue3,seekBarValue4};

    private class SpringSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mSelectedAnimerSolver = mAnimers.get(i);

            for (int index = 0;index<listSize;index++){
                MAX_VALUES[index] = (float) Float.valueOf(mSelectedAnimerSolver.getConfigData().getConfig("arg" + String.valueOf(index+1) +"_max").toString());
                MIN_VALUES[index] = (float) Float.valueOf(mSelectedAnimerSolver.getConfigData().getConfig("arg" + String.valueOf(index+1) +"_min").toString());
                RANGE_VALUES[index] = MAX_VALUES[index] - MIN_VALUES[index];
            }

            updateSeekBarsForAnimer(mSelectedAnimerSolver);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int val, boolean b) {


            for(int i = 0;i<listSize;i++){
                if (seekBar == SEEKBARS[i]) {
                    SEEKBAR_VALUES[i] = ((float) (val - MIN_SEEKBAR_VAL) / (MAX_SEEKBAR_VAL-MIN_SEEKBAR_VAL))*RANGE_VALUES[i] + MIN_VALUES[i];

                    if(i == 0){
                        String roundedValue1Label = DECIMAL_FORMAT_1.format(SEEKBAR_VALUES[i]);
                        SEEKBAR_LABElS[i].setText((String) mSelectedAnimerSolver.getConfigData().getConfig("arg" +String.valueOf(i+1) +"_name") + ": " + roundedValue1Label);
                        mSelectedAnimerSolver.getConfigData().setConfig("arg"+String.valueOf(i+1)+"",Float.valueOf(roundedValue1Label));
                        mSelectedAnimerSolver.setArg1(getConvertValueByIndex(i));
                    }
                    else if(i == 1){
                        String roundedValue1Label = DECIMAL_FORMAT.format(SEEKBAR_VALUES[i]);
                        SEEKBAR_LABElS[i].setText((String) mSelectedAnimerSolver.getConfigData().getConfig("arg" +String.valueOf(i+1) +"_name") + ": " + roundedValue1Label);
                        mSelectedAnimerSolver.getConfigData().setConfig("arg"+String.valueOf(i+1)+"",Float.valueOf(roundedValue1Label));
                        mSelectedAnimerSolver.setArg2(getConvertValueByIndex(i));
                    }

                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
    private Object getConvertValueByIndex(int i){
        Log.e("CONVERTER_TYPE",String.valueOf(CONVERTER_TYPE));
        Log.e("CONVERTER_TYPE_i",String.valueOf(i));
        switch (CONVERTER_TYPE) {
            case "NULL":
                return SEEKBAR_VALUES[i];
            case "AndroidSpring":
                return SEEKBAR_VALUES[i];
            case "DHO":
                if( i == 0) {
                    DHOConverter dhoConverter = new DHOConverter(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                    return dhoConverter.getStiffness();
                }
                else if(i == 1){
                    DHOConverter dhoConverter = new DHOConverter(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                    return dhoConverter.getDampingRatio();
                }
                else{
                    return SEEKBAR_VALUES[i];
                }
            case "RK4":
                if( i == 0) {
                    RK4Converter rk4Converter = new RK4Converter(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                    return rk4Converter.getStiffness();
                }
                else if(i == 1) {
                    RK4Converter rk4Converter = new RK4Converter(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                    return rk4Converter.getDampingRatio();
                }
                else{
                    return SEEKBAR_VALUES[i];
                }
            case "UIVIEW":
                if( i == 0) {
                    UIViewSpringConverter uiViewSpringConverter = new UIViewSpringConverter(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                    return uiViewSpringConverter.getStiffness();
                }
                else if(i == 1) {
                    UIViewSpringConverter uiViewSpringConverter = new UIViewSpringConverter(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                    return uiViewSpringConverter.getDampingRatio();
                }
                else{
                    return SEEKBAR_VALUES[i];
                }
            case "ORIGAMI":
                if( i == 0) {
                    OrigamiPOPConverter origamiPOPConverter = new OrigamiPOPConverter(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                    return origamiPOPConverter.getStiffness();
                }
                else if(i == 1) {
                    OrigamiPOPConverter origamiPOPConverter = new OrigamiPOPConverter(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                    return origamiPOPConverter.getDampingRatio();
                }
                else{
                    return SEEKBAR_VALUES[i];
                }
            default:
                return SEEKBAR_VALUES[i];
        }
    }


    private void updateSeekBarsForAnimer(Animer.AnimerSolver animerSolver) {
//        seekBarValue1 = (float) Float.valueOf(animerSolver.getConfigData().getConfig("arg1").toString());
//        seekBarValue2 = (float) Float.valueOf(animerSolver.getConfigData().getConfig("arg2").toString());

//        float value1 = (float) animer.getArgument1();
//        float value2 = (float) animer.getArgument2();

        if((animerSolver.getConfigData().getConfig("converter_type")) !=null){
            CONVERTER_TYPE = animerSolver.getConfigData().getConfig("converter_type").toString();
        }

        for(int i = 0;i<listSize;i++){

            SEEKBAR_VALUES[i] = Float.valueOf(animerSolver.getConfigData().getConfig("arg" + String.valueOf(i+1)).toString());
            float progress = (SEEKBAR_VALUES[i] - MIN_VALUES[i])/RANGE_VALUES[i] *(MAX_SEEKBAR_VAL-MIN_SEEKBAR_VAL) + MIN_SEEKBAR_VAL;
            SEEKBARS[i].setProgress((int) progress);
            SEEKBAR_LABElS[i].setText((String) animerSolver.getConfigData().getConfig("arg" + String.valueOf(i+1) +"_name") + ": " + (String) animerSolver.getConfigData().getConfig("arg" + String.valueOf(i+1)).toString());
        }
    }

    /**
     * toggle visibility when the nub is tapped.
     */
    private class OnNubTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                togglePosition();
            }
            return true;
        }
    }

    private void togglePosition() {
        double currentValue = mRevealAnimer.getCurrentPhysicsState().getPhysicsValue();
        mRevealAnimer.setEndvalue(currentValue == 1 ? 0 : 1);
    }

    private class SpinnerAdapter extends BaseAdapter {

        private final Context mContext;
        private final List<String> mStrings;

        public SpinnerAdapter(Context context) {
            mContext = context;
            mStrings = new ArrayList<String>();
        }

        @Override
        public int getCount() {
            return mStrings.size();
        }

        @Override
        public Object getItem(int position) {
            return mStrings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void add(String string) {
            mStrings.add(string);
            notifyDataSetChanged();
        }

        /**
         * Remove all elements from the list.
         */
        public void clear() {
            mStrings.clear();
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(mContext);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(params);
                int twelvePx = dpToPx(12, getResources());
                textView.setPadding(twelvePx, twelvePx, twelvePx, twelvePx);
                textView.setTextColor(mTextColor);
            } else {
                textView = (TextView) convertView;
            }
            textView.setText(mStrings.get(position));
            return textView;
        }
    }

    public static int dpToPx(float dp, Resources res) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                res.getDisplayMetrics());
    }

    public static FrameLayout.LayoutParams createLayoutParams(int width, int height) {
        return new FrameLayout.LayoutParams(width, height);
    }

    public static FrameLayout.LayoutParams createMatchParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public static FrameLayout.LayoutParams createWrapParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static FrameLayout.LayoutParams createWrapMatchParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public static FrameLayout.LayoutParams createMatchWrapParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
