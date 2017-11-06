package com.dasend.state.tolch.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.Log;

public class TolchMessageColumns {


    private static final int COLUMN_ID           = 1;
    private static final int COLUMN_THREAD_ID    = 2;
    private static final int COLUMN_GOOD         = 3;
    private static final int COLUMN_BAD          = 4;
    private static final int COLUMN_NEUTRAL      = 5;


    public static final int CACHE_SIZE         = 50;



    public static class ColumnsMap {
        private final String TAG = "ColumnsMap";
        private final boolean DEBUG = false;

        public int mColumnId;
        public int mColumnMsgId;
        public int mColumnThreadId;
        public int mColumnGood;
        public int mColumnBad;
        public int mColumnNeutral;


        public ColumnsMap() {
            mColumnId           = COLUMN_ID;
            mColumnThreadId     = COLUMN_THREAD_ID;
            mColumnGood         = COLUMN_GOOD;
            mColumnBad          = COLUMN_BAD;
            mColumnNeutral      = COLUMN_NEUTRAL;


        }

        @SuppressLint("InlinedApi")
        public ColumnsMap(Cursor cursor) {
            // Ignore all 'not found' exceptions since the custom columns
            // may be just a subset of the default columns.
            try {
                mColumnId = cursor.getColumnIndexOrThrow(TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnThreadId = cursor.getColumnIndexOrThrow(TolchContract.TolchMessages.COLUMN_NAME_THREAD_ID);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }


            try {
                mColumnGood = cursor.getColumnIndexOrThrow(TolchContract.TolchMessages.COLUMN_NAME_GOOD);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnBad = cursor.getColumnIndexOrThrow(TolchContract.TolchMessages.COLUMN_NAME_BAD);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnNeutral = cursor.getColumnIndexOrThrow(TolchContract.TolchMessages.COLUMN_NAME_NEUTRAL);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

        }
    }
}
