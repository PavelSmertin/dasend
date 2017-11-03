package com.dasend.state.tolch.model;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.dasend.state.tolch.db.ThreadColumns;

public class TolchThread {

    private long     mThreadId;
    private int      mMessagesCount;

    private long     mBad;
    private long     mGood;
    private long     mNeutral;
    private float    mFone;


    private String   mDate;

    @SuppressLint("NewApi")
    public TolchThread(Cursor cursor, ThreadColumns.ColumnsMap columnsMap) {
        mThreadId           = cursor.getLong(columnsMap.mColumnThreadId);
        mMessagesCount      = cursor.getInt(columnsMap.mColumnMessagesCount);
        mDate               = cursor.getString(columnsMap.mColumnDate);
    }



    public long getThreadId() {
        return mThreadId;
    }

    public int getMessagesCount() {
        return mMessagesCount;
    }

    public String getDate() {
        return mDate;
    }

    public float getFone() {
        return mFone;
    }

    public void calculateFone() {
        mFone = (float)mGood / mMessagesCount - (float)mBad / mMessagesCount;

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

        mNeutral++;
    }
}
