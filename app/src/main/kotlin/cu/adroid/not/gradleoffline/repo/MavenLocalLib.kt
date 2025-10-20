package cu.adroid.not.gradleoffline.repo

import cu.adroid.not.gradleoffline.Configuration
import java.io.File
import kotlin.math.max

data class MavenLocalLib(var name:String,
                         var groupId:String,
                         var versions:MutableList<String>){

  fun getLibraryRepoPathFile() = File(File(Configuration.Config.repoPath,groupId.replace(".",File.separator)),name)
  fun getLibVersionPathFile(version: String) = File(getLibraryRepoPathFile(),version)

  fun getPomFile(version: String): File {
    return File(getLibVersionPathFile(version),"$name-$version.pom")
  }
  fun hasPom(version: String): Boolean {
    return getPomFile(version).exists()
  }
  fun hasModule(version: String): Boolean {
    return getModuleFile(version).exists()
  }
  fun getModuleFile(version: String): File {
    return File(getLibVersionPathFile(version),"$name-$version.module")
  }
  fun getSources(version: String): File {
    return File(getLibVersionPathFile(version),"$name-$version-sources.jar")
  }
  fun getJavadoc(version: String): File {
    return File(getLibVersionPathFile(version),"$name-$version-javadoc.jar")
  }
  fun hasSources(version: String): Boolean {
    return getSources(version).exists()
  }
  fun hasJavadoc(version: String): Boolean {
    return getJavadoc(version).exists()
  }

  fun getOthers(version: String): Array<File>? = getLibVersionPathFile(version).listFiles { file->
    Regex(".sha1$|.sha256$|.sha512$|.md5$|.pom$|.module$").find(
      file.name,
      startIndex = max(0, file.name.length - 7)
    )?.groupValues.isNullOrEmpty()&&file.name!=getSources(version).name&&file.name!=getJavadoc(version).name
  }




}
