package com.gdo.naviopengl;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.util.Log;

public class GDO_ShaderUtil {

    public static int CreateShaderProgram(String vertexShaderCode, String fragmentShaderCode){

        int vShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        int mProgram = GLES20.glCreateProgram();

        if (mProgram != 0) {
            GLES20.glAttachShader(mProgram, vShader);
            GLES20.glAttachShader(mProgram, fShader);
            GLES20.glLinkProgram(mProgram);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                String error = GLES20.glGetProgramInfoLog(mProgram);
                //	deleteProgram();

                Log.e("Проверка шейдеров", GLU.gluErrorString(GLES20.glGetError()));
                Log.e("Инфо шейдоров", GLES20.glGetProgramInfoLog(mProgram));
                Log.e("Vertex Shader infolog", GLES20.glGetShaderInfoLog(vShader));
                Log.e("Fragment Shader infolog", GLES20.glGetShaderInfoLog(fShader));


            }

        }
        return mProgram;

    }


    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
