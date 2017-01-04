# Benchmark-demo

This is a benchmark app that demonstrates that [PaperParcel](http://grandstaish.github.io/paperparcel/) is the fastest parcelling library available. Although performance isn't the primary value proposition for PaperParcel, it's nice to show that the generated code is really fast.

This demo also uses separate packages for the model classes for each processor so that the total number of generated method references can be easily compared. The results are as follows:
- [AutoValue: Parcel Extension](https://github.com/rharter/auto-value-parcel) (216<sup>1</sup> + 0 library method references<sup>2</sup>)
- [Parceler](http://parceler.org/) (104 methods + 607 library method references<sup>2</sup>)
- [PaperParcel](http://grandstaish.github.io/paperparcel/) (94 methods + 194 library method references<sup>2</sup>)

The code in this demonstration is mostly copied from [LoganSquare](https://github.com/bluelinelabs/LoganSquare/)'s benchmark demo app — so shout-out to [bluelinelabs](http://bluelinelabs.com/) for the code!

## Installation

Warning: using Instant Run can give unexpected results. Install using the following command:

```
./gradlew clean :benchmark-demo:installDebug
```

#

<sup>1</sup> These methods are including [automatically generated gson adapters](https://github.com/rharter/auto-value-gson), the total count goes down to 172 without them. Note that [AutoValue](https://github.com/google/auto/tree/master/value) produces a lot of these method references but the point is to demonstate that with even just a few model classes, AutoValue extensions can quickly increase your method reference count. Full disclosure, [AutoValue: Parcel Extension](https://github.com/rharter/auto-value-parcel) is actually almost as lightweight as it possibly can be, in fact, PaperParcel's AutoValue extension generates slightly more method references to support a richer type system.

<sup>2</sup> Library method references are the standard methods that are compiled into the APK when using the library. 
