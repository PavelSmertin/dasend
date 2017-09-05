package com.dasend.state.tolch;

import android.view.View;
import android.widget.TextView;

import com.dasend.state.R;
import com.dasend.state.tolch.model.TolchMessageItem;
import com.moez.QKSMS.ui.base.ClickyViewHolder;
import com.moez.QKSMS.ui.base.QKActivity;
import com.moez.QKSMS.ui.mms.Presenter;

public class TolchMessageViewHolder extends ClickyViewHolder<TolchMessageItem> {
    private final String TAG = "TolchMessageViewHolder";

    // Views
    protected View mRoot;
    protected TextView mBodyView;

    protected Presenter mPresenter;

    public TolchMessageViewHolder(QKActivity context, View view) {
        super(context, view);
        mRoot = view;
        mBodyView = (TextView) view.findViewById(R.id.body_view);

    }



}
