package cu.adroid.not.gradleoffline.metadata.gradle

import com.google.gson.GsonBuilder
import cu.adroid.not.gradleoffline.metadata.BaseConstraint
import cu.adroid.not.gradleoffline.metadata.BaseDependencyMapEntry
import cu.adroid.not.gradleoffline.metadata.BaseExclude
import cu.adroid.not.gradleoffline.metadata.BaseLibraryDependency
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Gradle module metadata v1.1
 * as stated in
 * (@link https://github.com/gradle/gradle/blob/master/platforms/documentation/docs/src/docs/design/gradle-module-metadata-latest-specification.md)
 *
 **/
data class GradleModuleMetadata(
  val formatVersion: String="1.1",
  val component: GradleComponent,
  val createdBy: CreationSource?,
  val variants: MutableList<Variant>?
): BaseDependencyMapEntry(){

    override fun getPomFile(): String = "${component.module}-${component.version}.pom"


    override fun getModuleUrl(repo: String): String = "${getUrl(repo)}/${getModuleFile()}"

    override fun getPomUrl(repo: String): String = "${getUrl(repo)}/${getPomFile()}"

    override fun getModuleFile(): String = "${component.module}-${component.version}.module"

    override fun getUrl(repo: String): String =
        (if (repo.last() == '/') repo else "$repo/") +
            "${component.module.replace('.','/')}/"+ component.version

    override fun getLibMavenAMetadataUrl(repo: String): String =
        (if (repo.last() == '/') repo else "$repo/") +
            "${component.module.replace('.','/')}/maven-metadata.xml"

    override fun getLibDependencies(): List<BaseLibraryDependency> {
        val baseDependencies: MutableList<BaseLibraryDependency> =mutableListOf()
        if (variants!=null) {
            val a=mutableSetOf<String>()
            for (i in variants.filter { it.attributes?.get("org.gradle.usage") == "java-runtime"||it.attributes?.get("org.gradle.usage")=="kotlin-runtime" }) {
                if (i.dependencies!=null) {
                    for (dep in i.dependencies) {
                        var excludes = listOf<BaseExclude>()
                        if (dep.excludes.isNotEmpty()) {
                            excludes = dep.excludes.map { BaseExclude(it) }
                        }
                        if (a.contains(dep.group+":"+dep.module+":"+dep.version))
                            continue
                        a.add(dep.group+":"+dep.module+":"+dep.version)
                        baseDependencies.add(BaseLibraryDependency(dep,excludes))
                    }
                }
                if (i.availableAt!=null){
                    baseDependencies.add(BaseLibraryDependency(i.availableAt))
                }
            }
        }
        return baseDependencies
    }

    override fun getLibDependenciesConstraints(): List<BaseConstraint> {
        val baseConstraint: MutableList<BaseConstraint> =mutableListOf()
        if (variants!=null) {
            val a=mutableSetOf<String>()
            for (i in variants.filter { it.attributes?.get("org.gradle.usage") == "java-runtime"||it.attributes?.get("org.gradle.usage")=="kotlin-runtime" }) {
                if (i.dependencyConstraints!=null) {
                    for (dep in i.dependencyConstraints) {
                        if (a.contains(dep.group+":"+dep.module+":"+dep.version))
                            continue
                        a.add(dep.group+":"+dep.module+":"+dep.version)
                        baseConstraint.add(BaseConstraint(dep))
                    }
                }
            }
        }
        return baseConstraint
    }

  override fun getMissingFiles(repoPath: String): List<String> {
    TODO("Not yet implemented")
  }


  override fun getLibUrl(repo: String): String = getUrl(repo)+component.module+"-"+getLibExtensionPart()+component.version+getLibExtension()

  override fun getLibExtraFiles(repo: String): List<String> {
      return if(getLibExtensionPart().isEmpty()){
          listOf(
              getLibUrl(repo)+".sha1",
              getPomUrl(repo)+".sha1",
              getModuleUrl(repo)+".sha1",
              getUrl(repo)+component.module+"-sources-"+component.version+getLibExtension(),
              getUrl(repo)+component.module+"-sources-"+component.version+getLibExtension()+".sha1",
              getUrl(repo)+component.module+"-javadoc-"+component.version+getLibExtension()+".sha1",
              getUrl(repo)+component.module+"-javadoc-"+component.version+getLibExtension(),
          )
      }else listOf()
  }

  //TODO should do something here?
  // as gradle has the direct file and link i don't think so
  override fun getLibExtensionPart(): String = ""

  override fun getLibExtension(): String = variants?.first {
      it.attributes?.get("org.gradle.libraryelements") != null
  }?.attributes?.get("org.gradle.libraryelements")!!

  val isPomParentLike: Boolean get(){
      return (variants?.any { it.availableAt != null
        && !(it.files != null &&
        (it.attributes?.get("org.gradle.libraryelements") == "jar"||
        it.attributes?.get("artifactType") == "org.jetbrains.kotlin.klib"||
        it.attributes?.get("org.gradle.libraryelements") == "aar")
        &&
        it.attributes["org.gradle.category"] == "library")
      } == true)
  }

  fun hasMissingFiles()=(variants?.any { it.availableAt == null
      && (it.files != null &&
      (it.attributes?.get("org.gradle.libraryelements") == "jar"||
        it.attributes?.get("artifactType") == "org.jetbrains.kotlin.klib"||
        it.attributes?.get("org.gradle.libraryelements") == "aar")
      && it.attributes["org.gradle.category"] == "library")
    } == true)

  companion object{

      @Throws(IOException::class)
      fun loadFromFile(modFile: File): GradleModuleMetadata{
          return getGradleMetadataFromStream(modFile.inputStream())
      }

      fun getGradleMetadataFromStream
              (stream: InputStream): GradleModuleMetadata{
          val moduleFile = stream.reader().readText()
          val moduleMetadata = GsonBuilder().setPrettyPrinting().create()
              .fromJson(moduleFile, GradleModuleMetadata::class.java)
          return moduleMetadata
      }
  }
}

