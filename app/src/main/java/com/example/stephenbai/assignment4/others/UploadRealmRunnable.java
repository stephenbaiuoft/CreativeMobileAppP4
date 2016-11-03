package com.example.stephenbai.assignment4.others;

/**
 * Created by stephenbai on 2016-10-31.
 */

// class that upload to Realm Database
public class UploadRealmRunnable implements Runnable {
    @Override
    public void run(){
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }
}
