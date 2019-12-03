package com.martinrgb.animer.monitor.shader;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


import com.martinrgb.animer.monitor.shader.util.FPSCounter;
import com.martinrgb.animer.monitor.shader.util.LoggerConfig;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class ShaderRenderer implements GLSurfaceView.Renderer {

	private final float resolution[] = new float[]{0,0};
	private long startTime;
	private static final float NS_PER_SECOND = 1000000000f;

	private final Context context;
	private ShaderProgram shaderProgram;

	public ShaderRenderer(Context context) {
		this.context = context;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearColor(0f, 0f, 0f, 0f);
		shaderProgram = new ShaderProgram(context);
		shaderProgram.setOnCreate();
//		setFactorInput(mFactor1,0);
//		setFactorInput(mFactor2,1);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		startTime =  System.nanoTime();
		resolution[0] = width;
		resolution[1] = height;
		shaderProgram.setOnChange(width,height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		float time = (System.nanoTime() - startTime) / NS_PER_SECOND;
		shaderProgram.setOnDrawFrame(resolution,time,factors,mode,duration);
		if(LoggerConfig.ON == true){
			FPSCounter.logFrameRate();
		}
	}

	private float factors[] = new float[32];
	private float mode;
	private float duration;
//	private float mFactor1 = 1500;
//	private float mFactor2 = 0.5f;

	public void setFactorInput(float factor,int i){
		factors[i] = factor;
	}

	public void setCurveMode(float i){
		mode = i ;
	}

    public void setDuration(float i){
        duration = i ;
    }


	public void resetTime(){
		startTime = System.nanoTime();
	}

}
