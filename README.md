# PaperParcel

[![Build Status](https://travis-ci.org/grandstaish/paperparcel.svg?branch=master)](https://travis-ci.org/grandstaish/paperparcel)

http://blog.bradcampbell.nz/introducing-paperparcel/

## Overview

PaperParcel is an annotation processor that automatically generates type-safe [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) wrappers for Kotlin and Java. PaperParcel is unique in that it supports Kotlin [Data Classes](https://kotlinlang.org/docs/reference/data-classes.html).

Annotated data classes can contain any type that would normally be able to be parcelled. This includes all the basic Kotlin types, Lists, Maps, Arrays, SparseArrays, and [many more](https://github.com/grandstaish/PaperParcel/tree/master/compiler/src/test/java/nz/bradcampbell/paperparcel). In addition to the regular types, Data classes can contain other data classes, or they can have data class Arrays, or even data class type parameters. PaperParcel tries to have as little restriction as possible into how you write your data classes, so if you think anything is missing then please raise an issue.

## Usage (Kotlin)

Annotate your data class with `@PaperParcel` and implement `PaperParcelable`, e.g.:

``` java
@PaperParcel
data class Example(var test: Int) : PaperParcelable
```

Now your data class is `Parcelable` and can be passed directly to a `Bundle` or `Intent`

A simple example can be found in the [kotlin-example](https://github.com/grandstaish/paperparcel/tree/master/kotlin-example) module. For a more real-world example, see [here](https://github.com/grandstaish/four-letters-redux/blob/master/app/src/main/kotlin/nz/bradcampbell/fourletters/redux/state/State.kt).

## Usage (AutoValue) 

Simply implement `Parcelable` on your AutoValue class and PaperParcel's AutoValue extension will take care of the rest, e.g.:

``` java
@AutoValue
public abstract class Example implements Parcelable {
    public abstract int test();
    public static State create(int test) {
        return new AutoValue_Example(test);
    }
}
```

Now your AutoValue class can be passed directly to a `Bundle` or `Intent` 

A simple example can be found in the [autovalue-example](https://github.com/grandstaish/paperparcel/tree/master/autovalue-example) module.

## Usage (Java)

This is a little more manual. If your library doesn't use Kotlin or AutoValue, you might consider [Parceler](https://github.com/johncarl81/parceler) or one of the other great alternatives to PaperParcel.

However, if you use kotlin and java objects in the same project, you might still want to use PaperParcel. For an example on how to structure your model classes in java for PaperParcel to process them, see the [java-example](https://github.com/grandstaish/paperparcel/tree/master/java-example) module.

## Type Adapters

Occasionally when using PaperParcel you might find the need to parcel an unknown type, or modify how an object is read/written to a parcel. TypeAdapters allow you to do this.

A good example of when you might want this functionality is with java.util.Date objects. By default, PaperParcel will recognise Date as Serializable, and use Serialization as the Parcel reading/writing mechanism. Serialization is slow, so you might want to write a custom TypeAdapter for a Date object:

``` java
class DateTypeAdapter : TypeAdapter<Date> {
    override fun writeToParcel(value: Date, outParcel: Parcel) {
        outParcel.writeLong(value.time)
    }

    override fun readFromParcel(inParcel: Parcel): Date {
        return Date(inParcel.readLong())
    }
}
```

The TypeAdapters can be applied in multiple ways:

#### Global TypeAdapters

Annotate your type adapter with @GlobalTypeAdapter:

``` java
@GlobalTypeAdapter
class DateTypeAdapter : TypeAdapter<Date> {
  // ... 
}
```

In this example, PaperParcel will automatically use this TypeAdapter for any Date type unless a more explicit TypeAdapter is defined later.

#### Class TypeAdapters

Add the list of specific TypeAdapters to the PaperParcel annotation. This will take precedence over global TypeAdapters and will apply to all variables in this class.

``` java
@PaperParcel(typeAdapters = arrayOf(DateTypeAdapter::class))
data class Example(val a: Date) : PaperParcelable
```

#### Variable TypeAdapters

Add the specific TypeAdapter directly on the variable. This will take precedence over both global and class-scoped TypeAdapters and will only apply to the annotated variable.

``` java
@PaperParcel
data class Example(@FieldTypeAdapter(DateTypeAdapter::class) val a: Date) : PaperParcelable
```

## Limitations

The @PaperParcel annotation cannot be put directly on a data class with type parameters, e.g.:

This is wrong:
``` java
@PaperParcel
data class BadExample<T>(val child: T) : PaperParcelable
```

However, it is OK to use data classes with typed parameters inside of your annotated data class, e.g.:

This is OK:
``` java
@PaperParcel
data class GoodExample(val child: BadExample<Int>) : PaperParcelable
```

Please file a bug for anything you see is missing or not handled correctly.

## Download (Kotlin)

``` groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.grandstaish.paperparcel:paperparcel-kotlin:1.0.0-beta7'
    kapt 'com.github.grandstaish.paperparcel:compiler:1.0.0-beta7'
}
```

Note the use of `kapt` instead of `compile` for the compiler.

When using `kapt`, be sure to include the following in your `build.gradle` file:

```
kapt {
    generateStubs = true
}
```

## Download (AutoValue)

``` groovy
repositories {
    jcenter()
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.google.auto.value:auto-value:1.2-rc1'
    compile 'com.github.grandstaish.paperparcel:paperparcel:1.0.0-beta7'
    apt 'com.github.grandstaish.paperparcel:compiler:1.0.0-beta7'
}
```

Note that the [android-apt](https://bitbucket.org/hvisser/android-apt) plugin must be applied. 

## Proguard

In addition to the default Android rules set by proguard-android.txt in the SDK, add the following rules to your project proguard file:

```
-dontwarn org.jetbrains.annotations.**
-keepclassmembers class nz.bradcampbell.paperparcel.PaperParcelMapping {
  static ** FROM_ORIGINAL;
  static ** FROM_PARCELABLE;
}

# This is only needed if you are using paperparcel-kotlin
-keepclassmembers interface * extends android.os.Parcelable {
  static ** CREATOR;
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
