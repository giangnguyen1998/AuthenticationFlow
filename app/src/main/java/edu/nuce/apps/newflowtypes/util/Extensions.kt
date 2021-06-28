package edu.nuce.apps.newflowtypes.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.channels.SendChannel

fun <A, B, Result> LiveData<A>.combine(
    other: LiveData<B>,
    transform: (A, B) -> Result
): LiveData<Result> {
    val result = MediatorLiveData<Result>()
    result.addSource(other) { b ->
        val a = value
        if (a != null) {
            result.postValue(transform(a, b))
        }
    }
    result.addSource(this@combine) { a ->
        val b = other.value
        if (b != null) {
            result.postValue(transform(a, b))
        }
    }
    return result
}

fun <E> SendChannel<E>.tryOffer(element: E): Boolean = try {
    offer(element)
} catch (t: Throwable) {
    false
}