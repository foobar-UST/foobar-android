package com.foobarust.android.auth

import javax.inject.Inject

/**
 * Created by kevin on 9/23/20
 */

class AuthEmailUtil @Inject constructor() {

    val emailDomains: List<AuthEmailDomain> = listOf(
        AuthEmailDomain(domain = "connect.ust.hk", title = "@connect.ust.hk"),
        AuthEmailDomain(domain = "ust.hk", title = "@ust.hk")
    )
}

data class AuthEmailDomain(
    val domain: String,
    val title: String
)
