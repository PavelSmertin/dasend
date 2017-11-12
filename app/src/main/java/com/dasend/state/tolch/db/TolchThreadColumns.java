package com.dasend.state.tolch.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.Log;

public class TolchThreadColumns {

    private static final int COLUMN_THREAD_ID           = 1;
    private static final int COLUMN_FONE                = 2;
    private static final int COLUMN_DATE                = 3;

    public static final int CACHE_SIZE         = 50;

    public static class ColumnsMap {
        private final String TAG = "TolchThreadColumns.ColumnsMap";
        private final boolean DEBUG = false;

        public int mColumnThreadId;
        public int mColumnFone;
        public int mColumnDate;

        public ColumnsMap() {
            mColumnThreadId     = COLUMN_THREAD_ID;
            mColumnFone         = COLUMN_FONE;
            mColumnDate         = COLUMN_DATE;
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
                mColumnFone = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_AVG_FONE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnDate = cursor.getColumnIndexOrThrow(TolchContract.TolchThreads.COLUMN_NAME_DATE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }
        }
    }
}
