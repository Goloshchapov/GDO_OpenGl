package com.gdo.naviopengl;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gdo.naviopengl.scene_entity_objects.GDO_ModelLoader;
import com.gdo.naviopengl.scene_entity_objects.GDO_Scene;
import com.gdo.naviopengl.scene_entity_objects.GDO_Scene_BaseObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class GDO_Model extends GDO_Scene_BaseObject {

    private final int mPositionDataSize = 3;
    private static final String TAG = "GDO_Model ";

    //Matrixes
    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private float[] currentRotation = new float[16];
    private float[] accamulatedRotation = new float[16];
    private float[] temporaryRotateMatrix = new float[16];

    private float[] lightPosInEyeSpace;
    private float[] cameraPos;

    //Handlers
    private int shaderProgramHandler;
    private int positionHandler;
    private int mvpMatrixHandler;
    private int mvMatrixHandler;
    private int colorHandler;
    private int normalHandler;
    private int normalMatrixHandler;

    private int lightPositionHandler;
    private int cameraPositionHandler;

    private int texelsHandler;
    private int gTextureHandler;
    private int normalsMapHandler;

    private int diffuseTextureId;
    private int normalMapId;

    private boolean isDiffuseTextureSetted = false;
    private boolean isNormaMapSetted = false;
    private boolean isOESTextureSetted = false;
    private boolean isNeedUpdateVBO = false;
    private boolean isNormalsSetted = false;

    private FloatBuffer vertexesPositionsBuffer;
    private FloatBuffer normalsBuffer;
    private FloatBuffer texelsBuffer;
    private ShortBuffer indexesBuffer;

    private int vertexesVBOHandler;
    private int normalsVBOHandler;
    private int texelsVBOHandler;
    private int indexesVBOHandler;

    private int vertexesCount;
    private int indexesCount;

    private boolean isHaveAlphaChannel = false;
    private float alpha = 1.0f;
    private float[] color;

    public GDO_Model(GDO_Scene scene) {
        super(scene);
        Matrix.setIdentityM(accamulatedRotation, 0);
        Matrix.setIdentityM(modelMatrix, 0);
    }


    public GDO_Model(AssetManager ass, String file, String vertexShaderFileName, String fragmentShaderFileName) {
        Matrix.setIdentityM(accamulatedRotation, 0);
        Matrix.setIdentityM(modelMatrix, 0);

        GDO_ModelLoader.loadModelDataFromBinaryFile(ass, file, this);
        GDO_ModelLoader.convertFromIndexesModel(this);
        genVBObuffers();
        setShaderProgram(
                GDOExtarnalStorageFileHelper.readTextFile(ass, vertexShaderFileName),
                GDOExtarnalStorageFileHelper.readTextFile(ass, fragmentShaderFileName));
    }

    public GDO_Model(AssetManager ass, String vertexShaderFileName, String fragmentShaderFileName) {

        Matrix.setIdentityM(accamulatedRotation, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        setShaderProgram(
                GDOExtarnalStorageFileHelper.readTextFile(ass, vertexShaderFileName),
                GDOExtarnalStorageFileHelper.readTextFile(ass, fragmentShaderFileName));

    }

    public GDO_Model(Resources resources, int vertexShaderFileName, int fragmentShaderFileName) {

        Matrix.setIdentityM(accamulatedRotation, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        setShaderProgram(
                resources.getString(vertexShaderFileName),
                resources.getString(fragmentShaderFileName));
    }

    public GDO_Model(String vertexShader, String fragmentShader) {

        Matrix.setIdentityM(accamulatedRotation, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        setShaderProgram(vertexShader, fragmentShader);
    }

    @Override
    public void attachtoScene(GDO_Scene scene) {
        super.attachtoScene(scene);
        viewMatrix = scene.getViewMatrix();
        projectionMatrix = scene.getProjectionMatrix();
    }

    public void updateVBO(float[] newVertexes) {
        FloatBuffer floatBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(newVertexes.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(newVertexes).position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexesVBOHandler);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, floatBuffer.capacity() * 4, floatBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        floatBuffer.limit(0);
        floatBuffer = null;
    }

    public void genVBObuffers() {
        genVertexesBuffer();
        genUVBuffer();
        genNormalsBuffer();
        genIBOBuffer();
    }

    private void genVertexesBuffer() {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexesPositionsBuffer.capacity() * 4, vertexesPositionsBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        vertexesVBOHandler = buffers[0];
        vertexesPositionsBuffer.limit(0);
        vertexesPositionsBuffer = null;
    }

    private void genUVBuffer() {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texelsBuffer.capacity() * 4, texelsBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        texelsVBOHandler = buffers[0];
        texelsBuffer.limit(0);
        texelsBuffer = null;
    }

    private void genNormalsBuffer() {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normalsBuffer.capacity() * 4, normalsBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        normalsVBOHandler = buffers[0];
        normalsBuffer.limit(0);
        normalsBuffer = null;
        isNormalsSetted = true;
    }

    private void genIBOBuffer() {
        Log.e(TAG, "genIBOBuffer: index capacity = " + indexesBuffer.capacity());
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        indexesVBOHandler = buffers[0];
        indexesBuffer.limit(0);
        indexesBuffer = null;
    }

    public void draw(int mode, float[] mViewMatrix, float[] mProjectionMatrix) {
        viewMatrix = mViewMatrix;
        projectionMatrix = mProjectionMatrix;
        draw(mode);
    }

    public void draw(int mode) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        Matrix.multiplyMM(temporaryRotateMatrix, 0, modelMatrix, 0, accamulatedRotation, 0);
        System.arraycopy(temporaryRotateMatrix, 0, modelMatrix, 0, 16);
        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);
        GLES20.glUseProgram(shaderProgramHandler);

        if (scene.getCamera() != null && cameraPositionHandler != -1) {
            cameraPos = scene.getCamera().getPosition();
            GLES20.glUniform3f(cameraPositionHandler, cameraPos[0], cameraPos[1], cameraPos[2]);
        }
        if (scene.getLight() != null && lightPositionHandler != -1) {
            lightPosInEyeSpace = scene.getLight().getLightPosInEyeSpace();
            GLES20.glUniform3f(lightPositionHandler, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexesVBOHandler);
        GLES20.glEnableVertexAttribArray(positionHandler);
        GLES20.glVertexAttribPointer(positionHandler, mPositionDataSize, GLES20.GL_FLOAT, false, 0, 0);

        if (isDiffuseTextureSetted || isNormaMapSetted || isOESTextureSetted) {

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texelsVBOHandler);
            GLES20.glEnableVertexAttribArray(texelsHandler);
            GLES20.glVertexAttribPointer(texelsHandler, 2, GLES20.GL_FLOAT, false, 0, 0);
        }

        if (isNormalsSetted) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalsVBOHandler);
            GLES20.glEnableVertexAttribArray(normalHandler);
            GLES20.glVertexAttribPointer(normalHandler, 3, GLES20.GL_FLOAT, false, 0, 0);
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        if (isDiffuseTextureSetted) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, diffuseTextureId);
            GLES20.glUniform1i(gTextureHandler, 0);
        }

        if (isNormaMapSetted) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, normalMapId);
            GLES20.glUniform1i(normalsMapHandler, 1);
        }

        if (isOESTextureSetted) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, diffuseTextureId);
        }

        GLES20.glUniformMatrix4fv(mvMatrixHandler, 1, false, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvpMatrix, 0);
        if (color != null) {
            GLES20.glUniform4f(colorHandler, color[0], color[1], color[2], color[3]);
        }

        if (isHaveAlphaChannel) {
            int alphaChannel = GLES20.glGetUniformLocation(shaderProgramHandler, "u_alpha");
            GLES20.glUniform1f(alphaChannel, alpha);
            GLES20.glDepthMask(false);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glEnable(GLES20.GL_BLEND);
        }

//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesVBOHandler);


//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexesCount);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, indexesCount);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexesCount*3, GLES20.GL_UNSIGNED_SHORT, 0);
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDisableVertexAttribArray(positionHandler);
        GLES20.glDisableVertexAttribArray(texelsHandler);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(normalHandler);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(true);
    }

    public void moveToPosition(float x, float y, float z) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(modelMatrix, 0, x, y, z);
    }

    public void rotate(float xDir, float yDir, float zDir) {
//		Matrix.setIdentityM(currentRotation, 0);
//		Matrix.rotateM(currentRotation, 0, xDir, 1, 0, 0);
//		Matrix.rotateM(currentRotation, 0, yDir, 0, 1, 0);
//		Matrix.rotateM(currentRotation, 0, zDir, 0, 0, 1);
//
//		Matrix.multiplyMM(temporaryRotateMatrix, 0, currentRotation, 0, accamulatedRotation, 0);
//		System.arraycopy(temporaryRotateMatrix, 0, accamulatedRotation, 0, 16);
        Matrix.setIdentityM(accamulatedRotation, 0);
        Matrix.rotateM(modelMatrix, 0, xDir, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, yDir, 0, 1, 0);
        Matrix.rotateM(modelMatrix, 0, zDir, 0, 0, 1);
    }

    public void rotateNoAccumulate(float xDir, float yDir, float zDir) {
        Matrix.setIdentityM(accamulatedRotation, 0);
//		Matrix.rotateM(accamulatedRotation, 0, xDir, 1, 0, 0);
//		Matrix.rotateM(accamulatedRotation, 0, yDir, 0, 1, 0);
//		Matrix.rotateM(accamulatedRotation, 0, zDir, 0, 0, 1);
        Matrix.setRotateEulerM(modelMatrix, 0, xDir, yDir, zDir);
//		Matrix.setRotateM(modelMatrix, 0, xDir, 1, 0, 0);
//		Matrix.setRotateM(modelMatrix, 0, yDir, 0, 1, 0);
//		Matrix.setRotateM(modelMatrix, 0, zDir, 0, 0, 1);
    }

    public void identityRotation() {
        Matrix.setIdentityM(accamulatedRotation, 0);
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
        diffuseTextureId = GDO_TextureClass.loadTextureFromResources(context, resource, filterMode);
        isDiffuseTextureSetted = true;
    }


    public void setNormalMap(Context context, int resource) {
        normalMapId = GDO_TextureClass.loadTextureFromResources(context, resource, GLES20.GL_NEAREST);
        isNormaMapSetted = true;
    }

    public void initOESTexture() {
        bindTexturesHandlers();
        diffuseTextureId = GDO_TextureClass.generateOESTextureHandle();
        isOESTextureSetted = true;
    }

    public void setShaderProgram(String v, String f) {
        this.shaderProgramHandler = GDO_ShaderUtil.CreateShaderProgram(v, f);
        bindHandlers(this.shaderProgramHandler);
    }

    public void bindHandlers(int SPH) {
        colorHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "u_Color");
        positionHandler = GLES20.glGetAttribLocation(shaderProgramHandler, "a_position");
        normalHandler = GLES20.glGetAttribLocation(shaderProgramHandler, "a_normal");
        mvpMatrixHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "u_mvp_matrix");
        lightPositionHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "u_LightPos");
        cameraPositionHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "u_Camera");
        mvMatrixHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "u_mv_matrix");

        Log.e(TAG, "positionHandler = " + positionHandler);
        Log.e(TAG, "mvpMatrixHandler = " + mvpMatrixHandler);
        Log.e(TAG, "mvMatrixHandler = " + mvMatrixHandler);
        Log.e(TAG, "normalHandler = " + normalHandler);
        Log.e(TAG, "colorHandler = " + colorHandler);
        Log.e(TAG, "lightPositionHandler = " + lightPositionHandler);

    }


    public void bindTexturesHandlers() {
        texelsHandler = GLES20.glGetAttribLocation(shaderProgramHandler, "a_texel");
        gTextureHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "u_Texture");
        normalsMapHandler = GLES20.glGetUniformLocation(shaderProgramHandler, "u_NormalMap");

        Log.e(TAG, "texelsHandler = " + texelsHandler);

    }


    public void setVertexes(FloatBuffer gVertexes) {
        this.vertexesPositionsBuffer = gVertexes;
    }

    public void setTextureCoords(FloatBuffer gTextureCoords) {
        this.texelsBuffer = gTextureCoords;
    }

    public int getDiffuseTextureId() {
        return diffuseTextureId;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this.isHaveAlphaChannel = true;
    }


    // Getters and setters

    public int getVertexesCount() {
        return vertexesCount;
    }

    public int getIndexesCount() {
        return indexesCount;
    }

    public void setVertexesPositionsBuffer(FloatBuffer vertexesPositionsBuffer) {
        this.vertexesPositionsBuffer = vertexesPositionsBuffer;
    }

    public void setNormalsBuffer(FloatBuffer normalsBuffer) {
        this.normalsBuffer = normalsBuffer;
    }

    public void setTexelsBuffer(FloatBuffer texelsBuffer) {
        this.texelsBuffer = texelsBuffer;
    }

    public void setIndexesBuffer(ShortBuffer indexesBuffer) {
        this.indexesBuffer = indexesBuffer;
    }


    public FloatBuffer getVertexesPositionsBuffer() {
        return vertexesPositionsBuffer;
    }

    public FloatBuffer getNormalsBuffer() {
        return normalsBuffer;
    }

    public FloatBuffer getTexelsBuffer() {
        return texelsBuffer;
    }

    public ShortBuffer getIndexesBuffer() {
        return indexesBuffer;
    }

    public void setVertexesCount(int vertexesCount) {
        this.vertexesCount = vertexesCount;
    }

    public void setIndexesCount(int indexesCount) {
        this.indexesCount = indexesCount;
    }

}
