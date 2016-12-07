# Benchmark-demo

This is a benchmark app that demonstrates that [PaperParcel](http://grandstaish.github.io/paperparcel/) is the fastest and lightest parcelling library available. Although other parcelling libraries aren't necessarily *that* slow, it's nice to know that PaperParcel *is* really fast! 

This demo also uses separate packages for the model classes for each processor so that the total number of generated method references can be easily compared. The results are as follows:
- [AutoValue: Parcel Extension](https://github.com/rharter/auto-value-parcel) (216<sup>1</sup>)
- [Parceler](http://parceler.org/) (70 methods)
- [PaperParcel](http://grandstaish.github.io/paperparcel/) (60 methods)

<sup>1</sup> These methods are including [automatically generated gson adapters](https://github.com/rharter/auto-value-gson), the total count goes down to 172 without them. Note that [AutoValue](https://github.com/google/auto/tree/master/value) produces a lot of these method references but the point is to demonstate that with even just a few model classes, AutoValue extensions can quickly increase your method reference count. Full disclosure, [AutoValue: Parcel Extension](https://github.com/rharter/auto-value-parcel) is actually almost as lightweight as it possibly can be, in fact, PaperParcel's AutoValue extension generates slightly more method references to give it extra functionality.
