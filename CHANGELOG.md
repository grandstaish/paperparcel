Change Log
==========

Version 2.0.0 *(TBD)*
----------------------------

 * TL;DR: everything changed
 * Change package name from `nz.bradcampbell.paperparcel` to just `paperparcel`
 * New TypeAdapter system:
   * Composability allow for `TypeAdapter`s to take other `TypeAdapter`s as constructor
     arguments
   * `TypeAdapter`s can also have type arguments when the type it is adapting is also generic
   * Generated code uses `TypeAdapter`s exclusively, meaning the generated code is just wiring
     (no parcelling logic). All logic is moved into the `TypeAdapter`s themselves and is fully unit tested.
   * 49 default `TypeAdapter`s introduced
 * Removed wrapper types. Each wrapper is replaced with a `Parcelable.Creator` and a `writeToParcel` 
   implementation for classes to manually use/call in their model objects.
 * Removed the `TypedParcelable` interface as wrappers no longer exist
 * Removed `PaperParcels` class and all reflection calls
 * Removed Mapping file
 * Removed `paperparcel-kotlin`, `paperparcel-java7`, and `paperparcel-java8`
 * Removed support for `Serializable`, `Enum`, and non-primitive arrays out of the box. Support
   for these types can be manually added back with custom `TypeAdapter`s
 * Removed `AccessorName` API
 * Renamed `DefaultAdapter` to `RegisterAdapter` and removed all other types of adapter scoping
   (field and class scopes) as they served no purpose
 * Force annotated classes to implement `Parcelable` as wrappers have been removed
 * Add `@Exclude` annotation to exclude individual fields
 * Add better java support. Non-private fields can be read/written directly when re-creating
   instances of the model class. Any combination of direct access, getters, setters, and
   constructor arguments will be used to read/write model objects to a `Parcel`
 * Removed old kotlin hack that worked around a bug in kapt. As a result, v2.0 will only
   work with kapt2 (which is available in Kotlin 1.0.4+)
 * Use android support annotations instead of jetbrains annotations
 * Package paperparcel as an AAR, including proguard rules (currently there are none)
 * Adapter instances are defined as constants to reduce the amount of allocations when parcelling.
   All default adapters are singleton instances where they can be. 
 * Method count of output greatly reduced (as well as output being cleaner and easier to understand)
 * Improved error messaging
 * Fix: multi module builds
 * Release now on jcenter instead of jitpack

Version 1.0.0 *(2016-05-27)*
----------------------------

Initial release.
