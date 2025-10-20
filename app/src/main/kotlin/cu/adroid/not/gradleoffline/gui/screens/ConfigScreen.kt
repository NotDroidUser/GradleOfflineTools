package cu.adroid.not.gradleoffline.gui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cu.adroid.not.gradleoffline.Configuration
import cu.adroid.not.gradleoffline.gui.padding16
import cu.adroid.not.gradleoffline.gui.SearchProgress
import cu.adroid.not.gradleoffline.gui.utils.VerboseObject
import cu.adroid.not.gradleoffline.gui.snackbarShow
import cu.adroid.not.gradleoffline.gui.utils.border
import cu.adroid.not.gradleoffline.repo.MavenLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.collections.component1
import kotlin.collections.component2

@Preview
@Composable
fun ConfigScreen() {
  val removeEmptyDir = remember { mutableStateOf(Configuration.Config.removeEmptyDirs) }
  val moveFiles = remember { mutableStateOf(Configuration.Config.moveFiles) }
  val logToFile = remember { mutableStateOf(Configuration.Config.logger) }
  val border= remember { mutableStateOf(Configuration.Config.debugUI) }

  val checks = remember {
    mutableStateMapOf(
      Pair("Move Files", moveFiles),
      Pair("Remove Empty Dir", removeEmptyDir),
      Pair("Window verbose", VerboseObject.verboseDialog),
      Pair("Log to File", logToFile),
      Pair("Add Border (for Debug UI)",border )
    )
  }
  val repoPath = remember { mutableStateOf(Configuration.Config.repoPath) }
  val onlineRepos = remember { mutableStateOf(Configuration.Config.onlineRepos) }
  val showSnackbar: MutableState<Boolean> = remember { mutableStateOf(false) }
  val snackbarText: MutableState<String> = remember { mutableStateOf("Saved") }
  val scope = rememberCoroutineScope()
  val progress = remember { mutableStateOf(0f) }
  val onProgress = remember { mutableStateOf(false) }
  val progressQuantity = remember { mutableStateOf("") }
  val repoError = remember { mutableStateOf(false) }

  Column(modifier = Modifier.fillMaxSize().border()) {

    checks.forEach { (string, value) ->
      Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp).border(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        Text(string)
        Checkbox(checked = value.value, onCheckedChange = {
          checks[string]?.value = it
          val indexOf=checks.keys.indexOf(string)
          when (indexOf) {
            0 -> {
              Configuration.Config.moveFiles = it
            }
            1 -> {
              Configuration.Config.removeEmptyDirs = it
            }
            2 -> {
              Configuration.Config.verbose = it
            }
            3 -> {
              Configuration.Config.logger = it
            }
            4 -> {
              Configuration.Config.debugUI = it
            }
          }
          Configuration.saveConfig()
        })
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth().border()
    ) {
      OutlinedTextField(
        value = repoPath.value,
        label = { Text("Offline Repository Path") },
        isError = repoError.value,
        onValueChange = {
          repoPath.value = it
          repoError.value = false
        }, modifier = Modifier.padding(10.dp).border()
      )
      Button(content = { Text("Save & make Repo") }, onClick = {
        val file = File(repoPath.value)
        if (file.exists()) {
          Configuration.Config.repoPath = file.path
          Configuration.saveConfig()
          snackbarShow(scope, showSnackbar)
          scope.launch(Dispatchers.IO) {
            onProgress.value = true
            MavenLocalRepository.loadRepo(updateItAf = false, onProgress = { progressVal ->
              progress.value = progressVal
            }, onProgress2 = { value ->
              progressQuantity.value = value
            })
            onProgress.value = false
            snackbarShow(scope, showSnackbar, snackbarText, "I have scanned & cached your Repository!")
          }
        } else {
          repoError.value = true
        }
      }, modifier = Modifier.focusable().border())
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth().border()
    ) {
      AnimatedVisibility(repoError.value, content = {
        Text("Path doesn't valid", color = MaterialTheme.colors.error)
      })
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth().border()
    ) {
      OutlinedTextField(
        value = onlineRepos.value,
        label = { Text("Online Repository Path") },
        maxLines = 4,
        onValueChange = {
          onlineRepos.value = it
        }, modifier = padding16.fillMaxWidth(.7f).border()
      )
      Button(content = { Text("Save online repos") }, onClick = {
        Configuration.Config.onlineRepos = onlineRepos.value
        Configuration.saveConfig()
      }, modifier = padding16.focusable().border())
    }
    SearchProgress(onProgress, progressQuantity, progress, showSnackbar, snackbarText)
  }
}
