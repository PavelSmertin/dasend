package com.dasend.state.tolch.receivers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.dasend.state.tolch.TolchAnalizer;
import com.dasend.state.tolch.db.MessageColumns;
import com.moez.QKSMS.receiver.MessagingReceiver;

public class SmsReceiver extends MessagingReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        TolchAnalizer analizer = new TolchAnalizer(context);

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(getUri(), MessageColumns.PROJECTION, null, null, null);
            cursor.moveToNext();
            MessageColumns.ColumnsMap columnsMap = new MessageColumns.ColumnsMap(cursor);
            analizer.analizeThread(cursor.getLong(columnsMap.mColumnThreadId));
        }  catch (SQLiteException e) {
            // TODO report error
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

