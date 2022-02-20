package com.fasterxml.jackson.dataformat.csv;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

/**
 * Automatically generated from PackageVersion.java.in during
 * packageVersion-generate execution of maven-replacer-plugin in
 * pom.xml.
 */
public final class PackageVersion implements Versioned {
    public final static Version VERSION = VersionUtil.parseVersion(
        "2.14.0-SNAPSHOT", "com.fasterxml.jackson.dataformat", "jackson-dataformat-csv");

    @Override
    public Version version() {
        return VERSION;
    }
}
