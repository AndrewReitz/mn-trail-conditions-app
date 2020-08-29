import com.android.build.gradle.internal.tasks.factory.dependsOn
import trails.gradle.GcpExtension
import trails.gradle.GcpSetupTask

plugins {
  kotlin("js")
}

dependencies {
  implementation(npm("@google-cloud/functions-framework", "1.7.1"))
}

val extension = extensions.create<GcpExtension>("gcp")

afterEvaluate {

  // todo figure out a way to remove absolute paths to package json
  // or just update to jvm runtime which GCP Cloud functions now support.

  val deployAll = tasks.register("deployAll") {
    group = "Deploy"
    description = "Deploys all gcp function targets"
  }

  extension.targets.forEach { target ->
    val targetTaskName = target.name.capitalize()

    val setupTask = tasks.register<GcpSetupTask>("setup$targetTaskName") {
      dependsOn("build")
      gcpTarget = target.name
      outputDir.set(file("${rootProject.buildDir}/js"))
    }

    tasks.register<Exec>("run$targetTaskName") {
      group = "Run"
      description = "Runs $target firebase function for local testing"
      dependsOn(setupTask)
      workingDir("${rootProject.buildDir}/js")
      commandLine(
        listOf(
          "node_modules/.bin/functions-framework",
          "--target=${target.name}",
          if (target.trigger=="topic") "--signature-type=event" else ""
        )
      )
    }

    val deploy = tasks.register<Exec>("deploy$targetTaskName") {
      group = "Deploy"
      description = "Deploys $target to GCP Functions"
      dependsOn(setupTask)
      workingDir("${rootProject.buildDir}/js")
      commandLine(target.toArgumentList())
    }
    deployAll.dependsOn(deploy)
  }
}
