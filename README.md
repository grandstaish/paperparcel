# PaperParcel

[![Build Status](https://travis-ci.org/grandstaish/paperparcel.svg?branch=master)](https://travis-ci.org/grandstaish/paperparcel)

## Overview

PaperParcel is an annotation processor that automatically generates type-safe [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) boilerplate code for Kotlin and Java. PaperParcel supports Kotlin [Data Classes](https://kotlinlang.org/docs/reference/data-classes.html), Google's [AutoValue](https://github.com/google/auto/tree/master/value) via an [AutoValue Extension](http://jakewharton.com/presentation/2016-03-08-ny-android-meetup/), or just regular Java bean objects (for lack of a better word).

Annotated data classes can contain any type that would normally be able to be parcelled. This includes all the basic Kotlin/Java types, Lists, Maps, Sets, Arrays, SparseArrays, [Kotlin object declarations](https://kotlinlang.org/docs/reference/object-declarations.html#object-declarations), and many more (the full list can be found [here](https://github.com/grandstaish/paperparcel/wiki/Supported-Types)). Support for any other type can be added using [TypeAdapters](README.md#typeadapters).

## Usage 

Using PaperParcel is easy, the API is extemely minimal. Let's look at an example:

``` java
@PaperParcel // (1)
public class User implements Parcelable { // (2)
  public static final Creator<User> CREATOR = PaperParcelUser.CREATOR; // (3)

  public long id; // (4)
  public String firstName; // (4)
  public String lastName; // (4)

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelUser.writeToParcel(this, dest, flags); // (5)
  }
}
```

I've annotated each important part with a comment and a number, let's look at each of these one by one:

**1)** Annotating a class with `@PaperParcel` will automatically produce all of the up-to-date Parcelable boilerplate code for you at compile time. The boilerplate code is generated into another java class (in the same package as your model) called `PaperParcel$CLASS_NAME$`

**2)** PaperParcel forces any `@PaperParcel`-annotated type to implement `Parcelable`. This is different to PaperParcel 1.0, which used Parcelable "wrapper" types (similar to how [Parceler](https://github.com/johncarl81/parceler) works). That practice was convienient some of the time, but it was very restricting when it came to using it with other libraries (e.g. Hannes Dorfmann's [FragmentArgs](https://github.com/sockeqwe/fragmentargs) or Prateek Srivastava's [Dart](https://github.com/f2prateek/dart/)). As you can see, both of these libraries have had to build in support for Parceler, which isn't ideal. PaperParcel 2.0 is more strict in forcing users to make thier model objects actually Parcelable as it is the better practice. 

**3)** This is the first usage of some of the generated code - the generated CREATOR instance. 

**4)** These are the fields that will be processed by PaperParcel.

**5)** This is the second usage of some of the generated code - the generated writeToParcel implementation.

## Even Easier; Use The AutoValue Extension

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

Simply implement Parcelable and you're done. Compared with the first example, will gain you immutability, `toString`, `hashCode`, and the ability to use even more AutoValue extensions.

## Kotlin

PaperParcel 2 requires `kapt2` and `kotlin 1.0.5` or greater. To use `kapt2`, simply apply the `kotlin-kapt` gradle plugin to your app's `build.gradle` file:

`apply plugin: 'kotlin-kapt'`

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

Much like the AutoValue example, using kotlin's `data` annotation on the class gives us immutability, `toString`, `hashCode` and [even more](https://kotlinlang.org/docs/reference/data-classes.html)!

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

PaperParcel 2.0+ takes a different approach to other Parcelable boilerplate code generators in that it is centered around something called a `TypeAdapter`. A `TypeAdapter` is simple: it is an object that knows how to read and write a specific type to/from a [Parcel](http://developer.android.com/intl/es/reference/android/os/Parcel.html). Here's an example of one of the many built in `TypeAdapter`s:

``` java
public final class IntegerAdapter extends AbstractAdapter<Integer> {
  @NonNull @Override protected Integer read(@NonNull Parcel source) {
    return source.readInt();
  }
  
  @Override protected void write(@NonNull Integer value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value);
  }
}
```

The thing that makes `TypeAdapter`s so powerful is that they are [composable](https://en.wikipedia.org/wiki/Object_composition) and they can be [generic](https://docs.oracle.com/javase/tutorial/java/generics/types.html). Their constructor can take any number of other `TypeAdapter`s. This allows you to easily create `TypeAdapter`s for container types that don't come out of the box, e.g. `RealmList` for [Realm](https://github.com/realm/realm-java), `MutableList` for [Kotlin](https://github.com/JetBrains/kotlin/), and `ImmutableList` for [Guava](https://github.com/google/guava). Creating these `TypeAdapter`s for container types is really easy:

``` java
@RegisterAdapter // (1)
public final class MyContainerTypeAdapter<T> extends AbstractAdapter<MyContainerType<T>> { // (2)
  private final TypeAdapter<T> itemAdapter;

  public LongSparseArrayAdapter(TypeAdapter<T> itemAdapter) {  // (3)
    this.itemAdapter = itemAdapter; 
  }

  @NonNull @Override protected MyContainerType<T> read(@NonNull Parcel source) {
    // (4)
  }

  @Override protected void write(@NonNull MyContainerType<T> value, @NonNull Parcel dest, int flags) {
    // (5)
  }
}
```

Let's go over each note one-by-one:

**1)** The @RegisterAdapter annotation just registers your custom adapter with the PaperParcelProcessor.

**2)** There's a few key points on this line. First it demonstrates that `TypeAdapter`s can be generic. Second it extends `AbstractAdapter`, which handles null-checking for you. Third, the type that it handles is the type argument to `AbstractAdapter`, e.g. `MyContainerType<T>`

**3)** Demonstrates how you can use the constructor to get instances of other type adapter types

**4)** This is where you can create a new instance of your type by reading the saved values in from the Parcel.

**5)** This is where you can write your type into the Parcel

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

Constructor arugments are simple: they must have the same name as the field that it is assigning. In addition, the argument type must be assignable to the field type. Here's an example:

``` java
@PaperParcel
public class User implements Parcelable { 
  private final long id; // (1)
  
  public User(long id) { // (2)
    this.long = long;
  }

  // Parcelable code omitted for clarity
}
```

**1)** The private field 

**2)** The valid constructor arugment for the `id` field. As you can see, the names match, and `long` is assignable to `long`.

Setter methods are discovered using similar conventions to the aforementioned accessor method conventions:

**1)** The method needs to have one argument 

**2)** The argument type needs to be assignable to the field's type

**3)** The method needs to have one of the following names: `$FIELD_NAME$`, or `set$FIELD_NAME$`. For example, if the field is named `firstName`, then the set of valid setter method names would contain `firstName` and `setFirstName`.

## Limitations

Classes with type parameters cannot be annotated with `@PaperParcel`. For example, you can't do the following:

``` java
@PaperParcel 
public class SomeGenericClass<T> {
}
```

## Download

Development snapshots are available on [JFrog OSS Artifactory](https://oss.jfrog.org/oss-snapshot-local).

## Contributing

I would love contributions to this project if you think of anything you would like to see in the project or find any bugs. If you would like to contribute, first raise a GitHub issue so we can discuss the change you want to make. 

The best way to contribute is to [fork the project on github](https://help.github.com/articles/fork-a-repo/) then send me a [pull request](https://help.github.com/articles/using-pull-requests/) via [github](https://github.com/).

If you create your own fork, it might help to enable rebase by default when you pull by executing git config --global pull.rebase true. This will avoid your local repo having too many merge commits which will help keep your pull request simple and easy to apply.

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
