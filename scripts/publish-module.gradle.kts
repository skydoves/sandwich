import com.github.skydoves.sandwich.Configuration

rootProject.extra.apply {
  val snapshot = System.getenv("SNAPSHOT").toBoolean()
  val libVersion = if (snapshot) {
    Configuration.snapshotVersionName
  } else {
    Configuration.versionName
  }
  set("libVersion", libVersion)
}
