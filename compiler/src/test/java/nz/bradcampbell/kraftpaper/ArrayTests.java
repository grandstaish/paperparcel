package nz.bradcampbell.kraftpaper;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ArrayTests {

  @Test public void nullableBooleanArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import org.jetbrains.annotations.Nullable;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "@KraftPaper",
        "public final class Test {",
        "@Nullable private final Boolean[] child;",
        "public Test(@Nullable Boolean[] child) {",
        "this.child = child;",
        "}",
        "@Nullable public Boolean[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Boolean;",
        "import java.lang.Override;",
        "public class TestParcel implements Parcelable {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "return new TestParcel(in);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "private TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "private TestParcel(Parcel in) {",
        "Boolean[] outComponent1 = null;",
        "if (in.readInt() == 0) {",
        "int component1Size = in.readInt();",
        "Boolean[] component1 = new Boolean[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Boolean component1Item = (Boolean) in.readValue(null);",
        "component1[component1Index] = component1Item;",
        "}",
        "outComponent1 = component1;",
        "}",
        "this.data = new Test(outComponent1);",
        "}",
        "public static final TestParcel wrap(Test data) {",
        "return new TestParcel(data);",
        "}",
        "public Test getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Boolean[] component1 = data.component1();",
        "if (component1 == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Boolean component1Item = component1[component1Index];",
        "dest.writeValue(component1Item);",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void arrayOfListsOfNonParcelableObjectsTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "import java.util.List;",
        "@KraftPaper",
        "public final class Test {",
        "private final List<Child>[] child;",
        "public Test(List<Child>[] child) {",
        "this.child = child;",
        "}",
        "public List<Child>[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer test;",
        "public Child(Integer test) {",
        "this.test = test;",
        "}",
        "public Integer component1() {",
        "return this.test;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import java.util.ArrayList;",
        "import java.util.List;",
        "public class TestParcel implements Parcelable {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "return new TestParcel(in);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "private TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "private TestParcel(Parcel in) {",
        "int component1Size = in.readInt();",
        "List<Child>[] component1 = new List[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "List<Child> outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "int component1ItemSize = in.readInt();",
        "List<Child> component1Item = new ArrayList<Child>(component1ItemSize);",
        "for (int component1ItemIndex = 0; component1ItemIndex < component1ItemSize; component1ItemIndex++) {",
        "Child outComponent1ItemItem = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel component1ItemItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outComponent1ItemItem = component1ItemItemParcel.getContents();",
        "}",
        "component1Item.add(outComponent1ItemItem);",
        "}",
        "outComponent1Item = component1Item;",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
        "this.data = new Test(component1);",
        "}",
        "public static final TestParcel wrap(Test data) {",
        "return new TestParcel(data);",
        "}",
        "public Test getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "List<Child>[] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "List<Child> component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int component1ComponentSize = component1Item.size();",
        "dest.writeInt(component1ComponentSize);",
        "for (int component1ComponentIndex = 0; component1ComponentIndex < component1ComponentSize; component1ComponentIndex++) {",
        "Child component1ComponentItem = component1Item.get(component1ComponentIndex);",
        "if (component1ComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel component1ComponentParamParcel = ChildParcel.wrap(component1ComponentItem);",
        "component1ComponentParamParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nonParcelableArrayOfArraysTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "import android.util.SparseArray;",
        "@KraftPaper",
        "public final class Test {",
        "private final Child[][] child;",
        "public Test(Child[][] child) {",
        "this.child = child;",
        "}",
        "public Child[][] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer test;",
        "public Child(Integer test) {",
        "this.test = test;",
        "}",
        "public Integer component1() {",
        "return this.test;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "public class TestParcel implements Parcelable {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "return new TestParcel(in);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "private TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "private TestParcel(Parcel in) {",
        "int component1Size = in.readInt();",
        "Child[][] component1 = new Child[component1Size][];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child[] outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "int component1ItemSize = in.readInt();",
        "Child[] component1Item = new Child[component1ItemSize];",
        "for (int component1ItemIndex = 0; component1ItemIndex < component1ItemSize; component1ItemIndex++) {",
        "Child outComponent1ItemItem = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel component1ItemItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outComponent1ItemItem = component1ItemItemParcel.getContents();",
        "}",
        "component1Item[component1ItemIndex] = outComponent1ItemItem;",
        "}",
        "outComponent1Item = component1Item;",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
        "this.data = new Test(component1);",
        "}",
        "public static final TestParcel wrap(Test data) {",
        "return new TestParcel(data);",
        "}",
        "public Test getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child[][] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child[] component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int component1ComponentSize = component1Item.length;",
        "dest.writeInt(component1ComponentSize);",
        "for (int component1ComponentIndex = 0; component1ComponentIndex < component1ComponentSize; component1ComponentIndex++) {",
        "Child component1ComponentItem = component1Item[component1ComponentIndex];",
        "if (component1ComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel component1ComponentComponentParcel = ChildParcel.wrap(component1ComponentItem);",
        "component1ComponentComponentParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void parcelableArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "@KraftPaper",
        "public final class Test {",
        "private final Bitmap[] child;",
        "public Test(Bitmap[] child) {",
        "this.child = child;",
        "}",
        "public Bitmap[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "public class TestParcel implements Parcelable {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "return new TestParcel(in);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "private TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "private TestParcel(Parcel in) {",
        "int component1Size = in.readInt();",
        "Bitmap[] component1 = new Bitmap[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Bitmap outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "outComponent1Item = Bitmap.CREATOR.createFromParcel(in);",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
        "this.data = new Test(component1);",
        "}",
        "public static final TestParcel wrap(Test data) {",
        "return new TestParcel(data);",
        "}",
        "public Test getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Bitmap[] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Bitmap component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "component1Item.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void bitmapArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "@KraftPaper",
        "public final class Test {",
        "private final Bitmap[] child;",
        "public Test(Bitmap[] child) {",
        "this.child = child;",
        "}",
        "public Bitmap[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "public class TestParcel implements Parcelable {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "return new TestParcel(in);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "private TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "private TestParcel(Parcel in) {",
        "int component1Size = in.readInt();",
        "Bitmap[] component1 = new Bitmap[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Bitmap outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "outComponent1Item = Bitmap.CREATOR.createFromParcel(in);",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
        "this.data = new Test(component1);",
        "}",
        "public static final TestParcel wrap(Test data) {",
        "return new TestParcel(data);",
        "}",
        "public Test getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Bitmap[] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Bitmap component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "component1Item.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nonParcelableArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "@KraftPaper",
        "public final class Test {",
        "private final Child[] child;",
        "public Test(Child[] child) {",
        "this.child = child;",
        "}",
        "public Child[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer test;",
        "public Child(Integer test) {",
        "this.test = test;",
        "}",
        "public Integer component1() {",
        "return this.test;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "public class TestParcel implements Parcelable {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "return new TestParcel(in);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "private TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "private TestParcel(Parcel in) {",
        "int component1Size = in.readInt();",
        "Child[] component1 = new Child[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel component1ItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outComponent1Item = component1ItemParcel.getContents();",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
        "this.data = new Test(component1);",
        "}",
        "public static final TestParcel wrap(Test data) {",
        "return new TestParcel(data);",
        "}",
        "public Test getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child[] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel component1ComponentParcel = ChildParcel.wrap(component1Item);",
        "component1ComponentParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void genericNonParcelablesTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "@KraftPaper",
        "public final class Test {",
        "private final Child<Child<Integer, Long>, Boolean> child;",
        "public Test(Child<Child<Integer, Long>, Boolean> child) {",
        "this.child = child;",
        "}",
        "public Child<Child<Integer, Long>, Boolean> component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child<T1, T2> {",
        "private final T1 test1;",
        "private final T2 test2;",
        "public Child(T1 test1, T2 test2) {",
        "this.test1 = test1;",
        "this.test2 = test2;",
        "}",
        "public T1 component1() {",
        "return this.test1;",
        "}",
        "public T2 component2() {",
        "return this.test2;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Boolean;",
        "import java.lang.Integer;",
        "import java.lang.Long;",
        "import java.lang.Override;",
        "public class TestParcel implements Parcelable {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "return new TestParcel(in);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "private TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "private TestParcel(Parcel in) {",
        "Child_2075967058Parcel component1Parcel = Child_2075967058Parcel.CREATOR.createFromParcel(in);",
        "Child<Child<Integer, Long>, Boolean> component1 = component1Parcel.getContents();",
        "this.data = new Test(component1);",
        "}",
        "public static final TestParcel wrap(Test data) {",
        "return new TestParcel(data);",
        "}",
        "public Test getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child<Child<Integer, Long>, Boolean> component1 = data.component1();",
        "Child_2075967058Parcel component1Parcel = Child_2075967058Parcel.wrap(component1);",
        "component1Parcel.writeToParcel(dest, 0);",
        "}",
        "}"
    ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/Child_2075967058Parcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Boolean;",
        "import java.lang.Integer;",
        "import java.lang.Long;",
        "import java.lang.Override;",
        "public class Child_2075967058Parcel implements Parcelable {",
        "public static final Parcelable.Creator<Child_2075967058Parcel> CREATOR = new Parcelable.Creator<Child_2075967058Parcel>() {",
        "@Override public Child_2075967058Parcel createFromParcel(Parcel in) {",
        "return new Child_2075967058Parcel(in);",
        "}",
        "@Override public Child_2075967058Parcel[] newArray(int size) {",
        "return new Child_2075967058Parcel[size];",
        "}",
        "};",
        "private final Child<Child<Integer, Long>, Boolean> data;",
        "private Child_2075967058Parcel(Child<Child<Integer, Long>, Boolean> data) {",
        "this.data = data;",
        "}",
        "private Child_2075967058Parcel(Parcel in) {",
        "Child_669715220Parcel component1Parcel = Child_669715220Parcel.CREATOR.createFromParcel(in);",
        "Child<Integer, Long> component1 = component1Parcel.getContents();",
        "Boolean component2 = (Boolean) in.readValue(null);",
        "this.data = new Child<Child<Integer, Long>, Boolean>(component1, component2);",
        "}",
        "public static final Child_2075967058Parcel wrap(Child<Child<Integer, Long>, Boolean> data) {",
        "return new Child_2075967058Parcel(data);",
        "}",
        "public Child<Child<Integer, Long>, Boolean> getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child<Integer, Long> component1 = data.component1();",
        "Child_669715220Parcel component1Parcel = Child_669715220Parcel.wrap(component1);",
        "component1Parcel.writeToParcel(dest, 0);",
        "Boolean component2 = data.component2();",
        "dest.writeValue(component2);",
        "}",
        "}"
    ));

    JavaFileObject expectedSource3 = JavaFileObjects.forSourceString("test/Child_669715220Parcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Long;",
        "import java.lang.Override;",
        "public class Child_669715220Parcel implements Parcelable {",
        "public static final Parcelable.Creator<Child_669715220Parcel> CREATOR = new Parcelable.Creator<Child_669715220Parcel>() {",
        "@Override public Child_669715220Parcel createFromParcel(Parcel in) {",
        "return new Child_669715220Parcel(in);",
        "}",
        "@Override public Child_669715220Parcel[] newArray(int size) {",
        "return new Child_669715220Parcel[size];",
        "}",
        "};",
        "private final Child<Integer, Long> data;",
        "private Child_669715220Parcel(Child<Integer, Long> data) {",
        "this.data = data;",
        "}",
        "private Child_669715220Parcel(Parcel in) {",
        "Integer component1 = (Integer) in.readValue(null);",
        "Long component2 = (Long) in.readValue(null);",
        "this.data = new Child<Integer, Long>(component1, component2);",
        "}",
        "public static final Child_669715220Parcel wrap(Child<Integer, Long> data) {",
        "return new Child_669715220Parcel(data);",
        "}",
        "public Child<Integer, Long> getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Integer component1 = data.component1();",
        "dest.writeValue(component1);",
        "Long component2 = data.component2();",
        "dest.writeValue(component2);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2, expectedSource3);
  }
}
