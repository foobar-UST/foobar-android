package com.foobarust.data.utils

/**
 * Created by kevin on 8/25/20
 */

import android.net.Uri
import com.foobarust.domain.states.Resource
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await

const val ERROR_DOCUMENT_NOT_EXIST = "Document does not exist."

suspend inline fun <reified T, R> CollectionReference.getAwaitResult(mapper: (T) -> R): List<R> {
    return this.get()
        .await()
        .toObjects(T::class.java)
        .map { mapper(it) }
}

suspend inline fun <reified T, R> DocumentReference.getAwaitResult(mapper: (T) -> R): R {
    val result = this.get()
        .await()
        .toObject(T::class.java)
        ?: throw Exception(ERROR_DOCUMENT_NOT_EXIST)

    return mapper(result)
}

suspend inline fun <reified T, R> Query.getAwaitResult(mapper: (T) -> R): List<R> {
    return this.get()
        .await()
        .toObjects(T::class.java)
        .map { mapper(it) }
}

inline fun <reified T, R> CollectionReference.snapshotFlow(crossinline mapper: (T) -> R): Flow<Resource<List<R>>> {
    return channelFlow {
        channel.offer(Resource.Loading())

        val subscription = this@snapshotFlow.addSnapshotListener { value, error ->
            if (error != null) {
                // Close the channel if there is error
                channel.offer(Resource.Error(error.message))
                channel.close(CancellationException(error.message))
            } else {
                value?.let { snapshot ->
                    if (!snapshot.metadata.hasPendingWrites()) {
                        val results = value.toObjects(T::class.java)
                        channel.offer(Resource.Success(results.map { mapper(it) }))
                    }
                } ?: channel.close(CancellationException(ERROR_DOCUMENT_NOT_EXIST))
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
                    if (!it.metadata.hasPendingWrites()) {
                        val result = it.toObject(T::class.java)

                        if (result == null) {
                            channel.offer(Resource.Error(ERROR_DOCUMENT_NOT_EXIST))
                            channel.close(CancellationException(ERROR_DOCUMENT_NOT_EXIST))
                        } else {
                            channel.offer(Resource.Success(mapper(result)))
                        }
                    }
                } ?: channel.close(CancellationException(ERROR_DOCUMENT_NOT_EXIST))
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
                value?.let { snapshot ->
                    if (!snapshot.metadata.hasPendingWrites()) {
                        val results = value.toObjects(T::class.java)
                        channel.offer(Resource.Success(results.map { mapper(it) }))
                    }
                } ?: channel.close(CancellationException(ERROR_DOCUMENT_NOT_EXIST))
            }
        }

        awaitClose { subscription.remove() }
    }
}

fun StorageReference.putFileFlow(uri: Uri): Flow<Resource<Unit>> = channelFlow {
    val progressListener = OnProgressListener<UploadTask.TaskSnapshot> {
        val progress = 100.0 * it.bytesTransferred / it.totalByteCount
        channel.offer(Resource.Loading(progress))
    }
    val successListener = OnSuccessListener<UploadTask.TaskSnapshot> {
        channel.offer(Resource.Success(Unit))
        channel.close()
    }
    val failureListener = OnFailureListener {
        channel.offer(Resource.Error(it.message))
        channel.close(CancellationException(it.message))
    }

    val uploadTask = this@putFileFlow.putFile(uri)
        .addOnProgressListener(progressListener)
        .addOnSuccessListener(successListener)
        .addOnFailureListener(failureListener)

    awaitClose {
        uploadTask.run {
            removeOnProgressListener(progressListener)
            removeOnSuccessListener(successListener)
            removeOnFailureListener(failureListener)
        }
    }
}