package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class NonParcelableTests {

  @Test public void nestedDataTypeTest() {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import java.util.List;",
        "@PaperParcel",
        "public final class Root {",
        "private final Child child;",
        "public Root(Child child) {",
        "this.child = child;",
        "}",
        "public Child getChild() {",
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

    JavaFileObject rootParcel = JavaFileObjects.forSourceString("test/RootParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class RootParcel implements TypedParcelable<Root> {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "return new RootParcel(in);",
        "}",
        "@Override public RootParcel[] newArray(int size) {",
        "return new RootParcel[size];",
        "}",
        "};",
        "private final Root data;",
        "private RootParcel(Root data) {",
        "this.data = data;",
        "}",
        "private RootParcel(Parcel in) {",
        "Child outChild = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel childParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outChild = childParcel.getContents();",
        "}",
        "this.data = new Root(outChild);",
        "}",
        "public static final RootParcel wrap(Root data) {",
        "return new RootParcel(data);",
        "}",
        "public Root getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel childParcel = ChildParcel.wrap(child);",
        "childParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}"
    ));

    JavaFileObject childParcel = JavaFileObjects.forSourceString("test/ChildParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class ChildParcel implements TypedParcelable<Child> {",
        "public static final Parcelable.Creator<ChildParcel> CREATOR = new Parcelable.Creator<ChildParcel>() {",
        "@Override public ChildParcel createFromParcel(Parcel in) {",
        "return new ChildParcel(in);",
        "}",
        "@Override public ChildParcel[] newArray(int size) {",
        "return new ChildParcel[size];",
        "}",
        "};",
        "private final Child data;",
        "private ChildParcel(Child data) {",
        "this.data = data;",
        "}",
        "private ChildParcel(Parcel in) {",
        "Integer outChild = null;",
        "if (in.readInt() == 0) {",
        "outChild = in.readInt();",
        "}",
        "this.data = new Child(outChild);",
        "}",
        "public static final ChildParcel wrap(Child data) {",
        "return new ChildParcel(data);",
        "}",
        "public Child getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Integer child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(child);",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void emptyDataTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
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
        "this.data = new Test();",
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
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void twoEmptyDataObjectsTest() throws Exception {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Test1", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test1 {",
        "}"
    ));

    JavaFileObject source2 = JavaFileObjects.forSourceString("test.Test2", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test2 {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test1Parcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class Test1Parcel implements TypedParcelable<Test1> {",
        "public static final Parcelable.Creator<Test1Parcel> CREATOR = new Parcelable.Creator<Test1Parcel>() {",
        "@Override public Test1Parcel createFromParcel(Parcel in) {",
        "return new Test1Parcel(in);",
        "}",
        "@Override public Test1Parcel[] newArray(int size) {",
        "return new Test1Parcel[size];",
        "}",
        "};",
        "private final Test1 data;",
        "private Test1Parcel(Test1 data) {",
        "this.data = data;",
        "}",
        "private Test1Parcel(Parcel in) {",
        "this.data = new Test1();",
        "}",
        "public static final Test1Parcel wrap(Test1 data) {",
        "return new Test1Parcel(data);",
        "}",
        "public Test1 getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "}",
        "}"
    ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/Test2Parcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class Test2Parcel implements TypedParcelable<Test2> {",
        "public static final Parcelable.Creator<Test2Parcel> CREATOR = new Parcelable.Creator<Test2Parcel>() {",
        "@Override public Test2Parcel createFromParcel(Parcel in) {",
        "return new Test2Parcel(in);",
        "}",
        "@Override public Test2Parcel[] newArray(int size) {",
        "return new Test2Parcel[size];",
        "}",
        "};",
        "private final Test2 data;",
        "private Test2Parcel(Test2 data) {",
        "this.data = data;",
        "}",
        "private Test2Parcel(Parcel in) {",
        "this.data = new Test2();",
        "}",
        "public static final Test2Parcel wrap(Test2 data) {",
        "return new Test2Parcel(data);",
        "}",
        "public Test2 getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source1, source2))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void genericNonParcelablesTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final Child<? extends Child<Integer, Long>, Boolean> child;",
        "public Test(Child<? extends Child<Integer, Long>, Boolean> child) {",
        "this.child = child;",
        "}",
        "public Child<? extends Child<Integer, Long>, Boolean> getChild() {",
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
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
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
        "Child<? extends Child<Integer, Long>, Boolean> outChild = null;",
        "if (in.readInt() == 0) {",
        "Child1633727114Parcel childParcel = Child1633727114Parcel.CREATOR.createFromParcel(in);",
        "outChild = childParcel.getContents();",
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
        "Child<? extends Child<Integer, Long>, Boolean> child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "Child1633727114Parcel childParcel = Child1633727114Parcel.wrap(child);",
        "childParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}"
    ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/Child1633727114Parcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Boolean;",
        "import java.lang.Integer;",
        "import java.lang.Long;",
        "import java.lang.Override;",
        "public final class Child1633727114Parcel implements Parcelable {",
        "public static final Parcelable.Creator<Child1633727114Parcel> CREATOR = new Parcelable.Creator<Child1633727114Parcel>() {",
        "@Override public Child1633727114Parcel createFromParcel(Parcel in) {",
        "return new Child1633727114Parcel(in);",
        "}",
        "@Override public Child1633727114Parcel[] newArray(int size) {",
        "return new Child1633727114Parcel[size];",
        "}",
        "};",
        "private final Child<? extends Child<Integer, Long>, Boolean> data;",
        "private Child1633727114Parcel(Child<? extends Child<Integer, Long>, Boolean> data) {",
        "this.data = data;",
        "}",
        "private Child1633727114Parcel(Parcel in) {",
        "Child<Integer, Long> outChild1 = null;",
        "if (in.readInt() == 0) {",
        "Child_669715220Parcel child1Parcel = Child_669715220Parcel.CREATOR.createFromParcel(in);",
        "outChild1 = child1Parcel.getContents();",
        "}",
        "Boolean outChild2 = null;",
        "if (in.readInt() == 0) {",
        "outChild2 = in.readInt() == 1;",
        "}",
        "this.data = new Child<>(outChild1, outChild2);",
        "}",
        "public static final Child1633727114Parcel wrap(Child<? extends Child<Integer, Long>, Boolean> data) {",
        "return new Child1633727114Parcel(data);",
        "}",
        "public Child<? extends Child<Integer, Long>, Boolean> getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child<Integer, Long> child1 = data.getChild1();",
        "if (child1 == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "Child_669715220Parcel child1Parcel = Child_669715220Parcel.wrap(child1);",
        "child1Parcel.writeToParcel(dest, 0);",
        "}",
        "Boolean child2 = data.getChild2();",
        "if (child2 == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(child2 ? 1 : 0);",
        "}",
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
        "public final class Child_669715220Parcel implements Parcelable {",
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
        "Integer outChild1 = null;",
        "if (in.readInt() == 0) {",
        "outChild1 = in.readInt();",
        "}",
        "Long outChild2 = null;",
        "if (in.readInt() == 0) {",
        "outChild2 = in.readLong();",
        "}",
        "this.data = new Child<>(outChild1, outChild2);",
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
        "if (child1 == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(child1);",
        "}",
        "Long child2 = data.getChild2();",
        "if (child2 == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeLong(child2);",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2, expectedSource3);
  }
}
