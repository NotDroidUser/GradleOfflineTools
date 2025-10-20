package cu.adroid.not.gradleoffline.gui.utils

import androidx.compose.runtime.MutableState
import cu.adroid.not.gradleoffline.Configuration
import java.io.File
import javax.swing.JFileChooser

enum class ConfigPathChange{
    repoPath,
    gradleCachePath,
    offlinePath
}


fun folderSelectDialog(
  whatConfigIsChanged: ConfigPathChange,
  valueToChange: MutableState<String>
) {
  JFileChooser().apply {
    setCurrentDirectory(
      File(
        when (whatConfigIsChanged) {
          ConfigPathChange.repoPath -> Configuration.Companion.Config.repoPath
          ConfigPathChange.gradleCachePath -> Configuration.Companion.Config.gradleCachePath
          ConfigPathChange.offlinePath -> Configuration.Companion.Config.offlinePath
        }
      )
    )
    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    addActionListener {
      when (it.actionCommand) {
        "ApproveSelection" -> {
          val file = this.selectedFile.path
          when (whatConfigIsChanged) {
            ConfigPathChange.repoPath -> {
              Configuration.Companion.Config.repoPath = file
              valueToChange.value = file
            }

            ConfigPathChange.gradleCachePath -> {
              Configuration.Companion.Config.gradleCachePath = file
              valueToChange.value = file
            }

            ConfigPathChange.offlinePath -> {
              Configuration.Companion.Config.offlinePath = file
              valueToChange.value = file
            }
          }
        }

        else -> {}
      }
    }
    showOpenDialog(null)
  }
}
