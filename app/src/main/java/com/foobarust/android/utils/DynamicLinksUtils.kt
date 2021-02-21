package com.foobarust.android.utils

import android.net.Uri
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by kevin on 2/5/21
 */

private const val APP_DYNAMIC_LINK_DOMAIN = "foobarust.page.link"
private const val AUTH_DYNAMIC_LINK_DOMAIN = "foobarust2.page.link"

class DynamicLinksUtils @Inject constructor(
    private val firebaseDynamicLinks: FirebaseDynamicLinks
) {

    suspend fun extractDeepLink(link: Uri): Uri? {
        if (!isAppNavigationLink(link)) {
            return null
        }

        return try {
            firebaseDynamicLinks.getDynamicLink(link).await().link
        } catch (e: Exception) {
            null
        }
    }

    private fun isAuthenticationLink(link: Uri): Boolean {
        return link.toString().contains(AUTH_DYNAMIC_LINK_DOMAIN)
    }

    private fun isAppNavigationLink(link: Uri): Boolean {
        return link.toString().contains(APP_DYNAMIC_LINK_DOMAIN)
    }
}