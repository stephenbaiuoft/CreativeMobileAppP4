package com.example.stephenbai.assignment4;

import io.realm.RealmObject;

/**
 * Created by stephenbai on 2016-10-31.
 */

public class realmFace extends RealmObject {
    private float left, top, right, bottom ;
    public void setFace(float[] face){
        this.left = face[0];
        this.top = face[1];
        this.right = face[2];
        this.bottom = face[3];
    }
    public float getLeft (){
        return this.left;
    }

    public float getTop (){
        return this.top;
    }

    public float getRight (){
        return this.right;
    }

    public float getBottom (){
        return this.bottom;
    }

}
