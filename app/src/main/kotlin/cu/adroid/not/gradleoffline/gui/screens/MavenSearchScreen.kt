package cu.adroid.not.gradleoffline.gui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cu.adroid.not.gradleoffline.Configuration
import cu.adroid.not.gradleoffline.gui.MavenLibView
import cu.adroid.not.gradleoffline.gui.SearchBar
import cu.adroid.not.gradleoffline.gui.SearchProgress
import cu.adroid.not.gradleoffline.gui.dialogs.FullMavenDialog
import cu.adroid.not.gradleoffline.gui.dialogs.YesNoDialog
import cu.adroid.not.gradleoffline.gui.snackbarShow
import cu.adroid.not.gradleoffline.gui.utils.border
import cu.adroid.not.gradleoffline.repo.MavenLocalLib
import cu.adroid.not.gradleoffline.repo.MavenLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


@Preview
@Composable
fun MavenSearcherScreen() {
  val filter = remember { mutableStateOf("") }
  val snackbar: MutableState<Boolean> = remember { mutableStateOf(false) }
  val snackbarText: MutableState<String> = remember { mutableStateOf("Saved") }
  val progress = remember { mutableStateOf(0f) }
  val scope = rememberCoroutineScope()
  val progressQuantity = remember { mutableStateOf("") }
  val onProgress = remember { mutableStateOf(false) }
  val updateIt = remember { mutableStateOf(false) }
  val list = remember { mutableStateListOf<MavenLocalLib>() }
  val showDialog = remember { mutableStateOf(false) }
  val wantToCheck = remember { mutableStateOf(false) }
  val toCheck=remember { mutableStateOf("")}
  val mavenLocalLib = remember {
    mutableStateOf(
      MavenLocalLib(
        "",
        "",
        mutableListOf()
      )
    )
  }

  val horizontal = rememberLazyListState()

  Box {
    if (showDialog.value) {
      FullMavenDialog(showDialog, mavenLocalLib, onCheckLib = {version->
        wantToCheck.value=true
        showDialog.value=false
        toCheck.value=version
      })
    }
    if (wantToCheck.value) {
      YesNoDialog(wantToCheck, mutableStateOf("${ mavenLocalLib.value }"),{

      },{
        wantToCheck.value=false
        showDialog.value=true
      })
    }
    Column(
      modifier = Modifier.fillMaxSize().padding(top = 32.dp, start = 32.dp, end = 32.dp).border()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(1f).border()) {
        OutlinedTextField(
          value = MavenLocalRepository.repo.path,
          onValueChange = {},
          label = {
            Text("Repo Path:")
          },
          modifier = Modifier.fillMaxWidth().border(),
          trailingIcon = {
            IconButton(onClick = {
              updateIt.value=true
            }, content = {
              Icon(
                Icons.TwoTone.Refresh,
                contentDescription = "",
                modifier = Modifier.size(30.dp)
              )
            })
          }
        )
      }
      SearchBar(filter, onProgress, list)
      if ((MavenLocalRepository.repo.path != Configuration.Config.repoPath && Configuration.Config.lastUpdate + Configuration.twentyFourHours != Date().time && !onProgress.value) || updateIt.value && !onProgress.value) {
        scope.launch(Dispatchers.IO) {
          Thread.sleep(1000)
          scope.launch(Dispatchers.IO) {
            onProgress.value = true
            MavenLocalRepository.loadRepo(updateIt.value, onProgress = { progressVal ->
              progress.value = progressVal
            }, onProgress2 = { value ->
              progressQuantity.value = value
            }
            )
            updateIt.value = false
            onProgress.value = false
            snackbarShow(scope, snackbar, snackbarText, "I have scanned & cached your Repository!")
          }
        }
      }

      if (filter.value.length > 3 && !onProgress.value) {
        list.apply {
          clear()
          addAll(MavenLocalRepository.repo.searchLib(filter.value))
        }
      } else if (!onProgress.value) {
        list.clear()
      }
      //mavenLibs
      if (!onProgress.value) {
        Box {
          LazyColumn(state = horizontal) {
            items(items = list, key = { "${it.groupId}:${it.name}" }, itemContent = {
              MavenLibView(it, mavenLocalLib, showDialog)
            })
          }
          VerticalScrollbar(
            rememberScrollbarAdapter(horizontal),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
          )
        }
      }
    }
    if (onProgress.value) {
      Row(Modifier.align(Alignment.CenterEnd).fillMaxHeight()) {
        SearchProgress(onProgress, progressQuantity, progress, snackbar, snackbarText)
      }
    }
  }
}
