object Version{

    val kotlinVersion = "1.3.41"
    val appCompatVersion = "28.0.0"
    val appCompatX= "1.0.2"
    val junit4Version = "4.12"
    val constraintLayoutVersion = "1.1.3"
    val testRunnerVersion = "1.1.1"
    val espressoVersion = "3.1.1"
    val materialVersion = "1.1.0-alpha06"
    val coreKtx="1.0.2"
    val lifecycleExtensions="2.0.0"
    val recyclerview="1.0.0"
    val legacySupport="1.0.0"
    val converterSimplexml = "2.0.0-beta3"
    val compileSdkVersion = 28
    val targetSdkVersion = 28
    val minSdkVersion = 22
    val versionCode = 1
    val versionName  = "1.0"


}

object Deps{
    val kotlin_sdkLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Version.kotlinVersion}"
    val junit4 =  "junit:junit:${Version.junit4Version}"
    val testRunner = "androidx.test:runner:${Version.testRunnerVersion}"
    val espresso =  "androidx.test.espresso:espresso-core:${Version.espressoVersion}"
    val appcompat =  "androidx.appcompat:appcompat:${Version.appCompatX}"
    val constraintLayout =  "androidx.constraintlayout:constraintlayout:${Version.constraintLayoutVersion}"
    val coreKTX =  "androidx.core:core-ktx:${Version.coreKtx}"
    val lifecycleExtensions =  "androidx.lifecycle:lifecycle-extensions:${Version.lifecycleExtensions}"
    val recyclerview =  "androidx.recyclerview:recyclerview:${Version.recyclerview}"
    val legacySupport =  "androidx.legacy:legacy-support-v4:${Version.legacySupport}"
    val material =  "com.google.android.material:material:${Version.materialVersion}"
    val converterSimpleXml =  "com.squareup.retrofit2:converter-simplexml:${Version.converterSimplexml}"
    
}