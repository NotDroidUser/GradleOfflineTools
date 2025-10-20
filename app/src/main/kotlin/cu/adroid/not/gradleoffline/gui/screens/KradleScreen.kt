package cu.adroid.not.gradleoffline.gui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cu.adroid.not.gradleoffline.Configuration
import cu.adroid.not.gradleoffline.Kradle
import cu.adroid.not.gradleoffline.LogLevel
import cu.adroid.not.gradleoffline.LogToFile
import cu.adroid.not.gradleoffline.gui.dialogs.YesNoDialog
import cu.adroid.not.gradleoffline.gui.snackbarShow
import cu.adroid.not.gradleoffline.gui.utils.ConfigPathChange
import cu.adroid.not.gradleoffline.gui.utils.border
import cu.adroid.not.gradleoffline.gui.utils.folderSelectDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Preview
@Composable
fun KradleScreen() {
  val localCache = remember { mutableStateOf(Configuration.Config.gradleCachePath) }
  val localRepo = remember { mutableStateOf(Configuration.Config.offlinePath) }
  val scope = rememberCoroutineScope()
  val snackBar: MutableState<Boolean> = remember { mutableStateOf(false) }
  val snackBarText: MutableState<String> = remember { mutableStateOf("Saved") }
  val progress = remember { mutableStateOf(0f) }
  val onProgress = remember { mutableStateOf(false) }
  val progressText = remember { mutableStateOf("Searching Files") }
  val showDialog = remember { mutableStateOf(false) }
  val dialogText = remember { mutableStateOf("") }
  val whatShouldDo = remember { mutableStateOf({}) }
  Box {
    if (showDialog.value) {
      YesNoDialog(showDialog, dialogText, {
        showDialog.value = false
        whatShouldDo.value()
      })
    }
    Column(modifier = Modifier.fillMaxSize().border().padding(32.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.weight(weight = 0.5f, fill = true).fillMaxWidth().border()
      )
      {
        val error = remember { mutableStateOf(false) }
        OutlinedTextField(
          value = localCache.value,
          label = { Text("Gradle Cache") },
          isError = error.value,
          onValueChange = {
            localCache.value = it
            error.value = false
          }, modifier = Modifier.padding(10.dp).border()
        )
        Button(content = { Text("Select") }, onClick = {
          folderSelectDialog(ConfigPathChange.gradleCachePath, localCache)
          /*  val file = File(localCache.value)
            if (file.exists()) {
              Config.gradleCachePath = file.path
              saveConfig()
              snackbarShow(scope, snackbar)
            } else {
              error.value = true
            }*/
        }, modifier = Modifier.focusable().border())
        AnimatedVisibility(error.value, content = {
          Text("Path doesn't valid", color = MaterialTheme.colors.error)
        })
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.weight(weight = 0.5f, fill = true).fillMaxWidth().border()
      ) {
        val error = remember { mutableStateOf(false) }
        OutlinedTextField(
          value = localRepo.value,
          label = { Text("Local Repository Path") },
          isError = error.value,
          onValueChange = {
            localRepo.value = it
            error.value = false
          }, modifier = Modifier.padding(10.dp).border()
        )
        Button(content = { Text("Select") }, onClick = {
          folderSelectDialog(ConfigPathChange.offlinePath, localRepo)
          /*val file = File(localRepo.value)
                  if (file.exists()) {
                      Config.offlinePath = file.path
                      saveConfig()
                      snackbarShow(scope,snackbar)
                  } else {
                      error.value = true
                  }*/
        }, modifier = Modifier.focusable().border())
        /*AnimatedVisibility(
          error.value,
          modifier = Modifier.padding(horizontal = 8.dp),
          content = {
            Column {
              Text("Path doesn't exist", color = MaterialTheme.colors.error)
              Button(content = { Text("Create it") }, onClick = {
                error.value = false
                val file = File(localRepo.value)
                if (file.exists()) {
                  snackbarShow(scope, snackBar, snackBarText, "Error path actually exist ._.")
                } else {
                  file.mkdirs()
                  Config.offlinePath = file.path
                  saveConfig()
                  snackbarShow(scope, snackBar, snackBarText, "Created & saved")
                }
              })
            }
          })*/
      }
      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.weight(weight = 1f, fill = true).fillMaxWidth().border()
      ) {
//        remember { mutableStateOf(false) }
        Button(content = { Text("Kradle it!") }, onClick = {
          dialogText.value = "Are you sure?(I will use values that saved already)"
          whatShouldDo.value = {
            scope.launch(Dispatchers.IO) {
              onProgress.value = true
              Kradle.kradleOrder(updateProgress = { progressVal, text ->
                progress.value = progressVal
                progressText.value = text
              }, printIt = { toSave ->
                if (Configuration.Config.logger||Configuration.Config.verbose) {
                  LogToFile.log(toSave, LogLevel.verbose)
                }
                println(toSave)
              })
              onProgress.value = false
              snackbarShow(scope, snackBar, snackBarText, "Kradled!")
            }
          }
          showDialog.value = true
        }, modifier = Modifier.focusable().border())
        /*AnimatedVisibility(
          sure.value,
          modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(1f).border()
        ) {
          Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f, fill = true)
          ) {
            Text(
              "",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 3
            )
            Row(
              modifier = Modifier.weight(1f, fill = true).fillMaxWidth().padding(4.dp).border(),
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
              Button(content = { Text("Yes") }, onClick = {
                sure.value = false
                scope.launch(Dispatchers.IO) {
                  onProgress.value = true
                  Kradle.kradleOrder(updateProgress = { progressVal, text ->
                    progress.value = progressVal
                    progressText.value = text
                  }, printIt = { tosave ->
                    if (Config.logger) {
                      log(tosave, LogLevel.debug)
                      println(tosave)
                    } else {
                      println(tosave)
                    }
                  })
                  onProgress.value = false
                  snackbarShow(scope, snackBar, snackBarText, "Kradled!")
                }
              })
              Button(content = { Text("No") }, onClick = {
                sure.value = false
              })
            }
          }
        }*/
      }
      AnimatedVisibility(
        onProgress.value,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      ) {
        Text(text = progressText.value)
        if (progress.value == -1f)
          LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        else
          LinearProgressIndicator(
            progress = progress.value,
            modifier = Modifier.fillMaxWidth()
          )
      }
      AnimatedVisibility(snackBar.value) {
        Snackbar {
          Text(snackBarText.value)
        }
      }
    }
  }
}
