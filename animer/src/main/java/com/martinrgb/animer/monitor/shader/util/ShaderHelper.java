package com.martinrgb.animer.monitor.shader.util;

import android.opengl.GLES20;
import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glValidateProgram;

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    //构建顶点着色器对象
    public static int complieVertexShader(String shaderCode){
        return compileShader(GLES20.GL_VERTEX_SHADER,shaderCode);
    }

    //构建片段着色器对象
    public static int complieFragmentShader(String shaderCode){
        return compileShader(GLES20.GL_FRAGMENT_SHADER,shaderCode);
    }

    //构建单个着色器对象
    private static int compileShader(int type,String shaderCode){

        //glCreateShader 构建了着色器对象，并把 ID 存入shaderObjectID
        final int shaderObjectId = GLES20.glCreateShader(type);

        if(shaderObjectId == 0){
            if(LoggerConfig.ON){
                Log.w(TAG,"Could not create new shader");
            }
            return  0;
        }

        //把着色器源代码传入着色器对象里
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        //编译着色器
        GLES20.glCompileShader(shaderObjectId);

        //检测编译是否成功
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId,GL_COMPILE_STATUS,compileStatus,0);

        //检测连接成功失败的日志

        //如果编译好了，给出ID
        if (LoggerConfig.ON) {
            Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" + GLES20.glGetShaderInfoLog(
                    shaderObjectId));
        }

        //如果编译失败
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed");
            }
        }

        return shaderObjectId;
    };

    //将顶点着色器和片段着色器连接，并构建对象
    public static int linkProgram(int vertexShaderId,int fragmentShaderId){
        //创建新的程序对象
        final int programObjectId = glCreateProgram();

        //如果对象没有创建
        if(programObjectId == 0){
            if(LoggerConfig.ON){
                Log.w(TAG,"Could not create new program");
            }
            return 0;

        }

        //程序对象附着上着色器
        glAttachShader(programObjectId,vertexShaderId);
        glAttachShader(programObjectId,fragmentShaderId);

        //链接程序
        glLinkProgram(programObjectId);

        //检测连接成功失败的日志
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId,GL_LINK_STATUS,linkStatus,0);

        //如果创建好了，给出ID
        if (LoggerConfig.ON) {
            // Print the program info log to the Android log output.
            Log.v(TAG, "Results of linking program:\n"
                    + glGetProgramInfoLog(programObjectId));
        }

        //如果创建失败
        if(linkStatus[0] == 0){
            glDeleteProgram(programObjectId);
            if(LoggerConfig.ON){
                Log.w(TAG,"Linking of program failed");
            }
            return 0;
        }

        return programObjectId;

    };

    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId,GL_VALIDATE_STATUS,validateStatus,0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;

    };

    public static int buildProgram(String vertexShaderSource,String fragmentShaderSource){
        int program;

        int vertexShader = complieVertexShader(vertexShaderSource);
        int fragmentShader = complieFragmentShader(fragmentShaderSource);
        program = linkProgram(vertexShader,fragmentShader);

        if(LoggerConfig.ON){
            validateProgram(program);
        }

        return program;

    };

}