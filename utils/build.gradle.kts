plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.freefair.lombok)
}

dependencies {
    api(libs.com.github.cryptomorin.xseries)
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.com.arcaniax.headdatabase.api)
    compileOnly(platform(libs.com.intellectualsites.bom.bom.newest))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly(libs.clipper2)
}

description = "AlpsLib-Utils"
version = "1.5.0-CIS"
