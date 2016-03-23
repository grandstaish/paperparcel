package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class SparseArrayTests {

  @Test public void sparseArrayOfParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import android.util.SparseArray;",
        "@PaperParcel",
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
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "SparseArray<Integer> outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "SparseArray<Integer> child = new SparseArray<Integer>(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "int childKey = in.readInt();",
        "Integer outChildValue = null;",
        "if (in.readInt() == 0) {",
        "outChildValue = in.readInt();",
        "}",
        "child.put(childKey, outChildValue);",
        "}",
        "outChild = child;",
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
        "SparseArray<Integer> child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.size();",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "int childKey = child.keyAt(childIndex);",
        "dest.writeInt(childKey);",
        "Integer childValue = child.get(childKey);",
        "if (childValue == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childValue);",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void sparseArrayOfDataTypesTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import android.util.SparseArray;",
        "@PaperParcel",
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
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class RootParcel implements TypedParcelable<Root> {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "SparseArray<Child> outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "SparseArray<Child> child = new SparseArray<Child>(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "int childKey = in.readInt();",
        "Child outChildValue = null;",
        "if (in.readInt() == 0) {",
        "Integer outChildValueChild = null;",
        "if (in.readInt() == 0) {",
        "outChildValueChild = in.readInt();",
        "}",
        "outChildValue = new Child(outChildValueChild);",
        "}",
        "child.put(childKey, outChildValue);",
        "}",
        "outChild = child;",
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
        "SparseArray<Child> child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
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
        "if (childValue.getChild() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childValue.getChild());",
        "}",
        "}",
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
}
