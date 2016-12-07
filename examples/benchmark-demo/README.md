# Benchmark-demo

This is a benchmark app that demonstrates that [PaperParcel](http://grandstaish.github.io/paperparcel/) is the fastest and lightest parcelling library available. 

This demo also uses separate packages for the model classes for each processor so that the total number of generated method references can be easily compared. The results are as follows:
- [AutoValue: Parcel Extension](https://github.com/rharter/auto-value-parcel) (216<sup>1</sup> + 0 library method references<sup>2</sup>)
- [Parceler](http://parceler.org/) (70 methods + 607 base library references<sup>2</sup>)
- [PaperParcel](http://grandstaish.github.io/paperparcel/) (60 methods + 187 library method references<sup>2</sup>)

The code in this demonstration is mostly copied from [LoganSquare](https://github.com/bluelinelabs/LoganSquare/)'s benchmark demo app — so shout-out to [bluelinelabs](http://bluelinelabs.com/) for the code!

<sup>1</sup> These methods are including [automatically generated gson adapters](https://github.com/rharter/auto-value-gson), the total count goes down to 172 without them. Note that [AutoValue](https://github.com/google/auto/tree/master/value) produces a lot of these method references but the point is to demonstate that with even just a few model classes, AutoValue extensions can quickly increase your method reference count. Full disclosure, [AutoValue: Parcel Extension](https://github.com/rharter/auto-value-parcel) is actually almost as lightweight as it possibly can be, in fact, PaperParcel's AutoValue extension generates slightly more method references to give it better type support.

<sup>2</sup> Library method references are the standard methods that are compiled into the APK when using the library. 
