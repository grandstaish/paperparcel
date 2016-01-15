package nz.bradcampbell.dataparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ObjectArrayTests {

  @Test public void nullableBooleanObjectArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.Nullable;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
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
        "import java.lang.ClassLoader;",
        "import java.lang.Object;",
        "import java.lang.Override;",
        "public class TestParcel implements Parcelable {",
        "private static final ClassLoader CLASS_LOADER = Test.class.getClassLoader();",
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
        "Boolean[] component1 = null;",
        "if (in.readInt() == 0) {",
        "Object[] component1Wrapped = in.readArray(CLASS_LOADER);",
        "component1 = new Boolean[component1Wrapped.length];",
        "for (int component1Index = 0; component1Index < component1Wrapped.length; component1Index++) {",
        "Boolean _component1 = null;",
        "Boolean _component1Wrapped = (Boolean) component1Wrapped[component1Index];",
        "_component1 = _component1Wrapped;",
        "component1[component1Index] = _component1;",
        "}",
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
        "if (data.component1() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "Boolean[] component1 = data.component1();",
        "dest.writeArray(component1);",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void genericNonParcelableArrayTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import android.util.SparseArray;",
        "@DataParcel",
        "public final class Test {",
        "private final SparseArray<Child>[] child;",
        "public Test(SparseArray<Child>[] child) {",
        "this.child = child;",
        "}",
        "public SparseArray<Child>[] component1() {",
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
        "import android.util.SparseArray;",
        "import java.lang.ClassLoader;",
        "import java.lang.Object;",
        "import java.lang.Override;",
        "public class TestParcel implements Parcelable {",
        "private static final ClassLoader CLASS_LOADER = Test.class.getClassLoader();",
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
        "SparseArray<Child>[] component1 = null;",
        "Object[] component1Wrapped = in.readArray(CLASS_LOADER);",
        "component1 = new SparseArray[component1Wrapped.length];",
        "for (int component1Index = 0; component1Index < component1Wrapped.length; component1Index++) {",
        "SparseArray<Child> _component1 = null;",
        "SparseArray<ChildParcel> _component1Wrapped = (SparseArray<ChildParcel>) component1Wrapped[component1Index];",
        "_component1 = new SparseArray<Child>();",
        "for (int _component1Index = 0; _component1Index < _component1Wrapped.size(); _component1Index++) {",
        "Child __component1 = null;",
        "int _component1WrappedKey = _component1Wrapped.keyAt(_component1Index);",
        "ChildParcel __component1Wrapped = _component1Wrapped.get(_component1WrappedKey);",
        "__component1 = __component1Wrapped.getContents();",
        "_component1.put(_component1WrappedKey, __component1);",
        "}",
        "component1[component1Index] = _component1;",
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
        "SparseArray<Child>[] component1 = data.component1();",
        "SparseArray<ChildParcel>[] component1Wrapped = new SparseArray[component1.length];",
        "for (int component1Index = 0; component1Index < component1.length; component1Index++) {",
        "SparseArray<Child> _component1 = component1[component1Index];",
        "SparseArray<ChildParcel> _component1Wrapped = new SparseArray<ChildParcel>();",
        "for (int _component1Index = 0; _component1Index < _component1.size(); _component1Index++) {",
        "int _component1Key = _component1.keyAt(_component1Index);",
        "ChildParcel __component1 = ChildParcel.wrap(_component1.get(_component1Key));",
        "_component1Wrapped.put(_component1Key, __component1);",
        "}",
        "component1Wrapped[component1Index] = _component1Wrapped;",
        "}",
        "dest.writeArray(component1Wrapped);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
