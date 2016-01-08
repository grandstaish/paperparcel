package nz.bradcampbell.dataparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class StringTests {

  @Test public void nullableTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.Nullable;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "private final Integer testInt;",
        "@Nullable private final String testString;",
        "public Test(Integer testInt, @Nullable String testString) {",
        "this.testInt = testInt;",
        "this.testString = testString;",
        "}",
        "public Integer component1() {",
        "return this.testInt;",
        "}",
        "@Nullable public String component2() {",
        "return this.testString;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import java.lang.String;",
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
        "Integer component1 = null;",
        "component1 = in.readInt();",
        "String component2 = null;",
        "if (in.readInt() == 0) {",
        "component2 = in.readString();",
        "}",
        "this.data = new Test(component1, component2);",
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
        "Integer component1 = data.component1();",
        "dest.writeInt(component1);",
        "if (data.component2() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "String component2 = data.component2();",
        "dest.writeString(component2);",
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
}
