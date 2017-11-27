package com.gdo.naviopengl;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_screen_glView)
    GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new NaviAddressRenderer(this));
        glSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onResume() {
        glSurfaceView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        glSurfaceView.onPause();
        super.onPause();
    }
}
