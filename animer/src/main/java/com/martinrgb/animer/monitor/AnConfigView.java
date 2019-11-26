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
import android.widget.AdapterView;
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


/**
 * The SpringConfiguratorView provides a reusable view for live-editing all registered springs
 * within an Application. Each registered Spring can be accessed by its id and its tension and
 * friction properties can be edited while the user tests the effected UI live.
 */
public class AnConfigView extends FrameLayout {

    private final AnSpinnerAdapter solverObjectSpinnerAdapter;
    private final AnSpinnerAdapter solverTypeSpinnerAdapter;

    private AnConfigRegistry anConfigRegistry;
    private final int mTextColor = Color.argb(255, 225, 225, 225);

    private Spinner mSolverObjectSelectorSpinner;
    private Spinner mSolverTypeSelectorSpinner;
    private Animer currentAnimer;
    //private Animer.AnimerSolver currentSolver;
    private Animer mRevealAnimer;

    private FrameLayout root;
    private LinearLayout listLayout;
    private TextView animatorType;
    private  SeekbarListener seekbarListener;
    private SoverSelectedListener soverSelectedListener;

    //int PX_5,PX_10,PX_20,PX_120;

    private int listSize = 2;
    private static int SEEKBAR_START_ID = 15000;
    private static int SEEKLABEL_START_ID_START_ID = 20000;
    private static final int MAX_SEEKBAR_VAL = 100000;
    private static final int MIN_SEEKBAR_VAL = 1;

    private SeekBar mArgument1SeekBar,mArgument2SeekBar,mArgument3SeekBar,mArgument4SeekBar;
    private TextView mArgument1SeekLabel,mArgument2SeekLabel,mArgument3SeekLabel,mArgument4SeekLabel;
    private SeekBar[] SEEKBARS = new SeekBar[]{mArgument1SeekBar,mArgument2SeekBar,mArgument3SeekBar,mArgument4SeekBar};
    private TextView[] SEEKBAR_LABElS = new TextView[]{mArgument1SeekLabel,mArgument2SeekLabel,mArgument3SeekLabel,mArgument4SeekLabel};

    private String currentType,previousSelectedType = "NULL";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat DECIMAL_FORMAT_1 = new DecimalFormat("#.#");

    private float MAX_VAL1,MAX_VAL2,MAX_VAL3,MAX_VAL4,MIN_VAL1,MIN_VAL2,MIN_VAL3,MIN_VAL4,RANGE_VAL1,RANGE_VAL2,RANGE_VAL3,RANGE_VAL4,seekBarValue1,seekBarValue2,seekBarValue3,seekBarValue4;
    private float[] MAX_VALUES = new float[]{MAX_VAL1,MAX_VAL2,MAX_VAL3,MAX_VAL4};
    private float[] MIN_VALUES = new float[]{MIN_VAL1,MIN_VAL2,MIN_VAL3,MIN_VAL4};
    private float[] RANGE_VALUES = new float[]{RANGE_VAL1,RANGE_VAL2,RANGE_VAL3,RANGE_VAL4};
    private float[] SEEKBAR_VALUES = new float[]{seekBarValue1,seekBarValue2,seekBarValue3,seekBarValue4};
    private final int PX_5 = dpToPx(5, getResources());
    private final int PX_10 = dpToPx(10, getResources());
    private final int PX_20 = dpToPx(20, getResources());
    private final int PX_120 = dpToPx(120, getResources());

    private ANConfigMap<String,Animer.AnimerSolver> mSolverTypesMap;
    private ANConfigMap<String,Animer> mAnimerObjectsMap;

    public AnConfigView(Context context) {
        this(context, null);
    }

    public AnConfigView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnConfigView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        Resources resources = getResources();
        anConfigRegistry = AnConfigRegistry.getInstance();
        solverObjectSpinnerAdapter = new AnSpinnerAdapter(context,resources);
        solverTypeSpinnerAdapter = new AnSpinnerAdapter(context,resources);

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
                        float yTranslate = -(val * range) + minTranslate;
                        AnConfigView.this.setTranslationY(yTranslate);
                    }
                });
            }
        });

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
        params = createLayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, PX_20);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(params);
        container.setBackgroundColor(Color.argb(100, 0, 0, 0));
        root.addView(container);

        // ## Spinner
        soverSelectedListener = new SoverSelectedListener();

        seekWrapper = new LinearLayout(context);
        params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
        seekWrapper.setPadding(PX_10, PX_10, PX_10, PX_20);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        container.addView(seekWrapper);

        mSolverObjectSelectorSpinner = new Spinner(context, Spinner.MODE_DIALOG);
        params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
        mSolverObjectSelectorSpinner.setLayoutParams(tableLayoutParams);
        seekWrapper.addView(mSolverObjectSelectorSpinner);


        mSolverObjectSelectorSpinner.setAdapter(solverObjectSpinnerAdapter);
        mSolverObjectSelectorSpinner.setOnItemSelectedListener(soverSelectedListener);

        seekWrapper = new LinearLayout(context);
        params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
        seekWrapper.setPadding(PX_10, PX_10, PX_10, PX_20);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        container.addView(seekWrapper);

        mSolverTypeSelectorSpinner = new Spinner(context, Spinner.MODE_DIALOG);
        params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(PX_10, PX_10, PX_10, PX_10);
        mSolverTypeSelectorSpinner.setLayoutParams(tableLayoutParams);
        seekWrapper.addView(mSolverTypeSelectorSpinner);

        mSolverTypeSelectorSpinner.setAdapter(solverTypeSpinnerAdapter);
        mSolverTypeSelectorSpinner.setOnItemSelectedListener(soverSelectedListener);

        refreshAnimerConfigs();

        // ## List LinearLayout
        listLayout = new LinearLayout(context);
        params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(40, resources));
        listLayout.setLayoutParams(params);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        container.addView(listLayout);


        // ### Seekbar List
        seekbarListener = new SeekbarListener();

        for (int i = 0;i<listSize;i++){
            tableLayoutParams.setMargins(0, 0, PX_5, 0);

            seekWrapper = new LinearLayout(context);
            params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
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

        View nub = new View(context);
        params = createLayoutParams(dpToPx(60, resources), dpToPx(40, resources));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        nub.setLayoutParams(params);
        nub.setOnTouchListener(new OnNubTouchListener());
        nub.setBackgroundColor(Color.argb(255, 0, 164, 209));
        root.addView(nub);

        return root;
    }

    public void refreshAnimerConfigs() {
        mAnimerObjectsMap = anConfigRegistry.getAllAnimer();
        solverObjectSpinnerAdapter.clear();

        for(int i = 0; i< mAnimerObjectsMap.size(); i++){
            solverObjectSpinnerAdapter.add(String.valueOf(mAnimerObjectsMap.getKey(i)));
        }
        solverObjectSpinnerAdapter.notifyDataSetChanged();
        if (solverObjectSpinnerAdapter.getCount() > 0) {
            mSolverObjectSelectorSpinner.setSelection(0);
            initTypeConfigs();
        }
    }

    private void initTypeConfigs() {
        mSolverTypesMap = anConfigRegistry.getAllSolverTypes();

        solverTypeSpinnerAdapter.clear();
       // mSolverTypes.clear();

        for(int i = 0; i< mSolverTypesMap.size(); i++){
            solverTypeSpinnerAdapter.add(String.valueOf(mSolverTypesMap.getKey(i)));
        }

        solverTypeSpinnerAdapter.notifyDataSetChanged();
        if (solverObjectSpinnerAdapter.getCount() > 0) {
            currentAnimer = (Animer) mAnimerObjectsMap.getValue(0);
            //currentSolver = currentAnimer.getCurrentSolver();
            previousSelectedType = String.valueOf(currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("converter_type"));
            int typeIndex = mSolverTypesMap.getIndexByString(previousSelectedType);
            mSolverTypeSelectorSpinner.setSelection(typeIndex,false);
        }
    }


    private int typeChecker = 0;
    private boolean isFixedSelection = false;

    private class SoverSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            final TextView tv =(TextView) view;
            tv.setTextColor(Color.WHITE);

            if(adapterView == mSolverObjectSelectorSpinner){
                currentAnimer = (Animer) mAnimerObjectsMap.getValue(i);
                //currentSolver = currentAnimer.getCurrentSolver();
                redefineMinMax(currentAnimer.getCurrentSolver());
                updateSeekBars(currentAnimer.getCurrentSolver());

            }
            else if (adapterView == mSolverTypeSelectorSpinner){
                if(typeChecker > 0 &&  currentAnimer.getCurrentSolver() !=null) {
                    if(isFixedSelection){
                        isFixedSelection = false;
                    }
                    else{

                        Animer.AnimerSolver seltectedSolver = (Animer.AnimerSolver) mSolverTypesMap.getValue(i);
                        Log.e("Solver Mode",String.valueOf(seltectedSolver.getSolverMode()));
                        previousSelectedType = String.valueOf(seltectedSolver.getConfigSet().getKeyByString("converter_type"));
                        Log.e("PreviousSelectedType",String.valueOf(previousSelectedType));

//                        currentAnimer.getCurrentSolver().setSolverMode(seltectedSolver.getSolverMode());
//                        currentAnimer.getCurrentSolver().setArg1( seltectedSolver.getArg1());
//                        currentAnimer.getCurrentSolver().setArg2( seltectedSolver.getArg2());
//                        currentAnimer.getCurrentSolver().getConfigSet().cloneConfigFrom(seltectedSolver.getConfigSet().getConfigs());

                        currentAnimer.setSolver(seltectedSolver);

                        redefineMinMax(currentAnimer.getCurrentSolver());
                        updateSeekBars(currentAnimer.getCurrentSolver());
                    }

                }
                typeChecker++;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }


    private void redefineMinMax(Animer.AnimerSolver animerSolver){
        for (int index = 0;index<listSize;index++){
            MAX_VALUES[index] = (float) Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(index+1) +"_max").toString());
            MIN_VALUES[index] = (float) Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(index+1) +"_min").toString());
            RANGE_VALUES[index] = MAX_VALUES[index] - MIN_VALUES[index];
        }
    }

    private void updateSeekBars(Animer.AnimerSolver animerSolver) {
        if((animerSolver.getConfigSet().getKeyByString("converter_type")) !=null){
            currentType = animerSolver.getConfigSet().getKeyByString("converter_type").toString();

            //TODO BUGs here
            if(previousSelectedType != currentType){
                int typeIndex = mSolverTypesMap.getIndexByString(currentType);
                isFixedSelection = true;
                mSolverTypeSelectorSpinner.setSelection(typeIndex,false);
            }
        }

        for(int i = 0;i<listSize;i++){
            SEEKBAR_VALUES[i] = Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(i+1)).toString());
            float progress = (SEEKBAR_VALUES[i] - MIN_VALUES[i])/RANGE_VALUES[i] *(MAX_SEEKBAR_VAL-MIN_SEEKBAR_VAL) + MIN_SEEKBAR_VAL;
            SEEKBARS[i].setProgress((int) progress);
            SEEKBAR_LABElS[i].setText((String) animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(i+1) +"_name") + ": " + (String) animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(i+1)).toString());
        }

    }

    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int val, boolean b) {


            Log.e("On Process Changed","On Process Changed");

            for(int i = 0;i<listSize;i++){
                if (seekBar == SEEKBARS[i]) {
                    SEEKBAR_VALUES[i] = ((float) (val - MIN_SEEKBAR_VAL) / (MAX_SEEKBAR_VAL-MIN_SEEKBAR_VAL))*RANGE_VALUES[i] + MIN_VALUES[i];
                    if(i == 0){
                        String roundedValue1Label = DECIMAL_FORMAT_1.format(SEEKBAR_VALUES[i]);
                        SEEKBAR_LABElS[i].setText((String)  currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("arg" +String.valueOf(i+1) +"_name") + ": " + roundedValue1Label);
                        currentAnimer.getCurrentSolver().getConfigSet().addConfig("arg"+String.valueOf(i+1)+"",Float.valueOf(roundedValue1Label));
                    }
                    else if(i == 1){
                        String roundedValue1Label = DECIMAL_FORMAT.format(SEEKBAR_VALUES[i]);
                        SEEKBAR_LABElS[i].setText((String)  currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("arg" +String.valueOf(i+1) +"_name") + ": " + roundedValue1Label);
                        currentAnimer.getCurrentSolver().getConfigSet().addConfig("arg"+String.valueOf(i+1)+"",Float.valueOf(roundedValue1Label));
                    }

                }
            }

            if(currentType != "AndroidFling"){
                currentAnimer.getCurrentSolver().setArg1(getConvertValueByIndexAndType(0,currentType));
                currentAnimer.getCurrentSolver().setArg2(getConvertValueByIndexAndType(1,currentType));
            }


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }


    private Animer.AnimerSolver getSolverByType(String type){
        switch (type) {
            case "NULL":
                return null;
            case "AndroidInterpolator":
                //TODO
                //return Animer.interpolatorDroid(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
                return null;
            case "AndroidFling":
                return Animer.flingDroid(50,0.99f);
            case "AndroidSpring":
                return Animer.springDroid(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
            case "DHOSpring":
                return Animer.springDHO(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
            case "iOSCoreAnimationSpring":
                return Animer.springiOSCoreAnimation(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
            case "RK4Spring":
                return Animer.springRK4(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
            case "ProtopieSpring":
                return Animer.springProtopie(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
            case "PrincipleSpring":
                return Animer.springPrinciple(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
            case "iOSUIViewSpring":
                return Animer.springiOSUIView(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
            case "OrigamiPOPSpring":
                return Animer.springOrigamiPOP(SEEKBAR_VALUES[0],SEEKBAR_VALUES[1]);
            default:
                return null;
        }
    }


    private Object getConvertValueByIndexAndType(int i,String type){
        switch (type) {
            case "NULL":
                return null;
            case "AndroidInterpolator":
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
                    // velocity here
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
                    // velocity here
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
                    // velocity here
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
                    // velocity here
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
                    // velocity here
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
                    // velocity here
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
                    // velocity here
                    return SEEKBAR_VALUES[i];
                }
            default:
                return SEEKBAR_VALUES[i];
        }
    }

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

    public static int dpToPx(float dp, Resources res) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,res.getDisplayMetrics());
    }

    public static FrameLayout.LayoutParams createLayoutParams(int width, int height) {
        return new FrameLayout.LayoutParams(width, height);
    }
}
