package com.ust.weatherforecastapp.interior

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

fun <T> Task<T>.asDeferred(): Deferred<T> {   //taken from resocoder.com
    val deferred = CompletableDeferred<T>()

    this.addOnSuccessListener {result ->
        deferred.complete(result)
    }

    this.addOnFailureListener { exception->
        deferred.completeExceptionally(exception)
    }

    return deferred
}