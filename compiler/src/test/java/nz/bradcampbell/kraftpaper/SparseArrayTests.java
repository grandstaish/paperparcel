package nz.bradcampbell.kraftpaper;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class SparseArrayTests {

  @Test public void sparseArrayOfParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "import android.util.SparseArray;",
        "@KraftPaper",
        "public final class Test {",
        "private final SparseArray<Integer> child;",
        "public Test(SparseArray<Integer> child) {",
        "this.child = child;",
        "}",
        "public SparseArray<Integer> getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import android.util.SparseArray;",
        "import java.lang.Integer;",
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
        "SparseArray<Integer> child = new SparseArray<Integer>(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "int childKey = in.readInt();",
        "Integer childValue = (Integer) in.readValue(null);",
        "child.put(childKey, childValue);",
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
        "SparseArray<Integer> child = data.getChild();",
        "int childSize = child.size();",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "int childKey = child.keyAt(childIndex);",
        "dest.writeInt(childKey);",
        "Integer childValue = child.get(childKey);",
        "dest.writeValue(childValue);",
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

  @Test public void sparseArrayOfDataTypesTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "import android.util.SparseArray;",
        "@KraftPaper",
        "public final class Root {",
        "private final SparseArray<Child> child;",
        "public Root(SparseArray<Child> child) {",
        "this.child = child;",
        "}",
        "public SparseArray<Child> getChild() {",
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
        "import android.util.SparseArray;",
        "import java.lang.Override;",
        "public class RootParcel implements Parcelable {",
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
        "int childSize = in.readInt();",
        "SparseArray<Child> child = new SparseArray<Child>(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "int childKey = in.readInt();",
        "Child outChildValue = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel childValueParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outChildValue = childValueParcel.getContents();",
        "}",
        "child.put(childKey, outChildValue);",
        "}",
        "this.data = new Root(child);",
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
        "SparseArray<Child> child = data.getChild();",
        "int childSize = child.size();",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "int childKey = child.keyAt(childIndex);",
        "dest.writeInt(childKey);",
        "Child childValue = child.get(childKey);",
        "if (childValue == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel childParamParcel = ChildParcel.wrap(childValue);",
        "childParamParcel.writeToParcel(dest, 0);",
        "}",
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
        "public class ChildParcel implements Parcelable {",
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
        "Integer child = (Integer) in.readValue(null);",
        "this.data = new Child(child);",
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
        "dest.writeValue(child);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }
}