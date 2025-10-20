package cu.adroid.not.gradleoffline.exceptions

import java.io.FileNotFoundException

class PackageDontExist(path:String, reason:String): FileNotFoundException(reason)