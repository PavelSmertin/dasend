package com.dasend.state.tolch.model;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.dasend.state.tolch.db.ThreadColumns;

public class TolchThread {

    private long     mThreadId;
    private int      mMessagesCount;

    private long     mBad;
    private long     mGood;
    private float    mFone;


    private long   mDate;

    @SuppressLint("NewApi")
    public TolchThread(Cursor cursor) {

        ThreadColumns.ColumnsMap columnsMap = new ThreadColumns.ColumnsMap(cursor);

        mThreadId           = cursor.getLong(columnsMap.mColumnThreadId);
        mMessagesCount      = cursor.getInt(columnsMap.mColumnMessagesCount);
        mDate               = cursor.getLong(columnsMap.mColumnDate);
    }


    public long getThreadId() {
        return mThreadId;
    }

    public long getDate() {
        return mDate;
    }

    public float getFone() {
        return mFone;
    }

    public void calculateFone() {
        if(mMessagesCount == 0) {
            return;
        }
        mFone = (float)Math.sin((Math.PI/2) * ((float)(mGood - mBad) / mMessagesCount));
    }

    public void increment(Tolch tolch) {
        if (tolch.getBad() > tolch.getNeutral() && tolch.getBad() > tolch.getGood()) {
            mBad++;
            return;
        }

        if (tolch.getGood() > tolch.getNeutral() && tolch.getGood() > tolch.getBad()) {
            mGood++;
            return;
        }

    }
}
