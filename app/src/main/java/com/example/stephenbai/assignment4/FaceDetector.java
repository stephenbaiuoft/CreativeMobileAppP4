package com.example.stephenbai.assignment4;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.util.ArrayList;

/**
 * Created by stephenbai on 2016-10-31.
 */

// FaceDetector is the controller
public class FaceDetector {
    private Bitmap mBitmap;
    // store faces found in mBitmap
    private SparseArray<Face> mFaces;


    public void setBitmap( Context context, Bitmap bitmap  ) {
// get correct size of bitmap
        mBitmap = bitmap;
// create FaceDetector
        com.google.android.gms.vision.face.FaceDetector detector =
                new com.google.android.gms.vision.face.FaceDetector.Builder( context )
                .setTrackingEnabled(false)
                .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.NO_LANDMARKS)
                .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                .build();

        if (!detector.isOperational()) {
            //Handle contingency
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            mFaces = detector.detect(frame);
            // release resources associated with the bitmap
            detector.release();
        }

    }


    // return FacePositions based on bitmap
    public ArrayList<float[]> getRawFaceBox( ) {
        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;
        // create res of size 4
        ArrayList< float[]> res = new ArrayList<>();

        //float[] tmp = new float[4];
        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = mFaces.valueAt(i);
            float[] tmp = new float[4];
            tmp[0] = (float) ( face.getPosition().x );
            tmp[1] = (float) ( face.getPosition().y );
            tmp[2]= (float) ( face.getPosition().x + face.getWidth() );
            tmp[3] = (float) ( face.getPosition().y + face.getHeight() );
            res.add(tmp);
        }
        return res;
    }
}
