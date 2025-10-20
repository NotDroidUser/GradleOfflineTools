package cu.adroid.not.gradleoffline.metadata

import okhttp3.OkHttpClient

abstract class BaseDependencyMapEntry{

    abstract fun getPomFile():String
    abstract fun getModuleFile():String
    abstract fun getUrl(repo:String):String
    abstract fun getLibUrl(repo:String):String
    abstract fun getLibExtraFiles(repo: String):List<String>
    abstract fun getLibExtension():String

    abstract fun getPomUrl(repo:String):String
    abstract fun getModuleUrl(repo:String):String
    abstract fun getLibExtensionPart(): String
    abstract fun getLibMavenAMetadataUrl(repo: String):String
    /**
     * Gives only the pom/module file links and also the exclusions
     * */
    abstract fun getLibDependencies(): List<BaseLibraryDependency>
    abstract fun getLibDependenciesConstraints():List<BaseConstraint>

    abstract fun getMissingFiles(repoPath:String):List<String>

    /**
     * Gives only the version file of the library
     * */
    open fun getLibVersionsUrl(repo: String,client: OkHttpClient){

    }

}
