package com.foobarust.data.utils

/**
 * Created by kevin on 8/25/20
 */

import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

inline fun <reified T, R> CollectionReference.snapshotFlow(crossinline mapper: (T) -> R): Flow<Resource<List<R>>> {
    return channelFlow {
        channel.offer(Resource.Loading())
        val subscription = this@snapshotFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
                channel.close(CancellationException(error.message))
            } else {
                value?.let {
                    val results = value.toObjects(T::class.java)
                    channel.offer(Resource.Success(results.map { mapper(it) }))
                } ?: channel.close(CancellationException("Error getting data."))
            }
        }
        awaitClose { subscription.remove() }
    }
}

inline fun <reified T, R> DocumentReference.snapshotFlow(crossinline mapper: (T) -> R): Flow<Resource<R>> {
    return channelFlow {
        channel.offer(Resource.Loading())
        val subscription = this@snapshotFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
                channel.close(CancellationException(error.message))
            } else {
                value?.let {
                    val result = it.toObject(T::class.java)

                    if (result == null) {
                        channel.offer(Resource.Error("Error getting data."))
                        channel.close(CancellationException("Error getting data."))
                    } else {
                        channel.offer(Resource.Success(mapper(result)))
                    }
                } ?: channel.close(CancellationException("Error getting data."))
            }
        }
        awaitClose { subscription.remove() }
    }
}

inline fun <reified T, R> Query.snapshotFlow(crossinline mapper: (T) -> R): Flow<Resource<List<R>>> {
    return channelFlow {
        channel.offer(Resource.Loading())
        val subscription = this@snapshotFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
                channel.close(CancellationException(error.message))
            } else {
                value?.let {
                    val results = value.toObjects(T::class.java)
                    channel.offer(Resource.Success(results.map { mapper(it) }))
                } ?: channel.close(CancellationException("Error getting data."))
            }
        }
        awaitClose { subscription.remove() }
    }
}