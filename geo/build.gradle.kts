plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.freefair.lombok)
}

dependencies {
    implementation(libs.org.slf4j.slf4j.api)
    implementation(libs.org.json.json)
}

description = "AlpsLib-Geo"
version = "1.0.0"
