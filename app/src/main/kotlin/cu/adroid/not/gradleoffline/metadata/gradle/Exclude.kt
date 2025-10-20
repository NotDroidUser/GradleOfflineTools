package cu.adroid.not.gradleoffline.metadata.gradle

/**
 * group can contain wildcard(*) to be used as regex for excluding a entire group
 * module also can contain it
 * If them two are wildcards then exclude all transitive dependencies
 * TODO make regex from this for the children dependency
 * */
data class Exclude(val group:String,
                   val module:String)