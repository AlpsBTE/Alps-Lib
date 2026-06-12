package com.alpsbte.alpslib.utils;

/**
 * Configurable provider entry used to resolve a {@link MapLink}.
 * <p>
 * This class is intentionally mutable to support object mappers (JSON/YAML).
 */
public class MapLinkEntryConfig {
    private String id;
    private String name;
    private String urlTemplate;
    private Boolean enabled;

    /**
     * No-arg constructor for object mappers.
     */
    public MapLinkEntryConfig() {
    }

    /**
     * Creates a full provider configuration entry.
     *
     * @param id stable provider ID
     * @param name display name
     * @param urlTemplate URL template supporting placeholders {@code {lat}} and {@code {lon}}
     * @param enabled whether the entry should be active; {@code null} means inherit default during merge
     */
    public MapLinkEntryConfig(String id, String name, String urlTemplate, Boolean enabled) {
        this.id = id;
        this.name = name;
        this.urlTemplate = urlTemplate;
        this.enabled = enabled;
    }

    /**
     * @return provider ID
     */
    public String getId() {
        return id;
    }

    /**
     * @param id provider ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return display name shown to users
     */
    public String getName() {
        return name;
    }

    /**
     * @param name display name shown to users
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return URL template for link generation
     */
    public String getUrlTemplate() {
        return urlTemplate;
    }

    /**
     * @param urlTemplate URL template with coordinate placeholders
     */
    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    /**
     * @return whether this provider is enabled; {@code null} means inherit when merged
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled whether this provider is enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}


