# PaperParcel

[![Build Status](https://travis-ci.org/grandstaish/paperparcel.svg?branch=master)](https://travis-ci.org/grandstaish/paperparcel)

PaperParcel is an annotation processor that automatically generates the `CREATOR` and `writeToParcel(...)` implementations for you when writing [Parcelable](http://developer.android.com/intl/es/reference/android/os/Parcelable.html) objects. PaperParcel fully supports both Java and Kotlin (including [Kotlin Data Classes](https://kotlinlang.org/docs/reference/data-classes.html)). Additionally, PaperParcel supports Google's [AutoValue](https://github.com/google/auto/tree/master/value) via an [AutoValue Extension](http://jakewharton.com/presentation/2016-03-08-ny-android-meetup/).

For more information please see [the website](http://grandstaish.github.io/paperparcel/).

**Note:** JetBrains have released Parcelize as an "experimental" feature of Kotlin. As of Kotlin `1.1.60` I personally have found it stable enough to use in production. If you don't require some of the more advanced features of PaperParcel (e.g. type adapters), I recommend looking into Parcelize _before_ this library. 

## Download

**Java:**

```groovy
dependencies {
  compile 'nz.bradcampbell:paperparcel:2.0.4'
  annotationProcessor 'nz.bradcampbell:paperparcel-compiler:2.0.4'
}
```

**Kotlin:**

PaperParcel requires `kotlin 1.0.5` (or greater) and the `'kotlin-kapt'` Gradle plugin. Please note that the `'kotlin-kapt'` Gradle plugin is still experimental and may have issues.

```groovy
apply plugin: 'kotlin-kapt'

dependencies {
  compile 'nz.bradcampbell:paperparcel:2.0.4'
  compile 'nz.bradcampbell:paperparcel-kotlin:2.0.4' // Optional
  kapt 'nz.bradcampbell:paperparcel-compiler:2.0.4'
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
