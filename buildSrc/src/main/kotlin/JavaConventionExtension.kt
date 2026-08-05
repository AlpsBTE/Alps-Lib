import org.gradle.api.provider.Property

interface JavaConventionExtension {
    /**
     * Whether sources and Javadoc JARs should be generated and published.
     */
    val documentationJars: Property<Boolean>
}