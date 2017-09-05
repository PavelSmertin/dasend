package com.dasend.state.tolch.db;

import android.database.Cursor;
import android.util.LruCache;

import com.dasend.state.tolch.model.TolchThreadItem;
import com.moez.QKSMS.common.utils.CursorUtils;


public class TolchThreadItemCache extends LruCache<Long, TolchThreadItem> {
    private final String TAG = "MessageItemCache";

    private TolchThreadColumns.ColumnsMap mColumnsMap;

    public TolchThreadItemCache(TolchThreadColumns.ColumnsMap columnsMap, int maxSize) {
        super(maxSize);
        mColumnsMap = columnsMap;
    }

    public long getKey(long msgId) {
        return msgId;
    }

    public TolchThreadItem get(long msgId, Cursor c) {
        long key = getKey(msgId);
        TolchThreadItem item = get(key);

        if (item == null && CursorUtils.isValid(c)) {

            item = new TolchThreadItem(c, mColumnsMap);
            key = getKey(item.getThreadId());
            put(key, item);

        }
        return item;
    }
}
