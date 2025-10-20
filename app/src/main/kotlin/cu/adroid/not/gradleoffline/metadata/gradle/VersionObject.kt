package cu.adroid.not.gradleoffline.metadata.gradle

data class VersionObject(val requires:String?,
                         val prefers:String?,
                         val strictly:String?,
                         val rejects:String?){

    fun getRequiredVersion():String? =strictly ?: prefers ?: requires
}
