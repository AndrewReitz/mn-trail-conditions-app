package cash.andrew.mntrailconditions.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import cash.andrew.mntrailconditions.R
import cash.andrew.mntrailconditions.data.preference.Preference
import cash.andrew.mntrailconditions.databinding.TrailItemBottomBarBinding
import cash.andrew.mntrailconditions.ui.trails.TrailViewModel
import cash.andrew.mntrailconditions.util.setToolTipTextCompat
import cash.andrew.mntrailconditions.util.subscribeToTopicV2
import cash.andrew.mntrailconditions.util.toTopicName
import cash.andrew.mntrailconditions.util.unsubscribeFromTopicV2
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

/**
 * View that contains actions for trails such as favoring and push notifications.
 */
class TrailItemBottomBar(
        context: Context, attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

    private val binding: TrailItemBottomBarBinding

    init {
        inflate(context, R.layout.trail_item_bottom_bar, this)

        binding = TrailItemBottomBarBinding.bind(this)

        binding.trailFavorite.setToolTipTextCompat(R.string.favorite)
        binding.trailNotification.setToolTipTextCompat(R.string.notification)
    }

    fun bind(
            trail: TrailViewModel,
            favoriteTrailsPref: Preference<Set<String>>,
            notificationsPref: Preference<Set<String>>,
            firebaseMessaging: FirebaseMessaging,
            firebaseAnalytics: FirebaseAnalytics
    ) {
        binding.trailFavorite.apply {
            setOnCheckedChangeListener(null)
            isEnabled = false
            isChecked = favoriteTrailsPref.get().contains(trail.name)
            setOnCheckedChangeListener(
                    favoriteClickListener(trail, favoriteTrailsPref, firebaseAnalytics)
            )
            isEnabled = true
        }

        binding.trailNotification.apply {
            setOnCheckedChangeListener(null)
            isEnabled = false
            isChecked = notificationsPref.get().contains(trail.name)
            setOnCheckedChangeListener(
                    notificationListener(trail, notificationsPref, firebaseMessaging, firebaseAnalytics)
            )
            isEnabled = true
        }
    }

    private fun favoriteClickListener(
            trail: TrailViewModel,
            favoriteTrailsPref: Preference<Set<String>>,
            firebaseAnalytics: FirebaseAnalytics
    ): (View, Boolean) -> Unit = { _, enabled ->
        Timber.d("favoriteClickListener() enabled = [%s]", enabled)
        Timber.d("trailName = [%s]", trail.name)

        val bundle = Bundle().apply {
            putString("favorite_name", trail.name)
        }

        val favorite = if (enabled) "favorite_endabled" else "favorite_disabled"
        firebaseAnalytics.logEvent(favorite, bundle)

        val favoriteTrails = favoriteTrailsPref.get()
        Timber.d("favoriteTrails = [%s]", favoriteTrails)

        val updatedTrails = favoriteTrails.toMutableSet().apply {
            if (enabled) add(trail.name) else remove(trail.name)
        }
        Timber.d("after update favoriteTrails = [%s]", updatedTrails)

        favoriteTrailsPref.set(updatedTrails)
    }

    private fun notificationListener(
            trail: TrailViewModel,
            notificationsPref: Preference<Set<String>>,
            firebaseMessaging: FirebaseMessaging,
            firebaseAnalytics: FirebaseAnalytics
    ): (View, Boolean) -> Unit = { _, enabled ->
        Timber.d("notificationListener() enabled = [%s]", enabled)
        Timber.d("trailName = [%s]", trail.name)

        val bundle = Bundle().apply {
            putString("notification_name", trail.name)
        }

        val event = if (enabled) "notification_enabled" else "notification_disabled"
        firebaseAnalytics.logEvent(event, bundle)

        val notifications = notificationsPref.get()
        Timber.d("notifications = [%s]", notifications)

        val updated = notifications.toMutableSet().apply {
            if (enabled) add(trail.name) else remove(trail.name)
        }
        Timber.d("after update notifications = [%s]", updated)

        notificationsPref.set(updated)

        val unSubscribe = notifications - updated
        val subscribe = updated - notifications

        unSubscribe.forEach { trailName ->
            firebaseMessaging.unsubscribeFromTopicV2(trailName.toTopicName())
                    .addOnSuccessListener { Timber.i("Success un-subscribing to ${trailName.toTopicName()}") }
                    .addOnFailureListener { Timber.e(it, "Error removing notifications for ${trailName.toTopicName()}") }
        }

        subscribe.forEach { trailName ->
            firebaseMessaging.subscribeToTopicV2(trailName)
                    .addOnSuccessListener { Timber.i("Success subscribing to ${trailName.toTopicName()}") }
                    .addOnFailureListener { Timber.e(it, "Error subscribing to notifications for ${trailName.toTopicName()}") }
        }
    }
}
