package com.gdo.naviopengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.gdo.naviopengl.scene_entity_objects.GDO_Camera;
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
        camera.initCamera(2,1,4,0,1,0);
        GDO_Model testModel = new GDO_Model(context.getAssets(),"cube","vertex.glsl", "fshaderTex.glsl");
        testModel.setDiffuseTexture(context, R.drawable.followme, GLES20.GL_NEAREST);
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
