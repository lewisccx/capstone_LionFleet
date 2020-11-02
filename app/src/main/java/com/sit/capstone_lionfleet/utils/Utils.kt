package com.sit.capstone_lionfleet.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.sit.capstone_lionfleet.R
import timber.log.Timber
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun <A : Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this, activity).also{
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}
fun isFetchNeeded(saveTimeStamp: LocalDateTime, apiModuleLimit: Int): Boolean{

    return ChronoUnit.MINUTES.between(saveTimeStamp, LocalDateTime.now()) > apiModuleLimit
}

const val PRIMARY_ROUTE_BUNDLE_KEY = "myPrimaryRouteBundleKey"

/**
 * Used by the example activities to get a DirectionsRoute from a bundle.
 *
 * @param bundle to get the DirectionsRoute from
 * @return a DirectionsRoute or null
 */
fun getRouteFromBundle(bundle: Bundle): DirectionsRoute? {
    try {
        if (bundle.containsKey(PRIMARY_ROUTE_BUNDLE_KEY)) {
            val routeAsJson = bundle.getString(PRIMARY_ROUTE_BUNDLE_KEY)
            return DirectionsRoute.fromJson(routeAsJson)
        }
    } catch (ex: Exception) {
        Timber.i(ex)
    }
    return null
}

fun showFeedbackSentSnackBar(
    context: Context,
    view: View,
    @StringRes message: Int = R.string.mapbox_feedback_reported,
    length: Int = Snackbar.LENGTH_SHORT,
    setAnchorView: Boolean = false
) {
    val snackBar = Snackbar.make(
        view,
        message,
        length
    )

    if (setAnchorView) {
        snackBar.anchorView = view
    }

    snackBar.view.setBackgroundColor(
        ContextCompat.getColor(
            context,
            com.mapbox.navigation.ui.R.color.mapbox_feedback_bottom_sheet_secondary
        )
    )
    snackBar.setTextColor(
        ContextCompat.getColor(
            context,
            com.mapbox.navigation.ui.R.color.mapbox_feedback_bottom_sheet_primary_text
        )
    )

    snackBar.show()
}
