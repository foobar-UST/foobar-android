package com.foobarust.data.repositories

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import com.foobarust.data.api.RemoteService
import com.foobarust.data.cache.networkCacheResource
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.common.Constants.USERS_PUBLIC_COLLECTION
import com.foobarust.data.common.Constants.USER_PHOTOS_STORAGE_FOLDER
import com.foobarust.data.db.UserDao
import com.foobarust.data.mappers.UserMapper
import com.foobarust.data.models.user.UpdateUserDetailRequest
import com.foobarust.data.preferences.PreferencesKeys.ONBOARDING_COMPLETED
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.data.utils.putFileFlow
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * We assume users must be signed in before calling these methods
 */

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val remoteService: RemoteService,
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
        return networkCacheResource(
            cacheSource = {
                userMapper.fromCacheDtoToUserDetail(
                    userDao.getUser(userId)
                )
            },
            networkSource = {
                firestore.document("$USERS_COLLECTION/$userId")
                    .snapshotFlow(userMapper::fromNetworkDtoToUserDetail, true)
            },
            updateLocal = {
                userDao.insertUser(
                    userMapper.toUserDetailCacheDto(it)
                )
            }
        )
    }

    override suspend fun updateUserDetail(idToken: String, name: String?, phoneNum: String?) {
        val request = UpdateUserDetailRequest(
            name = name,
            phoneNum = phoneNum
        )
        remoteService.updateUserDetail(idToken, request)
    }

    override suspend fun clearUserDetailCache() {
        userDao.deleteAll()
    }

    override fun uploadUserPhoto(userId: String, uri: String, extension: String): Flow<Resource<Unit>> {
        val photoFile = Uri.parse(uri)
        val photoFileName = userId + extension
        val photoRef = storageReference.child("$USER_PHOTOS_STORAGE_FOLDER/$photoFileName")
        return photoRef.putFileFlow(photoFile)
    }

    override suspend fun getUserPublicInfo(userId: String): UserPublic {
        return firestore.document("$USERS_PUBLIC_COLLECTION/$userId")
            .getAwaitResult(userMapper::toUserPublic)
    }
}