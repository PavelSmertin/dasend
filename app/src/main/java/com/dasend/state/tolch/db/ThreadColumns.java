package com.dasend.state.tolch.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

public class ThreadColumns {

    private static final int COLUMN_THREAD_ID           = 1;
    private static final int COLUMN_MESSAGE_COUNT       = 2;
    private static final int COLUMN_DATE                = 3;

    public static final int CACHE_SIZE         = 50;

    @SuppressLint("InlinedApi")
    public static final String[] PROJECTION = {
            Telephony.Threads._ID,
            Telephony.Threads.MESSAGE_COUNT,
            Telephony.Threads.DATE
    };


    public static class ColumnsMap {
        private final String TAG = "ThreadColumns.ColumnsMap";
        private final boolean DEBUG = false;

        public int mColumnThreadId;
        public int mColumnMessagesCount;
        public int mColumnDate;

        public ColumnsMap() {
            mColumnThreadId           = COLUMN_THREAD_ID;
            mColumnMessagesCount      = COLUMN_MESSAGE_COUNT;
            mColumnDate               = COLUMN_DATE;
        }

        @SuppressLint("InlinedApi")
        public ColumnsMap(Cursor cursor) {
            try {
                mColumnThreadId = cursor.getColumnIndexOrThrow(Telephony.Threads._ID);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMessagesCount = cursor.getColumnIndexOrThrow(Telephony.Threads.MESSAGE_COUNT);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnDate = cursor.getColumnIndexOrThrow(Telephony.Threads.DATE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

        }
    }
}
