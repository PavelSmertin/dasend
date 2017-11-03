package com.dasend.state.tolch.conversationlist;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.MatrixCursor;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dasend.state.R;
import com.dasend.state.tolch.db.TolchContract;
import com.dasend.state.tolch.db.TolchThreadColumns;
import com.dasend.state.tolch.messagelist.MessageListActivity;
import com.dasend.state.tolch.views.SimpleDividerItemDecoration;
import com.melnykov.fab.FloatingActionButton;
import com.moez.QKSMS.QKSMSApp;
import com.moez.QKSMS.common.BlockedConversationHelper;
import com.moez.QKSMS.common.DialogHelper;
import com.moez.QKSMS.common.LiveViewManager;
import com.moez.QKSMS.common.utils.ColorUtils;
import com.moez.QKSMS.data.Contact;
import com.moez.QKSMS.data.ConversationLegacy;
import com.moez.QKSMS.enums.QKPreference;
import com.moez.QKSMS.ui.MainActivity;
import com.moez.QKSMS.ui.ThemeManager;
import com.moez.QKSMS.ui.base.QKFragment;
import com.moez.QKSMS.ui.base.RecyclerCursorAdapter;
import com.moez.QKSMS.ui.compose.ComposeActivity;
import com.moez.QKSMS.ui.dialog.conversationdetails.ConversationDetailsDialog;
import com.moez.QKSMS.ui.settings.SettingsFragment;

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ConversationListFragment extends QKFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerCursorAdapter.ItemClickListener<Conversation>, RecyclerCursorAdapter.MultiSelectListener, Observer {

    public static final String TAG = "ConversationListFragment";

    private static final int VERTICAL_ITEM_SPACE = 6;

    @BindView(R.id.empty_state)          View mEmptyState;
    @BindView(R.id.empty_state_icon)     ImageView mEmptyStateIcon;
    @BindView(R.id.conversations_list)   RecyclerView mRecyclerView;
    @BindView(R.id.fab)                  FloatingActionButton mFab;

    private Cursor cursorThreads = null;
    private Cursor cursorTolch = null;

    private ConversationListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ConversationDetailsDialog mConversationDetailsDialog;
    private SharedPreferences mPrefs;
    private MenuItem mBlockedItem;
    private boolean mShowBlocked = false;

    private boolean mViewHasLoaded = false;

    // This does not hold the current position of the list, rather the position the list is pending being set to
    private int mPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        setHasOptionsMenu(true);

        mAdapter = new ConversationListAdapter(mContext);
        mAdapter.setItemClickListener(this);
        mAdapter.setMultiSelectListener(this);
        mLayoutManager = new LinearLayoutManager(mContext);
        mConversationDetailsDialog = new ConversationDetailsDialog(mContext, getFragmentManager());

        LiveViewManager.registerView(QKPreference.THEME, this, key -> {
            if (!mViewHasLoaded) {
                return;
            }

            mFab.setColorNormal(ThemeManager.getColor());
            mFab.setColorPressed(ColorUtils.lighten(ThemeManager.getColor()));
            mFab.getDrawable().setColorFilter(ThemeManager.getTextOnColorPrimary(), PorterDuff.Mode.SRC_ATOP);

            mEmptyStateIcon.setColorFilter(ThemeManager.getTextOnBackgroundPrimary());
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations, null);
        ButterKnife.bind(this, view);

        mEmptyStateIcon.setColorFilter(ThemeManager.getTextOnBackgroundPrimary());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        SimpleDividerItemDecoration decoration = new SimpleDividerItemDecoration(getActivity(), VERTICAL_ITEM_SPACE);
        mRecyclerView.addItemDecoration(decoration);


        mFab.setColorNormal(ThemeManager.getColor());
        mFab.setColorPressed(ColorUtils.lighten(ThemeManager.getColor()));
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setColorFilter(ThemeManager.getTextOnColorPrimary());
        mFab.setOnClickListener(v -> {
            if (mAdapter.isInMultiSelectMode()) {
                mAdapter.disableMultiSelectMode(true);
            } else {
                mContext.startActivity(ComposeActivity.class);
            }
        });

        mViewHasLoaded = true;

        initLoaderManager();
        BlockedConversationHelper.FutureBlockedConversationObservable.getInstance().addObserver(this);

        return view;
    }

    /**
     * Returns the weighting for unread vs. read conversations that are selected, to decide
     * which options we should show in the multi selction toolbar
     */
    private int getUnreadWeight() {
        int unreadWeight = 0;
        for (Conversation conversation : mAdapter.getSelectedItems().values()) {
            unreadWeight += conversation.hasUnreadMessages() ? 1 : -1;
        }
        return unreadWeight;
    }

    /**
     * Returns the weighting for blocked vs. unblocked conversations that are selected
     */
    private int getBlockedWeight() {
        int blockedWeight = 0;
        for (Conversation conversation : mAdapter.getSelectedItems().values()) {
            blockedWeight += BlockedConversationHelper.isConversationBlocked(mPrefs, conversation.getThreadId()) ? 1 : -1;
        }
        return blockedWeight;
    }

    /**
     * Returns whether or not any of the selected conversations have errors
     */
    private boolean doSomeHaveErrors() {
        for (Conversation conversation : mAdapter.getSelectedItems().values()) {
            if (conversation.hasError()) {
                return true;
            }
        }
        return false;
    }

    public void inflateToolbar(Menu menu, MenuInflater inflater, Context context) {
        if (mAdapter.isInMultiSelectMode()) {
            inflater.inflate(R.menu.conversations_selection, menu);
            mContext.setTitle(getString(R.string.title_conversations_selected, mAdapter.getSelectedItems().size()));

            menu.findItem(R.id.menu_block).setVisible(mPrefs.getBoolean(SettingsFragment.BLOCKED_ENABLED, false));

            menu.findItem(R.id.menu_mark_read).setIcon(getUnreadWeight() >= 0 ? R.drawable.ic_mark_read : R.drawable.ic_mark_unread);
            menu.findItem(R.id.menu_mark_read).setTitle(getUnreadWeight() >= 0 ? R.string.menu_mark_read : R.string.menu_mark_unread);
            menu.findItem(R.id.menu_block).setTitle(getBlockedWeight() > 0 ? R.string.menu_unblock_conversations : R.string.menu_block_conversations);
            menu.findItem(R.id.menu_delete_failed).setVisible(doSomeHaveErrors());
        } else {
            inflater.inflate(R.menu.conversations, menu);
            mContext.setTitle(mShowBlocked ? R.string.title_blocked : R.string.title_conversation_list);

            mBlockedItem = menu.findItem(R.id.menu_blocked);
            BlockedConversationHelper.bindBlockedMenuItem(mContext, mPrefs, mBlockedItem, mShowBlocked);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_blocked:
                setShowingBlocked(!mShowBlocked);
                return true;

            case R.id.menu_delete:
                DialogHelper.showDeleteConversationsDialog((MainActivity) mContext, mAdapter.getSelectedItems().keySet());
                mAdapter.disableMultiSelectMode(true);
                return true;

            case R.id.menu_mark_read:
                for (long threadId : mAdapter.getSelectedItems().keySet()) {
                    if (getUnreadWeight() >= 0) {
                        new ConversationLegacy(mContext, threadId).markRead();
                    } else {
                        new ConversationLegacy(mContext, threadId).markUnread();
                    }
                }
                mAdapter.disableMultiSelectMode(true);
                return true;

            case R.id.menu_block:
                for (long threadId : mAdapter.getSelectedItems().keySet()) {
                    if (getBlockedWeight() > 0) {
                        BlockedConversationHelper.unblockConversation(mPrefs, threadId);
                    } else {
                        BlockedConversationHelper.blockConversation(mPrefs, threadId);
                    }
                }
                mAdapter.disableMultiSelectMode(true);
                initLoaderManager();
                return true;

            case R.id.menu_delete_failed:
                DialogHelper.showDeleteFailedMessagesDialog((MainActivity) mContext, mAdapter.getSelectedItems().keySet());
                mAdapter.disableMultiSelectMode(true);
                return true;

            case R.id.menu_done:
                mAdapter.disableMultiSelectMode(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isShowingBlocked() {
        return mShowBlocked;
    }

    public void setShowingBlocked(boolean showBlocked) {
        mShowBlocked = showBlocked;
        mContext.setTitle(mShowBlocked ? R.string.title_blocked : R.string.title_conversation_list);
        BlockedConversationHelper.bindBlockedMenuItem(mContext, mPrefs, mBlockedItem, mShowBlocked);
        initLoaderManager();
    }

    @Override
    public void onItemClick(Conversation conversation, View view) {
        if (mAdapter.isInMultiSelectMode()) {
            mAdapter.toggleSelection(conversation.getThreadId(), conversation);
        } else {
            MessageListActivity.launch(mContext, conversation.getThreadId(), -1, null, true);
        }
    }

    @Override
    public void onItemLongClick(final Conversation conversation, View view) {
        mAdapter.toggleSelection(conversation.getThreadId(), conversation);
    }

    public void setPosition(int position) {
        mPosition = position;
        if (mLayoutManager != null && mAdapter != null) {
            mLayoutManager.scrollToPosition(Math.min(mPosition, mAdapter.getCount() - 1));
        }
    }

    public int getPosition() {
        return mLayoutManager.findFirstVisibleItemPosition();
    }

    private void initLoaderManager() {
        getLoaderManager().restartLoader(QKSMSApp.LOADER_CONVERSATIONS, null, this);
        getLoaderManager().restartLoader(TolchContract.LOADER_CONVERSATIONS, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BlockedConversationHelper.FutureBlockedConversationObservable.getInstance().deleteObserver(this);

        if (null == mRecyclerView) {
            return;
        }
        try {
            for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                View child = mRecyclerView.getChildAt(i);
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(child);
                if (holder instanceof ConversationListViewHolder) {
                    Contact.removeListener((ConversationListViewHolder) holder);
                }
            }
        } catch (Exception ignored) {
            //
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private CursorLoader getThreadLoader() {
        return new CursorLoader(
                mContext,
                Telephony.Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build(),
                Conversation.ALL_THREADS_PROJECTION,
                BlockedConversationHelper.getCursorSelection(mPrefs, mShowBlocked),
                BlockedConversationHelper.getBlockedConversationArray(mPrefs),
                "date DESC"
        );
    }


    private CursorLoader getTolchLoader() {
        return new CursorLoader(mContext, TolchContract.TolchThreads.CONTENT_URI, TolchContract.TolchThreads.DEFAULT_PROJECTION, null, null, "date DESC");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QKSMSApp.LOADER_CONVERSATIONS) {
            return getThreadLoader();
        }

        if (id == TolchContract.LOADER_CONVERSATIONS) {
            return getTolchLoader();
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Cursor cursorJoined = null;
        if (loader.getId() == QKSMSApp.LOADER_CONVERSATIONS && mAdapter != null) {
            // Swap the new cursor in.  (The framework will take care of closing the, old cursor once we return.)
            cursorThreads = data;
            cursorJoined = joinCursors();
        }

        if (loader.getId() == TolchContract.LOADER_CONVERSATIONS && mAdapter != null) {
            // Swap the new cursor in.  (The framework will take care of closing the, old cursor once we return.)
            cursorTolch = data;
            cursorJoined = joinCursors();
        }

        if(cursorJoined != null) {
            mAdapter.changeCursor(cursorJoined);
            if (mPosition != 0) {
                mRecyclerView.scrollToPosition(Math.min(mPosition, data.getCount() - 1));
                mPosition = 0;
            }
            mEmptyState.setVisibility(data != null && data.getCount() > 0 ? View.GONE : View.VISIBLE);
        }

    }

    private Cursor joinCursors() {
        if (cursorThreads != null && cursorTolch != null) {
            CursorJoiner joiner =  new CursorJoiner(
                    cursorThreads,
                    new String[]{BaseColumns._ID},
                    cursorTolch,
                    new String[]{ TolchContract.TolchMessages.COLUMN_NAME_THREAD_ID });


            int joinedProjectionLength = Conversation.ALL_THREADS_PROJECTION.length + 1;
            String[] joinedProjection = new String[joinedProjectionLength];
            System.arraycopy(Conversation.ALL_THREADS_PROJECTION, 0, joinedProjection, 0, Conversation.ALL_THREADS_PROJECTION.length);
            joinedProjection[joinedProjection.length-1] = TolchContract.TolchThreads.COLUMN_NAME_AVG_FONE;


            MatrixCursor cursor = new MatrixCursor(joinedProjection);


            TolchThreadColumns.ColumnsMap columnsTolchMap = new TolchThreadColumns.ColumnsMap(cursorTolch);

            while (joiner.hasNext()) {
                CursorJoiner.Result result = joiner.next();
                if(result == CursorJoiner.Result.LEFT) {
                    MatrixCursor.RowBuilder row = cursor.newRow();
                    concateLeftRows(row);
                }
                if(result == CursorJoiner.Result.BOTH) {
                    MatrixCursor.RowBuilder row = cursor.newRow();
                    concateBothRows(row, columnsTolchMap);
                }
            }

            return cursor;

        }
        return null;
    }

    private void concateLeftRows(MatrixCursor.RowBuilder row) {
        row.add(cursorThreads.getLong(Conversation.ID));
        row.add(cursorThreads.getLong(Conversation.DATE));
        row.add(cursorThreads.getInt(Conversation.MESSAGE_COUNT));
        row.add(cursorThreads.getString(Conversation.RECIPIENT_IDS));
        row.add(cursorThreads.getString(Conversation.SNIPPET));
        row.add(cursorThreads.getInt(Conversation.SNIPPET_CS));
        row.add(cursorThreads.getInt(Conversation.READ));
        row.add(cursorThreads.getInt(Conversation.ERROR));
        row.add(cursorThreads.getInt(Conversation.HAS_ATTACHMENT));
    }

    private void concateBothRows(MatrixCursor.RowBuilder row, TolchThreadColumns.ColumnsMap columnsTolchMap) {

        concateLeftRows(row);

        // Tolch
        row.add(cursorTolch.getFloat(columnsTolchMap.mColumnFone));
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (mAdapter != null && loader.getId() == QKSMSApp.LOADER_CONVERSATIONS) {
            mAdapter.changeCursor(null);
        }
        if (mAdapter != null && loader.getId() == TolchContract.LOADER_CONVERSATIONS) {
            mAdapter.changeCursor(null);
        }
    }

    @Override
    public void onMultiSelectStateChanged(boolean enabled) {
        mContext.invalidateOptionsMenu();
        mFab.setImageResource(enabled ? R.drawable.ic_accept : R.drawable.ic_add);
    }

    @Override
    public void onItemAdded(long id) {
        mContext.invalidateOptionsMenu();
    }

    @Override
    public void onItemRemoved(long id) {
        mContext.invalidateOptionsMenu();
    }

    /**
     * This should be called when there's a future blocked conversation, and it's received
     */
    @Override
    public void update(Observable observable, Object data) {
        initLoaderManager();
    }
}
