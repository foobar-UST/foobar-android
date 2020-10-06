package com.foobarust.data.repositories

import android.net.Uri
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.common.Constants.USER_PHOTOS_STORAGE_FOLDER
import com.foobarust.data.mappers.UserMapper
import com.foobarust.data.utils.saveUpdateTimestamp
import com.foobarust.data.utils.serializeToMutableMap
import com.foobarust.data.utils.snapshotObservableFlow
import com.foobarust.domain.models.UserDetail
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * We assume users must be signed in before calling these methods
 */

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storageReference: StorageReference,
    private val userMapper: UserMapper
) : UserRepository {

    // Get a document from 'users' collection
    override fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>> {
        return firestore.collection(USERS_COLLECTION).document(userId)
            .snapshotObservableFlow(userMapper::toUserDetail)
    }

    override suspend fun updateUserDetail(userId: String, userDetail: UserDetail) {
        val updatedMap = userMapper.toUserDetailEntity(userDetail)
            .serializeToMutableMap()
            .saveUpdateTimestamp()

        firestore.collection(USERS_COLLECTION).document(userId)
            .update(updatedMap)
            .await()
    }

    override fun updateUserPhoto(userId: String, uriString: String): Flow<Resource<Unit>> {
        return channelFlow {
            val photoFile = Uri.parse(uriString)
            val photoRef = storageReference.child("$USER_PHOTOS_STORAGE_FOLDER/$userId")

            // Start uploading
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

            val uploadTask = photoRef.putFile(photoFile)
                .addOnProgressListener(progressListener)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)

            // Clean up
            awaitClose {
                uploadTask.run {
                    removeOnProgressListener(progressListener)
                    removeOnSuccessListener(successListener)
                    removeOnFailureListener(failureListener)
                }
            }
        }
    }
}