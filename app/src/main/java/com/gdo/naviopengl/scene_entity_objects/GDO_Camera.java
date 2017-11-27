package com.gdo.naviopengl.scene_entity_objects;


import android.opengl.Matrix;

public class GDO_Camera extends GDO_Scene_BaseObject {

    private float[] lookAt = new float[]{0,0,0};


    private float[] position = new float[]{0,0,0};
    private float[] upVector = new float[]{0,1,0};


    public GDO_Camera(GDO_Scene scene) {
        super(scene);
    }

    public void initCamera(float positionX, float positionY, float positionZ, float lookX, float lookY, float lookZ){
        this.lookAt[0] = lookX;
        this.lookAt[1] = lookY;
        this.lookAt[2] = lookZ;
        this.position[0] =positionX;
        this.position[1] = positionY;
        this.position[2] = positionZ;
        Matrix.setLookAtM(scene.getViewMatrix(), 0, position[0], position[1], position[2], lookAt[0], lookAt[1], lookAt[2], upVector[0], upVector[1], upVector[2]);
    }

    public void lookAt(float x, float y, float z) {
        this.lookAt[0] = x;
        this.lookAt[1] = y;
        this.lookAt[2] = z;
        Matrix.setLookAtM(scene.getViewMatrix(), 0, position[0], position[1], position[2], lookAt[0], lookAt[1], lookAt[2], upVector[0], upVector[1], upVector[2]);
    }

    public void position(float x, float y, float z){
        this.position[0] =x;
        this.position[1] = y;
        this.position[2] = z;
        Matrix.setLookAtM(scene.getViewMatrix(), 0, position[0], position[1], position[2], lookAt[0], lookAt[1], lookAt[2], upVector[0], upVector[1], upVector[2]);
    }

    public void setUpVector(float x, float y, float z){
        upVector[0] = x;
        upVector[1] =y;
        upVector[2] = z;
        Matrix.setLookAtM(scene.getViewMatrix(), 0, position[0], position[1], position[2], lookAt[0], lookAt[1], lookAt[2], upVector[0], upVector[1], upVector[2]);
    }

    public float[] getLookAt() {
        return lookAt;
    }

    public float[] getPosition() {
        return position;
    }

}
