# PaperParcel

[![Build Status](https://travis-ci.org/grandstaish/paperparcel.svg?branch=master)](https://travis-ci.org/grandstaish/paperparcel)

http://blog.bradcampbell.nz/introducing-paperparcel/

## Overview

PaperParcel is an annotation processor that automatically generates type-safe [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) boilerplate code for Kotlin and Java. PaperParcel supports Kotlin [Data Classes](https://kotlinlang.org/docs/reference/data-classes.html), Google's [AutoValue](https://github.com/google/auto/tree/master/value) via an [AutoValue Extension](http://jakewharton.com/presentation/2016-03-08-ny-android-meetup/), or just regular Java bean objects (for lack of a better word).

Annotated data classes can contain any type that would normally be able to be parcelled. This includes all the basic Kotlin/Java types, Lists, Maps, Sets, Arrays, SparseArrays, [Kotlin object declarations](https://kotlinlang.org/docs/reference/object-declarations.html#object-declarations), and many more (the full list can be found [here](https://github.com/grandstaish/paperparcel/wiki/Supported-Types)). 

## Usage 

All documentation can be found in the [wiki](https://github.com/grandstaish/paperparcel/wiki):

- For Kotlin users, see [Kotlin Usage](https://github.com/grandstaish/paperparcel/wiki/Kotlin-Usage)
- For AutoValue users, see [AutoValue Usage](https://github.com/grandstaish/paperparcel/wiki/AutoValue-Usage)
- For Java users, see [Java Usage](https://github.com/grandstaish/paperparcel/wiki/Java-Usage)

## Type Adapters

Occasionally when using PaperParcel you might find the need to parcel an unknown type, or modify how an object is read/written to a `Parcel`. `TypeAdapter`s allow you to do this.

A good example of when you might want this functionality is with `java.util.Date` objects. By default, PaperParcel will recognise `Date` as `Serializable`, and use Serialization as the `Parcel` reading/writing mechanism. Serialization is slow, so you might want to write a custom `TypeAdapter` for a `Date` object.

Defining `TypeAdapter`s for a particular type automatically allows the use of this type with any container type, e.g. a `TypeAdapter` for `Date` will apply to the `Date` elements in `List<Map<String, Date>>`.

For information on how to define and use `TypeAdapters`, see the [Type Adapters wiki page](https://github.com/grandstaish/paperparcel/wiki/Type-Adapters).

## Limitations

Classes with type parameters cannot be annotated with `@PaperParcel` 

E.g.: you can't do the following:
``` java
@PaperParcel 
data class BadExample<T>(
  val child: T
) 
```

An issue has been raised for this [here](https://github.com/grandstaish/paperparcel/issues/44).

## Download (Kotlin)

``` groovy
kapt {
    generateStubs = true
}
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.grandstaish.paperparcel:paperparcel:1.0.0-rc4'
    compile 'com.github.grandstaish.paperparcel:paperparcel-kotlin:1.0.0-rc4'
    kapt 'com.github.grandstaish.paperparcel:compiler:1.0.0-rc4'
}
```

## Download (AutoValue)

``` groovy
repositories {
    jcenter()
    maven { url 'https://jitpack.io' }
}
dependencies {
    provided 'com.google.auto.value:auto-value:1.2'
    apt 'com.google.auto.value:auto-value:1.2'
    compile 'com.github.grandstaish.paperparcel:paperparcel:1.0.0-rc4'
    apt 'com.github.grandstaish.paperparcel:compiler:1.0.0-rc4'
}
```

Note that the [android-apt](https://bitbucket.org/hvisser/android-apt) plugin must be applied. 

## Download (Java)

``` groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.grandstaish.paperparcel:paperparcel:1.0.0-rc4'
    compile 'com.github.grandstaish.paperparcel:paperparcel-java7:1.0.0-rc4'
    apt 'com.github.grandstaish.paperparcel:compiler:1.0.0-rc4'
}
```

Note that the [android-apt](https://bitbucket.org/hvisser/android-apt) plugin must be applied. 

## Proguard

In addition to the default Android rules set by proguard-android.txt in the SDK, add the following rules to your project proguard file:

```
-dontwarn org.jetbrains.annotations.**
-keep class * implements nz.bradcampbell.paperparcel.PaperParcels$Delegate { *; }
-keep @nz.bradcampbell.paperparcel.PaperParcel class * { *; }
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
