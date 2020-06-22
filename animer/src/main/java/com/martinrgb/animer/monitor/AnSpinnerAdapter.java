package com.martinrgb.animer.monitor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.martinrgb.animer.R;

import java.util.ArrayList;
import java.util.List;

public class AnSpinnerAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<String> mStrings;
    private final Resources mResources;
    //private final int mTextColor = Color.argb(255, 255, 255, 255);

    private int mTextColor;

    public AnSpinnerAdapter(Context context,Resources resources) {
        mTextColor = ContextCompat.getColor(context, R.color.secondaryColor);
        mContext = context;
        mResources = resources;
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
            int twelvePx = dpToPx(12, mResources);
            int ninePx = dpToPx(9, mResources);
            textView.setPadding(dpToPx(32,mResources), ninePx, dpToPx(32,mResources),ninePx);
            textView.setTextColor(mTextColor);
            textView.setTextSize(10);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(mStrings.get(position));
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View v = null;
        v = super.getDropDownView(position, null, parent);
        // If this is the selected item position
        if (position == seletecedIndex) {
            v.setAlpha(1.f);
        }
        else {
            v.setAlpha(0.5f);
        }
        return v;
    }

    private int seletecedIndex = -1;
    public void setSelectedItemIndex(int i){
        seletecedIndex = i;
    }

    public static int dpToPx(float dp, Resources res) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                res.getDisplayMetrics());
    }
}

