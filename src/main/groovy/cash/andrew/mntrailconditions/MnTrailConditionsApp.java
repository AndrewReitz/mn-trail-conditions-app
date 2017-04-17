package cash.andrew.mntrailconditions;

import android.app.Application;
import android.support.annotation.NonNull;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.jakewharton.threetenabp.AndroidThreeTen;
import cash.andrew.mntrailconditions.data.Injector;
import cash.andrew.mntrailconditions.data.LumberYard;
import cash.andrew.mntrailconditions.ui.ActivityHierarchyServer;
import com.squareup.leakcanary.LeakCanary;
import dagger.ObjectGraph;
import javax.inject.Inject;
import timber.log.Timber;

public final class MnTrailConditionsApp extends Application {
  private ObjectGraph objectGraph;

  @Inject ActivityHierarchyServer activityHierarchyServer;
  @Inject LumberYard lumberYard;
  @Inject MnTrailConditionsInitializer appInitializer;

  @Override public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this) || ProcessPhoenix.isPhoenixProcess(this)) {
      return;
    }
    LeakCanary.install(this);
    AndroidThreeTen.init(this);

    objectGraph = ObjectGraph.create(Modules.list(this));
    objectGraph.inject(this);

    appInitializer.init(this);

    lumberYard.cleanUp();
    Timber.plant(lumberYard.tree());

    registerActivityLifecycleCallbacks(activityHierarchyServer);
  }

  @Override public Object getSystemService(@NonNull String name) {
    if (Injector.matchesService(name)) {
      return objectGraph;
    }
    return super.getSystemService(name);
  }
}