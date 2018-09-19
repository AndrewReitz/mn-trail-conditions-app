@file:Suppress("unused")

package cash.andrew.mntrailconditions.util

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T : Any> Single<T>.subscribeOnIO(): Single<T> = subscribeOn(Schedulers.io())

fun <T : Any> Observable<T>.observeOnMainThread(): Observable<T> = observeOn(AndroidSchedulers.mainThread())
fun <T : Any> Single<T>.observeOnMainThread(): Single<T> = observeOn(AndroidSchedulers.mainThread())
fun <T : Any> Maybe<T>.observeOnMainThread(): Maybe<T> = observeOn(AndroidSchedulers.mainThread())

/**
 * Sets a time out on the [Single] and retries the single [times] specified.
 * This is useful when you know that a [Single] should not take very long, and if it does something
 * is wrong (such as a slow network connection). This kills the request and retries rather than
 * letting it complete (most likely in error). This helps keep UIs performant.
 */
fun <T : Any> Single<T>.retryWithTimeout(
        times: Long = 3,
        timeout: Long = 1,
        timeUnit: TimeUnit = TimeUnit.SECONDS
): Single<T> = timeout(timeout, timeUnit)
        .retry { count, throwable -> throwable is TimeoutException && count < times }

/**
 * Sets a time out on the [Observable] and retries the [times] specified.
 * This is useful when you know that a [Observable] should not take very long, and if it does something
 * is wrong (such as a slow network connection). This kills the request and retries rather than
 * letting it complete (most likely in error). This helps keep UIs performant.
 */
fun <T : Any> Observable<T>.retryWithTimeout(
        times: Long = 3,
        timeout: Long = 1,
        timeUnit: TimeUnit = TimeUnit.SECONDS
): Observable<T> = timeout(timeout, timeUnit)
        .retry(times, { error -> error is TimeoutException })

/**
 * Sets a time out on the [Maybe] and retries the single [times] specified.
 * This is useful when you know that a [Maybe] should not take very long, and if it does something
 * is wrong (such as a slow network connection). This kills the request and retries rather than
 * letting it complete (most likely in error). This helps keep UIs performant.
 */
fun <T : Any> Maybe<T>.retryWithTimeout(
        times: Long = 3,
        timeout: Long = 1,
        timeUnit: TimeUnit = TimeUnit.SECONDS
): Maybe<T> = timeout(timeout, timeUnit)
        .retry { count, throwable -> throwable is TimeoutException && count < times }
