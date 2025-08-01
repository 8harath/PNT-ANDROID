# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# MQTT Paho Client
-keep class org.eclipse.paho.** { *; }
-dontwarn org.eclipse.paho.**

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-dontwarn com.google.android.gms.maps.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Material Design
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep model classes
-keep class com.carcrashdetection.app.models.** { *; }

# Keep service classes
-keep class com.carcrashdetection.app.services.** { *; }

# Keep activity classes
-keep class com.carcrashdetection.app.activities.** { *; }

# Keep main activity
-keep class com.carcrashdetection.app.MainActivity { *; }

# General Android rules
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep R classes
-keep class **.R$* {
    public static <fields>;
}

# Keep View constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep onClick methods
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    void set*(...);
    *** get*();
}

# Keep custom application class
-keep public class * extends android.app.Application

# Keep custom activity classes
-keep public class * extends android.app.Activity
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends androidx.fragment.app.Fragment

# Keep custom service classes
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# Keep custom content provider classes
-keep public class * extends android.content.ContentProvider

# Keep custom view classes
-keep public class * extends android.view.View
-keep public class * extends android.view.ViewGroup

# Keep custom adapter classes
-keep public class * extends android.widget.BaseAdapter
-keep public class * extends androidx.recyclerview.widget.RecyclerView$Adapter

# Keep custom dialog classes
-keep public class * extends android.app.Dialog
-keep public class * extends androidx.appcompat.app.AlertDialog

# Keep custom animation classes
-keep public class * extends android.view.animation.Animation

# Keep custom drawable classes
-keep public class * extends android.graphics.drawable.Drawable

# Keep custom layout classes
-keep public class * extends android.view.ViewGroup$LayoutParams

# Keep custom widget classes
-keep public class * extends android.widget.* 