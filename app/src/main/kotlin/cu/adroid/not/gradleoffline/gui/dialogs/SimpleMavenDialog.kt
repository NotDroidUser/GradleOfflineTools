package cu.adroid.not.gradleoffline.gui.dialogs

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cu.adroid.not.gradleoffline.gui.padding16
import cu.adroid.not.gradleoffline.gui.padding8
import cu.adroid.not.gradleoffline.gui.utils.DialogTitle
import cu.adroid.not.gradleoffline.gui.utils.MavenLibraryVersionView
import cu.adroid.not.gradleoffline.gui.utils.border
import cu.adroid.not.gradleoffline.repo.MavenLocalLib


@Composable
fun SimpleMavenDialog(
  showDialog: MutableState<Boolean>,
  mavenLocalLib: MutableState<MavenLocalLib>,
  version: String,
  onCancel: () -> Unit = { showDialog.value = false },
  onCheckLib:() -> Unit = {}
) {
  val horizontal = rememberLazyListState()
  Row(
    horizontalArrangement = Arrangement.Center,
    modifier = padding8.fillMaxSize(1f).background(Color(0xFFFFFFFF))
  ) {
    Dialog(onDismissRequest = onCancel) {
      Card(modifier = padding8.height(IntrinsicSize.Min)) {
        Column(
          verticalArrangement = Arrangement.Center,
          modifier = padding16.fillMaxSize(1f)
        ) {
          DialogTitle("Maven Library",onCancel)
          Text(
            mavenLocalLib.value.groupId + ":" + mavenLocalLib.value.name + ":" + version,
            modifier = padding16.border(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          Row(modifier = Modifier.fillMaxWidth(1f).height(400.dp)) {
            Box {
              MavenLibraryVersionView(mavenLocalLib,version, onCheckLib)
              VerticalScrollbar(
                rememberScrollbarAdapter(horizontal),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
              )
            }
          }
        }
      }
    }
  }
}


