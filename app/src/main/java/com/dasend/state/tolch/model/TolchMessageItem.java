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

import com.dasend.state.tolch.db.TolchMessageColumns;


public class TolchMessageItem {
    private static String TAG = "TolchMessageItem";


    public int mMessageId;
    public int mThreadId;
    public float mGood;
    public float mBad;
    public float mNeutral;

    public Tolch mTolch;



    @SuppressLint("NewApi")
    public TolchMessageItem(final Cursor cursor, final TolchMessageColumns.ColumnsMap columnsMap) {

        mMessageId = cursor.getInt(columnsMap.mColumnMsgId);
        mThreadId = cursor.getInt(columnsMap.mColumnThreadId);
        mGood = cursor.getFloat(columnsMap.mColumnGood);
        mBad = cursor.getFloat(columnsMap.mColumnBad);
        mNeutral = cursor.getFloat(columnsMap.mColumnNeutral);

    }

    @Override
    public String toString() {
        return "messageId: " + mMessageId +
                " threadId: " + mThreadId +
                " good: " + mGood +
                " bad: " + mBad +
                " neutral: " + mNeutral
                ;
    }



}
