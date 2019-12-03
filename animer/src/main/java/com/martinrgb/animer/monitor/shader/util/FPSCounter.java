package com.martinrgb.animer.monitor.shader.util;

import android.os.SystemClock;
import android.util.Log;

public class FPSCounter {
	private static long startTimeMs;

	private static int frameCount;

	public static void logFrameRate(){
		final long elapsedRealtimeMs = SystemClock.elapsedRealtime();
		final double elapsedSeconds = (elapsedRealtimeMs - startTimeMs)/1000.0;

		if(elapsedSeconds >= 1.0){
			Log.v("Current FPS is ",frameCount/elapsedSeconds + "fps");
			startTimeMs = SystemClock.elapsedRealtime();
			frameCount = 0;
		}
		frameCount ++;
	}

	public static int getFPS() {
		return frameCount;
	}
}