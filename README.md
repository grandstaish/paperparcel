# PaperParcel

[![Build Status](https://travis-ci.org/grandstaish/paperparcel.svg?branch=master)](https://travis-ci.org/grandstaish/paperparcel)

## Overview

PaperParcel is an annotation processor that automatically generates the `CREATOR` and `writeToParcel(...)` implementations for you when writing [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) objects. PaperParcel fully supports both Java and Kotlin (including [Kotlin Data Classes](https://kotlinlang.org/docs/reference/data-classes.html)). Additionally, PaperParcel supports Google's [AutoValue](https://github.com/google/auto/tree/master/value) via an [AutoValue Extension](http://jakewharton.com/presentation/2016-03-08-ny-android-meetup/).

PaperParcel supports a wide range of common Android/Java value and container types out the box, including many types that [Parcel](http://developer.android.com/intl/es/reference/android/os/Parcel.html) and [Bundle](https://developer.android.com/reference/android/os/Bundle.html) don't support natively (e.g. `Set`, `SparseArray`, `ArrayMap`, etc). The full list of supported types can be found [here](paperparcel/src/main/java/paperparcel/adapter). Support for any other type can be added using [TypeAdapters](README.md#typeadapters).

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

**1)** Annotating a class with `@PaperParcel` will automatically generate the `CREATOR` and `writeToParcel(...)` implementations for that class at compile time. These implementations are generated into a java class (in the same package as your model) called `PaperParcel$CLASS_NAME$`

**2)** This is the first usage of the generated code — the generated `CREATOR` instance. 

**3)** These are the fields that will be processed by PaperParcel.

**4)** This is the second usage of the generated code — the generated `writeToParcel(...)` implementation.

## Even Easier; Use The AutoValue Extension

If you are already using AutoValue, all you need to do is simply add `implements Parcelable` to your AutoValue object and that's it:

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

While PaperParcel supports a large number of types out-of-the-box, sometimes you will need to extend the type system to add support for other types. You can do this by registering your own custom `TypeAdapter`. Luckily defining a custom `TypeAdapter` is simple. Let's look at an example that adds support for `java.util.UUID`s:

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

**1)** This is how you register your custom adapter with the compiler — just by annotating this class, generated code will now use `UuidAdapter` when reading and writing `UUID` fields.

**2)** Rather than implementing `TypeAdapter` directly, it may be convienient to extend `AbstractAdapter` instead. This is a base implementation of `TypeAdapter` handles null checking for you. 

**3)** This is a completely optional singleton instance. If PaperParcel notices your class is a singleton, it will use the singleton instance. This helps greatly in preventing unecessary allocations. For this reason, most of the built-in type adapters are singletons. Note for Kotlin users, this is equivalent to defining your Adapter as an `object`.

Many similar projects also use some variant of `TypeAdapter`, however the PaperParcel implementation is slightly more flexible. PaperParcel allows `TypeAdapter`s to be [composable](https://en.wikipedia.org/wiki/Object_composition) and [generic](https://docs.oracle.com/javase/tutorial/java/generics/types.html). To see why this is useful, let's look at how PaperParcel's [SparseArrayAdapter](paperparcel/src/main/java/paperparcel/adapter/SparseArrayAdapter.java) is defined:

```java
public final class SparseArrayAdapter<T> extends AbstractAdapter<SparseArray<T>> {
  private final TypeAdapter<T> itemAdapter;

  public SparseArrayAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }
  ...
}
```

As you can see, `SparseArrayAdapter` has a dependency on another `TypeAdapter` to handle the parcelling of its items, but the item type is not hard-coded (it is generic). This means we don't need to define a new adapter class each time we use a `SparseArray` with a different item type, instead this single adapter will handle all item types. 

A `TypeAdapter` can list any number of `TypeAdapter` dependencies as constructor parameters and PaperParcel will resolve them at compile time. You can take advantage of this power to easily add support for container types that don't come out of the box, e.g. `RealmList` for [Realm](https://github.com/realm/realm-java), various non-java `Collection` and `Tuple` types in [Kotlin](https://github.com/JetBrains/kotlin/), or even `ImmutableMap` for [Guava](https://github.com/google/guava). 

## Excluding Fields

By default, PaperParcel will exclude any `static` or `transient` field from being included in the generated `CREATOR` and `writeToParcel(...)` implementations. If you have more complicated requirements for excluding fields, then you can customise this behaviour using the `@PaperParcel.Options` API:

**Exclude via modifiers**

Let's say you wanted to exclude all `transient` and `static final` fields (therefore keeping any non-final `static` field). You could achieve that like this:

```java
@PaperParcel
@PaperParcel.Options(excludeFieldsWithModifiers = { Modifier.TRANSIENT, Modifier.STATIC | Modifier.FINAL })
public class Example implements Parcelable {
  static final long field1 = 0; // Will not be parcelled
  static long field2; // Will be parcelled
  transient long field3; // Will not be parcelled
  long field4; // Will be parcelled
  ...
}
```

**Exclude via annotation**

Another API available to you is `excludeFieldsWithAnnotations`. This API lists all of the annotations that will be used to exclude fields. Let's see how this might look:

First we'll create an annotation and call it `Exclude`:

```java
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Exclude {
}
```

Now we can use this annotation in any of our model classes to exclude fields like so:

```java
@PaperParcel
@PaperParcel.Options(excludeFieldsWithAnnotations = Exclude.class)
public class Example implements Parcelable {
  long field1; // Will be parcelled
  @Exclude long field2; // Will not be parcelled
  ...
}
```

**Opt-in field strategy**

Finally, it can also be useful for PaperParcel to ignore all fields unless specified otherwise. An example for when this can be of use is when you are extending a class from another library and you want to ignore all of the fields in that base class. PaperParcel provides a `@Pack` annotation which should be used alongside `excludeFieldsWithoutPackAnnotation` for this purpose:

```java
@PaperParcel
@PaperParcel.Options(excludeFieldsWithoutPackAnnotation = true)
public class Example implements Parcelable {
  @Pack long field1; // Will be parcelled
  long field2; // Will not be parcelled
  ...
}
```

**Reusable rule sets**

Applying exclusion rules in this manner can become tedius if you if you want to apply the same rules to many (or all) of your model objects. For a more reusable strategy, you may wish to create a custom annotation which will define all of the rules you wish to apply; then use your custom annotation on your `@PaperParcel` classes instead. Here's an example of a custom annotation that has `@PaperParcel.Options` applied to it:

```java
@PaperParcel.Options(...) // Define your rules here
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MyOptions {
}
```

After defining `@MyOptions` (call it whatever you like), you can then apply it directly to any `@PaperParcel` class to apply the rules:

```java
@MyOptions
@PaperParcel
public class Example implements Parcelable {
  ...
}
```

## Model Conventions

*Note: this section is only relevant if you are using PaperParcel without the assitance of AutoValue or Kotlin's data classes.*

PaperParcel uses no reflection to access fields. Because of that, all of the fields that PaperParcel is going to process need to follow a few loose conventions in order for PaperParcel to know how to read your fields and re-instantiate your models. Any failure to follow these conventions will result in a compile time error with a clear message informing you of what is wrong.

#### Reading Fields

The easiest way for PaperParcel to read a field is for it to be non-private. Because the generated code lies in the same package as the model itself, `default`, `protected`, or `public` fields can be read directly. 

However private fields are common practice and need to be supported. Therefore, if a field is private, PaperParcel will look for an accessor method (AKA a getter method) for that field. PaperParcel relies on the following conventions to find accessor methods:

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

**Java:**

```groovy
dependencies {
  compile 'nz.bradcampbell:paperparcel:2.0.0-SNAPSHOT'
  annotationProcessor 'nz.bradcampbell:paperparcel-compiler:2.0.0-SNAPSHOT'
}
```

**Kotlin:**

PaperParcel requires `kapt2` and `kotlin 1.0.5` or greater. To use `kapt2`, apply the `kotlin-kapt` gradle plugin to your app's `build.gradle` file.

```groovy
apply plugin: 'kotlin-kapt'

dependencies {
  compile 'nz.bradcampbell:paperparcel:2.0.0-SNAPSHOT'
  compile 'nz.bradcampbell:paperparcel-kotlin:2.0.0-SNAPSHOT' // Optional
  kapt 'nz.bradcampbell:paperparcel-compiler:2.0.0-SNAPSHOT'
}
```

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
