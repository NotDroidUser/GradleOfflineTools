package cu.adroid.not.gradleoffline.metadata

import cu.adroid.not.gradleoffline.metadata.gradle.Exclude
import cu.adroid.not.gradleoffline.metadata.maven.Exclusion

class BaseExclude(val group:String,val library:String){

    constructor(ex: Exclusion):this(ex.groupId,ex.artifactId)
    constructor(ex: Exclude):this(group = ex.group,ex.module)
}
