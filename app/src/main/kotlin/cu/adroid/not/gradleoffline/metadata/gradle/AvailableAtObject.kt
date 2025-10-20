package cu.adroid.not.gradleoffline.metadata.gradle

/**
 * The url is relative to the module,
 * so if you try to resolve with this module URI you should get the file path
 **/
data class AvailableAtObject(val url:String,
                             val group:String,
                             val module:String,
                             val version:String)