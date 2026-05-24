plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":alpslib-utils"))
    api(project(":alpslib-io"))
    api(libs.com.alpsbte.canvas)
    api(libs.com.github.cryptomorin.xseries)
    api(libs.org.mariadb.jdbc.mariadb.java.client)
    api(libs.com.zaxxer.hikaricp)
    api(libs.org.apache.commons.commons.vfs2)
    api(libs.commons.net.commons.net)
    api(libs.com.github.mwiede.jsch)
    api(libs.org.jetbrains.annotations)
    api(libs.com.squareup.okhttp3.okhttp.jvm)
    api(libs.com.googlecode.json.simple)
    compileOnly(platform(libs.com.intellectualsites.bom.bom.newest))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly(libs.com.arcaniax.headdatabase.api)
    compileOnly(libs.commons.io.commons.io)
    compileOnly(libs.io.papermc.paper.paper.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
    testImplementation(libs.org.junit.platform.junit.platform.runner)
    testImplementation(libs.org.hamcrest.hamcrest)
    testCompileOnly(libs.io.papermc.paper.paper.api)
}

description = "AlpsLib-LibPlotSystemTerra"
version = "1.1.4"

tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}