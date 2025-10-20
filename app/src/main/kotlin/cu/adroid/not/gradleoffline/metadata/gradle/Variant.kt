package cu.adroid.not.gradleoffline.metadata.gradle

/** Attributes in gradle module are is a map of string to string with same standard, they don't are mandatory
 * for further reference see gradle ones
 * org.gradle.usage are declared in org.gradle.api.attributes
 * org.gradle.status indicates "release" or "integration"
 * org.gradle.category indicates if the component is a "library", "platform" or "documentation"
 * org.gradle.libraryelements indicates the content of the library variant if its a jar, classes, etc
 * org.gradle.doctstype indicates the documentation type, it can be javadoc, sources, doxygen
 * org.gradle.dependency.bundling indicates how dependencies of the variant are bundled see org.gradle.api.attributes.Bundling
 **/
data class Variant(val name:String,
                   val attributes: MutableMap <String,String>?,
                   @com.google.gson.annotations.SerializedName("available-at")
                   val availableAt:AvailableAtObject?,
                   val dependencies: MutableList<DependencyObject>?,
                   val dependencyConstraints: MutableList<DependencyConstraintObject>?,
                   val files: MutableList<FileObject>?,
                   val capabilities:MutableList<CapabilityObject>?)
