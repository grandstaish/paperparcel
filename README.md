# PaperParcel

[![Build Status](https://travis-ci.org/grandstaish/paperparcel.svg?branch=master)](https://travis-ci.org/grandstaish/paperparcel)

http://blog.bradcampbell.nz/introducing-paperparcel/

## Overview

PaperParcel is an annotation processor that automatically generates type-safe [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) boilerplate code for Kotlin and Java. PaperParcel supports Kotlin [Data Classes](https://kotlinlang.org/docs/reference/data-classes.html), Google's [AutoValue](https://github.com/google/auto/tree/master/value) via an [AutoValue Extension](http://jakewharton.com/presentation/2016-03-08-ny-android-meetup/), or just regular Java bean objects (for lack of a better word).

Annotated data classes can contain any type that would normally be able to be parcelled. This includes all the basic Kotlin types, Lists, Maps, Arrays, SparseArrays, and [many more](https://github.com/grandstaish/PaperParcel/tree/master/compiler/src/test/java/nz/bradcampbell/paperparcel). In addition to the regular types, Data classes can contain other data classes, or they can have data class Arrays, or even data class type parameters. PaperParcel tries to have as little restriction as possible into how you write your data classes, so if you think anything is missing then please raise an issue.

PaperParcel is 100% generated code, no reflection or byte-code manipulation is involved. You can see all the generated classes yourself by viewing the auto-generated `PaperParcelMapping` class after a build.

## Usage (Kotlin)

Annotate your data class with `@PaperParcel`, implement `PaperParcelable`, and create a JVM static instance of `PaperParcelable.Creator` e.g.:

``` java
@PaperParcel
data class Example(var test: Int) : PaperParcelable {
  companion object {
    @JvmField val CREATOR = PaperParcelable.Creator(Example::class.java)
  }
}
```

Now your data class is `Parcelable` and can be passed directly to a `Bundle` or `Intent`. 

Unfortunately this is still a little bit of boilerplate code, but it only has to be applied to the class you want to be `Parcelable`, e.g.:

``` java
@PaperParcel
data class Example(var test: ChildExample) : PaperParcelable {
  companion object {
    @JvmField val CREATOR = PaperParcelable.Creator(Example::class.java)
  }
}

data class ChildExample(var test: Int)
```

A simple example can be found in the [kotlin-example](https://github.com/grandstaish/paperparcel/tree/master/examples/kotlin-example) module. For a more real-world example, see [here](https://github.com/grandstaish/four-letters-redux/blob/master/app/src/main/kotlin/nz/bradcampbell/fourletters/redux/state/State.kt).

If you add additional properties to your data class, ensure to make them transient (via `@Transient`), e.g.:

``` java
@PaperParcel
data class Example(var test: Int) : PaperParcelable {
  ...
  @delegate:Transient val somethingElse by lazy { ... }
}
```

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

Now your `AutoValue` class can be passed directly to a `Bundle` or `Intent`. 

Just like the Kotlin example, members of a `Parcelable` `AutoValue` class that are "bean" objects (e.g. other `AutoValue` classes) do not have to implement `Parcelable`. 

A simple example can be found in the [autovalue-example](https://github.com/grandstaish/paperparcel/tree/master/examples/autovalue-example) module.

## Usage (Java)

PaperParcel makes the following assumptions about annotated classes: 

- The primary constructor must have public or default visibility as PaperParcel doesn't use reflection. 
- The annotated class' member variable names must equal the primary constructor parameter names (ordering does not matter)
- The number of member variables should equal the number of arguments in the primary constructor. Static and transient member variables are not included in this count. 
- For each member variable, either the member variable must be public (or default) or its accessor method must be named `x()`, `isX()`, `getX()`,  where `x` is the member variable's name. Alternatively, the member variable can be annotated with `@AccessorName` to specify what the actual accessor name is. Additionally, the accessor method must have no parameters.

A misconfigured class will fail with an exception telling you what went wrong.

E.g.:

``` java
@PaperParcel
public final class Example extends PaperParcelable {
  private static final PaperParcelable.Creator<Example> CREATOR = new PaperParcelable.Creator<>(Example.class);

  private final int firstMember;
  private final long secondMember;

  public Example(int firstMember, long secondMember) {
    this.firstMember = firstMember;
    this.secondMember = secondMember;
  }

  public int getFirstMember() {
    return firstMember;
  }

  public long getSecondMember() {
    return secondMember;
  }
}
```

If you can't `extend PaperParcelable`, it's not required. Just `implement Parcelable` instead, and copy implementation for `writeToParcel` and `describeContents` (they're both 1-liners). 

If your app targets Android N+, paperparcel-java8 provides `PaperParcelable` as an interface with `default` methods. 

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

Annotate your type adapter with `@DefaultAdapter`:

``` java
@DefaultAdapter
class DateTypeAdapter : TypeAdapter<Date> {
  // ... 
}
```

In this example, PaperParcel will automatically use this TypeAdapter for any Date type unless a more explicit TypeAdapter is defined later.

#### Class TypeAdapters

Add the list of specific TypeAdapters to the data class. This will take precedence over global TypeAdapters and will apply to all variables in this class of the specified type.

``` java
@PaperParcel
@TypeAdapters(DateTypeAdapter::class)
data class Example(val a: Date) : PaperParcelable { ... }
```

#### Variable TypeAdapters

Add the list of specific TypeAdapters directly on the variable. These will take precedence over both global and class-scoped TypeAdapters and will only apply to the annotated variable.

``` java
@PaperParcel
data class Example(@TypeAdapters(DateTypeAdapter::class) val a: Date) : PaperParcelable { ... }
```

## Limitations

The @PaperParcel annotation cannot be put directly on a data class with type parameters, e.g.:

This is wrong:
``` java
@PaperParcel
data class BadExample<T>(val child: T) : PaperParcelable { ... }
```

However, it is OK to use data classes with typed parameters inside of your annotated data class, e.g.:

This is OK:
``` java
@PaperParcel
data class GoodExample(val child: BadExample<Int>) : PaperParcelable { ... }
```

Please file a bug for anything you see is missing or not handled correctly.

## Download (Kotlin)

``` groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.grandstaish.paperparcel:paperparcel:1.0.0-rc1'
    compile 'com.github.grandstaish.paperparcel:paperparcel-kotlin:1.0.0-rc1'
    kapt 'com.github.grandstaish.paperparcel:compiler:1.0.0-rc1'
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
    compile 'com.github.grandstaish.paperparcel:paperparcel:1.0.0-rc1'
    apt 'com.github.grandstaish.paperparcel:compiler:1.0.0-rc1'
}
```

## Download (Java)

``` groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.grandstaish.paperparcel:paperparcel:1.0.0-rc1'
    compile 'com.github.grandstaish.paperparcel:paperparcel-java7:1.0.0-rc1'
    apt 'com.github.grandstaish.paperparcel:compiler:1.0.0-rc1'
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
