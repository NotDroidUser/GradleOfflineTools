package cu.adroid.not.gradleoffline.metadata.maven

import cu.adroid.not.gradleoffline.metadata.BaseConstraint
import cu.adroid.not.gradleoffline.metadata.BaseDependencyMapEntry
import cu.adroid.not.gradleoffline.metadata.BaseExclude
import cu.adroid.not.gradleoffline.metadata.BaseLibraryDependency
import okhttp3.OkHttpClient
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

data class POM(
        val groupId:String,
        val artifactId:String,
        val version:String,
        var packaging:String="jar",
        var parent:Parent=Parent(),
        var dependencies:List<Dependency>,
        var dependencyManagement:List<Dependency>
    ): BaseDependencyMapEntry() {

    override fun getPomFile(): String = "$artifactId-$version.pom"


    override fun getModuleUrl(repo: String): String = "${getUrl(repo)}/${getModuleFile()}"

    override fun getPomUrl(repo: String): String = "${getUrl(repo)}/${getPomFile()}"

    override fun getModuleFile(): String = "$artifactId-$version.module"

    override fun getUrl(repo: String): String =
        (if (repo.last() == '/') repo else "$repo/") +
            "${groupId.replace('.','/')}/"+ version

    override fun getLibMavenAMetadataUrl(repo: String): String =
        (if (repo.last() == '/') repo else "$repo/") +
            "${groupId.replace('.','/')}/maven-metadata.xml"

    override fun getLibDependencies(): List<BaseLibraryDependency> {
        val baseDependencies: MutableList<BaseLibraryDependency> =mutableListOf()
        for (i in dependencies){
            var excludes = listOf<BaseExclude>()
            if (i.exclusions.isNotEmpty()){
                excludes=i.exclusions.map { BaseExclude(it) }
            }
            baseDependencies.add(BaseLibraryDependency(i,excludes))
        }
        return baseDependencies
    }

    override fun getLibDependenciesConstraints(): List<BaseConstraint> {
        val baseConstraints: MutableList<BaseConstraint> =mutableListOf()
        for (i in dependencyManagement){
            baseConstraints.add(BaseConstraint(i))
        }
        return baseConstraints
    }

    override fun getMissingFiles(repoPath: String): List<String> {
      TODO("Not yet implemented")
    }

  override fun getLibUrl(repo: String): String = getUrl(repo)+artifactId+"-"+getLibExtensionPart()+version+getLibExtension()

    override fun getLibExtraFiles(repo: String): List<String> {
        return if(getLibExtensionPart().isEmpty()){
            listOf(
                getLibUrl(repo)+".sha1",
                getPomUrl(repo)+".sha1",
                getModuleUrl(repo)+".sha1",
                getUrl(repo)+artifactId+"-sources-"+version+getLibExtension(),
                getUrl(repo)+artifactId+"-sources-"+version+getLibExtension()+".sha1",
                getUrl(repo)+artifactId+"-javadoc-"+version+getLibExtension(),
                getUrl(repo)+artifactId+"-javadoc-"+version+getLibExtension()+".sha1",
            )
        }else listOf()
    }


    override fun getLibExtensionPart(): String = when(packaging){
        "test-jar"-> "test-"
        "ejb-client"-> "client-"
        "java-source"-> "sources-"
        else -> ""
    }

    override fun getLibExtension(): String =when(packaging){
        "pom"-> "pom"
        "jar" -> "jar"
        "aar" -> "aar"
        "test-jar"-> "jar"
        "maven-plugin" -> "jar"
        "ejb" -> "jar"
        "ejb-client"-> "jar"
        "war"-> "war"
        "ear"-> "ear"
        "rar"->"rar"
        "java-source"->"jar"
        "javadoc"-> "jar"
        "klib"->"klib"
        else -> packaging
    }

    companion object{

        fun loadFromFile(pom: File): POM? {
            return getPropertiesByPOMStream(pom.inputStream())
        }

        fun getPropertiesByPOMStream(pom: InputStream): POM? {
            val parse = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(pom)
            var pomObject:POM? =null
            try {
                ///todo dont search for the first one, instead search for project children's
                pomObject=POM(
                    groupId=parse.getElementsByTagName("groupId").getFirstChildWithParent("project")?.textContent ?:"",
                    artifactId = parse.getElementsByTagName("artifactId").getFirstChildWithParent("project")?.textContent ?:"",
                    version = parse.getElementsByTagName("version").getFirstChildWithParent("project")?.textContent ?:"",
                    packaging = parse.getElementsByTagName("packaging")?.getFirstChildWithParent("project")?.textContent ?: "jar",
                    dependencies = listOf(),
                    dependencyManagement = listOf()
                )
                try {
                    pomObject.packaging = parse.getElementsByTagName("packaging").item(0).textContent
                }catch (_:NullPointerException){}

                val parentTag= parse.getElementsByTagName("parent")

                if (parentTag.length!=0){
                    (0 until parentTag.length).forEach { index->
                        val element = parentTag.item(index) as Element
                        if (element.parentNode.nodeName=="project"){
                            val parent=Parent()
                            parent.artifactId=element.getFirstElementByTagNameOrNull("artifactId")?.textContent
                            parent.version=element.getFirstElementByTagNameOrNull("version")?.textContent
                            parent.groupId=element.getFirstElementByTagNameOrNull("groupId")?.textContent
                            parent.relativePath=element.getFirstElementByTagNameOrNull("relativePath")?.textContent
                            pomObject.parent=parent
                        }
                    }
                }

                val dependenciesAll =
                    parse.getElementsByTagName("dependencies")
                for( i in 0 until dependenciesAll.length) {
                    val dep = dependenciesAll.item(i) as Element
                    if(dep.parentNode.nodeName=="dependencyManagement"){
                        val dependencies = dep.getElementsByTagName("dependency")
                        val depList = mutableListOf<Dependency>()
                        (0 until dependencies.length).forEach {depIndex->
                            try {
                                val depElement = dependencies.item(depIndex) as Element
                                val dependency = Dependency(
                                    groupId = depElement.getFirstElementByTagName("groupId").textContent,
                                    artifactId = depElement.getFirstElementByTagName("artifactId").textContent,
                                )
                                dependency.version =
                                    depElement.getFirstElementByTagNameOrNull("version")?.textContent
                                        ?: dependency.version
                                dependency.type =
                                    depElement.getFirstElementByTagNameOrNull("type")?.textContent
                                        ?: dependency.type
                                dependency.scope =
                                    depElement.getFirstElementByTagNameOrNull("scope")?.textContent
                                        ?: dependency.scope
                                dependency.systemPath =
                                    depElement.getFirstElementByTagNameOrNull("systemPath")?.textContent
                                dependency.optional =
                                    (depElement.getFirstElementByTagNameOrNull("optional")?.textContent
                                        ?: "") == "true"

                                val exclusions =
                                    depElement.getFirstElementByTagNameOrNull("exclusions")
                                        ?.getElementsByTagName("exclusion")
                                if (exclusions != null && exclusions.length != 0) {
                                    val exList = mutableListOf<Exclusion>()
                                  (0 until exclusions.length).forEach { exIndex ->
                                    val exElement = exclusions.item(exIndex) as Element
                                    val exclusion = Exclusion(
                                      exElement.getFirstElementByTagName("groupId").textContent,
                                      exElement.getFirstElementByTagName("artifactId").textContent
                                    )
                                    exList.add(exclusion)
                                  }
                                    dependency.exclusions = exList
                                }

                                depList.add(dependency)
                            } catch (_: NullPointerException) { }
                        }
                        pomObject.dependencyManagement = depList
                    }
                    else if(dep.parentNode.nodeName=="project"){
                        val dependencies = dep.getElementsByTagName("dependency")
                        val depList = mutableListOf<Dependency>()
                        (0 until dependencies.length).forEach {depIndex->
                            try {
                                val depElement = dependencies.item(depIndex) as Element
                                val dependency = Dependency(
                                    groupId = depElement.getFirstElementByTagName("groupId").textContent,
                                    artifactId = depElement.getFirstElementByTagName("artifactId").textContent,
                                )
                                dependency.version =
                                    depElement.getFirstElementByTagNameOrNull("version")?.textContent
                                        ?: dependency.version
                                dependency.type =
                                    depElement.getFirstElementByTagNameOrNull("type")?.textContent
                                        ?: dependency.type
                                dependency.scope =
                                    depElement.getFirstElementByTagNameOrNull("scope")?.textContent
                                        ?: dependency.scope
                                dependency.systemPath =
                                    depElement.getFirstElementByTagNameOrNull("systemPath")?.textContent
                                dependency.optional =
                                    (depElement.getFirstElementByTagNameOrNull("optional")?.textContent
                                        ?: "") == "true"

                                val exclusions =
                                    depElement.getFirstElementByTagNameOrNull("exclusions")
                                        ?.getElementsByTagName("exclusion")
                                if (exclusions != null && exclusions.length != 0) {

                                    val exList = mutableListOf<Exclusion>()
                                        (0 until exclusions.length).forEach { exIndex ->
                                            val exElement = exclusions.item(exIndex) as Element
                                            val exclusion = Exclusion(
                                            exElement.getFirstElementByTagName("groupId").textContent,
                                            exElement.getFirstElementByTagName("artifactId").textContent
                                            )
                                            exList.add(exclusion)
                                        }
                                    dependency.exclusions = exList
                                }

                                depList.add(dependency)
                            } catch (_: NullPointerException) { }
                        }
                        pomObject.dependencies = depList
                    }
                }
                } catch (_:NullPointerException){}
            return pomObject
        }


        fun NodeList.getFirstChildWithParent(parentName:String):Element?{
            (0 until this.length).forEach { i ->
                if (this.item(i).parentNode.nodeName==parentName){
                    return this.item(i) as Element?
                }
            }
            return null
        }
        fun Element.getFirstElementByTagNameOrNull(tagName: String): Element?{
            try {
                return this.getElementsByTagName(tagName).item(0) as Element?
            }catch (_: Exception){}
            return null
        }
        fun Document.getFirstElementByTagNameOrNull(tagName: String):Element?{
            try {
                return this.getElementsByTagName(tagName).item(0) as Element?
            }catch (_: Exception){}
            return null
        }


        @Throws(NullPointerException::class)
        fun Element.getFirstElementByTagName(tagName:String):Node=this.getElementsByTagName(tagName).item(0)?:throw NullPointerException()

    }

    fun toPom():String{
        var pom = "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "  <groupId>$groupId</groupId>\n" +
                "  <artifactId>$artifactId</artifactId>\n" +
                "  <version>$version</version>\n" +
                "  <packaging>$packaging</packaging>\n" +
                "  <dependencies>\n"
        dependencies.forEach { dependency ->
        pom +=  "    <dependency>\n" +
                "      <groupId>${dependency.groupId}</groupId>\n" +
                "      <artifactId>${dependency.artifactId}</artifactId>\n" +
                "      <version>${dependency.version}</version>\n" +
                //"      <type>${dependency.type}</type>\n" +
                "      <scope>${dependency.scope}</scope>\n" +
                "    </dependency>\n"
        }
        pom+=   "  </dependencies>\n" +
                "</project>"
        return pom

    }


}



