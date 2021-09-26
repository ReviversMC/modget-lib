# Modget Library

This library contains common functions used by different modget tools.

To use it, add jitpack to the end of your `build.gradle` repositories:
```gradle
repositories {
    ...
    maven {
        url = "https://jitpack.io"
    }
}
```

And then add modget-lib to your dependencies:
```gradle
implementation "com.github.ReviversMC:modget-lib:${modget_lib_version}:all"
```
