package com.foobarust.data.repositories

import android.net.Uri
import com.foobarust.data.mappers.UserMapper
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.UserDetailInfo
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

private const val USERS_COLLECTION = "users"
private const val USER_PHOTOS_BUCKET = "user_photos"

private const val ERROR_NOT_SIGNED_IN = "User is not signed in."

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storageReference: StorageReference,
    private val userMapper: UserMapper
) : UserRepository {

    // Get a document from 'users' collection
    override fun getUserDetailInfoObservable(uid: String): Flow<Resource<UserDetailInfo>> {
        return firestore.collection(USERS_COLLECTION).document(uid)
            .snapshotFlow(userMapper::toUserDetailInfo)
    }

    override suspend fun updateUserName(uid: String, name: String) {
        val currentUser = firebaseAuth.currentUser ?: throw Exception(ERROR_NOT_SIGNED_IN)

        firestore.collection(USERS_COLLECTION).document(currentUser.uid)
            .update(mapOf(
                "name" to name,
                "updated_at" to FieldValue.serverTimestamp()
            ))
            .await()
    }

    override suspend fun updateUserPhoneNumber(uid: String, phoneNum: String) {
        val currentUser = firebaseAuth.currentUser ?: throw Exception(ERROR_NOT_SIGNED_IN)

        firestore.collection(USERS_COLLECTION).document(currentUser.uid)
            .update(mapOf(
                "phone_num" to phoneNum,
                "updated_at" to FieldValue.serverTimestamp()
            ))
            .await()
    }

    override fun updateUserPhoto(uid: String, uriString: String): Flow<Resource<Unit>> = channelFlow {
        val photoFile = Uri.parse(uriString)
        val photoRef = storageReference.child("$USER_PHOTOS_BUCKET/$uid")

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