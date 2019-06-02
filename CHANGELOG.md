Change Log
==========

Version 2.0.8 *(2019-06-02)*
----------------------------

* AndroidX migration (thanks @Shusshu!)

Version 2.0.7 *(2019-06-02)*
----------------------------

* Support incremental annotation processing (thanks @bejibx!)

Version 2.0.6 *(2018-05-01)*
----------------------------

* Fix incorrect proguard rules for for paperparcel-kotlin

Version 2.0.5 *(2017-08-28)*
----------------------------

* Synchronize the write method cache
* Stricter consumer proguard rules for paperparcel-kotlin

Version 2.0.4 *(2017-08-28)*
----------------------------

* Fix: setter method name matching for properties that start with 'has'

Version 2.0.3 *(2017-08-28)*
----------------------------

* Add optional Lombok support (thanks to @ayanyev and @InviaTravel)
* Fix: compile issue when passing Parcelable creators of a generic type
* Fix: internal properties in kotlin

Version 2.0.2 *(2017-07-31)*
----------------------------

* Fix: resolve generic field types for fields contained in subclasses
* Fix: crash when a superclass and a subclass have a field with the same name

Version 2.0.1 *(2017-04-02)*
----------------------------

* Fix: broken build using kotlin 1.1.2.
* Fix: error message for non-writable types would sometimes tell you a field was private when it
  wasn't.
* Fix: Java keywords could not be used as property names as of Kotlin 1.1.0.

Version 2.0.0 *(2017-02-11)*
----------------------------

* Remove RegisterAdapter. ProcessorConfig should be used instead.
* Undeprecate PaperParcel.Options.
* Consolidate ProcessorConfig API and PaperParcel.Options as they share many of the same APIs.
* Favours local options over global options.
* More descriptive error messages.
* Allow PaperParcel class inheritance.
* Allow java.lang.Object adapters.
* Fix: Jack compilation.
* Fix: property method matching when variables start with 'is'

Version 2.0.0-beta2 *(2016-12-29)*
----------------------------

* Added `ProcessorConfig` API for adding custom `TypeAdapter`s and configuring other options in
  the processor.
* Deprecated `RegisterAdapter` API in favour of `ProcessorConfig`
* Deprecated `PaperParcel.Options` API in favour of `ProcessorConfig`
* Re-added `Serializable` support
* Performance improvements in the compiler and in the generated code
* Fix: allow use of "new" as a variable name in kotlin (as it isn't a kotlin keyword)
* Validate kapt version and give a helpful error message if the wrong version is being used
* Fix: allow use of contravariant generic types
* Greatly improve type matching system. This allows for adapters to handle complex generic types,
  including intersection types.

Version 2.0.0-beta1 *(2016-11-27)*
----------------------------

 * Change package name from `nz.bradcampbell.paperparcel` to just `paperparcel`
 * `TypeAdapter`s can now list other `TypeAdapter`s or `Class`es as constructor parameters to allow
   for adding proper support for non-standard container and types (and more).
 * Removed "wrapper" types. Each wrapper is replaced with a `Parcelable.Creator` and a `writeToParcel`
   implementation for classes to manually use/call in their model objects.
 * Removed the `TypedParcelable` interface as wrappers no longer exist
 * Removed `PaperParcels` class and all reflection calls
 * Removed Mapping file
 * Removed support for `Serializable` out of the box. Users can opt-in to using `Serializable` via
   explicit `TypeAdapter`s
 * Removed `AccessorName` API
 * Renamed `DefaultAdapter` to `RegisterAdapter` and removed all other types of adapter scoping
   (field and class scopes) as they served no purpose
 * Force annotated classes to implement `Parcelable` as wrappers have been removed
 * Greatly improve usage from Java
 * Add a more powerful abstraction for excluding fields
 * Package paperparcel as an AAR (includes proguard rules)
 * Adapter instances are defined as static constants to reduce the amount of allocations when
   parcelling. All built-in `TypeAdapter`s are singleton instances where they can be.
 * Method count of output greatly reduced (as well as output being cleaner and easier to understand)
 * Improved error messaging
 * Fix: multi module builds
 * Release now on jcenter and maven central instead of jitpack

Version 1.0.1 *(2016-10-31)*
----------------------------

* Fix: Can't reference `CREATOR` on `com.google.android.gms.maps.model.LatLng`

Version 1.0.0 *(2016-05-27)*
----------------------------

* Initial release.
