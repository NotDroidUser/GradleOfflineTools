package cu.adroid.not.gradleoffline.gui.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object VerboseObject{
  val verboseDialog= mutableStateOf(false)
  val verbose= mutableStateListOf("")

  fun log(log:String){
    verbose.add(log)
  }
}

enum class Screen {
  Main, Kradle, MavenSearcher, Config, DependenciesDownloader, AboutScreen
}

object Navigator {
  val currentScreen = mutableStateOf(Screen.MavenSearcher)

  fun navigateTo(screen: Screen) {
    currentScreen.value = screen
  }
}
