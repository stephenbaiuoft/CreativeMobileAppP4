package com.example.stephenbai.assignment4.others;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by stephenbai on 2016-10-31.
 */

public class UploadManager {
    private static UploadManager sInstance;
    private final BlockingQueue <Runnable> mUploadWorkQueue;
    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    // A managed pool of background download threads
    private final ThreadPoolExecutor mUploadThreadPool;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
         sInstance = new UploadManager();
    }

    // set CORE_POOL_SIZE && MAXIMUM_POOL_SIZE same?
    private UploadManager(){
        mUploadWorkQueue = new LinkedBlockingDeque<Runnable>();

        mUploadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mUploadWorkQueue);

    }

    public static UploadManager getsInstance(){
        return sInstance;
    }
}
