package com.dasend.state;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dasend.state.tolch.TolchAnalizer;
import com.dasend.state.tolch.db.TolchContract;
import com.dasend.state.tolch.views.SemiCircleProgressBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AnalizeActivity extends AppCompatActivity{

    public static final String TAG = "AnalizeActivity";

    private TolchAnalizer mAnalizer;

    @BindView(R.id.progress_analize) SemiCircleProgressBarView mProgressAnalize;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analize);
        setTitle(R.string.title_conversation_list);

        ButterKnife.bind(this);

        mAnalizer = new TolchAnalizer(this);

        mAnalizer
                .getSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(this::setAnalize);

        Observable
                .just(true)
                .subscribeOn(Schedulers.computation())
                .map(succ -> mAnalizer.analize())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    setAnalize(100);
                    finish();
                });

        mProgressAnalize.setClipping(0);

    }





    private void setAnalize(int progress) {
        mProgressAnalize.setClipping(progress);
    }


    private boolean isAnalized() {
        Cursor cursorMessages = null;
        Cursor cursorTolch = null;
        try{
//            cursorMessages = getContentResolver().query(Telephony.Sms.CONTENT_URI, MessageColumns.PROJECTION, null, null, null);
            cursorTolch = getContentResolver().query(TolchContract.TolchMessages.CONTENT_URI, TolchContract.TolchMessages.DEFAULT_PROJECTION, null, null, null);

//            if(cursorMessages == null) {
//                return true;
//            }

            if(cursorTolch != null && cursorTolch.getCount() > 0 ) {
                return true;
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursorMessages != null) {
                cursorMessages.close();
            }
            if (cursorTolch != null) {
                cursorTolch.close();
            }
        }
        return false;
    }



}
