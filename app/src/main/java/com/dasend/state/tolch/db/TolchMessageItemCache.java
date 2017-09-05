package com.dasend.state.tolch.db;

import android.database.Cursor;
import android.util.LruCache;

import com.dasend.state.tolch.model.TolchMessageItem;
import com.moez.QKSMS.common.utils.CursorUtils;


public class TolchMessageItemCache extends LruCache<Long, TolchMessageItem> {
    private final String TAG = "MessageItemCache";

    private TolchMessageColumns.ColumnsMap mColumnsMap;

    public TolchMessageItemCache(TolchMessageColumns.ColumnsMap columnsMap, int maxSize) {
        super(maxSize);
        mColumnsMap = columnsMap;
    }

    public long getKey(long msgId) {
        return msgId;
    }

    public TolchMessageItem get(long msgId, Cursor c) {
        long key = getKey(msgId);
        TolchMessageItem item = get(key);

        if (item == null && CursorUtils.isValid(c)) {

            item = new TolchMessageItem(c, mColumnsMap);
            key = getKey(item.mMessageId);
            put(key, item);

        }
        return item;
    }
}
