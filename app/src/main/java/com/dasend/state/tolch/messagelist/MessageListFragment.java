package com.dasend.state.tolch.messagelist;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SqliteWrapper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.dasend.state.MainActivity;
import com.dasend.state.R;
import com.dasend.state.tolch.JoinedMessageItem;
import com.dasend.state.tolch.db.JoinedMessageColumns;
import com.dasend.state.tolch.db.TolchContract;
import com.dasend.state.tolch.db.TolchMessageColumns;
import com.google.android.mms.ContentType;
import com.moez.QKSMS.LogTag;
import com.moez.QKSMS.MmsConfig;
import com.moez.QKSMS.QKSMSApp;
import com.moez.QKSMS.common.CIELChEvaluator;
import com.moez.QKSMS.common.ConversationPrefsHelper;
import com.moez.QKSMS.common.DialogHelper;
import com.moez.QKSMS.common.LiveViewManager;
import com.moez.QKSMS.common.QKPreferences;
import com.moez.QKSMS.common.utils.KeyboardUtils;
import com.moez.QKSMS.common.utils.MessageUtils;
import com.moez.QKSMS.common.vcard.ContactOperations;
import com.moez.QKSMS.data.Contact;
import com.moez.QKSMS.data.ContactList;
import com.moez.QKSMS.data.Conversation;
import com.moez.QKSMS.data.ConversationLegacy;
import com.moez.QKSMS.data.Message;
import com.moez.QKSMS.enums.QKPreference;
import com.moez.QKSMS.interfaces.ActivityLauncher;
import com.moez.QKSMS.transaction.NotificationManager;
import com.moez.QKSMS.transaction.SmsHelper;
import com.moez.QKSMS.ui.SwipeBackLayout;
import com.moez.QKSMS.ui.ThemeManager;
import com.moez.QKSMS.ui.base.QKFragment;
import com.moez.QKSMS.ui.base.RecyclerCursorAdapter;
import com.moez.QKSMS.ui.delivery.DeliveryReportHelper;
import com.moez.QKSMS.ui.delivery.DeliveryReportItem;
import com.moez.QKSMS.ui.dialog.AsyncDialog;
import com.moez.QKSMS.ui.dialog.ConversationSettingsDialog;
import com.moez.QKSMS.ui.dialog.QKDialog;
import com.moez.QKSMS.ui.dialog.conversationdetails.ConversationDetailsDialog;
import com.moez.QKSMS.ui.messagelist.MessageColumns;
import com.moez.QKSMS.ui.messagelist.MessageListActivity;
import com.moez.QKSMS.ui.popup.QKComposeActivity;
import com.moez.QKSMS.ui.settings.SettingsFragment;
import com.moez.QKSMS.ui.view.ComposeView;
import com.moez.QKSMS.ui.view.MessageListRecyclerView;
import com.moez.QKSMS.ui.view.SmoothLinearLayoutManager;
import com.moez.QKSMS.ui.widget.WidgetProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ezvcard.Ezvcard;
import ezvcard.VCard;


public class MessageListFragment extends QKFragment implements ActivityLauncher, SensorEventListener,
        LoaderManager.LoaderCallbacks<Cursor>, RecyclerCursorAdapter.MultiSelectListener, SwipeBackLayout.ScrollChangedListener,
        RecyclerCursorAdapter.ItemClickListener<JoinedMessageItem> {


    private Cursor cursorMessages = null;
    private Cursor cursorTolch = null;

    public static final String TAG = "MessageListFragment";

    private static final int MESSAGE_LIST_QUERY_TOKEN = 9527;
    private static final int MESSAGE_LIST_QUERY_AFTER_DELETE_TOKEN = 9528;
    private static final int DELETE_MESSAGE_TOKEN = 9700;

    private static final int MENU_EDIT_MESSAGE = 14;
    private static final int MENU_VIEW_SLIDESHOW = 16;
    private static final int MENU_VIEW_MESSAGE_DETAILS = 17;
    private static final int MENU_DELETE_MESSAGE = 18;
    private static final int MENU_SEARCH = 19;
    private static final int MENU_DELIVERY_REPORT = 20;
    private static final int MENU_FORWARD_MESSAGE = 21;
    private static final int MENU_CALL_BACK = 22;
    private static final int MENU_SEND_EMAIL = 23;
    private static final int MENU_COPY_MESSAGE_TEXT = 24;
    private static final int MENU_COPY_TO_SDCARD = 25;
    private static final int MENU_ADD_ADDRESS_TO_CONTACTS = 27;
    private static final int MENU_LOCK_MESSAGE = 28;
    private static final int MENU_UNLOCK_MESSAGE = 29;
    private static final int MENU_SAVE_RINGTONE = 30;
    private static final int MENU_PREFERENCES = 31;
    private static final int MENU_GROUP_PARTICIPANTS = 32;

    private boolean mIsSmsEnabled;

    private Cursor mCursor;
    private CIELChEvaluator mCIELChEvaluator;
    private MessageListAdapter mAdapter;
    private SmoothLinearLayoutManager mLayoutManager;
    private MessageListRecyclerView mRecyclerView;
    private Conversation mConversation;
    private ConversationLegacy mConversationLegacy;

    private Sensor mProxSensor;
    private SensorManager mSensorManager;
    private AsyncDialog mAsyncDialog;
    private ComposeView mComposeView;
    private ConversationPrefsHelper mConversationPrefs;
    private ConversationDetailsDialog mConversationDetailsDialog;

    private int mSavedScrollPosition = -1;  // we save the ListView's scroll position in onPause(),
    // so we can remember it after re-entering the activity.
    // If the value >= 0, then we jump to that line. If the
    // value is maxint, then we jump to the end.

    private BackgroundQueryHandler mBackgroundQueryHandler;

    private long mThreadId;
    private long mRowId;
    private String mHighlight;
    private boolean mShowImmediate;

    public static MessageListFragment getInstance(long threadId, long rowId, String highlight, boolean showImmediate) {

        Bundle args = new Bundle();
        args.putLong(MessageListActivity.ARG_THREAD_ID, threadId);
        args.putLong(MessageListActivity.ARG_ROW_ID, rowId);
        args.putString(MessageListActivity.ARG_HIGHLIGHT, highlight);
        args.putBoolean(MessageListActivity.ARG_SHOW_IMMEDIATE, showImmediate);

        MessageListFragment fragment = new MessageListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public MessageListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle args = getArguments();
            mThreadId = args.getLong(MessageListActivity.ARG_THREAD_ID, -1);
            mRowId = args.getLong(MessageListActivity.ARG_ROW_ID, -1);
            mHighlight = args.getString(MessageListActivity.ARG_HIGHLIGHT, null);
            mShowImmediate = args.getBoolean(MessageListActivity.ARG_SHOW_IMMEDIATE, false);
        } else if (savedInstanceState != null) {
            mThreadId = savedInstanceState.getLong(MessageListActivity.ARG_THREAD_ID, -1);
            mRowId = savedInstanceState.getLong(MessageListActivity.ARG_ROW_ID, -1);
            mHighlight = savedInstanceState.getString(MessageListActivity.ARG_HIGHLIGHT, null);
            mShowImmediate = savedInstanceState.getBoolean(MessageListActivity.ARG_SHOW_IMMEDIATE, false);
        }

        mConversationPrefs = new ConversationPrefsHelper(mContext, mThreadId);
        mIsSmsEnabled = MmsConfig.isSmsEnabled(mContext);
        mConversationDetailsDialog = new ConversationDetailsDialog(mContext, getFragmentManager());
        onOpenConversation();
        setHasOptionsMenu(true);

        LiveViewManager.registerView(QKPreference.CONVERSATION_THEME, this, key -> {
            mCIELChEvaluator = new CIELChEvaluator(mConversationPrefs.getColor(), ThemeManager.getThemeColor());
        });


        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mProxSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (QKPreferences.getBoolean(QKPreference.PROXIMITY_SENSOR)) {
            mSensorManager.registerListener(this, mProxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        mBackgroundQueryHandler = new BackgroundQueryHandler(mContext.getContentResolver());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        mRecyclerView = (MessageListRecyclerView) view.findViewById(R.id.conversation);

        mAdapter = new MessageListAdapter(mContext);
        mAdapter.setItemClickListener(this);
        mAdapter.setMultiSelectListener(this);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            private long mLastMessageId = -1;
            @Override
            public void onChanged() {
                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int position;

                if (mRowId != -1 && mCursor != null) {
                    // Scroll to the position in the conversation for that message.
                    position = getPositionForMessageId(mCursor, "sms", mRowId, mAdapter.getColumnsMap());

                    // Be sure to reset the row ID here---we only want to scroll to the message
                    // the first time the cursor is loaded after the row ID is set.
                    mRowId = -1;

                } else {
                    position = mAdapter.getItemCount() - 1;
                }

                if(mAdapter.getCount() > 0) {
                    JoinedMessageItem lastMessage = mAdapter.getItem(mAdapter.getCount() - 1);
                    if (mLastMessageId >= 0 && mLastMessageId != lastMessage.getMessageId()) {
                        // Scroll to bottom only if a new message was inserted in this conversation
                        if (position != -1) {
                            manager.smoothScrollToPosition(mRecyclerView, null, position);
                        }
                    }
                    mLastMessageId = lastMessage.getMessageId();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new SmoothLinearLayoutManager(mContext);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mComposeView = (ComposeView) view.findViewById(R.id.compose_view);
        mComposeView.setActivityLauncher(this);
        mComposeView.setLabel("MessageList");

        mRecyclerView.setComposeView(mComposeView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (QKPreferences.getBoolean(QKPreference.PROXIMITY_SENSOR)) {
            mSensorManager.registerListener(this, mProxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        ThemeManager.setActiveColor(mConversationPrefs.getColor());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(MessageListActivity.ARG_THREAD_ID, mThreadId);
        outState.putLong(MessageListActivity.ARG_ROW_ID, mRowId);
        outState.putString(MessageListActivity.ARG_HIGHLIGHT, mHighlight);
        outState.putBoolean(MessageListActivity.ARG_SHOW_IMMEDIATE, mShowImmediate);
    }

    public long getThreadId() {
        return mThreadId;
    }

    /**
     * To be called when the user opens a conversation. Initializes the Conversation objects, sets
     * up the draft, and marks the conversation as read.
     * <p>
     * Note: This will have no effect if the context has not been initialized yet.
     */
    private void onOpenConversation() {
        new LoadConversationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setTitle() {
        if (mContext != null && mConversation != null) {
            mContext.setTitle(mConversation.getRecipients().formatNames(", "));
        }
    }

    @Override
    public void onItemClick(final JoinedMessageItem messageItem, View view) {
        if (mAdapter.isInMultiSelectMode()) {
            mAdapter.toggleSelection(messageItem.getMessageId(), messageItem);
        } else {
            if (view.getId() == R.id.image_view || view.getId() == R.id.play_slideshow_button) {
                switch (messageItem.mAttachmentType) {
                    case SmsHelper.IMAGE:
                    case SmsHelper.AUDIO:
                    case SmsHelper.SLIDESHOW:
                        MessageUtils.viewMmsMessageAttachment(getActivity(), messageItem.mMessageUri, messageItem.mSlideshow, getAsyncDialog());
                        break;
                    case SmsHelper.VIDEO:
                        new QKDialog()
                                .setContext(mContext)
                                .setTitle(R.string.warning)
                                .setMessage(R.string.stagefright_warning)
                                .setNegativeButton(R.string.cancel, null)
                                .setPositiveButton(R.string.yes, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MessageUtils.viewMmsMessageAttachment(getActivity(), messageItem.mMessageUri, messageItem.mSlideshow, getAsyncDialog());
                                    }
                                })
                                .show();
                        break;
                }
            } else if (messageItem != null && messageItem.isOutgoingMessage() && messageItem.isFailedMessage()) {
                showMessageResendOptions(messageItem);
            } else if (messageItem != null && ContentType.TEXT_VCARD.equals(messageItem.mTextContentType)) {
                openVcard(messageItem);
            } else {
                showMessageDetails(messageItem);
            }
        }
    }

    @Override
    public void onItemLongClick(JoinedMessageItem messageItem, View view) {

        QKDialog dialog = new QKDialog();
        dialog.setContext(mContext);
        dialog.setTitle(R.string.message_options);

        MsgListMenuClickListener l = new MsgListMenuClickListener(messageItem);

        // It is unclear what would make most sense for copying an MMS message
        // to the clipboard, so we currently do SMS only.
        if (messageItem.isSms()) {
            // Message type is sms. Only allow "edit" if the message has a single recipient
            if (getRecipients().size() == 1 && (messageItem.mBoxId == Telephony.Sms.MESSAGE_TYPE_OUTBOX || messageItem.mBoxId == Telephony.Sms.MESSAGE_TYPE_FAILED)) {
                dialog.addMenuItem(R.string.menu_edit, MENU_EDIT_MESSAGE);

            }

            dialog.addMenuItem(R.string.copy_message_text, MENU_COPY_MESSAGE_TEXT);
        }

        addCallAndContactMenuItems(dialog, messageItem);

        // Forward is not available for undownloaded messages.
        if (messageItem.isDownloaded() && (messageItem.isSms() || MessageUtils.isForwardable(mContext, messageItem.getMessageId())) && mIsSmsEnabled) {
            dialog.addMenuItem(R.string.menu_forward, MENU_FORWARD_MESSAGE);
        }

        if (messageItem.isMms()) {
            switch (messageItem.mBoxId) {
                case Telephony.Mms.MESSAGE_BOX_INBOX:
                    break;
                case Telephony.Mms.MESSAGE_BOX_OUTBOX:
                    // Since we currently break outgoing messages to multiple
                    // recipients into one message per recipient, only allow
                    // editing a message for single-recipient conversations.
                    if (getRecipients().size() == 1) {
                        dialog.addMenuItem(R.string.menu_edit, MENU_EDIT_MESSAGE);
                    }
                    break;
            }
            switch (messageItem.mAttachmentType) {
                case SmsHelper.TEXT:
                    break;
                case SmsHelper.VIDEO:
                case SmsHelper.IMAGE:
                    if (MessageUtils.haveSomethingToCopyToSDCard(mContext, messageItem.mMsgId)) {
                        dialog.addMenuItem(R.string.copy_to_sdcard, MENU_COPY_TO_SDCARD);
                    }
                    break;
                case SmsHelper.SLIDESHOW:
                default:
                    dialog.addMenuItem(R.string.view_slideshow, MENU_VIEW_SLIDESHOW);
                    if (MessageUtils.haveSomethingToCopyToSDCard(mContext, messageItem.mMsgId)) {
                        dialog.addMenuItem(R.string.copy_to_sdcard, MENU_COPY_TO_SDCARD);
                    }
                    if (MessageUtils.isDrmRingtoneWithRights(mContext, messageItem.mMsgId)) {
                        dialog.addMenuItem(MessageUtils.getDrmMimeMenuStringRsrc(mContext, messageItem.mMsgId), MENU_SAVE_RINGTONE);
                    }
                    break;
            }
        }

        if (messageItem.mLocked && mIsSmsEnabled) {
            dialog.addMenuItem(R.string.menu_unlock, MENU_UNLOCK_MESSAGE);
        } else if (mIsSmsEnabled) {
            dialog.addMenuItem(R.string.menu_lock, MENU_LOCK_MESSAGE);
        }

        dialog.addMenuItem(R.string.view_message_details, MENU_VIEW_MESSAGE_DETAILS);

        if (messageItem.mDeliveryStatus != JoinedMessageItem.DeliveryStatus.NONE || messageItem.mReadReport) {
            dialog.addMenuItem(R.string.view_delivery_report, MENU_DELIVERY_REPORT);
        }

        if (mIsSmsEnabled) {
            dialog.addMenuItem(R.string.delete_message, MENU_DELETE_MESSAGE);
        }

        dialog.buildMenu(l);
        dialog.show();
    }

    private void addCallAndContactMenuItems(QKDialog dialog, JoinedMessageItem msgItem) {
        if (TextUtils.isEmpty(msgItem.mBody)) {
            return;
        }
        SpannableString msg = new SpannableString(msgItem.mBody);
        Linkify.addLinks(msg, Linkify.ALL);
        ArrayList<String> uris = MessageUtils.extractUris(msg.getSpans(0, msg.length(), URLSpan.class));

        // Remove any dupes so they don't get added to the menu multiple times
        HashSet<String> collapsedUris = new HashSet<>();
        for (String uri : uris) {
            collapsedUris.add(uri.toLowerCase());
        }
        for (String uriString : collapsedUris) {
            String prefix = null;
            int sep = uriString.indexOf(":");
            if (sep >= 0) {
                prefix = uriString.substring(0, sep);
                uriString = uriString.substring(sep + 1);
            }
            Uri contactUri = null;
            boolean knownPrefix = true;
            if ("mailto".equalsIgnoreCase(prefix)) {
                contactUri = MessageUtils.getContactUriForEmail(mContext, uriString);
            } else if ("tel".equalsIgnoreCase(prefix)) {
                contactUri = MessageUtils.getContactUriForPhoneNumber(uriString);
            } else {
                knownPrefix = false;
            }
            if (knownPrefix && contactUri == null) {
                Intent intent = MainActivity.createAddContactIntent(uriString);

                String addContactString = getString(R.string.menu_add_address_to_contacts, uriString);
                dialog.addMenuItem(addContactString, MENU_ADD_ADDRESS_TO_CONTACTS);
            }
        }
    }

    private ContactList getRecipients() {
        return mConversation.getRecipients();
    }

    AsyncDialog getAsyncDialog() {
        if (mAsyncDialog == null) {
            mAsyncDialog = new AsyncDialog(getActivity());
        }
        return mAsyncDialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_call:
                makeCall();
                return true;

            case R.id.menu_notifications:
                boolean notificationMuted = mConversationPrefs.getNotificationsEnabled();
                mConversationPrefs.putBoolean(SettingsFragment.NOTIFICATIONS, !notificationMuted);
                mContext.invalidateOptionsMenu();
                vibrateOnConversationStateChanged(notificationMuted);
                return true;

            case R.id.menu_details:
                mConversationDetailsDialog.showDetails(mConversation);
                return true;

            case R.id.menu_notification_settings:
                ConversationSettingsDialog.newInstance(mThreadId, mConversation.getRecipients().formatNames(", "))
                        .setContext(mContext)
                        .show();
                return true;

            case R.id.menu_delete_conversation:
                DialogHelper.showDeleteConversationDialog(mContext, mThreadId);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeCall() {
        Intent openDialerIntent = new Intent(Intent.ACTION_CALL);
        openDialerIntent.setData(Uri.parse("tel:" + mConversationLegacy.getAddress()));
        startActivity(openDialerIntent);
    }

    private void vibrateOnConversationStateChanged(final boolean notificationMuted) {
        final int vibrateTime = 70;
        Toast.makeText(getActivity(), notificationMuted ?
                R.string.notification_mute_off : R.string.notification_mute_on, Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(vibrateTime);
    }

    /**
     * Photo Selection result
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (!mComposeView.onActivityResult(requestCode, resultCode, data)) {
            // Wasn't handled by ComposeView
        }
    }

    /**
     * Should only be called for failed messages. Deletes the message, placing the text from the
     * message back in the edit box to be updated and then sent.
     * <p>
     * Assumes that cursor points to the correct JoinedMessageItem.
     *
     * @param msgItem
     */
    private void editMessageItem(JoinedMessageItem msgItem) {
        String body = msgItem.mBody;

        // Delete the message and put the text back into the edit text.
        deleteMessageItem(msgItem);

        // Set the text and open the keyboard
        KeyboardUtils.show(mContext);

        mComposeView.setText(body);
    }

    /**
     * Should only be called for failed messages. Deletes the message and resends it.
     *
     * @param msgItem
     */
    public void resendMessageItem(final JoinedMessageItem msgItem) {
        String body = msgItem.mBody;
        deleteMessageItem(msgItem);

        mComposeView.setText(body);
        mComposeView.sendSms();
    }

    /**
     * Deletes the message from the conversation list and the conversation history.
     *
     * @param msgItem
     */
    public void deleteMessageItem(final JoinedMessageItem msgItem) {
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... none) {
                if (msgItem.isMms()) {
                    MessageUtils.removeThumbnailsFromCache(msgItem.getSlideshow());

                    QKSMSApp.getApplication().getPduLoaderManager().removePdu(msgItem.mMessageUri);
                    // Delete the message *after* we've removed the thumbnails because we
                    // need the pdu and slideshow for removeThumbnailsFromCache to work.
                }

                // Determine if we're deleting the last item in the cursor.
                Boolean deletingLastItem = false;
                if (mAdapter != null && mAdapter.getCursor() != null) {
                    mCursor = mAdapter.getCursor();
                    mCursor.moveToLast();
                    long msgId = mCursor.getLong(JoinedMessageColumns.COLUMN_ID);
                    deletingLastItem = msgId == msgItem.mMsgId;
                }

                mBackgroundQueryHandler.startDelete(DELETE_MESSAGE_TOKEN, deletingLastItem,
                        msgItem.mMessageUri, msgItem.mLocked ? null : "locked=0", null);
                return null;
            }
        }.execute();
    }

    private void initLoaderManager() {
        getLoaderManager().initLoader(QKSMSApp.LOADER_MESSAGES, null, this);
        getLoaderManager().initLoader(TolchContract.LOADER_MESSAGES, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mComposeView.saveDraft();

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }

        if (mConversationLegacy != null) {
            mConversationLegacy.markRead();
        }

        if (mConversation != null) {
            mConversation.blockMarkAsRead(true);
            mConversation.markAsRead();
            mComposeView.saveDraft();
        }

        ThemeManager.setActiveColor(ThemeManager.getThemeColor());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.menu_notifications).setTitle(mConversationPrefs.getNotificationsEnabled() ?
                R.string.menu_notifications : R.string.menu_notifications_off);
        menu.findItem(R.id.menu_notifications).setIcon(mConversationPrefs.getNotificationsEnabled() ?
                R.drawable.ic_notifications : R.drawable.ic_notifications_muted);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0 && isAdded()) {
            makeCall();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ignored
    }


    private CursorLoader getMessagesLoader() {
        return new CursorLoader(
                mContext,
                Uri.withAppendedPath(Message.MMS_SMS_CONTENT_PROVIDER, String.valueOf(mThreadId)),
                MessageColumns.PROJECTION,
                null,
                null,
                "_id ASC"
        );
    }


    private CursorLoader getTolchLoader() {
        return new CursorLoader(
                mContext,
                Uri.withAppendedPath(
                    TolchContract.TolchMessages.Thread.CONTENT_ID_URI_BASE,
                    String.valueOf(mThreadId)
                ),
                TolchContract.TolchMessages.DEFAULT_PROJECTION,
                null,
                null,
                "message_id ASC"
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QKSMSApp.LOADER_MESSAGES) {
            return getMessagesLoader();
        }

        if (id == TolchContract.LOADER_MESSAGES) {
            return getTolchLoader();
        }


        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Cursor cursorJoined = null;
        if (loader.getId() == QKSMSApp.LOADER_MESSAGES && mAdapter != null) {
            // Swap the new cursor in.  (The framework will take care of closing the, old cursor once we return.)
            cursorMessages = data;
            cursorJoined = joinCursors();
        }

        if (loader.getId() == TolchContract.LOADER_MESSAGES && mAdapter != null) {
            // Swap the new cursor in.  (The framework will take care of closing the, old cursor once we return.)
            cursorTolch = data;
            cursorJoined = joinCursors();
        }

        if(cursorJoined != null) {
            mAdapter.changeCursor(cursorJoined);
        }


    }

    private Cursor joinCursors() {
        if (cursorMessages != null && cursorTolch != null) {
            CursorJoiner joiner =  new CursorJoiner(
                    cursorMessages,
                    new String[]{BaseColumns._ID},
                    cursorTolch,
                    new String[]{ TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID });

            MatrixCursor cursor = new MatrixCursor(JoinedMessageColumns.PROJECTION);

            JoinedMessageColumns.ColumnsMap columnsMessagesMap = new JoinedMessageColumns.ColumnsMap(cursorMessages);
            TolchMessageColumns.ColumnsMap columnsTolchMap = new TolchMessageColumns.ColumnsMap(cursorTolch);

            while (joiner.hasNext()) {
                CursorJoiner.Result result = joiner.next();
                if(result == CursorJoiner.Result.LEFT) {
                    MatrixCursor.RowBuilder row = cursor.newRow();
                    concateLeftRows(row, columnsMessagesMap);
                }
                if(result == CursorJoiner.Result.BOTH) {
                    MatrixCursor.RowBuilder row = cursor.newRow();
                    concateBothRows(row, columnsMessagesMap, columnsTolchMap);
                }
            }

            return cursor;

        }
        return null;
    }

    private void concateLeftRows(MatrixCursor.RowBuilder row, JoinedMessageColumns.ColumnsMap columnsMessagesMap) {
        row.add(cursorMessages.getString(columnsMessagesMap.mColumnMsgType));
        row.add(cursorMessages.getLong(columnsMessagesMap.mColumnMsgId));
        row.add(cursorMessages.getLong(columnsMessagesMap.mColumnThreadId));
        row.add(cursorMessages.getString(columnsMessagesMap.mColumnSmsAddress));
        row.add(cursorMessages.getString(columnsMessagesMap.mColumnSmsBody));
        row.add(cursorMessages.getLong(columnsMessagesMap.mColumnSmsDate));
        row.add(cursorMessages.getLong(columnsMessagesMap.mColumnSmsDateSent));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnSmsType));
        row.add(cursorMessages.getLong(columnsMessagesMap.mColumnSmsStatus));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnSmsLocked));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnSmsErrorCode));
        row.add(cursorMessages.getString(columnsMessagesMap.mColumnMmsSubject));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnMmsSubjectCharset));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnMmsMessageType));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnMmsMessageBox));
        row.add(cursorMessages.getString(columnsMessagesMap.mColumnMmsDeliveryReport));
        row.add(cursorMessages.getString(columnsMessagesMap.mColumnMmsReadReport));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnMmsErrorType));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnMmsLocked));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnMmsStatus));
        row.add(cursorMessages.getInt(columnsMessagesMap.mColumnMmsTextOnly));
    }

    private void concateBothRows(MatrixCursor.RowBuilder row, JoinedMessageColumns.ColumnsMap columnsMessagesMap, TolchMessageColumns.ColumnsMap columnsTolchMap) {

        concateLeftRows(row, columnsMessagesMap);

        // Tolch
        row.add(cursorTolch.getFloat(columnsTolchMap.mColumnGood));
        row.add(cursorTolch.getFloat(columnsTolchMap.mColumnBad));
        row.add(cursorTolch.getFloat(columnsTolchMap.mColumnNeutral));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null && loader.getId() == QKSMSApp.LOADER_MESSAGES) {
            mAdapter.changeCursor(null);
        }
        if (mAdapter != null && loader.getId() == TolchContract.LOADER_MESSAGES) {
            mAdapter.changeCursor(null);
        }
    }

    @Override
    public void onMultiSelectStateChanged(boolean enabled) {

    }

    @Override
    public void onItemAdded(long id) {

    }

    @Override
    public void onItemRemoved(long id) {

    }

    @Override
    public void onScrollChanged(float scrollPercent) {
        if (mConversationPrefs != null) {
            ThemeManager.setActiveColor(mCIELChEvaluator.evaluate(scrollPercent));
        }
    }

    private class DeleteMessageListener implements DialogInterface.OnClickListener {
        private final JoinedMessageItem mMessageItem;

        public DeleteMessageListener(JoinedMessageItem messageItem) {
            mMessageItem = messageItem;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
            deleteMessageItem(mMessageItem);
        }
    }

    /**
     * Context menu handlers for the message list view.
     */
    private final class MsgListMenuClickListener implements AdapterView.OnItemClickListener {
        private JoinedMessageItem mMsgItem;

        public MsgListMenuClickListener(JoinedMessageItem msgItem) {
            mMsgItem = msgItem;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mMsgItem == null) {
                return;
            }

            switch ((int) id) {
                case MENU_EDIT_MESSAGE:
                    editMessageItem(mMsgItem);
                    break;

                case MENU_COPY_MESSAGE_TEXT:
                    MessageUtils.copyToClipboard(mContext, mMsgItem.mBody);
                    break;

                case MENU_FORWARD_MESSAGE:
                    forwardMessage(mContext, mMsgItem.mBody);
                    break;

                case MENU_VIEW_SLIDESHOW:
                    MessageUtils.viewMmsMessageAttachment(getActivity(), ContentUris.withAppendedId(Telephony.Mms.CONTENT_URI, mMsgItem.mMsgId), null, getAsyncDialog());
                    break;

                case MENU_VIEW_MESSAGE_DETAILS:
                    showMessageDetails(mMsgItem);
                    break;

                case MENU_DELETE_MESSAGE:
                    DeleteMessageListener l = new DeleteMessageListener(mMsgItem);
                    confirmDeleteDialog(l, mMsgItem.mLocked);
                    break;

                case MENU_DELIVERY_REPORT:
                    showDeliveryReport(mMsgItem.mMsgId, mMsgItem.mType);
                    break;

                case MENU_COPY_TO_SDCARD: {
                    int resId = MessageUtils.copyMedia(mContext, mMsgItem.mMsgId) ? R.string.copy_to_sdcard_success : R.string.copy_to_sdcard_fail;
                    Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
                    break;
                }

                case MENU_SAVE_RINGTONE: {
                    int resId = MessageUtils.getDrmMimeSavedStringRsrc(mContext, mMsgItem.mMsgId, MessageUtils.saveRingtone(mContext, mMsgItem.mMsgId));
                    Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
                    break;
                }

                case MENU_ADD_ADDRESS_TO_CONTACTS:
                    addToContacts(mContext, mMsgItem.mAddress);
                    break;

                case MENU_LOCK_MESSAGE:
                    lockMessage(mContext, mMsgItem.mType, mMsgItem.mMsgId, true);
                    break;

                case MENU_UNLOCK_MESSAGE:
                    lockMessage(mContext, mMsgItem.mType, mMsgItem.mMsgId, false);
                    break;
            }
        }
    }

    private void forwardMessage(Context context, String body) {
        Intent forwardIntent = new Intent(context, QKComposeActivity.class);
        forwardIntent.putExtra("sms_body", body);
        context.startActivity(forwardIntent);
    }

    private void addToContacts(Context context, String ddress) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, ddress);
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void lockMessage(Context context, String type, long msgId, boolean locked) {
        Uri uri;
        if ("sms".equals(type)) {
            uri = Telephony.Sms.CONTENT_URI;
        } else {
            uri = Telephony.Mms.CONTENT_URI;
        }
        final Uri lockUri = ContentUris.withAppendedId(uri, msgId);

        final ContentValues values = new ContentValues(1);
        values.put("locked", locked ? 1 : 0);

        new Thread(() -> {
            context.getContentResolver().update(lockUri,
                    values, null, null);
        }, "MainActivity.lockMessage").start();
    }

    private int getPositionForMessageId(Cursor cursor, String messageType, long messageId, JoinedMessageColumns.ColumnsMap map) {

        // Modified binary search on the cursor to find the position of the message in the cursor.
        // It's modified because, although the SMS and MMS are generally ordered in terms of their
        // ID, they have different IDs. So, we might have a list of IDs like:
        //
        // [ 4444, 4447, 4449, 4448, 312, 315, 4451 ]
        //
        // where the 44xx IDs are for SMS messages, and the 31x IDs are for MMS messages. The
        // solution is to do a linear scan if we reach a point in the list where the ID doesn't
        // match what we're looking for.

        // Lower and upper bounds for doing the search
        int min = 0;
        int max = cursor.getCount() - 1;

        while (min <= max) {
            int mid = min / 2 + max / 2 + (min & max & 1);

            cursor.moveToPosition(mid);
            long candidateId = cursor.getLong(map.mColumnMsgId);
            String candidateType = cursor.getString(map.mColumnMsgType);

            if (messageType.equals(candidateType)) {
                if (messageId < candidateId) {
                    max = mid - 1;
                } else if (messageId > candidateId) {
                    min = mid + 1;
                } else {
                    return mid;
                }

            } else {
                // This message is the wrong type, so we have to do a linear search until we find a
                // message that is the right type so we can orient ourselves.

                // First, look forward. Stop when we move past max, or reach the end of the cursor.
                boolean success = false;
                while (cursor.getPosition() <= max && cursor.moveToNext()) {
                    candidateType = cursor.getString(map.mColumnMsgType);
                    if (candidateType.equals(messageType)) {
                        success = true;
                        break;
                    }
                }

                if (!success) {
                    // We didn't find any messages of the right type by looking forward, so try
                    // looking backwards.
                    cursor.moveToPosition(mid);
                    while (cursor.getPosition() >= min && cursor.moveToPrevious()) {
                        candidateType = cursor.getString(map.mColumnMsgType);
                        if (candidateType.equals(messageType)) {
                            success = true;
                            break;
                        }
                    }
                }

                if (!success) {
                    // There is no message with that ID of the correct type!
                    return -1;
                }

                // In this case, we've found a message of the correct type! Now to do the binary
                // search stuff.
                candidateId = cursor.getLong(map.mColumnMsgId);
                int pos = cursor.getPosition();
                if (messageId < candidateId) {
                    // The new upper bound is the minimum of where we started and where we ended
                    // up, subtract one.
                    max = (mid < pos ? mid : pos) - 1;
                } else if (messageId > candidateId) {
                    // Same as above but in reverse.
                    min = (mid > pos ? mid : pos) + 1;
                } else {
                    return pos;
                }
            }
        }

        // This is the case where we've minimized our bounds until they're the same, and we haven't
        // found anything yet---this means that the item doesn't exist, so return -1.
        return -1;
    }

    private boolean showMessageResendOptions(final JoinedMessageItem msgItem) {
        final Cursor cursor = mAdapter.getCursorForItem(msgItem);
        if (cursor == null) {
            return false;
        }

        KeyboardUtils.hide(mContext, mComposeView);

        new QKDialog()
                .setContext(mContext)
                .setTitle(R.string.failed_message_title)
                .setItems(R.array.resend_menu, (parent, view, position, id) -> {
                    switch (position) {
                        case 0: // Resend message
                            resendMessageItem(msgItem);

                            break;
                        case 1: // Edit message
                            editMessageItem(msgItem);

                            break;
                        case 2: // Delete message
                            confirmDeleteDialog(new DeleteMessageListener(msgItem), false);
                            break;
                    }
                }).show();
        return true;
    }

    private void openVcard(JoinedMessageItem messageItem) {
        Log.d(TAG, "Vcard: " + messageItem.mBody);

        VCard vCard = Ezvcard.parse(messageItem.mBody).first();

        ContactOperations operations = new ContactOperations(mContext);
        try {
            operations.insertContact(vCard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean showMessageDetails(JoinedMessageItem msgItem) {
        Cursor cursor = mAdapter.getCursorForItem(msgItem);
        if (cursor == null) {
            return false;
        }
        String messageDetails = MessageUtils.getMessageDetails(mContext, cursor, msgItem.mMessageSize);
        new QKDialog()
                .setContext(mContext)
                .setTitle(R.string.message_details_title)
                .setMessage(messageDetails)
                .setCancelOnTouchOutside(true)
                .show();
        return true;
    }

    private void confirmDeleteDialog(DialogInterface.OnClickListener listener, boolean locked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setMessage(locked ? R.string.confirm_delete_locked_message : R.string.confirm_delete_message);
        builder.setPositiveButton(R.string.delete, listener);
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void showDeliveryReport(long messageId, String type) {
        DeliveryReportHelper deliveryReportHelper = new DeliveryReportHelper(mContext, messageId, type);
        List<DeliveryReportItem> deliveryReportItems = deliveryReportHelper.getListItems();

        String[] items = new String[deliveryReportItems.size() * 3];
        for (int i = 0; i < deliveryReportItems.size() * 3; i++) {
            switch (i % 3) {
                case 0:
                    items[i] = deliveryReportItems.get(i - (i / 3)).recipient;
                    break;
                case 1:
                    items[i] = deliveryReportItems.get(i - 1 - ((i - 1) / 3)).status;
                    break;
                case 2:
                    items[i] = deliveryReportItems.get(i - 2 - ((i - 2) / 3)).deliveryDate;
                    break;
            }
        }

        new QKDialog()
                .setContext(mContext)
                .setTitle(R.string.delivery_header_title)
                .setItems(items, null)
                .setPositiveButton(R.string.okay, null)
                .show();
    }

    private void startMsgListQuery(int token) {
        /*if (mSendDiscreetMode) {
            return;
        }*/
        Uri conversationUri = mConversation.getUri();

        if (conversationUri == null) {
            Log.v(TAG, "##### startMsgListQuery: conversationUri is null, bail!");
            return;
        }

        long threadId = mConversation.getThreadId();
        if (LogTag.VERBOSE || Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
            Log.v(TAG, "startMsgListQuery for " + conversationUri + ", threadId=" + threadId +
                    " token: " + token + " mConversation: " + mConversation);
        }

        // Cancel any pending queries
        mBackgroundQueryHandler.cancelOperation(token);
        try {
            // Kick off the new query
            mBackgroundQueryHandler.startQuery(
                    token,
                    threadId /* cookie */,
                    conversationUri,
                    JoinedMessageColumns.PROJECTION,
                    null, null, null);
        } catch (SQLiteException e) {
            SqliteWrapper.checkSQLiteException(mContext, e);
        }
    }

    public final class BackgroundQueryHandler extends Conversation.ConversationQueryHandler {
        public BackgroundQueryHandler(ContentResolver contentResolver) {
            super(contentResolver, mContext);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case MainActivity.HAVE_LOCKED_MESSAGES_TOKEN:
                    if (mContext.isFinishing()) {
                        Log.w(TAG, "ComposeMessageActivity is finished, do nothing ");
                        if (cursor != null) {
                            cursor.close();
                        }
                        return;
                    }
                    @SuppressWarnings("unchecked")
                    ArrayList<Long> threadIds = (ArrayList<Long>) cookie;
                    MainActivity.confirmDeleteThreadDialog(
                            new MainActivity.DeleteThreadListener(threadIds, mBackgroundQueryHandler, mContext), threadIds,
                            cursor != null && cursor.getCount() > 0, mContext);
                    if (cursor != null) {
                        cursor.close();
                    }
                    break;

                case MESSAGE_LIST_QUERY_AFTER_DELETE_TOKEN:
                    // check consistency between the query result and 'mConversation'
                    long tid = (Long) cookie;

                    if (LogTag.VERBOSE || Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
                        Log.v(TAG, "##### onQueryComplete (after delete): msg history result for threadId " + tid);
                    }
                    if (cursor == null) {
                        return;
                    }
                    if (tid > 0 && cursor.getCount() == 0) {
                        // We just deleted the last message and the thread will get deleted
                        // by a trigger in the database. Clear the threadId so next time we
                        // need the threadId a new thread will get created.
                        Log.v(TAG, "##### MESSAGE_LIST_QUERY_AFTER_DELETE_TOKEN clearing thread id: " + tid);
                        Conversation conv = Conversation.get(mContext, tid, false);
                        if (conv != null) {
                            conv.clearThreadId();
                            conv.setDraftState(false);
                        }
                        mContext.onBackPressed();
                    }
                    cursor.close();
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            switch (token) {
                case MainActivity.DELETE_CONVERSATION_TOKEN:
                    mConversation.setMessageCount(0);
                    // fall through
                case DELETE_MESSAGE_TOKEN:

                    // Update the notification for new messages since they may be deleted.
                    NotificationManager.update(mContext);

                    // TODO Update the notification for failed messages since they may be deleted.
                    //updateSendFailedNotification();
                    break;
            }
            // If we're deleting the whole conversation, throw away our current working message and bail.
            if (token == MainActivity.DELETE_CONVERSATION_TOKEN) {
                ContactList recipients = mConversation.getRecipients();

                // Remove any recipients referenced by this single thread from the It's possible for two or more
                // threads to reference the same contact. That's ok if we remove it. We'll recreate that contact
                // when we init all Conversations below.
                if (recipients != null) {
                    for (Contact contact : recipients) {
                        contact.removeFromCache();
                    }
                }

                // Make sure the conversation cache reflects the threads in the DB.
                Conversation.init(mContext);

                // Go back to the conversation list
                mContext.onBackPressed();
            } else if (token == DELETE_MESSAGE_TOKEN) {
                // Check to see if we just deleted the last message
                startMsgListQuery(MESSAGE_LIST_QUERY_AFTER_DELETE_TOKEN);
            }

            WidgetProvider.notifyDatasetChanged(mContext);
        }
    }

    private class LoadConversationTask extends AsyncTask<Void, Void, Void> {

        public LoadConversationTask() {
            Log.d(TAG, "LoadConversationTask");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "Loading conversation");
            mConversation = Conversation.get(mContext, mThreadId, true);
            mConversationLegacy = new ConversationLegacy(mContext, mThreadId);

            mConversationLegacy.markRead();
            mConversation.blockMarkAsRead(true);
            mConversation.markAsRead();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "Conversation loaded");

            mComposeView.onOpenConversation(mConversation, mConversationLegacy);
            setTitle();

            mAdapter.setIsGroupConversation(mConversation.getRecipients().size() > 1);

            if (isAdded()) {
                initLoaderManager();
            }
        }
    }
}
