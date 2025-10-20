package cu.adroid.not.gradleoffline.metadata

import cu.adroid.not.gradleoffline.metadata.gradle.AvailableAtObject
import cu.adroid.not.gradleoffline.metadata.gradle.DependencyObject
import cu.adroid.not.gradleoffline.metadata.maven.Dependency

data class BaseLibraryDependency(val group:String,
                       val library:String,
                       val version:String? =null ,
                       val exclusions: List<BaseExclude>){
    constructor(dep: Dependency, exclusions: List<BaseExclude>):
        this(dep.groupId,dep.artifactId,dep.version,exclusions)
    constructor(dep: DependencyObject, exclusions: List<BaseExclude>):
        this(dep.group,dep.module,dep.version.getRequiredVersion(),exclusions)
    constructor(dep: AvailableAtObject):
        this(dep.group, dep.module, dep.version, listOf())
}
