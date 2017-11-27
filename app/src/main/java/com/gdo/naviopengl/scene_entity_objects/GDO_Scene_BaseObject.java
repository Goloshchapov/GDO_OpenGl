package com.gdo.naviopengl.scene_entity_objects;


public abstract class GDO_Scene_BaseObject {
    protected GDO_Scene scene;

    public GDO_Scene_BaseObject(GDO_Scene scene){
        this.scene = scene;
    }
    public GDO_Scene_BaseObject(){

    }

    public void attachtoScene(GDO_Scene scene){
        this.scene = scene;
    }
}
