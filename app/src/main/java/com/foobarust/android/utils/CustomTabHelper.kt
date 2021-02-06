package com.foobarust.android.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.SHARE_STATE_ON

/**
 * Created by kevin on 11/1/20
 */

private const val STABLE_PACKAGE = "com.android.chrome"
private const val BETA_PACKAGE = "com.chrome.beta"
private const val DEV_PACKAGE = "com.chrome.dev"
private const val LOCAL_PACKAGE = "com.google.android.apps.chrome"
private const val ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService"

object CustomTabHelper {

    fun launchCustomTab(context: Context, url: String, @ColorInt colorInt: Int): Boolean {
        val customTabPackageName = getAvailableCustomTabPackage(context)
        if (customTabPackageName != null) {
            val intent = buildCustomTabIntent(colorInt)
            intent.intent.setPackage(customTabPackageName)
            intent.launchUrl(context, Uri.parse(url))
            return true
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return true
            }
        }

        return false
    }

    /**
     * Get the package that support custom tab.
     * Return null if no package found.
     */
    private fun getAvailableCustomTabPackage(context: Context): String? {
        val customTabsPackages = getSupportCustomTabPackages(context)
        return when {
            customTabsPackages.isEmpty() -> null
            customTabsPackages.size == 1 -> customTabsPackages.first()
            STABLE_PACKAGE in customTabsPackages -> STABLE_PACKAGE
            BETA_PACKAGE in customTabsPackages -> BETA_PACKAGE
            DEV_PACKAGE in customTabsPackages -> DEV_PACKAGE
            LOCAL_PACKAGE in customTabsPackages -> LOCAL_PACKAGE
            else -> null
        }
    }

    private fun buildCustomTabIntent(@ColorInt colorInt: Int): CustomTabsIntent {
        val colorSchemeBuilder = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(colorInt)
            .build()

        return CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colorSchemeBuilder)
            .setShareState(SHARE_STATE_ON)
            .setShowTitle(true)
            .build()
    }

    private fun getSupportCustomTabPackages(context: Context): List<String> {
        val packageManager = context.packageManager

        // Get default view intent handler
        val activityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))

        // Get all apps that can handle view intents
        val resolvedActivityList = packageManager.queryIntentActivities(activityIntent, 0)

        return resolvedActivityList.filter {
            val serviceIntent = Intent().apply {
                action = ACTION_CUSTOM_TABS_CONNECTION
                setPackage(it.activityInfo.packageName)
            }
            packageManager.resolveService(serviceIntent, 0) != null
        }.map { it.activityInfo.packageName }
    }
}
