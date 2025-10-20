package cu.adroid.not.gradleoffline.metadata.maven

import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

//todo add mavenG and mavenV support maybe
data class MavenAMetadataXml(val groupId:String,
                             val artifactId:String,
                             val lastVer:String,
                             val stableVer:String,
                             val versions:MutableList<String>,
                             val lastUpdateTimestamp:String)
{
  companion object{
    //if this one is thrown is because you're seeing a rare (luck maybe) case of MavenGMetadataXML
    // that is only visible on half of the group folders
    // or maybe a V that is on snapshots folders
    @Throws(NullPointerException::class)
    fun fromXML(xml: InputStream):MavenAMetadataXml{
      val parse = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(xml)
      var latest=""
      try {
        latest=parse.getElementsByTagName("latest").item(0).textContent
      }catch (ex:NullPointerException){
        latest=parse.getElementsByTagName("release").item(0).textContent
      }
      val maven = MavenAMetadataXml(
          parse.getElementsByTagName("groupId").item(0).textContent,
          parse.getElementsByTagName("artifactId").item(0).textContent,
          latest,
          parse.getElementsByTagName("release").item(0).textContent,
          mutableListOf(),
          parse.getElementsByTagName("lastUpdated").item(0).textContent
      )
      val tags=parse.getElementsByTagName("version")
      for (i in (0 until tags.length)) {
        maven.versions.add(tags.item(i).textContent)
      }
      return maven
    }
  }

  fun toXml(): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<metadata>\n" +
        "<groupId>$groupId</groupId>\n" +
        "<artifactId>$artifactId</artifactId>\n" +
        "<versioning>\n" +
        "<latest>$lastVer</latest>\n" +
        "<release>$stableVer</release>\n")
        append("<versions>\n")
        versions.forEach{
          append("<version>$it</version>\n")
        }
        append("</versions>\n" +
        "<lastUpdated>$lastUpdateTimestamp</lastUpdated>\n" +
        "</versioning>\n" +
        "</metadata>")
  }
}