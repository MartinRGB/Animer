package com.martinrgb.animer.monitor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.martinrgb.animer.Animer;
import com.martinrgb.animer.R;
import com.martinrgb.animer.core.interpolator.AnInterpolator;
import com.martinrgb.animer.core.math.converter.DHOConverter;
import com.martinrgb.animer.core.math.converter.OrigamiPOPConverter;
import com.martinrgb.animer.core.math.converter.RK4Converter;
import com.martinrgb.animer.core.math.converter.UIViewSpringConverter;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class AnConfigView extends FrameLayout {

    private Spinner mSolverObjectSelectorSpinner,mSolverTypeSelectorSpinner;
    private AnSpinnerAdapter solverObjectSpinnerAdapter,solverTypeSpinnerAdapter;
    private Animer currentAnimer,mRevealAnimer;
    private AnConfigRegistry anConfigRegistry;
    private LinearLayout listLayout;
    private SeekbarListener seekbarListener;
    private SoverSelectedListener soverSelectedListener;
    private final int mTextColor = Color.argb(255, 255, 255, 255);

    private String currentObjectType = "NULL";

    private int listSize = 2;
    private static int SEEKBAR_START_ID = 15000;
    private static int SEEKLABEL_START_ID_START_ID = 20000;
    private static int EDITTEXT_START_ID_START_ID = 20000;
    private static final int MAX_SEEKBAR_VAL = 100000;
    private static final int MIN_SEEKBAR_VAL = 1;
    private static final DecimalFormat DECIMAL_FORMAT_2 = new DecimalFormat("#.##");
    private static final DecimalFormat DECIMAL_FORMAT_1 = new DecimalFormat("#.#");
    private float MAX_VAL1,MAX_VAL2,MAX_VAL3,MAX_VAL4,MAX_VAL5;
    private float[] MAX_VALUES = new float[]{MAX_VAL1,MAX_VAL2,MAX_VAL3,MAX_VAL4,MAX_VAL5};
    private float MIN_VAL1,MIN_VAL2,MIN_VAL3,MIN_VAL4,MIN_VAL5;
    private float[] MIN_VALUES = new float[]{MIN_VAL1,MIN_VAL2,MIN_VAL3,MIN_VAL4,MIN_VAL5};
    private float RANGE_VAL1,RANGE_VAL2,RANGE_VAL3,RANGE_VAL4,RANGE_VAL5;
    private float[] RANGE_VALUES = new float[]{RANGE_VAL1,RANGE_VAL2,RANGE_VAL3,RANGE_VAL4,RANGE_VAL5};
    private float seekBarValue1,seekBarValue2,seekBarValue3,seekBarValue4,seekBarValue5;
    private Object[] SEEKBAR_VALUES = new Object[]{seekBarValue1,seekBarValue2,seekBarValue3,seekBarValue4,seekBarValue5};
    private TextView mArgument1SeekLabel,mArgument2SeekLabel,mArgument3SeekLabel,mArgument4SeekLabel,mArgument5SeekLabel;
    private TextView[] SEEKBAR_LABElS = new TextView[]{mArgument1SeekLabel,mArgument2SeekLabel,mArgument3SeekLabel,mArgument4SeekLabel,mArgument5SeekLabel};
    private EditText mArgument1EditText,mArgument2EditText,mArgument3EditText,mArgument4EditText,mArgument5EditText;
    private EditText[] EDITTEXTS = new EditText[]{mArgument1EditText,mArgument2EditText,mArgument3EditText,mArgument4EditText,mArgument5EditText};
    private SeekBar mArgument1SeekBar,mArgument2SeekBar,mArgument3SeekBar,mArgument4SeekBar,mArgument5SeekBar;
    private SeekBar[] SEEKBARS = new SeekBar[]{mArgument1SeekBar,mArgument2SeekBar,mArgument3SeekBar,mArgument4SeekBar,mArgument5SeekBar};

    private final int MARGIN_SIZE = (int) getResources().getDimension(R.dimen.margin_size);
    private final int PADDING_SIZE = (int) getResources().getDimension(R.dimen.padding_size);
    private final int PX_120 = dpToPx(120, getResources());

    private ANConfigMap<String,Animer.AnimerSolver> mSolverTypesMap;
    private ANConfigMap<String,Animer> mAnimerObjectsMap;

    private Context mContext;

    public AnConfigView(Context context) {
        this(context, null);
    }

    public AnConfigView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnConfigView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);

    }

    private void initView(Context context) {
        View view = inflate(getContext(), R.layout.config_view, null);
        addView(view);

        mContext = context;

        // ## Spinner
        anConfigRegistry = AnConfigRegistry.getInstance();

        solverObjectSpinnerAdapter = new AnSpinnerAdapter(context,getResources());
        solverTypeSpinnerAdapter = new AnSpinnerAdapter(context,getResources());

        mSolverObjectSelectorSpinner = findViewById(R.id.object_spinner);
        mSolverTypeSelectorSpinner = findViewById(R.id.type_spinner);

        soverSelectedListener = new SoverSelectedListener();
        seekbarListener = new SeekbarListener();

        mSolverObjectSelectorSpinner.setAdapter(solverObjectSpinnerAdapter);
        mSolverObjectSelectorSpinner.setOnItemSelectedListener(soverSelectedListener);

        mSolverTypeSelectorSpinner.setAdapter(solverTypeSpinnerAdapter);
        mSolverTypeSelectorSpinner.setOnItemSelectedListener(soverSelectedListener);

        refreshAnimerConfigs();

        // ## List
        listLayout = findViewById(R.id.list_layout);

        // ## Nub
        View nub = findViewById(R.id.nub);
        nub.setOnTouchListener(new OnNubTouchListener());

        ViewTreeObserver vto = view.getViewTreeObserver();
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
                        float maxTranslate = view.getMeasuredHeight() - getResources().getDimension(R.dimen.nub_height);
                        float range = maxTranslate - minTranslate;
                        float yTranslate = -(val * range) + minTranslate;
                        AnConfigView.this.setTranslationY(yTranslate);
                    }
                });
            }
        });

        this.setElevation(1000);
    }


    public void refreshAnimerConfigs() {
        mAnimerObjectsMap = anConfigRegistry.getAllAnimer();
        solverObjectSpinnerAdapter.clear();

        for(int i = 0; i< mAnimerObjectsMap.size(); i++){
            solverObjectSpinnerAdapter.add(String.valueOf(mAnimerObjectsMap.getKey(i)));
        }
        solverObjectSpinnerAdapter.notifyDataSetChanged();
        if (solverObjectSpinnerAdapter.getCount() > 0) {
            // object first time selection
            mSolverObjectSelectorSpinner.setSelection(0);
            initTypeConfigs();
        }
    }

    private void initTypeConfigs() {
        mSolverTypesMap = anConfigRegistry.getAllSolverTypes();
        solverTypeSpinnerAdapter.clear();

        for(int i = 0; i< mSolverTypesMap.size(); i++){
            solverTypeSpinnerAdapter.add(String.valueOf(mSolverTypesMap.getKey(i)));
        }

        solverTypeSpinnerAdapter.notifyDataSetChanged();
        if (solverObjectSpinnerAdapter.getCount() > 0) {
            // solver first time selection
            currentAnimer = (Animer) mAnimerObjectsMap.getValue(0);

            currentAnimer.setUpdateListener(new Animer.UpdateListener() {
                @Override
                public void onUpdate(float value, float velocity, float progress) {
                    Log.e("mvalue",String.valueOf(value));
                }
            });
            recreateList();
            int typeIndex = 0;
            // select the right interpolator
            if(String.valueOf(currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("converter_type")) == "AndroidInterpolator"){
                typeIndex = mSolverTypesMap.getIndexByString(String.valueOf(currentAnimer.getCurrentSolver().getArg1().getClass().getSimpleName()));
            }
            // select the right animator
            else{
                typeIndex = mSolverTypesMap.getIndexByString(String.valueOf(currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("converter_type")));
            }
            mSolverTypeSelectorSpinner.setSelection(typeIndex,false);
        }
    }

    private void recreateList(){
        FrameLayout.LayoutParams params;
        LinearLayout seekWrapper;
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        tableLayoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);


        listLayout.removeAllViews();
        if(currentAnimer.getCurrentSolverData().getKeyByString("converter_type").toString() != "AndroidInterpolator") {
            listSize =2;
        }
        else{
            AnInterpolator mInterpolator = (AnInterpolator) currentAnimer.getCurrentSolver().getArg1();
            listSize = 1 + (mInterpolator.getArgNum()) ;
        }
        for (int i = 0;i<listSize;i++){
            seekWrapper = new LinearLayout(mContext);
            params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);
            seekWrapper.setPadding(PADDING_SIZE, PADDING_SIZE, PADDING_SIZE, PADDING_SIZE);
            seekWrapper.setLayoutParams(params);
            seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
            //seekWrapper.setBackgroundColor(Color.BLACK);
            listLayout.addView(seekWrapper);

            SEEKBAR_LABElS[i] = new TextView(getContext());
            params = createLayoutParams(dpToPx(84,getResources()), ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);
            SEEKBAR_LABElS[i].setLayoutParams(params);
            SEEKBAR_LABElS[i].setPadding(PADDING_SIZE + dpToPx(8,getResources()), PADDING_SIZE, PADDING_SIZE, PADDING_SIZE);
            SEEKBAR_LABElS[i].setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            SEEKBAR_LABElS[i].setTextColor(mTextColor);
            SEEKBAR_LABElS[i].setTextSize(11);
            SEEKBAR_LABElS[i].setMaxLines(1);
            SEEKBAR_LABElS[i].setId(SEEKLABEL_START_ID_START_ID + i);
            seekWrapper.addView(SEEKBAR_LABElS[i]);

            EDITTEXTS[i] = new EditText(getContext());
            params = createLayoutParams(dpToPx(56,getResources()),ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(MARGIN_SIZE,MARGIN_SIZE,MARGIN_SIZE,MARGIN_SIZE);
            EDITTEXTS[i].setLayoutParams(params);
            EDITTEXTS[i].setPadding(PADDING_SIZE,PADDING_SIZE, PADDING_SIZE, PADDING_SIZE);
            EDITTEXTS[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            EDITTEXTS[i].setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            EDITTEXTS[i].setTextAlignment(TEXT_ALIGNMENT_CENTER);
            EDITTEXTS[i].setHint("0");
            EDITTEXTS[i].setHintTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            EDITTEXTS[i].setBackground(ContextCompat.getDrawable(mContext,R.drawable.ic_edit_border));
            EDITTEXTS[i].setGravity(Gravity.LEFT);

            EDITTEXTS[i].addTextChangedListener(new EditTextListener(EDITTEXTS[i],i));

            //TODO Refelection for old version
//            EDITTEXTS[i].setTextCursorDrawable(ContextCompat.getDrawable(mContext,R.drawable.text_cursor));

            EDITTEXTS[i].setTextSize(10);
            seekWrapper.addView(EDITTEXTS[i]);

            SEEKBARS[i] =  new SeekBar(mContext);
            params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);
            SEEKBARS[i].setLayoutParams(params);
            SEEKBARS[i].setPadding(PADDING_SIZE+ dpToPx(8,getResources()), PADDING_SIZE, PADDING_SIZE + dpToPx(16,getResources()), PADDING_SIZE);
            SEEKBARS[i].setId(SEEKBAR_START_ID + i);
            SEEKBARS[i].setProgressBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorWhite)));
            SEEKBARS[i].setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.mainColor)));
            SEEKBARS[i].setThumb(ContextCompat.getDrawable(mContext,R.drawable.ic_thumb));
            //SEEKBARS[i].setBackgroundColor(Color.RED);
            seekWrapper.addView(SEEKBARS[i]);

            SEEKBARS[i].setMax(MAX_SEEKBAR_VAL);
            SEEKBARS[i].setMin(MIN_SEEKBAR_VAL);
            SEEKBARS[i].setOnSeekBarChangeListener(seekbarListener);

        }
    }


    private int typeChecker,objectChecker = 0;
    private boolean typeSpinnerIsFixedSelection = false;

    private class SoverSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            final TextView tv =(TextView) view;
            tv.setTextColor(Color.WHITE);

            if(adapterView == mSolverObjectSelectorSpinner){
                // get animer from Map
                currentAnimer = (Animer) mAnimerObjectsMap.getValue(i);
                recreateList();
                redefineMinMax(currentAnimer.getCurrentSolver());
                updateSeekBars(currentAnimer.getCurrentSolver());

                // will not excute in init
                if(objectChecker > 0){
                    typeSpinnerIsFixedSelection = true;
                    int typeIndex;
                    // select the right interpolator
                    if(String.valueOf(currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("converter_type")) == "AndroidInterpolator"){
                        typeIndex = mSolverTypesMap.getIndexByString(String.valueOf(currentAnimer.getCurrentSolver().getArg1().getClass().getSimpleName()));
                    }
                    // select the right animator
                    else{
                        typeIndex = mSolverTypesMap.getIndexByString(currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("converter_type").toString());
                    }
                    mSolverTypeSelectorSpinner.setSelection(typeIndex,false);
                }

                objectChecker++;
            }
            else if (adapterView == mSolverTypeSelectorSpinner){
                // will not excute in init
                if(typeChecker > 0) {
                    if(typeSpinnerIsFixedSelection){
                        typeSpinnerIsFixedSelection = false;
                    }
                    else{
                        // reset animer from Map
                        Animer.AnimerSolver seltectedSolver = (Animer.AnimerSolver) mSolverTypesMap.getValue(i);
                        currentAnimer.setSolver(seltectedSolver);
                        recreateList();
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
        currentObjectType = animerSolver.getConfigSet().getKeyByString("converter_type").toString();

        if(currentObjectType != "AndroidInterpolator"){
            for (int index = 0;index<listSize;index++){
                    MAX_VALUES[index] = Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(index+1) +"_max").toString());
                    MIN_VALUES[index] = Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(index+1) +"_min").toString());
                    RANGE_VALUES[index] = MAX_VALUES[index] - MIN_VALUES[index];
            }
        }
        else{
            for (int index = 0;index<listSize-1;index++){

                MAX_VALUES[index] = ((AnInterpolator) currentAnimer.getCurrentSolver().getArg1()).getArgMax(index);;
                MIN_VALUES[index] =  ((AnInterpolator) currentAnimer.getCurrentSolver().getArg1()).getArgMin(index);;
                RANGE_VALUES[index] = MAX_VALUES[index] - MIN_VALUES[index];
            }

            MAX_VALUES[listSize-1] = Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(2) +"_max").toString());
            MIN_VALUES[listSize-1] = Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(2) +"_min").toString());
            RANGE_VALUES[listSize -1] = MAX_VALUES[listSize -1] - MIN_VALUES[listSize - 1];
        }
    }

    private void updateSeekBars(Animer.AnimerSolver animerSolver) {

        if(currentObjectType != "AndroidInterpolator") {
            for(int i = 0;i<listSize;i++){
                    SEEKBAR_VALUES[i] = Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(i + 1)).toString());
                    float progress = ((float) SEEKBAR_VALUES[i] - MIN_VALUES[i]) / RANGE_VALUES[i] * (MAX_SEEKBAR_VAL - MIN_SEEKBAR_VAL) + MIN_SEEKBAR_VAL;
                    SEEKBARS[i].setProgress((int) progress);
                    SEEKBAR_LABElS[i].setText((String) animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(i + 1) + "_name") + ": ");
                    EDITTEXTS[i].setText(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(i + 1)).toString());
            }

        }
        else{

            for (int index = 0;index<listSize-1;index++){
                SEEKBAR_VALUES[index] = ((AnInterpolator) currentAnimer.getCurrentSolver().getArg1()).getArgValue(index);
                float progress = ((float) SEEKBAR_VALUES[index] - MIN_VALUES[index]) / RANGE_VALUES[index] * (MAX_SEEKBAR_VAL - MIN_SEEKBAR_VAL) + MIN_SEEKBAR_VAL;
                SEEKBARS[index].setProgress((int) progress);
                SEEKBAR_LABElS[index].setText(((AnInterpolator) currentAnimer.getCurrentSolver().getArg1()).getArgString(index) + ": ");
                EDITTEXTS[index].setText(String.valueOf(((AnInterpolator) currentAnimer.getCurrentSolver().getArg1()).getArgValue(index)));
            }

            SEEKBAR_VALUES[listSize-1] = Float.valueOf(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(2)).toString());
            float progress = ((float) SEEKBAR_VALUES[listSize-1] - MIN_VALUES[listSize-1]) / RANGE_VALUES[listSize-1] * (MAX_SEEKBAR_VAL - MIN_SEEKBAR_VAL) + MIN_SEEKBAR_VAL;
            SEEKBARS[listSize-1].setProgress((int) progress);
            SEEKBAR_LABElS[listSize-1].setText((String) animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(2) + "_name") + ": ");
            EDITTEXTS[listSize-1].setText(animerSolver.getConfigSet().getKeyByString("arg" + String.valueOf(2)).toString());
        }

    }

    private boolean isEditListenerWork = true;
    private boolean canSetEditText = false;
    private class EditTextListener implements TextWatcher{
        private EditText mEditText;
        private int mIndex;
        public EditTextListener(EditText editText,int index) {
            mEditText = editText;
            mIndex = index;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Log.e("BeforeChange",String.valueOf(s));
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Log.e("onTextChanged",String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {
            String mString = String.valueOf(s);

            if (mString.isEmpty()) {
                //Do Nothing
            } else {
                //Code to perform calculations
                if(isEditListenerWork){
                    Log.e("Changed",String.valueOf("Changed"));
                    float calculatedProgress = (Float.valueOf(String.valueOf(s)) - MIN_VALUES[mIndex])/ RANGE_VALUES[mIndex]* (MAX_SEEKBAR_VAL - MIN_SEEKBAR_VAL) + MIN_SEEKBAR_VAL;
                    canSetEditText = false;
                    SEEKBARS[mIndex].setProgress((int) calculatedProgress);
                    canSetEditText = true;
                }
//                Log.e("Changed",String.valueOf("Changed"));
//                float calculatedProgress = (Float.valueOf(String.valueOf(s)) - MIN_VALUES[mIndex])/ RANGE_VALUES[mIndex]* (MAX_SEEKBAR_VAL - MIN_SEEKBAR_VAL) + MIN_SEEKBAR_VAL;
//                canSetEditText = false;
//                SEEKBARS[mIndex].setProgress((int) calculatedProgress);
//                canSetEditText = true;
            }
        }
    }

    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int val, boolean b) {

            Log.e("On Process Changed","On Process Changed");

            if(currentObjectType != "AndroidInterpolator") {
                for (int i = 0; i < listSize; i++) {
                    if (seekBar == SEEKBARS[i]) {
                        SEEKBAR_VALUES[i] = ((float) (val - MIN_SEEKBAR_VAL) / (MAX_SEEKBAR_VAL - MIN_SEEKBAR_VAL)) * RANGE_VALUES[i] + MIN_VALUES[i];
                        if (i == 0) {
                            String roundedValue1Label = DECIMAL_FORMAT_1.format(SEEKBAR_VALUES[i]);
                            SEEKBAR_LABElS[i].setText((String) currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("arg" + String.valueOf(i + 1) + "_name") + ": ");
                            if(canSetEditText){
                                EDITTEXTS[i].setText(roundedValue1Label);
                            }

                            currentAnimer.getCurrentSolver().getConfigSet().addConfig("arg" + String.valueOf(i + 1) + "", Float.valueOf(roundedValue1Label));
                        } else if (i == 1) {
                            String roundedValue1Label = DECIMAL_FORMAT_2.format(SEEKBAR_VALUES[i]);
                            SEEKBAR_LABElS[i].setText((String) currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("arg" + String.valueOf(i + 1) + "_name") + ": ");
                            if(canSetEditText){
                                EDITTEXTS[i].setText(roundedValue1Label);
                            }
                            currentAnimer.getCurrentSolver().getConfigSet().addConfig("arg" + String.valueOf(i + 1) + "", Float.valueOf(roundedValue1Label));
                        }

//                        Log.e("progress",String.valueOf(seekBar.getProgress()));
//                        Log.e("val",String.valueOf(val));
//                        Log.e("real value",String.valueOf(SEEKBAR_VALUES[i]));
                    }
                }
                // Seekbar in Fling not works at all
                if (currentObjectType != "AndroidFling") {
                    currentAnimer.getCurrentSolver().setArg1(getConvertValueByIndexAndType(0, currentObjectType));
                    currentAnimer.getCurrentSolver().setArg2(getConvertValueByIndexAndType(1, currentObjectType));
                }


            }
            else{
                // Interpolator Factor
                for (int i = 0; i < listSize - 1; i++) {
                    if (seekBar == SEEKBARS[i]) {
                        SEEKBAR_VALUES[i] = ((float) (val - MIN_SEEKBAR_VAL) / (MAX_SEEKBAR_VAL - MIN_SEEKBAR_VAL)) * RANGE_VALUES[i] + MIN_VALUES[i];
                        String roundedValue1Label = DECIMAL_FORMAT_2.format(SEEKBAR_VALUES[i]);
                        SEEKBAR_LABElS[i].setText(((AnInterpolator) currentAnimer.getCurrentSolver().getArg1()).getArgString(i) + ": ");
                        if(canSetEditText){
                            EDITTEXTS[i].setText(roundedValue1Label);
                        }
                        ((AnInterpolator) currentAnimer.getCurrentSolver().getArg1()).resetArgValue(i,Float.valueOf(roundedValue1Label));
                    }
                }

                // Interpolator Duration
                if (seekBar == SEEKBARS[listSize - 1]) {
                    SEEKBAR_VALUES[listSize - 1] = ((float) (val - MIN_SEEKBAR_VAL) / (MAX_SEEKBAR_VAL - MIN_SEEKBAR_VAL)) * RANGE_VALUES[listSize - 1] + MIN_VALUES[listSize - 1];
                    String roundedValue1Label = DECIMAL_FORMAT_1.format(SEEKBAR_VALUES[listSize - 1]);
                    SEEKBAR_LABElS[listSize - 1].setText((String) currentAnimer.getCurrentSolver().getConfigSet().getKeyByString("arg" + String.valueOf(2) + "_name") + ": ");
                    if(canSetEditText){
                        EDITTEXTS[listSize - 1].setText(roundedValue1Label);
                    }
                    currentAnimer.getCurrentSolver().getConfigSet().addConfig("arg" + String.valueOf(2) + "", Float.valueOf(roundedValue1Label));
                    float floatVal = Float.valueOf(roundedValue1Label);
                    currentAnimer.getCurrentSolver().setArg2( (long) floatVal);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.e("Touched",String.valueOf("Touched"));
            isEditListenerWork = false;
            canSetEditText = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.e("Stoped",String.valueOf("Stoped"));
            isEditListenerWork = true;
            canSetEditText = false;
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
                DHOConverter dhoConverter = new DHOConverter((float)SEEKBAR_VALUES[0],(float)SEEKBAR_VALUES[1]);
                return dhoConverter.getArg(i);
            case "iOSCoreAnimationSpring":
                DHOConverter iOSCASpring = new DHOConverter((float)SEEKBAR_VALUES[0],(float)SEEKBAR_VALUES[1]);
                return iOSCASpring.getArg(i);
            case "RK4Spring":
                RK4Converter rk4Converter = new RK4Converter((float)SEEKBAR_VALUES[0],(float)SEEKBAR_VALUES[1]);
                return rk4Converter.getArg(i);
            case "ProtopieSpring":
                RK4Converter protopieConverter = new RK4Converter((float)SEEKBAR_VALUES[0],(float)SEEKBAR_VALUES[1]);
                return protopieConverter.getArg(i);
            case "PrincipleSpring":
                RK4Converter principleConverter = new RK4Converter((float)SEEKBAR_VALUES[0],(float)SEEKBAR_VALUES[1]);
                return principleConverter.getArg(i);
            case "iOSUIViewSpring":
                UIViewSpringConverter uiViewSpringConverter = new UIViewSpringConverter((float)SEEKBAR_VALUES[0],(float)SEEKBAR_VALUES[1]);
                return uiViewSpringConverter.getArg(i);
            case "OrigamiPOPSpring":
                OrigamiPOPConverter origamiPOPConverter = new OrigamiPOPConverter((float)SEEKBAR_VALUES[0],(float)SEEKBAR_VALUES[1]);
                return origamiPOPConverter.getArg(i);
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
