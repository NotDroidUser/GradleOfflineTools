package cu.adroid.not.gradleoffline

import cu.adroid.not.gradleoffline.Configuration.Companion.Config
import cu.adroid.not.gradleoffline.Configuration.Companion.getConfig
import cu.adroid.not.gradleoffline.Kradle.Companion.getURL
import cu.adroid.not.gradleoffline.exceptions.ConnectionError
import cu.adroid.not.gradleoffline.exceptions.PackageDontExist
import cu.adroid.not.gradleoffline.metadata.maven.POM
import cu.adroid.not.gradleoffline.repo.MavenLocalRepository.Companion.loadRepo
import cu.adroid.not.gradleoffline.repo.MavenLocalRepository.Companion.repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.codec.digest.DigestUtils
import java.io.*
import java.net.ConnectException
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws
import java.io.File.separatorChar as ps


enum class FileType{
  default,
  javadoc,
  sources
}


fun getRepoFile(repoPath: File, kradle: Kradle):File{
  val (groupId:String,packageName:String,version:String,_)=kradle
  return File(repoPath, "${groupId.replace('.',ps)}$ps$packageName$ps$version$ps")
}


fun main() {
//  val onlineRepo="http://nexus.uclv.edu.cu/repository/maven2/"
  //val onlineRepo="http://0.0.0.0:8080/repository/"
  val onlineRepos= Config.onlineRepos + "${System.lineSeparator()}https://plugins.gradle.org/m2/"
  //val implementation="implementation \"com.android.tools.build:builder:8.5.0\""
  val implementation="implementation \"com.github.johnrengelman:shadow:8.1.1\""
  getConfig()
  loadRepo(onProgress = {})
  val parts = implementation.split("\"")[1].split(":")
  val kradle = Kradle(parts[0], parts[1], parts[2])
  val scope = CoroutineScope(Dispatchers.IO)
  downloadImplementation(onlineRepos,kradle,File("/home/fedora/.m2/"),scope)
  while (scope.isActive){
    //wait
  }
}

@Throws(Exception::class)
fun downloadFile(repo: String, i: Kradle, offlinePath: String, ext: String,printIt: (String) -> Unit): Boolean {
  try{
    val (fileStream,response) = urlStream(getURL(repo,i, ext))
    saveToRepo(repo,File(offlinePath),fileStream,i,ext)
    response.close()
  } catch (e:NullPointerException){
    printIt(e.stackTraceToString())
    return false
  } catch (e: ConnectionError){
    println(getURL(repo,i,ext)+" has an error\n\n"+e.stackTraceToString())
    return false
  }catch (e:Exception){
    throw e
  }
  return true
}

@Throws(NullPointerException::class, ConnectionError::class, Exception::class)
fun urlStream(url:String): Pair<InputStream, Response> {

  val client = OkHttpClient.Builder()
    .connectTimeout(25,TimeUnit.SECONDS)
    .callTimeout(15,TimeUnit.SECONDS)
    .writeTimeout(60,TimeUnit.SECONDS)
    .readTimeout(60,TimeUnit.SECONDS)
    .build()
  val request = Request.Builder().url(url)
    .get().header("User-Agent",
      "Mozilla/5.0 (X11; Linux x86_64; rv:105.0) Gecko/20100101 Firefox/105.0").build()
  val response=client.newCall(request).execute()
  val code = response.code
  if(code == 200 ){
    val steam= response.body?.byteStream()!!
    return steam to response
  }else{
    response.close()
    throw ConnectionError(code)
  }
}

@Throws(IOException::class, NullPointerException::class)
fun saveToRepo(onlineRepo:String, repoPath: File, stream: InputStream,
               kradle: Kradle, ext:String, fileType: FileType= FileType.default,
               printIt:(String)->Unit={} ): FileInputStream {
  val (_: String, packageName: String, version: String,_)=kradle
  val folder=getRepoFile(repoPath,kradle)
  if (!folder.exists())
    folder.mkdirs()
  val file=File(folder, packageName + getFileTypeNamePart(fileType)+ "-$version.$ext")
  
  var onlineSha1=""

  try {
      val (sha1Steam,response) = urlStream(getURL(onlineRepo,kradle, "$ext.sha1",fileType))
      onlineSha1=String(sha1Steam.readAllBytes()).also {
        response.close()
      }
  }catch (_: Exception ){  }

  val fileSHA1=File(folder, packageName +
      getFileTypeNamePart(fileType)+
      "-$version.$ext.sha1")
  val localSha1=if(fileSHA1.exists()){
    fileSHA1.readText()
  } else if(file.exists()){
    DigestUtils.sha1Hex(file.inputStream())
  }else {
    ""
  }

  if(localSha1==onlineSha1&&file.exists()&&DigestUtils.sha1Hex(file.inputStream())==onlineSha1){
    printIt("It's the same file online and local, skiping ${kradle.library+"-"+kradle.version}.$ext")
    stream.close()
    return file.inputStream()
  }
  if (onlineSha1!=localSha1&&onlineSha1.isNotEmpty()){
    fileSHA1.writeText(onlineSha1)
  }
  printIt("Saving file ${file.path}")
  printIt("SHA1:$onlineSha1")
  val out=file.outputStream()
  stream.copyTo(out)
  out.close()
  out.flush()
  stream.close()
  return file.inputStream()
}

fun getFileTypeNamePart(fileType: FileType): String =
  when (fileType) {
    FileType.sources -> {
      "-sources"
    }
    FileType.javadoc-> {
      "-javadoc"
    }
    else ->{
      ""
    }
  }

var taskCounter=0

//todo do same implementation but with gradle source
fun downloadImplementation(
  onlineRepos: String,
  kradle: Kradle,
  repoPath: File,
  scope: CoroutineScope
) {
  for (onlineRepo in onlineRepos.split(System.lineSeparator())){
    try{
      if(kradle.version.contains(Regex("(\\$\\{.*\\})|\\[.*,.*]"))||kradle.library.contains("junit")){
        kradle.version
        return
      }

      var (pom,response) = urlStream(getURL(onlineRepo, kradle, "pom"))
      
      println("Downloading ${kradle.library} package")
      //todo change this sh*t
      pom = saveToRepo(onlineRepo,repoPath, pom, kradle, "pom")
      response.close()
      val POMFile = POM.getPropertiesByPOMStream(pom)
      pom.close()

      if (POMFile!=null){
        if(POMFile.packaging=="bundle"){
          POMFile.packaging="jar"
        }
        if(POMFile.packaging!="pom"){
          val (jar,responseJar) = urlStream(getURL(onlineRepo,kradle,POMFile.packaging))
          saveToRepo(onlineRepo,repoPath,jar,kradle,POMFile.packaging).also {
            responseJar.close()
          }
          try {
            val (sources,resoponseSource) = urlStream(getURL(onlineRepo,kradle,ext= "jar", fileType = FileType.sources))
            saveToRepo(onlineRepo,repoPath,sources,kradle,"jar", FileType.sources).also {
              resoponseSource.close()
            }
          } catch(_: Exception){ }
        }
        POMFile.dependencies.forEach { i ->
          try {
            if (repo.getLib("${i.groupId}:${i.artifactId}").versions.any { it == i.version }) {
              println(i.artifactId + ":" + i.version + " in repo")
            } else {
              println(i.artifactId + ":" + i.version + " not in repo")
              scope.launch {
                while (taskCounter>8) {
                  Thread.sleep(100)
                }
                taskCounter++
                downloadImplementation(
                  onlineRepos,
                  Kradle(i.groupId, i.artifactId, i.version),
                  repoPath,
                  scope
                )
                taskCounter--
              }
            }
          } catch (e: PackageDontExist) {
            println(i.artifactId + " not in repo")
            scope.launch {
              while (taskCounter>8) {
                Thread.sleep(100)
              }
              taskCounter++
              downloadImplementation(
                onlineRepos,
                Kradle(i.groupId, i.artifactId, i.version),
                repoPath,
                scope
              )
              taskCounter--
            }
          }
        }
        break
      }
    }catch (e:ConnectException){
      //println(e.stackTraceToString())
      println("No connection")
    }catch (e: ConnectionError){
      //println(e.stackTraceToString())
    }
  }
}
