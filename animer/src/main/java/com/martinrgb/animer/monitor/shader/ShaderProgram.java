package com.martinrgb.animer.monitor.shader;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martinrgb.animer.R;
import com.martinrgb.animer.monitor.shader.util.ShaderHelper;
import com.martinrgb.animer.monitor.shader.util.TextReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;

public class ShaderProgram {


    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private final int mPositionDataSize = 4;
    private final int mBytesPerFloat = 4;
    private FloatBuffer vertexBuffer;

    public static final String ATTRIBUTE_POSITION = "a_position";
    public static final String UNIFORM_RESOLUTION = "u_resolution";
    public static final String UNIFORM_TIME = "u_time";
    public static final String UNIFORM_FACTOR = "u_factor";
    public static final String UNIFORM_MVP = "u_MVPMatrix";
    public static final String UNIFORM_MODE = "u_mode";
    public static final String UNIFORM_DURATION = "u_duration";

    private int program = 0;
    private int positionLoc;
    private int resolutionLoc;
    private int modeLoc;
    private int timeLoc;
    private int durationLoc;
    private static int factorLength = 5;
    private final int factorLocs[] = new int[32];
    private int mvpLoc;

    public ShaderProgram(Context context) {
        if (program != 0) { program = 0; }
        program = ShaderHelper.buildProgram(
                TextReader.readTextFileFromResource(context,R.raw.simplevert),
                TextReader.readTextFileFromResource(context,R.raw.simplefrag));
    }

    public void setOnCreate(){
        positionLoc = GLES20.glGetAttribLocation(program, ATTRIBUTE_POSITION);
        resolutionLoc = GLES20.glGetUniformLocation(program, UNIFORM_RESOLUTION);
        timeLoc = GLES20.glGetUniformLocation(program, UNIFORM_TIME);
        mvpLoc = GLES20.glGetUniformLocation(program, UNIFORM_MVP);
        modeLoc = GLES20.glGetUniformLocation(program, UNIFORM_MODE);
        durationLoc = GLES20.glGetUniformLocation(program,UNIFORM_DURATION);
        for (int i = 0;i < factorLength;i++ ) {
            factorLocs[i] = GLES20.glGetUniformLocation(program,UNIFORM_FACTOR + (i+1));
        }

        final float[] verticesData = {
                1.0f,-1.0f,0.0f,1.0f,
                -1.0f,-1.0f,0.0f,1.0f,
                1.0f,1.0f,0.0f,1.0f,
                -1.0f,1.0f,0.0f,1.0f};

        vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(verticesData).position(0);
        GLES20.glEnableVertexAttribArray(positionLoc);

        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.0f;
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    public void setOnChange(int width, int height){
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void setOnDrawFrame(float[] resolution,float time,float[] factors,float mode,float duration){
        // ######################### clear the canvas #########################
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // #########################   first program  #########################
        GLES20.glUseProgram(program);
        if (positionLoc > -1){ GLES20.glVertexAttribPointer(positionLoc, mPositionDataSize, GLES20.GL_FLOAT, false,0, vertexBuffer); }
        if (resolutionLoc > -1) { GLES20.glUniform2fv(resolutionLoc,1,resolution,0); }
        if (timeLoc > -1) { GLES20.glUniform1f(timeLoc,time); }
        if (modeLoc > -1) { GLES20.glUniform1f(modeLoc,mode); }
        if (durationLoc > -1) { GLES20.glUniform1f(durationLoc,duration); }

        //5 Factors
        if (factorLocs != null){ for (int i = 0; i < factorLocs.length; ++i) {GLES20.glUniform1f(factorLocs[i],factors[i]);} }

        //Draw the triangle facing straight on.
        if(mvpLoc > -1){
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(mvpLoc, 1, false, mMVPMatrix, 0);
        }


        GLES20.glViewport(0,0,(int) resolution[0],(int) resolution[1]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GL_TRIANGLE_STRIP,0,4);
    }
}
