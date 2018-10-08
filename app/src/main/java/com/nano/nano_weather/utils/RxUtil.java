package com.nano.nano_weather.utils;

import com.nano.nano_weather.Fragment.MultiCityFragment;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class RxUtil {
    //Rx封装方法
    public static <T> ObservableTransformer<T, T> fragmentLifecycle(RxFragment fragment) {
        return observable ->
                observable.compose(fragment.bindUntilEvent(FragmentEvent.DESTROY));
    }
    private static <T> ObservableTransformer<T, T> schedulerTransformer(Scheduler scheduler) {
        return observable ->
                observable
                        .subscribeOn(scheduler)
                        .observeOn(AndroidSchedulers.mainThread(), true);
    }

    public static <T> ObservableTransformer<T, T> io() {
        return schedulerTransformer(Schedulers.io());
    }
}
