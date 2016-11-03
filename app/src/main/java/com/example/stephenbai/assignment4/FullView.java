package com.example.stephenbai.assignment4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.example.stephenbai.assignment4.ShowGallery.EXTRA_PATH;

public class FullView extends AppCompatActivity {

    private ImageView mImageView;
    private String mCurrentPhotoPath;
    private FaceOverlayView mFaceOverlayView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.screen3_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_this_pic:
                pic_delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

// Delete current selected picture
    private void pic_delete(){
        if ( mCurrentPhotoPath!=null){
        RealmResults<Picture> results =
            mRealm.where(Picture.class).equalTo("photoPath",mCurrentPhotoPath).findAll();

            for (Picture pic : results){
                pic.deleteFromRealm();
            }
            // switch back to ShowGallery Activity

            Intent iShowGallery = new Intent(FullView.this, ShowGallery.class);
            startActivity(iShowGallery);
        ;

        }else{
            Toast.makeText(this, "Invalid Photo Path", Toast.LENGTH_SHORT);
        }

    }

    private Realm mRealm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        setTitle("Full Picture View");
        //mImageView = (ImageView) findViewById(R.id.fullimageView);

        mFaceOverlayView = (FaceOverlayView) findViewById(R.id.fullimageView);
        Intent intent = getIntent();

        mCurrentPhotoPath = intent.getStringExtra(EXTRA_PATH);
        mRealm = Realm.getDefaultInstance();
        RealmResults<Picture> mQuery =
                mRealm.where(Picture.class).equalTo("photoPath", mCurrentPhotoPath).findAll();

        if (mQuery.isEmpty()) {
            Toast.makeText(this, mCurrentPhotoPath + " not found", Toast.LENGTH_SHORT);
        } else {
            Picture mPicture = mQuery.get(0);
            RealmList<realmFace> mFaceList = mPicture.getFaces();

            mFaceOverlayView.setBitmap(imageHelper.decodeSampledBitmapFromFileDefault(mCurrentPhotoPath
                    , 100, 100), mFaceList);

        }

    }





}
