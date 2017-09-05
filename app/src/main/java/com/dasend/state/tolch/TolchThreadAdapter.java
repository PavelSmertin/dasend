package com.dasend.state.tolch;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasend.state.R;
import com.dasend.state.tolch.db.TolchThreadColumns;
import com.dasend.state.tolch.db.TolchThreadItemCache;
import com.dasend.state.tolch.model.TolchThreadItem;
import com.moez.QKSMS.common.utils.CursorUtils;
import com.moez.QKSMS.ui.base.QKActivity;
import com.moez.QKSMS.ui.base.RecyclerCursorAdapter;


public class TolchThreadAdapter extends RecyclerCursorAdapter<TolchThreadViewHolder, TolchThreadItem> {
    private final String TAG = "TolchMessageAdapter";

    private TolchThreadColumns.ColumnsMap mColumnsMap;

    private TolchThreadItemCache mTolchItemCache;

    public TolchThreadAdapter(QKActivity context) {
        super(context);
    }

    protected TolchThreadItem getItem(int position) {
        mCursor.moveToPosition(position);

        long threadId = mCursor.getLong(mColumnsMap.mColumnThreadId);

        return mTolchItemCache.get(threadId, mCursor);
    }

    public TolchThreadColumns.ColumnsMap getColumnsMap() {
        return mColumnsMap;
    }

    @Override
    public TolchThreadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_tolch, parent, false);
        return setupViewHolder(view);
    }

    private TolchThreadViewHolder setupViewHolder(View view) {
        return new TolchThreadViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(TolchThreadViewHolder holder, int position) {
        TolchThreadItem threadItem = getItem(position);

        holder.mData = threadItem;
        holder.mContext = mContext;
        holder.mPresenter = null;

        bindBody(holder, threadItem);

    }

    private void bindBody(TolchThreadViewHolder holder, TolchThreadItem threadItem) {
        holder.mBodyView.setAutoLinkMask(0);
        holder.mBodyView.setText(threadItem.toString());
    }


    @Override
    public void changeCursor(Cursor cursor) {
        if (CursorUtils.isValid(cursor)) {
            mColumnsMap = new TolchThreadColumns.ColumnsMap(cursor);
            mTolchItemCache = new TolchThreadItemCache(mColumnsMap, TolchThreadColumns.CACHE_SIZE);
        }

        super.changeCursor(cursor);
    }


}
