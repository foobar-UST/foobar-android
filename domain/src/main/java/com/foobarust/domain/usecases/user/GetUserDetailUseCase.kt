package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.ApplicationScope
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.AuthProfile
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.asUserDetail
import com.foobarust.domain.models.user.isSignedIn
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import com.foobarust.domain.utils.cancelIfActive
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 9/17/20
 */

class GetUserDetailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, UserDetail>(coroutineDispatcher) {

    private var observeUserDetailJob: Job? = null

    override fun execute(parameters: Unit): Flow<Resource<UserDetail>> = channelFlow {
        authRepository.getAuthProfileObservable().collect { result ->
            stopObserveUserDetail()

            if (result is Resource.Success) {
                val authProfile = result.data
                if (authProfile.isSignedIn()) {
                    println("[GetUserDetailUseCase]: observing auth: signed in")
                    startObserveUserDetail(userId = authProfile.id!!, authProfile = authProfile)
                } else {
                    println("[GetUserDetailUseCase]: observing auth: signed out")
                    channel.offer(Resource.Success(authProfile.asUserDetail()))
                }
            }
        }
    }

    private fun ProducerScope<Resource<UserDetail>>.startObserveUserDetail(
        userId: String,
        authProfile: AuthProfile
    ) {
        println("[GetUserDetailUseCase]: startObserveUserDetail")
        observeUserDetailJob = externalScope.launch(coroutineDispatcher) {
            userRepository.getUserDetailObservable(userId).collect {
                when (it) {
                    is Resource.Success -> {
                        // Offer user detail data from firestore
                        println("[GetUserDetailUseCase]: using UserDetail")
                        channel.offer(Resource.Success(it.data))
                    }
                    is Resource.Error -> {
                        // Offer auth profile data if there is network error or the user detail
                        // document is not created yet. The snapshot observable will still keep alive
                        // when there is no network, it will only be detached once the user is signed out.
                        println("[GetUserDetailUseCase]: using AuthProfile")
                        channel.offer(Resource.Success(authProfile.asUserDetail()))
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserDetail() {
        println("[GetUserDetailUseCase]: stopObserveUserDetail")
        observeUserDetailJob.cancelIfActive()
    }
}