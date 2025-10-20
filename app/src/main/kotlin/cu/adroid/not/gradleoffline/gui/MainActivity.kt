package cu.adroid.not.gradleoffline.gui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarData
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cu.adroid.not.gradleoffline.Configuration
import cu.adroid.not.gradleoffline.getAssetsResource
import cu.adroid.not.gradleoffline.gui.dialogs.HelpDialog
import cu.adroid.not.gradleoffline.gui.screens.AboutScreen
import cu.adroid.not.gradleoffline.gui.screens.ConfigScreen
import cu.adroid.not.gradleoffline.gui.screens.KradleScreen
import cu.adroid.not.gradleoffline.gui.screens.MavenSearcherScreen
import cu.adroid.not.gradleoffline.gui.utils.Navigator
import cu.adroid.not.gradleoffline.gui.utils.Screen
import cu.adroid.not.gradleoffline.gui.utils.VerboseObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Dimension
import kotlin.system.exitProcess
import org.jetbrains.skia.Image as SkiaImage

val padding8 = Modifier.padding(8.dp)
val padding16 = Modifier.padding(16.dp)
val padding4 = Modifier.padding(4.dp)

@Suppress("unused")
fun MainActivity() {
  application {
    Window(onCloseRequest = {
      Configuration.Config.apply { verbose=false }
      Configuration.saveConfig()
      VerboseObject.verboseDialog.value
    }, title = "Verbose log",
    resizable = true,
    icon = BitmapPainter(
      SkiaImage.makeFromEncoded(
        getAssetsResource("icon.png")
      ).toComposeImageBitmap()
    ),
    visible = VerboseObject.verboseDialog.value ){
      Column{
        Text(text="Verbose:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        val state = rememberLazyListState()
        Box {
          LazyColumn(state = state) {
            items(VerboseObject.verbose){ value->
              Text(value)
            }
          }
          VerticalScrollbar(rememberScrollbarAdapter(state))
        }
      }
    }
    Window(
      onCloseRequest = {
        this.exitApplication()
        exitProcess(0)
      },
      title = "Gradle Offline Tools",
      resizable = true,
      icon = BitmapPainter(
        SkiaImage.makeFromEncoded(getAssetsResource("icon.png")).toComposeImageBitmap()
      )
    ) {
      window.minimumSize= Dimension(800,600)
      MaterialTheme {
        val showHelp= remember { mutableStateOf(false) }
        Row {
          MenuBar {
            Menu(text = "Tools", mnemonic = 'T', enabled = true) {
              Item("Kradle", onClick = {
                Navigator.navigateTo(Screen.Kradle)
              }, mnemonic = 'K')
              Item(text = "MavenSearcher", onClick = {
                Navigator.navigateTo(Screen.MavenSearcher)
              }, mnemonic = 'M')
              /*Item(text = "Dependencies Downloader", onClick = {
                Navigator.navigateTo(Screen.DependenciesDownloader)
              }, mnemonic = 'D')*/
              Item(text = "Config", onClick = {
                Navigator.navigateTo(Screen.Config)
              }, mnemonic = 'C')
            }
            Menu(text = "Help", mnemonic = 'h', enabled = true) {
              Item(text = "Help with this...",onClick={
                showHelp.value=true
              }, mnemonic = 'A')
              Item(text = "About the toolset", onClick = {
                Navigator.navigateTo(Screen.AboutScreen)
              }, mnemonic = 'B')
            }
          }
          Box {
            if (showHelp.value) {
              HelpDialog(showHelp)
            }
            Crossfade(
              targetState = Navigator.currentScreen,
              animationSpec = SpringSpec(),
            ) { screenState ->
              when (screenState.value) {
                Screen.Kradle -> KradleScreen()
                Screen.Config -> ConfigScreen()
                Screen.MavenSearcher -> MavenSearcherScreen()
                Screen.DependenciesDownloader -> {}
                Screen.AboutScreen -> AboutScreen()
                Screen.Main -> {}
              }
            }
          }
        }
      }
    }
  }
}



fun snackbarShow(
  scope: CoroutineScope,
  snackbar: MutableState<Boolean>,
  snackbarText: MutableState<String>? = null,
  newText: String = ""
) {
  scope.launch(Dispatchers.IO) {
    var actualValue: String? = null
    snackbarText?.let {
      actualValue = snackbarText.value
      snackbarText.value = newText
    }
    snackbar.value = true
    Thread.sleep(1000)
    snackbar.value = false
    snackbarText?.let {
      Thread.sleep(100)
      snackbarText.value = actualValue ?: "Saved"
    }
  }
}


/*@Preview
@Composable
fun mavenPreview(){
    MavenLibView(MavenLib("compose","androidx.compose", versions = mutableListOf("1.0","1.1","1.0.0-alpha5").sorted().toMutableList()))
}*/

///MAKE A MAIN SCREEN HERE


