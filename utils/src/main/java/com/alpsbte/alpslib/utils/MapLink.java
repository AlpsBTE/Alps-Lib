package com.alpsbte.alpslib.utils;

/**
 * Resolved map provider link.
 *
 * @param name display name of the provider
 * @param url absolute URL to open the location in that provider
 */
public record MapLink(String name, String url) {}
