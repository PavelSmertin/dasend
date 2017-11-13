package com.dasend.state.tolch.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.Log;

public class TolchThreadColumns {

    private static final int COLUMN_THREAD_ID           = 1;
    private static final int COLUMN_DATE                = 2;
    private static final int COLUMN_READY               = 3;
    private static final int COLUMN_BAD                 = 4;
    private static final int COLUMN_GOOD                = 5;
    private static final int COLUMN_NEUTRAL             = 6;
    private static final int COLUMN_FONE                = 7;

    public static final int CACHE_SIZE         = 50;

    public static class ColumnsMap {
        private final String TAG = "TolchThreadColumns.ColumnsMap";
        private final boolean DEBUG = false;

        public int mColumnThreadId;
        public int mColumnDate;
        public int mColumnReady;

        public int mColumnBad;
        public int mColumnGood;
        public int mColumnNeutral;
        public int mColumnFone;


        public ColumnsMap() {
            mColumnThreadId     = COLUMN_THREAD_ID;
            mColumnDate         = COLUMN_DATE;
            mColumnReady        = COLUMN_READY;
            mColumnBad          = COLUMN_BAD;
            mColumnGood         = COLUMN_GOOD;
            mColumnNeutral      = COLUMN_NEUTRAL;
            mColumnFone         = COLUMN_FONE;

        }

        @SuppressLint("InlinedApi")
        public ColumnsMap(Cursor cursor) {
            // Ignore all 'not found' exceptions since the custom columns
            // may be just a subset of the default columns.
            try {
                mColumnThreadId = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnDate = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_DATE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnReady = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_READY);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnBad = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_BAD);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnGood = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_GOOD);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnNeutral = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_NEUTRAL);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnFone = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_AVG_FONE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }
        }
    }
}
