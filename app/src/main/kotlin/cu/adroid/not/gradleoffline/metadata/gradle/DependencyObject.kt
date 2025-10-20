package cu.adroid.not.gradleoffline.metadata.gradle

data class DependencyObject(val group:String,
                            val module:String,
                            val version:VersionObject,
                            val excludes: MutableList<Exclude>,
                            val reason: String,
                            val attributes: MutableMap<String, String>,
                            val requestedCapabilities: MutableList<CapabilityObject>,
                            val endorseStrictVersions: Boolean,
                            val thirdPartyCompatibility: TPCObject)