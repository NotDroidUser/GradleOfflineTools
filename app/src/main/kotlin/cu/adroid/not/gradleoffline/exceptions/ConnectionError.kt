package cu.adroid.not.gradleoffline.exceptions

import java.io.IOException

class ConnectionError(val code:Int = 404) : IOException()