# PaperParcel

[![Build Status](https://travis-ci.org/grandstaish/paperparcel.svg?branch=master)](https://travis-ci.org/grandstaish/paperparcel)

## Overview

PaperParcel is an annotation processor that automatically generates the `CREATOR` and `writeToParcel(...)` implementations for you when writing [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) objects. PaperParcel fully supports both Java and Kotlin (including [Kotlin Data Classes](https://kotlinlang.org/docs/reference/data-classes.html)). Additionally, PaperParcel supports Google's [AutoValue](https://github.com/google/auto/tree/master/value) via an [AutoValue Extension](http://jakewharton.com/presentation/2016-03-08-ny-android-meetup/).

PaperParcel supports a wide range of common Android/Java value types out the box, including many types that the [Parcel](http://developer.android.com/intl/es/reference/android/os/Parcel.html) and [Bundle](https://developer.android.com/reference/android/os/Bundle.html) don't support natively (e.g. Set, BigInteger, Date, etc). The full list of supported types can be found [here](paperparcel/src/main/java/paperparcel/adapter). Support for any other type can be added using [TypeAdapters](README.md#typeadapters).

## Usage 

Using PaperParcel is easy, the API is extemely minimal. Let's look at an example:

``` java
@PaperParcel // (1)
public class User implements Parcelable { 
  public static final Creator<User> CREATOR = PaperParcelUser.CREATOR; // (2)

  public long id; // (3)
  public String firstName; // (3)
  public String lastName; // (3)

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelUser.writeToParcel(this, dest, flags); // (4)
  }
}
```

I've annotated each important part with a comment and a number, let's look at each of these one by one:

**1)** Annotating a class with `@PaperParcel` will automatically produce all of the up-to-date Parcelable boilerplate code for you at compile time. The boilerplate code is generated into a java class (in the same package as your model) called `PaperParcel$CLASS_NAME$`

**2)** This is the first usage of some of the generated code - the generated CREATOR instance. 

**3)** These are the fields that will be processed by PaperParcel.

**4)** This is the second usage of some of the generated code - the generated writeToParcel implementation.

## Even Easier; Use The AutoValue Extension

If you are already using AutoValue, all you need to do is simply implement Parcelable on your AutoValue object and you're done:

``` java
@AutoValue 
public abstract class User implements Parcelable { 
  public abstract long id(); 
  public abstract String firstName(); 
  public abstract String lastName(); 

  public static User create(long id, String firstName, String lastName) {
    return new AutoValue_User(id, firstName, lastName);
  }
}
```

## Kotlin

Usage is exactly the same as java:

``` kotlin
@PaperParcel
data class User(
    val id: Long,
    val firstName: String,
    val lastName: String
) : Parcelable {
  companion object {
    @JvmField val CREATOR = PaperParcelUser.CREATOR
  }
  
  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    PaperParcelUser.writeToParcel(this, dest, flags)
  }
}
```

**Optional:** If you don't mind a minor amount of reflection, the `paperparcel-kotlin` module provides [PaperParcelable](paperparcel-kotlin/src/main/java/paperparcel/PaperParcelable.kt). `PaperParcelable` is an interface with default implementations written for `describeContents` and `writeToParcel(...)` so you don't have to write them yourself, e.g.:

``` kotlin
@PaperParcel
data class User(
    val id: Long,
    val firstName: String,
    val lastName: String
) : PaperParcelable {
  companion object {
    @JvmField val CREATOR = PaperParcelUser.CREATOR
  }
}
```

## TypeAdapters

A `TypeAdapter` is an object that specifies how to read and write a certian type to a Parcel instance. This gives you the ability to use types in your models that PaperParcel doesn't support out-of-the-box. 

Let's look at an example of creating a `TypeAdapter` to handle `java.util.UUID` objects:

``` java
@RegisterAdapter // 1
public final class UuidAdapter extends AbstractAdapter<UUID> { // 2
  public static final UuidAdapter INSTANCE = new UuidAdapter(); // 3

  @NonNull @Override protected Integer read(@NonNull Parcel source) {
    return new UUID(source.readLong(), source.readLong()); 
  }
  
  @Override protected void write(@NonNull Integer value, @NonNull Parcel dest, int flags) {
    dest.writeLong(value.getMostSignificantBits()); 
    dest.writeLong(value.getLeastSignificantBits());  
  }
}
```

I've annotated each important part with a comment and a number, let's look at each of these one by one:

**1)** This is how you register your custom adapter with the compiler - just by annotating this class, generated code will now use this type adapter for any `UUID` field.

**2)** Rather than implementing `TypeAdapter` directly, it may be convienient to extend `AbstractAdapter` instead. This is a base implementation of `TypeAdapter` handles null checking for you. 

**3)** This is a completely optional singleton instance. If PaperParcel notices your class is a singleton, it will use the singleton instance. This helps greatly in preventing unecessary allocations. For this reason, most of the built-in type adapters are singletons. Note for Kotlin users, this is equivalent to defining your Adapter as an `object`.

Many similar projects also use some variant of `TypeAdapter`, however the PaperParcel implementation is slightly more flexible. PaperParcel allows `TypeAdapter`s to be [composable](https://en.wikipedia.org/wiki/Object_composition) and [generic](https://docs.oracle.com/javase/tutorial/java/generics/types.html). This allows you to easily create `TypeAdapter`s for container types that don't come out of the box, e.g. `RealmList` for [Realm](https://github.com/realm/realm-java), various collection types in [Kotlin](https://github.com/JetBrains/kotlin/), or even `ImmutableList` for [Guava](https://github.com/google/guava). 

For an example of how composing `TypeAdapter`s looks, the [realm-example](examples/realm-example) project has the [RealmListTypeAdapter](examples/realm-example/src/main/java/nz/bradcampbell/realmexample/adapter/RealmListTypeAdapter.java). Additionally, all of PaperParcel's default types are supported via `TypeAdapter`s, so there are plenty of additional examples [in the source code](paperparcel/src/main/java/paperparcel/adapter).  

## Excluding Fields

PaperParcel provides the ability to exclude fields from being processed. There are two ways to do this:

**1)** Make the field static. 

**2)** Use the `@Exclude` annotation on a field:

``` java
@PaperParcel 
public class Test implements Parcelable { 
  @Exclude 
  public long fieldToIgnore;
  public long fieldToInclude;

  // Parcelable implementation omitted for clarity
}
```

## Model Conventions

*Note: this section is only relevant if you are using PaperParcel without the assitance of AutoValue or Kotlin's data classes.*

PaperParcel 2.0 uses no reflection at all. Because of that, all of the fields that PaperParcel is going to process need to follow a few loose conventions in order for PaperParcel to know how to read your fields, and how to fully re-instantiate your models at creation time. Any failure to follow these conventions will result in a compile-time error with a clear message informing you of what is wrong.

#### Reading Fields

The easiest way for PaperParcel to read a field is for it to be non-private. Because the generated code lies in the same package as the model itself, `default`, `protected`, or `public` fields can be read directly. 

However private fields are common practice, and need to be supported. Therefore, if a field is private, PaperParcel will look for an accessor method (AKA a getter method) for that field. PaperParcel relies on the following conventions to find accessor methods:

**1)** The method needs to return an assignable type to the field's type

**2)** The method needs to have no arguments

**3)** The method needs to have one of the following names: `$FIELD_NAME$`, `get$FIELD_NAME$`, or `is$FIELD_NAME$`. For example, if the field is named `firstName`, then the set of valid accessor method names would contain `firstName`, `getFirstName`, and `isFirstName`.

#### Writing Fields

The easiest way for PaperParcel to write a field is for it to be non-private and non-final. Because the generated code lies in the same package as the model itself, `default`, `protected`, or `public` fields can be written directly. 

As already mentioned, private fields to be supported. Therefore, if a field is private, PaperParcel will look for either a corresponding constructor arugment for the field, or a setter method for the field.

Constructor arugments are simple: they must have the same name as the field that it is assigning. In addition, the argument type must be assignable to the field type. 

Setter methods are discovered using similar conventions to the aforementioned accessor method conventions:

**1)** The method needs to have one argument 

**2)** The argument type needs to be assignable to the field's type

**3)** The method needs to have one of the following names: `$FIELD_NAME$`, or `set$FIELD_NAME$`. For example, if the field is named `firstName`, then the set of valid setter method names would contain `firstName` and `setFirstName`.

## Download

Development snapshots are available on [JFrog OSS Artifactory](https://oss.jfrog.org/oss-snapshot-local).

## Contributing

If you would like to contribute code you can do so by forking the repository and sending a pull request.

When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible. Please also make sure your code compiles by running `gradlew clean build`.

## License
    Copyright 2016 Bradley Campbell.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
