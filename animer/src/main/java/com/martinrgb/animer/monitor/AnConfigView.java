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
    private final List<Animer> mAnimers = new ArrayList<Animer>();
    private final float mStashPx;
    private final float mRevealPx;
    private final AnConfigRegistry anConfigRegistry;
    private final int mTextColor = Color.argb(255, 225, 225, 225);

    private Spinner mSpringSelectorSpinner;
    private Animer mSelectedAnimer;
    private Animer mRevealAnimer;

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
        mRevealPx = dpToPx(40, resources);
        mStashPx = dpToPx(280, resources);

        mRevealAnimer = new Animer();
        mRevealAnimer.setSolver(Animer.springDroid(500,0.95f));
        mRevealAnimer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {
                float val = (float) value;
                float minTranslate = mRevealPx;
                float maxTranslate = mStashPx;
                float range = maxTranslate - minTranslate;
                float yTranslate = (val * range) + minTranslate;

                //float yTranslate = AnUtil.mapValueFromRangeToRange(value,0.f,1.f,0.f,600.f);

                AnConfigView.this.setTranslationY(yTranslate);
            }
        });



        addView(generateHierarchy(context));

        SeekbarListener seekbarListener = new SeekbarListener();
        mArgument1SeekBar.setMax(MAX_SEEKBAR_VAL);
        mArgument1SeekBar.setMin(1);
        mArgument1SeekBar.setOnSeekBarChangeListener(seekbarListener);

        mArgument2SeekBar.setMax(MAX_SEEKBAR_VAL);
        mArgument1SeekBar.setMin(1);
        mArgument2SeekBar.setOnSeekBarChangeListener(seekbarListener);

        mSpringSelectorSpinner.setAdapter(spinnerAdapter);
        mSpringSelectorSpinner.setOnItemSelectedListener(new SpringSelectedListener());
        refreshAnConfigs();

        mRevealAnimer.setCurrentValue(1);
        //this.setTranslationY(mStashPx);
    }

    private View generateHierarchy(Context context) {
        Resources resources = getResources();

        FrameLayout.LayoutParams params;
        int fivePx = dpToPx(5, resources);
        int tenPx = dpToPx(10, resources);
        int twentyPx = dpToPx(20, resources);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f);
        tableLayoutParams.setMargins(0, 0, fivePx, 0);
        LinearLayout seekWrapper;

        FrameLayout root = new FrameLayout(context);
        params = createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(300, resources));
        root.setLayoutParams(params);

        FrameLayout container = new FrameLayout(context);
        params = createMatchParams();
        params.setMargins(0, twentyPx, 0, 0);
        container.setLayoutParams(params);
        container.setBackgroundColor(Color.argb(100, 0, 0, 0));
        root.addView(container);

        mSpringSelectorSpinner = new Spinner(context, Spinner.MODE_DIALOG);
        params = createMatchWrapParams();
        params.gravity = Gravity.TOP;
        params.setMargins(tenPx, tenPx, tenPx, 0);
        mSpringSelectorSpinner.setLayoutParams(params);
        container.addView(mSpringSelectorSpinner);

        LinearLayout linearLayout = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(0, 0, 0, dpToPx(80, resources));
        params.gravity = Gravity.BOTTOM;
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        container.addView(linearLayout);

        seekWrapper = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(tenPx, tenPx, tenPx, twentyPx);
        seekWrapper.setPadding(tenPx, tenPx, tenPx, tenPx);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(seekWrapper);

        mArgument1SeekBar = new SeekBar(context);
        mArgument1SeekBar.setLayoutParams(tableLayoutParams);
        seekWrapper.addView(mArgument1SeekBar);

        mArgument1SeekLabel = new TextView(getContext());
        mArgument1SeekLabel.setTextColor(mTextColor);
        params = createLayoutParams(
                dpToPx(100, resources),
                ViewGroup.LayoutParams.MATCH_PARENT);
        mArgument1SeekLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        mArgument1SeekLabel.setLayoutParams(params);
        mArgument1SeekLabel.setMaxLines(1);
        seekWrapper.addView(mArgument1SeekLabel);

        seekWrapper = new LinearLayout(context);
        params = createMatchWrapParams();
        params.setMargins(tenPx, tenPx, tenPx, twentyPx);
        seekWrapper.setPadding(tenPx, tenPx, tenPx, tenPx);
        seekWrapper.setLayoutParams(params);
        seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(seekWrapper);

        mArgument2SeekBar = new SeekBar(context);
        mArgument2SeekBar.setLayoutParams(tableLayoutParams);
        seekWrapper.addView(mArgument2SeekBar);

        mArgument2SeekLabel = new TextView(getContext());
        mArgument2SeekLabel.setTextColor(mTextColor);
        params = createLayoutParams(dpToPx(100, resources), ViewGroup.LayoutParams.MATCH_PARENT);
        mArgument2SeekLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        mArgument2SeekLabel.setLayoutParams(params);
        mArgument2SeekLabel.setMaxLines(1);
        seekWrapper.addView(mArgument2SeekLabel);

        View nub = new View(context);
        params = createLayoutParams(dpToPx(60, resources), dpToPx(40, resources));
        params.gravity = Gravity.TOP | Gravity.CENTER;
        nub.setLayoutParams(params);
        nub.setOnTouchListener(new OnNubTouchListener());
        nub.setBackgroundColor(Color.argb(255, 0, 164, 209));
        root.addView(nub);

        return root;
    }

    public void refreshAnConfigs() {
        Map<Animer, String> mAnimerMap = anConfigRegistry.getAllAnimer();

        spinnerAdapter.clear();
        mAnimers.clear();

        for (Map.Entry<Animer, String> entry : mAnimerMap.entrySet()) {
            mAnimers.add(entry.getKey());
            spinnerAdapter.add(entry.getValue());
        }
        // Add the default config in last.
        spinnerAdapter.notifyDataSetChanged();
        if (mAnimers.size() > 0) {
            mSpringSelectorSpinner.setSelection(0);
        }
    }

    private class SpringSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mSelectedAnimer = mAnimers.get(i);
            updateSeekBarsForAnimer(mSelectedAnimer);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private static final int MAX_SEEKBAR_VAL = 3000;
    private static final float MIN_TENSION = 0;
    private static final float MAX_TENSION = 200;
    private static final float MIN_FRICTION = 0;
    private static final float MAX_FRICTION = 50;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private SeekBar mArgument1SeekBar;
    private SeekBar mArgument2SeekBar;
    private TextView mArgument1SeekLabel;
    private TextView mArgument2SeekLabel;

    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int val, boolean b) {

            if (seekBar == mArgument1SeekBar) {
                //float scaledTension = ((val) * tensionRange) / MAX_SEEKBAR_VAL + MIN_TENSION;
                float value1 = val;
                mSelectedAnimer.setArgument1(value1);
                String roundedTensionLabel = DECIMAL_FORMAT.format(value1);
                mArgument1SeekLabel.setText("Arg1:" + roundedTensionLabel);
            }

            if (seekBar == mArgument2SeekBar) {
                //float scaledFriction = ((val) * frictionRange) / MAX_SEEKBAR_VAL + MIN_FRICTION;
                float value2 = val;
                mSelectedAnimer.setArgument2(value2);
                String roundedFrictionLabel = DECIMAL_FORMAT.format(value2);
                mArgument2SeekLabel.setText("Arg2:" + roundedFrictionLabel);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private void updateSeekBarsForAnimer(Animer animer) {
        float value1 = (float) animer.getArgument1();
//        float tensionRange = MAX_TENSION - MIN_TENSION;
//        int scaledTension = Math.round(((tension - MIN_TENSION) * MAX_SEEKBAR_VAL) / tensionRange);

        float value2 = (float) animer.getArgument2();
//        float frictionRange = MAX_FRICTION - MIN_FRICTION;
//        int scaledFriction = Math.round(((friction - MIN_FRICTION) * MAX_SEEKBAR_VAL) / frictionRange);

        mArgument1SeekBar.setProgress((int)value1);
        mArgument2SeekBar.setProgress((int)value2);
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
