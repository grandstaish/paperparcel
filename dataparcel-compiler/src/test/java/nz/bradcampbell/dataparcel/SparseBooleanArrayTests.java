package nz.bradcampbell.dataparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class SparseBooleanArrayTests {

  @Test public void nullableSparseBooleanArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.util.SparseBooleanArray;",
        "import android.support.annotation.Nullable;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "@Nullable private final SparseBooleanArray child;",
        "public Test(@Nullable SparseBooleanArray child) {",
        "this.child = child;",
        "}",
        "@Nullable public SparseBooleanArray component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import android.util.SparseBooleanArray;",
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
        "SparseBooleanArray component1 = null;",
        "if (in.readInt() == 0) {",
        "component1 = in.readSparseBooleanArray();",
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
        "SparseBooleanArray component1 = data.component1();",
        "dest.writeSparseBooleanArray(component1);",
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

  @Test public void sparseBooleanArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.util.SparseBooleanArray;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "private final SparseBooleanArray child;",
        "public Test(SparseBooleanArray child) {",
        "this.child = child;",
        "}",
        "public SparseBooleanArray component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import android.util.SparseBooleanArray;",
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
        "SparseBooleanArray component1 = null;",
        "component1 = in.readSparseBooleanArray();",
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
        "SparseBooleanArray component1 = data.component1();",
        "dest.writeSparseBooleanArray(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
