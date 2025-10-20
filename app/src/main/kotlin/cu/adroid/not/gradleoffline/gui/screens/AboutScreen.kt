package cu.adroid.not.gradleoffline.gui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.adroid.not.gradleoffline.getAssetsResource
import cu.adroid.not.gradleoffline.gui.padding16
import cu.adroid.not.gradleoffline.gui.padding4
import cu.adroid.not.gradleoffline.gui.padding8
import cu.adroid.not.gradleoffline.gui.utils.Screen
import cu.adroid.not.gradleoffline.gui.utils.border
import java.util.Calendar
import java.util.Date
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun AboutScreen(){
  
  val aboutScreen= remember { mutableStateOf(Screen.Kradle) }
  val animateAutor= remember { mutableStateOf(false) }
  val animateToolset = remember {  }
  val backColor = Color(0xFF0d1117)
  val colors = darkColors()
  val linkStyles = TextLinkStyles(style = SpanStyle(color = colors.onBackground, textDecoration = TextDecoration.Underline),
    focusedStyle = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline),
    hoveredStyle = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline),
    pressedStyle = SpanStyle(color = Color.Magenta, textDecoration = TextDecoration.Underline))
  
  val counter = remember { mutableStateOf(0) }
  MaterialTheme(colors = colors) {
    Box(modifier = Modifier.background(color = backColor, RectangleShape).fillMaxSize(1f)) {
      Column(Modifier.fillMaxSize(1f)) {
        Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight(0.1f).fillMaxWidth()) {
          Text("NotDroidUser (2021-${Calendar.getInstance().get(Calendar.YEAR)}) ",color= colors.onBackground, modifier = padding8)
          Image(
            bitmap = SkiaImage.makeFromEncoded(getAssetsResource("notdroid.png")).toComposeImageBitmap(), "", modifier = padding8.size(32.dp).clip(CircleShape)
          )
        }
        Row (Modifier.fillMaxHeight(0.1f).fillMaxWidth()){
          Column(Modifier.fillMaxSize(1f)){
            Row (verticalAlignment = Alignment.Bottom ,modifier=Modifier.fillMaxSize()){
              OutlinedButton({aboutScreen.value=Screen.Kradle}, modifier = padding4){
                Text("Kradle")
              }
              OutlinedButton({aboutScreen.value=Screen.MavenSearcher}, modifier = padding4){
                Text("Maven Searcher")
              }
              OutlinedButton({aboutScreen.value=Screen.DependenciesDownloader; counter.value++}, modifier = padding4){
                Text("Coming Next..")
              }
              OutlinedButton({aboutScreen.value=Screen.AboutScreen}, padding4){
                Text("About the developer")
              }
            }
          }
        }
        Row (modifier = Modifier.height(1.dp).fillMaxWidth(1f).border(width = 1.dp,Color(0xFF3f444f))){}
        Row (modifier = Modifier.fillMaxHeight(1f).fillMaxWidth()){
          Column(horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier.fillMaxHeight(1f).fillMaxWidth(0.3f)) {
            Image(SkiaImage.makeFromEncoded(getAssetsResource("icon.png")).toComposeImageBitmap(),"", modifier = Modifier.size(128.dp).clip(RoundedCornerShape(0.2f)))
            Row(modifier = padding16){
              Text(text="G", fontFamily = FontFamily.SansSerif, fontSize = 20.sp, fontWeight = FontWeight.W700,color= colors.onBackground,)
              Text(text="radle ", fontFamily = FontFamily.SansSerif, fontSize = 20.sp,color= colors.onBackground)
              Text(text="O", fontFamily = FontFamily.SansSerif, fontSize = 20.sp, fontWeight = FontWeight.W700,color= colors.onBackground)
              Text(text="ffline ", fontFamily = FontFamily.SansSerif, fontSize = 20.sp,color= colors.onBackground)
              Text(text="T", fontFamily = FontFamily.SansSerif, fontSize = 20.sp, fontWeight =  FontWeight.W700,color= colors.onBackground,)
              Text(text="ools", fontFamily = FontFamily.SansSerif, fontSize = 20.sp ,color= colors.onBackground)
            }
            Text(buildAnnotatedString{
              append("This program is licensed under")
              withLink(LinkAnnotation)
            })
          }
          Column(Modifier.border(width = 1.dp,Color(0xFF3f444f)).width(1.dp).fillMaxHeight()) {  }
          Column(modifier = padding8.fillMaxHeight(1f).fillMaxWidth(1f)) {
            Text(buildAnnotatedString {
              when(aboutScreen.value){
                Screen.Kradle -> {
                  withStyle(SpanStyle(color = colors.onBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)){append("Kradle:")}
                  append("\n\n Makes use of that `.gradle` cache folder\n" +
                    "Sometimes you need go offline, but that cache sometimes:\n" +
                    "* Don't works in two projects when you need it\n" +
                    "* It don't download all the pom/module files\n" +
                    "* Sometimes it updates the libraries and there isn't anywhere to be found a jar, klib or aar for the one that your project needs, and library isn't a pom,\n" +
                    "* You have a sh*t connection and you cant afford waiting 4 hours for every project init and want a solution of that\n" +
                    "* Gradle removes some of the actual cache and get you in middle of a trip without roaming/wifi")
                }
                
                Screen.MavenSearcher -> {
                  withStyle(SpanStyle(color = colors.onBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)){append("Maven Searcher:")}
                  
                  append("\n\n" +
                    "As a complement of Kradle the original idea of this code, i have made a `search` likely the original maven repository one because you always need to know what version you have and also a shorthand for getting the implementation for your project\n" +
                    " \n" +
                    "It allows you:\n" +
                    "* Know what versions you have on the offline repo\n" +
                    "* Get the groovy/maven/kts/toml code for less time wasted" +
                    "* ")
                }
                Screen.DependenciesDownloader -> {
                  if(counter.value==1){
                    append("There are something here on future")
                  } else if (counter.value in 2..10) {
                    append("There is something here on future")
                  } else{
                    append("Maybe some `Downloader`")
                  }
                }
                Screen.AboutScreen->{
                  withStyle(SpanStyle(color = colors.onBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)){append("NotDroidUser:\n\n")}
                  
                  append("Some developer out there, trying to do some work in peace, at least when gradle don't has a update.\n" +
                    "You can see my other apps here ")
                  
                  withLink(
                    LinkAnnotation.Url("https://github.com/NotDroidUser/",
                      styles =
                        linkStyles),
                  ) {
                    append("on my Github account")
                  }
                  append(", i had do some other apps (or doing yet) if you want to see them, also any issue is good received, code changes also, if you don't vibe the code with ia at music rhythm.")
                }
                Screen.Main,Screen.Config  -> {
                  append("I don't know how you got there but submit a issue about it")
                }
              }
            }, color = colors.onBackground)
          }
        }
      }
    }
  }
}

