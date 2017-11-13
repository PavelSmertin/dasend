package com.dasend.state;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dasend.state.tolch.TolchAnalizer;
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
                .map(succ -> mAnalizer.syncInitial())
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
}
