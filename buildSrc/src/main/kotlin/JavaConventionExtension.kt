import org.gradle.api.provider.Property

/**
 * Configuration options for the Java conventions plugin.
 */
interface JavaConventionExtension {
    /**
     * Whether sources and Javadoc JARs should be generated and published.
     */
    val documentationJars: Property<Boolean>
}