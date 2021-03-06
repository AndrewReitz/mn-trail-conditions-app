package cash.andrew.mntrailconditions.util

import cash.andrew.mntrailconditions.data.NotificationTrails
import cash.andrew.mntrailconditions.data.preference.Preference
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

/**
 * Re-subscribes to all topics when the app starts. This is to avoid issues when
 * the app is upgraded or installed from play w/ data restored, to ensure the
 * user gets notifications.
 */
class StartUpFirebaseTopicSubscriber @Inject constructor(
        @NotificationTrails private val notificationsPref: Preference<Set<String>>,
        private val firebaseMessaging: FirebaseMessaging
) {
    fun subscribe() {
        notificationsPref.get()
                .forEach {
                  firebaseMessaging.unsubscribeFromTopic(it)
                  firebaseMessaging.subscribeToTopicV2(it)
                }
    }
}
