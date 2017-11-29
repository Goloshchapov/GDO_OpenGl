package com.gdo.naviopengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.gdo.naviopengl.scene_entity_objects.GDO_Camera;
import com.gdo.naviopengl.scene_entity_objects.GDO_Light;
import com.gdo.naviopengl.scene_entity_objects.GDO_Scene;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created by gdjedi on 11/20/17.
 */

public class NaviAddressRenderer implements GLSurfaceView.Renderer {


    private GDO_Scene gdoScene = new GDO_Scene();
    private Context context;

    public NaviAddressRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0,0,1,1);
         gdoScene = new GDO_Scene();
        GDO_Camera camera = new GDO_Camera(gdoScene);
        camera.initCamera(1,2,3,0,1,0);
        GDO_Light light = new GDO_Light();
        light.setLightPosition(0,0.5f,2);
        gdoScene.attachLight(light);
        GDO_Model testModel = new GDO_Model(context.getAssets(),"box.gdom","vertexP_N_UV.glsl", "fshaderTex.glsl");
//        testModel.setDiffuseTexture(context, R.drawable.followme, GLES20.GL_NEAREST);
        testModel.setColor(1.0f,0.0f,0.0f,1.0f);
        gdoScene.addModelToScene(testModel);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
        gdoScene.initProjectionMatrix(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gdoScene.render();

    }
}
