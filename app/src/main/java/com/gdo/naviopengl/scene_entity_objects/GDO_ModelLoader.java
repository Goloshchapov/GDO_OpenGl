package com.gdo.naviopengl.scene_entity_objects;

import android.content.res.AssetManager;
import android.util.Log;

import com.gdo.naviopengl.GDO_Model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by gdjedi on 11/29/17.
 */

public class GDO_ModelLoader {
    private static final String TAG = "GDO_ModelLoader:";


    public static void loadModelDataFromBinaryFile(AssetManager assetManager, String file, GDO_Model dstModel) {
        byte[] bytearray;
        InputStream inputStream;
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.order(ByteOrder.nativeOrder());
        try {
            inputStream = assetManager.open(file);
            bytearray = new byte[16];
            inputStream.read(bytearray, 0, 16);
            byteBuffer.put(bytearray);

            int[] head = new int[4];

            byteBuffer.flip();
            byteBuffer.asIntBuffer().get(head, 0, head.length);

            bytearray = new byte[(head[0]) * 4 * 3];
            byteBuffer.clear();
            byteBuffer = ByteBuffer.allocateDirect((head[0]) * 4 * 3);
            inputStream.read(bytearray, 0, bytearray.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(bytearray);
            byteBuffer.flip();
            dstModel.setVertexesPositionsBuffer(byteBuffer.asFloatBuffer());

            bytearray = new byte[(head[1]) * 4 * 2];
            byteBuffer.clear();
            byteBuffer = ByteBuffer.allocateDirect((head[1]) * 4 * 2);
            inputStream.read(bytearray, 0, bytearray.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(bytearray);
            byteBuffer.flip();
            dstModel.setTexelsBuffer(byteBuffer.asFloatBuffer());

            bytearray = new byte[(head[2]) * 4 * 3];
            byteBuffer.clear();
            byteBuffer = ByteBuffer.allocateDirect((head[2]) * 4 * 3);
            inputStream.read(bytearray, 0, bytearray.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(bytearray);
            byteBuffer.flip();
            dstModel.setNormalsBuffer(byteBuffer.asFloatBuffer());

            bytearray = new byte[(head[3]) * 2 * 3];
            byteBuffer.clear();
            byteBuffer = ByteBuffer.allocateDirect((head[3]) * 2 * 3);
            inputStream.read(bytearray, 0, bytearray.length);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.put(bytearray);
            byteBuffer.flip();
            dstModel.setIndexesBuffer(byteBuffer.asShortBuffer());

            dstModel.setVertexesCount(head[0]);
            dstModel.setIndexesCount(head[3]);

            inputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void convertFromIndexesModel(GDO_Model model){

        float[] vert = new float[model.getIndexesCount()*3];
        float[] tex = new float[model.getIndexesCount()*2];
        float[] norm = new float[model.getIndexesCount()*3];
        for(int c = 0 ; c< model.getIndexesCount()*3; c+=3){
            vert[c]= model.getVertexesPositionsBuffer().get((model.getIndexesBuffer().get(c)-1)*3);
            vert[c+1]= model.getVertexesPositionsBuffer().get((model.getIndexesBuffer().get(c)-1)*3+1);
            vert[c+2]= model.getVertexesPositionsBuffer().get((model.getIndexesBuffer().get(c)-1)*3+2);

            norm[c]= model.getNormalsBuffer().get((model.getIndexesBuffer().get(c+2)-1)*3);
            norm[c+1]= model.getNormalsBuffer().get((model.getIndexesBuffer().get(c+2)-1)*3+1);
            norm[c+2]= model.getNormalsBuffer().get((model.getIndexesBuffer().get(c+2)-1)*3+2);
        }

        for(int c = 0 ; c< model.getIndexesCount(); c++){
            tex[c*2]= model.getTexelsBuffer().get((model.getIndexesBuffer().get(c*3+1)-1)*2);
            tex[c*2+1]= model.getTexelsBuffer().get((model.getIndexesBuffer().get(c*3+1)-1)*2+1);
        }

        model.setVertexesPositionsBuffer(FloatBuffer.wrap(vert));
        model.setTexelsBuffer(FloatBuffer.wrap(tex));
        model.setNormalsBuffer(FloatBuffer.wrap(norm));
    }
}
