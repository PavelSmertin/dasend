package com.dasend.state.tolch.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TolchContract {

    public static final int LOADER_MESSAGES         = 101;
    public static final int LOADER_CONVERSATIONS    = 102;

    public static final String AUTHORITY = "tolch.contract";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tolch.db";


    private static final String INTEGER_TYPE = " INTEGER";
    private static final String INTEGER_TYPE_DEFAULT = " DEFAULT ''";
    private static final String REAL_TYPE = " REAL";
    private static final String REAL_TYPE_DEFAULT = " DEFAULT 0";
    private static final String COMMA_SEP = ", ";

    public static final String SQL_CREATE_MESSAGES =
            "CREATE TABLE " + TolchMessages.TABLE_NAME + " (" +
                    TolchMessages._ID + " INTEGER PRIMARY KEY," +
                    TolchMessages.COLUMN_NAME_THREAD_ID + INTEGER_TYPE + INTEGER_TYPE_DEFAULT + COMMA_SEP +
                    TolchMessages.COLUMN_NAME_MESSAGE_ID + INTEGER_TYPE + INTEGER_TYPE_DEFAULT + COMMA_SEP +
                    TolchMessages.COLUMN_NAME_GOOD + REAL_TYPE + REAL_TYPE_DEFAULT + COMMA_SEP +
                    TolchMessages.COLUMN_NAME_BAD + REAL_TYPE + REAL_TYPE_DEFAULT + COMMA_SEP +
                    TolchMessages.COLUMN_NAME_NEUTRAL + REAL_TYPE + REAL_TYPE_DEFAULT +
                    " )";

    public static final String SQL_DELETE_MESSAGES =
            "DROP TABLE IF EXISTS " + TolchMessages.TABLE_NAME;

    public static final String SQL_CREATE_THREADS =
            "CREATE TABLE " + TolchThreads.TABLE_NAME + " (" +
                    TolchThreads._ID + " INTEGER PRIMARY KEY," +
                    TolchThreads.COLUMN_NAME_THREAD_ID + INTEGER_TYPE + INTEGER_TYPE_DEFAULT + COMMA_SEP +
                    TolchThreads.COLUMN_NAME_AVG_FONE + REAL_TYPE + REAL_TYPE_DEFAULT +
                    " )";

    public static final String SQL_DELETE_THREADS =
            "DROP TABLE IF EXISTS " + TolchThreads.TABLE_NAME;



    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TolchContract() {}

    /* Inner class that defines the table contents */
    public static final class TolchMessages implements BaseColumns {

        public static final String  TABLE_NAME                  = "tolch_messages";
        public static final String  SCHEME                      = "content://";
        public static final String  PATH_MESSAGES               = "/tolch_messages";
        public static final String  PATH_MESSAGES_ID            = "/tolch_messages/";
        public static final Uri     CONTENT_URI                 = Uri.parse(SCHEME + AUTHORITY + PATH_MESSAGES);
        public static final Uri     CONTENT_ID_URI_BASE         = Uri.parse(SCHEME + AUTHORITY + PATH_MESSAGES_ID);
        public static final String  CONTENT_TYPE                = "vnd.android.cursor.dir/vnd.google.tolch_messages";
        public static final String  CONTENT_ITEM_TYPE           = "vnd.android.cursor.item/vnd.google.tolch_messages";
        public static final String  DEFAULT_SORT_ORDER          = "message_id ASC";

        public static final int     MESSAGES_ID_PATH_POSITION           = 1;


        public static final String COLUMN_NAME_THREAD_ID    = "thread_id";
        public static final String COLUMN_NAME_MESSAGE_ID   = "message_id";
        public static final String COLUMN_NAME_GOOD         = "good";
        public static final String COLUMN_NAME_BAD          = "bad";
        public static final String COLUMN_NAME_NEUTRAL      = "neutral";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                TolchContract.TolchMessages._ID,
                TolchContract.TolchMessages.COLUMN_NAME_THREAD_ID,
                TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID,
                TolchContract.TolchMessages.COLUMN_NAME_GOOD,
                TolchContract.TolchMessages.COLUMN_NAME_BAD,
                TolchContract.TolchMessages.COLUMN_NAME_NEUTRAL
        };

        public static final class Thread {
            public static final String  PATH_THREAD_ID              = "/tolch_thread/";
            public static final Uri     CONTENT_ID_URI_BASE         = Uri.parse(SCHEME + AUTHORITY + PATH_MESSAGES + PATH_THREAD_ID);


            public static final int     MESSAGES_THREAD_ID_PATH_POSITION    = 2;
        }

    }


    public static final class TolchThreads implements BaseColumns {

        public static final String  TABLE_NAME          = "tolch_threads";
        public static final String  SCHEME              = "content://";
        public static final String  PATH_THREADS        = "/tolch_threads";
        public static final String  PATH_THREADS_ID     = "/tolch_threads/";
        public static final Uri     CONTENT_URI         = Uri.parse(SCHEME + AUTHORITY + PATH_THREADS);
        public static final Uri     CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_THREADS_ID);
        public static final String  CONTENT_TYPE        = "vnd.android.cursor.dir/vnd.google.tolch_threads";
        public static final String  CONTENT_ITEM_TYPE   = "vnd.android.cursor.item/vnd.google.tolch_threads";
        public static final String  DEFAULT_SORT_ORDER  = "thread_id ASC";

        public static final int     THREADS_ID_PATH_POSITION = 1;

        public static final String COLUMN_NAME_THREAD_ID    = "thread_id";
        public static final String COLUMN_NAME_AVG_FONE     = "avg_fone";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                TolchContract.TolchThreads._ID,
                TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID,
                TolchContract.TolchThreads.COLUMN_NAME_AVG_FONE
        };

    }
}
