package cu.adroid.not.gradleoffline

import cu.adroid.not.gradleoffline.Configuration.Companion.getConfig
import cu.adroid.not.gradleoffline.gui.MainActivity
import org.jetbrains.skiko.setSystemLookAndFeel


fun main() {
    setSystemLookAndFeel()
    getConfig()
    MainActivity()
}


