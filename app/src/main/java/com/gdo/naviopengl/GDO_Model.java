package com.gdo.naviopengl;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gdo.naviopengl.scene_entity_objects.GDO_Scene;
import com.gdo.naviopengl.scene_entity_objects.GDO_Scene_BaseObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class GDO_Model extends GDO_Scene_BaseObject {

    private final int mPositionDataSize = 3;


    //Matrixes
    private float[] gModelMatrix = new float[16];
    private float[] gViewMatrix = new float[16];
    private float[] gProjectionMatrix = new float[16];
    private float[] gMVMatrix = new float[16];
    private float[] gMVPMatrix = new float[16];


    private float[] gCurrentRotation = new float[16];
    private float[] gAccamulatedRotation = new float[16];
    private float[] gTemporary = new float[16];

    private float[] lightPosInEyeSpace;
    private float[] cameraPos;

    //Handlers
    private int gShaderProgramHandler;
    private int gPositionHandler;
    private int gMVPMatrixHandler;
    private int gMVMatrixHandler;
    private int gColorHandler;
    private int gNormalHandler;
    private int gNormalMatrixHandler;

    private int gLightPositionHandler;
    private int gCameraPositionHandler;

    private int gTexCoordinateHandler;
    private int gTextureHandler;
    private int gNormalMapHandler;

    private int gDiffuseTextureId;
    private int gNormalMapId;

    private boolean isDiffuseTextureSetted = false;
    private boolean isNormaMapSetted = false;
    private boolean isOESTextureSetted = false;
    private boolean isNeedUpdateVBO = false;
    private boolean isNormalsSetted = false;

    private FloatBuffer gVertexes;
    private FloatBuffer gNormals;
    private FloatBuffer gTextureCoords;
    private ShortBuffer gIndexes;

    private int gVertexBufferIdx;
    private int gNormalsBufferIdx;
    private int gTextureCoordsBufferIdx;

    private int gVertexesLength;
    private int gIndexesLenght;

    private boolean isHaveAlphaChannel = false;
    private float alpha = 1.0f;
    private float[] color;

    public GDO_Model(GDO_Scene scene) {
        super(scene);
        Matrix.setIdentityM(gAccamulatedRotation, 0);
        Matrix.setIdentityM(gModelMatrix, 0);
    }


    public GDO_Model(AssetManager ass, String file, String vertexShaderFileName, String fragmentShaderFileName) {
        Matrix.setIdentityM(gAccamulatedRotation, 0);
        Matrix.setIdentityM(gModelMatrix, 0);

        loadModelDataFromBinaryFile(ass, file);
        setShaderProgram(
                GDOExtarnalStorageFileHelper.readTextFile(ass, vertexShaderFileName),
                GDOExtarnalStorageFileHelper.readTextFile(ass, fragmentShaderFileName));
    }


    public GDO_Model(AssetManager ass, String vertexShaderFileName, String fragmentShaderFileName) {

        Matrix.setIdentityM(gAccamulatedRotation, 0);
        Matrix.setIdentityM(gModelMatrix, 0);
        setShaderProgram(
                GDOExtarnalStorageFileHelper.readTextFile(ass, vertexShaderFileName),
                GDOExtarnalStorageFileHelper.readTextFile(ass, fragmentShaderFileName));

    }

    public GDO_Model(Resources resources, int vertexShaderFileName, int fragmentShaderFileName) {

        Matrix.setIdentityM(gAccamulatedRotation, 0);
        Matrix.setIdentityM(gModelMatrix, 0);
        setShaderProgram(
                resources.getString(vertexShaderFileName),
                resources.getString(fragmentShaderFileName));

    }

    public GDO_Model(String vertexShader, String fragmentShader) {

        Matrix.setIdentityM(gAccamulatedRotation, 0);
        Matrix.setIdentityM(gModelMatrix, 0);
        setShaderProgram(vertexShader, fragmentShader);
    }

    @Override
    public void attachtoScene(GDO_Scene scene) {
        super.attachtoScene(scene);
        gViewMatrix = scene.getViewMatrix();
        gProjectionMatrix = scene.getProjectionMatrix();
    }

    public void updateVBO(float[] newVertexes) {
        FloatBuffer floatBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(newVertexes.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(newVertexes).position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, gVertexBufferIdx);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, floatBuffer.capacity() * 4, floatBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        floatBuffer.limit(0);
        floatBuffer = null;
    }

    private void genVertexesBuffer() {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, gVertexes.capacity() * 4, gVertexes, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        gVertexBufferIdx = buffers[0];
        gVertexes.limit(0);
        gVertexes = null;
    }

    private void genUVBuffer() {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, gTextureCoords.capacity() * 4, gTextureCoords, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        gTextureCoordsBufferIdx = buffers[0];
        gTextureCoords.limit(0);
        gTextureCoords = null;
    }

    private void genNormalsBuffer() {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, gNormals.capacity() * 4, gNormals, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        gNormalsBufferIdx = buffers[0];
        gNormals.limit(0);
        gNormals = null;
        isNormalsSetted = true;
    }

    public void draw(int mode, float[] mViewMatrix, float[] mProjectionMatrix) {
        gViewMatrix = mViewMatrix;
        gProjectionMatrix = mProjectionMatrix;
        draw(mode);
    }

    public void draw(int mode) {
GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        Matrix.multiplyMM(gTemporary, 0, gModelMatrix, 0, gAccamulatedRotation, 0);
        System.arraycopy(gTemporary, 0, gModelMatrix, 0, 16);
        Matrix.multiplyMM(gMVMatrix, 0, gViewMatrix, 0, gModelMatrix, 0);
        Matrix.multiplyMM(gMVPMatrix, 0, gProjectionMatrix, 0, gMVMatrix, 0);


        GLES20.glUseProgram(gShaderProgramHandler);

        if (scene.getCamera() != null && gCameraPositionHandler != -1) {
            cameraPos = scene.getCamera().getPosition();
            GLES20.glUniform3f(gCameraPositionHandler, cameraPos[0], cameraPos[1], cameraPos[2]);
        }
        if (scene.getLight() != null && gLightPositionHandler != -1) {
            lightPosInEyeSpace = scene.getLight().getLightPosInEyeSpace();
            GLES20.glUniform3f(gLightPositionHandler, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
        }


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, gVertexBufferIdx);
        GLES20.glEnableVertexAttribArray(gPositionHandler);

        GLES20.glVertexAttribPointer(gPositionHandler, mPositionDataSize, GLES20.GL_FLOAT, false, 0, 0);

        //if (meshes[i].gNormals != null){}

        if (isNormalsSetted) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, gNormalsBufferIdx);
            GLES20.glEnableVertexAttribArray(gNormalHandler);
            GLES20.glVertexAttribPointer(gNormalHandler, 3, GLES20.GL_FLOAT, false, 0, 0);
        }

        if (isDiffuseTextureSetted || isNormaMapSetted || isOESTextureSetted) {

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, gTextureCoordsBufferIdx);
            GLES20.glEnableVertexAttribArray(gTexCoordinateHandler);
            GLES20.glVertexAttribPointer(gTexCoordinateHandler, 2, GLES20.GL_FLOAT, false, 0, 0);
        }

        if (isDiffuseTextureSetted) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, gDiffuseTextureId);
            GLES20.glUniform1i(gTextureHandler, 0);
        }

        if (isNormaMapSetted) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, gNormalMapId);
            GLES20.glUniform1i(gNormalMapHandler, 1);
        }

        if (isOESTextureSetted) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, gDiffuseTextureId);
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glUniformMatrix4fv(gMVMatrixHandler, 1, false, gMVMatrix, 0);
        GLES20.glUniformMatrix4fv(gMVPMatrixHandler, 1, false, gMVPMatrix, 0);
        if (color != null) {
            GLES20.glUniform4f(gColorHandler, color[0], color[1], color[2], color[3]);
        }

        if (isHaveAlphaChannel) {
            int alphaChannel = GLES20.glGetUniformLocation(gShaderProgramHandler, "u_alpha");
            GLES20.glUniform1f(alphaChannel, alpha);
            GLES20.glDepthMask(false);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glEnable(GLES20.GL_BLEND);
        }

        GLES20.glDrawArrays(mode, 0, gVertexesLength);
        GLES20.glDisableVertexAttribArray(gPositionHandler);
        GLES20.glDisableVertexAttribArray(gNormalHandler);
        GLES20.glDisableVertexAttribArray(gTexCoordinateHandler);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(true);
    }

    public void moveToPosition(float x, float y, float z) {
        Matrix.setIdentityM(gModelMatrix, 0);
        Matrix.translateM(gModelMatrix, 0, x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(gModelMatrix, 0, x, y, z);
    }

    public void rotate(float xDir, float yDir, float zDir) {
//		Matrix.setIdentityM(gCurrentRotation, 0);
//		Matrix.rotateM(gCurrentRotation, 0, xDir, 1, 0, 0);
//		Matrix.rotateM(gCurrentRotation, 0, yDir, 0, 1, 0);
//		Matrix.rotateM(gCurrentRotation, 0, zDir, 0, 0, 1);
//
//		Matrix.multiplyMM(gTemporary, 0, gCurrentRotation, 0, gAccamulatedRotation, 0);
//		System.arraycopy(gTemporary, 0, gAccamulatedRotation, 0, 16);
        Matrix.setIdentityM(gAccamulatedRotation, 0);
        Matrix.rotateM(gModelMatrix, 0, xDir, 1, 0, 0);
        Matrix.rotateM(gModelMatrix, 0, yDir, 0, 1, 0);
        Matrix.rotateM(gModelMatrix, 0, zDir, 0, 0, 1);
    }

    public void rotateNoAccumulate(float xDir, float yDir, float zDir) {
        Matrix.setIdentityM(gAccamulatedRotation, 0);
//		Matrix.rotateM(gAccamulatedRotation, 0, xDir, 1, 0, 0);
//		Matrix.rotateM(gAccamulatedRotation, 0, yDir, 0, 1, 0);
//		Matrix.rotateM(gAccamulatedRotation, 0, zDir, 0, 0, 1);
        Matrix.setRotateEulerM(gModelMatrix, 0, xDir, yDir, zDir);
//		Matrix.setRotateM(gModelMatrix, 0, xDir, 1, 0, 0);
//		Matrix.setRotateM(gModelMatrix, 0, yDir, 0, 1, 0);
//		Matrix.setRotateM(gModelMatrix, 0, zDir, 0, 0, 1);
    }

    public void identityRotation() {
        Matrix.setIdentityM(gAccamulatedRotation, 0);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color = new float[]{r, g, b, a};
    }

    public void setAlphaDiffuseTexture(Context context, int resource, int filterMode) {
        setDiffuseTexture(context, resource, filterMode);
        isHaveAlphaChannel = true;
    }

    public void setDiffuseTexture(Context context, int resource, int filterMode) {
        bindTexturesHandlers();
        gDiffuseTextureId = GDO_TextureClass.loadTextureFromResources(context, resource, filterMode);
        isDiffuseTextureSetted = true;
    }


    public void setNormalMap(Context context, int resource) {
        gNormalMapId = GDO_TextureClass.loadTextureFromResources(context, resource, GLES20.GL_NEAREST);
        isNormaMapSetted = true;
    }

    public void initOESTexture() {
        bindTexturesHandlers();
        gDiffuseTextureId = GDO_TextureClass.generateOESTextureHandle();
        isOESTextureSetted = true;
    }

    public void setShaderProgram(String v, String f) {
        this.gShaderProgramHandler = GDO_ShaderUtil.CreateShaderProgram(v, f);
        bindHandlers(this.gShaderProgramHandler);
    }

    public void bindHandlers(int SPH) {
        gPositionHandler = GLES20.glGetAttribLocation(SPH, "a_Position");
        gColorHandler = GLES20.glGetUniformLocation(SPH, "u_Color");
        gNormalHandler = GLES20.glGetAttribLocation(SPH, "a_Normal");
        gMVPMatrixHandler = GLES20.glGetUniformLocation(gShaderProgramHandler, "u_MVPMatrix");
        gLightPositionHandler = GLES20.glGetUniformLocation(gShaderProgramHandler, "u_LightPos");
        gCameraPositionHandler = GLES20.glGetUniformLocation(gShaderProgramHandler, "u_Camera");
        gMVMatrixHandler = GLES20.glGetUniformLocation(gShaderProgramHandler, "u_MVMatrix");

    }


    public void bindTexturesHandlers() {
        gTexCoordinateHandler = GLES20.glGetAttribLocation(gShaderProgramHandler, "a_TextureCoord");
        gTextureHandler = GLES20.glGetUniformLocation(gShaderProgramHandler, "u_Texture");
        gNormalMapHandler = GLES20.glGetUniformLocation(gShaderProgramHandler, "u_NormalMap");
    }

    public void loadModelDataFromBinaryFile(AssetManager assetManager, String file) {
        byte[] bytearray;
        InputStream inputStream;
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        try {
            inputStream = assetManager.open(file);
            bytearray = new byte[16];
            inputStream.read(bytearray, 0, 16);
            byteBuffer.put(bytearray);
            byteBuffer.order(ByteOrder.nativeOrder());


            int[] head = new int[4];

            byteBuffer.flip();
            byteBuffer.asIntBuffer().get(head, 0, head.length);

            System.out.println(Integer.toString(head[0]));
            System.out.println(Integer.toString(head[1]));
            System.out.println(Integer.toString(head[2]));
            System.out.println(Integer.toString(head[3]));

            bytearray = null;
            bytearray = new byte[(head[0]) * 4 * 3];
            byteBuffer.clear();
            byteBuffer = ByteBuffer.allocateDirect((head[0]) * 4 * 3);
            inputStream.read(bytearray, 0, bytearray.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(bytearray);
            byteBuffer.flip();
            gVertexes = byteBuffer.asFloatBuffer();


            //������ UV
            bytearray = null;
            bytearray = new byte[(head[0]) * 4 * 2];
            byteBuffer.clear();
            byteBuffer = ByteBuffer.allocateDirect((head[0]) * 4 * 2);

            inputStream.read(bytearray, 0, bytearray.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(bytearray);
            byteBuffer.flip();

            gTextureCoords = byteBuffer.asFloatBuffer();

            // ������ �������
            bytearray = null;
            bytearray = new byte[(head[0]) * 4 * 3];
            byteBuffer.clear();
            byteBuffer = ByteBuffer.allocateDirect((head[0]) * 4 * 3);

            inputStream.read(bytearray, 0, bytearray.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(bytearray);
            byteBuffer.flip();
            gNormals = byteBuffer.asFloatBuffer();


            gVertexesLength = head[0];
            inputStream.close();

            genVertexesBuffer();
            genUVBuffer();
            genNormalsBuffer();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public void setVertexes(FloatBuffer gVertexes) {
        this.gVertexes = gVertexes;
    }

    public void setTextureCoords(FloatBuffer gTextureCoords) {
        this.gTextureCoords = gTextureCoords;
    }

    public int getDiffuseTextureId() {
        return gDiffuseTextureId;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this.isHaveAlphaChannel = true;
    }

}
