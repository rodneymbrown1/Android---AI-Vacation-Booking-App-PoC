# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- kotlinx.serialization ---
# @Serializable models (core:network) are looked up reflectively via their
# generated $$serializer companion, which R8 can't see statically.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.learning_2.core.network.model.**$$serializer { *; }
-keepclassmembers class com.example.learning_2.core.network.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.learning_2.core.network.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Ktor / OkHttp engine ---
# OkHttp references optional platform providers that aren't present on Android;
# these are safe to strip rather than warn about.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
