package com.example.stephenbai.assignment4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

// have to Adapter Change Listener
public class ShowGallery extends AppCompatActivity implements  AdapterView.OnItemClickListener {

    private GridView mGridView;
    private Realm mRealm;
    private com.example.stephenbai.assignment4.ImageAdapter mAdapter;
    public static final String EXTRA_PATH = "FullView" ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screen2_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.show_realm_database:
                mAdapter.setData(queryRealm(false));
                mGridView.setAdapter(mAdapter);
                return true;
            case R.id.show_face:
                mAdapter.setData(queryRealm(true));
                mGridView.setAdapter(mAdapter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private RealmResults<Picture> queryRealm(boolean showFace){
        RealmQuery<Picture> query = mRealm.where(Picture.class);
        if (showFace){
             return query.equalTo("containFace", true).findAll();
        }
        else{
            // every thing
            return query.equalTo("containFace", true).
                    or().equalTo("containFace", false).findAll();
        }

        //mCursor = getContentResolver().query()
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gallery);

      //  Toolbar toolbar = (Toolbar) findViewById(R.id.my_screentwo_toolbar);
     //   setSupportActionBar(toolbar);

        mRealm = Realm.getDefaultInstance();
        mAdapter = new com.example.stephenbai.assignment4.ImageAdapter(this);


        // load mCursor first --> default is all in Realm


        RealmResults result = queryRealm(false);
        mAdapter.setData(result);



        mGridView = (GridView) findViewById(R.id.GalleryGrid);


        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(ShowGallery.this);

        //mAdapter.notifyDataSetChanged();
        mGridView.invalidate();




    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView imageView = (ImageView) view.findViewById(R.id.item_show_gallery_view);
        // Adapter is the current Cursor
        String imageFilePath = (( Picture )mAdapter.getItem(position)).getPath();
        //String size = (( Picture )mAdapter.getItem(position)).getSize();


        imageView.setTag(imageFilePath);
        Intent i_fullimage = new Intent(ShowGallery.this, FullView.class);

        i_fullimage.putExtra( EXTRA_PATH, imageFilePath);
        startActivity(i_fullimage);

    }


}
