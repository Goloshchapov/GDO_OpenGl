package com.gdo.naviopengl.scene_entity_objects;


import android.opengl.Matrix;


public class GDO_Light extends GDO_Scene_BaseObject{
    private float[] gLightMatrix = new float[16];
    private float[] gLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private float[] gLightPosInWorldSpace = new float[4];


    private float[] glightPosInEyeSpace = new float[4];

    public void setLightPosition(float lX, float lY, float lZ) {
        Matrix.setIdentityM(gLightMatrix, 0);
        Matrix.translateM(gLightMatrix, 0, lX, lY, lZ);
    }

    public float[] getLightPosInEyeSpace() {
        Matrix.multiplyMV(gLightPosInWorldSpace, 0, gLightMatrix, 0, gLightPosInModelSpace, 0);
        Matrix.multiplyMV(glightPosInEyeSpace, 0, scene.getViewMatrix(), 0, gLightPosInWorldSpace, 0);
        return glightPosInEyeSpace;
    }

    public float[] getGlightPosInEyeSpace() {
        return glightPosInEyeSpace;
    }

}
