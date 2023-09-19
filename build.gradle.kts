// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id ("androidx.navigation.safeargs") version "2.5.0" apply false

}
//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//    }
//    dependencies {
//        val nav_version = "2.7.2"
//        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
//        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
//        classpath("com.android.tools.build:gradle:7.0.4")
//
//
//
//    }
//}



tasks.register("clean").configure {
    delete("build")
}






