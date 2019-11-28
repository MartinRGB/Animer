package com.martinrgb.animer.monitor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class AnSpinnerAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<String> mStrings;
    private final Resources mResources;
    private final int mTextColor = Color.argb(255, 0, 0, 0);
    private String selectedString;

    public AnSpinnerAdapter(Context context,Resources resources) {
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
            textView.setPadding(twelvePx, twelvePx, twelvePx,twelvePx);
            textView.setTextColor(mTextColor);
            textView.setTextSize(11);
            selectedString = textView.getText().toString();
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(mStrings.get(position));
        return textView;
    }

    public static int dpToPx(float dp, Resources res) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                res.getDisplayMetrics());
    }
}

