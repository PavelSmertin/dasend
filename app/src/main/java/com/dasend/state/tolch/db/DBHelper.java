package com.dasend.state.tolch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, TolchContract.DATABASE_NAME, null, TolchContract.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TolchContract.SQL_CREATE_MESSAGES);
        db.execSQL(TolchContract.SQL_CREATE_THREADS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(TolchContract.SQL_DELETE_MESSAGES);
        db.execSQL(TolchContract.SQL_DELETE_THREADS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


  }