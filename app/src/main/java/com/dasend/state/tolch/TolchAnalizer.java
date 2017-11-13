package com.dasend.state.tolch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Telephony;

import com.dasend.state.tolch.db.DBHelper;
import com.dasend.state.tolch.db.MessageColumns;
import com.dasend.state.tolch.db.ThreadColumns;
import com.dasend.state.tolch.db.TolchContract;
import com.dasend.state.tolch.db.TolchMessageColumns;
import com.dasend.state.tolch.db.TolchThreadColumns;
import com.dasend.state.tolch.model.TolchMessage;
import com.dasend.state.tolch.model.TolchThread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

public class TolchAnalizer {

    private Context mContext;

    private int mTotal;
    private int mCurrent;

    private PublishSubject<Integer> subject = PublishSubject.create();

    public TolchAnalizer(Context context) {
        mContext = context;
    }

    public boolean syncInitial() {
        DBHelper dbHelper = new DBHelper(mContext);
        dbHelper.onUpgrade(dbHelper.getReadableDatabase(), 0, 0);

        getTotalSmsCount();

        Cursor cursor = null;
        try {

            // get threads
            cursor = mContext.getContentResolver().query(
                    Telephony.Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build(),
                    ThreadColumns.PROJECTION,
                    null,
                    null,
                    null
            );

            if(cursor == null) {
                return false;
            }

            // preSync. Чтобы число расчетов всегда совпадало числом трэдов
            while (cursor.moveToNext()) {
                TolchThread thread = new TolchThread(cursor);
                createTolchThread(thread);
            }

            // calculate
            mCurrent = 0;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                TolchThread thread = new TolchThread(cursor);

                thread = calcThread(thread);
                thread.setReady(true);
                updateTolchThread(thread);
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

    public boolean syncThreads() {

        List<Long> ids = getTolchThreads();

        if(ids == null) {
            return syncInitial();
        }

        Cursor cursorThread = null;
        try {
            // get threads
            cursorThread = mContext.getContentResolver().query(
                    Telephony.Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build(),
                    ThreadColumns.PROJECTION,
                    null,
                    null,
                    null
            );

            if(cursorThread == null) {
                return true;
            }

            //
            while (cursorThread.moveToNext()) {
                ThreadColumns.ColumnsMap columnsMap = new ThreadColumns.ColumnsMap(cursorThread);
                Long threadId = cursorThread.getLong(columnsMap.mColumnThreadId);
                if(ids.contains(threadId)) {
                    ids.remove(threadId);
                    continue;
                }

                TolchThread thread = new TolchThread(cursorThread);
                thread = calcThread(thread);
                createTolchThread(thread);
            }

            if(ids.size() > 0) {
                removeTolchThreads(ids);
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursorThread != null) {
                cursorThread.close();
            }
        }

        return true;
    }

    public boolean syncThreadById(long threadId) {

        Cursor cursorThread = null;
        Cursor cursorTolch = null;

        try {

            // Подгружаем существующий трэд, чтобы обновить дату расчета
            cursorThread = mContext.getContentResolver().query(
                    Telephony.Threads.CONTENT_URI.buildUpon()
                            .appendQueryParameter("simple", "true")
                            .build(),
                    ThreadColumns.PROJECTION,
                    Telephony.Threads._ID + " = " + Long.toString(threadId),
                    null,
                    null
            );

            assert cursorThread != null;
            cursorThread.moveToNext();

            TolchThread thread = new TolchThread(cursorThread);

            // Подгружаем существующий расчет для существующего трэда
            cursorTolch = mContext.getContentResolver().query(
                    Uri.withAppendedPath(TolchContract.TolchThreads.CONTENT_ID_URI_BASE, Long.toString(threadId)),
                    TolchContract.TolchThreads.DEFAULT_PROJECTION,
                    null,
                    null,
                    null
            );

            if(cursorTolch == null) {
                thread = calcThread(thread);
                createTolchThread(thread);
                return true;
            }

            thread = recalcThread(thread);
            updateTolchThread(thread);


        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursorThread != null) {
                cursorThread.close();
            }
            if (cursorTolch != null) {
                cursorTolch.close();
            }
        }
        return true;
    }

    private void getTotalSmsCount() {
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(Telephony.Sms.CONTENT_URI, new String[] {BaseColumns._ID}, null, null, null);
            if(cursor == null) {
                mTotal = 0;
                return;
            }
            mTotal = cursor.getCount();
        } catch (SQLiteException e) {
            // TODO report error
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void createTolchThread(TolchThread thread) {
        ContentValues values = new ContentValues();
        values.put(TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID, thread.getThreadId());
        values.put(TolchContract.TolchThreads.COLUMN_NAME_AVG_FONE, thread.getFone());
        values.put(TolchContract.TolchThreads.COLUMN_NAME_DATE, thread.getDate());
        values.put(TolchContract.TolchThreads.COLUMN_NAME_READY, thread.isReady());
        mContext.getContentResolver().insert(TolchContract.TolchThreads.CONTENT_URI, values);
    }

    private void updateTolchThread(TolchThread thread) {

        ContentValues values = new ContentValues();
        values.put(TolchContract.TolchThreads.COLUMN_NAME_AVG_FONE, thread.getFone());
        values.put(TolchContract.TolchThreads.COLUMN_NAME_DATE, thread.getDate());
        values.put(TolchContract.TolchThreads.COLUMN_NAME_READY, thread.isReady());

        mContext.getContentResolver().update(
                TolchContract.TolchThreads.CONTENT_URI,
                values,
                TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID + " = " + Long.toString(thread.getThreadId()),
                null
        );
    }

    private TolchThread calcThread(TolchThread thread) {
        Cursor cursor = null;
        try {
            //cursor = mContext.getContentResolver().query(Telephony.Sms.CONTENT_URI, MessageColumns.PROJECTION, null, null, null);
            cursor = mContext.getContentResolver().query(
                    Uri.withAppendedPath(
                            Telephony.Sms.Conversations.CONTENT_URI,
                            String.valueOf(thread.getThreadId())
                    ), MessageColumns.PROJECTION, null, null, null);

            if(cursor == null) {
                return thread;
            }

            while (cursor.moveToNext()) {
                TolchMessage message = new TolchMessage(cursor);

                createTolchMessage(message);

                thread.increment(message.getTolch());

                int progress = (int)(100 * ((float)(mCurrent++)/((float)mTotal)));

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

    private TolchThread recalcThread(TolchThread thread) {
        Cursor cursorMessage = null;
        try {

            List<Long> ids = getTolchMessages(thread);

            if(ids == null) {
                return calcThread(thread);
            }

            // Подгружаем существующие сообщения для трэда
            cursorMessage = mContext.getContentResolver().query(
                    Uri.withAppendedPath(
                            Telephony.Sms.Conversations.CONTENT_URI,
                            String.valueOf(thread.getThreadId())
                    ), MessageColumns.PROJECTION, null, null, null);

            if(cursorMessage == null) {
                return thread;
            }

            //
            while (cursorMessage.moveToNext()) {
                MessageColumns.ColumnsMap columnsMap = new MessageColumns.ColumnsMap(cursorMessage);
                Long messageId = cursorMessage.getLong(columnsMap.mColumnMsgId);
                if(ids.contains(messageId)) {
                    ids.remove(messageId);
                    continue;
                }

                TolchMessage message = new TolchMessage(cursorMessage);
                createTolchMessage(message);
                thread.increment(message.getTolch());
            }

            if(ids.size() > 0) {
                removeTolchMessages(ids);
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursorMessage != null) {
                cursorMessage.close();
            }
        }

        thread.calculateFone();
        return thread;
    }

    private void createTolchMessage(TolchMessage message) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID, message.getMessageId());
        values.put(TolchContract.TolchMessages.COLUMN_NAME_THREAD_ID, message.getThreadId());
        values.put(TolchContract.TolchMessages.COLUMN_NAME_BAD, message.getTolch().getBad());
        values.put(TolchContract.TolchMessages.COLUMN_NAME_GOOD, message.getTolch().getGood());
        values.put(TolchContract.TolchMessages.COLUMN_NAME_NEUTRAL, message.getTolch().getNeutral());

        // Insert the new row, returning the primary key value of the new row
        mContext.getContentResolver().insert(TolchContract.TolchMessages.CONTENT_URI, values);
    }

    private List<Long> getTolchThreads() {
        List<Long> ids = new ArrayList<>();
        Cursor cursorTolch = null;
        try {

            // Подгружаем существующие расчеты для существующих  трэдов
            cursorTolch = mContext.getContentResolver().query(
                    TolchContract.TolchThreads.CONTENT_URI,
                    TolchContract.TolchThreads.DEFAULT_PROJECTION,
                    null,
                    null,
                    null);

            if(cursorTolch == null) {
                return null;
            }

            while (cursorTolch.moveToNext()) {
                TolchThreadColumns.ColumnsMap columnsMap = new TolchThreadColumns.ColumnsMap(cursorTolch);
                ids.add(cursorTolch.getLong(columnsMap.mColumnThreadId));
            }

        } catch (SQLiteException e) {
            cursorTolch = null;
            e.printStackTrace();
        } finally {
            if(cursorTolch != null) {
                cursorTolch.close();
            }
        }

        return ids;

    }

    private List<Long> getTolchMessages(TolchThread thread) {
        List<Long> ids = new ArrayList<>();
        Cursor cursorTolch = null;
        try {
            // Подгружаем существующие расчеты для существующих сообщений
            cursorTolch = mContext.getContentResolver().query(Uri.withAppendedPath(
                    TolchContract.TolchMessages.Thread.CONTENT_ID_URI_BASE,
                    String.valueOf(thread.getThreadId())
                    ),
                    TolchContract.TolchMessages.DEFAULT_PROJECTION,
                    null,
                    null,
                    null);

            if(cursorTolch == null) {
                return null;
            }

            while (cursorTolch.moveToNext()) {
                TolchMessageColumns.ColumnsMap columnsMap = new TolchMessageColumns.ColumnsMap(cursorTolch);
                ids.add(cursorTolch.getLong(columnsMap.mColumnMsgId));
            }

        } catch (SQLiteException e) {
            cursorTolch = null;
            e.printStackTrace();
        } finally {
            if(cursorTolch != null) {
                cursorTolch.close();
            }
        }

        return ids;

    }

    private void removeTolchThreads(List<Long> ids) {
        mContext.getContentResolver().delete(
                TolchContract.TolchThreads.CONTENT_URI,
                TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID + " IN (" +  explodeIds(ids) + ")",
                null
        );
    }

    private void removeTolchMessages(List<Long> ids) {
        mContext.getContentResolver().delete(
                TolchContract.TolchMessages.CONTENT_URI,
                TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID + " IN (" + explodeIds(ids) + ")",
                null
        );
    }

    private String explodeIds(List<Long> ids) {
        StringBuilder builder = new StringBuilder();
        Iterator<Long> iterator = ids.iterator();

        builder.append("(");
        while(iterator.hasNext()) {
            builder.append(iterator.next());
            if(iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(")");

        return builder.toString();
    }

    public PublishSubject<Integer> getSubject() {
        return subject;
    }


}
