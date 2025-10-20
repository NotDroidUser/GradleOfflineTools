package cu.adroid.not.gradleoffline.gui.utils

import androidx.compose.foundation.border
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cu.adroid.not.gradleoffline.Configuration

var colors = listOf(Color.Companion.Blue, Color.Companion.Red, Color.Companion.Black, Color.Companion.Green)
var counter = 0


inline operator fun Modifier.plus(m:Modifier): Modifier{
  return CombinedModifier(this,m)
}

val border= Modifier.border()

fun Modifier.border(): Modifier {
  return if(Configuration.Config.debugUI)
   this.border(3.dp,colors[counter++%4])
  else
    this
}
