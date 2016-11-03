package com.example.stephenbai.assignment4;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;

import static com.example.stephenbai.assignment4.screen1.mProgressBar;
import static com.example.stephenbai.assignment4.screen1.mTextView;

/**
 * Created by stephenbai on 2016-11-01.
 */

// 3 generic types: Params, Progress and Result
class UploadtoRealmTask extends AsyncTask <String, Integer, Integer> {
    private Context mcontext;
    private Realm mrealm;
    private double mtotal ;

    public UploadtoRealmTask(Context context){
        this.mcontext = context;
    }

    @Override
    protected Integer doInBackground(String... photoPath){
       // mcontext.getClassLoader()

        // debugging code
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        mtotal = ((double) photoPath.length);
        mrealm = Realm.getDefaultInstance();
        FaceDetector mFaceDetector = new FaceDetector();
        Bitmap bitmap;
        if (photoPath != null) {
            for (int i = 0; i < photoPath.length; i++) {
                // set as 100x100 size anyway..so same size in display full image
                 bitmap = com.example.stephenbai.assignment4.imageHelper.decodeSampledBitmapFromFileDefault(photoPath[i], 100,
                        100);

                mFaceDetector.setBitmap(mcontext, bitmap);
                // mFaceLists is the one without scale!! calculate scale when drawing the picture!!!
                ArrayList<float[]> mFaceLists = mFaceDetector.getRawFaceBox();

                upload(photoPath[i], mFaceLists );

                publishProgress( i+1 );
            }
            return photoPath.length;
        }else{
            // ArrayBlockingQueue is empty
            return 0;
        }

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        String msg ;
        if ( progress[0] ==(int) mtotal){
            msg = "";
        }else{
         msg = progress[0] + "/" +
                (int)mtotal + " files uploaded";
        }
        int tmp = doWork(progress[0]);
        mProgressBar.setProgress( tmp );

        mTextView.setText(msg);
        //Toast.makeText(mcontext,msg,Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPostExecute(Integer result) {

        Toast.makeText(mcontext, "Uploaded " + result + " Files",
                Toast.LENGTH_LONG).show();
    }

    public void upload(final String photoPath,
                       final ArrayList<float[]> mFaceList){

        this.mrealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a person
                Picture picture
                        = realm.createObject(Picture.class, photoPath);

                Boolean containFace = false;
                if (!mFaceList.isEmpty()){
                    containFace = true;
                }
                picture.setPhotoPath(containFace);

                // if FaceList is contained
                if (containFace){
                // put in & save parameters
                for (float[] face : mFaceList){
                    picture.setFace(face);
                    }
                }
            }
        });
    }

    private int doWork(int input){
        double tmp ;


        if (mtotal> 0 ) {

            tmp = ((((double) input) / mtotal));
            tmp = tmp * 100;
           return ((int) tmp);

        }else{
            return 0;
        }
    }

}
