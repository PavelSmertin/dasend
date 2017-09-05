package com.dasend.state.tolch;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasend.state.R;
import com.dasend.state.tolch.db.TolchMessageColumns;
import com.dasend.state.tolch.db.TolchMessageItemCache;
import com.dasend.state.tolch.model.TolchMessageItem;
import com.moez.QKSMS.common.utils.CursorUtils;
import com.moez.QKSMS.ui.base.QKActivity;
import com.moez.QKSMS.ui.base.RecyclerCursorAdapter;


public class TolchMessageAdapter extends RecyclerCursorAdapter<TolchMessageViewHolder, TolchMessageItem> {
    private final String TAG = "TolchMessageAdapter";

    private TolchMessageColumns.ColumnsMap mColumnsMap;

    private TolchMessageItemCache mTolchMessageItemCache;

    public TolchMessageAdapter(QKActivity context) {
        super(context);
    }

    protected TolchMessageItem getItem(int position) {
        mCursor.moveToPosition(position);

        long msgId = mCursor.getLong(mColumnsMap.mColumnMsgId);

        return mTolchMessageItemCache.get(msgId, mCursor);
    }

    public TolchMessageColumns.ColumnsMap getColumnsMap() {
        return mColumnsMap;
    }

    @Override
    public TolchMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_tolch, parent, false);
        return setupViewHolder(view);
    }

    private TolchMessageViewHolder setupViewHolder(View view) {
        return new TolchMessageViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(TolchMessageViewHolder holder, int position) {
        TolchMessageItem tolchMessageItem = getItem(position);

        holder.mData = tolchMessageItem;
        holder.mContext = mContext;
        holder.mPresenter = null;

        bindBody(holder, tolchMessageItem);

    }

    private void bindBody(TolchMessageViewHolder holder, TolchMessageItem tolchMessageItem) {
        holder.mBodyView.setAutoLinkMask(0);
        holder.mBodyView.setText(tolchMessageItem.toString());
    }


    @Override
    public void changeCursor(Cursor cursor) {
        if (CursorUtils.isValid(cursor)) {
            mColumnsMap = new TolchMessageColumns.ColumnsMap(cursor);
            mTolchMessageItemCache = new TolchMessageItemCache(mColumnsMap, TolchMessageColumns.CACHE_SIZE);
        }

        super.changeCursor(cursor);
    }


}
