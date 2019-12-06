package com.martinrgb.animer.monitor.fps;


import android.content.Context;

/**
 * Created by brianplummer on 8/29/15.
 */
public class FPSDetector
{
    public static FPSBuilder create(){
        return new FPSBuilder();
    }

    public static void hide(Context context) {
        FPSBuilder.hide(context.getApplicationContext());
    }

}