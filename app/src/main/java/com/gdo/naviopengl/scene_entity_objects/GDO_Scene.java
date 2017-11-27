package com.gdo.naviopengl.scene_entity_objects;


import android.opengl.GLES20;
import android.opengl.Matrix;

import com.gdo.naviopengl.GDO_Model;

import java.util.ArrayList;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;

public class GDO_Scene {

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mpvMatrix = new float[16];
    private int projectionNear = 1;
    private int projectionFar = 7;

    private ArrayList<GDO_Model> models = new ArrayList<>();
    private GDO_Camera camera;
    private GDO_Light light;

    public GDO_Scene() {

    }

    public void addModelToScene(GDO_Model model){
        model.attachtoScene(this);
        models.add(model);
    }

    public void render(){
        glClear(GLES20.GL_DEPTH_BUFFER_BIT |GL_COLOR_BUFFER_BIT);

        Matrix.multiplyMM(mpvMatrix,0,projectionMatrix,0,viewMatrix,0);
        for(GDO_Model model : models){
            model.draw(GLES20.GL_TRIANGLES);
        }

    }

    public void initProjectionMatrix(int width, int height){
        float ascpectRatio = (float) width/height;
        Matrix.frustumM(projectionMatrix,0, -ascpectRatio, ascpectRatio, -1,1,projectionNear, projectionFar);
    }

    public float[] getMVPMatrix() {
        return mpvMatrix;
    }

    public void attachCamera(GDO_Camera camera) {
        this.camera = camera;
    }

    public void attachLight(GDO_Light light){
        this.light = light;
    }

    public GDO_Camera getCamera() {
        return camera;
    }

    public GDO_Light getLight(){
        return light;
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }
}
