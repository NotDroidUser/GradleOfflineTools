package cu.adroid.not.gradleoffline.gui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cu.adroid.not.gradleoffline.gui.utils.DialogTitle

@Composable
fun YesNoDialog(
  showDialog: MutableState<Boolean>,
  textToShow: MutableState<String>,
  onOk: () -> Unit,
  onCancel: () -> Unit = { showDialog.value = false }
) {
  Row(
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxSize(1f).background(Color(0xFFFFFFFF))
  ) {
    Dialog(onDismissRequest = onCancel) {
      DialogTitle("Confirm",onCancel)
      Card(modifier = Modifier.padding(8.dp).height(IntrinsicSize.Min)) {
        Column(
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize(1f).padding(16.dp)
        ) {
          Text(textToShow.value, modifier = Modifier.padding(16.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Button(onOk, modifier = Modifier.padding(8.dp)) {
              Text("Ok")
            }
            Button(onCancel, modifier = Modifier.padding(8.dp)) {
              Text("Cancel")
            }
          }
        }
      }
    }
  }
}
