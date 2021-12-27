package shared

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

suspend fun <T> Promise<T>.await(): T = suspendCoroutine { continuation ->
    then({ continuation.resume(it) }, { continuation.resumeWithException(it) })
}

fun <T, S> Promise<T>.flatThen(callback: (T) -> Promise<S>) = Promise<S> { resolve, reject ->
    then { callback(it).then(resolve).catch(reject) }.catch(reject)
}