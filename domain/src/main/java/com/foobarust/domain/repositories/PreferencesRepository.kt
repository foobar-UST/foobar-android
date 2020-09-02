package com.foobarust.domain.repositories

/**
 * Created by kevin on 8/28/20
 */

interface PreferencesRepository {
    var emailToBeVerified: String?
    var isSkippedSignIn: Boolean
}