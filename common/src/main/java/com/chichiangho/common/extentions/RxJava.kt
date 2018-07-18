package com.chichiangho.common.extentions

import android.arch.lifecycle.LifecycleOwner
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Emitter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Emitter<T>.onNextComplete(t: T) {
    onNext(t)
    onComplete()
}

fun <T> Observable<T>.io_main(): Observable<T> =
        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.autoDispose(owner: LifecycleOwner): ObservableSubscribeProxy<T> =
        `as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner)))

fun <T> Observable<T>.autoDispose_io_main(owner: LifecycleOwner): ObservableSubscribeProxy<T> =
        io_main().autoDispose(owner)