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
        "@Nullable public Boolean[] getChild() {",
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
        "Boolean[] outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "Boolean[] child = new Boolean[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Boolean childItem = (Boolean) in.readValue(null);",
        "child[childIndex] = childItem;",
        "}",
        "outChild = child;",
        "}",
        "this.data = new Test(outChild);",
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
        "Boolean[] child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Boolean childItem = child[childIndex];",
        "dest.writeValue(childItem);",
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
        "public List<Child>[] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer child;",
        "public Child(Integer child) {",
        "this.child = child;",
        "}",
        "public Integer getChild() {",
        "return this.child;",
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
        "int childSize = in.readInt();",
        "List<Child>[] child = new List[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "List<Child> outChildItem = null;",
        "if (in.readInt() == 0) {",
        "int childItemSize = in.readInt();",
        "List<Child> childItem = new ArrayList<Child>(childItemSize);",
        "for (int childItemIndex = 0; childItemIndex < childItemSize; childItemIndex++) {",
        "Child outChildItemItem = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel childItemItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outChildItemItem = childItemItemParcel.getContents();",
        "}",
        "childItem.add(outChildItemItem);",
        "}",
        "outChildItem = childItem;",
        "}",
        "child[childIndex] = outChildItem;",
        "}",
        "this.data = new Test(child);",
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
        "List<Child>[] child = data.getChild();",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "List<Child> childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childComponentSize = childItem.size();",
        "dest.writeInt(childComponentSize);",
        "for (int childComponentIndex = 0; childComponentIndex < childComponentSize; childComponentIndex++) {",
        "Child childComponentItem = childItem.get(childComponentIndex);",
        "if (childComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel childComponentParamParcel = ChildParcel.wrap(childComponentItem);",
        "childComponentParamParcel.writeToParcel(dest, 0);",
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
        "public Child[][] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer child;",
        "public Child(Integer child) {",
        "this.child = child;",
        "}",
        "public Integer getChild() {",
        "return this.child;",
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
        "int childSize = in.readInt();",
        "Child[][] child = new Child[childSize][];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child[] outChildItem = null;",
        "if (in.readInt() == 0) {",
        "int childItemSize = in.readInt();",
        "Child[] childItem = new Child[childItemSize];",
        "for (int childItemIndex = 0; childItemIndex < childItemSize; childItemIndex++) {",
        "Child outChildItemItem = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel childItemItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outChildItemItem = childItemItemParcel.getContents();",
        "}",
        "childItem[childItemIndex] = outChildItemItem;",
        "}",
        "outChildItem = childItem;",
        "}",
        "child[childIndex] = outChildItem;",
        "}",
        "this.data = new Test(child);",
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
        "Child[][] child = data.getChild();",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child[] childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childComponentSize = childItem.length;",
        "dest.writeInt(childComponentSize);",
        "for (int childComponentIndex = 0; childComponentIndex < childComponentSize; childComponentIndex++) {",
        "Child childComponentItem = childItem[childComponentIndex];",
        "if (childComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel childComponentComponentParcel = ChildParcel.wrap(childComponentItem);",
        "childComponentComponentParcel.writeToParcel(dest, 0);",
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
        "public Bitmap[] getChild() {",
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
        "int childSize = in.readInt();",
        "Bitmap[] child = new Bitmap[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Bitmap outChildItem = null;",
        "if (in.readInt() == 0) {",
        "outChildItem = Bitmap.CREATOR.createFromParcel(in);",
        "}",
        "child[childIndex] = outChildItem;",
        "}",
        "this.data = new Test(child);",
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
        "Bitmap[] child = data.getChild();",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Bitmap childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "childItem.writeToParcel(dest, 0);",
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
        "public Bitmap[] getChild() {",
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
        "int childSize = in.readInt();",
        "Bitmap[] child = new Bitmap[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Bitmap outChildItem = null;",
        "if (in.readInt() == 0) {",
        "outChildItem = Bitmap.CREATOR.createFromParcel(in);",
        "}",
        "child[childIndex] = outChildItem;",
        "}",
        "this.data = new Test(child);",
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
        "Bitmap[] child = data.getChild();",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Bitmap childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "childItem.writeToParcel(dest, 0);",
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
        "public Child[] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer child;",
        "public Child(Integer child) {",
        "this.child = child;",
        "}",
        "public Integer getChild() {",
        "return this.child;",
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
        "int childSize = in.readInt();",
        "Child[] child = new Child[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child outChildItem = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel childItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outChildItem = childItemParcel.getContents();",
        "}",
        "child[childIndex] = outChildItem;",
        "}",
        "this.data = new Test(child);",
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
        "Child[] child = data.getChild();",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel childComponentParcel = ChildParcel.wrap(childItem);",
        "childComponentParcel.writeToParcel(dest, 0);",
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
        "public Child<Child<Integer, Long>, Boolean> getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child<T1, T2> {",
        "private final T1 child1;",
        "private final T2 child2;",
        "public Child(T1 child1, T2 child2) {",
        "this.child1 = child1;",
        "this.child2 = child2;",
        "}",
        "public T1 getChild1() {",
        "return this.child1;",
        "}",
        "public T2 getChild2() {",
        "return this.child2;",
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
        "Child_2075967058Parcel childParcel = Child_2075967058Parcel.CREATOR.createFromParcel(in);",
        "Child<Child<Integer, Long>, Boolean> child = childParcel.getContents();",
        "this.data = new Test(child);",
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
        "Child<Child<Integer, Long>, Boolean> child = data.getChild();",
        "Child_2075967058Parcel childParcel = Child_2075967058Parcel.wrap(child);",
        "childParcel.writeToParcel(dest, 0);",
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
        "Child_669715220Parcel child1Parcel = Child_669715220Parcel.CREATOR.createFromParcel(in);",
        "Child<Integer, Long> child1 = child1Parcel.getContents();",
        "Boolean child2 = (Boolean) in.readValue(null);",
        "this.data = new Child<Child<Integer, Long>, Boolean>(child1, child2);",
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
        "Child<Integer, Long> child1 = data.getChild1();",
        "Child_669715220Parcel child1Parcel = Child_669715220Parcel.wrap(child1);",
        "child1Parcel.writeToParcel(dest, 0);",
        "Boolean child2 = data.getChild2();",
        "dest.writeValue(child2);",
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
        "Integer child1 = (Integer) in.readValue(null);",
        "Long child2 = (Long) in.readValue(null);",
        "this.data = new Child<Integer, Long>(child1, child2);",
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
        "Integer child1 = data.getChild1();",
        "dest.writeValue(child1);",
        "Long child2 = data.getChild2();",
        "dest.writeValue(child2);",
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
