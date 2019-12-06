package com.martinrgb.animer.monitor.shader;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class ShaderSurfaceView extends GLSurfaceView {
	private ShaderRenderer renderer;

	public ShaderSurfaceView(Context context) {
		super(context);
		setRenderer(context);
	}

	public ShaderSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setRenderer(context);
	}
	private void setRenderer(Context context) {
		renderer = new ShaderRenderer(context);
		setEGLContextClientVersion(2);
		setZOrderOnTop(true);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		getHolder().setFormat(PixelFormat.RGBA_8888);
		setRenderer(renderer);
		//TODO Request Renderer
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

	}

	public ShaderRenderer getRenderer() {
		return renderer;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void setFactorInput(float factor,int i){
		renderer.setFactorInput(factor,i);
	}

	public void setCurveMode(float i){
		renderer.setCurveMode(i);
	}

	public void setDuration(float i){
		renderer.setDuration(i);
	}

	public void resetTime(){renderer.resetTime();}


}
