# PaperParcel

http://blog.bradcampbell.nz/introducing-paperparcel/

## Overview

PaperParcel is an annotation processor that automatically generates type-safe [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) wrappers for Kotlin and Java. PaperParcel is unique in that it supports Kotlin [Data Classes](https://kotlinlang.org/docs/reference/data-classes.html).

Annotated data classes can contain any type that would normally be able to be parcelled. This includes all the basic Kotlin types, Lists, Maps, Arrays, SparseArrays, and [many more](https://github.com/grandstaish/PaperParcel/tree/master/compiler/src/test/java/nz/bradcampbell/paperparcel). In addition to the regular types, Data classes can contain other data classes, or they can have data class Arrays, or even data class type parameters. PaperParcel tries to have as little restriction as possible into how you write your data classes, so if you think anything is missing then please raise an issue.

## Usage

Annotate your data class with @PaperParcel

```
@PaperParcel
data class Example(var test: Int)
```

Use generated class to wrap the data object. The generated class is always {ClassName}Parcel. In this example, it is ExampleParcel.

```
val example = Example(42)
val parcelableWrapper = ExampleParcel.wrap(example)

// e.g. use in a bundle
outState.putParcelable("example", parcelableWrapper)
```

Unwrap the bundled data object

```
// e.g. read from bundle
val parcelableWrapper = savedInstanceState.getParcelable<ExampleParcel>("example")

val example = parcelableWrapper.contents
```

## Data classes inside data classes

As mentioned in the Overview section, this is perfectly valid. Note you only need the @PaperParcel annotation on the root data object (although there is nothing wrong with putting it on both), e.g.:

```
@PaperParcel
data class ExampleRoot(var child: ExampleChild)

data class ExampleChild(var someValue: Int)
```

## Type Adapters

Occasionally when using PaperParcel you might find the need to parcel an unknown type, or modify how an object is read/written to a parcel. TypeAdapters allow you to do this.

A good example of when you might want this functionality is with java.util.Date objects. By default, PaperParcel will recognise Date as Serializable, and use Serialization as the Parcel reading/writing mechanism. Serialization is slow, so you might want to write a custom TypeAdapter for a Date object:

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

The TypeAdapter can be applied to the PaperParcel annotation like so:

```
@PaperParcel(typeAdapters = arrayOf(DateTypeAdapter::class))
data class Example(val a: Date)
```

## Limitations

The @PaperParcel annotation cannot be put directly on a data class with type parameters, e.g.:

This is wrong:
```
@PaperParcel
data class BadExample<T>(val child: T)
```

However, it is OK to use data classes with typed parameters inside of your annotated data class, e.g.:

This is OK:
```
@PaperParcel
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
    compile 'com.github.grandstaish.paperparcel:paperparcel:0.9.9'
    kapt 'com.github.grandstaish.paperparcel:compiler:0.9.9'
}
```

## Contributing

I would love contributions to this project if you think of anything you would like to see in the project or find any bugs. If you would like to contribute, first raise a GitHub issue so we can discuss the change you want to make. 

The best way to contribute is to [fork the project on github](https://help.github.com/articles/fork-a-repo/) then send me a [pull request](https://help.github.com/articles/using-pull-requests/) via [github](https://github.com/).

If you create your own fork, it might help to enable rebase by default when you pull by executing git config --global pull.rebase true. This will avoid your local repo having too many merge commits which will help keep your pull request simple and easy to apply.


## License
    Copyright 2015 Bradley Campbell.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
