# ====================================================================
# NexaCV AI ProGuard & R8 Optimization Rules
# ====================================================================

# 1. Kotlin Reflection & Metadata Rules (Required for Moshi KotlinJsonAdapterFactory)
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
-keepclassmembers class * {
    @kotlin.jvm.Transient *;
}
-keep class kotlin.reflect.** { *; }
-keep class kotlin.Metadata { *; }

# 2. Moshi JSON Serialization
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

-keepclassmembers class * {
    @com.squareup.moshi.Json *;
    @com.squareup.moshi.JsonClass *;
}

# 3. Custom App Domain & Core Serialization Data Models
-keep class com.mudasir.nexacvai.domain.model.** { *; }
-keepclassmembers class com.mudasir.nexacvai.domain.model.** { *; }

-keep class com.mudasir.nexacvai.core.utils.ProfileImportExportHelper** { *; }
-keepclassmembers class com.mudasir.nexacvai.core.utils.ProfileImportExportHelper** { *; }

# 4. Room Database Entities & Type Converters
-keep class com.mudasir.nexacvai.data.local.entity.** { *; }
-keepclassmembers class com.mudasir.nexacvai.data.local.entity.** { *; }

-keep class com.mudasir.nexacvai.data.local.Converters { *; }
-keepclassmembers class com.mudasir.nexacvai.data.local.Converters { *; }
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}

# 5. Support for @Keep Annotation
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}