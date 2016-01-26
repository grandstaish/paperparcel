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
        "private final SparseArray<Integer> testList;",
        "public Test(SparseArray<Integer> testList) {",
        "this.testList = testList;",
        "}",
        "public SparseArray<Integer> component1() {",
        "return this.testList;",
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
        "int component1Size = in.readInt();",
        "SparseArray<Integer> component1 = new SparseArray<Integer>(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "int component1Key = in.readInt();",
        "Integer component1Value = (Integer) in.readValue(null);",
        "component1.put(component1Key, component1Value);",
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
        "SparseArray<Integer> component1 = data.component1();",
        "int component1Size = component1.size();",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "int component1Key = component1.keyAt(component1Index);",
        "dest.writeInt(component1Key);",
        "Integer component1Value = component1.get(component1Key);",
        "dest.writeValue(component1Value);",
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
        "public SparseArray<Child> component1() {",
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
        "int component1Size = in.readInt();",
        "SparseArray<Child> component1 = new SparseArray<Child>(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "int component1Key = in.readInt();",
        "Child outComponent1Value = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel component1ValueParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outComponent1Value = component1ValueParcel.getContents();",
        "}",
        "component1.put(component1Key, outComponent1Value);",
        "}",
        "this.data = new Root(component1);",
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
        "SparseArray<Child> component1 = data.component1();",
        "int component1Size = component1.size();",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "int component1Key = component1.keyAt(component1Index);",
        "dest.writeInt(component1Key);",
        "Child component1Value = component1.get(component1Key);",
        "if (component1Value == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel component1ParamParcel = ChildParcel.wrap(component1Value);",
        "component1ParamParcel.writeToParcel(dest, 0);",
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
        "Integer component1 = (Integer) in.readValue(null);",
        "this.data = new Child(component1);",
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
        "Integer component1 = data.component1();",
        "dest.writeValue(component1);",
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
