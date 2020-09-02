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

/**
 * Wrap the result of a collection reference into a flow structure
 */
inline fun <reified T> CollectionReference.snapshotFlow(): Flow<Resource<List<T>>> {
    return channelFlow {
        channel.offer(Resource.Loading)
        val subscription = this@snapshotFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
            } else {
                value?.let {
                    val results = value.toObjects(T::class.java)
                    channel.offer(Resource.Success(results))
                } ?: channel.close(CancellationException("No data received."))
            }
        }
        awaitClose { subscription.remove() }
    }
}

/**
 * Wrap the result of a document reference into a flow structure
 */
inline fun <reified T> DocumentReference.snapshotFlow(): Flow<Resource<T?>> {
    return channelFlow {
        channel.offer(Resource.Loading)
        val subscription = this@snapshotFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
            } else {
                value?.let {
                    val result = value.toObject(T::class.java)
                    channel.offer(Resource.Success(result))
                } ?: channel.close(CancellationException("No data received."))
            }
        }
        awaitClose { subscription.remove() }
    }
}

/**
 * Wrap the result of a query into a flow structure
 */
inline fun <reified T> Query.snapshotFlow(): Flow<Resource<List<T>>> {
    return channelFlow {
        channel.offer(Resource.Loading)
        val subscription = this@snapshotFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
            } else {
                value?.let {
                    val results = value.toObjects(T::class.java)
                    channel.offer(Resource.Success(results))
                } ?: channel.close(CancellationException("No data received."))
            }
        }
        awaitClose { subscription.remove() }
    }
}