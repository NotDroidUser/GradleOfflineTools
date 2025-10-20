package cu.adroid.not.gradleoffline

import com.google.gson.GsonBuilder
import cu.adroid.not.gradleoffline.Configuration.Companion.Config
import cu.adroid.not.gradleoffline.metadata.gradle.GradleModuleMetadata
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.io.IOException
import java.net.URI

data class Kradle(val group:String,
                  val library:String,
                  val version:String,
                  var hasPom:Boolean=false,
                  var hasModule:Boolean=false,
                  var hasSource:Boolean=false,
                  var hasJavadoc:Boolean=false,
                  var hashes: MutableMap<String,String> = mutableMapOf() ){

    fun getURL(repo:String,
               ext:String,fileType: FileType= FileType.default):String{
        return "$repo${if (repo.last() != '/') "/" else ""}" +
            "${this.group.replace(".","/")}/" +
            "${this.library}/${this.version}/${this.library}" +getFileTypeNamePart(fileType) +
            "-${this.version}.$ext"
    }

    companion object{
        fun getURL(repo:String,
                   kradle:Kradle,
                   ext:String,fileType: FileType= FileType.default)=
            kradle.getURL(repo,ext,fileType)
        fun kradleOrder(offlinePath:String = Config.offlinePath,
                        gradlePath:String = Config.gradleCachePath,
                        updateProgress:(Float,String)->Unit={_,_->},
                        printIt:(String)->Unit={print(it)}){
            val fs=File.separator
            val path = File(gradlePath)
            val paths = path.listFiles { file -> file.isDirectory }?.toMutableList() ?: mutableListOf<File>()
            val kradle = mutableMapOf<String,Kradle>()
            val filesToOrder= mutableListOf<File>()

            updateProgress(-1f,"Scaning Files")
            while (paths.isNotEmpty()){
                val at = paths.removeAt(0)
                val (files,dirs)= at.listFiles()?.partition { it.isFile }
                    ?: (mutableListOf<File>() to mutableListOf<File>())
                if (files.isNotEmpty() && dirs.isEmpty()){
                    filesToOrder.addAll(files)
                }else if (dirs.isNotEmpty()){
                    paths.addAll(dirs)
                }
            }
            val offline = File(offlinePath).also {
                if (!it.exists()) {
                    it.mkdir()
                }
            }
            filesToOrder.forEachIndexed { index, orderFile ->
                updateProgress((index.toFloat()/filesToOrder.size.toFloat())/2.0f,"Making Repo...")
                val sha1 = orderFile.parentFile.name
                val sha1Hex = DigestUtils.sha1Hex(orderFile.inputStream())
                if (!(sha1Hex.contains(sha1)||sha1Hex.contentEquals(sha1))){
                    printIt("$sha1Hex!=$sha1")
                    printIt("Error File ${orderFile.name} has bad sha1 so its corrupted\n")
                    return@forEachIndexed
                }
                val version = orderFile.parentFile.parentFile.name
                val library = orderFile.parentFile.parentFile.parentFile.name
                val group = orderFile.parentFile.parentFile.parentFile.parentFile.name
                val mavenLibPath = "${group.replace(".", fs)}$fs$library$fs$version$fs"
                //pom file is equivalent to a module one and gradle prefers the latest one
                val offGradle = kradle.getOrPut(mavenLibPath) {
                    Kradle(group, library, version,
                        hasPom =  orderFile.extension == "pom",
                        hasModule = orderFile.extension == "module",
                        hasSource = orderFile.name.endsWith("-source"),
                        hasJavadoc = orderFile.name.endsWith("-javadoc"))
                }.let { old->
                    old.copy(
                        hasPom = old.hasPom||orderFile.extension == "pom",
                        hasModule = old.hasModule||orderFile.extension == "module",
                        hasSource = old.hasSource||orderFile.name.endsWith("-source"),
                        hasJavadoc = old.hasJavadoc||orderFile.name.endsWith("-javadoc")
                    )
                }
                val file= File(File(offline, mavenLibPath).also { it.mkdirs() },orderFile.name)
                val fileSha1Repo= File(File(offline, mavenLibPath).also { it.mkdirs() },orderFile.name+".sha1")
                if ((file.exists()&&fileSha1Repo.exists()&&file.isFile&&fileSha1Repo.readText()==sha1Hex)||
                    (file.exists()&&file.isFile&& DigestUtils.sha1Hex(file.inputStream()) ==sha1Hex)){
                    if (Config.verbose) printIt("The file ${orderFile.name} are the same, in offline repo " +
                        "${ if(Config.moveFiles) ", deleting the gradle cached".also { orderFile.delete() }
                        else ""}\n")
                    if (Config.removeEmptyDirs) removeEmptyParents(orderFile,path,printIt)
                }else{
                    try {
                        orderFile.copyTo(file)
                        if (Config.moveFiles) orderFile.delete()
                        if (Config.verbose) {
                            printIt ("${orderFile.name}${
                                if (Config.moveFiles)
                                    " moved to "
                                else " copied to "
                                    + file.parent}\n")
                        }
                        if (Config.removeEmptyDirs) {
                            removeEmptyParents(orderFile.parentFile,path,printIt)
                        }
                    }catch (e: IOException){
                        printIt("An io error has occurred\n")
                    }
                }
                if(!fileSha1Repo.exists()){
                    file.createNewFile()
                    fileSha1Repo.writeText(sha1Hex)
                }else if (fileSha1Repo.readText()!=sha1Hex){
                    fileSha1Repo.writeText(sha1Hex)
                }
                kradle[mavenLibPath]=offGradle
            }
            for ((index,kradleLib) in kradle.values.withIndex()){
                val mavenPathFile = File(offline, kradleLib.libraryPath).also { it.mkdirs() }
                val pom= File(mavenPathFile,kradleLib.library+"-"+kradleLib.version+".pom")
                val module= File(mavenPathFile,kradleLib.library+"-"+kradleLib.version+".module")
                var isPomDownloaded=false
                if(!(kradleLib.hasPom ||kradleLib.hasModule|| pom.exists()||module.exists())){
                    //todo download pom && module bc there is no way to know what dependencies needs this library
                    for (repo in Config.onlineRepos.split("\n")){
                        var theRepoHasConnection=false
                        try {
                            urlStream(URI(repo).resolve("robots.txt").toString())
                            theRepoHasConnection=true
                        }catch (_: Exception){ }
                        if (theRepoHasConnection){
                            try {
                                updateProgress(
                                    0.5f + ((index.toFloat() / kradle.size.toFloat()) / 2f),
                                    "Trying to download ${getURL(repo, kradleLib, "pom")} missing pom file"
                                )
                                isPomDownloaded = downloadFile(repo, kradleLib, offlinePath, "pom", printIt)
//                      if (isPomDownloaded) downloadFile(repo,kradleLib,offlinePath,"pom.sha1",printIt)
                                val isModuleDownloaded =
                                    downloadFile(repo, kradleLib, offlinePath, "module", printIt)
//                          if (isModuleDownloaded) downloadFile(repo, kradleLib, offlinePath, "module.sha1", printIt)
                                if (isPomDownloaded || isModuleDownloaded)
                                    break
                            } catch (e: Exception) {
                                printIt(e.stackTraceToString())
                                continue
                            }
                        }
                    }
                    if(!isPomDownloaded){
                        printIt("Warning:${kradleLib.library}-${kradleLib.version} library doesn't has pom\n")
                    }else{
                        printIt("Downloaded ${kradleLib.library}-${kradleLib.version} pom or module file")
                    }
                }
                if (module.exists()){
                    val gradleMeta=GsonBuilder().setPrettyPrinting().create().fromJson(module.readText(),
                        GradleModuleMetadata::class.java)
                    if (gradleMeta.variants?.isNotEmpty()==true) {
                        val toRename = gradleMeta.variants.filter {
                            it.files?.any { file -> !file.url.contains(file.name) }==true
                        }
                        if (toRename.isNotEmpty()) {
                            for (variant in toRename) {
                                if (!variant.files.isNullOrEmpty()) {
                                    for (file in variant.files) {
                                        val pathToFile =
                                            File(File(offline, kradleLib.libraryPath), file.name)
                                        val pathToMove =
                                            File(File(offline, kradleLib.libraryPath), file.url)
                                        printIt(file.name)
                                        printIt(file.url)
                                        printIt("${pathToMove.path!=pathToFile.path}")
                                        printIt("")
                                        if (pathToFile.exists() && !pathToMove.exists() && pathToMove.path!=pathToFile.path ) {
                                            pathToFile.copyTo(pathToMove)
                                            pathToFile.delete()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else if (pom.exists()){
                    //read pom file
                    //todo this just for see there is any unprocessed dependency as pom doesn't rename files
                }
                //todo then read the pom/module file if the library has a name-changed file, rename it,
                // then search if some has been left without it and download
                updateProgress(0.5f+((index.toFloat()/kradle.size.toFloat())/2f),
                    if(isPomDownloaded){
                        "Downloaded Missing Pom/Module files"
                    }else{
                        "Updating needed files in repo..."
                    }
                )
            }
        }


        fun removeEmptyParents(parentFile: File, topDir: File,printIt:(String)->Unit) {
            var parent =parentFile.parentFile
            while (parent.path.contains(topDir.path)&&
                topDir.path!=parent.path&&
                (parent.listFiles()?: arrayOf(parent)).isEmpty()) {
                printIt(parent.path+" deleted beacuse is empty")
                parent.delete()
                parent=parent.parentFile
            }
        }
    }

    val libraryPath: String get(){
        return group.replace(".",File.separator)+File.separator+library+File.separator+version+File.separator
    }
    //todo makeUseOf
    val pomPath: String get(){
        return "$libraryPath$library-$version.pom"
    }
    val modulePath: String get(){
        return "$libraryPath$library-$version.module"
    }
    val sourcePath: String get(){
        return "$libraryPath$library-$version-sources.jar"
    }
    val javadocPath: String get(){
        return "$libraryPath$library-$version-sources.jar"
    }
}

///http://nexus.uclv.edu.cu/repository/dl.google.com/dl/android/maven2/com/google/testing/platform/core-proto/maven-metadata.xml




