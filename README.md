# DataParcel

This is currently in beta until proven stable. 

## Overview

An annotation processor to automatically generate Parcelable wrappers for Kotlin [Data Classes](https://kotlinlang.org/docs/reference/data-classes.html).

## Usage

Annotate your data class with @DataParcel

```
@DataParcel
data class Example(var test: Int)
```

Use generated class to wrap the data object. The generated class is always {ClassName}Parcel. In this example, it is ExampleParcel.

```
val example = Example(42)
val parcel = ExampleParcel.wrap(example)

// e.g. use in a bundle
someBundle.putParcelable("example", parcel)
```

Unwrap the bundled data object

```
// e.g. read from bundle
val parcel = someBundle.getParcelable<ExampleParcel>("example")

val example = parcel.getContents()
```

## Data classes inside data classes

This works too. Note you only need the @DataParcel annotation on the root data object, e.g.:

```
@DataParcel
data class ExampleRoot(var child: ExampleChild)

data class ExampleChild(var someValue: Int)
```

## Limitations

The @DataParcel annotation cannot be put directly on a data class with type parameters, e.g.:

This is wrong:
```
@DataParcel
data class BadExample<T>(val child: T)
```

However, it is OK to use data classes with typed parameters inside of your annotated data class, e.g.:

This is OK:
```
@DataParcel
data class GoodExample(val child: BadExample<Int>)
```

Please file a bug for anything you see is missing or not handled correctly.

## Download

Gradle:

```
repositories {
    maven { url = 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.grandstaish.DataParcel:dataparcel-annotations:0.9'
    kapt 'com.github.grandstaish.DataParcel:dataparcel-compiler:0.9'
}
```

## License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
