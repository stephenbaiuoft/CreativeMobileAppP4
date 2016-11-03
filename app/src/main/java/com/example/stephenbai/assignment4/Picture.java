package com.example.stephenbai.assignment4;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by stephenbai on 2016-10-30.
 */

public class Picture extends RealmObject {
   // private byte[] bitmapData;

    @PrimaryKey
    @Required
    private String photoPath;

    private RealmList<realmFace> faces;
    private int inSampleSize ;
    private Boolean containFace;

    // add bitmap and 1 set of face per time
    void setPhotoPath(  Boolean containFace){

        //this.photoPath = photoPath;
        // true means face detected
        this.containFace = containFace;
        // initialize faces
        faces = new RealmList<>();
    }

    // add face data to faces
    void setFace(float[] face){
        realmFace rFace = new realmFace();
        rFace.setFace(face);

        this.faces.add( rFace );
    }

    String getName(){
        return photoPath.substring(
                photoPath.lastIndexOf('/'),
                photoPath.length() - 1
                );

    }

    String getPath(){
        return photoPath;
    }

    String getSize(){
        return String.valueOf(  inSampleSize );
    }

    RealmList<realmFace> getFaces(){
        return faces;
    }
}
