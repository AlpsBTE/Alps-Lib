plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.freefair.lombok)
}

dependencies {
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.commons.io.commons.io)
    compileOnly(libs.org.jetbrains.annotations)
    compileOnly(libs.com.zaxxer.hikaricp)
    compileOnly(project(":alpslib-utils"))
    compileOnly(libs.configurate.yaml)
    compileOnly("space.arim.dazzleconf:dazzleconf-yaml:2.0.0-M1")
}

description = "AlpsLib-IO"
version = "1.2.5"
