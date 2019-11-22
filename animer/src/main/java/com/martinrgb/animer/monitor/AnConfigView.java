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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * The SpringConfiguratorView provides a reusable view for live-editing all registered springs
 * within an Application. Each registered Spring can be accessed by its id and its tension and
 * friction properties can be edited while the user tests the effected UI live.
 */
public class AnConfigView extends FrameLayout {

    private final SpinnerAdapter solverObjectSpinnerAdapter;
    private final SpinnerAdapter solverTypeSpinnerAdapter;
    private final List<Animer.AnimerSolver> mSolverObjects = new ArrayList<Animer.AnimerSolver>();
    private final List<Animer.AnimerSolver> mSolverTypes = new ArrayList<Animer.AnimerSolver>();

    private AnConfigRegistry anConfigRegistry;
    private final int mTextColor = Color.argb(255, 225, 225, 225);

    private Spinner mSolverObjectSelectorSpinner;
    private Spinner mSolverTypeSelectorSpinner;
    private Animer.AnimerSolver mSelectedSolverObject,mSelectedSolverType;
    private Animer mRevealAnimer;

    private FrameLayout root;
    private LinearLayout listLayout;
    private TextView animatorType;
    private  SeekbarListener seekbarListener;

    int PX_5,PX_10,PX_20,PX_120;
    private static LinkedHashMap<String, Animer.AnimerSolver> solverTypesMap = new LinkedHashMap<>();
    private Map<String,Animer.AnimerSolver> mAnimerMap;

    public AnConfigView(Context context) {
        this(context, null);
    }

    public AnConfigView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnConfigView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        anConfigRegistry = AnConfigRegistry.getInstance();
        initSolverMap();
        solverObjectSpinnerAdapter = new SpinnerAdapter(context);
        solverTypeSpinnerAdapter = new SpinnerAdapter(context);

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

                //mRevealAnimer.setCurrentValue(1);
            }
        });

    }

    private void initSolverMap(){
        solverTypesMap.put("AndroidSpring",Animer.springDroid(1500,0.5f));
        solverTypesMap.put("AndroidFling",Animer.flingDroid(4000,0.8f));
        solverTypesMap.put("iOSUIViewSpring",Animer.springiOSUIView(0.5f,0.5f));
        solverTypesMap.put("iOSCoreAnimationSpring",Animer.springiOSCoreAnimation(100,10));
        solverTypesMap.put("OrigamiPOPSpring",Animer.springOrigami(50,10));
        solverTypesMap.put("RK4Spring",Animer.springRK4(200,25));
        solverTypesMap.put("DHOSpring",Animer.springDHO(50,2f));
        solverTypesMap.put("ProtopieSpring",Animer.springProtopie(300,15f));
        solverTypesMap.put("PrincipleSpring",Animer.springPrinciple(380,20f));
    }


    private View generateHierarchy(Context context) {
        Resources resources = getResources();

        FrameLayout.LayoutParams params;
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        tableLayoutParams.setMargins(0, 0, PX_5, 0);
        LinearLayout seekWrapper;

        // Root

        root = new FrameLayout(context);
        params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        root.setLayoutParams(params);


        // # Container

        LinearLayout container = new LinearLayout(context);
        params = createMatchParams();
        params.setMargins(0, PX_20, 0, 0);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(params);
        container.setBackgroundColor(Color.argb(100, 0, 0, 0));
        root.addView(container);

        // ## Spinner
        seekWrapper = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
        seekWrapper.setPadding(PX_10, PX_10, PX_10, PX_20);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        container.addView(seekWrapper);

        mSolverObjectSelectorSpinner = new Spinner(context, Spinner.MODE_DIALOG);
        params = createMatchWrapParams();
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
        mSolverObjectSelectorSpinner.setLayoutParams(tableLayoutParams);
        seekWrapper.addView(mSolverObjectSelectorSpinner);

        mSolverObjectSelectorSpinner.setAdapter(solverObjectSpinnerAdapter);
        refreshAnConfigs();
        mSolverObjectSelectorSpinner.setOnItemSelectedListener(new SoverObjectSelectedListener());


        seekWrapper = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
        seekWrapper.setPadding(PX_10, PX_10, PX_10, PX_20);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        container.addView(seekWrapper);

        mSolverTypeSelectorSpinner = new Spinner(context, Spinner.MODE_DIALOG);
        params = createMatchWrapParams();
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
        mSolverTypeSelectorSpinner.setLayoutParams(tableLayoutParams);
        seekWrapper.addView(mSolverTypeSelectorSpinner);

        mSolverTypeSelectorSpinner.setAdapter(solverTypeSpinnerAdapter);
        //TODO
        //refreshTypeConfigs();
        //mSolverTypeSelectorSpinner.setOnItemSelectedListener(new SoverTypeSelectedListener());






        // ## List LinearLayout
        listLayout = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(0, 0, 0, dpToPx(40, resources));
        listLayout.setLayoutParams(params);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        container.addView(listLayout);

        // ### Animator TypeTextView

        seekWrapper = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(PX_10, PX_10, PX_10, 0);
        seekWrapper.setPadding(PX_10, PX_10, PX_10, PX_10);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        listLayout.addView(seekWrapper);

        animatorType = new TextView(getContext());
        animatorType.setTextColor(mTextColor);
        params = createMatchWrapParams();
        animatorType.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        animatorType.setPadding(PX_10, PX_10, PX_10, PX_10);
        animatorType.setLayoutParams(params);
        animatorType.setMaxLines(1);
        animatorType.setText("Animator Type");
        seekWrapper.addView(animatorType);

        // ### Seekbar

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
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
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
        mAnimerMap = anConfigRegistry.getAllAnimer();

        solverObjectSpinnerAdapter.clear();
        mSolverObjects.clear();

        for (Map.Entry<String,Animer.AnimerSolver> entry : mAnimerMap.entrySet()) {
            mSolverObjects.add(entry.getValue());
            solverObjectSpinnerAdapter.add(entry.getKey());
            Log.e("aa","aaa");
        }
        // Add the default config in last.
        solverObjectSpinnerAdapter.notifyDataSetChanged();
        if (mSolverObjects.size() > 0) {
            mSolverObjectSelectorSpinner.setSelection(0);

            int typeIndex = getIndexByConfigString(String.valueOf(((Animer.AnimerSolver)mAnimerMap.get(mAnimerMap.keySet().toArray()[0])).getConfigData().getConfig("converter_type")));
            //TODO
            refreshTypeConfigs(typeIndex);
            mSolverTypeSelectorSpinner.setOnItemSelectedListener(new SoverTypeSelectedListener());
        }
    }

    public int getIndexByConfigString(String string){
        for(int i = 0;i< solverTypesMap.size();i++){
            Object key = solverTypesMap.keySet().toArray()[i];
            Object value = solverTypesMap.get(key);

            if(string == key){
                return i;
            }
        }
        return -1;
    }

    public void refreshTypeConfigs(int index) {
        solverTypeSpinnerAdapter.clear();
        mSolverTypes.clear();

        for (Map.Entry<String,Animer.AnimerSolver> entry : solverTypesMap.entrySet()) {
            mSolverTypes.add(entry.getValue());
            solverTypeSpinnerAdapter.add(entry.getKey());
            Log.e("bb","bbb");
        }
        // Add the default config in last.
        solverTypeSpinnerAdapter.notifyDataSetChanged();
        if (mSolverTypes.size() > 0) {
            mSolverTypeSelectorSpinner.setSelection(index,false);
        }
    }

    private float MAX_VAL1,MAX_VAL2,MAX_VAL3,MAX_VAL4,MIN_VAL1,MIN_VAL2,MIN_VAL3,MIN_VAL4,RANGE_VAL1,RANGE_VAL2,RANGE_VAL3,RANGE_VAL4,seekBarValue1,seekBarValue2,seekBarValue3,seekBarValue4;
    private float[] MAX_VALUES = new float[]{MAX_VAL1,MAX_VAL2,MAX_VAL3,MAX_VAL4};
    private float[] MIN_VALUES = new float[]{MIN_VAL1,MIN_VAL2,MIN_VAL3,MIN_VAL4};
    private float[] RANGE_VALUES = new float[]{RANGE_VAL1,RANGE_VAL2,RANGE_VAL3,RANGE_VAL4};
    private float[] SEEKBAR_VALUES = new float[]{seekBarValue1,seekBarValue2,seekBarValue3,seekBarValue4};

    private int objectSelectedIndex = 0;
    private class SoverObjectSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mSelectedSolverObject = mSolverObjects.get(i);
            objectSelectedIndex = i;

            Log.e("!1","213");
            for (int index = 0;index<listSize;index++){
                MAX_VALUES[index] = (float) Float.valueOf(mSelectedSolverObject.getConfigData().getConfig("arg" + String.valueOf(index+1) +"_max").toString());
                MIN_VALUES[index] = (float) Float.valueOf(mSelectedSolverObject.getConfigData().getConfig("arg" + String.valueOf(index+1) +"_min").toString());
                RANGE_VALUES[index] = MAX_VALUES[index] - MIN_VALUES[index];

            }

            updateSeekBarsForAnimer(mSelectedSolverObject);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private class SoverTypeSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mSelectedSolverType = mSolverTypes.get(i);

            for (int index = 0;index<listSize;index++){
                MAX_VALUES[index] = (float) Float.valueOf(mSelectedSolverType.getConfigData().getConfig("arg" + String.valueOf(index+1) +"_max").toString());
                MIN_VALUES[index] = (float) Float.valueOf(mSelectedSolverType.getConfigData().getConfig("arg" + String.valueOf(index+1) +"_min").toString());
                RANGE_VALUES[index] = MAX_VALUES[index] - MIN_VALUES[index];
            }



            mSolverObjects.set(mSolverObjectSelectorSpinner.getSelectedItemPosition(),mSelectedSolverType);
            mSelectedSolverObject = mSolverObjects.get(mSolverObjectSelectorSpinner.getSelectedItemPosition());
            //TODO
            updateSeekBarsForAnimer(mSelectedSolverObject);

            mAnimerMap.get(mAnimerMap.keySet().toArray()[mSolverObjectSelectorSpinner.getSelectedItemPosition()]).setArg1(getConvertValueByIndex(0));
            mAnimerMap.get(mAnimerMap.keySet().toArray()[mSolverObjectSelectorSpinner.getSelectedItemPosition()]).setArg2(getConvertValueByIndex(1));


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
                        SEEKBAR_LABElS[i].setText((String) mSelectedSolverObject.getConfigData().getConfig("arg" +String.valueOf(i+1) +"_name") + ": " + roundedValue1Label);
                        mSelectedSolverObject.getConfigData().setConfig("arg"+String.valueOf(i+1)+"",Float.valueOf(roundedValue1Label));

                    }
                    else if(i == 1){
                        String roundedValue1Label = DECIMAL_FORMAT.format(SEEKBAR_VALUES[i]);
                        SEEKBAR_LABElS[i].setText((String) mSelectedSolverObject.getConfigData().getConfig("arg" +String.valueOf(i+1) +"_name") + ": " + roundedValue1Label);
                        mSelectedSolverObject.getConfigData().setConfig("arg"+String.valueOf(i+1)+"",Float.valueOf(roundedValue1Label));

                        //TODO
                        mSelectedSolverObject.setArg1(getConvertValueByIndex(i-1));
                        mSelectedSolverObject.setArg2(getConvertValueByIndex(i));
                        mAnimerMap.get(mAnimerMap.keySet().toArray()[mSolverObjectSelectorSpinner.getSelectedItemPosition()]).setArg1(getConvertValueByIndex(0));
                        mAnimerMap.get(mAnimerMap.keySet().toArray()[mSolverObjectSelectorSpinner.getSelectedItemPosition()]).setArg2(getConvertValueByIndex(1));
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
        switch (CONVERTER_TYPE) {
            case "NULL":
                return SEEKBAR_VALUES[i];
            case "AndroidFling":
                return SEEKBAR_VALUES[i];
            case "AndroidSpring":
                return SEEKBAR_VALUES[i];
            case "DHOSpring":
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
            case "iOSCoreAnimationSpring":
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
            case "RK4Spring":
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
            case "ProtopieSpring":
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
            case "PrincipleSpring":
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
            case "iOSUIViewSpring":
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
            case "OrigamiPOPSpring":
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
            animatorType.setText(CONVERTER_TYPE);

            // TODO SHOULD DELTE
            int typeIndex = getIndexByConfigString(CONVERTER_TYPE);
            mSolverTypeSelectorSpinner.setSelection(typeIndex,false);

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
    public static FrameLayout.LayoutParams createMatchWrapParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
