package cash.andrew.mntrailconditions

import android.app.Application
import cash.andrew.mntrailconditions.data.DataModule
import cash.andrew.mntrailconditions.data.LumberYard
import cash.andrew.mntrailconditions.ui.ActivityHierarchyServer
import cash.andrew.mntrailconditions.ui.UiModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope
import javax.inject.Singleton

@Scope annotation class ApplicationScope

@Singleton
@Component(modules = [MnTrailConditionsModule::class])
interface AppComponent {
    val activityHierarchyServer: ActivityHierarchyServer
    val lumberYard: LumberYard
    val appInitializer: MnTrailConditionsInitializer

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}

@Module(includes = [UiModule::class, DataModule::class])
object MnTrailConditionsModule {
    @JvmStatic
    @Provides
    fun provideMnTrailConditionsInitializer(): MnTrailConditionsInitializer = {}
}
