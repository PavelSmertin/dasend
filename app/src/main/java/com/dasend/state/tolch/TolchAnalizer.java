package com.dasend.state.tolch;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.util.Log;

import com.dasend.state.tolch.db.DBHelper;
import com.dasend.state.tolch.db.MessageColumns;
import com.dasend.state.tolch.db.ThreadColumns;
import com.dasend.state.tolch.db.TolchContract;
import com.dasend.state.tolch.model.TolchMessage;
import com.dasend.state.tolch.model.TolchThread;

import io.reactivex.subjects.PublishSubject;

public class TolchAnalizer {

    private Context mContext;

    private int mTotal;
    private int mCurrent;

    private PublishSubject<Integer> subject = PublishSubject.create();

    public TolchAnalizer(Context context) {
        mContext = context;
    }

    public boolean analize() {

        DBHelper dbHelper = new DBHelper(mContext);
        dbHelper.onUpgrade(dbHelper.getReadableDatabase(), 0, 0);

        Cursor cursor = null;
        Cursor cursorAll = null;
        try {
            cursorAll = mContext.getContentResolver().query(Telephony.Sms.CONTENT_URI, new String[] {BaseColumns._ID}, null, null, null);

            if(cursorAll == null) {
                return false;
            }

            Log.d("PROGRESS_all", cursorAll.getCount()+" ");


            cursor = mContext.getContentResolver().query(Telephony.Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build(), ThreadColumns.PROJECTION, null, null, null);

            if(cursor == null) {
                return false;
            }

            Log.d("PROGRESS_threads", cursor.getCount()+" ");


            mTotal = cursorAll.getCount();
            mCurrent = 0;

            cursor.getCount();
            while (cursor.moveToNext()) {



                ThreadColumns.ColumnsMap columnsMap = new ThreadColumns.ColumnsMap(cursor);
                TolchThread thread = new TolchThread(cursor, columnsMap);

                Log.d("PROGRESS_thread", thread.getThreadId()+" "+cursor.getCount());


                thread = prepareThread(thread);

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID, thread.getThreadId());
                values.put(TolchContract.TolchThreads.COLUMN_NAME_AVG_FONE, thread.getFone());
                values.put(TolchContract.TolchThreads.COLUMN_NAME_DATE, thread.getDate());

                // Insert the new row, returning the primary key value of the new row
                mContext.getContentResolver().insert(TolchContract.TolchThreads.CONTENT_URI, values);

            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return true;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private TolchThread prepareThread(TolchThread thread) {
        Cursor cursor = null;
        try {
            //cursor = mContext.getContentResolver().query(Telephony.Sms.CONTENT_URI, MessageColumns.PROJECTION, null, null, null);
            cursor = mContext.getContentResolver().query(Uri.withAppendedPath(Telephony.Sms.Conversations.CONTENT_URI, String.valueOf(thread.getThreadId())), MessageColumns.PROJECTION, null, null, null);

            if(cursor == null) {
                return thread;
            }

            Log.d("PROGRESS_thread", thread.getThreadId()+" count: "+cursor.getCount());

            while (cursor.moveToNext()) {
                MessageColumns.ColumnsMap columnsMap = new MessageColumns.ColumnsMap(cursor);
                TolchMessage message = new TolchMessage(cursor, columnsMap);

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(TolchContract.TolchMessages.COLUMN_NAME_THREAD_ID, message.getThreadId());
                values.put(TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID, message.getMessageId());
                values.put(TolchContract.TolchMessages.COLUMN_NAME_BAD, message.getTolch().getBad());
                values.put(TolchContract.TolchMessages.COLUMN_NAME_GOOD, message.getTolch().getGood());
                values.put(TolchContract.TolchMessages.COLUMN_NAME_NEUTRAL, message.getTolch().getNeutral());

                // Insert the new row, returning the primary key value of the new row
                mContext.getContentResolver().insert(TolchContract.TolchMessages.CONTENT_URI, values);

                thread.increment(message.getTolch());

                int progress = (int)(100 * ((float)(mCurrent++)/((float)mTotal)));
                Log.d("PROGRESS", progress+" "+mCurrent);

                subject.onNext(progress);

            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        thread.calculateFone();
        return thread;
    }

    public PublishSubject<Integer> getSubject() {
        return subject;
    }



}
