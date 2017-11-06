package com.dasend.state.tolch.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.util.Log;

public class MessageColumns {

    public static final int COLUMN_ID                  = 1;
    public static final int COLUMN_THREAD_ID           = 2;
    public static final int COLUMN_SMS_BODY            = 3;


    public static final int CACHE_SIZE         = 50;

    @SuppressLint("InlinedApi")
    public static final String[] PROJECTION = new String[] {
            BaseColumns._ID,
            Telephony.Sms.Conversations.THREAD_ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY
    };

    public static class ColumnsMap {
        private final String TAG = "ColumnsMap";
        private final boolean DEBUG = false;

        public int mColumnMsgId;
        public int mColumnThreadId;
        public int mColumnSmsBody;

        public ColumnsMap() {
            mColumnMsgId              = COLUMN_ID;
            mColumnThreadId           = COLUMN_THREAD_ID;
            mColumnSmsBody            = COLUMN_SMS_BODY;

        }

        @SuppressLint("InlinedApi")
        public ColumnsMap(Cursor cursor) {
            try {
                mColumnMsgId = cursor.getColumnIndexOrThrow(BaseColumns._ID);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnThreadId = cursor.getColumnIndexOrThrow(Telephony.Sms.Conversations.THREAD_ID);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnSmsBody = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

        }
    }
}
