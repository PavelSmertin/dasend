package com.dasend.state.tolch;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.LruCache;

import com.dasend.state.tolch.db.JoinedMessageColumns;
import com.google.android.mms.MmsException;
import com.moez.QKSMS.common.utils.CursorUtils;

import java.util.regex.Pattern;

public class MessageItemCache extends LruCache<Long, JoinedMessageItem> {
    private final String TAG = "MessageItemCache";

    private Context mContext;
    private JoinedMessageColumns.ColumnsMap mColumnsMap;
    private Pattern mSearchHighlighter;

    public MessageItemCache(Context context, JoinedMessageColumns.ColumnsMap columnsMap, Pattern searchHighlighter, int maxSize) {
        super(maxSize);

        mContext = context;
        mColumnsMap = columnsMap;
        mSearchHighlighter = searchHighlighter;
    }

    @Override
    protected void entryRemoved(boolean evicted, Long key, JoinedMessageItem oldValue, JoinedMessageItem newValue) {
        oldValue.cancelPduLoading();
    }

    /**
     * Generates a unique key for this message item given its type and message ID.
     *
     * @param type
     * @param msgId
     */
    public long getKey(String type, long msgId) {
        if (type.equals("mms")) {
            return -msgId;
        } else {
            return msgId;
        }
    }


    public JoinedMessageItem get(String type, long msgId, Cursor c) {
        long key = getKey(type, msgId);
        JoinedMessageItem item = get(key);

        if (item == null && CursorUtils.isValid(c)) {
            try {
                item = new JoinedMessageItem(mContext, type, c, mColumnsMap, mSearchHighlighter, false);
                key = getKey(item.mType, item.mMsgId);
                put(key, item);
            } catch (MmsException e) {
                Log.e(TAG, "getCachedMessageItem: ", e);
            }
        }
        return item;
    }
}
