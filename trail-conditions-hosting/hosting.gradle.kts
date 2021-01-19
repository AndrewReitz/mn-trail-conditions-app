description = """
  Host files on firebase. Used by the app to get "about" and "why are trails closed" markdown
  files so that the app does not need to be published every time text is updated.
"""

tasks.register<Exec>("publishHosting") {
  description = "Publish trail conditions markdown to firebase"
  group = "deploy"
  commandLine(
    "firebase",
    "deploy",
    "--only",
    "hosting"
  )
}
