package cash.andrew.mntrailconditions;

final class Modules {
  static Object[] list(MnTrailConditionsApp app) {
    return new Object[] {
        new MnTrailConditionsModule(app),
        new DebugMnTrailConditionsModule()
    };
  }

  private Modules() {
    // No instances.
  }
}