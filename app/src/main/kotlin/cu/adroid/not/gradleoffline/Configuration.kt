package cu.adroid.not.gradleoffline

import com.google.gson.GsonBuilder
import cu.adroid.not.gradleoffline.gui.utils.VerboseObject
import java.io.File
import java.io.FileReader
import java.util.Date

class Configuration{
  var moveFiles=false
  var removeEmptyDirs=false
  var verbose= false
  var logger=true
  var debugUI=false

  var os :String = if (isWindows)  "Windows" else {
    if (isUnix) "Unix" else "Unknown"
  }
  var offlinePath: String = if (isUnix) {
      File(System.getenv("HOME"),"/.m2").path
  }
  else{
    File(System.getenv("USERPROFILE"), ".m2").path
  }
  var gradleCachePath: String = if (isUnix) {
    System.getenv("HOME")+"/.gradle/caches/modules-2/files-2.1"
  }
  else{
    File(System.getenv("USERPROFILE"), ".gradle\\caches\\modules-2\\files-2.1").path
  }
  var repoPath: String =if (isUnix) {
    System.getenv("HOME")+"/.m2"
  }
  else{
    File(System.getenv("USERPROFILE"), ".m2").path
  }
  var onlineRepos="https://repo1.maven.org/" +
      System.lineSeparator() +
      "http://maven.google.com/"
  var lastUpdate:Long = Date().time

    companion object{
      val isWindows = System.getenv("PATH").contains(":\\")
      val isUnix = System.getenv("PATH").contains(Regex("/usr.*|/home.*|/var.*|/run/media.*|/Users.*"))
      var Config = Configuration()
      @Suppress("ConstPropertyName")
      const val twentyFourHours = (1 * 24 * 60 * 60 * 1000)

      fun getConfig() {
          val file = File("config.txt")
          if (file.exists()) {
              Config =
                  GsonBuilder().setPrettyPrinting().create().fromJson(FileReader(file), Configuration::class.java)?: Config
          }

          if (isWindows && Config.os != "Windows") {
              Config = Configuration().apply {
                os = "Windows"
              }
              saveConfig()
          }
          if (isUnix && Config.os != "Unix") {
              Config = Configuration().apply {
                os = "Unix"
              }
              saveConfig()
          }
          VerboseObject.verboseDialog.value=Config.verbose
      }

      fun saveConfig(){
          val file = File("config.txt")

          if (file.exists()) {
              file.delete()
          }
          VerboseObject.verboseDialog.value=Config.verbose
          file.createNewFile()
          val bufferedWriter = file.bufferedWriter()
          bufferedWriter.write(GsonBuilder().setPrettyPrinting().create().toJson(Config, Configuration::class.java))
          bufferedWriter.flush()
          bufferedWriter.close()
      }
    }
}
