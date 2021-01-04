package com.foobarust.data.repositories

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.common.Constants.USERS_PUBLIC_COLLECTION
import com.foobarust.data.common.Constants.USER_PHOTOS_STORAGE_FOLDER
import com.foobarust.data.mappers.UserMapper
import com.foobarust.data.preferences.PreferencesKeys.ONBOARDING_COMPLETED
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.data.utils.putFileFlow
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * We assume users must be signed in before calling these methods
 */

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storageReference: StorageReference,
    private val preferences: SharedPreferences,
    private val userMapper: UserMapper
) : UserRepository {

    override suspend fun getOnboardingCompleted(): Boolean {
        return preferences.getBoolean(ONBOARDING_COMPLETED, false)
    }

    override suspend fun updateOnboardingCompleted(completed: Boolean) {
        preferences.edit { putBoolean(ONBOARDING_COMPLETED, completed) }
    }

    override fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>> {
        return firestore.document("$USERS_COLLECTION/$userId")
            .snapshotFlow(userMapper::toUserDetail, true)
    }

    override suspend fun updateUserDetail(userId: String, userDetail: UserDetail) {
        val userDetailEntity = userMapper.toUserDetailEntity(userDetail)

        firestore.document("$USERS_COLLECTION/$userId")
            .set(userDetailEntity, SetOptions.merge())
            .await()
    }

    override fun updateUserPhoto(userId: String, uriString: String): Flow<Resource<Unit>> {
        val photoFile = Uri.parse(uriString)
        val photoRef = storageReference.child("$USER_PHOTOS_STORAGE_FOLDER/$userId")

        return photoRef.putFileFlow(photoFile)
    }

    override suspend fun getUserPublicInfo(userId: String): UserPublic {
        return firestore.document("$USERS_PUBLIC_COLLECTION/$userId")
            .getAwaitResult(userMapper::toUserPublic)
    }
}