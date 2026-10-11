plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.freefair.lombok)
}

dependencies {
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.commons.io.commons.io)
    compileOnly(libs.org.jetbrains.annotations)
    compileOnly(libs.com.zaxxer.hikaricp)
    compileOnly(libs.com.alpsbte.alpslib.alpslib.utils)
    compileOnly(libs.configurate.yaml)
}

description = "AlpsLib-IO"
version = "1.2.5"
