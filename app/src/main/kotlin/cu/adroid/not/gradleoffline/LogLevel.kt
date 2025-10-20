package cu.adroid.not.gradleoffline

import cu.adroid.not.gradleoffline.gui.utils.VerboseObject
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.Date

enum class LogLevel{
    error,
    verbose;
}


object LogToFile {
  //change this to some like log4j
  fun log(string: String, tag: LogLevel) {
    val logMessage = "${DateFormat.getDateTimeInstance().format(Date())}- ${tag.name} - $string"
    if (Configuration.Companion.Config.logger){
      val file = File("verbose.log")
      if (!file.exists()) {
        file.createNewFile()
      }
      FileOutputStream(file, true).apply {
        write(logMessage.toByteArray())
        flush()
        close()
      }
    }
    if (Configuration.Companion.Config.logger) {
      VerboseObject.log(logMessage)
    }
  }
}
