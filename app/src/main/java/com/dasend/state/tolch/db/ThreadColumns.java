package com.dasend.state.tolch.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

public class ThreadColumns {

    public static final int COLUMN_ID                  = 1;
    public static final int COLUMN_MESSAGE_COUNT       = 2;

    public static final int CACHE_SIZE         = 50;

    @SuppressLint("InlinedApi")
    public static final String[] PROJECTION = {
            Telephony.Threads._ID,
            Telephony.Threads.MESSAGE_COUNT
    };


    public static class ColumnsMap {
        private final String TAG = "ColumnsMap";
        private final boolean DEBUG = false;

        public int mColumnThreadId;
        public int mColumnMessagesCount;

        public ColumnsMap() {
            mColumnThreadId           = COLUMN_ID;
            mColumnMessagesCount      = COLUMN_MESSAGE_COUNT;

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

        }
    }
}
