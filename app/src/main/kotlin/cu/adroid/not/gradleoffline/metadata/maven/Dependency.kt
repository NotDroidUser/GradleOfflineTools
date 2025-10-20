package cu.adroid.not.gradleoffline.metadata.maven


/**
 * See <a href=https://maven.apache.org/pom.html> Pom Reference </a> for full reference
 * as this app don't need all the things that a pom file has maybe some is missing
 * */
data class Dependency(
        val groupId:String,
        val artifactId:String,
        // 3k text about this one in pom about how you re-(format, replace, remake),
        // just don't use in a mine maven repo camp, compare as string case insensitive and live on
        var version:String="[0,999999999999999999999]",
        var type:String="jar",
        var scope:String="compile",
        var exclusions: MutableList<Exclusion> = mutableListOf(),
        var systemPath:String?=null,
        var optional: Boolean=false
        ){



        fun toPomUrl(repoUrl:String): String {
                return repoUrl + if(repoUrl.last()=='/'){ "" } else{"/"} +
                  groupId.replace(".","/")+
                  "/"+artifactId+"/"+version+
                  "/"+artifactId+"-"+version+".pom"
        }
}

