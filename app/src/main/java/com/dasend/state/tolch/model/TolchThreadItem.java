/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dasend.state.tolch.model;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.dasend.state.tolch.db.TolchThreadColumns;

public class TolchThreadItem {
    private static String TAG = "TolchThreadItem";

    private int mThreadId;
    private float mFone;




    @SuppressLint("NewApi")
    public TolchThreadItem(final Cursor cursor, final TolchThreadColumns.ColumnsMap columnsMap) {
        mThreadId   = cursor.getInt(columnsMap.mColumnThreadId);
        mFone       = cursor.getFloat(columnsMap.mColumnFone);

    }

    @Override
    public String toString() {
        return
                " threadId: " + mThreadId +
                " fone: " + mFone
                ;
    }


    public long getThreadId() {
        return mThreadId;
    }
}
