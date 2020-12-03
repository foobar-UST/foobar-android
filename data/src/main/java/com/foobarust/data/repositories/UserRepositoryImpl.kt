package com.foobarust.data.repositories

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.common.Constants.USER_CART_ITEMS_SUB_COLLECTION
import com.foobarust.data.common.Constants.USER_PHOTOS_STORAGE_FOLDER
import com.foobarust.data.mappers.UserMapper
import com.foobarust.data.preferences.PreferencesKeys.PREF_KEY_ONBOARDING_COMPLETED
import com.foobarust.data.preferences.PreferencesKeys.PREF_KEY_USER_DETAIL
import com.foobarust.data.utils.*
import com.foobarust.domain.models.user.UserCartItem
import com.foobarust.domain.models.user.UserDetail
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
    private val userMapper: UserMapper,
    private val preferences: SharedPreferences
) : UserRepository {

    override suspend fun getIsOnboardingCompleted(): Boolean {
        return preferences.getBoolean(PREF_KEY_ONBOARDING_COMPLETED, false)
    }

    override suspend fun saveIsOnboardingCompleted(isCompleted: Boolean) {
        preferences.edit { putBoolean(PREF_KEY_ONBOARDING_COMPLETED, isCompleted) }
    }

    override suspend fun getLocalUserDetail(userId: String): UserDetail? {
        return preferences.getObject("${PREF_KEY_USER_DETAIL}_$userId")
    }

    override suspend fun updateLocalUserDetail(userId: String, userDetail: UserDetail) {
        preferences.putObject("${PREF_KEY_USER_DETAIL}_$userId", userDetail)
    }

    override suspend fun removeLocalUserDetail(userId: String) {
        preferences.putObject("${PREF_KEY_USER_DETAIL}_$userId", null)
    }

    override suspend fun getRemoteUserDetail(userId: String): UserDetail {
        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .getAwaitResult(userMapper::toUserDetail)
    }

    override fun getRemoteUserDetailObservable(userId: String): Flow<Resource<UserDetail>> {
        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .snapshotFlow(userMapper::toUserDetail)
    }

    override suspend fun updateRemoteUserDetail(userId: String, userDetail: UserDetail) {
        val userDetailEntity = userMapper.toUserDetailEntity(userDetail)

        firestore.collection(USERS_COLLECTION).document(userId)
            .set(userDetailEntity, SetOptions.merge())
            .await()
    }

    override fun updateUserPhoto(userId: String, uriString: String): Flow<Resource<Unit>> {
        val photoFile = Uri.parse(uriString)
        val photoRef = storageReference.child("$USER_PHOTOS_STORAGE_FOLDER/$userId")

        return photoRef.putFileFlow(photoFile)
    }

    override fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>> {
        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(USER_CART_ITEMS_SUB_COLLECTION)
            .snapshotFlow(userMapper::toUserCartItem)
    }

    // TODO: Migrate to backend
    override suspend fun addUserCartItem(userId: String, userCartItem: UserCartItem) {
        // TODO: increment amount
        val userCartItemEntity = userMapper.toUserCartItemEntity(userCartItem)

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(USER_CART_ITEMS_SUB_COLLECTION)
            .document()
            .set(userCartItemEntity)
            .await()
    }

    // TODO: Migrate to backend
    override suspend fun removeUserCartItem(userId: String, userCartItem: UserCartItem) {
        val document = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(USER_CART_ITEMS_SUB_COLLECTION)
            .document(userCartItem.id)

        val requestedItem = document.getAwaitResult(userMapper::toUserCartItem)

        // Reduce the amounts or delete the cart item
        if (requestedItem.amounts > 1) {
            val updatedItem = requestedItem.copy(amounts = requestedItem.amounts - 1)
            document.set(userMapper.toUserCartItemEntity(userCartItem = updatedItem), SetOptions.merge())
        } else {
            document.delete().await()
        }
    }
}