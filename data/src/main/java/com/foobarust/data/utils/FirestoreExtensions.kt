package com.foobarust.data.utils

/**
 * Created by kevin on 8/25/20
 */

import com.foobarust.data.common.Constants.UPDATED_AT_FIELD
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

const val ERROR_DOCUMENT_NOT_EXIST = "Document does not exist."

internal fun MutableMap<String, Any>.saveUpdateTimestamp(): Map<String, Any> {
    put(UPDATED_AT_FIELD, FieldValue.serverTimestamp())
    return this
}

inline fun <reified T, R> CollectionReference.snapshotFlow(crossinline mapper: (T) -> R): Flow<Resource<List<R>>> {
    return flow {
        emit(Resource.Loading())

        try {
            val result = this@snapshotFlow.get().await()
                .toObjects(T::class.java)
                .map { mapper(it) }

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
}

inline fun <reified T, R> DocumentReference.snapshotFlow(crossinline mapper: (T) -> R): Flow<Resource<R>> {
    return flow {
        emit(Resource.Loading())

        try {
            val result = this@snapshotFlow.get().await()
                .toObject(T::class.java)

            if (result == null) {
                emit(Resource.Error(ERROR_DOCUMENT_NOT_EXIST))
            } else {
                emit(Resource.Success(mapper(result)))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
}

inline fun <reified T, R> Query.snapshotFlow(crossinline mapper: (T) -> R): Flow<Resource<List<R>>> {
    return flow {
        emit(Resource.Loading())

        try {
            val result = this@snapshotFlow.get().await()
                .toObjects(T::class.java)
                .map { mapper(it) }

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
}

inline fun <reified T, R> CollectionReference.snapshotObservableFlow(crossinline mapper: (T) -> R): Flow<Resource<List<R>>> {
    return channelFlow {
        channel.offer(Resource.Loading())
        val subscription = this@snapshotObservableFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
                channel.close(CancellationException(error.message))
            } else {
                value?.let {
                    val results = value.toObjects(T::class.java)
                    channel.offer(Resource.Success(results.map { mapper(it) }))
                } ?: channel.close(CancellationException(ERROR_DOCUMENT_NOT_EXIST))
            }
        }
        awaitClose { subscription.remove() }
    }
}

inline fun <reified T, R> DocumentReference.snapshotObservableFlow(crossinline mapper: (T) -> R): Flow<Resource<R>> {
    return channelFlow {
        channel.offer(Resource.Loading())
        val subscription = this@snapshotObservableFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
                channel.close(CancellationException(error.message))
            } else {
                value?.let {
                    val result = it.toObject(T::class.java)

                    if (result == null) {
                        channel.offer(Resource.Error(ERROR_DOCUMENT_NOT_EXIST))
                        channel.close(CancellationException(ERROR_DOCUMENT_NOT_EXIST))
                    } else {
                        channel.offer(Resource.Success(mapper(result)))
                    }
                } ?: channel.close(CancellationException(ERROR_DOCUMENT_NOT_EXIST))
            }
        }
        awaitClose { subscription.remove() }
    }
}

inline fun <reified T, R> Query.snapshotObservableFlow(crossinline mapper: (T) -> R): Flow<Resource<List<R>>> {
    return channelFlow {
        channel.offer(Resource.Loading())
        val subscription = this@snapshotObservableFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
                channel.close(CancellationException(error.message))
            } else {
                value?.let {
                    val results = value.toObjects(T::class.java)
                    channel.offer(Resource.Success(results.map { mapper(it) }))
                } ?: channel.close(CancellationException(ERROR_DOCUMENT_NOT_EXIST))
            }
        }
        awaitClose { subscription.remove() }
    }
}