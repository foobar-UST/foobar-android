package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.ApplicationScope
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.AuthProfile
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.asUserDetail
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.AuthState
import com.foobarust.domain.usecases.AuthUseCase
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
 * Created by kevin on 2/6/21
 */

private const val TAG = "GetUserAuthStateUseCase"

@Singleton
class GetUserAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : AuthUseCase<Unit, UserDetail>(coroutineDispatcher) {

    private var observeUserDetailJob: Job? = null

    private val authFlow: SharedFlow<AuthState<UserDetail>> = channelFlow<AuthState<UserDetail>> {
        authRepository.getAuthProfileObservable().collect {
            stopObserveUserDetail()
            when (it) {
                is AuthState.Authenticated -> {
                    println("[$TAG]: User is signed in. Start observe UserDetail.")
                    startObserveUserDetail(authProfile = it.data)
                }
                AuthState.Unauthenticated -> {
                    println("[$TAG]: User is signed out.")
                    channel.offer(AuthState.Unauthenticated)
                }
                AuthState.Loading -> {
                    println("[$TAG]: Loading auth profile...")
                    channel.offer(AuthState.Loading)
                }
            }
        }
    }.shareIn(
        scope = externalScope,
        started = SharingStarted.Eagerly,
        replay = 1
    )

    override fun execute(parameters: Unit): Flow<AuthState<UserDetail>> = authFlow

    private fun ProducerScope<AuthState<UserDetail>>.startObserveUserDetail(
        authProfile: AuthProfile
    ) {
        observeUserDetailJob = externalScope.launch(coroutineDispatcher) {
            userRepository.getUserDetailObservable(authProfile.id).collect {
                when (it) {
                    is Resource.Success -> {
                        // Offer user detail data from network db or local cache.
                        println("[$TAG]: Network available, offered UserDetail.")
                        channel.offer(AuthState.Authenticated(it.data))
                    }
                    is Resource.Error -> {
                        // Offer auth profile if there is network error or the user detail
                        // document is not created yet. The snapshot observable should keep alive
                        // all the time, it will only be detached once the user is signed out.
                        println("[$TAG]: Network unavailable, offered AuthProfile.")
                        channel.offer(AuthState.Authenticated(authProfile.asUserDetail()))
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserDetail() {
        println("[$TAG]: Stop observing UserDetail.")
        observeUserDetailJob.cancelIfActive()
    }
}