package cu.adroid.not.gradleoffline.gui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.twotone.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.adroid.not.gradleoffline.getAssetsResource
import cu.adroid.not.gradleoffline.gui.LabelWithIcon
import cu.adroid.not.gradleoffline.gui.padding16
import cu.adroid.not.gradleoffline.gui.padding4
import cu.adroid.not.gradleoffline.gui.padding8
import cu.adroid.not.gradleoffline.metadata.gradle.GradleModuleMetadata
import cu.adroid.not.gradleoffline.metadata.maven.POM
import cu.adroid.not.gradleoffline.repo.MavenLocalLib
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun DialogTitle(title: String, onClose: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth().border()
  ) {
    Column(modifier = Modifier.border()) {
      Text(title, Modifier.border())
    }
    Column(
      horizontalAlignment = Alignment.End,
      modifier = Modifier.border().fillMaxWidth()
    ) {
      IconButton(onClose, modifier = Modifier.border()) {
        Icon(
          Icons.TwoTone.Close,
          contentDescription = "",
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}



@Composable
fun MavenLibraryVersionView(mavenLocalLib: MutableState<MavenLocalLib>, version:String, onCheckLib: () -> Unit ={}){
  Surface(
    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    color = MaterialTheme.colors.primary.copy(alpha = .3f),
    shape = AbsoluteRoundedCornerShape(topLeft = 10.dp, topRight = 10.dp)
  ) {
    Row(modifier = padding16.fillMaxWidth()) {
      Text(version, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
  }
  Surface(
    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    color = Color.Gray.copy(alpha = .3f),
    shape = AbsoluteRoundedCornerShape(bottomLeft = 10.dp, bottomRight = 10.dp)
  ) {
    Column(padding16) {
      var shouldCheck = false
      val hasModule = mavenLocalLib.value.hasModule(version)
      val hasPom = mavenLocalLib.value.hasPom(version)
      val otherFiles = mavenLocalLib.value.getOthers(version)
      var hasLib = false
      if (otherFiles != null && otherFiles.isNotEmpty() && (hasPom || hasModule)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (File(
              mavenLocalLib.value.getLibVersionPathFile(version),
              "${mavenLocalLib.value.name}-${version}.aar"
            ).exists()
          ) {
            LabelWithIcon(
              bitmap = SkiaImage.makeFromEncoded(getAssetsResource("android.png"))
                .toComposeImageBitmap(), mutableStateOf("Library Type:AAR")
            )
          }
          if (File(
              mavenLocalLib.value.getLibVersionPathFile(version),
              "${mavenLocalLib.value.name}-${version}.klib"
            ).exists()
          ) {
            LabelWithIcon(
              bitmap = SkiaImage.makeFromEncoded(getAssetsResource("kotlin.png"))
                .toComposeImageBitmap(),
              mutableStateOf("Library Type:Kotlin Library")
            )
          }
          if (File(
              mavenLocalLib.value.getLibVersionPathFile(version),
              "${mavenLocalLib.value.name}-${version}.jar"
            ).exists()
          ) {
            LabelWithIcon(
              bitmap = SkiaImage.makeFromEncoded(getAssetsResource("java.png"))
                .toComposeImageBitmap(), mutableStateOf("Library Type:JAR")
            )
          }
        }
      } else if (hasModule || hasPom) {
        if (hasPom) {
          val pom = POM.loadFromFile(mavenLocalLib.value.getPomFile(version))
          if (pom?.packaging == "pom") {
            LabelWithIcon(
              bitmap = SkiaImage.makeFromEncoded(getAssetsResource("xml.png"))
                .toComposeImageBitmap(), mutableStateOf("Library Type:POM")
            )
            hasLib = true
          } else if (File(
              mavenLocalLib.value.getLibVersionPathFile(version),
              "${mavenLocalLib.value.name}-${version}.${pom!!.getLibExtension()}"
            ).exists()
          ) {
            LabelWithIcon(
              vector = Icons.Default.Check,
              mutableStateOf("Library Type:${pom.packaging}")
            )
            hasLib = true
          } else {
            LabelWithIcon(
              vector = Icons.Default.Warning,
              mutableStateOf("Looks like a broken library")
            )
            shouldCheck = true
          }
        }
        if (hasModule && !hasLib) {
          val module =
            GradleModuleMetadata.loadFromFile(mavenLocalLib.value.getModuleFile(version)!!)
          if (module.isPomParentLike) {
            LabelWithIcon(
              bitmap = SkiaImage.makeFromEncoded(getAssetsResource("json.png"))
                .toComposeImageBitmap(), mutableStateOf("Library Type:Module")
            )
          } else if (module.hasMissingFiles()) {
            LabelWithIcon(
              vector = Icons.Default.Warning,
              mutableStateOf("Looks like a broken library")
            )
            shouldCheck = true
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "hasPom:")
        Checkbox(checked = hasPom, onCheckedChange = {})
        Text(text = "hasModule:")
        Checkbox(checked = hasModule, onCheckedChange = {})
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "hasSource:")
        Checkbox(checked = mavenLocalLib.value.hasSources(version), onCheckedChange = {})
        Text(text = "hasHasJavadoc:")
        Checkbox(checked = mavenLocalLib.value.hasJavadoc(version), onCheckedChange = {})
      }
      /*Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.align(Alignment.End)
      ) {
        if (shouldCheck) Text(
          "Should check->",
          color = Color.Red,
          fontWeight = FontWeight.Bold
        )
        Button(onClick = onCheckLib, modifier = padding8) {
          Text("Check for missing/invalid files")
        }
      }*/
      getImplementation(mavenLocalLib, version)
    }
  }
}


@Composable
fun getImplementation(mavenLocalLib: MutableState<MavenLocalLib>, version: String) {
  Text("Get implementation:", modifier = padding16, fontSize = 16.sp, fontWeight = FontWeight.Light)
  Row {
    Button(onClick = {
      val clip =
        StringSelection("implementation \"${mavenLocalLib.value.groupId}:${mavenLocalLib.value.name}:${version}\"")
      Toolkit.getDefaultToolkit().systemClipboard.setContents(clip, clip)
    }, modifier = padding4) {
      Text("Groovy")
    }
    Button(onClick = {
      val clip = StringSelection(
        "<dependency>\n" +
          "<groupId>${mavenLocalLib.value.groupId}</groupId>\n" +
          "<artifactId>${mavenLocalLib.value.name}</artifactId>\n" +
          "<version>${version}</version>\n" +
          "</dependency>"
      )
      Toolkit.getDefaultToolkit().systemClipboard.setContents(clip, clip)
    }, modifier = padding4) {
      Text("Maven")
    }
    Button(onClick = {
      val clip =
        StringSelection("implementation(\"${mavenLocalLib.value.groupId}:${mavenLocalLib.value.name}:${version}\")")
      Toolkit.getDefaultToolkit().systemClipboard.setContents(clip, clip)
    }, modifier = padding4) {
      Text("Kts")
    }
    Button(onClick = {
      val clip = StringSelection(
        "[versions]\n" +
          "${mavenLocalLib.value.name}Ver = \"$version\"\n" +
          "[libraries]\n" +
          "${mavenLocalLib.value.name} = { module = \"${mavenLocalLib.value.groupId}:${mavenLocalLib.value.name}\", version.ref = \"${mavenLocalLib.value.name}Ver\" }\n"
      )
      Toolkit.getDefaultToolkit().systemClipboard.setContents(clip, clip)
    }, modifier = padding4) {
      Text("TOML")
    }
  }
}

