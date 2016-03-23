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
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class RootParcel implements TypedParcelable<Root> {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "Child outChild = null;",
        "if (in.readInt() == 0) {",
        "Integer outChildChild = null;",
        "if (in.readInt() == 0) {",
        "outChildChild = in.readInt();",
        "}",
        "outChild = new Child(outChildChild);",
        "}",
        "Root data = new Root(outChild);",
        "return new RootParcel(data);",
        "}",
        "@Override public RootParcel[] newArray(int size) {",
        "return new RootParcel[size];",
        "}",
        "};",
        "public final Root data;",
        "public RootParcel(Root data) {",
        "this.data = data;",
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
        "if (child.getChild() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(child.getChild());",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel);
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
        "Test data = new Test();",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
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
        "Test1 data = new Test1();",
        "return new Test1Parcel(data);",
        "}",
        "@Override public Test1Parcel[] newArray(int size) {",
        "return new Test1Parcel[size];",
        "}",
        "};",
        "public final Test1 data;",
        "public Test1Parcel(Test1 data) {",
        "this.data = data;",
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
        "Test2 data = new Test2();",
        "return new Test2Parcel(data);",
        "}",
        "@Override public Test2Parcel[] newArray(int size) {",
        "return new Test2Parcel[size];",
        "}",
        "};",
        "public final Test2 data;",
        "public Test2Parcel(Test2 data) {",
        "this.data = data;",
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
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
        "Child<? extends Child<Integer, Long>, Boolean> outChild = null;",
        "if (in.readInt() == 0) {",
        "Child<Integer, Long> outChildChild1 = null;",
        "if (in.readInt() == 0) {",
        "Integer outChildChild1Child1 = null;",
        "if (in.readInt() == 0) {",
        "outChildChild1Child1 = in.readInt();",
        "}",
        "Long outChildChild1Child2 = null;",
        "if (in.readInt() == 0) {",
        "outChildChild1Child2 = in.readLong();",
        "}",
        "outChildChild1 = new Child<Integer, Long>(outChildChild1Child1, outChildChild1Child2);",
        "}",
        "Boolean outChildChild2 = null;",
        "if (in.readInt() == 0) {",
        "outChildChild2 = in.readInt() == 1;",
        "}",
        "outChild = new Child<Child<Integer, Long>, Boolean>(outChildChild1, outChildChild2);",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
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
        "if (child.getChild1() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "if (child.getChild1().getChild1() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(child.getChild1().getChild1());",
        "}",
        "if (child.getChild1().getChild2() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeLong(child.getChild1().getChild2());",
        "}",
        "}",
        "if (child.getChild2() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(child.getChild2() ? 1 : 0);",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
