package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.ApplicationScope
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.AuthProfile
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.asUserDetail
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import com.foobarust.domain.utils.cancelIfActive
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kevin on 9/17/20
 */

/**
 * If the user is signed in and network is available -> produce data from network db.
 * If the user is signed in and network in unavailable -> produce data from auth.
 * If the user is signed out, produce null.
 */
@Singleton
class GetUserDetailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, UserDetail?>(coroutineDispatcher) {

    private var observeUserDetailJob: Job? = null

    // Share result to multiple consumers
    private val sharedResult: SharedFlow<Resource<UserDetail?>> = channelFlow<Resource<UserDetail?>> {
        authRepository.getAuthProfileObservable().collect { result ->
            println("[GetUserDetailUseCase]: AuthProfile collected.")
            stopObserveUserDetail()
            if (result is Resource.Success) {
                val authProfile = result.data
                if (authProfile != null) {
                    // User is signed in
                    println("[GetUserDetailUseCase]: User is signed in. Start Observe UserDetail.")
                    startObserveUserDetail(userId = authProfile.id, authProfile = authProfile)
                } else {
                    // User is signed out
                    println("[GetUserDetailUseCase]: User is signed out.")
                    channel.offer(Resource.Success(null))
                }
            }
        }
    }.shareIn(
        scope = externalScope,
        started = SharingStarted.Eagerly,       // Produce immediately
        replay = 1
    )

    override fun execute(parameters: Unit): Flow<Resource<UserDetail?>> = sharedResult

    private fun ProducerScope<Resource<UserDetail>>.startObserveUserDetail(
        userId: String,
        authProfile: AuthProfile
    ) {
        observeUserDetailJob = externalScope.launch(coroutineDispatcher) {
            userRepository.getUserDetailObservable(userId).collect {
                when (it) {
                    is Resource.Success -> {
                        // Offer user detail data from Firestore
                        println("[GetUserDetailUseCase]: Offered UserDetail.")
                        channel.offer(Resource.Success(it.data))
                    }
                    is Resource.Error -> {
                        // Offer auth profile data if there is network error or the user detail
                        // document is not created yet. The snapshot observable will still keep alive
                        // when there is no network, it will only be detached once the user is signed out.
                        println("[GetUserDetailUseCase]: Offered AuthProfile.")
                        channel.offer(Resource.Success(authProfile.asUserDetail()))
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserDetail() {
        println("[GetUserDetailUseCase]: Stop observing UserDetail.")
        observeUserDetailJob.cancelIfActive()
    }
}