package cu.adroid.not.gradleoffline.metadata.gradle

data class DependencyConstraintObject(val group:String,
                                      val module:String,
                                      val version:VersionObject,
                                      val reason: String,
                                      val attributes: MutableMap<String, String>)

