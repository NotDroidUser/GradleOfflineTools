package cu.adroid.not.gradleoffline.repo

import com.google.gson.GsonBuilder
import cu.adroid.not.gradleoffline.Configuration.Companion.Config
import cu.adroid.not.gradleoffline.Configuration.Companion.saveConfig
import cu.adroid.not.gradleoffline.Configuration.Companion.twentyFourHours
import cu.adroid.not.gradleoffline.exceptions.PackageDontExist
import java.io.File
import java.io.FileReader
import java.util.Date

data class MavenLocalRepository(
  var path: String,
  var libs: MutableMap<String, MavenLocalLib>
) {

  companion object {

    var repo: MavenLocalRepository = MavenLocalRepository("", mutableMapOf())

    fun loadRepo(
      updateItAf: Boolean = false,
      onProgress: (Float) -> Unit,
      onProgress2: (String) -> Unit = {}
    ) {
      val file = File("repo.json")
      if (file.exists() and !updateItAf and (Config.lastUpdate + twentyFourHours > Date().time)) {
        val jsonFileReader = FileReader(file)
        repo = GsonBuilder().setPrettyPrinting().create()
          .fromJson(jsonFileReader, MavenLocalRepository::class.java) ?: repo
        jsonFileReader.close()
      } else if (updateItAf || (Config.lastUpdate + twentyFourHours < Date().time)) {
        repo = makeMavenRepo(barProgress = onProgress, textExtraProgress = onProgress2)
        saveRepo()
        Config.lastUpdate = Date().time
        saveConfig()
      }
    }

    fun saveRepo() {
      val file = File("repo.json")

      if (file.exists()) {
        file.delete()
      }
      file.createNewFile()

      val bufferedWriter = file.bufferedWriter()
      bufferedWriter.write(
        GsonBuilder().setPrettyPrinting().create().toJson(repo, MavenLocalRepository::class.java)
      )
      bufferedWriter.flush()
      bufferedWriter.close()
    }
    
    fun makeMavenRepo(
      repoPath: File = File(Config.repoPath),
      barProgress: (Float) -> Unit = {},
      textExtraProgress: (String) -> Unit = {},
      printIt: (String) -> Unit = { print(it) }
    ): MavenLocalRepository {
      val files = arrayListOf<File>()
      files.addAll(repoPath.listFiles() ?: arrayOf())
      val repository = MavenLocalRepository(repoPath.path, mutableMapOf())
      var counter = 0
      val fs = File.separator
      val setOfLibs=mutableSetOf<String>()
      while (files.isNotEmpty()) {
        val localFile = files.removeFirst()
        if (localFile.isDirectory) {
          if ((localFile.listFiles { file ->
              file.isFile &&
                (file.name.endsWith(".pom") ||
                  file.name.endsWith(".module"))
            } ?: arrayOf()).isNotEmpty()) {
            setOfLibs.add(localFile.parentFile.path)
          }
          files.addAll(localFile.listFiles() ?: arrayOf())
        } else if (localFile.isFile && (localFile.extension == "pom" || localFile.extension == "module")) {
          ///media/android/ToshibaExt/MavenAndroidOfflineRepoUnited/repository/[android/arch/core/common/]1.0.0/common-1.0.0-sources.jar
          val libraryFolder = localFile.parentFile.parentFile
          val versionsFolders = arrayListOf<File>().apply {
            addAll((libraryFolder.listFiles { file ->
              file.isDirectory && file.listFiles()?.all { allFiles-> allFiles.isFile } ?: false
            } ?: arrayOf()).toList())
          }
          val versions = arrayListOf<String>()
          versionsFolders.forEach { versions.add(it.name) }
          val toRemove = versionsFolders
          while (toRemove.isNotEmpty()) {
            val removeFile = toRemove.removeLast()
            if (removeFile.isDirectory) {
              toRemove.addAll(removeFile.listFiles() ?: arrayOf())
            }
            files.remove(removeFile)
          }
          //[android/arch/core/common]
          val subSequence = libraryFolder.path
            .subSequence(repoPath.path.length + 1, libraryFolder.path.length)
            .split(fs)
            .toMutableList()
//check first if the package is in repo then keep finding things
          val name = subSequence.removeLast()
          var packages = subSequence.removeLast().also {
            subSequence.reverse()
          }
          subSequence.forEach { if (it.isNotBlank()) packages = "$it.$packages" }
          val mavenLib = repository.libs["$packages:$name"]
          if (mavenLib == null) {
            repository.libs["$packages:$name"] = MavenLocalLib(name, packages, versions)
          } else {
            if (Config.verbose) {
              printIt("\n")
              printIt("THIS is an error, it shouldn't should exist ._.\n")
              printIt("$packages:$name:$versions\n")
              printIt("make a issue on git containing this")
            }
          }
          counter++
        }
        val size=setOfLibs.size
        if (size != 0 && counter != 0) {
          barProgress(java.lang.Float.min(1f, (counter).toFloat() / (size).toFloat()))
          textExtraProgress("$counter$fs$size=${(counter).toFloat() / (size).toFloat()}")
        }
      }
      return repository
    }
  }

  @Throws(PackageDontExist::class)
  fun getLib(packageName: String): MavenLocalLib {
    return libs[packageName] ?: throw PackageDontExist(packageName, "Package was not found")
  }

  fun searchLib(some: String): MutableList<MavenLocalLib> {
    return libs.filter { it.key.contains(some) }.values.toMutableList()
  }


}

