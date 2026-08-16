plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.freefair.lombok)
}

dependencies {
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.com.github.decentsoftware.eu.decentholograms)
}

description = "AlpsLib-Hologram"
version = "1.1.3"