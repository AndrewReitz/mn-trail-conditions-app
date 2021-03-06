package cash.andrew.mntrailconditions.data

import android.content.SharedPreferences
import cash.andrew.mntrailconditions.data.api.ApiModule
import cash.andrew.mntrailconditions.data.api.DebugApiModule
import cash.andrew.mntrailconditions.data.preference.*
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Qualifier

@Qualifier annotation class ApiEndpoint
@Qualifier annotation class AnimationSpeed
@Qualifier annotation class CaptureIntents
@Qualifier annotation class NetworkDelay
@Qualifier annotation class NetworkFailurePercent
@Qualifier annotation class NetworkVariancePercent

@Module(includes = [ApiModule::class, DebugApiModule::class])
object DebugDataModule {

    private const val DEFAULT_ANIMATION_SPEED = 1 // 1x (normal) speed.
    private const val DEFAULT_CAPTURE_INTENTS = true // Capture external intents.

    @Provides
    @Reusable
    @ApiEndpoint
    fun provideEndpointPreference(preferences: SharedPreferences): Preference<String> =
            preferences.stringPreference("debug_endpoint", requireNotNull(ApiEndpoints.PRODUCTION.url))

    @Provides
    @Reusable
    @NetworkDelay
    fun provideNetworkDelay(preferences: SharedPreferences): Preference<Long> =
            preferences.longPreference("debug_network_delay", 2000L)

    @Provides
    @Reusable
    @NetworkFailurePercent
    fun provideNetworkFailurePercent(preferences: SharedPreferences): Preference<Int> =
            preferences.intPreference("debug_network_failure_percent", 3)

    @Provides
    @Reusable
    @NetworkVariancePercent
    fun provideNetworkVariancePercent(preferences: SharedPreferences): Preference<Int> =
            preferences.intPreference("debug_network_variance_percent", 40)

    @Provides
    @Reusable
    @CaptureIntents
    fun provideCaptureIntentsPreference(preferences: SharedPreferences): Preference<Boolean> =
            preferences.booleanPreference("debug_capture_intents", DEFAULT_CAPTURE_INTENTS)

    @Provides
    @Reusable
    @AnimationSpeed
    fun provideAnimationSpeed(preferences: SharedPreferences): Preference<Int> =
            preferences.intPreference("debug_animation_speed", DEFAULT_ANIMATION_SPEED)
}
