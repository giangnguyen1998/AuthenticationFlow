package edu.nuce.apps.newflowtypes.shared.util

import kotlinx.coroutines.Job

val <T> T.checkAllMatched: T
    get() = this

/**
 * Cancel the Job if it's active.
 */
fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}