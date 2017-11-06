package com.dasend.state.tolch.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;


public class TolchProvider extends ContentProvider {

    private static HashMap<String, String> MESSAGES_PROJECTION_MAP = new HashMap<>();
    private static HashMap<String, String> THREADS_PROJECTION_MAP = new HashMap<>();

    private static final int MESSAGE_INDEX          = 1;
    private static final int MESSAGE_ID             = 2;
    private static final int MESSAGE_THREAD_ID      = 3;
    private static final int THREAD_INDEX           = 4;
    private static final int THREAD_ID              = 5;

    private static final UriMatcher sUriMatcher;
    private DBHelper dbHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TolchContract.AUTHORITY, TolchContract.TolchMessages.TABLE_NAME, MESSAGE_INDEX);
        sUriMatcher.addURI(TolchContract.AUTHORITY, TolchContract.TolchMessages.TABLE_NAME + "/#", MESSAGE_ID);
        sUriMatcher.addURI(TolchContract.AUTHORITY, TolchContract.TolchMessages.TABLE_NAME + TolchContract.TolchMessages.Thread.PATH_THREAD_ID + "#", MESSAGE_THREAD_ID);
        sUriMatcher.addURI(TolchContract.AUTHORITY, TolchContract.TolchThreads.TABLE_NAME, THREAD_INDEX);
        sUriMatcher.addURI(TolchContract.AUTHORITY, TolchContract.TolchThreads.TABLE_NAME + "/#", THREAD_ID);

        for (int i = 0; i < TolchContract.TolchMessages.DEFAULT_PROJECTION.length; i++) {
            MESSAGES_PROJECTION_MAP.put(TolchContract.TolchMessages.DEFAULT_PROJECTION[i], TolchContract.TolchMessages.DEFAULT_PROJECTION[i]);
        }

        for (int i = 0; i < TolchContract.TolchThreads.DEFAULT_PROJECTION.length; i++) {
            THREADS_PROJECTION_MAP.put(TolchContract.TolchThreads.DEFAULT_PROJECTION[i], TolchContract.TolchThreads.DEFAULT_PROJECTION[i]);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String finalWhere;
        int count;
        switch (sUriMatcher.match(uri)) {
            case MESSAGE_INDEX:
                count = db.delete(TolchContract.TolchMessages.TABLE_NAME, where, whereArgs);
                break;
            case MESSAGE_ID:
                finalWhere = TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID + " = " + uri.getPathSegments().get(TolchContract.TolchMessages.MESSAGES_ID_PATH_POSITION);
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.delete(TolchContract.TolchMessages.TABLE_NAME, finalWhere, whereArgs);
                break;
            case THREAD_INDEX:
                count = db.delete(TolchContract.TolchThreads.TABLE_NAME, where, whereArgs);
                break;
            case THREAD_ID:
                finalWhere = TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID + " = " + uri.getPathSegments().get(TolchContract.TolchThreads.THREADS_ID_PATH_POSITION);
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.delete(TolchContract.TolchThreads.TABLE_NAME, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MESSAGE_INDEX:
                return TolchContract.TolchMessages.CONTENT_TYPE;
            case MESSAGE_ID:
                return TolchContract.TolchMessages.CONTENT_ITEM_TYPE;
            case THREAD_INDEX:
                return TolchContract.TolchThreads.CONTENT_TYPE;
            case THREAD_ID:
                return TolchContract.TolchThreads.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues initialValues) {

        if (sUriMatcher.match(uri) != MESSAGE_INDEX && sUriMatcher.match(uri) != THREAD_INDEX ) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values;

        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        long rowId;
        Uri rowUri = Uri.EMPTY;

        switch (sUriMatcher.match(uri)) {
            case MESSAGE_INDEX:
                rowId = db.insert(TolchContract.TolchMessages.TABLE_NAME, TolchContract.TolchMessages.COLUMN_NAME_BAD, values);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(TolchContract.TolchMessages.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
            case THREAD_INDEX:
                rowId = db.insert(TolchContract.TolchThreads.TABLE_NAME, TolchContract.TolchThreads.COLUMN_NAME_AVG_FONE, values);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(TolchContract.TolchThreads.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
        }

        return rowUri;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy;
        switch (sUriMatcher.match(uri)) {
            case MESSAGE_INDEX:
                qb.setTables(TolchContract.TolchMessages.TABLE_NAME);
                qb.setProjectionMap(MESSAGES_PROJECTION_MAP);
                orderBy = TolchContract.TolchMessages.DEFAULT_SORT_ORDER;
                break;
            case MESSAGE_ID:
                qb.setTables(TolchContract.TolchMessages.TABLE_NAME);
                qb.setProjectionMap(MESSAGES_PROJECTION_MAP);
                qb.appendWhere(TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID + "=" + uri.getPathSegments().get(TolchContract.TolchMessages.MESSAGES_ID_PATH_POSITION));
                orderBy = TolchContract.TolchMessages.DEFAULT_SORT_ORDER;
                break;
            case MESSAGE_THREAD_ID:
                qb.setTables(TolchContract.TolchMessages.TABLE_NAME);
                qb.setProjectionMap(MESSAGES_PROJECTION_MAP);
                qb.appendWhere(TolchContract.TolchMessages.COLUMN_NAME_THREAD_ID + "=" + uri.getPathSegments().get(TolchContract.TolchMessages.Thread.MESSAGES_THREAD_ID_PATH_POSITION));
                orderBy = TolchContract.TolchMessages.DEFAULT_SORT_ORDER;
                break;
            case THREAD_INDEX:
                qb.setTables(TolchContract.TolchThreads.TABLE_NAME);
                qb.setProjectionMap(THREADS_PROJECTION_MAP);
                orderBy = TolchContract.TolchThreads.DEFAULT_SORT_ORDER;
                break;
            case THREAD_ID:
                qb.setTables(TolchContract.TolchThreads.TABLE_NAME);
                qb.setProjectionMap(THREADS_PROJECTION_MAP);
                qb.appendWhere(TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID + "=" + uri.getPathSegments().get(TolchContract.TolchThreads.THREADS_ID_PATH_POSITION));
                orderBy = TolchContract.TolchThreads.DEFAULT_SORT_ORDER;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(sortOrder != null) {
            orderBy = sortOrder;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        String finalWhere;
        String id;

        switch (sUriMatcher.match(uri)) {
            case MESSAGE_INDEX:
                count = db.update(TolchContract.TolchMessages.TABLE_NAME, values, where, whereArgs);
                break;
            case MESSAGE_ID:
                id = uri.getPathSegments().get(TolchContract.TolchMessages.MESSAGES_ID_PATH_POSITION);
                finalWhere = TolchContract.TolchMessages.COLUMN_NAME_MESSAGE_ID + " = " + id;
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.update(TolchContract.TolchMessages.TABLE_NAME, values, finalWhere, whereArgs);
                break;
            case THREAD_INDEX:
                count = db.update(TolchContract.TolchThreads.TABLE_NAME, values, where, whereArgs);
                break;
            case THREAD_ID:
                id = uri.getPathSegments().get(TolchContract.TolchThreads.THREADS_ID_PATH_POSITION);
                finalWhere = TolchContract.TolchThreads.COLUMN_NAME_THREAD_ID + " = " + id;
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.update(TolchContract.TolchThreads.TABLE_NAME, values, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


}
