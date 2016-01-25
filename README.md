# DataParcel

This is currently in beta until proven stable. 

## Overview

An annotation processor to automatically generate Parcelable wrappers for Kotlin [Data Classes](https://kotlinlang.org/docs/reference/data-classes.html).

Annotated data classes can contain any type that would normally be able to be parcelled. This includes all the basic Kotlin types, Lists, Maps, Arrays, SparseArrays, and [many more](https://github.com/grandstaish/DataParcel/tree/master/dataparcel-compiler/src/test/java/nz/bradcampbell/dataparcel). In addition to the regular types, Data classes can contain other data classes, or they can have data class Arrays, or even data class type parameters. DataParcel tries to have as little restriction as possible into how you write your data classes, so if you think anything is missing then please raise an issue.

## Usage

Annotate your data class with @DataParcel

```
@DataParcel
data class Example(var test: Int)
```

Use generated class to wrap the data object. The generated class is always {ClassName}Parcel. In this example, it is ExampleParcel.

```
val example = Example(42)
val parcelableWrapper = ExampleParcel.wrap(example)

// e.g. use in a bundle
savedInstanceState.putParcelable("example", parcelableWrapper)
```

Unwrap the bundled data object

```
// e.g. read from bundle
val parcelableWrapper = savedInstanceState.getParcelable<ExampleParcel>("example")

val example = parcelableWrapper.contents
```

## Data classes inside data classes

As mentioned in the Overview section, this is perfectly valid. Note you only need the @DataParcel annotation on the root data object (although there is nothing wrong with putting it on both), e.g.:

```
@DataParcel
data class ExampleRoot(var child: ExampleChild)

data class ExampleChild(var someValue: Int)
```

## Type Adapters

Occasionally when using DataParcel you might find the need to parcel an unknown type, or modify how an object is read/written to a parcel. TypeAdapters allow you to do this.

A good example of when you might want this functionality is with java.util.Date objects. By default, DataParcel will recognise Date as Serializable, and use Serialization as the Parcel reading/writing mechanism. Serialization is slow, so you might want to write a custom TypeAdapter for a Date object:

```
class DateTypeAdapter : TypeAdapter<Date> {
    override fun writeToParcel(value: Date, outParcel: Parcel) {
        outParcel.writeLong(value.time)
    }

    override fun readFromParcel(inParcel: Parcel): Date {
        return Date(inParcel.readLong())
    }
}
```

The TypeAdapter can be applied to the DataParcel annotation like so:

```
@DataParcel(typeAdapters = arrayOf(DateTypeAdapter::class))
data class Example(val a: Date)
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
    compile 'com.github.grandstaish.DataParcel:dataparcel:0.9.4'
    kapt 'com.github.grandstaish.DataParcel:dataparcel-compiler:0.9.4'
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
