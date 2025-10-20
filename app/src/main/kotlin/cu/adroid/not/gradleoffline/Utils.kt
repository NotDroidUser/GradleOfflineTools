package cu.adroid.not.gradleoffline

import java.io.File
import java.util.Objects
import kotlin.math.max

operator fun <E> List<E>.get(ints: IntRange): MutableList<E> {
  return this.range(ints)
}

fun <E> List<E>.range(ints: IntRange): MutableList<E> {
  val range=mutableListOf<E>()
  for (i in Integer.min(ints.start, size - 1)..Integer.min(
    size - 1,
    max(ints.start, ints.endInclusive)
  )){
    range.add(get(i))
  }
  return range
}

fun getAssetsResource(name:String): ByteArray {
  Kradle::class.java.getResourceAsStream("/assets/$name")?.let {
    return it.readBytes()
  }
  return "".toByteArray()
}
