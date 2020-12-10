package com.foobarust.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.SHARE_STATE_ON
import com.foobarust.android.R

/**
 * Created by kevin on 11/1/20
 */

private const val STABLE_PACKAGE = "com.android.chrome"
private const val BETA_PACKAGE = "com.chrome.beta"
private const val DEV_PACKAGE = "com.chrome.dev"
private const val LOCAL_PACKAGE = "com.google.android.apps.chrome"
private const val ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService"

/**
 * Launch either a chrome custom tab or browser intent
 * @return false if no browser is installed
 */
fun Activity.launchCustomTab(url: String): Boolean {
    val customTabPackageName = getCustomTabPackageNameToUse(this)

    return if (customTabPackageName != null) {
        launchCustomTab(this, customTabPackageName, url)
        true
    } else {
        launchBrowser(this, url)
    }
}

private fun launchCustomTab(context: Context, customTabPackageName: String, url: String) {
    val intent = buildCustomTabsIntent(context)

    intent.run {
        this.intent.setPackage(customTabPackageName)
        launchUrl(context, Uri.parse(url))
    }
}

private fun launchBrowser(context: Context, url: String): Boolean {
    val intent =  Intent(Intent.ACTION_VIEW, Uri.parse(url))

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
        return true
    }

    return false
}

private fun getCustomTabPackageNameToUse(context: Context): String? {
    val customTabsPackages = getCustomTabsPackages(context)

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

private fun buildCustomTabsIntent(context: Context): CustomTabsIntent {
    return CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(context.themeColor(R.attr.colorPrimarySurface))
                .build()
        )
        .setShareState(SHARE_STATE_ON)
        .setShowTitle(true)
        .build()
}

private fun getCustomTabsPackages(context: Context): List<String> {
    val packageManager = context.packageManager

    // Get default view intent handler
    val activityIntent = Intent()
        .setAction(Intent.ACTION_VIEW)
        .addCategory(Intent.CATEGORY_BROWSABLE)
        .setData(Uri.fromParts("http", "", null))

    // Get all apps that can handle view intents
    val resolvedActivityList = packageManager.queryIntentActivities(activityIntent, 0)

    return resolvedActivityList
        .filter {
            val serviceIntent = Intent().apply {
                action = ACTION_CUSTOM_TABS_CONNECTION
                setPackage(it.activityInfo.packageName)
            }

            packageManager.resolveService(serviceIntent, 0) != null
        }
        .map { it.activityInfo.packageName }
}