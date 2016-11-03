package com.example.stephenbai.assignment4;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.RealmSchema;
import io.realm.Sort;

// Connect to Realm Database, then query if emtpy, then go to display activity


public class screen1 extends AppCompatActivity {

    public static final String TAG = screen1.class.getName();
    public static ProgressBar mProgressBar;
    public static TextView mTextView;
    // local Realm Database Query
    private  Realm realm;
    private LinearLayout rootLayout = null ;
    private com.example.stephenbai.assignment4.FaceOverlayView mFaceOverlayView;
    private Button mButton ;

    // DCIM Table Query
    static final int URL_MEDIASTORE = 0;
    static final Uri mDataUrl = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    // ID is required and MediaStore.MediaColumns.Data refers to the file path
    static final String[] mediaProjection = new String[] {
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATA
    };
    private Cursor mCursor ;

    // image bitmap processing variables
    private FaceDetector mFaceDetector;

    // Multi-thread data structure
    private ArrayBlockingQueue<String> mArrayFileQueue;
    private ArrayList<String> mArrayFilePaths;
    private Executor mExecutor ;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screen1_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.realmdelete:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Picture.class);
                    }
                });
                RealmResults<Picture> result1 = realm.where(Picture.class).findAll();
                Toast.makeText(this,"All Realm Deleted",Toast.LENGTH_SHORT);
                return true;

            case R.id.multi_threads:
                multiThreadsPopup();
                return true;

            case R.id.show_gallery:
                showGallery();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


// start the gallery view activity
    public void showGallery(){

        Intent screen2 = new Intent(screen1.this, ShowGallery.class);
        startActivity(screen2);
    }

// put cursor content into Realm Database
    public void loadCursorToQueue() {
        // clear all the paths!!!
        this.mArrayFilePaths.clear();
        Cursor tCursor;
        tCursor = getContentResolver().query(
                mDataUrl,        // Table to query
                mediaProjection,     // Projection to return
                null,            // Selection Clause
                null, // Selection Args
                null// Order
        );
       // ArrayList<String> photoPathAry = new ArrayList<>();
        if (tCursor.moveToFirst()){
            do{
                // second column which is MediaStore.MediaColumns.DATA
                this.mArrayFileQueue.add( tCursor.getString(1));
                this.mArrayFilePaths.add( tCursor.getString(1) );

            }while(tCursor.moveToNext());
        }
        tCursor.close();

        // now populate Local RealmDatabase with 1 thread by default
     //   populateRealm(photoPathAry);
    }

    // cursor content is uploaded to blockingarraylist by now
    // default is set to 1 first
    protected void multiThreadsUpload(int threadNum ){

        UploadtoRealmTask mUploadTask = new UploadtoRealmTask( screen1.this );
        // assign photo paths to Async Tasks anyway

        //mArrayFileQueue.poll();
        String [] mPhotoPaths  = new String[mArrayFilePaths.size()];
        int i = 0;

    // only upload unfound sets!!!

        for ( String photoPath: mArrayFilePaths){
            // is this is not in the database
            if (realm.where(Picture.class).equalTo("photoPath", photoPath)
            .findAll().isEmpty())
            {
                mPhotoPaths[i] = photoPath;
                i++;
            }
        }
        // upload iff mPhotoPaths contain values

        if ( mPhotoPaths[0] != null  ) {
            //= mArrayFilePaths.toArray();
            if (threadNum == 1) {
                mUploadTask.execute(mPhotoPaths);
            } else {
                mExecutor = new ThreadPoolExecutor(threadNum, threadNum, 10,
                        TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(100));
                mUploadTask.executeOnExecutor(mExecutor);
            }
        }else{

            mProgressBar.setProgress(100);
            Toast.makeText(this,"All Files Stored Already", Toast.LENGTH_SHORT).show();

        }

       // RealmResults<Picture> result1 = realm.where(Picture.class).findAll();
    }

// assign correct number of threads to Async Tasks
    public void multiThreadsPopup(){
        CharSequence choices[] = new CharSequence[] {"1", "2","4","8"};
        AlertDialog.Builder builder = new AlertDialog.Builder(screen1.this);
        builder.setTitle("Choose Number of Threads ");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // local copy
                if (which == 0) {
                    loadCursorToQueue();
                    // call number of Async Tasks
                    multiThreadsUpload(1);
                }
                else if (which == 1){
                    multiThreadsUpload(2);
                }
                else if (which == 2){
                    multiThreadsUpload(4);
                }
                // 8 threads
                else{
                    multiThreadsUpload(8);
                }
            }
        });

        // important to show the PopUp Menu
        builder.show();
    }

    private RealmMigration runMigration(){
        RealmMigration migration = new RealmMigration() {
            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                // DynamicRealm exposes an editable schema
                RealmSchema schema = realm.getSchema();
                // Migrate to version 1: Add a new class.
                // Example:
                // public Person extends RealmObject {
                //     private String name;
                //     private int age;
                //     // getters and setters left out for brevity
                // }
                if (oldVersion == 0) {
                    schema.get("Picture")
                            .addField("containFace", Boolean.class);
                    oldVersion++;
                }

                // Migrate to version 2: Add a primary key + object references
                // Example:
                // public Person extends RealmObject {
                //     private String name;
                //     @PrimaryKey
                //     private int age;
                //     private Dog favoriteDog;
                //     private RealmList<Dog> dogs;
                //     // getters and setters left out for brevity
                // }
                 /*
                if (oldVersion == 1) {
                    schema.get("Person")
                            .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                            .addRealmObjectField("favoriteDog", schema.get("Dog"))
                            .addRealmListField("dogs", schema.get("Dog"));
                    oldVersion++;
                }*/
            }
        };

        return migration;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // this Realm is the correct Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();


      // RealmConfiguration config = new RealmConfiguration.Build();
        Realm.deleteRealm(config);

        realm = Realm.getDefaultInstance();

        this.mFaceDetector = new FaceDetector();

        // max of 1024 pictures
        this.mArrayFileQueue = new ArrayBlockingQueue<String>(1024);
        this.mArrayFilePaths = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setTitle("Screen One");
        setContentView(R.layout.activity_screen1);
        rootLayout = ((LinearLayout) findViewById(R.id.container));
        //rootLayout.removeAllViews();

//      add ProgressBar
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTextView = (TextView) findViewById(R.id.textView);


//      add Toolbar, which includes Menu attributes
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_screenone_toolbar);
        setSupportActionBar(toolbar);

        //mButton = ( Button )rootLayout.findViewById(R.id.import_process);
        mButton = ((Button) findViewById(R.id.import_process));
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load content to Realm Database
                loadCursorToQueue();
                multiThreadsUpload(1);

            }
        });

        //ImageView imageView = (ImageView) findViewById(R.id.face);
//***************************************************************************************************************
//***************************************************************************************************************

        //DEBUG PURPOSE
       // mFaceOverlayView = (com.example.stephenbai.assignment4.FaceOverlayView) findViewById( R.id.face );


        // Need to be placed in base activity
       // realm = Realm.getDefaultInstance();


// upload if isEmpty
     //   if (isEmpty()){

            //Drawable drawable = getResources().getDrawable(R.drawable.ttwo);


       // Bitmap bitmap  = com.example.stephenbai.assignment4.imageHelper.
         //       decodeSampledBitmapFromResource(getResources(),R.drawable.multi,
           //     100,100);

                //Bitmap bitmap = BitmapFactory.decodeResource(
               //   getResources(),R.drawable.ttwo
              //  );
        /*
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
        */
//                mFaceOverlayView.setBitmap(bitmap);


              //  upload(bitmap);

       // }




/*
        basicCRUD(realm);
        basicQuery(realm);
        basicLinkQuery(realm);


        // More complex operations can be executed on another thread.
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String info;
                info = complexReadWrite();
                info += complexQuery();
                return info;
            }

            @Override
            protected void onPostExecute(String result) {
                showStatus(result);
            }
        }.execute();
        */
    }

     static class inSampleSize{
        int size;
        public void setSize(int size){
            this.size = size;
        }


        public int getSize(){
            return this.size;
        }
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public boolean isEmpty(){
        final Picture picture =
                realm.where(Picture.class).findFirst();
        if (picture == null )
            //empty
        return true;
        else{
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    private void showStatus(String txt) {
        Log.i(TAG, txt);
        TextView tv = new TextView(this);
        tv.setText(txt);
        rootLayout.addView(tv);
    }


    private void basicCRUD(Realm realm) {
        showStatus("Perform basic Create/Read/Update/Delete (CRUD) operations...");

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a person
                com.example.stephenbai.assignment4.Person person
                        = realm.createObject(com.example.stephenbai.assignment4.Person.class);

                person.setId(1);
                person.setName("Young Person");
                person.setAge(14);

            }
        });

        // Find the first person (no query conditions) and read a field
        final com.example.stephenbai.assignment4.Person person =
                realm.where(com.example.stephenbai.assignment4.Person.class).findFirst();

        showStatus(person.getName() + ":" + person.getAge());

        // Update person in a transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                person.setName("Senior Person");
                person.setAge(99);
                showStatus(person.getName() + " got older: " + person.getAge());
            }
        });

        // Delete all persons
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(com.example.stephenbai.
                        assignment4.Person.class);
            }
        });
    }


    private void basicQuery(Realm realm) {
        showStatus("\nPerforming basic Query operation...");
        showStatus("Number of persons: " + realm.where(com.example.stephenbai.assignment4.
                Person.class).count());

        RealmResults<com.example.stephenbai.assignment4.Person> results = realm.where(
                com.example.stephenbai.assignment4.Person.class).equalTo("age", 99).findAll();

        showStatus("Size of result set: " + results.size());
    }


    private void basicLinkQuery(Realm realm) {
        showStatus("\nPerforming basic Link Query operation...");
        showStatus("Number of persons: " + realm.where(com.example.stephenbai.
                assignment4.Person.class).count());

        RealmResults<com.example.
                stephenbai.assignment4.Person> results = realm.where(com.example.stephenbai.
                assignment4.Person.class).equalTo("cats.name", "Tiger").findAll();

        showStatus("Size of result set: " + results.size());
    }


    private String complexReadWrite() {
        String status = "\nPerforming complex Read/Write operation...";

        // Open the default realm. All threads must use it's own reference to the realm.
        // Those can not be transferred across threads.
        Realm realm = Realm.getDefaultInstance();

        // Add ten persons in one transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                com.example.stephenbai.assignment4.Dog fido = realm.createObject(com.example.stephenbai.assignment4.Dog.class);
                fido.name = "fido";
                for (int i = 0; i < 10; i++) {
                    Person person = realm.createObject(Person.class);
                    person.setId(i);
                    person.setName("Person no. " + i);
                    person.setAge(i);
                    person.setDog(fido);

                    // The field tempReference is annotated with @Ignore.
                    // This means setTempReference sets the Person tempReference
                    // field directly. The tempReference is NOT saved as part of
                    // the RealmObject:
                    person.setTempReference(42);

                    for (int j = 0; j < i; j++) {
                        com.example.stephenbai.assignment4.Cat cat = realm.createObject(com.example.stephenbai.assignment4.Cat.class);
                        cat.name = "Cat_" + j;
                        person.getCats().add(cat);
                    }
                }
            }
        });

        // Implicit read transactions allow you to access your objects
        status += "\nNumber of persons: " + realm.where(Person.class).count();

        // Iterate over all objects
        for (Person pers : realm.where(Person.class).findAll()) {
            String dogName;
            if (pers.getDog() == null) {
                dogName = "None";
            } else {
                dogName = pers.getDog().name;
            }
            status += "\n" + pers.getName() + ":" + pers.getAge() + " : " + dogName + " : " + pers.getCats().size();
        }

        // Sorting
        RealmResults<Person> sortedPersons = realm.where(Person.class).findAllSorted("age", Sort.DESCENDING);
        status += "\nSorting " + sortedPersons.last().getName() + " == " + realm.where(Person.class).findFirst()
                .getName();

        realm.close();
        return status;
    }

    private String complexQuery() {
        String status = "\n\nPerforming complex Query operation...";

        Realm realm = Realm.getDefaultInstance();
        status += "\nNumber of persons: " + realm.where(Person.class).count();

        // Find all persons where age between 7 and 9 and name begins with "Person".
        RealmResults<Person> results = realm.where(Person.class)
                .between("age", 7, 9)       // Notice implicit "and" operation
                .beginsWith("name", "Person").findAll();
        status += "\nSize of result set: " + results.size();

        realm.close();
        return status;
    }

}
