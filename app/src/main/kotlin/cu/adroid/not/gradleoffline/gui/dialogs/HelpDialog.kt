package cu.adroid.not.gradleoffline.gui.dialogs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cu.adroid.not.gradleoffline.gui.padding16
import cu.adroid.not.gradleoffline.gui.padding4
import cu.adroid.not.gradleoffline.gui.utils.DialogTitle
import cu.adroid.not.gradleoffline.gui.utils.Navigator
import cu.adroid.not.gradleoffline.gui.utils.Screen
import jdk.javadoc.internal.doclets.formats.html.markup.Text

@Preview
@Composable
fun HelpDialog(showDialog: MutableState<Boolean>, onCancel: () -> Unit = { showDialog.value = false }){
  Row {
    Dialog(onDismissRequest = onCancel) {
      Card(modifier = Modifier.padding(8.dp).height(IntrinsicSize.Min)) {
        
        Column(
          verticalArrangement = Arrangement.Center,
          modifier = padding16.fillMaxSize(1f)
        ) {
          DialogTitle("About ${
            when(Navigator.currentScreen.value){

              Screen.Kradle -> "Kradle"
              Screen.MavenSearcher -> "the offline Maven Searcher"
              Screen.Config -> "Configuration"
              Screen.Main,Screen.DependenciesDownloader -> "how you got here"
              Screen.AboutScreen ->"About (this is not a browser)"
            }
          }",onCancel)
          Row (modifier = padding4.height(1.dp).fillMaxWidth()){  }
          Text(
          when(Navigator.currentScreen.value){
            Screen.Main->{
              "This one is supposed to be the default screen, yet not available form outside code (or that i think)"
            }
            Screen.Kradle -> {
            "There you have two select buttons,\n\n First one for gradle cache, normally in your user" +
              " folder, inside a dot gradle folder, then inside that folder you only have the libraries" +
              " on caches/modules-2/files-2.1 folders, im not putting that on advance because maybe some update change it." +
              "\n\nThe second one are your local cache folder, i personally prefer to make it another" +
              " folder (different of the one on the configuration for the searcher) because if you have" +
              " bad connection/bad \"offline\" repo mabe some files will be rewritten yet if their sha1" +
              " dictates yet them will be rewritten in a rerun (if put on config), when they got to your" +
              " pc without problems then you can diff the folders with a tool like KDiff and copy only the \"changes\""
            }
            Screen.MavenSearcher -> {
            "A search for your offline repo, if you want to change the directory change it on Config," +
              " you must put at least 3 chars for it to work, you can search as groovy like implementation" +
              " path (example: com.android:zipflinger)," +
              " also if you're lazy can get the implementation from the buttons, and also know if the library is a aar, klib, pom or module" +
              " "
            }
            Screen.Config -> {
            "In order: verbose log to file\n" +
              "\nMove files: when doing kradle, moves the cached files so gradle gets them from your offline repo" +
              "\nWindow verbose: sends all verbose to a window" +
              "\nRemove empty dir: a companion to move files, so folders arent keep when moving" +
              "\nOffline repository: where maven repo is, also this dir don't have to be same as Kradle one" +
              "\nOnline repository: when you do a kradle it tries to do a download to any file that are missing " +
              "(like the sha1/pom/module), sometimes gradle downloads a pom and uses a newer version, i get all " +
              "those ones just in case as it will do that anyways, yet if the files don't exist it will say, " +
              "it don't exists and i don't want to compile, even if you have a maven-metadata.xml(Maven A Metadata for versions on the root of domain:package) with those two versions" +
              "\n\n\n" +
              "Add border(debug ui) was my help when i had started,tldr; a border of different colors, as this was my first project on Compose," +
              " i don't had done in Compose Multiplatform as the idea wasn't a MP app, bold of myself, Android Studio/Intellij" +
              " don't even try to make a preview of it, i had started it on 2021 when compose was mostly," +
              " a beta, no way to do this, all made of wood," +
              " documentation was literally stackoverflow, even the project" +
              " idea was because gradle module files had a name and outside was another," +
              " now i had mostly done it so i don't need intelli make a preview of it but for anyone that want to do it, don't, do a mp project if you want previews"
            }
            Screen.DependenciesDownloader -> {
              "This one is supposed to be the dependencies downloader screen, yet not available form outside code (or that i think)"
            }
            Screen.AboutScreen -> {
              "So you want to know more about this? I think there is nothing else to say here just navigate around buttons and maybe you find something else"
            }
          })
        }
      }
    }
  }
}

