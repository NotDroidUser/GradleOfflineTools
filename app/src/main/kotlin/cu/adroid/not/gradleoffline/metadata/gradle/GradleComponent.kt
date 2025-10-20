package cu.adroid.not.gradleoffline.metadata.gradle

/**
 * Gradle module metadata component object
 * Group and version are equivalent their respectives on pom file
 * Moudle is the Name on file
 * Url is somelike parent on pom (yet to review this one)
 *
 **/
data class GradleComponent(
  val group:String,
  val module: String,
  val version: String,
  val url:String
  )