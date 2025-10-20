package cu.adroid.not.gradleoffline.gui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.adroid.not.gradleoffline.get
import cu.adroid.not.gradleoffline.gui.utils.border
import cu.adroid.not.gradleoffline.repo.MavenLocalLib
import cu.adroid.not.gradleoffline.repo.MavenLocalRepository
import kotlin.math.max

@Composable
fun SearchProgress(
  onProgress: MutableState<Boolean>,
  progressQuantity: MutableState<String>,
  progress: MutableState<Float>,
  showSnackBar: MutableState<Boolean>,
  snackbarText: MutableState<String>
) {
  Column {
    Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f).border()) {
      AnimatedVisibility(
        onProgress.value,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      ) {
        Row {
          Text(
            "Making repo" + listOf("...", "..", ".").random(),
            modifier = Modifier.padding(8.dp)
          )
          Text(
            progressQuantity.value,
            modifier = Modifier.padding(8.dp)
          )
        }
        LinearProgressIndicator(
          progress = progress.value,
          modifier = Modifier.fillMaxWidth()
        )
      }
      AnimatedVisibility(showSnackBar.value) {
        Snackbar {
          Text(snackbarText.value)
        }
      }
    }
  }
}

@Composable
fun LabelWithIcon(vector: ImageVector,text: MutableState<String>){
  Row(verticalAlignment = Alignment.CenterVertically, modifier = padding8){
    Image(
      vector, "", modifier = Modifier.size(48.dp).padding(end = 8.dp).border()
    )
    Text(text.value)
  }
}

@Composable
fun LabelWithIcon(bitmap: ImageBitmap,text: MutableState<String>){
  Row(verticalAlignment = Alignment.CenterVertically, modifier = padding8){
    Image(
      bitmap = bitmap, "", modifier = Modifier.size(48.dp).padding(end = 8.dp).border()
    )
    Text(text.value)
  }
}

@Composable
fun SearchBar(
  filter: MutableState<String>,
  onProgress: MutableState<Boolean>,
  list: SnapshotStateList<MavenLocalLib>
) {
  val update = {
    if (filter.value.length > 3 && !onProgress.value) {
      list.apply {
        clear()
        addAll(MavenLocalRepository.repo.searchLib(filter.value))
      }
    }
  }
  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().border()) {
    OutlinedTextField(
      filter.value, modifier = Modifier.fillMaxWidth(),
      onValueChange = {
        filter.value = it
      },
      label = { Text("Search") },
      trailingIcon = {
        IconButton(onClick = {
          update()
        }, content = {
          Icon(
            Icons.TwoTone.Search,
            contentDescription = "",
            modifier = Modifier.size(30.dp)
          )
        })
      },
    )

  }
}


@Composable
fun MavenLibView(lib: MavenLocalLib, mavenLocalLib: MutableState<MavenLocalLib>, showDialog: MutableState<Boolean>) {
  Surface(
    shape = androidx.compose.foundation.shape.AbsoluteRoundedCornerShape(10.dp),
    elevation = 10.dp,
    border = BorderStroke(1.dp, Color.Black.copy(alpha = .3f)),
    content = {
      Column(modifier = Modifier.fillMaxWidth().border()) {
        Row(modifier = padding16.fillMaxWidth().border()) {
          Text(
            "${lib.groupId}:${lib.name}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = Color.Gray.copy(alpha = .3f),
          shape = AbsoluteRoundedCornerShape(bottomLeft = 10.dp, bottomRight = 10.dp)
        ) {
          val maxSize = 7
          Box {
            val state=rememberLazyListState()
            Column(modifier = Modifier.padding(all = 8.dp)){
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(
                  10.dp,
                  Alignment.CenterHorizontally
                ), state = state
              ) {
                items(
                  items = lib.versions.sorted()[max(
                    0,
                    lib.versions.size - maxSize
                  ) until lib.versions.size].reversed(), itemContent = { version ->
                    Button(onClick = {
                      mavenLocalLib.value = lib
                      showDialog.value = true
                    }, content = {
                      Text(version)
                    })
                  }
                )
                if (lib.versions.size >= maxSize) {
                  items(1) {
                    Button(onClick = {
                      mavenLocalLib.value = lib
                      showDialog.value = true
                      //snackbarShow()
                    }, content = {
                      Text("++")
                    })
                  }
                }
              }
            }
            HorizontalScrollbar(rememberScrollbarAdapter(state), modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth())
          }
        }
      }
    },
    modifier = Modifier.padding(8.dp)
  )
}
