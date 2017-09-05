package com.dasend.state.tolch.model;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.dasend.state.tolch.TolchCalculator;
import com.dasend.state.tolch.db.MessageColumns;


public class TolchMessage {

    private long     mThreadId;
    private long     mMessageId;
    private Tolch    mTolch;

    @SuppressLint("NewApi")
    public TolchMessage(Cursor cursor, MessageColumns.ColumnsMap columnsMap) {
        mMessageId  = cursor.getLong(columnsMap.mColumnMsgId);
        mThreadId   = cursor.getLong(columnsMap.mColumnThreadId);

        TolchCalculator tolchCalculator = new TolchCalculator();
        String body = cursor.getString(columnsMap.mColumnSmsBody);

        mTolch = tolchCalculator.calculate(body);
    }


    public long getThreadId() {
        return mThreadId;
    }

    public long getMessageId() {
        return mMessageId;
    }

    public Tolch getTolch() {
        return mTolch;
    }


}
