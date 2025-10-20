package cu.adroid.not.gradleoffline.metadata

import cu.adroid.not.gradleoffline.metadata.gradle.DependencyConstraintObject
import cu.adroid.not.gradleoffline.metadata.gradle.DependencyObject
import cu.adroid.not.gradleoffline.metadata.maven.Dependency

data class BaseConstraint(val group:String,
                          val library:String,
                          val version:String? =null){
    constructor(dep: Dependency):
        this(dep.groupId,dep.artifactId)
    constructor(dep: DependencyConstraintObject):
        this(dep.group,dep.module,dep.version.getRequiredVersion())
}
