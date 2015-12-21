# DataParcel

Do not use this. It is incomplete. 

## Overview

An annotation to automatically generate Parcelable wrappers for Kotlin [Data Classes](https://kotlinlang.org/docs/reference/data-classes.html).

## Usage

Annotate your data class with @DataParcel

```
@DataParcel
data class Example(var test: Int)
```

Build app

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
val parcel = someBundle.readParcelable(getClass().getClassLoader())

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

Currently this class can _only_ be used with Kotlin data objects. These data objects must only hold either more data objects, or objects that can already be parceled by Android. 

Please file a bug for anything you see is missing or not handled correctly.

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
