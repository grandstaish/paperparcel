# PaperParcel

[![Build Status](https://travis-ci.org/grandstaish/paperparcel.svg?branch=master)](https://travis-ci.org/grandstaish/paperparcel)

PaperParcel is an annotation processor that automatically generates the `CREATOR` and `writeToParcel(...)` implementations for you when writing [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) objects. PaperParcel fully supports both Java and Kotlin (including [Kotlin Data Classes](https://kotlinlang.org/docs/reference/data-classes.html)). Additionally, PaperParcel supports Google's [AutoValue](https://github.com/google/auto/tree/master/value) via an [AutoValue Extension](http://jakewharton.com/presentation/2016-03-08-ny-android-meetup/).

For more information please see [the website](http://grandstaish.github.io/paperparcel/).

## Download

**Java:**

```groovy
dependencies {
  compile 'nz.bradcampbell:paperparcel:2.0.0-beta1'
  annotationProcessor 'nz.bradcampbell:paperparcel-compiler:2.0.0-beta1'
}
```

**Kotlin:**

PaperParcel requires `kapt3` and `kotlin 1.0.6` or greater. To use `kapt3`, apply the `kotlin-kapt` gradle plugin to your app's `build.gradle` file. Please note that `kapt3` is still experimental and may have issues. PaperParcel 2.0 (non-beta) will be released when `kapt3` is stable.

```groovy
apply plugin: 'kotlin-kapt'

dependencies {
  compile 'nz.bradcampbell:paperparcel:2.0.0-beta1'
  compile 'nz.bradcampbell:paperparcel-kotlin:2.0.0-beta1' // Optional
  kapt 'nz.bradcampbell:paperparcel-compiler:2.0.0-beta1'
}
```

Development snapshots are available on [JFrog OSS Artifactory](https://oss.jfrog.org/oss-snapshot-local).

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
