package com.dasend.state.tolch.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.util.Log;

public class JoinedMessageColumns {

    private static final int COLUMN_MSG_TYPE            = 0;
    public static final int COLUMN_ID                  = 1;
    private static final int COLUMN_THREAD_ID           = 2;
    private static final int COLUMN_SMS_ADDRESS         = 3;
    private static final int COLUMN_SMS_BODY            = 4;
    private static final int COLUMN_SMS_DATE            = 5;
    private static final int COLUMN_SMS_DATE_SENT       = 6;
    private static final int COLUMN_SMS_TYPE            = 7;
    private static final int COLUMN_SMS_STATUS          = 8;
    private static final int COLUMN_SMS_LOCKED          = 9;
    private static final int COLUMN_SMS_ERROR_CODE      = 10;
    private static final int COLUMN_MMS_SUBJECT         = 11;
    private static final int COLUMN_MMS_SUBJECT_CHARSET = 12;
    private static final int COLUMN_MMS_MESSAGE_TYPE    = 13;
    private static final int COLUMN_MMS_MESSAGE_BOX     = 14;
    private static final int COLUMN_MMS_DELIVERY_REPORT = 15;
    private static final int COLUMN_MMS_READ_REPORT     = 16;
    private static final int COLUMN_MMS_ERROR_TYPE      = 17;
    private static final int COLUMN_MMS_LOCKED          = 18;
    private static final int COLUMN_MMS_STATUS          = 19;
    private static final int COLUMN_MMS_TEXT_ONLY       = 20;

    // tolch
    private static final int COLUMN_GOOD               = 21;
    private static final int COLUMN_BAD                = 22;
    private static final int COLUMN_NEUTRAL            = 23;

    public static final int CACHE_SIZE                 = 50;

    @SuppressLint("InlinedApi")
    public static final String[] PROJECTION = new String[] {
            Telephony.MmsSms.TYPE_DISCRIMINATOR_COLUMN,
            BaseColumns._ID,
            Telephony.Sms.Conversations.THREAD_ID,
            // For SMS
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.DATE_SENT,
            Telephony.Sms.TYPE,
            Telephony.Sms.STATUS,
            Telephony.Sms.LOCKED,
            Telephony.Sms.ERROR_CODE,
            // For MMS
            Telephony.Mms.SUBJECT,
            Telephony.Mms.SUBJECT_CHARSET,
            Telephony.Mms.MESSAGE_TYPE,
            Telephony.Mms.MESSAGE_BOX,
            Telephony.Mms.DELIVERY_REPORT,
            Telephony.Mms.READ_REPORT,
            Telephony.MmsSms.PendingMessages.ERROR_TYPE,
            Telephony.Mms.LOCKED,
            Telephony.Mms.STATUS,
            Telephony.Mms.TEXT_ONLY,

            // tolch
            TolchContract.TolchMessages.COLUMN_NAME_GOOD,
            TolchContract.TolchMessages.COLUMN_NAME_BAD,
            TolchContract.TolchMessages.COLUMN_NAME_NEUTRAL

    };

    public static class ColumnsMap {
        private final String TAG = "ColumnsMap";
        private final boolean DEBUG = false;

        public int mColumnMsgType;
        public int mColumnMsgId;
        public int mColumnThreadId;
        public int mColumnSmsAddress;
        public int mColumnSmsBody;
        public int mColumnSmsDate;
        public int mColumnSmsDateSent;
        public int mColumnSmsType;
        public int mColumnSmsStatus;
        public int mColumnSmsLocked;
        public int mColumnSmsErrorCode;
        public int mColumnMmsSubject;
        public int mColumnMmsSubjectCharset;
        public int mColumnMmsMessageType;
        public int mColumnMmsMessageBox;
        public int mColumnMmsDeliveryReport;
        public int mColumnMmsReadReport;
        public int mColumnMmsErrorType;
        public int mColumnMmsLocked;
        public int mColumnMmsStatus;
        public int mColumnMmsTextOnly;

        // tolch
        public int mColumnGood;
        public int mColumnBad;
        public int mColumnNeutral;


        public ColumnsMap() {
            mColumnMsgType            = COLUMN_MSG_TYPE;
            mColumnMsgId              = COLUMN_ID;
            mColumnThreadId           = COLUMN_THREAD_ID;
            mColumnSmsAddress         = COLUMN_SMS_ADDRESS;
            mColumnSmsBody            = COLUMN_SMS_BODY;
            mColumnSmsDate            = COLUMN_SMS_DATE;
            mColumnSmsDateSent        = COLUMN_SMS_DATE_SENT;
            mColumnSmsType            = COLUMN_SMS_TYPE;
            mColumnSmsStatus          = COLUMN_SMS_STATUS;
            mColumnSmsLocked          = COLUMN_SMS_LOCKED;
            mColumnSmsErrorCode       = COLUMN_SMS_ERROR_CODE;
            mColumnMmsSubject         = COLUMN_MMS_SUBJECT;
            mColumnMmsSubjectCharset  = COLUMN_MMS_SUBJECT_CHARSET;
            mColumnMmsMessageType     = COLUMN_MMS_MESSAGE_TYPE;
            mColumnMmsMessageBox      = COLUMN_MMS_MESSAGE_BOX;
            mColumnMmsDeliveryReport  = COLUMN_MMS_DELIVERY_REPORT;
            mColumnMmsReadReport      = COLUMN_MMS_READ_REPORT;
            mColumnMmsErrorType       = COLUMN_MMS_ERROR_TYPE;
            mColumnMmsLocked          = COLUMN_MMS_LOCKED;
            mColumnMmsStatus          = COLUMN_MMS_STATUS;
            mColumnMmsTextOnly        = COLUMN_MMS_TEXT_ONLY;

            // tolch
            mColumnGood               = COLUMN_GOOD;
            mColumnBad                = COLUMN_BAD;
            mColumnNeutral            = COLUMN_NEUTRAL;
        }

        @SuppressLint("InlinedApi")
        public ColumnsMap(Cursor cursor) {
            // Ignore all 'not found' exceptions since the custom columns
            // may be just a subset of the default columns.
            try {
                mColumnMsgType = cursor.getColumnIndexOrThrow(Telephony.MmsSms.TYPE_DISCRIMINATOR_COLUMN);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

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
                mColumnSmsAddress = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnSmsBody = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnSmsDate = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnSmsDateSent = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnSmsType = cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnSmsStatus = cursor.getColumnIndexOrThrow(Telephony.Sms.STATUS);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnSmsLocked = cursor.getColumnIndexOrThrow(Telephony.Sms.LOCKED);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnSmsErrorCode = cursor.getColumnIndexOrThrow(Telephony.Sms.ERROR_CODE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsSubject = cursor.getColumnIndexOrThrow(Telephony.Mms.SUBJECT);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsSubjectCharset = cursor.getColumnIndexOrThrow(Telephony.Mms.SUBJECT_CHARSET);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsMessageType = cursor.getColumnIndexOrThrow(Telephony.Mms.MESSAGE_TYPE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsMessageBox = cursor.getColumnIndexOrThrow(Telephony.Mms.MESSAGE_BOX);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsDeliveryReport = cursor.getColumnIndexOrThrow(Telephony.Mms.DELIVERY_REPORT);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsReadReport = cursor.getColumnIndexOrThrow(Telephony.Mms.READ_REPORT);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsErrorType = cursor.getColumnIndexOrThrow(Telephony.MmsSms.PendingMessages.ERROR_TYPE);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsLocked = cursor.getColumnIndexOrThrow(Telephony.Mms.LOCKED);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsStatus = cursor.getColumnIndexOrThrow(Telephony.Mms.STATUS);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            try {
                mColumnMmsTextOnly = cursor.getColumnIndexOrThrow(Telephony.Mms.TEXT_ONLY);
            } catch (IllegalArgumentException e) {
                if (DEBUG) Log.w(TAG, e.getMessage());
            }

            // tolch
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
