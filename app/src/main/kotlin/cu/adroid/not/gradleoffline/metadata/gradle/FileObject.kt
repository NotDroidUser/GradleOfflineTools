package cu.adroid.not.gradleoffline.metadata.gradle

/**
 * This one name must be renamed when translated to maven repo to url name (and location also if it contains also a path)
 * */
data class FileObject(val name:String,
                      val url:String,
                      val size:Long,
                      val sha1:String,
                      val sha256:String,
                      val sha512:String,
                      val md5:String)