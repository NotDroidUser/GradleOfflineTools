import cu.adroid.not.gradleoffline.metadata.gradle.GradleModuleMetadata
import cu.adroid.not.gradleoffline.metadata.maven.POM
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ProjectFilesTest {

    fun getResourceFile(resource: String): File? =File(this.javaClass.getResource(resource)?.file!!)

    @Test
    fun getPropertiesByPOMFileTestWithoutParent() {
        val pom = getResourceFile("kotlin-gradle-plugin-2.1.21.pom")?.let { POM.loadFromFile(it) }
        assertNotNull(pom)
        if(pom!=null){
            assertEquals(pom.artifactId,"kotlin-gradle-plugin")
            assertEquals(pom.version,"2.1.21")
            assertEquals(pom.packaging,"pom")
            assertEquals(pom.groupId,"org.jetbrains.kotlin")
            assertEquals(pom.dependencies.size,9)
            assertNotNull(pom.parent)
            assertNull(pom.parent.artifactId)
            assertEquals(pom.dependencyManagement.size,1)
            val expected = pom.dependencyManagement[0]
            assertEquals(expected.groupId,"org.jetbrains.kotlin")
            assertEquals(expected.artifactId,"kotlin-gradle-plugins-bom")
            assertEquals(expected.version,"2.1.21")
            assertEquals(expected.type,"pom")
            assertEquals(expected.scope,"import")

            val expected2 = pom.dependencies[0]
            assertEquals(expected2.groupId,"org.jetbrains.kotlin")
            assertEquals(expected2.artifactId,"kotlin-gradle-plugin-api")
            assertEquals(expected2.version,"2.1.21")
            assertEquals(expected2.type,"jar")
            assertEquals(expected2.scope,"compile")
            val exclusions = expected2.exclusions
            assertEquals(exclusions.size,6)
            val exclusion = exclusions[0]
            assertEquals(exclusion.groupId,"org.jetbrains.kotlin")
            assertEquals(exclusion.artifactId,"kotlin-reflect")

        }
    }

    @Test
    fun getPropertiesByPOMFileTestWithParent() {
        val pom = getResourceFile("jackson-core-2.13.5.pom")?.let { POM.loadFromFile(it) }
        assertNotNull(pom)
        if(pom!=null){
            assertEquals(pom.artifactId,"jackson-core")
            assertEquals(pom.version,"2.13.5")
            assertEquals(pom.packaging,"bundle")
            assertEquals(pom.groupId,"com.fasterxml.jackson.core")
            assertNotNull(pom.parent)
            assertNotNull(pom.parent.artifactId)
            assertEquals(pom.parent.groupId,"com.fasterxml.jackson")
            assertEquals(pom.parent.artifactId,"jackson-base")
            assertEquals(pom.parent.version,"2.13.5")
            assertEquals(pom.dependencies.size,0)
            assertEquals(pom.dependencyManagement.size,0)
        }
    }

    @Test
    fun getPropertiesByModuleFileTestThatDontHaveParent() {
        val moduleMetadata = getResourceFile("kotlin-gradle-plugin-2.1.21.module")?.let {
            GradleModuleMetadata.loadFromFile(it)
        }
        assertNotNull(moduleMetadata)
        if(moduleMetadata!=null){
            assertEquals(moduleMetadata.formatVersion,"1.1")
            assertNotNull(moduleMetadata.component)
            assertEquals(moduleMetadata.component.module,"kotlin-gradle-plugin")
            assertEquals(moduleMetadata.component.version,"2.1.21")
            assertEquals(moduleMetadata.component.group,"org.jetbrains.kotlin")
            assertEquals(moduleMetadata.createdBy?.gradle?.version,"8.12.1")
            assertNotNull(moduleMetadata.variants)
            assertEquals(moduleMetadata.variants?.size,20)//a lot of bs but good one
            val variant = moduleMetadata.variants?.get(2)
            assertNotNull(variant)
            assertEquals(variant!!.name,"runtimeElementsWithFixedAttribute")
            assertNotNull(variant.dependencies)
            assertEquals(variant.dependencies?.size,10)
            assertNotNull(variant.attributes)
            assertEquals(variant.attributes!!.size,6)
            assertEquals(variant.attributes!!["org.gradle.category"],"library")
            assertEquals(variant.attributes!!["org.gradle.libraryelements"],"jar")
            //this one is the must be for downloading dependencies
            assertEquals(variant.attributes!!["org.gradle.usage"],"java-runtime")
            val dep=variant.dependencies?.get(4)
            assertNotNull(dep)
            assertEquals(dep!!.group,"org.jetbrains.kotlin")
            assertEquals(dep.module,"kotlin-util-klib-metadata")
            assertEquals(dep.version.requires,"2.1.21")
            assertNull(dep.version.prefers)
            assertNull(dep.version.rejects)
            assertNull(dep.version.strictly)
            assertNotNull(dep.endorseStrictVersions)
            assertFalse(dep.endorseStrictVersions)
            val exclusions = dep.excludes
            assertEquals(exclusions.size,6)
            val exclusion = exclusions[0]
            assertEquals(exclusion.group,"org.jetbrains.kotlin")
            assertEquals(exclusion.module,"kotlin-reflect")
            val file=variant.files?.get(0)
            assertNotNull(file)
            assertEquals(file!!.name,"kotlin-gradle-plugin-2.1.21.jar")
            assertEquals(file.url,"kotlin-gradle-plugin-2.1.21.jar")
            assertEquals(file.size,30277508L)
            assertEquals(file.sha512,"132f3e9bd59dbde1449114fd9d838045e5751ae7f970a986d995a1be2a58cba4a276434c56f6df1d8018612103fef4d8f4157fcd2f6e7aa729ae355b04b140cb")
            assertEquals(file.sha256,"b5aef0d0b73546d0e95bf723e6753d47ec2a22db1127c8218d2ae999280a8885")
            assertEquals(file.sha1,"6f9c4375afae1996a4c845f8fedb242c921d1d43")
            assertEquals(file.md5,"3e4d845f166b45c6e8ded968ac8f8407")
            assertEquals(moduleMetadata.variants!!.filter { it.attributes?.get("org.gradle.usage")=="java-runtime" }.size,15)
        }
    }

    @Test
    fun getPropertiesByModuleFileTestThatHaveParent() {
        val moduleMetadata = getResourceFile("jackson-core-2.13.5.module")?.let {
            GradleModuleMetadata.loadFromFile(it)
        }
        assertNotNull(moduleMetadata)
        if(moduleMetadata!=null){
            assertEquals(moduleMetadata.formatVersion,"1.1")
            assertNotNull(moduleMetadata.component)
            assertEquals(moduleMetadata.component.module,"jackson-core")
            assertEquals(moduleMetadata.component.version,"2.13.5")
            assertEquals(moduleMetadata.component.group,"com.fasterxml.jackson.core")
            assertNotNull(moduleMetadata.createdBy?.maven?.version,"3.8.6")
            assertNotNull(moduleMetadata.variants)
            assertEquals(moduleMetadata.variants?.size,2)//literally in this case a pom is better
            val variant = moduleMetadata.variants?.get(1)
            assertNotNull(variant)
            assertEquals(variant!!.name,"runtimeElements")
            assertNotNull(variant.dependencies)
            assertEquals(variant.dependencies?.size,1)
            assertNotNull(variant.attributes)
            assertEquals(variant.attributes!!.size,4)
            assertEquals(variant.attributes!!["org.gradle.category"],"library")
            assertEquals(variant.attributes!!["org.gradle.libraryelements"],"jar")
            //this one is the must be for downloading dependencies
            assertEquals(variant.attributes!!["org.gradle.usage"],"java-runtime")
            val dep=variant.dependencies?.get(0)
            assertNotNull(dep)
            assertEquals(dep!!.group,"com.fasterxml.jackson")
            assertEquals(dep.module,"jackson-bom")
            assertEquals(dep.version.requires,"2.13.5")
            assertNull(dep.version.prefers)
            assertNull(dep.version.rejects)
            assertNull(dep.version.strictly)
            assertNull(dep.excludes)
            val file=variant.files?.get(0)
            assertNotNull(file)
            assertEquals(file!!.name,"jackson-core-2.13.5.jar")
            assertEquals(file.url,"jackson-core-2.13.5.jar")
            assertEquals(file.size,375186L)
            assertEquals(file.sha512,"c43227696cafe70f71c2f8a8f4bb6106f91ccc5c35ef0ff06fd52bd2913ae825e3e12cb6ec8f4c3fc47c93aef76a48fce994c7e5e7da44bfe1bd268d9d5be6f7")
            assertEquals(file.sha256,"48f36a025311d0464ad8dda4512a20c79e279a9550f63f3179d731d94482474b")
            assertEquals(file.sha1,"d07c97d3de9ea658caf1ff1809fd9de930a286a")
            assertEquals(file.md5,"2272453c780d1383ecd2efde00c1a7a9")
            assertEquals(moduleMetadata.variants!!.filter { it.attributes?.get("org.gradle.usage")=="java-runtime" }.size,1)
        }
    }
}
