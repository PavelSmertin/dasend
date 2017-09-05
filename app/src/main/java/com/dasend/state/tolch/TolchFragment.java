package com.dasend.state.tolch;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dasend.state.R;
import com.dasend.state.tolch.db.TolchContract;
import com.moez.QKSMS.QKSMSApp;
import com.moez.QKSMS.data.Conversation;
import com.moez.QKSMS.ui.base.QKFragment;
import com.moez.QKSMS.ui.view.MessageListRecyclerView;
import com.moez.QKSMS.ui.view.SmoothLinearLayoutManager;


public class TolchFragment extends QKFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "TolchFragment";

    private BackgroundQueryHandler mBackgroundQueryHandler;

    private TolchMessageAdapter mAdapter;
    private SmoothLinearLayoutManager mLayoutManager;
    private MessageListRecyclerView mRecyclerView;

    private TolchAnalizer mAnalizer;

    public static TolchFragment getInstance() {
        Bundle args = new Bundle();
        TolchFragment fragment = new TolchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TolchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onOpenTolch();
        setHasOptionsMenu(true);


        mBackgroundQueryHandler = new BackgroundQueryHandler(mContext.getContentResolver());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tolch, container, false);
        mRecyclerView = (MessageListRecyclerView) view.findViewById(R.id.conversation);

        mAdapter = new TolchMessageAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new SmoothLinearLayoutManager(mContext);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAnalizer = new TolchAnalizer(getActivity());
        Button calculate = (Button) view.findViewById(R.id.calculate);
        calculate.setOnClickListener(v -> mAnalizer.analize());

        return view;
    }


    private void onOpenTolch() {
        new LoadTolchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setTitle() {
        if (mContext != null) {
            mContext.setTitle(", ");
        }
    }


    private void initLoaderManager() {
        getLoaderManager().initLoader(QKSMSApp.LOADER_MESSAGES, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QKSMSApp.LOADER_MESSAGES) {
            return new CursorLoader(mContext, TolchContract.TolchMessages.CONTENT_URI, TolchContract.TolchMessages.DEFAULT_PROJECTION, null, null, TolchContract.TolchMessages.DEFAULT_SORT_ORDER);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter != null && loader.getId() == QKSMSApp.LOADER_MESSAGES) {
            // Swap the new cursor in.  (The framework will take care of closing the, old cursor once we return.)
            mAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null && loader.getId() == QKSMSApp.LOADER_MESSAGES) {
            mAdapter.changeCursor(null);
        }
    }

    private final class BackgroundQueryHandler extends Conversation.ConversationQueryHandler {
        public BackgroundQueryHandler(ContentResolver contentResolver) {
            super(contentResolver, mContext);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null) {
                cursor.close();
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
        }
    }

    private class LoadTolchTask extends AsyncTask<Void, Void, Void> {

        public LoadTolchTask() {
            Log.d(TAG, "LoadTolchTask");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "Loading tolch");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "Conversation loaded");

            setTitle();

            if (isAdded()) {
                initLoaderManager();
            }
        }
    }
}
