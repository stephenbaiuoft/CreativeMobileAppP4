package com.example.stephenbai.assignment4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import io.realm.RealmList;

/**
 * Created by stephenbai on 2016-10-30.
 */

public class FaceOverlayView extends View {

    private Bitmap mBitmap;
    private RealmList<realmFace> mFaceList;
    // store faces found in mBitmap

    public FaceOverlayView(Context context) {
        this(context, null);
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap(Bitmap bitmap , RealmList<realmFace> faceList) {
// get correct size of bitmap
        mBitmap = bitmap;
        mFaceList = faceList;
        // call to draw immediately afterwards... as onDraw is Override
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((mBitmap != null)) {
            double scale = drawBitmap(canvas);
            drawFaceBox(canvas, scale);
        }
    }

    private double drawBitmap( Canvas canvas ) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min( viewWidth / imageWidth, viewHeight / imageHeight );

        Rect destBounds = new Rect( 0, 0, (int) ( imageWidth * scale ), (int) ( imageHeight * scale ) );
        canvas.drawBitmap( mBitmap, null, destBounds, null );
        return scale;
    }

    private void drawFaceBox(Canvas canvas, double scale) {
        //paint should be defined as a member variable rather than
        //being created on each onDraw request, but left here for
        //emphasis.
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;

        for( int i = 0; i < mFaceList.size(); i++ ) {
            realmFace face = mFaceList.get(i);

            left = (float) ( face.getLeft() * scale );
            top = (float) ( face.getTop() * scale );
            right = (float) (scale *  face.getRight() );
            bottom = (float) (scale * face.getBottom());
            canvas.drawRect( left, top, right, bottom, paint );
        }
    }
}

